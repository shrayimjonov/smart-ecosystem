package uz.uzkassa.smartpossupply.service;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import uz.uzkassa.smartpossupply.domain.base.SimpleEntity;
import uz.uzkassa.smartpossupply.domain.company.Company;
import uz.uzkassa.smartpossupply.domain.company.CompanyDeliverySettings;
import uz.uzkassa.smartpossupply.dto.marketplacesettings.CompanyDeliverySettingsDTO;
import uz.uzkassa.smartpossupply.dto.marketplacesettings.CompanyDeliverySettingsDetailDTO;
import uz.uzkassa.smartpossupply.enums.DeliveryType;
import uz.uzkassa.smartpossupply.enums.MarketplaceSettingsStep;
import uz.uzkassa.smartpossupply.repository.CompanyDeliverySettingsRepository;
import uz.uzkassa.smartpossupply.repository.WarehouseRepository;

import java.util.stream.Collectors;

import static lombok.AccessLevel.PRIVATE;

@Service
@Transactional(readOnly = true)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CompanyDeliverySettingsService extends BaseService {

    CompanyDeliverySettingsRepository companyDeliverySettingsRepository;
    WarehouseRepository warehouseRepository;
    CacheService cacheService;

    @Transactional
    public Long create(CompanyDeliverySettingsDTO companyDeliverySettingsDTO) {
        Company company = getCurrentCompany();
        CompanyDeliverySettings companyDeliverySettings = companyDeliverySettingsRepository.findFirstByCompanyIdAndType(company.getId(), companyDeliverySettingsDTO.getType()).orElseGet(CompanyDeliverySettings::new);
        companyDeliverySettings.setCompanyId(company.getId());
        companyDeliverySettings.setType(companyDeliverySettingsDTO.getType());
        companyDeliverySettings.setFromDay(companyDeliverySettingsDTO.getFromDay());
        companyDeliverySettings.setToDay(companyDeliverySettingsDTO.getToDay());
        companyDeliverySettings.setWarehouseIds(companyDeliverySettingsDTO.getWarehouseIds());
        companyDeliverySettings.setSelectAll(companyDeliverySettingsDTO.isSelectAll());
        companyDeliverySettingsRepository.save(companyDeliverySettings);

        if (MarketplaceSettingsStep.COMPLETED != company.getMarketplaceSettingsStep()) {
            if (DeliveryType.SELF_PICKUP == companyDeliverySettingsDTO.getType()) {
                company.setMarketplaceSettingsStep(MarketplaceSettingsStep.DELIVERY);
            } else if (DeliveryType.DELIVERY == companyDeliverySettingsDTO.getType()) {
                company.setMarketplaceSettingsStep(MarketplaceSettingsStep.PAYMENT_TYPE);
            }
            companyRepository.save(company);
            cacheService.evictCompany(company.getTin());
        }
        return companyDeliverySettings.getId();
    }

    public CompanyDeliverySettingsDetailDTO get(DeliveryType type) {
        CompanyDeliverySettings companyDeliverySettings;
        if (type != null) {
            companyDeliverySettings = companyDeliverySettingsRepository.findFirstByCompanyIdAndType(getCurrentCompanyId(), type).orElse(null);
        } else {
            companyDeliverySettings = companyDeliverySettingsRepository.findFirstByCompanyId(getCurrentCompanyId()).orElse(null);
        }
        if (companyDeliverySettings != null) {
            CompanyDeliverySettingsDetailDTO companyDeliverySettingsDetailDTO = new CompanyDeliverySettingsDetailDTO();
            companyDeliverySettingsDetailDTO.setId(companyDeliverySettings.getId());
            companyDeliverySettingsDetailDTO.setType(companyDeliverySettings.getType());
            companyDeliverySettingsDetailDTO.setFromDay(companyDeliverySettings.getFromDay());
            companyDeliverySettingsDetailDTO.setToDay(companyDeliverySettings.getToDay());
            companyDeliverySettingsDetailDTO.setSelectAll(companyDeliverySettings.isSelectAll());
            if (!CollectionUtils.isEmpty(companyDeliverySettings.getWarehouseIds())) {
                companyDeliverySettingsDetailDTO.setWarehouses(warehouseRepository.findAllById(companyDeliverySettings.getWarehouseIds()).stream().map(SimpleEntity::toCommonDTO).collect(Collectors.toSet()));
            }
            return companyDeliverySettingsDetailDTO;
        }
        return null;
    }
}