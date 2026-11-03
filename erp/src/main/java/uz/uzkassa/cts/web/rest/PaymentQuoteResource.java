package uz.uzkassa.cts.web.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.cts.dto.paymentQuote.PaymentQuoteDTO;
import uz.uzkassa.cts.dto.paymentQuote.PaymentQuoteDetailDTO;
import uz.uzkassa.cts.enums.PaymentQuote;
import uz.uzkassa.cts.enums.PaymentQuoteStatus;
import uz.uzkassa.cts.enums.PaymentQuoteType;
import uz.uzkassa.cts.filter.PaymentQuoteFilter;
import uz.uzkassa.cts.service.PaymentQuoteService;

import java.util.List;

@RestController
@RequestMapping("/api/payment-quote")
@Tag(name = "Payment quote")
@Slf4j
@RequiredArgsConstructor
public class PaymentQuoteResource {

    private final PaymentQuoteService paymentQuoteService;

    @Operation(summary = "List")
    @GetMapping
    public ResponseEntity<Page<PaymentQuoteDetailDTO>> list(@ParameterObject PaymentQuoteFilter filter) {
        return ResponseEntity.ok(paymentQuoteService.list(filter));
    }

    @Operation(summary = "List by tin")
    @GetMapping(value = "/byTin/{tin}")
    public ResponseEntity<List<PaymentQuoteDetailDTO>> getListByTin(@PathVariable String tin) {
        return ResponseEntity.ok(paymentQuoteService.paymentQuoteListByTin(tin));
    }

    @Operation(summary = "Create")
    @PostMapping
    public ResponseEntity<Long> create(@Valid @RequestBody PaymentQuoteDTO dto) {
        return ResponseEntity.ok(paymentQuoteService.create(dto));
    }

    @Operation(summary = "Update")
    @PutMapping(value = "/{id}/{modelId}")
    public ResponseEntity<Long> update(@PathVariable Long id, @PathVariable Long modelId) {
        return ResponseEntity.ok(paymentQuoteService.update(id, modelId));
    }

    @Operation(summary = "Get")
    @GetMapping(value = "/{id}")
    public ResponseEntity<PaymentQuoteDetailDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(paymentQuoteService.get(id));
    }

    @Operation(summary = "Generate Template")
    @GetMapping(value = "/{id}/generate-template")
    public ResponseEntity<String> getTemplate(@PathVariable Long id) {
        return ResponseEntity.ok(paymentQuoteService.generateHtmlTemplate(id));
    }

    @Operation(summary = "Update status")
    @PutMapping(value = "/{id}/change-status")
    public ResponseEntity<Long> changeStatus(@PathVariable Long id, @RequestParam(name = "status") PaymentQuoteStatus status) {
        paymentQuoteService.updateStatus(id, status);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Update payment status")
    @PutMapping(value = "/{id}/payment-status")
    public ResponseEntity<Void> updatePaymentStatus(@PathVariable Long id, @RequestParam(name = "paymentStatus") PaymentQuote paymentStatus) {
        paymentQuoteService.updatePaymentStatus(id, paymentStatus);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Update payment type")
    @PutMapping(value = "/{id}/payment-type")
    public ResponseEntity<Void> updatePaymentType(@PathVariable Long id, @RequestParam(name = "paymentType") PaymentQuoteType paymentType) {
        paymentQuoteService.updatePaymentType(id, paymentType);
        return ResponseEntity.ok().build();
    }
}
