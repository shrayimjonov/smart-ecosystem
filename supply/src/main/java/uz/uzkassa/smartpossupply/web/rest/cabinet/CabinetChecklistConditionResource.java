package uz.uzkassa.smartpossupply.web.rest.cabinet;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.smartpossupply.constants.ApiConstants;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.service.ConditionService;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.cabinetChecklistConditionRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CabinetChecklistConditionResource {

    ConditionService conditionService;

    @PostMapping
    public ResponseEntity<Long> create(@RequestBody CommonDTO conditionDTO) {
        return ResponseEntity.ok(conditionService.create(conditionDTO));
    }

    @GetMapping(ApiConstants.id)
    public ResponseEntity<CommonDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(conditionService.get(id));
    }

    @PutMapping(ApiConstants.id)
    public ResponseEntity<Long> update(@PathVariable Long id, @RequestBody CommonDTO conditionDTO) {
        return ResponseEntity.ok(conditionService.update(id, conditionDTO));
    }

    @GetMapping(ApiConstants.items)
    public ResponseEntity<List<CommonDTO>> getItems() {
        return ResponseEntity.ok(conditionService.getItems());
    }
}
