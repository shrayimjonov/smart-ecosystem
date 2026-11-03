package uz.uzkassa.cts.web.rest;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.cts.dto.caseTypeAccess.CaseTypeAccessDTO;
import uz.uzkassa.cts.dto.caseTypeAccess.CaseTypeAccessListDTO;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.service.CaseTypeAccessService;


@Slf4j
@Tag(name = "Case type Employees Access")
@RestController
@RequestMapping("/api/case-type-access")
@RequiredArgsConstructor
@Deprecated
public class CaseTypeAccessResource {

    private final CaseTypeAccessService caseTypeAccessService;

    @PutMapping
    public ResponseEntity<Boolean> update(@Valid @RequestBody CaseTypeAccessDTO caseDTO) {
        caseTypeAccessService.insertUpdate(caseDTO);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<Page<CaseTypeAccessListDTO>> getList(BaseFilter filter) {
        return ResponseEntity.ok(caseTypeAccessService.list(filter));
    }
}
