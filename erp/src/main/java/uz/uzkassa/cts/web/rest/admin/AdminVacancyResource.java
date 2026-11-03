package uz.uzkassa.cts.web.rest.admin;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.cts.constants.ApiConstants;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.dto.vacancy.VacancyDTO;
import uz.uzkassa.cts.dto.vacancy.VacancyDetailAdminDTO;
import uz.uzkassa.cts.dto.vacancy.VacancyListAdminDTO;
import uz.uzkassa.cts.enums.ContentType;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.enums.VacancyStatus;
import uz.uzkassa.cts.filter.VacancyFilter;
import uz.uzkassa.cts.service.ExportReportService;
import uz.uzkassa.cts.service.VacancyService;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.adminVacancyRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AdminVacancyResource {

    VacancyService vacancyService;
    ExportReportService exportReportService;

    @PostMapping
    public ResponseEntity<Long> create(@RequestBody VacancyDTO vacancyDTO) {
        return ResponseEntity.ok(vacancyService.create(vacancyDTO));
    }

    @PutMapping(ApiConstants.id)
    public ResponseEntity<Long> update(@PathVariable Long id, @RequestBody VacancyDTO vacancyDTO) {
        return ResponseEntity.ok(vacancyService.update(id, vacancyDTO));
    }

    @GetMapping(ApiConstants.id)
    public ResponseEntity<VacancyDetailAdminDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(vacancyService.get(id));
    }

    @GetMapping
    public ResponseEntity<Page<VacancyListAdminDTO>> getList(@ParameterObject VacancyFilter filter) {
        return ResponseEntity.ok(vacancyService.getListForAdmin(filter));
    }

    @GetMapping(ApiConstants.lookUp)
    public ResponseEntity<List<CommonDTO>> lookUp(@ParameterObject VacancyFilter filter) {
        return ResponseEntity.ok(vacancyService.lookUp(filter));
    }

    @PatchMapping(ApiConstants.updateStatus)
    public ResponseEntity<Void> updateStatus(@PathVariable Long id, @RequestParam("status") VacancyStatus status) {
        vacancyService.updateStatus(id, status);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping(ApiConstants.id)
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        vacancyService.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping(ApiConstants.exportExcel)
    public ResponseEntity<Void> exportToExcel(@ParameterObject VacancyFilter filter) {
        exportReportService.export(filter, EntityTypeEnum.VACANCY, ContentType.EXCEL);
        return ResponseEntity.ok().build();
    }

    @GetMapping(ApiConstants.exportCSV)
    public ResponseEntity<Void> exportToCSV(@ParameterObject VacancyFilter filter) {
        exportReportService.export(filter, EntityTypeEnum.VACANCY, ContentType.CSV);
        return ResponseEntity.ok().build();
    }

    @PatchMapping(ApiConstants.assignHr)
    public ResponseEntity<Void> assignHr(@PathVariable Long id, @RequestParam Long hrId) {
        vacancyService.assignVacancyToHr(id, hrId);
        return ResponseEntity.ok().build();
    }
}
