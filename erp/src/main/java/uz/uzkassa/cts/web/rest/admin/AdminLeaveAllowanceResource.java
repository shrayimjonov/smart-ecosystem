package uz.uzkassa.cts.web.rest.admin;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.cts.constants.ApiConstants;
import uz.uzkassa.cts.dto.leaveallowance.LeaveAllowanceDTO;
import uz.uzkassa.cts.dto.leaveallowance.LeaveAllowanceDetailDTO;
import uz.uzkassa.cts.dto.leaveallowance.LeaveAllowanceListDTO;
import uz.uzkassa.cts.filter.LeaveAllowanceFilter;
import uz.uzkassa.cts.service.LeaveAllowanceService;

import static lombok.AccessLevel.PRIVATE;


@RestController
@RequestMapping(ApiConstants.adminLeaveAllowanceRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AdminLeaveAllowanceResource {

    LeaveAllowanceService leaveAllowanceService;

    @PostMapping
    public ResponseEntity<Long> create(@RequestBody LeaveAllowanceDTO leaveAllowanceDTO) {
        return ResponseEntity.ok(leaveAllowanceService.create(leaveAllowanceDTO));
    }

    @PutMapping(ApiConstants.id)
    public ResponseEntity<Long> update(@PathVariable Long id, @RequestBody LeaveAllowanceDTO leaveAllowanceDTO) {
        return ResponseEntity.ok(leaveAllowanceService.update(id, leaveAllowanceDTO));
    }

    @GetMapping(ApiConstants.id)
    public ResponseEntity<LeaveAllowanceDetailDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(leaveAllowanceService.get(id));
    }

    @GetMapping
    public ResponseEntity<Page<LeaveAllowanceListDTO>> getList(@ParameterObject LeaveAllowanceFilter filter) {
        return ResponseEntity.ok(leaveAllowanceService.getList(filter));
    }

    @DeleteMapping(ApiConstants.id)
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        leaveAllowanceService.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
