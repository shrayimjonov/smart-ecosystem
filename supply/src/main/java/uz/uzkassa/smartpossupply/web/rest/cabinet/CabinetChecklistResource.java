package uz.uzkassa.smartpossupply.web.rest.cabinet;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.smartpossupply.constants.ApiConstants;
import uz.uzkassa.smartpossupply.dto.checklist.ChecklistDTO;
import uz.uzkassa.smartpossupply.dto.checklist.ChecklistDetailDTO;
import uz.uzkassa.smartpossupply.dto.checklist.ChecklistListDTO;
import uz.uzkassa.smartpossupply.filters.ChecklistFilter;
import uz.uzkassa.smartpossupply.service.ChecklistService;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.cabinetChecklistRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CabinetChecklistResource {

    ChecklistService checklistService;

    @PostMapping
    public ResponseEntity<Long> create(@Valid @RequestBody ChecklistDTO checklistDTO) {
        return ResponseEntity.ok(checklistService.create(checklistDTO));
    }

    @GetMapping(ApiConstants.id)
    public ResponseEntity<ChecklistDetailDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(checklistService.get(id));
    }

    @PutMapping(ApiConstants.id)
    public ResponseEntity<Long> update(@PathVariable Long id, @Valid @RequestBody ChecklistDTO checklistDTO) {
        return ResponseEntity.ok(checklistService.update(id, checklistDTO));
    }

    @GetMapping
    public ResponseEntity<Page<ChecklistListDTO>> getList(@ParameterObject ChecklistFilter filter) {
        return ResponseEntity.ok(checklistService.getList(filter));
    }
}
