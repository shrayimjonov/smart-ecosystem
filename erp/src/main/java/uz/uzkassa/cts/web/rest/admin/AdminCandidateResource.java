package uz.uzkassa.cts.web.rest.admin;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.cts.constants.ApiConstants;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.dto.candidate.CandidateDTO;
import uz.uzkassa.cts.dto.candidate.CandidateDetailAdminDTO;
import uz.uzkassa.cts.dto.candidate.CandidateListAdminDTO;
import uz.uzkassa.cts.enums.CandidateStatus;
import uz.uzkassa.cts.enums.ContentType;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.service.CandidateService;
import uz.uzkassa.cts.service.ExportReportService;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequiredArgsConstructor
@RequestMapping(ApiConstants.adminCandidateRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class AdminCandidateResource {

    CandidateService candidateService;
    ExportReportService exportReportService;

    @PostMapping
    public ResponseEntity<Long> create(@RequestBody CandidateDTO candidateDTO) {
        return ResponseEntity.ok(candidateService.create(candidateDTO));
    }

    @PutMapping(ApiConstants.id)
    public ResponseEntity<Long> update(@PathVariable Long id, @RequestBody CandidateDTO candidateDTO) {
        return ResponseEntity.ok(candidateService.update(id, candidateDTO));
    }

    @GetMapping(ApiConstants.id)
    public ResponseEntity<CandidateDetailAdminDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(candidateService.getForAdmin(id));
    }

    @GetMapping
    public ResponseEntity<Page<CandidateListAdminDTO>> getList(@ParameterObject BaseFilter filter) {
        return ResponseEntity.ok(candidateService.getListForAdmin(filter));
    }

    @GetMapping(ApiConstants.lookUp)
    public ResponseEntity<List<CommonDTO>> lookUp(@ParameterObject BaseFilter filter) {
        return ResponseEntity.ok(candidateService.lookUp(filter));
    }

    @PatchMapping(ApiConstants.updateStatus)
    public ResponseEntity<Void> updateStatus(@PathVariable Long id, @RequestParam("status") CandidateStatus status) {
        candidateService.updateStatus(id, status);
        return ResponseEntity.ok().build();
    }

    @GetMapping(ApiConstants.exportExcel)
    public ResponseEntity<Void> exportToExcel(@ParameterObject BaseFilter filter) {
        exportReportService.export(filter, EntityTypeEnum.CANDIDATE, ContentType.EXCEL);
        return ResponseEntity.ok().build();
    }

    @GetMapping(ApiConstants.exportCSV)
    public ResponseEntity<Void> exportToCSV(@ParameterObject BaseFilter filter) {
        exportReportService.export(filter, EntityTypeEnum.CANDIDATE, ContentType.CSV);
        return ResponseEntity.ok().build();
    }
}
