package uz.uzkassa.cts.web.rest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.cts.dto.apay.APayUpdateBillStatusRequestDTO;
import uz.uzkassa.cts.dto.bill.BillConfirmDTO;
import uz.uzkassa.cts.dto.bill.BillDetailDTO;
import uz.uzkassa.cts.dto.bill.BillResponseDTO;
import uz.uzkassa.cts.dto.bill.BillUpdateDTO;
import uz.uzkassa.cts.filter.BillFilter;
import uz.uzkassa.cts.service.BillService;


@Slf4j
@RestController
@RequestMapping(value = "/api/bill")
@RequiredArgsConstructor
public class BillResource {

    private final BillService billService;

    @GetMapping("/{id}")
    public ResponseEntity<BillDetailDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(billService.get(id));
    }

    @GetMapping
    public ResponseEntity<Page<BillDetailDTO>> list(BillFilter filter) {
        return ResponseEntity.ok(billService.list(filter));
    }

    @PutMapping(value = "/card-transaction")
    public ResponseEntity<?> get(@RequestBody @Valid BillUpdateDTO billUpdateDTO) {
        billService.updateAPay(billUpdateDTO);
        return ResponseEntity.ok().build();
    }

    @PutMapping(value = "/check-transaction/{billId}")
    public ResponseEntity<BillResponseDTO> check(@PathVariable String billId) {
        return ResponseEntity.ok(billService.checkPay(billId));
    }

    @PutMapping(value = "/confirm-transaction")
    public ResponseEntity<?> confirm(@RequestBody @Valid BillConfirmDTO dto) {
        billService.confirmationPay(dto);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/bill-response")
    public ResponseEntity<?> updateBillStatusByBillId(String billId,
                                                      @RequestBody APayUpdateBillStatusRequestDTO billStatusRequestDTO) {
        billService.updateBillStatusByBillId(billId, billStatusRequestDTO);
        return ResponseEntity.ok().build();
    }
}
