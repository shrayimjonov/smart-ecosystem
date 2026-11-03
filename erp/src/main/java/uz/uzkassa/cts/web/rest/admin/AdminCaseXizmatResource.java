package uz.uzkassa.cts.web.rest.admin;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.cts.constants.ApiConstants;
import uz.uzkassa.cts.dto.caseXizmat.CaseXizmatDTO;
import uz.uzkassa.cts.dto.caseXizmat.CaseXizmatDetailAdminDTO;
import uz.uzkassa.cts.dto.caseXizmat.CaseXizmatListAdminDTO;
import uz.uzkassa.cts.enums.CommonStatus;
import uz.uzkassa.cts.enums.FMReportReason;
import uz.uzkassa.cts.filter.CaseXizmatFilter;
import uz.uzkassa.cts.service.CaseXizmatService;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.adminCaseXizmatRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AdminCaseXizmatResource {

    CaseXizmatService caseXizmatService;

    @GetMapping
    public ResponseEntity<Page<CaseXizmatListAdminDTO>> getList(@ParameterObject CaseXizmatFilter filter) {
        return ResponseEntity.ok(caseXizmatService.getListForAdmin(filter));
    }

    @PostMapping
    public ResponseEntity<Long> create(@RequestBody CaseXizmatDTO caseTypeXizmatDTO) {
        return ResponseEntity.ok(caseXizmatService.create(caseTypeXizmatDTO));
    }

    @PutMapping(ApiConstants.id)
    public ResponseEntity<Long> update(@PathVariable Long id, @RequestBody CaseXizmatDTO caseTypeXizmatDTO) {
        return ResponseEntity.ok(caseXizmatService.update(id, caseTypeXizmatDTO));
    }

    @GetMapping(ApiConstants.id)
    public ResponseEntity<CaseXizmatDetailAdminDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(caseXizmatService.getForAdmin(id));
    }

    @PatchMapping(ApiConstants.updateStatus)
    public ResponseEntity<Void> updateStatus(@PathVariable Long id, @RequestParam("status") CommonStatus status) {
        caseXizmatService.updateStatus(id, status);
        return ResponseEntity.ok().build();
    }

    @PatchMapping(value = "/{id}/reason")
    public ResponseEntity<Void> updateReason(@PathVariable Long id, @RequestParam("reason") FMReportReason reason) {
        caseXizmatService.updateReason(id, reason);
        return ResponseEntity.ok().build();
    }
}
