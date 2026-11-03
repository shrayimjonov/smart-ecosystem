package uz.uzkassa.smartpos.integration.cto;

import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.smartpos.config.FeignPropagateBadRequestsConfiguration;
import uz.uzkassa.smartpos.web.rest.vm.TokenDTO;

import java.util.List;
import java.util.Objects;

@Component
@FeignClient(name = "CTO", url = "${application.cto.host}/api", configuration = FeignPropagateBadRequestsConfiguration.class)
public interface CtoClient {
    @PostMapping("/login")
    ResponseEntity<TokenDTO> login(@RequestBody Credential credential);

    @GetMapping("/smartpos/fiscalization/detail")
    CtoResultDTO getTerminal(@RequestHeader(value = "Authorization") String token,
                             @RequestParam("deviceSerial") String deviceSerial);

    @PostMapping("/fiscalizations/check/device")
    Object checkDeviceFiscalization(@RequestHeader(value = "Authorization") String token, @RequestBody CtoFiscalCheckDTO request);


    @GetMapping("/contract-for-leasing/offer-template")
    String getActTemplate(@RequestHeader(value = "Authorization") String token);

    @GetMapping("/modelPriceHistory/detail")
    ModelPriceHistoryDetailDTO getModelPriceHistoryDetail(@RequestHeader(value = "Authorization") String token);

    @PostMapping("/admin/v1/leads/smartpos-trade")
    Long createFromSmartposTrade(@RequestHeader("Authorization") String token, @RequestBody @Valid LeadRequestDto dto);

    @GetMapping("/admin/v1/leads/smartpos-list/{tin}")
    LeadResponseDTO getSmartposList(@RequestHeader("Authorization") String token, @PathVariable String tin);

    @GetMapping("/act-of-transmission/prime-list")
    Page<ActOfTransmissionSmartposDTO> getPrimeList(@RequestHeader("Authorization") String token, @RequestParam String tin);

    @GetMapping("/act-of-transmission/{id}/schedule-list")
    List<ActPaymentScheduleSmartposDTO> getScheduleList(@RequestHeader("Authorization") String token, @PathVariable Long id);

    @GetMapping("/admin/v1/customer-devices/{tin}/active-devices-by-tin")
    ResponseEntity<List<ActiveDeviceResponseDto>> getActiveDevices(@RequestHeader(value = "Authorization") String token, @PathVariable(value = "tin") String tin);

    default String getToken(String username, String password) {
        ResponseEntity<TokenDTO> responseEntity = login(new Credential(username, password));
        return Objects.requireNonNull(responseEntity.getBody()).getAccessToken();
    }
}
