package uz.uzkassa.horeca.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.horeca.domain.company.CompanySettings;
import uz.uzkassa.horeca.dto.companysettings.CompanySettingsDTO;
import uz.uzkassa.horeca.dto.companysettings.CompanySettingsDetailDTO;
import uz.uzkassa.horeca.repository.CompanySettingsRepository;

import java.util.Optional;

@Service

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class CompanySettingsService extends BaseService {

    CompanySettingsRepository companySettingsRepository;

    @Transactional
    public Long create(CompanySettingsDTO companySettingsDTO) {
        CompanySettings companySettings = companySettingsRepository
                .findFirstByCompanyId(getCurrentCompanyId())
                .orElse(new CompanySettings());
        companySettings.setFeePercent(companySettingsDTO.getFeePercent());
        companySettings.setCompanyId(getCurrentCompanyId());
        companySettings.setBranchId(Optional.ofNullable(companySettingsDTO.getBranchId()).orElseGet(BaseService::getCurrentBranchId));
        return companySettingsRepository.save(companySettings).getId();
    }

    @Transactional
    public Long update(Long id, CompanySettingsDTO companySettingsDTO) {
        return companySettingsRepository
                .findById(id)
                .map(companySettings -> {
                    companySettings.setFeePercent(companySettingsDTO.getFeePercent());
                    companySettingsRepository.save(companySettings);
                    return id;
                })
                .orElseThrow(notFoundExceptionThrow(CompanySettings.class.getSimpleName(), "id", id));
    }

    public CompanySettingsDetailDTO get() {
        return companySettingsRepository
                .findFirstByCompanyId(getCurrentCompanyId())
                .map(companySettings -> {
                    CompanySettingsDetailDTO companySettingsDetailDTO = new CompanySettingsDetailDTO();
                    companySettingsDetailDTO.setId(companySettings.getId());
                    companySettingsDetailDTO.setCompany(toCompanyBaseDTO(companySettings.getCompany()));
                    if (companySettings.getBranchId() != null) {
                        companySettingsDetailDTO.setBranch(companySettings.getBranch().toCommonDTO());
                    }
                    return companySettingsDetailDTO;
                })
                .orElse(null);
    }
}
