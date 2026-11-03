package uz.uzkassa.smartpossupply.web.rest.cabinet;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.smartpossupply.constants.ApiConstants;
import uz.uzkassa.smartpossupply.dto.factura.FacturaNewDTO;
import uz.uzkassa.smartpossupply.dto.factura.Status;
import uz.uzkassa.smartpossupply.dto.factura.WebhookSignDTO;
import uz.uzkassa.smartpossupply.service.FacturaWebhookService;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.cabinetInvoiceRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Slf4j
public class CabinetInvoiceWebhookResource {

    FacturaWebhookService facturaWebhookService;

    @Operation(summary = "Webhook Received")
    @PostMapping(ApiConstants.webhookReceived)
    public ResponseEntity<Void> onReceive(@Valid @RequestBody WebhookSignDTO webhookSignDTO) {
        facturaWebhookService.onReceive(webhookSignDTO.getSign());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Webhook Accepted")
    @PostMapping(ApiConstants.webhookAccepted)
    public ResponseEntity<Void> onAccept(@Valid @RequestBody WebhookSignDTO webhookSignDTO) {
        facturaWebhookService.onStatusChange(webhookSignDTO.getSign(), Status.ACCEPTED);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Webhook Agent Accepted")
    @PostMapping(ApiConstants.webhookAgentAccepted)
    public ResponseEntity<Void> onAgentAccept(@Valid @RequestBody WebhookSignDTO webhookSignDTO) {
        facturaWebhookService.onStatusChange(webhookSignDTO.getSign(), Status.AGENT_ACCEPTED);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Webhook Rejected")
    @PostMapping(ApiConstants.webhookRejected)
    public ResponseEntity<Void> onRejected(@Valid @RequestBody WebhookSignDTO webhookSignDTO) {
        facturaWebhookService.onStatusChange(webhookSignDTO.getSign(), Status.REJECTED);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Webhook Cancelled")
    @PostMapping(ApiConstants.webhookCancelled)
    public ResponseEntity<Void> onCancelled(@Valid @RequestBody WebhookSignDTO webhookSignDTO) {
        facturaWebhookService.onStatusChange(webhookSignDTO.getSign(), Status.CANCELLED);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Webhook Updated")
    @PostMapping(ApiConstants.webhookUpdated)
    public ResponseEntity<Void> onUpdated(@Valid @RequestBody FacturaNewDTO facturaNewDTO) {
        facturaWebhookService.onUpdate(facturaNewDTO);
        return ResponseEntity.ok().build();
    }
}
