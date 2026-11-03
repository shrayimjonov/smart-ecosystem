package uz.uzkassa.silen.rest;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.silen.dto.filter.AggregationFilter;
import uz.uzkassa.silen.dto.filter.MongoFilter;
import uz.uzkassa.silen.dto.marking.BlockCreateDTO;
import uz.uzkassa.silen.dto.marking.BlockUpdateDTO;
import uz.uzkassa.silen.dto.marking.ValidationDTO;
import uz.uzkassa.silen.dto.mongo.BlockDTO;
import uz.uzkassa.silen.enumeration.AggregationStatus;
import uz.uzkassa.silen.enumeration.FileType;
import uz.uzkassa.silen.service.AggregationService;
import uz.uzkassa.silen.service.BlockService;
import uz.uzkassa.silen.service.ValidationService;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * REST controller for managing {@link uz.uzkassa.silen.domain.Aggregation}.
 */
@RestController
@RequestMapping("/api/aggregation/block")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AggregationBlockResource {
    BlockService blockService;
    ValidationService validationService;
    AggregationService aggregationService;

    static List<AggregationStatus> aggregationStatusList = Arrays.asList(AggregationStatus.CREATED, AggregationStatus.IN_PROGRESS,
            AggregationStatus.UPDATED, AggregationStatus.ERROR, AggregationStatus.SENT, AggregationStatus.APPLIED);

    @PostMapping
    public ResponseEntity<String> create(@RequestBody @Valid BlockCreateDTO blockCreateDTO) {
        return ResponseEntity.ok(blockService.create(blockCreateDTO, false));
    }

    @PutMapping
    public ResponseEntity<String> update(@RequestParam("id") String id, @RequestBody @Valid BlockUpdateDTO blockUpdateDTO) {
        return ResponseEntity.ok(blockService.update(id, blockUpdateDTO));
    }

    @PutMapping("/validate")
    public ResponseEntity<String> validate(@RequestBody @Valid ValidationDTO dto) {
        return ResponseEntity.ok(validationService.validate(dto));
    }

    @GetMapping
    public ResponseEntity<Page<BlockDTO>> getAll(@ModelAttribute MongoFilter filter) {
        filter.setAggregationStatusList(aggregationStatusList);
        return ResponseEntity.ok(blockService.findAllByFilter(filter));
    }

    @GetMapping("/stats")
    public ResponseEntity<BigDecimal> getStats(@ModelAttribute final MongoFilter filter) {
        filter.setAggregationStatusList(aggregationStatusList);
        return ResponseEntity.ok(blockService.getStats(filter));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BlockDTO> get(@PathVariable("id") String id) {
        return ResponseEntity.ok(blockService.getOne(id));
    }

    @GetMapping(value = "/export_csv")
    public ResponseEntity<Void> generateCsv(@ModelAttribute MongoFilter filter) {
        filter.setFileType(FileType.CSV);
        blockService.exportReport(filter);
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/export")
    public ResponseEntity<Void> generateExportReport(@ModelAttribute MongoFilter filter) {
        filter.setFileType(FileType.CSV);
        blockService.exportReport(filter);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/send/{id}")
    public ResponseEntity<AggregationStatus> send(@PathVariable String id) {
        return ResponseEntity.ok(blockService.checkAndUtiliseById(id));
    }

    @GetMapping("/start/cron")
    public ResponseEntity<Void> startCron(@ModelAttribute AggregationFilter filter) {
        aggregationService.checkAndResendByFilter(filter);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/check/{code}")
    public ResponseEntity<Void> checkCode(@PathVariable String code) {
        blockService.checkAndUtiliseByCode(code);
        return ResponseEntity.ok().build();
    }


    @PostMapping("/batch/send")
    public ResponseEntity<Map<String, String>> batchSend(@RequestBody List<String> blockIds) {
        return ResponseEntity.ok(blockService.checkAndUtiliseByBatch(blockIds));
    }
}
