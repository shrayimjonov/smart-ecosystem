package uz.uzkassa.silen.rest.integration.webhook;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.silen.dto.Result;
import uz.uzkassa.silen.dto.callback.AcceptanceTransferActCallback;
import uz.uzkassa.silen.service.AcceptanceTransferActService;

@RestController
@RequestMapping("/api/acceptance-transfer-act/webhook")
@RequiredArgsConstructor
public class AcceptanceTransferActWebhookResource {
    private final AcceptanceTransferActService acceptanceTransferActService;

    @PostMapping
    public ResponseEntity<Result<Void>> callback(@RequestBody final AcceptanceTransferActCallback dto) {
        acceptanceTransferActService.callback(dto);
        return ResponseEntity.ok().build();
    }
}
