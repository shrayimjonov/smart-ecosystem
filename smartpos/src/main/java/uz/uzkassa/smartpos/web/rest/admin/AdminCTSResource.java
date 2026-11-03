package uz.uzkassa.smartpos.web.rest.admin;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.smartpos.dto.BankTariffHistoryDTO;
import uz.uzkassa.smartpos.service.BankTariffHistoryService;

@RequestMapping("/api/v1/admin/cts")
@RestController
@RequiredArgsConstructor
public class AdminCTSResource {
    private final BankTariffHistoryService service;

    @PostMapping("/bank-tariff")
    public void saveBankTariff(@RequestBody @Valid BankTariffHistoryDTO dto) {
        service.saveBankTariff(dto);
    }
}
