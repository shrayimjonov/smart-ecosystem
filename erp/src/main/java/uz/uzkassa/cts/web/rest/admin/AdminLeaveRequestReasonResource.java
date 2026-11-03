package uz.uzkassa.cts.web.rest.admin;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.cts.constants.ApiConstants;
import uz.uzkassa.cts.dto.leaverequestreason.LeaveRequestReasonDTO;
import uz.uzkassa.cts.dto.leaverequestreason.LeaveRequestReasonDetailDTO;
import uz.uzkassa.cts.dto.leaverequestreason.LeaveRequestReasonItemDTO;
import uz.uzkassa.cts.dto.leaverequestreason.LeaveRequestReasonListDTO;
import uz.uzkassa.cts.enums.LeaveRequestReasonType;
import uz.uzkassa.cts.filter.LeaveRequestReasonFilter;
import uz.uzkassa.cts.service.LeaveRequestReasonService;

import java.util.EnumSet;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;


@RestController
@RequestMapping(ApiConstants.adminLeaveRequestReasonRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AdminLeaveRequestReasonResource {

    LeaveRequestReasonService leaveRequestReasonService;

    @PostMapping
    public ResponseEntity<Long> create(@RequestBody LeaveRequestReasonDTO LeaveRequestReasonDTO) {
        return ResponseEntity.ok(leaveRequestReasonService.create(LeaveRequestReasonDTO));
    }

    @PutMapping(ApiConstants.id)
    public ResponseEntity<Long> update(@PathVariable Long id, @RequestBody LeaveRequestReasonDTO LeaveRequestReasonDTO) {
        return ResponseEntity.ok(leaveRequestReasonService.update(id, LeaveRequestReasonDTO));
    }

    @GetMapping(ApiConstants.id)
    public ResponseEntity<LeaveRequestReasonDetailDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(leaveRequestReasonService.get(id));
    }

    @GetMapping
    public ResponseEntity<Page<LeaveRequestReasonListDTO>> getList(@ParameterObject LeaveRequestReasonFilter filter) {
        return ResponseEntity.ok(leaveRequestReasonService.getList(filter));
    }

    @GetMapping(ApiConstants.items)
    public ResponseEntity<List<LeaveRequestReasonItemDTO>> getItems() {
        return ResponseEntity.ok(leaveRequestReasonService.getItems());
    }

    @GetMapping(ApiConstants.types)
    public ResponseEntity<EnumSet<LeaveRequestReasonType>> getTypes() {
        return ResponseEntity.ok(EnumSet.allOf(LeaveRequestReasonType.class));
    }

    @DeleteMapping(ApiConstants.id)
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        leaveRequestReasonService.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
