package uz.uzkassa.cts.web.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.cts.dto.sale.contract.SaleContractDTO;
import uz.uzkassa.cts.dto.sale.contract.SaleContractResponseDTO;
import uz.uzkassa.cts.dto.sale.contract.SaleContractUpdateStatusDTO;
import uz.uzkassa.cts.filter.SaleContractFilter;
import uz.uzkassa.cts.service.SaleContractService;

@RestController
@RequestMapping("/api/sale-contract")
@Tag(name = "Sale Contract")
@Slf4j
@RequiredArgsConstructor
public class SaleContractResource {

    private final SaleContractService saleContractService;

    @Operation(summary ="List")
    @GetMapping
    public ResponseEntity<Page<SaleContractResponseDTO>> getAll(SaleContractFilter filter) {
        return ResponseEntity.ok(saleContractService.list(filter));
    }

    @Operation(summary ="Create")
    @PostMapping
    public ResponseEntity<Long> create(@Valid @RequestBody SaleContractDTO dto) {
        return ResponseEntity.ok(saleContractService.create(dto));
    }

    @Operation(summary ="Get")
    @GetMapping("/{id}")
    public ResponseEntity<SaleContractResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(saleContractService.getById(id));
    }

    @Operation(summary ="Change Status")
    @PutMapping("/status/change")
    public ResponseEntity<Long> changeStatus(@Valid @RequestBody SaleContractUpdateStatusDTO updateStatusDTO) {
        return ResponseEntity.ok(saleContractService.updateStatus(updateStatusDTO));
    }

    @Operation(summary ="Generate Template")
    @GetMapping("/{id}/generate-template")
    public ResponseEntity<String> getTemplate(@PathVariable Long id) {
        return ResponseEntity.ok(saleContractService.getHtmlTemplate(id));
    }

    @Operation(summary ="Upload File")
    @PostMapping("/{id}/upload-contract")
    public ResponseEntity<SaleContractResponseDTO> uploadFile(@RequestParam("file") MultipartFile file, @PathVariable Long id) {
        return ResponseEntity.ok(saleContractService.uploadFile(id, file));
    }
}
