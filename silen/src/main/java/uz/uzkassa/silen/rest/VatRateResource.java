package uz.uzkassa.silen.rest;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.silen.dto.VatRateDTO;
import uz.uzkassa.silen.service.VatRateService;

import java.util.List;

@RestController
@RequestMapping("/api/vat/rate")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class VatRateResource {
    VatRateService vatRateService;

    @PostMapping
    public ResponseEntity<VatRateDTO> create(@RequestBody VatRateDTO vatRateDTO) {
        return ResponseEntity.ok(vatRateService.create(vatRateDTO));
    }

    @GetMapping
    public ResponseEntity<List<VatRateDTO>> findAll() {
        return ResponseEntity.ok(vatRateService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<VatRateDTO> get(@PathVariable("id") Long id) {
        return ResponseEntity.ok(vatRateService.get(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<VatRateDTO> update(@PathVariable("id") Long id, @RequestBody VatRateDTO vatRateDTO) {
        return ResponseEntity.ok(vatRateService.update(id, vatRateDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        vatRateService.delete(id);
        return ResponseEntity.ok().build();
    }
}
