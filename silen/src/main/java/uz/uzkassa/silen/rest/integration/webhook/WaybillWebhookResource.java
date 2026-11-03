package uz.uzkassa.silen.rest.integration.webhook;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.silen.dto.Result;
import uz.uzkassa.silen.dto.callback.WaybillCallback;
import uz.uzkassa.silen.service.WaybillService;

@RestController
@RequestMapping("/api/waybill/webhook")
@RequiredArgsConstructor
public class WaybillWebhookResource {
    private static final Logger log = LoggerFactory.getLogger(WaybillWebhookResource.class);
    private final WaybillService waybillService;

    @PostMapping
    public ResponseEntity<Result<Void>> callback(@Valid @RequestBody final WaybillCallback dto) {
        waybillService.callback(dto);
        return ResponseEntity.ok().build();
    }
}
