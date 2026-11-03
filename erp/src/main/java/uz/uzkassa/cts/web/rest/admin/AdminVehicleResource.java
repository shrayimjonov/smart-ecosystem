package uz.uzkassa.cts.web.rest.admin;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.cts.constants.ApiConstants;
import uz.uzkassa.cts.dto.vehicle.VehicleDTO;
import uz.uzkassa.cts.dto.vehicle.VehicleDetailDTO;
import uz.uzkassa.cts.dto.vehicle.VehicleListDTO;
import uz.uzkassa.cts.enums.CommonStatus;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.service.VehicleService;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.adminVehicleRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AdminVehicleResource {

    VehicleService vehicleService;

    @PostMapping
    public ResponseEntity<Long> create(@RequestBody VehicleDTO vehicleDTO) {
        return ResponseEntity.ok(vehicleService.create(vehicleDTO));
    }

    @PutMapping(ApiConstants.id)
    public ResponseEntity<Long> update(@PathVariable Long id, @RequestBody VehicleDTO vehicleDTO) {
        return ResponseEntity.ok(vehicleService.update(id, vehicleDTO));
    }

    @GetMapping(ApiConstants.id)
    public ResponseEntity<VehicleDetailDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(vehicleService.get(id));
    }

    @GetMapping
    public ResponseEntity<Page<VehicleListDTO>> getList(@ParameterObject BaseFilter filter) {
        return ResponseEntity.ok(vehicleService.getList(filter));
    }

    @PatchMapping(ApiConstants.updateStatus)
    public ResponseEntity<Void> updateStatus(@PathVariable Long id, CommonStatus status) {
        vehicleService.updateStatus(id, status);
        return ResponseEntity.ok().build();
    }
}
