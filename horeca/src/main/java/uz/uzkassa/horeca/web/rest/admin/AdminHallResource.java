package uz.uzkassa.horeca.web.rest.admin;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.horeca.constants.ApiConstants;
import uz.uzkassa.horeca.dto.base.CommonDTO;
import uz.uzkassa.horeca.dto.hall.HallDTO;
import uz.uzkassa.horeca.dto.hall.HallDetailDTO;
import uz.uzkassa.horeca.dto.hall.HallListDTO;
import uz.uzkassa.horeca.filters.BaseFilter;
import uz.uzkassa.horeca.interfaces.CrudRest;
import uz.uzkassa.horeca.interfaces.LookUpRest;
import uz.uzkassa.horeca.service.HallService;

import java.time.LocalDateTime;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.adminHallRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AdminHallResource
        implements CrudRest<Long, HallDTO, HallDetailDTO, HallListDTO, BaseFilter>, LookUpRest<CommonDTO, BaseFilter> {

    HallService hallService;

    @PostMapping
    @Override
    public ResponseEntity<Long> create(@RequestBody HallDTO hallDTO) {
        return ResponseEntity.ok(hallService.create(hallDTO));
    }

    @GetMapping(ApiConstants.id)
    @Override
    public ResponseEntity<HallDetailDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(hallService.get(id));
    }

    @GetMapping
    @Override
    public ResponseEntity<Page<HallListDTO>> getList(@ParameterObject BaseFilter filter) {
        return ResponseEntity.ok(hallService.getList(filter));
    }

    @PutMapping(ApiConstants.id)
    @Override
    public ResponseEntity<Long> update(@PathVariable Long id, @RequestBody HallDTO hallDTO) {
        return ResponseEntity.ok(hallService.update(id, hallDTO));
    }

    @DeleteMapping(ApiConstants.id)
    @Override
    public ResponseEntity<Void> delete(@PathVariable Long id, @RequestParam(required = false) LocalDateTime lastModifiedDate) {
        hallService.delete(id);
        return ResponseEntity.ok().build();
    }

    @Override
    @GetMapping(ApiConstants.lookUp)
    public ResponseEntity<List<CommonDTO>> lookUp(@ParameterObject BaseFilter filter) {
        return ResponseEntity.ok(hallService.lookUp(filter));
    }
}
