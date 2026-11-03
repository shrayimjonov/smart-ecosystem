package uz.uzkassa.cts.web.rest.admin;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.cts.constants.ApiConstants;
import uz.uzkassa.cts.dto.event.EventDTO;
import uz.uzkassa.cts.dto.event.admin.EventDetailAdminDTO;
import uz.uzkassa.cts.dto.event.admin.EventListAdminDTO;
import uz.uzkassa.cts.enums.EventStatus;
import uz.uzkassa.cts.filter.EventFilter;
import uz.uzkassa.cts.service.EventService;

import static lombok.AccessLevel.PRIVATE;

/**
 * Created date time: 26.12.2024 18:04
 */

@RestController
@RequestMapping(ApiConstants.adminEventRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AdminEventResource {

    EventService eventService;

    @PostMapping
    public ResponseEntity<Long> create(@RequestBody EventDTO eventDTO) {
        return ResponseEntity.ok(eventService.create(eventDTO));
    }

    @PutMapping(ApiConstants.id)
    public ResponseEntity<Long> update(@PathVariable Long id, @RequestBody EventDTO eventDTO) {
        return ResponseEntity.ok(eventService.update(id, eventDTO));
    }

    @GetMapping(ApiConstants.id)
    public ResponseEntity<EventDetailAdminDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(eventService.getForAdmin(id));
    }

    @GetMapping
    public ResponseEntity<Page<EventListAdminDTO>> getList(@ParameterObject EventFilter filter) {
        return ResponseEntity.ok(eventService.getListForAdmin(filter));
    }

    @PatchMapping(ApiConstants.updateStatus)
    public ResponseEntity<Void> updateStatus(@PathVariable Long id, @RequestParam EventStatus status) {
        eventService.updateStatus(id, status);
        return ResponseEntity.ok().build();
    }
}
