package uz.uzkassa.smartpossupply.web.rest.cabinet;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.smartpossupply.constants.ApiConstants;
import uz.uzkassa.smartpossupply.dto.waybill.WaybillWebhookDTO;
import uz.uzkassa.smartpossupply.service.WaybillWebhookService;

import static lombok.AccessLevel.PRIVATE;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 17.07.2024
 */
@RestController
@RequestMapping(ApiConstants.cabinetWaybillRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CabinetWaybillWebhookResource {

    WaybillWebhookService waybillWebhookService;

    @Operation(summary = "Webhook Updated")
    @PostMapping(ApiConstants.webhookUpdated)
    public ResponseEntity<Void> onUpdated(@RequestBody WaybillWebhookDTO waybillWebhookDTO) {
        waybillWebhookService.onUpdate(waybillWebhookDTO);
        return ResponseEntity.ok().build();
    }
}