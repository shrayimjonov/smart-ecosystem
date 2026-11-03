package uz.uzkassa.smartpos.web.rest.integration;

import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.smartpos.dto.BranchSelectItem;
import uz.uzkassa.smartpos.dto.device.DeviceDto;
import uz.uzkassa.smartpos.dto.terminal.sales.TransactionDto;
import uz.uzkassa.smartpos.filter.BranchFilter;
import uz.uzkassa.smartpos.filter.DeviceFilter;
import uz.uzkassa.smartpos.filter.TransactionFilter;
import uz.uzkassa.smartpos.service.BranchService;
import uz.uzkassa.smartpos.service.DeviceService;
import uz.uzkassa.smartpos.service.TransactionService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/integration/bank")
@RequiredArgsConstructor
public class BankResource {
    private final BranchService branchService;
    private final DeviceService deviceService;
    private final TransactionService transactionService;

    @GetMapping("/branches")
    public List<BranchSelectItem> branches(@ParameterObject BranchFilter filter) {
        return branchService.search(filter);
    }

    @GetMapping("/devices")
    public Page<DeviceDto> devices(@ParameterObject DeviceFilter filter) {
        return deviceService.findAllByFilter(filter);
    }

    @GetMapping("/transactions")
    public Page<TransactionDto> transactions(@ParameterObject TransactionFilter filter) {
        return transactionService.findAllByFilter(filter);
    }
}
