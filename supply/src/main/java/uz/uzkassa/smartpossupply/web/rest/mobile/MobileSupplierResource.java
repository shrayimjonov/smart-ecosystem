package uz.uzkassa.smartpossupply.web.rest.mobile;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.smartpossupply.constants.ApiConstants;
import uz.uzkassa.smartpossupply.dto.company.CompanyBaseDTO;
import uz.uzkassa.smartpossupply.dto.supplier.SupplierDetailDTO;
import uz.uzkassa.smartpossupply.dto.supplier.SupplierListDTO;
import uz.uzkassa.smartpossupply.filters.CompanyFilter;
import uz.uzkassa.smartpossupply.service.SupplierService;
import uz.uzkassa.smartpossupply.utils.SecurityUtils;

import java.util.ArrayList;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;


@RestController
@RequestMapping(ApiConstants.mobileSupplierRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class MobileSupplierResource {

    SupplierService supplierService;

    @Operation(summary = ApiConstants.Get, operationId = "getSupplierDetails")
    @GetMapping(ApiConstants.id)
    public ResponseEntity<SupplierDetailDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(supplierService.get(id));
    }

    @Operation(summary = ApiConstants.List, operationId = "getSupplierList" )
    @GetMapping
    public ResponseEntity<Page<SupplierListDTO>> getList(@ParameterObject CompanyFilter filter) {
        if (SecurityUtils.getCurrentCompanyId() == null) {
            return ResponseEntity.ok(new PageImpl<>(new ArrayList<>(), filter.getPageable(), 0L));
        }
        return ResponseEntity.ok(supplierService.getList(filter));
    }

    @Operation(summary = ApiConstants.LookUp, operationId = "getSupplierLookUp")
    @GetMapping(ApiConstants.lookUp)
    public ResponseEntity<List<CompanyBaseDTO>> lookUp(@ParameterObject CompanyFilter filter) {
        if (SecurityUtils.getCurrentCompanyId() == null) {
            return ResponseEntity.ok(new ArrayList<>());
        }
        return ResponseEntity.ok(supplierService.lookUp(filter));
    }
}
