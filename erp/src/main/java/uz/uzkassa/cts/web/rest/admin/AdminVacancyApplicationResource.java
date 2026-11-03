package uz.uzkassa.cts.web.rest.admin;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.cts.constants.ApiConstants;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.dto.vacancyapplication.VacancyApplicationDTO;
import uz.uzkassa.cts.dto.vacancyapplication.admin.VacancyApplicationDetailAdminDTO;
import uz.uzkassa.cts.dto.vacancyapplication.admin.VacancyApplicationListAdminDTO;
import uz.uzkassa.cts.enums.ContentType;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.enums.VacancyApplicationStatus;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.service.ExportReportService;
import uz.uzkassa.cts.service.VacancyApplicationService;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.adminVacancyApplicationRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AdminVacancyApplicationResource {

    VacancyApplicationService vacancyApplicationService;
    ExportReportService exportReportService;

    @PostMapping
    public ResponseEntity<Long> create(@RequestBody VacancyApplicationDTO vacancyApplicationDTO) {
        return ResponseEntity.ok(vacancyApplicationService.create(vacancyApplicationDTO));
    }

    @PutMapping(ApiConstants.id)
    public ResponseEntity<Long> update(@PathVariable Long id, @RequestBody VacancyApplicationDTO vacancyApplicationDTO) {
        return ResponseEntity.ok(vacancyApplicationService.update(id, vacancyApplicationDTO));
    }

    @GetMapping(ApiConstants.id)
    public ResponseEntity<VacancyApplicationDetailAdminDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(vacancyApplicationService.get(id));
    }

    @GetMapping
    public ResponseEntity<Page<VacancyApplicationListAdminDTO>> getList(@ParameterObject BaseFilter filter) {
        return ResponseEntity.ok(vacancyApplicationService.getListForAdmin(filter));
    }

    @GetMapping(ApiConstants.lookUp)
    public ResponseEntity<List<CommonDTO>> lookUp(@ParameterObject BaseFilter filter) {
        return ResponseEntity.ok(vacancyApplicationService.lookUp(filter));
    }

    @PatchMapping(ApiConstants.updateStatus)
    public ResponseEntity<Void> updateStatus(@PathVariable Long id,
                                             @RequestParam("status") VacancyApplicationStatus status,
                                             @RequestParam("reason") String reason) {
        vacancyApplicationService.updateStatus(id, status, reason);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/convert/{id}")
    public ResponseEntity<Long> convert(@PathVariable Long id) {
        return ResponseEntity.ok(vacancyApplicationService.convert(id));
    }

    @DeleteMapping(ApiConstants.id)
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        vacancyApplicationService.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping(ApiConstants.uploadFile)
    public ResponseEntity<Void> uploadFile(@RequestParam("file") MultipartFile file, @PathVariable Long id) {
        vacancyApplicationService.uploadFile(id, file);
        return ResponseEntity.ok().build();
    }

    @GetMapping(ApiConstants.exportExcel)
    public ResponseEntity<Void> exportToExcel(@ParameterObject BaseFilter filter) {
        exportReportService.export(filter, EntityTypeEnum.VACANCY_APPLICATION, ContentType.EXCEL);
        return ResponseEntity.ok().build();
    }

    @GetMapping(ApiConstants.exportCSV)
    public ResponseEntity<Void> exportToCSV(@ParameterObject BaseFilter filter) {
        exportReportService.export(filter, EntityTypeEnum.VACANCY_APPLICATION, ContentType.CSV);
        return ResponseEntity.ok().build();
    }
}
