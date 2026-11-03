package uz.uzkassa.smartpossupply.service;

import lombok.extern.slf4j.Slf4j;
import uz.uzkassa.smartpossupply.constants.MessageConstants;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.smartpossupply.domain.auth.User;
import uz.uzkassa.smartpossupply.domain.company.Company;
import uz.uzkassa.smartpossupply.domain.company.CompanyBank;
import uz.uzkassa.smartpossupply.dto.companybank.CompanyBankDTO;
import uz.uzkassa.smartpossupply.dto.companybank.CompanyBankDetailDTO;
import uz.uzkassa.smartpossupply.dto.payload.SyncPayload;
import uz.uzkassa.smartpossupply.dto.sync.marketplace.MarketplaceCompanyBankDTO;
import uz.uzkassa.smartpossupply.dto.sync.marketplace.MarketplaceCompanyBankSyncDTO;
import uz.uzkassa.smartpossupply.enums.AppType;
import uz.uzkassa.smartpossupply.enums.Role;
import uz.uzkassa.smartpossupply.enums.UserStatus;
import uz.uzkassa.smartpossupply.integration.MarketplaceClient;
import uz.uzkassa.smartpossupply.rabbitmq.producer.RabbitMqCompanyBankSyncProducer;
import uz.uzkassa.smartpossupply.rabbitmq.producer.RabbitMqCompanySyncProducer;
import uz.uzkassa.smartpossupply.repository.CompanyBankRepository;

import java.util.List;
import java.util.Optional;

import static lombok.AccessLevel.PRIVATE;

