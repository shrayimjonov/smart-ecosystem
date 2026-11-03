package uz.uzkassa.silen.rest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.silen.dto.SelectItem;
import uz.uzkassa.silen.dto.StoreDTO;
import uz.uzkassa.silen.dto.filter.StoreFilter;
import uz.uzkassa.silen.enumeration.StoreStatus;
import uz.uzkassa.silen.exceptions.BadRequestException;
import uz.uzkassa.silen.service.StoreService;

import java.util.List;

/**
 * REST controller for managing {@link uz.uzkassa.silen.domain.Store}.
 */
@RestController
@RequestMapping("/api/store")
@RequiredArgsConstructor
public class StoreResource {
    private final StoreService storeService;

    @PostMapping
    public ResponseEntity<StoreDTO> create(@RequestBody @Valid StoreDTO storeDTO) {
        if (storeDTO.getId() != null) {
            throw new BadRequestException("A new object cannot already have an ID");
        }
        return ResponseEntity.ok(storeService.save(storeDTO));
    }

    @PutMapping
    public ResponseEntity<StoreDTO> update(@RequestBody @Valid StoreDTO storeDTO) {
        if (storeDTO.getId() == null) {
            throw new BadRequestException("Invalid id");
        }
        return ResponseEntity.ok(storeService.save(storeDTO));
    }

    @GetMapping
    public ResponseEntity<Page<StoreDTO>> getAll(@ModelAttribute StoreFilter filter) {
        return ResponseEntity.ok(storeService.findAll(filter));
    }

    @GetMapping("/items")
    public ResponseEntity<List<SelectItem>> getItems(@ModelAttribute StoreFilter filter) {
        return ResponseEntity.ok(storeService.getItems(filter));
    }

    @GetMapping("/statuses")
    public ResponseEntity<List<SelectItem>> getStatuses() {
        return ResponseEntity.ok(StoreStatus.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<StoreDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(storeService.findOne(id));
    }
}
