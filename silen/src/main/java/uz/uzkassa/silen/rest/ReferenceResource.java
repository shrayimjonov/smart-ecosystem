package uz.uzkassa.silen.rest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.silen.dto.ReferenceDTO;
import uz.uzkassa.silen.dto.SelectItem;
import uz.uzkassa.silen.dto.filter.ReferenceFilter;
import uz.uzkassa.silen.exceptions.BadRequestException;
import uz.uzkassa.silen.service.ReferenceService;

import java.util.List;

/**
 * REST controller for managing {@link uz.uzkassa.silen.domain.Reference}.
 */
@RestController
@RequestMapping("/api/reference")
@RequiredArgsConstructor
public class ReferenceResource {
    private final ReferenceService referenceService;

    @PostMapping
    public ResponseEntity<ReferenceDTO> createReference(@RequestBody @Valid ReferenceDTO referenceDTO) {
        if (referenceDTO.getId() != null) {
            throw new BadRequestException("A new reference cannot already have an ID");
        }
        ReferenceDTO result = referenceService.save(referenceDTO);
        return ResponseEntity.ok(result);
    }

    @PutMapping
    public ResponseEntity<ReferenceDTO> updateReference(@RequestBody @Valid ReferenceDTO referenceDTO) {
        if (referenceDTO.getId() == null) {
            throw new BadRequestException("Invalid id");
        }
        ReferenceDTO result = referenceService.save(referenceDTO);
        return ResponseEntity.ok(result);
    }

    @GetMapping
    public ResponseEntity<Page<ReferenceDTO>> getAllReferences(@ModelAttribute ReferenceFilter filter) {
        Page<ReferenceDTO> result = referenceService.findAll(filter);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/items")
    public ResponseEntity<List<SelectItem>> getItems(@ModelAttribute ReferenceFilter filter) {
        List<SelectItem> result = referenceService.getItems(filter);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReferenceDTO> getReference(@PathVariable String id) {
        ReferenceDTO result = referenceService.findOne(id);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReference(@PathVariable String id) {
        referenceService.delete(id);
        return ResponseEntity.ok().build();
    }
}
