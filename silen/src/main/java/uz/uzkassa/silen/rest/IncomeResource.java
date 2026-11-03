package uz.uzkassa.silen.rest;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.silen.dto.filter.WarehouseFilter;
import uz.uzkassa.silen.dto.warehouse.IncomeDTO;
import uz.uzkassa.silen.exceptions.BadRequestException;
import uz.uzkassa.silen.service.IncomeService;

/**
 * REST controller for managing {@link uz.uzkassa.silen.domain.Income}.
 */
@RestController
@RequestMapping("/api/warehouse/income")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class IncomeResource {
    IncomeService incomeService;

    @GetMapping
    public ResponseEntity<Page<IncomeDTO>> list(@ModelAttribute WarehouseFilter filter) {
        return ResponseEntity.ok(incomeService.findAllByFilter(filter));
    }

    @GetMapping("/{id}")
    public ResponseEntity<IncomeDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(incomeService.getOne(id));
    }

    @PostMapping
    public ResponseEntity<Long> create(@RequestBody @Valid IncomeDTO dto) {
        return ResponseEntity.ok(incomeService.create(dto));
    }

    @PutMapping
    public ResponseEntity<IncomeDTO> update(@RequestBody @Valid IncomeDTO dto) {
        if (dto.getId() == null) {
            throw new BadRequestException("ID can not be null");
        }
        return ResponseEntity.ok(incomeService.update(dto));
    }

    @PutMapping("/change-status")
    public ResponseEntity<Void> changeStatus(@RequestBody @Valid IncomeDTO dto) {
        if (dto.getId() == null) {
            throw new BadRequestException("ID can not be null");
        }
        incomeService.changeStatus(dto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        incomeService.delete(id);
        return ResponseEntity.ok().build();
    }
}
