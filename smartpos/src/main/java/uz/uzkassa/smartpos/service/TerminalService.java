package uz.uzkassa.smartpos.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Sets;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.smartpos.constants.Constants;
import uz.uzkassa.smartpos.constants.TokenType;
import uz.uzkassa.smartpos.domain.postgres.FileUpload;
import uz.uzkassa.smartpos.domain.postgres.company.Branch;
import uz.uzkassa.smartpos.domain.postgres.company.Company;
import uz.uzkassa.smartpos.domain.postgres.terminal.Device;
import uz.uzkassa.smartpos.domain.postgres.terminal.Device_;
import uz.uzkassa.smartpos.domain.postgres.terminal.TerminalLog;
import uz.uzkassa.smartpos.domain.postgres.terminal.TerminalLog_;
import uz.uzkassa.smartpos.domain.postgres.user.User;
import uz.uzkassa.smartpos.dto.CabinetTerminalDto;
import uz.uzkassa.smartpos.dto.FullNameDTO;
import uz.uzkassa.smartpos.dto.TerminalDTO;
import uz.uzkassa.smartpos.dto.TerminalLogDTO;
import uz.uzkassa.smartpos.dto.elasticsearch.TerminalSerialNumberDto;
import uz.uzkassa.smartpos.dto.etcsms.request.EtcRequestDto;
import uz.uzkassa.smartpos.dto.filter.CabinetTerminalFilter;
import uz.uzkassa.smartpos.dto.filter.management.TerminalFilter;
import uz.uzkassa.smartpos.dto.filter.management.TerminalLogFilter;
import uz.uzkassa.smartpos.enums.DeviceType;
import uz.uzkassa.smartpos.enums.EntityType;
import uz.uzkassa.smartpos.enums.SortTypeEnum;
import uz.uzkassa.smartpos.exceptions.BadRequestException;
import uz.uzkassa.smartpos.filter.specification.TerminalSpecificationFilter;
import uz.uzkassa.smartpos.integration.cto.*;
import uz.uzkassa.smartpos.repository.postgres.common.ActivityTypeRepository;
import uz.uzkassa.smartpos.repository.postgres.common.CityRepository;
import uz.uzkassa.smartpos.repository.postgres.common.RegionRepository;
import uz.uzkassa.smartpos.repository.postgres.specifications.TerminalSpecification;
import uz.uzkassa.smartpos.repository.postgres.terminal.DeviceRepository;
import uz.uzkassa.smartpos.repository.postgres.terminal.TerminalLogRepository;
import uz.uzkassa.smartpos.repository.postgres.user.AuthorityRepository;
import uz.uzkassa.smartpos.repository.postgres.user.UserRepository;
import uz.uzkassa.smartpos.security.AuthoritiesConstants;
import uz.uzkassa.smartpos.security.SecurityUtils;
import uz.uzkassa.smartpos.social.TelegramBotApi;
import uz.uzkassa.smartpos.utils.RandomUtil;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static uz.uzkassa.smartpos.constants.Constants.COMMON_SMS;


@Service
@Slf4j
@RequiredArgsConstructor
public class TerminalService extends SmartposService {
    private final TerminalLogRepository terminalLogRepository;
    private final RegionRepository regionRepository;
    private final CityRepository cityRepository;
    private final ActivityTypeRepository activityTypeRepository;
    private final AuthorityRepository authorityRepository;
    private final PasswordEncoder passwordEncoder;
    private final TelegramBotApi botApi;
    private final CtoClient ctoClient;
    private final ObjectMapper objectMapper;
    private final FileUploadService fileUploadService;
    private final TokenStoreService tokenStoreService;
    private final UserRepository userRepository;
    private final EtcSmsService smsService;
    private final DeviceRepository deviceRepository;

