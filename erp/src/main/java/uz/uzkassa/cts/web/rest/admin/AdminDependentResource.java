package uz.uzkassa.cts.web.rest.admin;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.cts.constants.ApiConstants;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.dto.dependent.DependentDTO;
import uz.uzkassa.cts.dto.dependent.DependentDetailAdminDTO;
import uz.uzkassa.cts.dto.dependent.DependentListAdminDTO;
import uz.uzkassa.cts.filter.DependentFilter;
import uz.uzkassa.cts.service.DependentService;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequiredArgsConstructor
@RequestMapping(ApiConstants.adminDependentRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class AdminDependentResource {

    DependentService dependentService;

    @PostMapping
    public ResponseEntity<Long> create(@RequestBody DependentDTO dependentDTO) {
        return ResponseEntity.ok(dependentService.create(dependentDTO));
    }

    @PutMapping(ApiConstants.id)
    public ResponseEntity<Long> update(@PathVariable Long id, @RequestBody DependentDTO dependentDTO) {
        return ResponseEntity.ok(dependentService.update(id, dependentDTO));
    }

    @GetMapping(ApiConstants.id)
    public ResponseEntity<DependentDetailAdminDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(dependentService.getForAdmin(id));
    }

    @GetMapping
    public ResponseEntity<Page<DependentListAdminDTO>> getList(@ParameterObject DependentFilter filter) {
        return ResponseEntity.ok(dependentService.getListForAdmin(filter));
    }

    @GetMapping(ApiConstants.lookUp)
    public ResponseEntity<List<CommonDTO>> lookUp(@ParameterObject DependentFilter filter) {
        return ResponseEntity.ok(dependentService.lookUp(filter));
    }
}
