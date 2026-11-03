package uz.uzkassa.horeca.web.rest.cabinet;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.horeca.constants.ApiConstants;
import uz.uzkassa.horeca.dto.company.CompanyCommonDTO;
import uz.uzkassa.horeca.dto.supplier.SupplierDTO;
import uz.uzkassa.horeca.dto.supplier.SupplierDetailDTO;
import uz.uzkassa.horeca.dto.supplier.SupplierListDTO;
import uz.uzkassa.horeca.enums.CommonStatus;
import uz.uzkassa.horeca.filters.CompanyFilter;
import uz.uzkassa.horeca.interfaces.CrudRestAdvanced;
import uz.uzkassa.horeca.service.SupplierService;

import java.time.LocalDateTime;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.cabinetSupplierRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CabinetSupplierResource
        implements CrudRestAdvanced<Long, SupplierDTO, SupplierDetailDTO, SupplierListDTO, CompanyFilter, CommonStatus, CompanyCommonDTO> {

    SupplierService supplierService;

    @GetMapping(ApiConstants.check + "/{tin}")
    public ResponseEntity<SupplierDTO> checkTin(@PathVariable(name = "tin") String tin) {
        return ResponseEntity.ok(supplierService.check(tin));
    }

    @PostMapping
    @Override
    public ResponseEntity<Long> create(@RequestBody SupplierDTO supplierDTO) {
        return ResponseEntity.ok(supplierService.create(supplierDTO));
    }

    @PutMapping(ApiConstants.id)
    @Override
    public ResponseEntity<Long> update(@PathVariable Long id, @RequestBody SupplierDTO supplierDTO) {
        return ResponseEntity.ok(supplierService.update(id, supplierDTO));
    }

    @GetMapping(ApiConstants.id)
    @Override
    public ResponseEntity<SupplierDetailDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(supplierService.get(id));
    }

    @GetMapping
    @Override
    public ResponseEntity<Page<SupplierListDTO>> getList(@ParameterObject CompanyFilter filter) {
        return ResponseEntity.ok(supplierService.getList(filter));
    }

    @DeleteMapping(ApiConstants.id)
    @Override
    public ResponseEntity<Void> delete(@PathVariable Long id, @RequestParam(required = false) LocalDateTime lastModifiedDate) {
        supplierService.delete(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping(ApiConstants.lookUp)
    @Override
    public ResponseEntity<List<CompanyCommonDTO>> lookUp(@ParameterObject CompanyFilter filter) {
        return ResponseEntity.ok(supplierService.lookUp(filter));
    }

    @PatchMapping(ApiConstants.updateStatus)
    @Override
    public ResponseEntity<Void> updateStatus(@PathVariable Long id, @RequestBody CommonStatus status) {
        supplierService.updateStatus(id, status);
        return ResponseEntity.ok().build();
    }
}
