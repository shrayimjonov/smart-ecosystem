package uz.uzkassa.cts.web.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.cts.dto.servicepaymenthistory.response.ServicePaymentHistoryListDto;
import uz.uzkassa.cts.service.ServicePaymentHistoryService;

@RestController
@RequestMapping("/api/servicePaymentHistory")
@Tag(name = "Service Payment history")
@Slf4j
public class ServicePaymentHistoryResource {

    private final ServicePaymentHistoryService service;

    public ServicePaymentHistoryResource(ServicePaymentHistoryService service) {
        this.service = service;
    }

    @Operation(summary ="get current KKM payment statuses")
    @GetMapping("/customer/{id}")
    public ResponseEntity<ServicePaymentHistoryListDto> getPaymentHistoryByCustomerId(@PathVariable Long id) {
        log.debug("REST request to get payment history by customer id: {}", id);
        return ResponseEntity.ok(service.getByCustomerId(id));
    }

    @Operation(summary ="get KKM payment statuses by invoice")
    @GetMapping("/serviceInvoice/{id}")
    public ResponseEntity<ServicePaymentHistoryListDto> getPaymentHistoryByInvoiceId(@PathVariable Long id) {
        log.debug("REST request to get payment history by invoice id: {}", id);
        return ResponseEntity.ok(service.getByInvoiceId(id));
    }

    @Operation(summary ="get KKM payment statuses by invoice")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
