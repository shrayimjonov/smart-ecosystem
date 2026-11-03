package uz.uzkassa.smartpossupply.web.rest.cabinet;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.smartpossupply.constants.ApiConstants;
import uz.uzkassa.smartpossupply.dto.base.EnumDTO;
import uz.uzkassa.smartpossupply.dto.soliq.TaxCompanyDTO;
import uz.uzkassa.smartpossupply.dto.vehicle.VehicleDTO;
import uz.uzkassa.smartpossupply.dto.vehicle.VehicleDetailDTO;
import uz.uzkassa.smartpossupply.dto.vehicle.VehicleListDTO;
import uz.uzkassa.smartpossupply.dto.vehicle.VehicleLookupDTO;
import uz.uzkassa.smartpossupply.dto.waybill.WaybillDriverTransportDTO;
import uz.uzkassa.smartpossupply.enums.CommonStatus;
import uz.uzkassa.smartpossupply.enums.TrailerType;
import uz.uzkassa.smartpossupply.filters.BaseFilter;
import uz.uzkassa.smartpossupply.service.VehicleService;

import java.util.EnumSet;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 23.01.2025
 */
@RestController
@RequestMapping(ApiConstants.cabinetVehicleRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CabinetVehicleResource {

    VehicleService vehicleService;

    @PostMapping
    public ResponseEntity<Long> create(@RequestBody VehicleDTO vehicleDTO) {
        return ResponseEntity.ok(vehicleService.create(vehicleDTO));
    }

    @PostMapping(ApiConstants.batch)
    public ResponseEntity<Long> batchCreate(@RequestBody List<VehicleDTO> list) {
        vehicleService.batchCreate(list);
        return ResponseEntity.ok().build();
    }

    @GetMapping(ApiConstants.id)
    public ResponseEntity<VehicleDetailDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(vehicleService.get(id));
    }

    @PutMapping(ApiConstants.id)
    public ResponseEntity<Long> update(@PathVariable Long id, @RequestBody VehicleDTO warehouseDTO) {
        return ResponseEntity.ok(vehicleService.update(id, warehouseDTO));
    }

    @GetMapping(ApiConstants.statuses)
    public ResponseEntity<EnumSet<CommonStatus>> getStatuses() {
        return ResponseEntity.ok(EnumSet.allOf(CommonStatus.class));
    }

    @PatchMapping(ApiConstants.updateStatus)
    public ResponseEntity<Void> updateStatus(@PathVariable Long id, @RequestParam("status") CommonStatus status) {
        vehicleService.updateStatus(id, status);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<Page<VehicleListDTO>> getList(@ParameterObject BaseFilter filter) {
        return ResponseEntity.ok(vehicleService.getList(filter));
    }

    @DeleteMapping(ApiConstants.id)
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        vehicleService.delete(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping(ApiConstants.lookUp)
    public ResponseEntity<List<VehicleLookupDTO>> lookUp(@ParameterObject BaseFilter filter) {
        filter.setStatus(CommonStatus.ACTIVE.getCode());
        return ResponseEntity.ok(vehicleService.lookUp(filter));
    }

    @GetMapping("/trailer-types")
    public ResponseEntity<List<EnumDTO>> getTrailerTypes() {
        return ResponseEntity.ok(TrailerType.getAll());
    }

    @GetMapping("/company/{tin}")
    public ResponseEntity<WaybillDriverTransportDTO[]> getDriverTransports(@PathVariable String tin) {
        return ResponseEntity.ok(vehicleService.getTransports(tin));
    }

    @GetMapping("/driver-info")
    public ResponseEntity<TaxCompanyDTO> getDriverInfo(@RequestParam String tin) {
        return ResponseEntity.ok(vehicleService.getDriverInfo(tin));
    }
}
