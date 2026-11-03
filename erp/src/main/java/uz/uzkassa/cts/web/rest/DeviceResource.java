package uz.uzkassa.cts.web.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.cts.dto.device.*;
import uz.uzkassa.cts.dto.sync.DeviceTotalDTO;
import uz.uzkassa.cts.dto.sync.SyncDeviceListDTO;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.filter.CompanyDeviceListFilter;
import uz.uzkassa.cts.filter.DeviceLookUpFilter;
import uz.uzkassa.cts.handlers.export.excel.RegistryDeviceExportExcelHandler;
import uz.uzkassa.cts.service.DeviceService;
import uz.uzkassa.cts.service.WarehouseHistoryService;

import java.util.List;

/**
 * REST controller for managing {@link uz.uzkassa.cts.domain}.
 */
@RestController
@RequestMapping("/api/devices")
@Tag(name = "Device")
@Slf4j
@Deprecated
public class DeviceResource {

    private final DeviceService deviceService;
    private final WarehouseHistoryService warehouseHistoryService;
    private final RegistryDeviceExportExcelHandler registryDeviceExcelHandler;


    public DeviceResource(DeviceService deviceService, WarehouseHistoryService warehouseHistoryService, RegistryDeviceExportExcelHandler registryDeviceExcelHandler) {
        this.deviceService = deviceService;
        this.warehouseHistoryService = warehouseHistoryService;
        this.registryDeviceExcelHandler = registryDeviceExcelHandler;
    }

    /*@Operation(summary ="Add")
    @PostMapping
    public ResponseEntity<DeviceDetailDTO> create(@Valid @RequestBody DeviceDTO deviceDTO) {
        log.debug("REST request to create new DeviceDTO: {}", deviceDTO);
        return ResponseEntity.ok(deviceService.create(deviceDTO));
    }*/

    /*@Operation(summary = "Update")
    @PutMapping
    public ResponseEntity<DeviceDetailDTO> update(@Valid @RequestBody DeviceDTO deviceDTO) {
        log.debug("REST request to update DeviceDTO : {}", deviceDTO);
        return ResponseEntity.ok(deviceService.update(deviceDTO));
    }*/

    @Operation(summary = "Update Status")
    @PutMapping("/{id}/{status}")
    public ResponseEntity<Void> updateStatus(@RequestParam("file") MultipartFile file,
                                             @PathVariable(value = "id") Long id,
                                             @PathVariable(value = "status") String status) {
        log.debug("REST request to update device status: {},{}", id, status);
        deviceService.updateStatus(id, status, file);
        return ResponseEntity.ok().build();
    }

    /*@Operation(summary ="Get")
    @GetMapping("/{id}")
    public ResponseEntity<DeviceDetailDTO> getById(@PathVariable Long id) {
        log.debug("REST request to get DeviceDetailDTO: {}", id);
        return ResponseEntity.ok(deviceService.get(id));
    }*/

    @Operation(summary = "LookUp")
    @GetMapping("/lookup")
    public ResponseEntity<Page<SerialNumberDTO>> lookUp(DeviceLookUpFilter filter) {
        log.debug("REST request to get lookup devices: {}", filter);
        return ResponseEntity.ok(deviceService.lookUp(filter));
    }

    @Operation(summary = "Batch Add")
    @PostMapping("/batch")
    public ResponseEntity<Void> batchAdd(@RequestBody List<DeviceDTO> deviceDTOList) {
        log.debug("REST request to batch add devices");
        deviceService.batchAdd(deviceDTOList);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "List")
    @GetMapping
    public ResponseEntity<Page<DeviceDetailDTO>> getAll(BaseFilter filter) {
        log.debug("REST request to get all DeviceListDTO : {}", filter);
        return ResponseEntity.ok(deviceService.list(filter));
    }

    @Operation(summary = "List All")
    @GetMapping("/register")
    public ResponseEntity<Page<RegistryListDTO>> listRegister(CompanyDeviceListFilter filter) {
        log.debug("REST request to get all DeviceListDTO : {}", filter);
        return ResponseEntity.ok(deviceService.listRegister(filter));
    }

    @Operation(summary = "Export to CSV")
    @GetMapping("/register/export-csv")
    public ResponseEntity<?> listRegisterExportToCSV(CompanyDeviceListFilter filter) {
        return ResponseEntity
                .ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, String.format("attachment; filename=%s", "Devices.csv"))
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(registryDeviceExcelHandler.generate(filter));
    }

    @Operation(summary = "Delete")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.debug("REST request to delete device: {}", id);
        deviceService.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Import")
    @PostMapping("/import")
    public ResponseEntity<DeviceTotalDTO> importDevice(@RequestParam("file") MultipartFile file) {
        log.debug("REST request to upload file");
        return ResponseEntity.ok(new DeviceTotalDTO(deviceService.importDevices(file)));
    }

    @Operation(summary = "Free Device Total")
    @GetMapping("/total/{modelId}")
    public ResponseEntity<DeviceTotalDTO> getDeviceTotalByModel(@PathVariable("modelId") Long modelId) {
        log.debug("REST request to get devices by model {}", modelId);
        return ResponseEntity.ok(new DeviceTotalDTO(deviceService.getDeviceTotalByModel(modelId)));
    }

    @Operation(summary = "Histories")
    @GetMapping("/histories")
    public ResponseEntity<Page<DeviceHistoryDTO>> getHistories(BaseFilter filter) {
        log.debug("REST request to get device histories: {}", filter);
        return ResponseEntity.ok(warehouseHistoryService.getHistoryList(filter, false));
    }

    @Operation(summary = "Get Count List to Sync")
    @GetMapping("/sync/count")
    public ResponseEntity<Long> getListCountToSync() {
        log.debug("REST request to get all Device count");
        return ResponseEntity.ok(deviceService.getListCountToSync());
    }

    @Operation(summary = "Get List to Sync")
    @GetMapping("/sync/list")
    public ResponseEntity<Page<SyncDeviceListDTO>> getListToSync(BaseFilter filter) {
        log.debug("REST request to get all Devices : {}", filter);
        return ResponseEntity.ok(deviceService.getListToSync(filter));
    }
}
