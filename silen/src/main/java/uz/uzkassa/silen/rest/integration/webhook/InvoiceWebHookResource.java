package uz.uzkassa.silen.rest.integration.webhook;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.silen.dto.callback.InvoiceCallback;
import uz.uzkassa.silen.service.InvoiceHookService;

@RestController
@RequestMapping("/api/invoice/webhook")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class InvoiceWebHookResource {
    InvoiceHookService invoiceHookService;

    @PostMapping("/updated")
    public void onUpdate(@Valid @RequestBody final InvoiceCallback dto) {
        invoiceHookService.onUpdate(dto);
    }
}
