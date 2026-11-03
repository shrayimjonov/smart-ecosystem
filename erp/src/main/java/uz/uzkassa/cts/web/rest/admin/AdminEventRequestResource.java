package uz.uzkassa.cts.web.rest.admin;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.cts.constants.ApiConstants;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.dto.eventrequest.EventRequestDTO;
import uz.uzkassa.cts.dto.eventrequest.admin.EventRequestDetailAdminDTO;
import uz.uzkassa.cts.dto.eventrequest.admin.EventRequestListAdminDTO;
import uz.uzkassa.cts.enums.ApproverCommonStatus;
import uz.uzkassa.cts.filter.EventRequestFilter;
import uz.uzkassa.cts.service.EventRequestService;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

/**
 * Created date time: 26.12.2024 13:59
 */

@RestController
@RequestMapping(ApiConstants.adminEventRequestRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AdminEventRequestResource {

    EventRequestService eventRequestService;

    @PostMapping
    public ResponseEntity<Long> create(@RequestBody EventRequestDTO eventRequestDTO) {
        return ResponseEntity.ok(eventRequestService.create(eventRequestDTO));
    }

    @PutMapping(ApiConstants.id)
    public ResponseEntity<Long> update(@PathVariable Long id, @RequestBody EventRequestDTO eventRequestDTO) {
        return ResponseEntity.ok(eventRequestService.update(id, eventRequestDTO));
    }

    @GetMapping(ApiConstants.id)
    public ResponseEntity<EventRequestDetailAdminDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(eventRequestService.getForAdmin(id));
    }

    @GetMapping
    public ResponseEntity<Page<EventRequestListAdminDTO>> getList(@ParameterObject EventRequestFilter filter) {
        return ResponseEntity.ok(eventRequestService.getListForAdmin(filter));
    }

    @GetMapping(ApiConstants.lookUp)
    public ResponseEntity<List<CommonDTO>> lookUp(@ParameterObject EventRequestFilter filter) {
        return ResponseEntity.ok(eventRequestService.lookUp(filter));
    }

    @PatchMapping(ApiConstants.updateStatus)
    public ResponseEntity<Void> updateStatus(@PathVariable Long id, @RequestParam ApproverCommonStatus status) {
        eventRequestService.updateStatus(id, status);
        return ResponseEntity.ok().build();
    }
}
