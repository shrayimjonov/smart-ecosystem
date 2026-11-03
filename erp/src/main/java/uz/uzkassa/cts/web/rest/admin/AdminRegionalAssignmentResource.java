package uz.uzkassa.cts.web.rest.admin;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.cts.constants.ApiConstants;
import uz.uzkassa.cts.dto.regionalassignment.RegionalAssignmentDTO;
import uz.uzkassa.cts.dto.regionalassignment.RegionalAssignmentDetailDTO;
import uz.uzkassa.cts.dto.regionalassignment.RegionalAssignmentListDTO;
import uz.uzkassa.cts.enums.CommonStatus;
import uz.uzkassa.cts.filter.RegionalAssignmentFilter;
import uz.uzkassa.cts.service.RegionalAssignmentService;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.adminRegionalAssignmentRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AdminRegionalAssignmentResource {

    RegionalAssignmentService regionalAssignmentService;

    @PostMapping
    public ResponseEntity<Long> create(@RequestBody RegionalAssignmentDTO regionalAssignmentDTO) {
        return ResponseEntity.ok(regionalAssignmentService.create(regionalAssignmentDTO));
    }

    @PutMapping(ApiConstants.id)
    public ResponseEntity<Long> update(@PathVariable Long id, @RequestBody RegionalAssignmentDTO regionalAssignmentDTO) {
        return ResponseEntity.ok(regionalAssignmentService.update(id,regionalAssignmentDTO));
    }

    @GetMapping
    public ResponseEntity<Page<RegionalAssignmentListDTO>> getList(@ParameterObject RegionalAssignmentFilter filter) {
        return ResponseEntity.ok(regionalAssignmentService.getList(filter));
    }

    @GetMapping(ApiConstants.id)
    public ResponseEntity<RegionalAssignmentDetailDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(regionalAssignmentService.get(id));
    }

    @PatchMapping(ApiConstants.id)
    public ResponseEntity<Void> updateStatus(@PathVariable Long id, @RequestParam CommonStatus status) {
        regionalAssignmentService.updateStatus(id, status);
        return ResponseEntity.noContent().build();
    }
}
