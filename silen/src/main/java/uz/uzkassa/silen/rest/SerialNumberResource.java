package uz.uzkassa.silen.rest;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.silen.dto.SelectItem;
import uz.uzkassa.silen.dto.SerialNumberDTO;
import uz.uzkassa.silen.dto.SerialNumberListDTO;
import uz.uzkassa.silen.dto.SerialSelectItemDTO;
import uz.uzkassa.silen.dto.filter.SerialNumberFilter;
import uz.uzkassa.silen.enumeration.SerialNumberStatus;
import uz.uzkassa.silen.service.SerialNumberService;

import java.util.List;

@RestController
@FieldDefaults(level = AccessLevel.PACKAGE, makeFinal = true)
@RequiredArgsConstructor
@RequestMapping("/api/serial/number")
public class SerialNumberResource {
    SerialNumberService serialNumberService;

    @PostMapping
    public ResponseEntity<Void> create(@RequestBody SerialNumberDTO serialNumberDTO) {
        serialNumberService.save(serialNumberDTO);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<Page<SerialNumberListDTO>> findAll(@ModelAttribute SerialNumberFilter serialNumberFilter) {
        return ResponseEntity.ok(serialNumberService.findAll(serialNumberFilter));
    }

    @GetMapping("/items")
    public ResponseEntity<List<SerialSelectItemDTO>> getItems(@ModelAttribute SerialNumberFilter serialNumberFilter) {
        return ResponseEntity.ok(serialNumberService.selectItems(serialNumberFilter));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SerialNumberListDTO> findOne(@PathVariable String id) {
        return ResponseEntity.ok(serialNumberService.findOne(id));
    }

    @GetMapping("/stats")
    public ResponseEntity<Long> findStatsByFilter(@ModelAttribute SerialNumberFilter serialNumberFilter) {
        return ResponseEntity.ok(serialNumberService.findStatsByFilter(serialNumberFilter));
    }

    @GetMapping("/statuses")
    public ResponseEntity<List<SelectItem>> findAllStatuses() {
        return ResponseEntity.ok(SerialNumberStatus.getAll());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable String id, @RequestBody SerialNumberDTO serialNumberDTO) {
        serialNumberService.update(id, serialNumberDTO);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/{status}")
    public ResponseEntity<SerialNumberStatus> updateStatus(@PathVariable("id") String serialNumberId, @PathVariable("status") SerialNumberStatus serialNumberStatus) {
        return ResponseEntity.ok(serialNumberService.updateStatus(serialNumberId, serialNumberStatus));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") String id) {
        serialNumberService.delete(id);
        return ResponseEntity.ok().build();
    }

}
