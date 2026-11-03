package uz.uzkassa.cts.web.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.cts.dto.actoftransmissiondetail.response.ActOfTransmissionDetailListResponseDto;
import uz.uzkassa.cts.filter.ActOfTransmissionDetailFilter;
import uz.uzkassa.cts.service.ActOfTransmissionDetailService;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/act-of-transmission-details")
@Tag(name = "details")
@Slf4j
public class ActOfTransmissionDetailResource {

    private final ActOfTransmissionDetailService service;

    public ActOfTransmissionDetailResource(ActOfTransmissionDetailService service) {
        this.service = service;
    }

    @Operation(summary ="Get by contract id")
    @GetMapping("/list")
    public ResponseEntity<Page<ActOfTransmissionDetailListResponseDto>> getList(ActOfTransmissionDetailFilter filter) {
        return ResponseEntity.ok(service.getList(filter));
    }

    @Operation(summary ="current month expectations")
    @GetMapping("/get-current-month-expectations")
    public ResponseEntity<BigDecimal> getCurrentMonthIncomeExpectation() {
        return ResponseEntity.ok(service.getCurrentMonthIncomeExpectation());
    }

    @Operation(summary ="total income")
    @GetMapping("/total-income")
    public ResponseEntity<BigDecimal> getTotalIncome(ActOfTransmissionDetailFilter filter) {
        return ResponseEntity.ok(service.getTotalIncome(filter));
    }

    @Operation(summary ="debtors list")
    @GetMapping("/debtors")
    public ResponseEntity<String> getDebtors() {
        String result = service.getDebtors();
        return ResponseEntity.ok(result);
    }

    @Operation(summary ="export to excel")
    @GetMapping("/export-to-excel")
    public ResponseEntity<byte[]> exportToExcel(ActOfTransmissionDetailFilter filter) {
        return ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"act-details.xlsx\"")
            .body(service.download(filter));
    }
}