    @Transactional
    public TerminalDTO saveAdmin(TerminalDTO terminalDTO) {
        var terminal = deviceRepository.findFirstBySerialNumberAndType(terminalDTO.getSerialNumber(), DeviceType.FISCAL)
                .orElseGet(Device::new);
        if (terminal.getId() != null && (terminalDTO.getId() == null || !terminal.getId().equals(terminalDTO.getId()))) {
            throw badRequest(localizationService.getMessage("terminal.already.exists")).get();
        }
        Branch branch = branchRepository.getReferenceById(terminalDTO.getBranchId());
        terminal.setBranchId(branch.getId());
        terminal.setSerialNumber(terminalDTO.getSerialNumber());
        terminal.setCompanyId(branch.getCompanyId());
        terminal.setType(DeviceType.FISCAL);
        deviceRepository.save(terminal);
        terminalDTO.setId(terminal.getId());
        return terminalDTO;
    }

    public Page<TerminalDTO> getList(TerminalFilter filter, boolean forAdmin) {
        if (!forAdmin) {
            filter.setCompanyId(getCurrentCompanyId());
            if (filter.getBranchId() == null) {
                filter.setBranchId(getCurrentBranchId());
            }
        }
        TerminalSpecificationFilter specificationFilter = new TerminalSpecificationFilter(filter);
        return deviceRepository.findAll((Specification<Device>) (root, criteriaQuery, criteriaBuilder) ->
                                TerminalSpecification.getPredicateWithSort(root, criteriaQuery, criteriaBuilder, specificationFilter),
                        filter.getPageable())
                .map(Device::getDTO);
    }


    @Transactional
    public void syncTerminal(Long terminalId) {
        var terminal = deviceRepository.findById(terminalId).orElseThrow(notFound(localizationService.getMessage("terminal")));
        CtoResultDTO result = ctoClient.getTerminal(tokenStoreService.getToken(TokenType.CTS), terminal.getSerialNumber());
        if (result != null) {
            result.setDeviceSerial(terminal.getSerialNumber());
            syncTerminal(result);
        }
    }

