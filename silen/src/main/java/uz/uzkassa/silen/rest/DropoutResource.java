package uz.uzkassa.silen.rest;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.silen.dto.*;
import uz.uzkassa.silen.dto.filter.DropoutFilter;
import uz.uzkassa.silen.dto.filter.MongoFilter;
import uz.uzkassa.silen.enumeration.DropoutReason;
import uz.uzkassa.silen.integration.asilbelgi.dto.CisInfoResponse;
import uz.uzkassa.silen.service.DropoutService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/dropout")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DropoutResource {

    DropoutService dropoutService;

    @PostMapping
    public ResponseEntity<List<String>> create(@Valid @RequestBody DropoutDTO dropoutDTO) {
        return ResponseEntity.ok(dropoutService.create(dropoutDTO));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DropoutListDTO> get(@PathVariable("id") String id) {
        return ResponseEntity.ok(dropoutService.get(id));
    }

    @GetMapping
    public ResponseEntity<Page<DropoutListDTO>> getAll(DropoutFilter filter) {
        return ResponseEntity.ok(dropoutService.findAllByFilter(filter));
    }

    @GetMapping("/stats")
    public ResponseEntity<DropoutStatsDTO> stats(DropoutFilter filter) {
        return ResponseEntity.ok(dropoutService.findAllByFilterStats(filter));
    }

    @GetMapping("/reasons")
    public ResponseEntity<List<SelectItem>> dropoutReasons() {
        return ResponseEntity.ok(DropoutReason.getAll());
    }

    @PostMapping("/check")
    public ResponseEntity<DropoutCheckDTO> check(@RequestBody @Valid DropoutValidateDTO validateDTO) {
        return ResponseEntity.ok(dropoutService.validate(validateDTO));
    }

    @GetMapping("/check-party")
    public ResponseEntity<CisInfoResponse[]> partyCheck(@ModelAttribute MongoFilter filter) {
        return ResponseEntity.ok(dropoutService.partyCheck(filter));
    }

    @GetMapping("/batch-resend")
    public ResponseEntity<Void> batchResend() {
        dropoutService.sendBatchQueue();
        return ResponseEntity.ok().build();
    }
}
