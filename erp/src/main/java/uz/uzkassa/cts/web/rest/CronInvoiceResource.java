package uz.uzkassa.cts.web.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.cts.dto.contract.response.ContactResponseDto;
import uz.uzkassa.cts.service.CronInvoiceService;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/cronInvoice")
@Tag(name = "Cron check")
@Slf4j
public class CronInvoiceResource {

    private final CronInvoiceService service;

    public CronInvoiceResource(CronInvoiceService service) {
        this.service = service;
    }

    @Operation(summary ="check invoice creation")
    @PostMapping("/check/{date}")
    public ResponseEntity<ContactResponseDto> check(@PathVariable LocalDate date) {
        service.generateInvoice(date);
        return ResponseEntity.ok().build();
    }
}
