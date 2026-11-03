package uz.uzkassa.smartpossupply.service;


import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import uz.uzkassa.smartpossupply.domain.company.CompanyFieldHistory;
import uz.uzkassa.smartpossupply.dto.company.CompanyFieldHistorySuggestionDTO;
import uz.uzkassa.smartpossupply.dto.companyfieldhistory.CompanyFieldHistoryDTO;
import uz.uzkassa.smartpossupply.filters.CompanyFieldHistoryFilter;
import uz.uzkassa.smartpossupply.repository.CompanyFieldHistoryRepository;
import uz.uzkassa.smartpossupply.utils.SecurityUtils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static lombok.AccessLevel.PRIVATE;

@Service
@Transactional(readOnly = true)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Slf4j
public class CompanyFieldHistoryService {

    CompanyFieldHistoryRepository companyFieldHistoryRepository;

    public List<CompanyFieldHistorySuggestionDTO> lookUp(CompanyFieldHistoryFilter filter) {
        filter.setCompanyId(SecurityUtils.getCurrentCompanyId());
        return companyFieldHistoryRepository.lookUp(filter)
                .stream()
                .map(CompanyFieldHistory::toSuggestionDTO)
                .toList();
    }

    private boolean isValid(CompanyFieldHistoryDTO companyFieldHistoryDTO) {
        if (companyFieldHistoryDTO.getCompanyId() == null || companyFieldHistoryDTO.getFieldType() == null || !StringUtils.hasText(companyFieldHistoryDTO.getIdentifier())) {
            return false;
        }

        if (companyFieldHistoryRepository.existsByCompanyIdAndFieldTypeAndIdentifierAndDeletedFalse(companyFieldHistoryDTO.getCompanyId(), companyFieldHistoryDTO.getFieldType(), companyFieldHistoryDTO.getIdentifier())) {
            return false;
        }

        return true;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void create(CompanyFieldHistoryDTO companyFieldHistoryDTO) {
        if (!this.isValid(companyFieldHistoryDTO)) {
            return;
        }
        CompanyFieldHistory companyFieldHistory = new CompanyFieldHistory();
        companyFieldHistory.setCompanyId(companyFieldHistoryDTO.getCompanyId());
        companyFieldHistory.setUserId(companyFieldHistoryDTO.getUserId());
        companyFieldHistory.setFieldType(companyFieldHistoryDTO.getFieldType());
        companyFieldHistory.setIdentifier(companyFieldHistoryDTO.getIdentifier());
        companyFieldHistory.setDisplayValue(companyFieldHistoryDTO.getDisplayValue());
        companyFieldHistory.setExtraData(companyFieldHistoryDTO.getExtraData());
        companyFieldHistoryRepository.save(companyFieldHistory);
    }


    public static Map<String, Object> buildTransportExtraData(Integer transportType, Integer ownershipType, String tin) {
        Map<String, Object> extraData = new LinkedHashMap<>();
        if (transportType != null) {
            extraData.put("transportType", transportType);
        }
        if (ownershipType != null) {
            extraData.put("ownershipType", ownershipType);
        }
        if (StringUtils.hasText(tin)) {
            extraData.put("tin", tin);
        }
        return extraData;
    }
}
