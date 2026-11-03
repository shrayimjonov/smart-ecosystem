package uz.uzkassa.cts.web.rest.admin;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.cts.constants.ApiConstants;
import uz.uzkassa.cts.dto.bonus.BillingBonusWebhookDTO;
import uz.uzkassa.cts.dto.bonus.BonusListDTO;
import uz.uzkassa.cts.dto.bonus.BonusListDetailByEmployeeDTO;
import uz.uzkassa.cts.dto.bonus.BonusStatisticsDTO;
import uz.uzkassa.cts.enums.ContentType;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.service.BonusService;
import uz.uzkassa.cts.service.ExportReportService;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.adminBonusRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AdminBonusResource {

    BonusService bonusService;
    ExportReportService exportReportService;

    @PostMapping(ApiConstants.batch)
    public ResponseEntity<Long> createBatch(@RequestBody List<BillingBonusWebhookDTO> bonusDTO) {
        bonusService.createBatch(bonusDTO);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<Page<BonusListDTO>> getList(@ParameterObject BaseFilter filter) {
        return ResponseEntity.ok(bonusService.getList(filter));
    }

    @GetMapping(value = "/employee/{employeeId}")
    public ResponseEntity<Page<BonusListDetailByEmployeeDTO>> getListByEmployee(@PathVariable Long employeeId, @ParameterObject BaseFilter filter) {
        return ResponseEntity.ok(bonusService.getListByEmployee(employeeId, filter));
    }

    @GetMapping(ApiConstants.statistics)
    public ResponseEntity<BonusStatisticsDTO> getStatistic(@ParameterObject BaseFilter filter) {
        return ResponseEntity.ok(bonusService.getStatistic(filter));
    }

    @GetMapping(ApiConstants.exportExcel)
    public ResponseEntity<Void> exportToExcel(@ParameterObject BaseFilter filter) {
        exportReportService.export(filter, EntityTypeEnum.BONUS, ContentType.EXCEL);
        return ResponseEntity.ok().build();
    }

    @GetMapping(ApiConstants.exportCSV)
    public ResponseEntity<Void> exportToCsv(@ParameterObject BaseFilter filter) {
        exportReportService.export(filter, EntityTypeEnum.BONUS, ContentType.CSV);
        return ResponseEntity.ok().build();
    }
}
