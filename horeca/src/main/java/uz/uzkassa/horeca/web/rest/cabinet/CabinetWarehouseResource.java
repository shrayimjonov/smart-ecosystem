package uz.uzkassa.horeca.web.rest.cabinet;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.horeca.constants.ApiConstants;
import uz.uzkassa.horeca.dto.base.CommonDTO;
import uz.uzkassa.horeca.dto.warehouse.WarehouseDTO;
import uz.uzkassa.horeca.dto.warehouse.WarehouseDetailDTO;
import uz.uzkassa.horeca.dto.warehouse.WarehouseListDTO;
import uz.uzkassa.horeca.enums.CommonStatus;
import uz.uzkassa.horeca.filters.BaseFilter;
import uz.uzkassa.horeca.interfaces.CrudRestAdvanced;
import uz.uzkassa.horeca.service.WarehouseService;

import java.time.LocalDateTime;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.cabinetWarehouseRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CabinetWarehouseResource
        implements CrudRestAdvanced<Long, WarehouseDTO, WarehouseDetailDTO, WarehouseListDTO, BaseFilter, CommonStatus, CommonDTO> {

    WarehouseService warehouseService;

    @PostMapping
    @Override
    public ResponseEntity<Long> create(@RequestBody WarehouseDTO warehouseDTO) {
        return ResponseEntity.ok(warehouseService.create(warehouseDTO));
    }

    @PutMapping(ApiConstants.id)
    @Override
    public ResponseEntity<Long> update(@PathVariable Long id, @RequestBody WarehouseDTO warehouseDTO) {
        return ResponseEntity.ok(warehouseService.update(id, warehouseDTO));
    }

    @GetMapping(ApiConstants.id)
    @Override
    public ResponseEntity<WarehouseDetailDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(warehouseService.get(id));
    }

    @GetMapping
    @Override
    public ResponseEntity<Page<WarehouseListDTO>> getList(@ParameterObject BaseFilter filter) {
        return ResponseEntity.ok(warehouseService.getList(filter));
    }

    @DeleteMapping(ApiConstants.id)
    @Override
    public ResponseEntity<Void> delete(@PathVariable Long id, @RequestParam(required = false) LocalDateTime lastModifiedDate) {
        warehouseService.delete(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping(ApiConstants.lookUp)
    @Override
    public ResponseEntity<List<CommonDTO>> lookUp(@ParameterObject BaseFilter filter) {
        return ResponseEntity.ok(warehouseService.lookUp(filter));
    }

    @PatchMapping(ApiConstants.updateStatus)
    @Override
    public ResponseEntity<Void> updateStatus(@PathVariable Long id, @RequestParam CommonStatus status) {
        warehouseService.updateStatus(id, status);
        return ResponseEntity.ok().build();
    }
}
