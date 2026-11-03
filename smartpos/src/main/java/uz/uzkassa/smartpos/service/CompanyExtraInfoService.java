package uz.uzkassa.smartpos.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import uz.uzkassa.smartpos.domain.postgres.company.ActivityType;
import uz.uzkassa.smartpos.domain.postgres.company.Company;
import uz.uzkassa.smartpos.domain.postgres.company.CompanyExtraInfo;
import uz.uzkassa.smartpos.dto.company.CompanyExtraInfoDTO;
import uz.uzkassa.smartpos.enums.ProductGroup;
import uz.uzkassa.smartpos.repository.postgres.company.CompanyExtraInfoRepository;
import uz.uzkassa.smartpos.security.SecurityUtils;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CompanyExtraInfoService {
    private final CompanyService companyService;
    private final CompanyExtraInfoRepository companyExtraInfoRepository;


    public ResponseEntity<CompanyExtraInfoDTO> getExtraInfo() {
        Company company = companyService.getCompany();
        Long companyId = company.getId();
        Optional<CompanyExtraInfo> extraInfo = companyExtraInfoRepository.findByCompanyId(companyId);
        CompanyExtraInfoDTO extraInfoDTO;
        if (extraInfo.isPresent()) {
            extraInfoDTO = new CompanyExtraInfoDTO(extraInfo.get());
            extraInfoDTO.setCanDisableExcise(defineThatCanMakeExciseDisabled(company));
        } else {
            extraInfoDTO = new CompanyExtraInfoDTO();
            extraInfoDTO.setCompanyId(companyId);
            extraInfoDTO.setCanDisableExcise(defineThatCanMakeExciseDisabled(company));
            extraInfoDTO.setExciseEnabled(Boolean.TRUE);
            extraInfoDTO.setMarkingCheckEnabled(false);
        }
        return ResponseEntity.ok(extraInfoDTO);
    }


    public Boolean defineExciseDisabledOrNot(Long companyId) {
        Optional<CompanyExtraInfo> extraInfo = companyExtraInfoRepository.findByCompanyId(companyId);
        return extraInfo.map(CompanyExtraInfo::isExciseEnabled).orElse(Boolean.TRUE);
    }

    public void saveCompanyExtraInfo(Boolean exciseEnable) {
        CompanyExtraInfo companyExtraInfo = CompanyExtraInfo.builder()
                .id(SecurityUtils.getCurrentCompanyId())
                .exciseEnabled(exciseEnable)
                .build();
        companyExtraInfoRepository.save(companyExtraInfo);
    }

    public void toggleMarkingCheck(boolean markingCheckEnabled) {
        CompanyExtraInfo companyExtraInfo = companyExtraInfoRepository.findById(SecurityUtils.getCurrentCompanyId())
                .orElseGet(() -> CompanyExtraInfo.builder().id(SecurityUtils.getCurrentCompanyId()).build());
        companyExtraInfo.setMarkingCheckEnabled(markingCheckEnabled);
        companyExtraInfoRepository.save(companyExtraInfo);
    }

    public CompanyExtraInfoDTO extraInfo() {
        return companyExtraInfoRepository.findById(SecurityUtils.getCurrentCompanyId())
                .map(CompanyExtraInfoDTO::new)
                .orElseGet(CompanyExtraInfoDTO::new);
    }

    private Boolean defineThatCanMakeExciseDisabled(Company company) {
        return company.getActivityTypes().stream().map(ActivityType::getProductGroup).anyMatch(ProductGroup.Petrol::equals);
    }
}
