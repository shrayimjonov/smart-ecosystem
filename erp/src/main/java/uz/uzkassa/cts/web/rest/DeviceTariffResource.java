package uz.uzkassa.cts.web.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.cts.dto.launcher.LauncherNotification;
import uz.uzkassa.cts.jobs.PaymentHistoryCreatorCronJob;
import uz.uzkassa.cts.service.DeviceTariffService;
import uz.uzkassa.cts.service.ServicePaymentHistoryCronService;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/deviceTariff")
@Tag(name = "Device Tariff")
@Slf4j
@RequiredArgsConstructor
public class DeviceTariffResource {

    private final DeviceTariffService service;
    private final PaymentHistoryCreatorCronJob paymentHistoryCreatorCronJob;
    private final ServicePaymentHistoryCronService servicePaymentHistoryService;

    @Operation(summary ="switch devices")
    @GetMapping("switchDevices/{date}")
    public ResponseEntity<Void> switchDevices(@PathVariable LocalDate date) {
        service.switchDevices(date);
        return ResponseEntity.ok().build();
    }

    @Operation(summary ="notify devices devices")
    @PostMapping("notify")
    public ResponseEntity<Void> notifyDevices(@Valid @RequestBody LauncherNotification notification) {
        log.debug("REST request notification: {}", notification);
        service.notifyDevices(notification);
        return ResponseEntity.ok().build();
    }

    @Operation(summary ="block and unblock devices by status in db")
    @GetMapping("blockUnblockDevices")
    public ResponseEntity<Void> blockUnblockDevice() {
        log.debug("REST request block device");
        service.blockUnblockDevices();
        return ResponseEntity.ok().build();
    }


    @Operation(summary ="create history manually")
    @GetMapping("create-history-manually")
    public ResponseEntity<Void> createHistoryManually() {
        log.debug("REST request create history manually");
        paymentHistoryCreatorCronJob.createHistory();
        return ResponseEntity.ok().build();
    }

    @Operation(summary ="device switch manually")
    @GetMapping("device-switch-manually")
    public ResponseEntity<Void> deviceSwitchManually() {
        log.debug("REST request device Switch Manually");
        paymentHistoryCreatorCronJob.switchDevice();
        return ResponseEntity.ok().build();
    }

    @Operation(summary ="device switch manually")
    @GetMapping("device-switch-manually/{tin}")
    public ResponseEntity<Void> deviceSwitchManually(@PathVariable String tin) {
        log.debug("REST request device Switch Manually by tin");
        servicePaymentHistoryService.deviceStatusSyncWithLauncher(tin);
        return ResponseEntity.ok().build();
    }

    @Operation(summary ="payment create history check")
    @GetMapping("/payment-history-recalculation")
    public ResponseEntity<Void> paymentCreateHistoryCheck(@RequestParam("tin") String tin,
                                                          @RequestParam("deviceSerialNumber") String deviceSerialNumber,
                                                          @RequestParam("stopDate") LocalDate stopDate) {
        log.debug("SmartVista detailing with Apelsin");
        servicePaymentHistoryService.recalculateHistory(tin, deviceSerialNumber, stopDate);
//        xfileCronJob.resendServiceOrderInvoiceSendByDateAndTin(tin, LocalDate.now());
        return ResponseEntity.ok().build();
    }
}
