package uz.uzkassa.smartpossupply.web.rest.admin;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.smartpossupply.constants.ApiConstants;
import uz.uzkassa.smartpossupply.dto.company.CheckTinResultDTO;
import uz.uzkassa.smartpossupply.dto.company.CompanyBaseDTO;
import uz.uzkassa.smartpossupply.dto.supplier.SupplierDTO;
import uz.uzkassa.smartpossupply.dto.supplier.SupplierDetailDTO;
import uz.uzkassa.smartpossupply.dto.supplier.SupplierListDTO;
import uz.uzkassa.smartpossupply.filters.CompanyFilter;
import uz.uzkassa.smartpossupply.service.CompanyService;
import uz.uzkassa.smartpossupply.service.SupplierService;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.adminSupplierRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AdminSupplierResource {

    SupplierService supplierService;
    CompanyService companyService;

    @Operation(summary = ApiConstants.Create)
    @PostMapping
    public ResponseEntity<Long> create(@RequestBody @Valid SupplierDTO supplierDTO) {
        return ResponseEntity.ok(supplierService.create(supplierDTO).getId());
    }

    @Operation(summary = ApiConstants.Get)
    @GetMapping(ApiConstants.id)
    public ResponseEntity<SupplierDetailDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(supplierService.get(id));
    }

    @Operation(summary = ApiConstants.Update)
    @PutMapping(ApiConstants.id)
    public ResponseEntity<Long> update(@PathVariable Long id, @RequestBody @Valid SupplierDTO supplierDTO) {
        return ResponseEntity.ok(supplierService.update(id, supplierDTO));
    }

    @Operation(summary = ApiConstants.List)
    @GetMapping
    public ResponseEntity<Page<SupplierListDTO>> getList(@ParameterObject CompanyFilter filter) {
        return ResponseEntity.ok(supplierService.getList(filter));
    }

    @Operation(summary = ApiConstants.LookUp)
    @GetMapping(ApiConstants.lookUp)
    public ResponseEntity<List<CompanyBaseDTO>> lookUp(@ParameterObject CompanyFilter filter) {
        return ResponseEntity.ok(supplierService.lookUp(filter));
    }

    @Operation(summary = ApiConstants.Check)
    @GetMapping(ApiConstants.check + "/{tin}")
    public ResponseEntity<CheckTinResultDTO> checkTin(@PathVariable(name = "tin") String tin) {
        return ResponseEntity.ok(companyService.checkTin(tin));
    }

    @Operation(summary = ApiConstants.Delete)
    @DeleteMapping(ApiConstants.id)
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        supplierService.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
