package uz.uzkassa.silen.rest;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.silen.dto.AggregationBatchDTO;
import uz.uzkassa.silen.dto.SelectItem;
import uz.uzkassa.silen.dto.filter.AggregationFilter;
import uz.uzkassa.silen.dto.marking.*;
import uz.uzkassa.silen.dto.redis.RedisLastAggregationMarksDTO;
import uz.uzkassa.silen.enumeration.*;
import uz.uzkassa.silen.integration.turon.ReportInfoResponse;
import uz.uzkassa.silen.service.AggregationService;
import uz.uzkassa.silen.service.MultiUnitAggregationService;
import uz.uzkassa.silen.service.ValidationService;
import uz.uzkassa.silen.service.job.SendJob;

import java.util.*;

/**
 * REST controller for managing {@link uz.uzkassa.silen.domain.Aggregation}.
 */
@Validated
@RestController
@RequestMapping("/api/aggregation")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AggregationResource {

    SendJob sendJob;
    AggregationService aggregationService;
    ValidationService validationService;
    MultiUnitAggregationService multiUnitAggregationService;
    static List<AggregationStatus> aggregationStatusList = Arrays.asList(AggregationStatus.CREATED, AggregationStatus.IN_PROGRESS,
            AggregationStatus.UPDATED, AggregationStatus.ERROR, AggregationStatus.DRAFT, AggregationStatus.SENT, AggregationStatus.APPLIED);

    @PostMapping
    public ResponseEntity<String> create(@RequestBody @Valid AggregationCreateDTO dto) {
        return ResponseEntity.ok(aggregationService.create(dto, false));
    }

    @PutMapping
    public ResponseEntity<String> update(@RequestParam("id") String id, @RequestBody @Valid AggregationUpdateDTO aggregationUpdateDTO) {
        return ResponseEntity.ok(aggregationService.update(id, aggregationUpdateDTO));
    }

    @GetMapping
    public ResponseEntity<Page<AggregationListDTO>> findAllByFilter(@ModelAttribute AggregationFilter filter) {
        filter.setAggregationStatusList(aggregationStatusList);
        return ResponseEntity.ok(aggregationService.findAllByFilter(filter));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AggregationDTO> get(@PathVariable String id) {
        return ResponseEntity.ok(aggregationService.findOne(id));
    }

    @GetMapping("/package-types")
    public ResponseEntity<List<SelectItem>> getPackageTypes() {
        return ResponseEntity.ok(Arrays.asList(PackageType.BOX.toSelectItem(), PackageType.PALLET.toSelectItem()));
    }

    @GetMapping("/types")
    public ResponseEntity<List<SelectItem>> getAggregationTypes() {
        return ResponseEntity.ok(AggregationType.getAll());
    }

    @GetMapping("/statuses")
    public ResponseEntity<List<SelectItem>> getStatuses() {
        return ResponseEntity.ok(AggregationStatus.getList());
    }

    @GetMapping("/stats")
    public ResponseEntity<AggregationStatsDTO> getStats(@ModelAttribute AggregationFilter filter) {
        filter.setAggregationStatusList(aggregationStatusList);
        return ResponseEntity.ok(aggregationService.getStats(filter));
    }

    @PutMapping("/validate")
    public ResponseEntity<String> validate(@RequestBody @Valid ValidationDTO dto) {
        return ResponseEntity.ok(validationService.validate(dto));
    }

    @PutMapping("/check-code")
    public ResponseEntity<RedisLastAggregationMarksDTO> checkCode(@RequestBody @Valid CodeDTO dto) {
        return ResponseEntity.ok(validationService.checkCode(dto));
    }

    @PutMapping("/clear-code")
    public ResponseEntity<Void> clearCode(@RequestBody @Valid CodeDTO dto) {
        validationService.clearCode(dto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/clear-all")
    @Secured({"ROLE_SUPERADMIN", "ROLE_ADMIN"})
    public ResponseEntity<Void> clearAll() {
        validationService.clearAll();
        return ResponseEntity.ok().build();
    }

    @GetMapping("/get-codes/{id}")
    public ResponseEntity<Set<String>> getCodes(@PathVariable String id, @RequestParam("unit") PackageType unit, @RequestParam("child") boolean child) {
        return ResponseEntity.ok(validationService.getCodes(id, unit, child));
    }

    @GetMapping("/get-session-codes/{id}")
    public ResponseEntity<List<String>> getCodes(@PathVariable String id, @RequestParam("sessionId") Long sessionId) {
        return ResponseEntity.ok(validationService.getSessionCodes(id, sessionId));
    }

    @PutMapping("/send/{id}")
    public ResponseEntity<Void> send(@PathVariable String id) {
        aggregationService.checkAndUtiliseByBatch(List.of(id));
        return ResponseEntity.ok().build();
    }

    @GetMapping("/print")
    public ResponseEntity<List<String>> getAllForPrint(@ModelAttribute AggregationFilter filter) {
        return ResponseEntity.ok(aggregationService.print(filter));
    }

    @PostMapping(value = "/validate/{fileType}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, String>> readFromFileAndValidate(@PathVariable("fileType") FileType fileType, @RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(validationService.readFromFileAndValidate(fileType, file));
    }

    @GetMapping(value = "/file-types")
    public ResponseEntity<EnumSet<FileType>> fileType() {
        return ResponseEntity.ok(EnumSet.allOf(FileType.class));
    }

    @PostMapping("/batch/send")
    public ResponseEntity<Map<String, String>> batchSend(@RequestBody List<String> aggregationIds) {
        return ResponseEntity.ok(aggregationService.checkAndUtiliseByBatch(aggregationIds));
    }

    @GetMapping("/export")
    public ResponseEntity<Void> export(AggregationFilter filter) {
        aggregationService.exportReport(filter);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/report/info/{reportId}")
    public ResponseEntity<ReportInfoResponse> getReportInfo(@PathVariable String reportId, @RequestParam(required = false, name = "organizationId") String organizationId) {
        return ResponseEntity.ok(aggregationService.getTuronReportInfo(reportId, organizationId));
    }

//    @GetMapping("/check/{code}")
//    public ResponseEntity<Void> checkCode(@PathVariable String code) {
//        aggregationService.checkAndUtiliseByCode(code);
//        return ResponseEntity.ok().build();
//    }

    @GetMapping("/start/cron")
    public ResponseEntity<Void> startCron(@ModelAttribute AggregationFilter filter) {
        aggregationService.checkAndResendByFilter(filter);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/migrate")
    public ResponseEntity<Void> startMigrate(@ModelAttribute AggregationFilter filter) {
        sendJob.checkFromRedis(filter);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/create-history")
    public ResponseEntity<Void> createHistoryFilter(@ModelAttribute AggregationFilter filter) {
//        aggregationService.createHistoryFilter(filter);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/batch/create")
    public ResponseEntity<String> checkCode(@RequestBody @Valid @Size(min = 1, message = "Добавьте минимум одну аггрегацию") List<AggregationBatchDTO> batchRedisList) {
        return ResponseEntity.ok(multiUnitAggregationService.batch(batchRedisList));
    }
}
