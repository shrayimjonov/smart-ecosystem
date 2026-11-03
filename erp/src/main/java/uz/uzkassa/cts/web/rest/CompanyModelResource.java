package uz.uzkassa.cts.web.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.cts.domain.CompanyModel;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.dto.device.CompanyModelDTO;
import uz.uzkassa.cts.service.CompanyModelService;

import java.util.List;

/**
 * REST controller for managing {@link CompanyModel}.
 */
@RestController
@RequestMapping("/api/company/models")
@Tag(name = "Company Models")
@Slf4j
@Deprecated
public class CompanyModelResource {

    private final CompanyModelService companyModelService;


    public CompanyModelResource(CompanyModelService companyModelService) {
        this.companyModelService = companyModelService;
    }

    @Operation(summary ="Add Models to Company")
    @PostMapping
    public ResponseEntity<Void> add(@Valid @RequestBody CompanyModelDTO companyModelDTO) {
        log.debug("REST request to add model to Company: {}", companyModelDTO.getCompanyId());
        companyModelService.add(companyModelDTO);
        return ResponseEntity.ok().build();
    }

    @Operation(summary ="List Company Models")
    @GetMapping
    public ResponseEntity<List<CommonDTO>> list() {
        log.debug("REST request to all models");
        return ResponseEntity.ok(companyModelService.getItems());
    }

    @Operation(summary ="Get Company Registered Models")
    @GetMapping("/{companyId}")
    public ResponseEntity<List<CommonDTO>> getByCompany(@PathVariable(name = "companyId") Long companyId) {
        log.debug("REST request to get company models: {}", companyId);
        return ResponseEntity.ok(companyModelService.getByCompany(companyId));
    }
}
