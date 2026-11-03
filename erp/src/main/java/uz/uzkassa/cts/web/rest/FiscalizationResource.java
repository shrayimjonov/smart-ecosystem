package uz.uzkassa.cts.web.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.cts.dto.ofdapplication.OfdApplicationMergeDTO;
import uz.uzkassa.cts.dto.applicationfile.ApplicationTemplateDTO;
import uz.uzkassa.cts.dto.fm.*;
import uz.uzkassa.cts.dto.smartpos.SmartposFiscalizationListDTO;
import uz.uzkassa.cts.dto.sms.ReceiptSmsRequestDTO;
import uz.uzkassa.cts.enums.DeviceStatus;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.service.FiscalizationService;
import uz.uzkassa.cts.service.SmartposService;

import java.util.EnumSet;

/**
 * REST controller for managing {@link uz.uzkassa.cts.domain.Fiscalization}.
 */
@RestController
@RequestMapping("/api/fiscalizations")
@Tag(name = "Fiscalization")
@Slf4j
public class FiscalizationResource {

    private final FiscalizationService fiscalizationService;
    private final SmartposService smartposService;

    public FiscalizationResource(FiscalizationService fiscalizationService, SmartposService smartposService) {
        this.fiscalizationService = fiscalizationService;
        this.smartposService = smartposService;
    }

    @Operation(summary ="List")
    @GetMapping
    public ResponseEntity<Page<SmartposFiscalizationListDTO>> list(BaseFilter filter) {
        log.debug("REST request to get fiscalization list: {}", filter);
        return ResponseEntity.ok(fiscalizationService.list(filter));
    }

    @Operation(summary ="Fiscalization")
    @PostMapping
    public ResponseEntity<Long> fiscalization(@Valid @RequestBody FiscalizationDTO fiscalizationDTO) {
        log.debug("REST request to fiscalization new FiscalizationDTO: {}", fiscalizationDTO);
        return ResponseEntity.ok(fiscalizationService.fiscalization(fiscalizationDTO));
    }

    @Operation(summary ="Swap Device")
    @PutMapping("/swap/device")
    public ResponseEntity<Void> swapDevice(@Valid @RequestBody SwapDeviceDTO swapDeviceDTO) {
        log.debug("REST request to swap new device SerialNumberDTO: {}", swapDeviceDTO);
        fiscalizationService.swapDevice(swapDeviceDTO);
        return ResponseEntity.ok().build();
    }

    @Operation(summary ="Mark As Paid")
    @PutMapping("/mark/paid/{id}")
    public ResponseEntity<Void> maskAsPaid(@PathVariable(value = "id") Long id) {
        log.debug("REST request to mark fm as paid: {}", id);
        fiscalizationService.maskAsPaid(id);
        return ResponseEntity.ok().build();
    }

    @Operation(summary ="Merge")
    @PutMapping("/merge")
    public ResponseEntity<Void> mergeWithNIC(@Valid @RequestBody OfdApplicationMergeDTO ofdApplicationMergeDTO) {
        log.debug("REST request to merge application with NIC mergeApplicationDTO {}", ofdApplicationMergeDTO);
        fiscalizationService.mergeWithNIC(ofdApplicationMergeDTO);
        return ResponseEntity.ok().build();
    }

    @Operation(summary ="Swap Fiscal Module")
    @PutMapping("/swap/fiscal/module")
    public ResponseEntity<Void> swapFiscalModule(@Valid @RequestBody FiscalSwapFiscalModuleDTO fiscalSwapFiscalModuleDTO) {
        log.debug("REST request to swap new fiscal module SwapFiscalModuleDTO: {}", fiscalSwapFiscalModuleDTO);
        fiscalizationService.swapFiscalModule(fiscalSwapFiscalModuleDTO);
        return ResponseEntity.ok().build();
    }

    @Operation(summary ="Get Detail For Terminal")
    @PostMapping("/detail")
    public ResponseEntity<FiscalizationDetailDTO> getDetail(@RequestBody FiscalCheckDTO fiscalCheckDTO) {
        log.debug("REST request to get fiscalization detail new FiscalizationDTO: {}", fiscalCheckDTO);
        return ResponseEntity.ok(fiscalizationService.getDetail(fiscalCheckDTO));
    }

    @Operation(summary ="Check Hash")
    @PostMapping("/check/device")
    public ResponseEntity<FiscalizationCheckDeviceDTO> checkDeviceFiscalModule(@RequestBody FiscalCheckDTO fiscalCheckDTO) {
        return ResponseEntity.ok(fiscalizationService.fiscalizationCheck(fiscalCheckDTO));
    }

    @Operation(summary ="Fiscal Send SMS")
    @PostMapping("/sms/send")
    public ResponseEntity<Void> fiscalSendSms(@RequestBody ReceiptSmsRequestDTO receiptSmsRequestDTO) {
        smartposService.fiscalSendSms(receiptSmsRequestDTO);
        return ResponseEntity.ok().build();
    }

    @Operation(summary ="Clear Cache")
    @PostMapping("/clear/cache")
    public ResponseEntity<Void> clearCache(@RequestBody FiscalClearCacheDTO fiscalClearCacheDTO) {
        fiscalizationService.clearFiscalCache(fiscalClearCacheDTO);
        return ResponseEntity.ok().build();
    }

    @Operation(summary ="Clear map by hash")
    @PostMapping("/clear-map/{hash}")
    public ResponseEntity<Void> clearMapByHash(@PathVariable String hash) {
        fiscalizationService.clearMapByHash(hash);
        return ResponseEntity.ok().build();
    }

    @Operation(summary ="Update Status")
    @PutMapping("/{id}/status")
    public ResponseEntity<Void> updateStatus(@PathVariable Long id, @RequestParam("status") String status) {
        fiscalizationService.updateStatus(id, DeviceStatus.valueOf(status));
        return ResponseEntity.ok().build();
    }

    @Operation(summary ="Statuses")
    @GetMapping("/statuses")
    public ResponseEntity<EnumSet<DeviceStatus>> getStatuses() {
        return ResponseEntity.ok(EnumSet.of(DeviceStatus.FISCAL, DeviceStatus.FISCAL_ACTIVE));
    }

    @Operation(summary ="Sync With Smartpos")
    @PostMapping("/sync/smartpos")
    public ResponseEntity<Void> syncWithSmartpos(@RequestBody FiscalizationSyncDTO fiscalizationSyncDTO) {
        fiscalizationService.syncWithSmartpos(fiscalizationSyncDTO);
        return ResponseEntity.ok().build();
    }

    @Operation(summary ="Sync All With Smartpos")
    @PostMapping("/sync/smartpos/all")
    public ResponseEntity<Void> syncAllWithSmartpos() {
        fiscalizationService.syncAllWithSmartpos();
        return ResponseEntity.ok().build();
    }

    @Operation(summary ="Download Application Template")
    @PostMapping("/download/application/template")
    public ResponseEntity<String> downloadApplicationTemplate(@RequestBody ApplicationTemplateDTO applicationTemplateDTO) {
        log.debug("REST request to get application template: {}", applicationTemplateDTO);
        return ResponseEntity.ok(fiscalizationService.downloadApplicationTemplate(applicationTemplateDTO));
    }
}
