package uz.uzkassa.smartpos.web.rest.admin;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.smartpos.dto.product.AttributeDTO;
import uz.uzkassa.smartpos.service.fashion.AttributeService;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/admin/attributes")
public class AdminAttributeResource {
    private final AttributeService attributeService;

    @PostMapping
    public ResponseEntity<AttributeDTO> create(@RequestBody @Valid final AttributeDTO dto) {
        return ResponseEntity.ok(attributeService.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AttributeDTO> update(@PathVariable final Long id, @RequestBody @Valid final AttributeDTO dto) {
        return ResponseEntity.ok(attributeService.update(id, dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AttributeDTO> get(@PathVariable final Long id) {
        return ResponseEntity.ok(attributeService.get(id));
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable final Long id) {
        attributeService.delete(id);
    }

    @GetMapping
    public List<AttributeDTO> getAttributes(@RequestParam final String key) {
        return attributeService.getAttributes(key);
    }
}
