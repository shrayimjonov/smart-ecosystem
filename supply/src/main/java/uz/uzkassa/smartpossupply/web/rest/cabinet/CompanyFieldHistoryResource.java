package uz.uzkassa.smartpossupply.web.rest.cabinet;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.smartpossupply.constants.ApiConstants;
import uz.uzkassa.smartpossupply.dto.company.CompanyFieldHistorySuggestionDTO;
import uz.uzkassa.smartpossupply.enums.FieldHistoryType;
import uz.uzkassa.smartpossupply.filters.CompanyFieldHistoryFilter;
import uz.uzkassa.smartpossupply.service.CompanyFieldHistoryService;
import uz.uzkassa.smartpossupply.utils.SecurityUtils;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.cabinetCompanyFieldHistoryRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CompanyFieldHistoryResource {

    CompanyFieldHistoryService companyFieldHistoryService;

    @GetMapping(ApiConstants.lookUp)
    public ResponseEntity<List<CompanyFieldHistorySuggestionDTO>> lookUp(@ParameterObject CompanyFieldHistoryFilter filter) {
        if (SecurityUtils.getCurrentCompanyId() == null) {
            return ResponseEntity.ok(new ArrayList<>());
        }
        return ResponseEntity.ok(companyFieldHistoryService.lookUp(filter));
    }

    @GetMapping(ApiConstants.fieldTypes)
    public ResponseEntity<EnumSet<FieldHistoryType>> getFieldTypes() {
        return ResponseEntity.ok(EnumSet.allOf(FieldHistoryType.class));
    }
}