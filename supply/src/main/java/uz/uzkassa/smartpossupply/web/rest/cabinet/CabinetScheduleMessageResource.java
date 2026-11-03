package uz.uzkassa.smartpossupply.web.rest.cabinet;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.smartpossupply.constants.ApiConstants;
import uz.uzkassa.smartpossupply.dto.schedulemessage.ScheduleMessageDTO;
import uz.uzkassa.smartpossupply.dto.schedulemessage.ScheduleMessageDetailDTO;
import uz.uzkassa.smartpossupply.dto.schedulemessage.ScheduleMessageListDTO;
import uz.uzkassa.smartpossupply.enums.ScheduleMessageStatus;
import uz.uzkassa.smartpossupply.filters.BaseFilter;
import uz.uzkassa.smartpossupply.service.ScheduleMessageService;

import java.util.EnumSet;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.cabinetScheduleMessageRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CabinetScheduleMessageResource {

    ScheduleMessageService scheduleMessageService;

    @PostMapping
    public ResponseEntity<Long> create(@Valid @RequestBody ScheduleMessageDTO scheduleMessageDTO) {
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