    @Transactional
    public void syncTerminal(final CtoResultDTO dto) {
        //todo sunnatillo
        try {
            log.info("Sync with cts: {}", objectMapper.writeValueAsString(dto));
        } catch (JsonProcessingException ignored) {
        }
        if (dto.getCustomer() == null || dto.getCustomerBranch() == null) {
            return;
        } else if (dto.getCustomer().getOwner() == null) {
            botApi.sendMessageToAdminChannel("CTO owner is null \n <b>INN</b>=<code>" + dto.getCustomer().getTin()
                    + "</code> \n <b>Serial</b>=<code>" + dto.getDeviceSerial() + "</code>");
        }
//        company
        CtoCompanyDTO customer = dto.getCustomer();
        List<String> tins = Stream.of(customer.getTin(), customer.getOldTin()).filter(StringUtils::isNotBlank).toList();
        var companies = companyRepository.findByCtoIdOrInnIn(customer.getId(), tins);
        if (CollectionUtils.size(companies) > 1) {
            throw new BadRequestException(localizationService.getMessage("company.more.than.one"));
        }
        Company company = companies.size() == 1 ? companies.getFirst() : new Company();
        company.setBillingType(customer.getAppType());
        company.setFiscal(true);
        company.setSyncDate(LocalDateTime.now());
        company.setCtoId(customer.getId());
        if (customer.getName() != null) {
            company.setName(customer.getName());
        }
        if (customer.getTin() != null) {
            company.setInn(customer.getTin());
        }
        if (customer.getOldTin() != null) {
            company.setOldInn(customer.getOldTin());
        }
        company.setNdsPercent(customer.getVat());
        if (customer.getAddress() != null) {
            if (customer.getAddress().getRegionCode() != null) {
                company.setRegionId(regionRepository.findOneByCode(customer.getAddress().getRegionCode()).orElseThrow(notFound(localizationService.getMessage("region"))).getId());
                if (customer.getAddress().getDistrictCode() != null) {
                    company.setCityId(cityRepository.findOneByCodeAndRegionCode(customer.getAddress().getDistrictCode(), customer.getAddress().getRegionCode())
                            .orElseThrow(notFound(localizationService.getMessage("city")))
                            .getId());
                }
                company.setAddress(customer.getAddress().getAddressStr());
            }
        }
        if (customer.getBusinessType() != null) {
            company.setBusinessType(customer.getBusinessType());
        }
        if (customer.getActivityTypeCode() != null && customer.getActivityTypeParentCode() != null) {
            activityTypeRepository.findOneByCodeAndParent(customer.getActivityTypeCode(), customer.getActivityTypeParentCode())
                    .ifPresent(activityType -> company.getActivityTypes().add(activityType));
        }
        companyRepository.save(company);

//        branch
        CtoBranchDTO customerBranch = dto.getCustomerBranch();
        Optional<Branch> optionalBranch = branchRepository.findFirstByCtoId(customerBranch.getId());
        if (optionalBranch.isEmpty() && customerBranch.getOfdId() != null) {
            optionalBranch = branchRepository.findFirstByOfdId(customerBranch.getOfdId());
        }
        if (optionalBranch.isEmpty()) {
            optionalBranch = branchRepository.findFirstByCompanyIdAndName(company.getId(), customerBranch.getName());
        }
        Branch branch = optionalBranch.orElseGet(Branch::new);
        branch.setDeleted(false);
        branch.setFiscal(true);
        branch.setCtoId(customerBranch.getId());
        if (customerBranch.getName() != null) {
            branch.setName(customerBranch.getName());
        }
        if (customerBranch.getActivityTypeCode() != null && customerBranch.getActivityTypeParentCode() != null) {
            branch.setActivityTypeId(activityTypeRepository.findOneByCodeAndParent(customerBranch.getActivityTypeCode(), customerBranch.getActivityTypeParentCode()).orElseThrow(notFound(localizationService.getMessage("sphere.activity.missing"))).getId());
        }
        if (customerBranch.getAddress() != null) {
            if (customerBranch.getAddress().getRegionCode() != null) {
                branch.setRegionId(regionRepository.findOneByCode(customerBranch.getAddress().getRegionCode())
                        .orElseThrow(notFound(localizationService.getMessage("region"))).getId());
                if (customerBranch.getAddress().getDistrictCode() != null) {
                    branch.setCityId(cityRepository.findOneByCodeAndRegionCode(customerBranch.getAddress().getDistrictCode(), customerBranch.getAddress().getRegionCode())
                            .orElseThrow(notFound(localizationService.getMessage("city"))).getId());
                }
            }
            branch.setAddress(customerBranch.getAddress().getAddressStr());
        }
        if (customerBranch.getLatitude() != null) {
            branch.setLatitude(customerBranch.getLatitude());
        }
        if (customerBranch.getLongitude() != null) {
            branch.setLongitude(customerBranch.getLongitude());
        }
        if (customerBranch.getOfdId() != null) {
            branch.setOfdId(customerBranch.getOfdId());
        }
        branch.setCompanyId(company.getId());
        branchRepository.save(branch);

        // manager
        var managerDTO = dto.getCustomer().getManager();
        if (managerDTO != null) {
            User manager = userRepository.findOneByLogin(managerDTO.getLogin()).orElseGet(() -> {
                User user = new User();
                user.setLogin(managerDTO.getLogin());
                user.setPatronymic(managerDTO.getPatronymic());
                user.setFirstName(managerDTO.getFirstName());
                user.setLastName(managerDTO.getLastName());
                user.setBranchId(getMainBranchIdForAdminCompany());
                user.setCompanyId(getAdminCompanyId());
                user.setActivated(true);
                user.setDeleted(false);
                user.getAuthorities().addAll(Set.of(
                        authorityRepository.getReferenceById(AuthoritiesConstants.SALES_MANAGER),
                        authorityRepository.getReferenceById(AuthoritiesConstants.SUPPORT),
                        authorityRepository.getReferenceById(AuthoritiesConstants.SUPPORT_MANAGER))
                );
                final String s = RandomUtil.generateNumericKey();
                user.setPassword(passwordEncoder.encode(s));
                clearLoginCaches(managerDTO.getLogin());
                userRepository.save(user);
                return user;
            });
            company.setManagerId(manager.getId());
        }

//        owner
        FullNameDTO ownerDto = dto.getCustomer().getOwner();
        User owner = userRepository.findOneByLogin(ownerDto.getPhone())
                .orElseGet(User::new);
        owner.setActivated(true);
        owner.setDeleted(false);
        owner.setOwner(true);
        owner.setLogin(ownerDto.getPhone());
        owner.setFirstName(ownerDto.getFirstName());
        owner.setLastName(ownerDto.getLastName());
        owner.setPatronymic(ownerDto.getPatronymic());
        if (owner.getId() == null) {
            final String key = RandomUtil.generateNumericKey();
            owner.setPassword(passwordEncoder.encode(key));
            smsService.sendSms(new EtcRequestDto(owner.getLogin(), COMMON_SMS));
        } else if (!owner.isOwner()) {
            final String message = StringUtils.join("ВЛАДЕЛЕЦ ИЗМЕНЕНА \n <b>НОМЕР</b>=<code>", owner.getLogin(), "</code> \n <b>ОБНОВИЛ</b>=<code>", SecurityUtils.getCurrentUserLogin().orElse(Constants.SYSTEM_ACCOUNT), "</code>");
            botApi.sendMessageToAdminChannel(message);
        }
        owner.getAuthorities().clear();
        owner.getAuthorities().addAll(Set.of(authorityRepository.getReferenceById(AuthoritiesConstants.USER), authorityRepository.getReferenceById(AuthoritiesConstants.OWNER)));
        owner.setCompanyId(company.getId());
        owner.setBranchId(branch.getId());
        clearLoginCaches(ownerDto.getPhone());
        userRepository.save(owner);
        company.setOwnerId(owner.getId());
        companyRepository.save(company);
        Device terminal = deviceRepository.findFirstBySerialNumberAndType(dto.getDeviceSerial(), DeviceType.FISCAL)
                .orElseGet(Device::new);
        terminal.setSerialNumber(dto.getDeviceSerial());
        terminal.setBranchId(branch.getId());
        terminal.setType(DeviceType.FISCAL);
        terminal.setCompanyId(company.getId());
        deviceRepository.save(terminal);
    }

