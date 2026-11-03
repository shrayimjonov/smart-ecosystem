package uz.uzkassa.cts.web.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.cts.dto.paymentCompanyQuote.PaymentCompanyQuoteDTO;
import uz.uzkassa.cts.dto.paymentCompanyQuote.PaymentCompanyQuoteDetailDTO;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.service.PaymentCompanyQuoteService;

import java.util.List;

@RestController
@RequestMapping("/api/payment-company-quote")
@Tag(name = "Payment company quote")
@Slf4j
@RequiredArgsConstructor
public class PaymentCompanyQuoteResource {

    private final PaymentCompanyQuoteService paymentCompanyQuoteService;

    @Operation(summary ="List")
    @GetMapping
    public ResponseEntity<Page<PaymentCompanyQuoteDetailDTO>> list(BaseFilter filter) {
        return ResponseEntity.ok(paymentCompanyQuoteService.list(filter));
    }

    @Operation(summary ="List by tin")
    @GetMapping(value = "/byTin/{tin}")
    public ResponseEntity<List<PaymentCompanyQuoteDetailDTO>> getListByTin(@PathVariable String tin) {
        return ResponseEntity.ok(paymentCompanyQuoteService.paymentCompanyQuoteListByTin(tin));
    }

    @Operation(summary ="Create")
    @PostMapping
    public ResponseEntity<Long> create(@Valid @RequestBody PaymentCompanyQuoteDTO dto) {
        return ResponseEntity.ok(paymentCompanyQuoteService.create(dto));
    }

    @Operation(summary ="Get")
    @GetMapping(value = "/{id}")
    public ResponseEntity<PaymentCompanyQuoteDetailDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(paymentCompanyQuoteService.get(id));
    }

    @Operation(summary ="Generate Template")
    @GetMapping(value = "/{id}/generate-template")
    public ResponseEntity<String> getTemplate(@PathVariable Long id) {
        return ResponseEntity.ok(paymentCompanyQuoteService.generateHtmlTemplate(id));
    }
}
