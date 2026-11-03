package uz.uzkassa.smartpos.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.smartpos.constants.TokenType;
import uz.uzkassa.smartpos.domain.postgres.company.Branch;
import uz.uzkassa.smartpos.domain.postgres.company.Company;
import uz.uzkassa.smartpos.domain.postgres.user.TelegramAccount;
import uz.uzkassa.smartpos.dto.base.CommonDTO;
import uz.uzkassa.smartpos.dto.filter.TelegramAccountFilter;
import uz.uzkassa.smartpos.dto.integration.agreement.AgreementStatus;
import uz.uzkassa.smartpos.dto.integration.agreement.PublicAgreementDTO;
import uz.uzkassa.smartpos.dto.telegram.TelegramAccountDTO;
import uz.uzkassa.smartpos.enums.AuditedFields;
import uz.uzkassa.smartpos.exceptions.BadRequestException;
import uz.uzkassa.smartpos.integration.billing.BillingServiceFeignClient;
import uz.uzkassa.smartpos.mappers.TelegramAccountMapper;
import uz.uzkassa.smartpos.repository.postgres.user.TelegramAccountRepository;
import uz.uzkassa.smartpos.security.SecurityUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service Implementation for managing {@link TelegramAccount}.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TelegramAccountService extends SmartposService {
    private final TelegramAccountRepository telegramAccountRepository;
    private final TelegramAccountMapper telegramAccountMapper;
    private final BillingServiceFeignClient billingServiceFeignClient;
    private final TokenStoreService tokenStoreService;

    /**
     * Save a telegramAccount.
     *
     * @param telegramAccountDTO the entity to save.
     * @return the persisted entity.
     */
    @Transactional
    public TelegramAccountDTO save(TelegramAccountDTO telegramAccountDTO) {
//        operatorRelatedCheck(telegramAccountDTO.getCompanyId(), null, ServiceType.DATA_ENTRY, ServiceType.CUSTOM_TELEGRAM);
        if (telegramAccountRepository.findAllByPhone(telegramAccountDTO.getPhone()).stream().anyMatch(e -> telegramAccountDTO.getId() == null || e.getId().equals(telegramAccountDTO.getId()))) {
            throw new BadRequestException(localizationService.getMessage("phone.number.used"));
        }
        log.debug("Request to save TelegramAccount : {}", telegramAccountDTO);
        boolean isNew = telegramAccountDTO.getId() == null;
        TelegramAccount telegramAccount = !isNew ? telegramAccountRepository.getReferenceById(telegramAccountDTO.getId()) : null;
        boolean isConfirmed = !isNew && telegramAccount.isConfirmed() && StringUtils.equals(telegramAccount.getPhone(), telegramAccountDTO.getPhone());
        boolean reInitNameAndTelegramId = telegramAccount != null && !StringUtils.equals(telegramAccount.getPhone(), telegramAccountDTO.getPhone());

        if (telegramAccountDTO.getCompanyId() == null) {
            telegramAccountDTO.setCompanyId(SecurityUtils.getCurrentCompanyId());
        }
        telegramAccount = telegramAccountMapper.toEntity(telegramAccountDTO);
        if (reInitNameAndTelegramId) {
            telegramAccount.setName(telegramAccountDTO.getName());
            telegramAccount.setTelegramId(telegramAccountDTO.getTelegramId());
        }
        telegramAccount.setConfirmed(isConfirmed);
        telegramAccount = telegramAccountRepository.save(telegramAccount);
        return telegramAccountMapper.toDto(telegramAccount);
    }

    /**
     * Get all the telegramAccounts.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    public Page<TelegramAccountDTO> findAll(Pageable pageable) {
        log.debug("Request to get all TelegramAccounts");
        return telegramAccountRepository.findAll(pageable)
                .map(telegramAccountMapper::toDto);
    }


    /**
     * Get one telegramAccount by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    public Optional<TelegramAccountDTO> findOne(Long id) {
        log.debug("Request to get TelegramAccount : {}", id);
        return telegramAccountRepository.findById(id)
                .map(telegramAccountMapper::toDto);
    }

    /**
     * Delete the telegramAccount by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete TelegramAccount : {}", id);
        telegramAccountRepository.deleteById(id);
    }

    public void createIfNotExists(TelegramAccountDTO dto) {
        if (dto != null) {
            TelegramAccount telegramAccount = null;
            if (dto.getId() != null) {
                telegramAccount = telegramAccountRepository.getReferenceById(dto.getId());
            }
            if (telegramAccount == null) {
                for (TelegramAccount ta : telegramAccountRepository.findAllByPhone(dto.getPhone())) {
                    if (
                            (dto.getUserId() != null && ta.getUserId().equals(dto.getUserId())) ||
                            (dto.getUserId() == null && CollectionUtils.isNotEmpty(dto.getBranches()) && ta.getBranches().stream().map(Branch::getId).toList().stream().anyMatch(dto.getBranches().stream().map(CommonDTO::getId).toList()::contains)) ||
                            (dto.getUserId() == null && CollectionUtils.isEmpty(dto.getBranches()) && dto.getCompanyId() != null && dto.getCompanyId().equals(ta.getCompanyId()))) {
                        telegramAccount = ta;
                    }
                }
            }
            if (telegramAccount == null) {
                telegramAccount = new TelegramAccount();
            }
            if (telegramAccount.isConfirmed()) {
                telegramAccount.setConfirmed(dto.getPhone().equals(telegramAccount.getPhone()) && telegramAccount.getTelegramId() != null);
            }
            telegramAccount.setEnabled(true); // todo we need to remove this line when billing service is on production and telegram Xizmat is ready to use.
            telegramAccount.setUserId(dto.getUserId());
            telegramAccount.setBranches(new HashSet<>(branchRepository.findAllById(dto.getBranches().stream().map(CommonDTO::getId).toList())));
            telegramAccount.setCompanyId(dto.getCompanyId());
            telegramAccount.setName(dto.getName());
            telegramAccount.setPhone(dto.getPhone());
            telegramAccount.setTelegramId(dto.getTelegramId());
            telegramAccountRepository.save(telegramAccount);
            telegramAccount.addLog(AuditedFields.TELEGRAM, telegramAccount.getName(), dto.getPhone(), dto.getCompanyId());
        }
    }

    public List<TelegramAccount> findByPhone(String phone) {
        return telegramAccountRepository.findAllByPhone(phone);
    }

    public void save(TelegramAccount account) {
        telegramAccountRepository.save(account);
    }

    public List<PublicAgreementDTO> getTelegramAgreementList(Long companyId) {
        Company company = companyRepository.getReferenceById(companyId);
        ResponseEntity<List<PublicAgreementDTO>> dtos = billingServiceFeignClient.getActiveAgreements(tokenStoreService.getToken(TokenType.BILLING), company.getCtoId(), company.getInn(), "TELEGRAM", null);
        List<TelegramAccount> enabledAccounts = telegramAccountRepository.findAllbyCompanyIdAndEnabledIsTrue(companyId);
        List<PublicAgreementDTO> multipleActiveAgreements = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(dtos.getBody())) {
            for (PublicAgreementDTO agreementDTO : dtos.getBody()) {
                if (AgreementStatus.ACTIVE.equals(agreementDTO.getStatus()) && agreementDTO.getAvailability() > 0 && enabledAccounts.stream().filter(e -> e.getAgreementId().equals(agreementDTO.getId())).count() < agreementDTO.getAvailability()) {
                    multipleActiveAgreements.add(agreementDTO);
                }
            }
        }
        if (CollectionUtils.isEmpty(multipleActiveAgreements)) {
            Optional<PublicAgreementDTO> dataEntry = getDataEntryAgreementIfExists(company.getCtoId(), company.getInn());
            if (dataEntry.isPresent() && CollectionUtils.isEmpty(enabledAccounts)) {
                multipleActiveAgreements.add(dataEntry.get());
            }
        }
        return multipleActiveAgreements;

    }

    public TelegramAccountDTO forceEnable(Long telegramAccountId) {
        TelegramAccount account = telegramAccountRepository.getReferenceById(telegramAccountId);
        account.setEnabled(true);
        telegramAccountRepository.save(account);
        return telegramAccountMapper.toDto(account);
    }

    private Optional<PublicAgreementDTO> getDataEntryAgreementIfExists(Long ctoId, String inn) {
        ResponseEntity<List<PublicAgreementDTO>> dtos = billingServiceFeignClient.getActiveAgreements(tokenStoreService.getToken(TokenType.BILLING), ctoId, inn, "DATA_ENTRY", null);
        if (Objects.nonNull(dtos) && CollectionUtils.isNotEmpty(dtos.getBody())) {
            return dtos.getBody().stream().findFirst();
        }
        return Optional.empty();
    }

    public void disable(Long id) {
        TelegramAccount account = telegramAccountRepository.getReferenceById(id);
        account.setEnabled(false);
        account.setAgreementId(null);
        account.setAgreementName(null);
        telegramAccountRepository.save(account);
        telegramAccountMapper.toDto(account);
    }

    public Page<TelegramAccountDTO> findAllByFilter(TelegramAccountFilter filter) {
        return telegramAccountRepository.findAllByFilter(filter).map(telegramAccountMapper::toDto);
    }

    /**
     * get all enabled Telegram Accounts and check if the agreement is active.
     * <p>
     * This is scheduled to get fired every hour.
     */
    @Profile({"dev", "prod"})
    @Scheduled(cron = "0 */10 * * * *")// every 10 minutes
    @Transactional
    public void checkAgreementActivity() {
        final List<Long> agreementIds = billingServiceFeignClient.getActiveAgreementIdsByServiceType(tokenStoreService.getToken(TokenType.BILLING), "TELEGRAM").getBody();
        final List<Long> activeAgreementIds = telegramAccountRepository.findAllAgreementIdIsEnabled();
        List<Long> disableByAgreementIds = CollectionUtils.isEmpty(agreementIds) ? new ArrayList<>(activeAgreementIds) : agreementIds.stream().filter(e -> !activeAgreementIds.contains(e)).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(disableByAgreementIds)) {
            telegramAccountRepository.disableByAgreementIds(disableByAgreementIds);
        }
        if (CollectionUtils.isNotEmpty(activeAgreementIds)) {
            telegramAccountRepository.enableByAgreementIds(activeAgreementIds);
        }
    }


    public List<TelegramAccount> findAllByTelegramId(Long telegramId) {
        return telegramAccountRepository.findAllByTelegramId(telegramId);
    }
}
