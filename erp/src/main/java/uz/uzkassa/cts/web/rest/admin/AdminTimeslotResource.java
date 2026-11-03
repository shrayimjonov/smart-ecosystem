package uz.uzkassa.cts.web.rest.admin;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.cts.constants.ApiConstants;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.dto.timeslot.TimeslotDTO;
import uz.uzkassa.cts.dto.timeslot.admin.TimeslotListAdminDTO;
import uz.uzkassa.cts.enums.CommonStatus;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.enums.TimeslotType;
import uz.uzkassa.cts.filter.TimeslotFilter;
import uz.uzkassa.cts.service.TimeslotService;

import java.util.EnumSet;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.adminTimeslotRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AdminTimeslotResource {

    TimeslotService timeslotService;

    @PostMapping
    public ResponseEntity<Long> create(@RequestBody TimeslotDTO timeslotDTO) {
        return ResponseEntity.ok(timeslotService.create(timeslotDTO));
    }

    @PutMapping(ApiConstants.id)
    public ResponseEntity<Long> update(@PathVariable Long id, @RequestBody TimeslotDTO timeslotDTO) {
        return ResponseEntity.ok(timeslotService.update(id, timeslotDTO));
    }

    @GetMapping("/round-the-clock")
    public ResponseEntity<TimeslotDTO> getRoundTheClock() {
        return ResponseEntity.ok(timeslotService.getRoundTheClock());
    }

    @GetMapping
    public ResponseEntity<Page<TimeslotListAdminDTO>> getList(@ParameterObject TimeslotFilter filter) {
        filter.setCompanyTimeslot(true);
        return ResponseEntity.ok(timeslotService.getListForAdmin(filter));
    }

    @GetMapping(ApiConstants.lookUp)
    public ResponseEntity<List<CommonDTO>> lookUp(@ParameterObject TimeslotFilter filter) {
        return ResponseEntity.ok(timeslotService.lookUp(filter));
    }

    @GetMapping(ApiConstants.id)
    public ResponseEntity<TimeslotDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(timeslotService.get(id));
    }

    @GetMapping("/{entityType}/{entityId}")
    public ResponseEntity<TimeslotDTO> getByEntityIdAndEntityType(@PathVariable EntityTypeEnum entityType, @PathVariable Long entityId) {
        return ResponseEntity.ok(timeslotService.getByEntityIdAndEntityType(entityId, entityType));
    }

    @GetMapping(ApiConstants.items)
    public ResponseEntity<List<CommonDTO>> getItems() {
        return ResponseEntity.ok(timeslotService.getItems());
    }

    @GetMapping(ApiConstants.types)
    public ResponseEntity<EnumSet<TimeslotType>> getTypes() {
        return ResponseEntity.ok(EnumSet.allOf(TimeslotType.class));
    }

    @PatchMapping(ApiConstants.updateStatus)
    public ResponseEntity<Void> updateStatus(@PathVariable Long id, @RequestParam("status") CommonStatus status) {
        timeslotService.updateStatus(id, status);
        return ResponseEntity.ok().build();
    }
}
