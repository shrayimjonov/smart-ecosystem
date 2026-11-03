package uz.uzkassa.smartpossupply.service;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.smartpossupply.domain.company.Company;
import uz.uzkassa.smartpossupply.domain.company.CompanyPaymentSettings;
import uz.uzkassa.smartpossupply.dto.marketplacesettings.CompanyPaymentSettingsDTO;
import uz.uzkassa.smartpossupply.enums.MarketplaceSettingsStep;
import uz.uzkassa.smartpossupply.repository.CompanyPaymentSettingsRepository;

import static lombok.AccessLevel.PRIVATE;

@Service
@Transactional(readOnly = true)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CompanyPaymentSettingsService extends BaseService {

    CompanyPaymentSettingsRepository companyPaymentSettingsRepository;
    CacheService cacheService;
    CompanySyncService companySyncService;

    @Transactional
    public Long create(CompanyPaymentSettingsDTO companyPaymentSettingsDTO) {
        Company company = getCurrentCompany();
        CompanyPaymentSettings companyPaymentSettings = companyPaymentSettingsRepository.findFirstByCompanyId(company.getId()).orElseGet(CompanyPaymentSettings::new);
        companyPaymentSettings.setCompanyId(company.getId());
        companyPaymentSettings.setTypes(companyPaymentSettingsDTO.getTypes());
        companyPaymentSettingsRepository.save(companyPaymentSettings);
        if (MarketplaceSettingsStep.COMPLETED != company.getMarketplaceSettingsStep()) {
            company.setMarketplaceSettingsStep(MarketplaceSettingsStep.COMPLETED);
            companyRepository.save(company);
            cacheService.evictCompany(company.getTin());
            companySyncService.batchSyncToMarketplace(company);
        }
        return companyPaymentSettings.getId();
    }

    public CompanyPaymentSettingsDTO get() {
        return companyPaymentSettingsRepository.findFirstByCompanyId(getCurrentCompanyId()).map(companyPaymentSettings -> {
            CompanyPaymentSettingsDTO companyPaymentSettingsDTO = new CompanyPaymentSettingsDTO();
            companyPaymentSettingsDTO.setTypes(companyPaymentSettings.getTypes());
            return companyPaymentSettingsDTO;
        }).orElse(null);
    }
}