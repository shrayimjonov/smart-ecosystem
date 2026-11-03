package uz.uzkassa.smartpossupply.web.rest.mobile;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.smartpossupply.constants.ApiConstants;
import uz.uzkassa.smartpossupply.dto.checklist.ChecklistDetailDTO;
import uz.uzkassa.smartpossupply.enums.ChecklistStatus;
import uz.uzkassa.smartpossupply.service.ChecklistService;

import java.util.EnumSet;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.mobileChecklistRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class MobileChecklistResource {

    ChecklistService checklistService;

    @GetMapping("/supervisor/{supervisorId}")
    public ResponseEntity<ChecklistDetailDTO> getBySupervisor(@PathVariable Long supervisorId) {
        return ResponseEntity.ok(checklistService.getBySupervisor(supervisorId));
    }

    @PatchMapping(ApiConstants.check)
    public ResponseEntity<Void> check(@RequestParam("resultId") Long resultId,
                                      @RequestParam(value = "checked", required = false) Boolean checked) {
        checklistService.check(resultId, checked);
        return ResponseEntity.ok().build();
    }

    @PatchMapping(ApiConstants.updateStatus)
    public ResponseEntity<Void> updateStatus(@PathVariable Long id, @RequestParam("status") ChecklistStatus status) {
        checklistService.updateStatus(id, status);
        return ResponseEntity.ok().build();
    }

    @GetMapping(ApiConstants.statuses)
    public ResponseEntity<EnumSet<ChecklistStatus>> getStatuses() {
        return ResponseEntity.ok(EnumSet.allOf(ChecklistStatus.class));
    }
}
