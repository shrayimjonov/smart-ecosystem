package uz.uzkassa.smartpos.web.rest.integration;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.smartpos.dto.integration.erp.TerminalCashierErpCreateDto;
import uz.uzkassa.smartpos.dto.receipt.LastReceiptDto;
import uz.uzkassa.smartpos.enums.PaymentType;
import uz.uzkassa.smartpos.integration.cto.CtoResultDTO;
import uz.uzkassa.smartpos.service.DeviceService;
import uz.uzkassa.smartpos.service.ReceiptService;
import uz.uzkassa.smartpos.service.UserService;

@RestController
@RequestMapping("/api/v1/integration/erp")
@RequiredArgsConstructor
public class ErpResource {
    private final UserService userService;
    private final DeviceService deviceService;
    private final ReceiptService receiptService;

    @PostMapping("/terminal/cashier")
    public void terminalCashier(TerminalCashierErpCreateDto erpTerminalCashierDto) {
        userService.createTerminalCashier(erpTerminalCashierDto);
    }

    @DeleteMapping("/devices/{serialNumber}")
    public void removeDeviceFromBranch(@PathVariable String serialNumber) {
        deviceService.removeDeviceFromBranch(serialNumber);
    }

    @PostMapping("/devices/sync")
    public void syncDevice(@RequestBody @Valid CtoResultDTO ctoResultDTO) {
        deviceService.syncDevice(ctoResultDTO);
    }

    @GetMapping("/receipt/last")
    public LastReceiptDto lastReceipt(@RequestParam String serialNumber, @RequestParam String tin, @RequestParam PaymentType paymentType) {
        return receiptService.getLastReceipt(serialNumber, tin, paymentType);
    }

}
