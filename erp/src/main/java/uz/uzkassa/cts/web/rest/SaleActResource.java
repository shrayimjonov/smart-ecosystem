package uz.uzkassa.cts.web.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.cts.dto.sale.act.SaleActPowerOfAttorneyDTO;
import uz.uzkassa.cts.dto.sale.act.SaleActResponseDTO;
import uz.uzkassa.cts.filter.SaleActFilter;
import uz.uzkassa.cts.service.SaleActService;

@RestController
@RequestMapping("/api/sale-act")
@Tag(name = "Sale Act")
@Slf4j
@RequiredArgsConstructor
public class SaleActResource {

    private final SaleActService saleActService;


    @Operation(summary ="Get by contract id")
    @GetMapping("/by-contract-id/{id}")
    public ResponseEntity<SaleActResponseDTO> getByContractId(@PathVariable Long id) {
        return ResponseEntity.ok(saleActService.getByContractId(id));
    }

    @Operation(summary ="List")
    @GetMapping("/list")
    public ResponseEntity<Page<SaleActResponseDTO>> list(SaleActFilter filter) {
        return ResponseEntity.ok(saleActService.getList(filter));
    }

    @Operation(summary ="Get")
    @GetMapping("/{id}")
    public ResponseEntity<SaleActResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(saleActService.getById(id));
    }

    @Operation(summary ="Generate Template")
    @GetMapping("/{id}/generate-template")
    public ResponseEntity<String> getTemplateById(@PathVariable Long id) {
        return ResponseEntity.ok(saleActService.getHtmlTemplate(id));
    }

    @Operation(summary ="Upload File")
    @PostMapping("/{id}/upload")
    public ResponseEntity<SaleActResponseDTO> uploadFile(@RequestParam("file") MultipartFile file, @PathVariable Long id) {
        return ResponseEntity.ok(saleActService.uploadFile(id, file));
    }

    @Operation(summary ="Upload Power of Attorney")
    @PostMapping("/{id}/upload-power-attorney")
    public ResponseEntity<SaleActResponseDTO> uploadPowerOfAttorneyFile(@RequestParam("file") MultipartFile file, @PathVariable Long id) {
        return ResponseEntity.ok(saleActService.uploadPowerAttorneyFile(id, file));
    }

    @Operation(summary ="Accept")
    @PutMapping("/{id}/status/accept")
    public ResponseEntity<SaleActResponseDTO> accept(@PathVariable Long id) {
        return ResponseEntity.ok(saleActService.accept(id));
    }

    @Operation(summary ="Attach")
    @PutMapping("/{id}/status/change-to-attach")
    public ResponseEntity<SaleActResponseDTO> changeToAttach(@PathVariable Long id) {
        return ResponseEntity.ok(saleActService.changeToAttach(id));
    }

    @Operation(summary ="Generate POA Template")
    @PostMapping("/generate/poa/template")
    public ResponseEntity<String> generatePoaTemplate(@RequestBody SaleActPowerOfAttorneyDTO dto) {
        return ResponseEntity.ok(saleActService.generatePowerOfAttorneyTemplate(dto));
    }
}
