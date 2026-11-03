package uz.uzkassa.silen.rest;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.silen.dto.filter.BatchFilter;
import uz.uzkassa.silen.dto.marking.BatchAggregationDTO;
import uz.uzkassa.silen.dto.marking.BatchAggregationListDTO;
import uz.uzkassa.silen.dto.marking.BatchAggregationResponseDTO;
import uz.uzkassa.silen.exceptions.BadRequestException;
import uz.uzkassa.silen.service.BatchService;

/**
 * REST controller for managing {@link uz.uzkassa.silen.domain.BatchAggregation}.
 */
@Slf4j
@RestController
@RequestMapping("/api/aggregation/batch")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AggregationBatchResource {
    BatchService batchService;

    @PostMapping
    public ResponseEntity<BatchAggregationResponseDTO> create(@RequestBody @Valid BatchAggregationDTO batchAggregationDTO) {
        if (batchAggregationDTO.getQuantity() > 5000) {
            throw new BadRequestException("Допустимое значение для заказ КА от 1 до 5 000");
        }
        return ResponseEntity.ok(batchService.save(batchAggregationDTO));
    }

    @PostMapping("/resized")
    public ResponseEntity<BatchAggregationResponseDTO> createResized(@RequestBody @Valid BatchAggregationDTO batchAggregationDTO) {
        log.info("Resizing batch aggregation {}", batchAggregationDTO);
        if (batchAggregationDTO.getQuantity() > 30_000) {
            throw new BadRequestException("Допустимое значение для заказ КА от 1 до 30 000");
        }
        batchAggregationDTO.setExportToFile(true);
        return ResponseEntity.ok(batchService.save(batchAggregationDTO));
    }

    @GetMapping
    public ResponseEntity<Page<BatchAggregationListDTO>> getAll(@ModelAttribute BatchFilter batchFilter) {
        return ResponseEntity.ok(batchService.findAll(batchFilter));
    }

    @GetMapping("/stats")
    public ResponseEntity<Long> stats(@ModelAttribute BatchFilter batchFilter) {
        return ResponseEntity.ok(batchService.getStats(batchFilter));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BatchAggregationListDTO> get(@PathVariable String id) {
        return ResponseEntity.ok(batchService.findOne(id));
    }
}
