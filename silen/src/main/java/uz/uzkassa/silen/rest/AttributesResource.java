package uz.uzkassa.silen.rest;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.silen.dto.AttributesDTO;
import uz.uzkassa.silen.dto.AttributesListDTO;
import uz.uzkassa.silen.dto.ProductAttributesDTO;
import uz.uzkassa.silen.dto.filter.AttributesFilter;
import uz.uzkassa.silen.service.AttributesService;

import java.util.List;

@RestController
@RequestMapping("/api/attributes")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AttributesResource {
    AttributesService attributesService;

    @PostMapping
    public ResponseEntity<AttributesListDTO> create(@RequestBody @Valid AttributesDTO dto) {
        return ResponseEntity.ok(attributesService.save(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AttributesListDTO> findById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(attributesService.findOne(id));
    }

    @GetMapping
    public ResponseEntity<Page<AttributesListDTO>> findAll(@ModelAttribute AttributesFilter filter) {
        return ResponseEntity.ok(attributesService.findAll(filter));
    }

    @GetMapping("/items")
    public ResponseEntity<List<AttributesListDTO>> getItems(@ModelAttribute AttributesFilter filter) {
        return ResponseEntity.ok(attributesService.getItemsList(filter));
    }

    @GetMapping("/by/product")
    public ResponseEntity<List<ProductAttributesDTO>> findAllByProduct(@RequestParam("productId") String productId) {
        return ResponseEntity.ok(attributesService.findAllByProduct(productId));
    }

    @PutMapping
    public ResponseEntity<AttributesListDTO> update(@RequestBody @Valid AttributesListDTO dto) {
        attributesService.update(dto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        attributesService.delete(id);
        return ResponseEntity.ok().build();
    }
}
