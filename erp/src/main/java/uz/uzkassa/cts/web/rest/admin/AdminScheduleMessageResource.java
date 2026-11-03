package uz.uzkassa.cts.web.rest.admin;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.cts.constants.ApiConstants;
import uz.uzkassa.cts.dto.schedulemessage.ScheduleMessageDTO;
import uz.uzkassa.cts.dto.schedulemessage.ScheduleMessageDetailDTO;
import uz.uzkassa.cts.dto.schedulemessage.ScheduleMessageListDTO;
import uz.uzkassa.cts.enums.ScheduleMessageStatus;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.service.ScheduleMessageService;

import java.util.EnumSet;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.adminScheduleMessageRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AdminScheduleMessageResource {

    ScheduleMessageService scheduleMessageService;

    @PostMapping
    public ResponseEntity<Long> create(@RequestBody ScheduleMessageDTO scheduleMessageDTO) {
        return ResponseEntity.ok(scheduleMessageService.create(scheduleMessageDTO));
    }

    @PutMapping(ApiConstants.id)
    public ResponseEntity<Long> update(@PathVariable Long id, @RequestBody ScheduleMessageDTO scheduleMessageDTO) {
        return ResponseEntity.ok(scheduleMessageService.update(id, scheduleMessageDTO));
    }

    @GetMapping(ApiConstants.id)
    public ResponseEntity<ScheduleMessageDetailDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(scheduleMessageService.get(id));
    }

    @PatchMapping(ApiConstants.updateStatus)
    public ResponseEntity<Void> updateStatus(@PathVariable Long id, @RequestParam("status") ScheduleMessageStatus status) {
        scheduleMessageService.updateStatus(id, status);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping(ApiConstants.id)
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        scheduleMessageService.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<Page<ScheduleMessageListDTO>> getList(@ParameterObject BaseFilter filter) {
        return ResponseEntity.ok(scheduleMessageService.getList(filter));
    }

    @GetMapping(ApiConstants.statuses)
    public ResponseEntity<EnumSet<ScheduleMessageStatus>> getStatuses() {
        return ResponseEntity.ok(EnumSet.of(ScheduleMessageStatus.ACTIVE, ScheduleMessageStatus.IN_ACTIVE));
    }
}