@Slf4j
@Service
@Transactional(readOnly = true)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CompanyBankService extends BaseService {

    CompanyBankRepository companyBankRepository;
    CacheService cacheService;
    RabbitMqCompanyBankSyncProducer rabbitMqCompanyBankSyncProducer;
    RabbitMqCompanySyncProducer rabbitMqCompanySyncProducer;
    MarketplaceClient marketplaceClient;

    @Transactional
    public Long create(CompanyBankDTO companyBankDTO) {
        Long companyId = Optional.ofNullable(companyBankDTO.getCompanyId()).orElseGet(BaseService::getCurrentCompanyId);
        Company company = companyRepository.findById(companyId).orElseThrow(validationService.entityNotFoundExceptionThrow(Company.class.getSimpleName(), "id", companyId));

        if (companyBankRepository.findByCompanyIdAndBankId(company.getId(), companyBankDTO.getBankId()).isPresent()) {
            throw validationService.conflictExceptionThrow(localizationService.localize(MessageConstants.CLIENT_HAS_ITEM_ALREADY, new Object[]{"bank"})).get();
        }

        CompanyBank companyBank = new CompanyBank();
        companyBank.setBankId(companyBankDTO.getBankId());
        companyBank.setCompanyId(company.getId());
        companyBank.setCompany(company);

        companyBank.setAccountNumber(companyBankDTO.getAccountNumber());
        companyBank.setOked(companyBankDTO.getOked());
        companyBank.setMain(Optional.of(companyBankDTO.getMain()).orElse(true));
        companyBankRepository.save(companyBank);

        company.setCompanyBankId(companyBank.getId());
        companyRepository.save(company);
        cacheService.evictCompany(company.getTin());
        if (companyBank.getCompany().getMarketplaceId() != null) {
            var data = new SyncPayload(companyBank.getId(), AppType.MARKETPLACE);
            log.info("Sending message to queue: {}", data);
            rabbitMqCompanyBankSyncProducer.sync(data);
        }
        if (companyBank.getCompany().getErpId() != null) {
            rabbitMqCompanySyncProducer.sync(new SyncPayload(companyBank.getCompanyId(), AppType.ERP));
        }
        return companyBank.getId();
    }

    @Transactional
    public Long update(Long id, CompanyBankDTO companyBankDTO) {
        CompanyBank companyBank = companyBankRepository
            .findById(id)
            .orElseThrow(validationService.entityNotFoundExceptionThrow(CompanyBank.class.getSimpleName(), "id", id));

        companyBank.setBankId(companyBankDTO.getBankId());
        companyBank.setAccountNumber(companyBankDTO.getAccountNumber());
        companyBank.setOked(companyBankDTO.getOked());
        companyBank.setMain(companyBankDTO.getMain());
        companyBankRepository.save(companyBank);

        Company company = companyBank.getCompany();
        company.setCompanyBankId(companyBank.getId());
        companyRepository.save(company);
        cacheService.evictCompany(company.getTin());

        companyBankRepository.update(companyBank.isMain(), companyBank.getCompanyId(), companyBank.getBankId(), companyBank.getId());
        if (companyBank.getCompany().getMarketplaceId() != null) {
            var data = new SyncPayload(companyBank.getId(), AppType.MARKETPLACE);
            log.info("Sending message to queue: {}", data);
            rabbitMqCompanyBankSyncProducer.sync(data);
        }
        if (companyBank.getCompany().getErpId() != null) {
            rabbitMqCompanySyncProducer.sync(new SyncPayload(companyBank.getCompanyId(), AppType.ERP));
        }
        return companyBank.getId();
    }

    public CompanyBankDetailDTO get(Long companyId) {
        companyId = Optional.ofNullable(companyId).orElseGet(BaseService::getCurrentCompanyId);
        Company company = companyRepository.getCompanyWithBankById(companyId).orElseThrow(validationService.entityNotFoundExceptionThrow(Company.class.getSimpleName(), "id", companyId));
        return this.get(company);
    }

    public CompanyBankDetailDTO get(Company company) {
        if (company.getCompanyBank() == null) {
            return null;
        }
        CompanyBankDetailDTO result = new CompanyBankDetailDTO();
        result.setId(company.getCompanyBank().getId());
        result.setAccountNumber(company.getCompanyBank().getAccountNumber());
        result.setOked(company.getCompanyBank().getOked());
        result.setMain(company.getCompanyBank().isMain());
        result.setBank(company.getCompanyBank().getBank().toLookUpDTO());
        if (company.getCompanyBank().getBank().getParent() != null) {
            result.setParentBank(company.getCompanyBank().getBank().getParent().toLookUpDTO());
        }
        return result;
    }

    public MarketplaceCompanyBankDTO getForMarketplace(Long companyId) {
       return companyBankRepository.findFirstByCompanyIdAndMainIsTrue(companyId).map(companyBank -> {
            MarketplaceCompanyBankDTO marketplaceCompanyBankDTO = new MarketplaceCompanyBankDTO();
            marketplaceCompanyBankDTO.setId(companyBank.getId());
            marketplaceCompanyBankDTO.setAccountNumber(companyBank.getAccountNumber());
            marketplaceCompanyBankDTO.setBank(companyBank.getBank().toLookUpDTO());
            return marketplaceCompanyBankDTO;
        }).orElseThrow(validationService.entityNotFoundExceptionThrow(CompanyBank.class.getSimpleName()));
    }

    @Transactional
    public void syncQueue(SyncPayload payload) {
        CompanyBank companyBank = companyBankRepository.findByIdToSync(payload.getId()).orElseThrow(validationService.entityNotFoundExceptionThrow(CompanyBank.class.getSimpleName(), "id", payload.getId()));
        if (AppType.MARKETPLACE == payload.getAppType()) {
            List<Role> roles = List.of(Role.ROLE_BUSINESS_OWNER, Role.ROLE_MARKETPLACE_BUSINESS_OWNER);

            User user = userRepository.findAllByCompanyIdAndStatusAndRoleInAndDeletedIsFalse(companyBank.getCompany().getId(), UserStatus.ACTIVE, roles)
                    .stream()
                    .findFirst()
                    .orElseThrow(validationService.entityNotFoundExceptionThrow(User.class.getSimpleName(), "companyId", companyBank.getCompany().getId()));


            MarketplaceCompanyBankSyncDTO marketplaceCompanyBankSyncDTO = new MarketplaceCompanyBankSyncDTO();
            marketplaceCompanyBankSyncDTO.setId(companyBank.getId());
            marketplaceCompanyBankSyncDTO.setCompanyId(companyBank.getCompanyId());
            marketplaceCompanyBankSyncDTO.setMfo(companyBank.getBank().getMfo());
            marketplaceCompanyBankSyncDTO.setAccountNumber(companyBank.getAccountNumber());
            marketplaceCompanyBankSyncDTO.setAppType(AppType.SUPPLY.name());
            marketplaceCompanyBankSyncDTO.setOwnerRole(user.getRole().getCode());
            marketplaceClient.syncCompanyBank(marketplaceCompanyBankSyncDTO);
        }
    }
}
