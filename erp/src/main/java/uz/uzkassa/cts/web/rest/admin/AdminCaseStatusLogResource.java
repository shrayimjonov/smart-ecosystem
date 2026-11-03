package uz.uzkassa.cts.web.rest.admin;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.cts.constants.ApiConstants;
import uz.uzkassa.cts.dto.casestatuslog.CaseStatusLogListDTO;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.service.CaseStatusLogService;



@RestController
@RequestMapping(ApiConstants.adminCaseStatusLogRootApi)
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AdminCaseStatusLogResource {

    CaseStatusLogService caseStatusLogService;

    @GetMapping
    public ResponseEntity<Page<CaseStatusLogListDTO>> getList(@ParameterObject BaseFilter filter) {
        return ResponseEntity.ok(caseStatusLogService.getList(filter));
    }
}