    public boolean checkTerminal(TerminalDTO dto) {
        Optional<Device> terminalOptional = deviceRepository.findFirstBySerialNumberAndType(dto.getSerialNumber(), DeviceType.FISCAL);
        if (terminalOptional.isEmpty() || !terminalOptional.get().getBranchId().equals(dto.getBranchId())) {
            throw notFound(localizationService.getMessage("terminal")).get();
        }
        return true;
    }

    public List<TerminalDTO> choose(Set<Long> branchIds, Set<Long> branchGroupIds, Long branchId) {
        if (CollectionUtils.isEmpty(branchIds)) branchIds = Sets.newHashSet(getCurrentBranchId());
        branchIds = getBranchesByGroup(branchGroupIds, branchIds);
        if (branchId != null) {
            if (branchIds == null) branchIds = new HashSet<>();
            branchIds.add(branchId);
        }
        return deviceRepository.findAllByBranchIdInAndType(branchIds, DeviceType.FISCAL)
                .stream()
                .map(Device::getDTO)
                .collect(Collectors.toList());
    }

    public List<TerminalDTO> chooseCashbox(Long branchId) {
        List<TerminalDTO> result = new ArrayList<>();
        if (branchId != null) {
            final Branch branch = branchRepository.getReferenceById(branchId);
            if (StringUtils.isNotEmpty(branch.getSerialNumber())) {
                result.add(new TerminalDTO(branch.getSerialNumber(), branchId, branch.getName()));
            }
        } else {
            List<Branch> branches = branchRepository.findAllByCompanyIdAndFiscalIsTrueAndDeletedIsFalse(getCurrentCompanyId());
            branches.stream().filter(branch -> StringUtils.isNotEmpty(branch.getSerialNumber()))
                    .forEach(branch -> result.add(new TerminalDTO(branch.getSerialNumber(), branch.getId(), branch.getName())));
        }
        return result;
    }

    @Transactional
    public void delete(Long id) {
        deviceRepository.deleteById(id);
    }

    @Transactional
    public void removeFromClient(String serialNumber) {
        deviceRepository.findFirstBySerialNumberAndType(serialNumber, DeviceType.FISCAL).ifPresent(deviceRepository::delete);
    }

