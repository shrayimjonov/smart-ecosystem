package uz.uzkassa.cts.web.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.cts.dto.caseTypeEmployees.CaseTypeEmployeesDTO;
import uz.uzkassa.cts.dto.caseTypeEmployees.CaseTypeEmployeesListDTO;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.service.CaseTypeEmployeesService;


@Slf4j
@Tag(name = "Case type Employees")
@RestController
@RequestMapping("/api/case-type-employees")
@RequiredArgsConstructor
@Deprecated
public class CaseTypeEmployeesResource {

    private final CaseTypeEmployeesService typeEmployeesService;

    @Operation(summary = "Insert/Update")
    @PutMapping
    public ResponseEntity<Boolean> update(@Valid @RequestBody CaseTypeEmployeesDTO caseDTO) {
        log.debug("REST request to update CaseUpdateDTO : {}", caseDTO);
        return ResponseEntity.ok(typeEmployeesService.insertUpdate(caseDTO));
    }

    @Operation(summary = "List")
    @GetMapping
    public ResponseEntity<Page<CaseTypeEmployeesListDTO>> getList(BaseFilter filter) {
        return ResponseEntity.ok(typeEmployeesService.list(filter));
    }
}
