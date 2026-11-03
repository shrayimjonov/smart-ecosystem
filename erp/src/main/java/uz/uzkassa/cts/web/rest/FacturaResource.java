package uz.uzkassa.cts.web.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.cts.dto.factura.FacturaBankDTO;
import uz.uzkassa.cts.service.FacturaService;

import java.util.List;


@RestController
@RequestMapping("/api/factura")
@Tag(name = "Factura")
@Slf4j
public class FacturaResource {

    private final FacturaService facturaService;

    public FacturaResource(FacturaService facturaService) {
        this.facturaService = facturaService;
    }

    @Operation(summary ="Get Banks")
    @GetMapping("/banks/{language}")
    public ResponseEntity<List<FacturaBankDTO>> getBanks(@PathVariable String language) {
        return ResponseEntity.ok(facturaService.getBanks(language));
    }

}
