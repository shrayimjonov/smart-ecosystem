package uz.uzkassa.horeca.web.rest.admin;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.horeca.constants.ApiConstants;
import uz.uzkassa.horeca.dto.hallemployee.HallEmployeeBatchDTO;
import uz.uzkassa.horeca.dto.hallemployee.HallEmployeeListDTO;
import uz.uzkassa.horeca.filters.BaseFilter;
import uz.uzkassa.horeca.service.HallEmployeeService;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.cabinetHallEmployeeRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AdminHallEmployeeResource {

    HallEmployeeService hallEmployeeService;

    @PostMapping(ApiConstants.batch)
    public ResponseEntity<Void> createOrUpdateBatch(@RequestBody HallEmployeeBatchDTO hallEmployeeBatchDTO) {
        hallEmployeeService.createOrUpdateBatch(hallEmployeeBatchDTO);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<HallEmployeeListDTO>> getList(@ParameterObject BaseFilter filter) {
        return ResponseEntity.ok(hallEmployeeService.getList(filter));
    }

    @DeleteMapping(ApiConstants.id)
    public ResponseEntity<Void> deleted(@PathVariable Long id) {
        hallEmployeeService.delete(id);
        return ResponseEntity.ok().build();
    }
}
