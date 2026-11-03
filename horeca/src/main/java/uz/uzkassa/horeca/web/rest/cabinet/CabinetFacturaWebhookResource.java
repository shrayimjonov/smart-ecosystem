package uz.uzkassa.horeca.web.rest.cabinet;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.horeca.constants.ApiConstants;
import uz.uzkassa.horeca.dto.factura.FacturaNewDTO;
import uz.uzkassa.horeca.dto.factura.Status;
import uz.uzkassa.horeca.dto.factura.WebhookSignDTO;
import uz.uzkassa.horeca.service.FacturaWebhookService;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.cabinetInvoiceRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CabinetFacturaWebhookResource {

    FacturaWebhookService facturaWebhookService;

    @PostMapping(ApiConstants.webhookReceived)
    public ResponseEntity<Void> onReceive(@RequestBody WebhookSignDTO webhookSignDTO) {
        facturaWebhookService.onReceive(webhookSignDTO.getSign());
        return ResponseEntity.ok().build();
    }

    @PostMapping(ApiConstants.webhookAccepted)
    public ResponseEntity<Void> onAccept(@RequestBody WebhookSignDTO webhookSignDTO) {
        facturaWebhookService.onStatusChange(webhookSignDTO.getSign(), Status.ACCEPTED);
        return ResponseEntity.ok().build();
    }

    @PostMapping(ApiConstants.webhookAgentAccepted)
    public ResponseEntity<Void> onAgentAccept(@RequestBody WebhookSignDTO webhookSignDTO) {
        facturaWebhookService.onStatusChange(webhookSignDTO.getSign(), Status.AGENT_ACCEPTED);
        return ResponseEntity.ok().build();
    }

    @PostMapping(ApiConstants.webhookRejected)
    public ResponseEntity<Void> onRejected(@RequestBody WebhookSignDTO webhookSignDTO) {
        facturaWebhookService.onStatusChange(webhookSignDTO.getSign(), Status.REJECTED);
        return ResponseEntity.ok().build();
    }

    @PostMapping(ApiConstants.webhookCancelled)
    public ResponseEntity<Void> onCancelled(@RequestBody WebhookSignDTO webhookSignDTO) {
        facturaWebhookService.onStatusChange(webhookSignDTO.getSign(), Status.CANCELLED);
        return ResponseEntity.ok().build();
    }

    @PostMapping(ApiConstants.webhookUpdated)
    public ResponseEntity<Void> onUpdated(@RequestBody FacturaNewDTO facturaNewDTO) {
        facturaWebhookService.onUpdate(facturaNewDTO);
        return ResponseEntity.ok().build();
    }
}
