package uz.uzkassa.cts.web.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.cts.dto.base.CommonCodeDTO;
import uz.uzkassa.cts.dto.customerBranchScanner.CustomerBranchScannerDTO;
import uz.uzkassa.cts.filter.CBScannerFilter;
import uz.uzkassa.cts.service.CustomerBranchScannerService;

import java.util.List;

/**
 * REST controller for managing {@link uz.uzkassa.cts.domain.CustomerBranchScanner}.
 */
@RestController
@RequestMapping(value = "/api/customer/branch/scanner")
@Tag(name = "CB Scanner")
@Slf4j
@RequiredArgsConstructor
public class CustomerBranchScannerResource {

    private final CustomerBranchScannerService scannerService;

    @Operation(summary = "Create")
    @PostMapping
    public ResponseEntity<Long> create(@Valid @RequestBody CustomerBranchScannerDTO scannerDTO) {
        return ResponseEntity.ok(scannerService.create(scannerDTO));
    }

    @Operation(summary = "Update")
    @PutMapping("/{id}")
    public ResponseEntity<Long> update(@PathVariable Long id, @Valid @RequestBody CustomerBranchScannerDTO scannerDTO) {
        return ResponseEntity.ok(scannerService.update(id, scannerDTO));
    }

    @Operation(summary = "Get")
    @GetMapping("/{id}")
    public ResponseEntity<CommonCodeDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(scannerService.get(id));
    }

    @Operation(summary = "List")
    @GetMapping
    public ResponseEntity<Page<CommonCodeDTO>> list(CBScannerFilter filter) {
        return ResponseEntity.ok(scannerService.list(filter));
    }

    @Operation(summary = "Look Up")
    @GetMapping("/lookup")
    public ResponseEntity<List<CommonCodeDTO>> lookUp(CBScannerFilter filter) {
        return ResponseEntity.ok(scannerService.lookUp(filter));
    }

    @Operation(summary = "Sync")
    @GetMapping(value = "/sync")
    public ResponseEntity<Void> sync() {
        scannerService.sync();
        return ResponseEntity.ok().build();
    }
}