    public Object checkFiscalizationFromCto(final CtoFiscalCheckDTO request) {
        if (StringUtils.isBlank(request.getDeviceSerial())) {
            request.setDeviceSerial(getCurrentTerminalSnByHeader());
        }
        return ctoClient.checkDeviceFiscalization(tokenStoreService.getToken(TokenType.CTS), request);
    }

    public TerminalSerialNumberDto saveTerminalLog(TerminalSerialNumberDto logDTO) {
        TerminalLog terminalLog = terminalLogRepository.findFirstBySerialNumber(logDTO.getSerialNumber()).orElseGet(TerminalLog::new);
        terminalLog.setSerialNumber(logDTO.getSerialNumber());
        terminalLogRepository.save(terminalLog);
        return logDTO;
    }

    public void updateTerminalLog(String serialNumber, MultipartFile dbFile) {
        TerminalLog terminalLog = terminalLogRepository.findFirstBySerialNumber(serialNumber).orElseThrow(notFound("TerminalLog"));
        if (dbFile == null) {
            throw new BadRequestException("Terminal dataBase file not found");
        }
        FileUpload oldFile = terminalLog.getDbFile();
        FileUpload db = fileUploadService.fileUpload(dbFile, terminalLog.getId(), EntityType.TERMINAL_LOG, true);
        terminalLog.setDbFileId(db.getId());
        terminalLogRepository.save(terminalLog);
        if (oldFile != null) {
            fileUploadService.removeObject(oldFile);
        }
    }


    public boolean checkTerminalLogRequired(String serialNumber) {
        return terminalLogRepository.existsBySerialNumber(serialNumber);
    }

    public Page<TerminalLogDTO> getTerminalLog(TerminalLogFilter filter) {
        return terminalLogRepository.findAll(terminalLogSpecification(filter), filter.getPageable()).map(TerminalLogDTO::new);
    }

    public void deleteTerminalLogFromId(Long id) {
        TerminalLog terminalLog = terminalLogRepository.findById(id).orElseThrow(notFound("TerminalLog"));
        FileUpload dbFile = terminalLog.getDbFile();
        terminalLogRepository.delete(terminalLog);
        if (terminalLog.getDbFile() != null) {
            fileUploadService.removeObject(dbFile);
        }
    }


    @NotNull
    private static Specification<TerminalLog> terminalLogSpecification(TerminalLogFilter filter) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (StringUtils.isNotBlank(filter.getSearch())) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get(TerminalLog_.serialNumber)), filter.getSearchForQuery()));
            }
            if (StringUtils.isNotBlank(filter.getOrderBy())) {
                if (SortTypeEnum.desc.equals(filter.getSortOrder())) {
                    query.orderBy(criteriaBuilder.desc(root.get(filter.getOrderBy())));
                } else {
                    query.orderBy(criteriaBuilder.asc(root.get(filter.getOrderBy())));
                }
            } else {
                query.orderBy(criteriaBuilder.desc(root.get(TerminalLog_.id)));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    @Scheduled(cron = "0 0 1 */3 * *")
    public void deleteTerminalLog() {
        log.info("Delete Terminal Log started");
        terminalLogRepository.deleteAll();
    }

    public Page<CabinetTerminalDto> findAllForCabinet(CabinetTerminalFilter filter) {
        if (!isAdmin()) {
            filter.setCompanyId(getCurrentCompanyId());
        }
        return deviceRepository.findAll((Specification<Device>) (root, query, criteriaBuilder) -> {
            var predicates = new ArrayList<Predicate>();
            root.fetch(Device_.branch);
            predicates.add(criteriaBuilder.equal(root.get(Device_.type), DeviceType.FISCAL));
            if (filter.getCompanyId() != null) {
                predicates.add(criteriaBuilder.equal(root.get(Device_.companyId), filter.getCompanyId()));
            }
            if (filter.getBranchId() != null) {
                predicates.add(criteriaBuilder.equal(root.get(Device_.branchId), filter.getBranchId()));
            }
            if (StringUtils.isNotBlank(filter.getSearch())) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get(Device_.serialNumber)), filter.getSearchForQuery()));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        }, filter.getPageable()).map(device ->
                new CabinetTerminalDto(device.getId(),
                        device.getSerialNumber(),
                        device.getBranch().toCommonDTO()
                ));
    }
}
