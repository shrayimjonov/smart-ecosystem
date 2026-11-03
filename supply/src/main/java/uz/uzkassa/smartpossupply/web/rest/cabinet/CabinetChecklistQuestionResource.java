package uz.uzkassa.smartpossupply.web.rest.cabinet;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.smartpossupply.constants.ApiConstants;
import uz.uzkassa.smartpossupply.dto.checlistquestionnaire.ChecklistQuestionnaireBatchDTO;
import uz.uzkassa.smartpossupply.dto.checlistquestionnaire.ChecklistQuestionnaireDTO;
import uz.uzkassa.smartpossupply.dto.checlistquestionnaire.ChecklistQuestionnaireDetailDTO;
import uz.uzkassa.smartpossupply.dto.checlistquestionnaire.ChecklistQuestionnaireListDTO;
import uz.uzkassa.smartpossupply.service.ChecklistQuestionnaireService;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.cabinetChecklistQuestionnaireRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CabinetChecklistQuestionResource {

    ChecklistQuestionnaireService checklistQuestionnaireService;

    @PostMapping
    public ResponseEntity<Long> create(@Valid @RequestBody ChecklistQuestionnaireDTO checklistQuestionnaireDTO) {
        return ResponseEntity.ok(checklistQuestionnaireService.create(checklistQuestionnaireDTO));
    }

    @PostMapping(ApiConstants.batch)
    public ResponseEntity<Void> createBatch(@Valid @RequestBody ChecklistQuestionnaireBatchDTO checklistQuestionnaireBatchDTO) {
        checklistQuestionnaireService.createBatch(checklistQuestionnaireBatchDTO);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping(ApiConstants.batch)
    public ResponseEntity<Void> deleteBatch(@RequestBody List<Long> ids) {
        checklistQuestionnaireService.deleteBatch(ids);
        return ResponseEntity.ok().build();
    }

    @GetMapping(ApiConstants.id)
    public ResponseEntity<ChecklistQuestionnaireDetailDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(checklistQuestionnaireService.get(id));
    }

    @PutMapping(ApiConstants.id)
    public ResponseEntity<Long> update(@PathVariable Long id, @Valid @RequestBody ChecklistQuestionnaireDTO checklistQuestionnaireDTO) {
        return ResponseEntity.ok(checklistQuestionnaireService.update(id, checklistQuestionnaireDTO));
    }

    @GetMapping
    public ResponseEntity<List<ChecklistQuestionnaireListDTO>> getList(@RequestParam("checklistId") Long checklistId) {
        return ResponseEntity.ok(checklistQuestionnaireService.getList(checklistId));
    }
}
