package uz.uzkassa.silen.rest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.silen.dto.ExciseDTO;
import uz.uzkassa.silen.dto.filter.ExciseFilter;
import uz.uzkassa.silen.exceptions.BadRequestException;
import uz.uzkassa.silen.service.ExciseService;

/**
 * REST controller for managing {@link uz.uzkassa.silen.domain.Excise}.
 */
@RestController
@RequestMapping("/api/excise")
@RequiredArgsConstructor
public class ExciseResource {
    private final ExciseService exciseService;

    @PostMapping
    public ResponseEntity<ExciseDTO> create(@RequestBody @Valid ExciseDTO ExciseDTO) {
        if (ExciseDTO.getId() != null) {
            throw new BadRequestException("A new excise cannot already have an ID");
        }
        ExciseDTO result = exciseService.save(ExciseDTO);
        return ResponseEntity.ok(result);
    }

    @PutMapping
    public ResponseEntity<ExciseDTO> update(@RequestBody @Valid ExciseDTO ExciseDTO) {
        if (ExciseDTO.getId() == null) {
            throw new BadRequestException("Invalid id");
        }
        ExciseDTO result = exciseService.save(ExciseDTO);
        return ResponseEntity.ok(result);
    }

    @GetMapping
    public ResponseEntity<Page<ExciseDTO>> getAll(@ModelAttribute ExciseFilter filter) {
        Page<ExciseDTO> result = exciseService.findAll(filter);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExciseDTO> get(@PathVariable Long id) {
        ExciseDTO result = exciseService.findOne(id);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        exciseService.delete(id);
        return ResponseEntity.ok().build();
    }
}
