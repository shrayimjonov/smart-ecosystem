package uz.uzkassa.smartpos.web.rest.cabinet;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.smartpos.dto.base.CommonDTO;
import uz.uzkassa.smartpos.dto.warehouse.ShipmentCreateDto;
import uz.uzkassa.smartpos.dto.warehouse.ShipmentDto;
import uz.uzkassa.smartpos.dto.warehouse.ShipmentListDto;
import uz.uzkassa.smartpos.enums.ShipmentStatus;
import uz.uzkassa.smartpos.filter.ShipmentFilter;
import uz.uzkassa.smartpos.service.ShipmentService;

import java.util.EnumSet;
import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/cabinet/shipment")
public class CabinetShipmentResource {
    private final ShipmentService shipmentService;

    @PostMapping
    public ShipmentDto create(@RequestBody @Valid ShipmentCreateDto dto) {
        return shipmentService.create(dto);
    }

    @PutMapping("/{id}")
    public ShipmentDto update(@PathVariable Long id, @RequestBody @Valid ShipmentCreateDto dto) {
        return shipmentService.update(id, dto);
    }

    @PatchMapping("/{id}")
    public ShipmentDto updateStatus(@PathVariable Long id, @RequestBody @Valid ShipmentStatus status) {
        return shipmentService.updateStatus(id, status);
    }

    @GetMapping("/{id}")
    public ShipmentDto findById(@PathVariable Long id) {
        return shipmentService.findById(id);
    }

    @GetMapping
    public Page<ShipmentListDto> findAllByFilter(@ParameterObject ShipmentFilter filter) {
        return shipmentService.findAllByFilter(filter);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        shipmentService.delete(id);
    }

    @GetMapping("/statuses")
    public EnumSet<ShipmentStatus> getStatuses() {
        return EnumSet.allOf(ShipmentStatus.class);
    }

    @PutMapping("/convert-to-invoice")
    public Long convertToInvoice(@RequestBody @NotEmpty List<Long> ids) {
        return shipmentService.convertToInvoice(ids);
    }

    @GetMapping("/lookup")
    public List<CommonDTO> lookup(@ParameterObject ShipmentFilter filter) {
        return shipmentService.lookup(filter);
    }

}
