package uz.uzkassa.cts.web.rest;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.cts.dto.xfile.request.XfileChangeInvoiceStatusRequestDto;
import uz.uzkassa.cts.service.XfileService;

@RestController
@RequestMapping("/api/x-file")
@Tag(name = "x-file")
public class XfileResource {

    private final XfileService service;

    public XfileResource(XfileService service) {
        this.service = service;
    }

    @Operation(summary = "Get")
    @GetMapping("/auth")
    public ResponseEntity<String> auth() {
        return ResponseEntity.ok(service.authenticate());
    }

    @Operation(summary = "Get")
    @GetMapping("/invoice")
    public ResponseEntity<Long> invoice() {

//        return ResponseEntity.ok(service.sendInvoice(new FacturaDTO()));
        return ResponseEntity.ok().build();
    }

    @PostMapping("/change-invoice-status")
    public ResponseEntity<Void> changeInvoiceStatus(@Valid @RequestBody XfileChangeInvoiceStatusRequestDto dto) {
        service.changeInvoiceStatus(dto);
        return ResponseEntity.ok().build();
    }

}
