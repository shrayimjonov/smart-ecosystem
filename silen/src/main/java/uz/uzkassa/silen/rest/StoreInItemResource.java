package uz.uzkassa.silen.rest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.silen.dto.StoreInItemCreateDTO;
import uz.uzkassa.silen.dto.StoreInItemDTO;
import uz.uzkassa.silen.dto.StoreInMarkDTO;
import uz.uzkassa.silen.service.StoreInItemMarkService;
import uz.uzkassa.silen.service.StoreInItemService;

@Valid
@RestController
@RequestMapping("/api/store-in/item")
@RequiredArgsConstructor
public class StoreInItemResource {
    private final StoreInItemService storeInItemService;
    private final StoreInItemMarkService storeInItemMarkService;

    @PostMapping
    public ResponseEntity<Void> create(@RequestBody StoreInItemCreateDTO dto) {
        storeInItemService.create(dto);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<StoreInItemDTO> update(@PathVariable Long id, @RequestBody @Valid StoreInItemDTO storeInDTO) {

        return ResponseEntity.ok(storeInItemService.update(id, storeInDTO));
    }

    @GetMapping("/{id}")
    public ResponseEntity<StoreInItemDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(storeInItemService.getItem(id));
    }

    @PostMapping("/add-mark")
    public ResponseEntity<String> addMark(@RequestBody StoreInMarkDTO dto) {
        return ResponseEntity.ok(storeInItemMarkService.create(dto));
    }

    @DeleteMapping("/remove-mark/{id}")
    public ResponseEntity<StoreInMarkDTO> removeMark(@PathVariable String id) {
        storeInItemMarkService.delete(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        storeInItemService.delete(id);
        return ResponseEntity.ok().build();
    }

}
