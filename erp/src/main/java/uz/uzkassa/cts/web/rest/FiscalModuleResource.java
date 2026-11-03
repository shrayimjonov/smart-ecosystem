package uz.uzkassa.cts.web.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.cts.dto.device.DeviceHistoryDTO;
import uz.uzkassa.cts.dto.device.RegistryListDTO;
import uz.uzkassa.cts.dto.device.SerialNumberDTO;
import uz.uzkassa.cts.dto.fm.FiscalModuleListDTO;
import uz.uzkassa.cts.dto.fm.FiscalModuleRegisterDTO;
import uz.uzkassa.cts.dto.sync.DeviceTotalDTO;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.filter.CompanyDeviceListFilter;
import uz.uzkassa.cts.filter.FiscalModuleLookUpFilter;
import uz.uzkassa.cts.service.FiscalModuleService;
import uz.uzkassa.cts.service.InactiveFiscalModuleService;
import uz.uzkassa.cts.service.WarehouseHistoryService;

import java.util.List;

/**
 * REST controller for managing {@link uz.uzkassa.cts.domain.FiscalModule}.
 */
@RestController
@RequestMapping("/api/fiscal/modules")
@Tag(name = "Fiscal Modules")
@Slf4j
@Deprecated
public class FiscalModuleResource {

    private final FiscalModuleService fiscalModuleService;
    private final WarehouseHistoryService warehouseHistoryService;

    public FiscalModuleResource(FiscalModuleService fiscalModuleService, WarehouseHistoryService warehouseHistoryService, InactiveFiscalModuleService inactiveFiscalModuleService) {
        this.fiscalModuleService = fiscalModuleService;
        this.warehouseHistoryService = warehouseHistoryService;
    }

    @Operation(summary ="Add")
    @PostMapping
    public ResponseEntity<FiscalModuleRegisterDTO> add(@Valid @RequestBody FiscalModuleRegisterDTO fiscalModuleRegisterDTO) {
        log.debug("REST request to register new SerialNumberDTO: {}", fiscalModuleRegisterDTO);
        return ResponseEntity.ok(fiscalModuleService.add(fiscalModuleRegisterDTO));
    }

    @Operation(summary ="Batch Add")
    @PostMapping("/batch")
    public ResponseEntity<Void> batchAdd(@RequestBody List<SerialNumberDTO> fiscalModules) {
        log.debug("REST request to batch add devices");
        fiscalModuleService.batchAdd(fiscalModules);
        return ResponseEntity.ok().build();
    }

    @Operation(summary ="List")
    @GetMapping
    public ResponseEntity<Page<FiscalModuleListDTO>> list(BaseFilter filter) {
        log.debug("REST request to get all FiscalModuleListDTO: {}", filter);
        return ResponseEntity.ok(fiscalModuleService.list(filter));
    }

    @Operation(summary ="List All")
    @GetMapping("/register")
    public ResponseEntity<Page<RegistryListDTO>> listRegister(CompanyDeviceListFilter filter) {
        log.debug("REST request to get all RegistryListDTO: {}", filter);
        return ResponseEntity.ok(fiscalModuleService.listRegister(filter));
    }

    @Operation(summary ="Look Up")
    @GetMapping("/lookup")
    public ResponseEntity<Page<SerialNumberDTO>> lookUp(FiscalModuleLookUpFilter filter) {
        log.debug("REST request to get lookup devices: {}", filter);
        return ResponseEntity.ok(fiscalModuleService.lookUp(filter));
    }

    @Operation(summary ="Import")
    @PostMapping("/import")
    public ResponseEntity<DeviceTotalDTO> importFiscalModule(@RequestParam("file") MultipartFile file) {
        log.debug("REST request to upload file");
        return ResponseEntity.ok(new DeviceTotalDTO(fiscalModuleService.importFiscalModule(file)));
    }


    @Operation(summary ="Histories")
    @GetMapping("/histories")
    public ResponseEntity<Page<DeviceHistoryDTO>> getHistories(BaseFilter filter) {
        log.debug("REST request to get fiscal module histories: {}", filter);
        return ResponseEntity.ok(warehouseHistoryService.getHistoryList(filter, true));
    }

    @Operation(summary ="Sync")
    @GetMapping("/sync")
    public ResponseEntity<Void> sync() {
        log.debug("REST request to sync inactive fiscal modules");
        //inactiveFiscalModuleService.sync();
        return ResponseEntity.ok().build();
    }
}
