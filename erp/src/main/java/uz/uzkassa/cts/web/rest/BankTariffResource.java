package uz.uzkassa.cts.web.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.cts.constants.ApiConstants;
import uz.uzkassa.cts.dto.banktariff.BankTariffDTO;
import uz.uzkassa.cts.dto.banktariff.BankTariffDetailDTO;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.enums.CommonStatus;
import uz.uzkassa.cts.filter.BankTariffFilter;
import uz.uzkassa.cts.service.BankTariffService;

import java.util.List;

@RestController
@RequestMapping(value = "/api/bank-tariff")
@Tag(name = "Bank tariff")
@Slf4j
@RequiredArgsConstructor
public class BankTariffResource {

    private final BankTariffService bankTariffService;

    @Operation(summary = "Create")
    @PostMapping
    public ResponseEntity<Long> create(@RequestBody BankTariffDTO dto) {
        return ResponseEntity.ok(bankTariffService.create(dto));
    }

    @Operation(summary = "Update")
    @PutMapping("/{id}")
    public ResponseEntity<Long> update(@PathVariable Long id, @RequestBody BankTariffDTO dto) {
        return ResponseEntity.ok(bankTariffService.update(id, dto));
    }

    @Operation(summary = "Get")
    @GetMapping("/{id}")
    public ResponseEntity<BankTariffDetailDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(bankTariffService.get(id));
    }

    @Operation(summary = "List")
    @GetMapping
    public ResponseEntity<Page<BankTariffDetailDTO>> list(BankTariffFilter filter) {
        return ResponseEntity.ok(bankTariffService.list(filter));
    }

    @GetMapping(ApiConstants.lookUp)
    public ResponseEntity<List<CommonDTO>> lookUp(@ParameterObject BankTariffFilter filter) {
        return ResponseEntity.ok(bankTariffService.lookUp(filter));
    }

    @PatchMapping(ApiConstants.updateStatus)
    public ResponseEntity<Void> updateStatus(@PathVariable Long id, @RequestParam("status") CommonStatus status) {
        bankTariffService.updateStatus(id, status);
        return ResponseEntity.ok().build();
    }
}
