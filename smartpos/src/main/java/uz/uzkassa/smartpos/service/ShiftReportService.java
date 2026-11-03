package uz.uzkassa.smartpos.service;

import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.smartpos.domain.postgres.company.Branch;
import uz.uzkassa.smartpos.domain.postgres.company.Branch_;
import uz.uzkassa.smartpos.domain.postgres.company.Company;
import uz.uzkassa.smartpos.domain.postgres.receipt.Shift;
import uz.uzkassa.smartpos.domain.postgres.receipt.ShiftReport;
import uz.uzkassa.smartpos.domain.postgres.receipt.ShiftReportMapper;
import uz.uzkassa.smartpos.domain.postgres.receipt.ShiftReport_;
import uz.uzkassa.smartpos.domain.postgres.terminal.Device_;
import uz.uzkassa.smartpos.domain.postgres.user.UserSession;
import uz.uzkassa.smartpos.dto.filter.BaseFilter;
import uz.uzkassa.smartpos.dto.filter.ShiftFilter;
import uz.uzkassa.smartpos.dto.integration.agreement.ServiceType;
import uz.uzkassa.smartpos.dto.shift.*;
import uz.uzkassa.smartpos.dto.telegram.TelegramAccountDTO;
import uz.uzkassa.smartpos.enums.DeviceType;
import uz.uzkassa.smartpos.enums.ShiftStatus;
import uz.uzkassa.smartpos.exceptions.BadRequestException;
import uz.uzkassa.smartpos.filter.ShiftReportFilter;
import uz.uzkassa.smartpos.filter.specification.ShiftReportSpecificationFilter;
import uz.uzkassa.smartpos.repository.clickhouse.receipt.ClickhouseReceiptGenericRepository;
import uz.uzkassa.smartpos.repository.clickhouse.receipt.ClickhouseReceiptReportByBranch;
import uz.uzkassa.smartpos.repository.postgres.receipt.ReceiptRepository;
import uz.uzkassa.smartpos.repository.postgres.receipt.ShiftReportRepository;
import uz.uzkassa.smartpos.repository.postgres.receipt.ShiftRepository;
import uz.uzkassa.smartpos.repository.postgres.specifications.ShiftReportSpecification;
import uz.uzkassa.smartpos.repository.postgres.terminal.DeviceRepository;
import uz.uzkassa.smartpos.repository.postgres.user.UserRepository;
import uz.uzkassa.smartpos.repository.postgres.user.UserSessionRepository;
import uz.uzkassa.smartpos.security.SecurityUtils;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class ShiftReportService extends SmartposService {
    private final ShiftRepository shiftRepository;
    private final UserSessionRepository userSessionRepository;
    private final ShiftReportRepository shiftReportRepository;
    private final TelegramService telegramService;
    private final ReceiptRepository receiptRepository;
    private final ClickhouseReceiptGenericRepository clickhouseReceiptGenericRepository;
    private final UserRepository userRepository;
    private final ClickhouseReceiptReportByBranch clickhouseReceiptReportByBranch;
    private final ShiftReportMapper shiftReportMapper;
    private final DeviceRepository deviceRepository;

    /*
    Смена создается если текущий кассир не имеет открытых смен.
    При обращении в этот метод всегда открываеся сессия
 */
    @Transactional
    public ShiftDTO createOrGet(Long fiscalNumber) {
        String terminalSn = getCurrentTerminalSnByHeader();
        Shift shift;
        if (StringUtils.isNotEmpty(terminalSn)) {
            var terminal = deviceRepository.findFirstBySerialNumberAndType(terminalSn, DeviceType.FISCAL)
                    .orElseThrow(badRequest(localizationService.getMessage("terminal.not.found", terminalSn)));

            shift = shiftRepository.findFirstByTerminalIdAndStatusOrderByStartDateTimeDesc(terminal.getId(), ShiftStatus.OPEN)
                    .orElseGet(() -> createNewShift(terminal.getId(), null, fiscalNumber));
        } else {
            final Long branchId = getCurrentBranchId();
            shift = shiftRepository.findFirstByBranchIdAndStatusOrderByStartDateTimeDesc(branchId, ShiftStatus.OPEN)
                    .orElseGet(() -> createNewShift(null, branchId, fiscalNumber));
        }
        createNewSession(shift);
        return shift.getDTO();
    }

    @Transactional
    public Shift createNewShift(Long terminalId, Long branchId, Long fiscalNumber) {
        branchId = branchId == null ? getCurrentBranchId() : branchId;
        Shift newShift = new Shift();
        newShift.setUserId(getCurrentUserId());
        newShift.setTerminalId(terminalId);
        newShift.setBranchId(branchId);
        newShift.setStartDateTime(LocalDateTime.now());
        Integer maxShiftNumber = clickhouseReceiptGenericRepository.maxShiftNumber(branchId);
        newShift.setNumber(maxShiftNumber != null ? maxShiftNumber + 1 : 1L);
        newShift.setFiscalNumber(fiscalNumber != null ? fiscalNumber : newShift.getNumber());
        newShift.setStatus(ShiftStatus.OPEN);
        shiftRepository.save(newShift);
        return newShift;
    }

    @Transactional
    public void createNewSession(Shift shift) {
        closeSession();
        UserSession newSession = new UserSession();

        newSession.setStartDateTime(LocalDateTime.now());
        newSession.setShiftId(shift.getId());
        newSession.setUserId(getCurrentUserId());
        newSession.setTerminalId(shift.getTerminalId());
        newSession.setStatus(ShiftStatus.OPEN);
        userSessionRepository.save(newSession);
    }

    /*
        Закрытие текущей смены. Текущяя смена последняя не закрытая смена на сегодня
     */
    @Transactional
    public ShiftReportDTO closeShift(Long fiscalNumber, boolean forWeb) {
        closeSession();
        String terminalSn = getCurrentTerminalSnByHeader();

        Optional<Shift> currentShiftOptional;
        if (StringUtils.isNotEmpty(terminalSn)) {
            var terminal = deviceRepository.findFirstBySerialNumberAndType(getCurrentTerminalSnByHeader(), DeviceType.FISCAL)
                    .orElseThrow(badRequest(localizationService.getMessage("terminal.not.found", getCurrentTerminalSnByHeader())));
            currentShiftOptional = shiftRepository.findFirstByTerminalIdAndStatusOrderByStartDateTimeDesc(terminal.getId(), ShiftStatus.OPEN);
        } else {
            currentShiftOptional = shiftRepository.findFirstByBranchIdAndStatusOrderByStartDateTimeDesc(getCurrentBranchId(), ShiftStatus.OPEN);
        }

        if (currentShiftOptional.isPresent()) {
            Shift latestShift = currentShiftOptional.get();
            latestShift.setFiscalNumber(fiscalNumber);
            latestShift.setStatus(ShiftStatus.CLOSED);
            latestShift.setEndDateTime(LocalDateTime.now());
            shiftRepository.save(latestShift);
            if (forWeb) {
                return crateShiftReportForWeb(latestShift);
            } else {
                return null;
            }
        } else {
            throw notFound(localizationService.getMessage("shift")).get();
        }
    }

    @Transactional
    public ShiftReportDTO crateShiftReportForWeb(Shift shift) {
        ShiftReportDTO dto = clickhouseReceiptGenericRepository.xReport(shift.getBranchId(), shift.getNumber());
        ShiftReport report = new ShiftReport();
        report.setBranch(shift.getBranch());
        report.setBranchId(shift.getBranchId());
        report.setUserId(shift.getUserId());
        report.setUser(shift.getUser());
        report.setStartDateTime(shift.getStartDateTime());
        report.setEndDateTime(shift.getEndDateTime());
        report.setNumber(shift.getNumber());
        report.setReceipts(dto.getReceipts());
        report.setReceiptsReturned(dto.getReceiptsReturned());
        report.setCard(dto.getTotalCard());
        report.setCardReturned(dto.getTotalCardReturned());
        report.setCash(dto.getTotalCash());
        report.setCashReturned(dto.getTotalCashReturned());
        report.setNDS(dto.getNDS());
        report.setNdsReturned(dto.getNdsReturned());
        shiftReportRepository.save(report);

        sendTelegramMessage(report);
        ShiftReportDTO result = report.getDTO();
        result.setStatistics(getStatsForZReport(new ShiftFilter(shift.getBranchId(), shift.getNumber())));
        return result;
    }

    /*
        Закрытие текущей сессии
     */
    @Transactional
    public void closeSession() {
        Optional<UserSession> sessionOptional =
                userSessionRepository.findFirstByUserIdAndStatusOrderByStartDateTimeDesc(getCurrentUserId(), ShiftStatus.OPEN);
        if (sessionOptional.isPresent()) {
            UserSession session = sessionOptional.get();
            session.setStatus(ShiftStatus.CLOSED);
            session.setEndDateTime(LocalDateTime.now());
            userSessionRepository.save(session);
        }
    }

    @Transactional
    public void save(ShiftReportDTO dto) {
        var terminal = deviceRepository.findFirstBySerialNumberAndType(getCurrentTerminalSnByHeader(), DeviceType.FISCAL)
                .orElseThrow(notFound(localizationService.getMessage("terminal")));
        ShiftReport report = shiftReportRepository.findFirstByTerminalIdAndNumberAndEndDateTime(terminal.getId(),
                dto.getFiscalNumber(), dto.getEndDateTime()).orElseGet(ShiftReport::new);
        if (report.getId() != null) {
            return;
        }
        report.setNumber(dto.getFiscalNumber());
        report.setStartDateTime(dto.getStartDateTime());
        report.setEndDateTime(dto.getEndDateTime());
        report.setReceipts(dto.getReceipts());
        report.setReceiptsReturned(dto.getReceiptsReturned());
        report.setCard(dto.getTotalCard());
        report.setCardReturned(dto.getTotalCardReturned());
        report.setCash(dto.getTotalCash());
        report.setCashReturned(dto.getTotalCashReturned());
        report.setNDS(dto.getNDS());
        report.setNdsReturned(dto.getNdsReturned());
        report.setUserId(dto.getUserId());
        report.setTerminal(terminal);
        report.setTerminalId(terminal.getId());
        report.setBranch(terminal.getBranch());
        report.setBranchId(terminal.getBranchId());
        shiftReportRepository.save(report);

        sendTelegramMessage(report);
    }

    public void sendTelegramMessage(ShiftReport report) {
        List<TelegramAccountDTO> telegramAccounts = telegramService.getTelegramAccountsByCompanyId(getCurrentCompanyId());
        for (TelegramAccountDTO accountDTO : telegramAccounts) {
            if (accountDTO != null && accountDTO.getTelegramId() != null) {
                boolean isUzbek = "uz".equals(accountDTO.getLangKey());
                if (report.getUserId() != null) {
                    report.setUser(userRepository.getReferenceById(report.getUserId()));
                }
                try {
                    telegramService.sendXZReport(accountDTO.getTelegramId(), report.getDTO(), isUzbek);
                } catch (Exception e) {
                    log.error("Send telegram message failed", e);
                }
            }
        }
    }

    public Page<ShiftReportDTO> getReport(BaseFilter filter) {
        if (filter.getBranchId() == null) {
            filter.setBranchId(getCurrentBranchId());
        }
        if (!isAdmin() && !isOperator()) {
            Company company = branchRepository.findById(filter.getBranchId()).orElseThrow(notFound(localizationService.getMessage("branch"))).getCompany();
            if (!company.isFreeTrial() && !company.getPermissions().getOrDefault(ServiceType.FISCAL_REPORT, LocalDateTime.MIN).isAfter(LocalDateTime.now())) {
                Optional<ShiftReport> lastShiftOptional = shiftReportRepository
                        .findFirstByBranchIdOrderByStartDateTimeDesc(filter.getBranchId());
                if (lastShiftOptional.isPresent()) {
                    return new PageImpl<>(Collections.singletonList(lastShiftOptional.get().getDTO()));
                } else {
                    throw new BadRequestException("Закрытые смены отсутствуют!");
                }
            }
        }
        ShiftReportSpecificationFilter specificationFilter = ShiftReportSpecificationFilter.builder()
                .branchId(filter.getBranchId())
                .from(filter.getFrom())
                .to(filter.getTo())
                .terminalId(filter.getTerminalId())
                .search(filter.getSearch())
                .orderBy(filter.getOrderBy())
                .sortOrder(filter.getSortOrder())
                .build();
        return shiftReportRepository
                .findAll((Specification<ShiftReport>) (root, criteriaQuery, criteriaBuilder) ->
                        ShiftReportSpecification.getPredicateWithSort(root, criteriaQuery, criteriaBuilder, specificationFilter), filter.getPageable())
                .map(ShiftReport::getDTO);
    }

    public ShiftReportDTO getReportById(Long branchId, Long shiftNumber) {
        Branch branch = branchRepository.findById(branchId)
                .orElseThrow(notFound(localizationService.getMessage("branch"), branchId));
        checkForAccessToCompany(branch.getCompanyId());
        ShiftReportDTO dto = shiftReportRepository.findFirstByBranchIdAndNumberOrderByEndDateTimeDesc(branchId, shiftNumber).orElseThrow(badRequest("Такой смены нет")).getDTO();
        dto.setStatistics(getStatsForZReport(new ShiftFilter(branchId, shiftNumber)));
        return dto;
    }

    public ShiftReportStatDTO getReportStat(BaseFilter filter) {
        filter.setCompanyId(getCurrentCompanyId());
        if (filter.getBranchId() == null && !isOwner()) {
            filter.setBranchId(getCurrentBranchId());
        }
        ShiftReportSpecificationFilter specificationFilter = ShiftReportSpecificationFilter.builder()
                .companyId(filter.getCompanyId())
                .branchId(filter.getBranchId())
                .from(filter.getFrom())
                .to(filter.getTo())
                .terminalId(filter.getTerminalId())
                .userId(filter.getUserId())
                .search(filter.getSearch())
                .build();
        ShiftReportStatDTO stat = shiftReportRepository.getReportStat(specificationFilter);
        if (filter.getBranchId() == null) {
            stat.setDrafts(receiptRepository.findAllDraftsCountByCompany(filter.getCompanyId()));
        } else {
            stat.setDrafts(receiptRepository.findAllDraftsCount(filter.getBranchId()));
        }
        return stat;
    }

    public ShiftReportDTO xReport(ShiftFilter filter) {
        if (filter.getBranchId() == null) filter.setBranchId(getCurrentBranchId());
        Shift shift = shiftRepository.findFirstByBranchIdAndStatusOrderByStartDateTimeDesc(filter.getBranchId(), ShiftStatus.OPEN).orElseThrow(notFound("Shift"));
        filter.setShiftNumber(shift.getNumber());
        ShiftReportDTO report = clickhouseReceiptGenericRepository.xReport(filter.getBranchId(), shift.getNumber());
        report.setNumber(shift.getNumber());
        report.setFiscalNumber(shift.getFiscalNumber());
        report.setStartDateTime(shift.getStartDateTime());
        if (shift.getBranch() != null) {
            report.setBranchName(shift.getBranch().getName());
            report.setBranchAddress(shift.getBranch().getAddress());
            report.setTerminalSN(shift.getBranch().getSerialNumber());
            if (shift.getBranch().getCompany() != null) {
                final Company company = shift.getBranch().getCompany();
                report.setCompanyName(company.getName());
                report.setCompanyINN(company.getInn());
                report.setCompanyDateTime(company.getCreatedDate());
                report.setBusinessType(company.getBusinessType());
                report.setFmNumber(company.getFiscalNumber());
            }
        }
        if (shift.getUser() != null) {
            report.setUserName(shift.getUser().getName());
        }
        report.setStatistics(getStatsForZReport(filter));
        return report;
    }

    public ShiftReportDTO totalReport(Long branchId) {
        return clickhouseReceiptGenericRepository.totalReport(branchId != null ? branchId : getCurrentBranchId());
    }

    public ShiftReportStatDTO getStatsForZReport(ShiftFilter filter) {
        return clickhouseReceiptReportByBranch.getStatsForZReport(filter);
    }

    public ShiftReportStatDTO getStats(ShiftFilter filter) {
        ShiftReportStatDTO stats = clickhouseReceiptReportByBranch.getStats(filter);
        if (filter.getBranchId() == null) filter.setBranchId(getCurrentBranchId());
        stats.setDrafts(receiptRepository.findAllDraftsCount(filter.getBranchId()));
        return stats;
    }


    public ShiftReportStatDTO getStatsForCabinet(ShiftReportFilter filter) {
        if (!isOwner() && !isBranchAdmin()) {
            filter.setBranchIds(Set.of(getCurrentBranchId()));
        }
        if (isBranchAdmin() && CollectionUtils.isEmpty(filter.getBranchIds())) {
            SecurityUtils.getCurrentBranchGroupId()
                    .ifPresentOrElse(groupId -> filter.setBranchGroupIds(Set.of(groupId)),
                            () -> filter.setBranchIds(Set.of(SecurityUtils.getCurrentBranchId()))
                    );
        }
        filter.setBranchIds(getBranchesByGroup(filter.getBranchGroupIds(), filter.getBranchIds()));
        ShiftReportStatDTO stats = shiftReportRepository.getReportStat(filter);
        if (CollectionUtils.isEmpty(filter.getBranchIds())) {
            stats.setDrafts(receiptRepository.findAllDraftsCountByCompany(getCurrentCompanyId()));
        } else {
            stats.setDrafts(receiptRepository.findAllDraftsCount(filter.getBranchIds()));
        }
        return stats;
    }


    public ShiftTotalStatisticsDTO getTotalStatistics(ShiftFilter filter) {
        final Long branchId = getCurrentBranchId();
        if (filter.getBranchId() == null) filter.setBranchId(branchId);
        Branch branch = branchRepository.getReferenceById(branchId);
        ShiftReportStatDTO stats = clickhouseReceiptReportByBranch.getStats(filter);
        return ShiftTotalStatisticsDTO.builder()
                .companyName(branch.getCompany().getName())
                .companyAddress(branch.getCompany().getAddress())
                .registrationDate(branch.getCompany().getCreatedDate())
                .inn(branch.getCompany().getInn())
                .branchName(branch.getName())
                .branchAddress(branch.getAddress())
                .fmNumber(branch.getCompany().getFiscalNumber())
                .serialNumber(branch.getSerialNumber())
                .receipts(stats.getReceipts())
                .receiptsReturned(stats.getReceiptsReturned())
                .totalCash(stats.getTotalCash())
                .totalCard(stats.getTotalCard())
                .nds(stats.getNds())
                .discount(stats.getDiscount())
                .shifts(stats.getShifts())
                .build();
    }

    public Page<ShiftReportListDto> getReportForCabinet(ShiftReportFilter filter) {
        if (!isOwner() && !isBranchAdmin()) {
            filter.setBranchIds(Set.of(getCurrentBranchId()));
        }
        if (isBranchAdmin() && CollectionUtils.isEmpty(filter.getBranchIds())) {
            SecurityUtils.getCurrentBranchGroupId()
                    .ifPresentOrElse(groupId -> filter.setBranchGroupIds(Set.of(groupId)),
                            () -> filter.setBranchIds(Set.of(SecurityUtils.getCurrentBranchId()))
                    );
        }
        Set<Long> branchIds = getBranchesByGroup(filter.getBranchGroupIds(), filter.getBranchIds());
        Company company = companyRepository.findById(getCurrentCompanyId()).orElseThrow(notFound(localizationService.getMessage("company")));
        if (!company.isFreeTrial() && !company.getPermissions().getOrDefault(ServiceType.FISCAL_REPORT, LocalDateTime.MIN).isAfter(LocalDateTime.now())) {
            try {
                ShiftReport lastShift = shiftReportRepository.getLastShift(branchIds, getCurrentCompanyId());
                return new PageImpl<>(List.of(shiftReportMapper.toDto(lastShift)));
            } catch (Exception e) {
                throw new BadRequestException("Закрытые смены отсутствуют!");
            }
        }
        return shiftReportRepository.findAll((Specification<ShiftReport>) (root, query, criteriaBuilder) -> {
            var predicates = new ArrayList<Predicate>();
            if (CollectionUtils.isNotEmpty(branchIds)) {
                predicates.add(root.get(ShiftReport_.branchId).in(branchIds));
            } else {
                var branchJoin = root.join(ShiftReport_.branch);
                predicates.add(criteriaBuilder.equal(branchJoin.get(Branch_.companyId), getCurrentCompanyId()));
            }
            if (filter.getFrom() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get(ShiftReport_.endDateTime), filter.getFrom()));
            }
            if (filter.getTo() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get(ShiftReport_.endDateTime), filter.getTo()));
            }
            if (filter.getUserId() != null) {
                predicates.add(criteriaBuilder.equal(root.get(ShiftReport_.userId), filter.getUserId()));
            }
            if (StringUtils.isNotBlank(filter.getSearch())) {
                var searchPredicate = criteriaBuilder.or(
                        criteriaBuilder.like(root.get(ShiftReport_.branch).get(Branch_.serialNumber), filter.getSearchForQuery()),
                        criteriaBuilder.like(root.get(ShiftReport_.terminal).get(Device_.serialNumber), filter.getSearchForQuery())
                );
                predicates.add(searchPredicate);

            }
            query.orderBy(criteriaBuilder.desc(root.get(ShiftReport_.endDateTime)));
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        }, filter.getPageable()).map(shiftReportMapper::toDto);
    }
}
