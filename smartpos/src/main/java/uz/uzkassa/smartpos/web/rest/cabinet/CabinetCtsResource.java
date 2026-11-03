package uz.uzkassa.smartpos.web.rest.cabinet;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.smartpos.constants.TokenType;
import uz.uzkassa.smartpos.integration.cto.*;
import uz.uzkassa.smartpos.integration.enums.LeadPaymentType;
import uz.uzkassa.smartpos.service.TokenStoreService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/cabinet/cts")
public class CabinetCtsResource {
    private final CtoClient ctoClient;
    private final TokenStoreService tokenStoreService;

    @GetMapping("/acts/template")
    public String getActTemplate() {
        return ctoClient.getActTemplate(tokenStoreService.getToken(TokenType.CTS));
    }

    @GetMapping("/acts/detail")
    public ModelPriceHistoryDetailDTO getActDetail() {
        return ctoClient.getModelPriceHistoryDetail(tokenStoreService.getToken(TokenType.CTS));
    }

    @GetMapping("/acts")
    public Page<ActOfTransmissionSmartposDTO> getAllActs(@RequestParam String tin) {
        return ctoClient.getPrimeList(tokenStoreService.getToken(TokenType.CTS), tin);
    }

    @GetMapping("/acts/{id}/schedule")
    public List<ActPaymentScheduleSmartposDTO> getScheduleList(@PathVariable Long id) {
        return ctoClient.getScheduleList(tokenStoreService.getToken(TokenType.CTS), id);
    }

    @PostMapping("/leads")
    public Long createFromSmartposTrade(@RequestBody @Valid LeadRequestDto dto) {
        return ctoClient.createFromSmartposTrade(tokenStoreService.getToken(TokenType.CTS), dto);
    }

    @GetMapping("/leads/{tin}")
    public LeadResponseDTO getSmartposList(@PathVariable String tin) {
        return ctoClient.getSmartposList(tokenStoreService.getToken(TokenType.CTS), tin);
    }

    @GetMapping("/leads/types")
    public LeadPaymentType[] getAllLeadPayments() {
        return LeadPaymentType.values();
    }
}
