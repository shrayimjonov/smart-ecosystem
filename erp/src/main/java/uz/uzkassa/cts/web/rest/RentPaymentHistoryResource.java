package uz.uzkassa.cts.web.rest;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.cts.dto.rentpaymenthistory.request.RentPaymentHistoryRequestDto;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.service.RentPaymentHistoryService;
import uz.uzkassa.cts.web.rest.errors.BadRequestException;

import java.util.List;

@RestController
@RequestMapping("/api/rent-payment-history")
@Tag(name = "Rent payment history")
@Slf4j
public class RentPaymentHistoryResource {

    private final RentPaymentHistoryService service;

    public RentPaymentHistoryResource(RentPaymentHistoryService service) {
        this.service = service;
    }
//
//    @Operation(summary ="List")
//    @GetMapping
//    public ResponseEntity<Page<RentPaymentHistoryRequestDto>> getAll(TradeInContractFilter filter) {
//        log.debug("REST request to get all TradeInContract : {}", filter);
//        return ResponseEntity.ok(service.list(filter));
//    }

    @Operation(summary = "get by tin")
    @GetMapping("/get-by-customer-id")
    public ResponseEntity<Page<RentPaymentHistoryRequestDto>> getByCustomerId(BaseFilter filter) {
        if (filter.getCustomerId() == null)
            throw new BadRequestException("CustomerId cannot be null");
        return ResponseEntity.ok(service.getByCustomerId(filter));
    }

    @Operation(summary = "get by tin")
    @GetMapping("/by-offer-id/{offerId}")
    public ResponseEntity<List<RentPaymentHistoryRequestDto>> getByOfferId(@PathVariable Long offerId) {
        return ResponseEntity.ok(service.getByOfferId(offerId));
    }

    @Operation(summary = "get current details by offerId")
    @GetMapping("current-details/by-offer-id/{offerId}")
    public ResponseEntity<List<RentPaymentHistoryRequestDto>> getCurrentDetailsByOfferId(@PathVariable Long offerId) {
        return ResponseEntity.ok(service.getByOfferId(offerId));
    }

    @Operation(summary = "get details by invoiceId")
    @GetMapping("details/by-invoice-id/{invoiceId}")
    public ResponseEntity<List<RentPaymentHistoryRequestDto>> getDetailsByInvoiceId(@PathVariable Long invoiceId) {
        return ResponseEntity.ok(service.getByInvoiceId(invoiceId));
    }

    @Operation(summary = "create-test")
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<Void> create(@PathVariable Long customerId) {
        service.createHistoryByCustomerId(customerId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "create-test")
    @GetMapping("/bonus-list")
    public ResponseEntity<Void> getBonusList() {
        service.calculateBonusCron();
        return ResponseEntity.ok().build();
    }
}
