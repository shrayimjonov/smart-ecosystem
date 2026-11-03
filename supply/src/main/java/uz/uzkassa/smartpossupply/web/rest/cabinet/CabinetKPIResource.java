package uz.uzkassa.smartpossupply.web.rest.cabinet;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.smartpossupply.constants.ApiConstants;
import uz.uzkassa.smartpossupply.dto.kpi.KPIBaseDTO;
import uz.uzkassa.smartpossupply.dto.kpi.KPICalculateDTO;
import uz.uzkassa.smartpossupply.dto.kpi.KPICalculateTotalDTO;
import uz.uzkassa.smartpossupply.dto.kpi.KPIListDTO;
import uz.uzkassa.smartpossupply.dto.kpi.detail.KPIDetailDTO;
import uz.uzkassa.smartpossupply.enums.*;
import uz.uzkassa.smartpossupply.filters.KPIFilter;
import uz.uzkassa.smartpossupply.service.kpi.KPIService;

import java.util.List;

@RestController
@RequestMapping(ApiConstants.cabinetKPIRootApi)
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CabinetKPIResource {

    KPIService kpiService;

    @PostMapping
    public ResponseEntity<Long> create(@RequestBody @Valid KPIBaseDTO dto) {
        return ResponseEntity.ok(kpiService.create(dto));
    }

    @PutMapping(ApiConstants.id)
    public ResponseEntity<Long> update(@PathVariable Long id, @RequestBody @Valid KPIBaseDTO dto) {
        return ResponseEntity.ok(kpiService.update(id, dto));
    }

    @GetMapping(ApiConstants.id)
    public ResponseEntity<KPIDetailDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(kpiService.get(id));
    }

    @GetMapping
    public ResponseEntity<Page<KPIListDTO>> getList(@ParameterObject KPIFilter filter) {
        return ResponseEntity.ok(kpiService.getList(filter));
    }

    @GetMapping(ApiConstants.id + "/info")
    public ResponseEntity<KPIListDTO> getKPIInfo(@PathVariable Long id) {
        return ResponseEntity.ok(kpiService.getKPIInfo(id));
    }

    @DeleteMapping(ApiConstants.id)
    public ResponseEntity<Long> delete(@PathVariable Long id) {
        return ResponseEntity.ok(kpiService.delete(id));
    }

    @PatchMapping(ApiConstants.updateStatus)
    public ResponseEntity<Void> updateStatus(@PathVariable Long id, @RequestParam("status") KPIStatus status) {
        kpiService.updateStatus(id, status);
        return ResponseEntity.ok().build();
    }

    @GetMapping(ApiConstants.roles)
    public ResponseEntity<List<Role>> getRoles() {
        return ResponseEntity.ok(Role.getKPIRoles());
    }

    @GetMapping(ApiConstants.types)
    public ResponseEntity<List<KPIType>> getTypes() {
        return ResponseEntity.ok(KPIType.getKpiTypes());
    }

    @GetMapping("/bonus-types")
    public ResponseEntity<List<BonusType>> getBonusTypes() {
        return ResponseEntity.ok(BonusType.getBonusTypes());
    }

    @GetMapping("/sales-plan-types")
    public ResponseEntity<List<SalesPlanType>> getSalesPlanTypes() {
        return ResponseEntity.ok(SalesPlanType.getSalesPlanType());
    }

    @GetMapping("/product-add-types")
    public ResponseEntity<List<ProductAddType>> getProductAddType() {
        return ResponseEntity.ok(ProductAddType.getProductAddTypes());
    }

    @GetMapping("calculate")
    public ResponseEntity<Page<KPICalculateDTO>> getCalculating(@ParameterObject KPIFilter filter) {
        return ResponseEntity.ok(kpiService.calculate(filter));
    }

    @GetMapping("calculate-total")
    public ResponseEntity<KPICalculateTotalDTO> getCalculatingTotal(@ParameterObject KPIFilter filter) {
        return ResponseEntity.ok(kpiService.calculateTotal(filter));
    }
}
