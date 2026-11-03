package uz.uzkassa.horeca.web.rest.admin;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.horeca.constants.ApiConstants;
import uz.uzkassa.horeca.dto.base.CommonDTO;
import uz.uzkassa.horeca.dto.department.DepartmentDTO;
import uz.uzkassa.horeca.dto.department.DepartmentDetailDTO;
import uz.uzkassa.horeca.dto.department.DepartmentListDTO;
import uz.uzkassa.horeca.enums.CommonStatus;
import uz.uzkassa.horeca.filters.BaseFilter;
import uz.uzkassa.horeca.interfaces.CrudRestAdvanced;
import uz.uzkassa.horeca.service.DepartmentService;

import java.time.LocalDateTime;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.adminDepartmentRootApi)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AdminDepartmentResource
        implements CrudRestAdvanced<Long, DepartmentDTO, DepartmentDetailDTO, DepartmentListDTO, BaseFilter, CommonStatus, CommonDTO> {

    DepartmentService departmentService;

    @PostMapping
    @Override
    public ResponseEntity<Long> create(@RequestBody DepartmentDTO departmentDTO) {
        return ResponseEntity.ok(departmentService.create(departmentDTO));
    }

    @GetMapping(ApiConstants.id)
    @Override
    public ResponseEntity<DepartmentDetailDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(departmentService.get(id));
    }

    @GetMapping
    @Override
    public ResponseEntity<Page<DepartmentListDTO>> getList(@ParameterObject BaseFilter filter) {
        return ResponseEntity.ok(departmentService.getList(filter));
    }

    @DeleteMapping(ApiConstants.id)
    @Override
    public ResponseEntity<Void> delete(@PathVariable Long id, @RequestParam(required = false) LocalDateTime lastModifiedDate) {
        departmentService.delete(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping(ApiConstants.id)
    @Override
    public ResponseEntity<Long> update(@PathVariable Long id, @RequestBody DepartmentDTO departmentDTO) {
        return ResponseEntity.ok(departmentService.update(id, departmentDTO));
    }

    @GetMapping(ApiConstants.lookUp)
    @Override
    public ResponseEntity<List<CommonDTO>> lookUp(@ParameterObject BaseFilter filter) {
        return ResponseEntity.ok(departmentService.lookUp(filter));
    }

    @PatchMapping(ApiConstants.updateStatus)
    @Override
    public ResponseEntity<Void> updateStatus(@PathVariable Long id, @RequestParam("status") CommonStatus status) {
        departmentService.updateStatus(id, status);
        return ResponseEntity.ok().build();
    }
}
