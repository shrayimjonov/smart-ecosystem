package uz.uzkassa.smartpossupply.web.rest.mobile;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.smartpossupply.constants.ApiConstants;
import uz.uzkassa.smartpossupply.dto.checlistquestionnaire.mobile.ChecklistQuestionAnswerMobileDTO;
import uz.uzkassa.smartpossupply.dto.checlistquestionnaire.mobile.ChecklistQuestionnaireListMobileDTO;
import uz.uzkassa.smartpossupply.service.ChecklistQuestionnaireService;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.mobileChecklistQuestionnaireRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class MobileChecklistQuestionResource {

    ChecklistQuestionnaireService checklistQuestionnaireService;

    @GetMapping
    public ResponseEntity<List<ChecklistQuestionnaireListMobileDTO>> getByChecklist(@RequestParam("checklistId") Long checklistId) {
        return ResponseEntity.ok(checklistQuestionnaireService.getByChecklist(checklistId));
    }

    @PostMapping(ApiConstants.id)
    public ResponseEntity<Void> answer(@PathVariable Long id, @RequestBody ChecklistQuestionAnswerMobileDTO checklistQuestionAnswerMobileDTO) {
        checklistQuestionnaireService.answer(id, checklistQuestionAnswerMobileDTO);
        return ResponseEntity.ok().build();
    }
}
