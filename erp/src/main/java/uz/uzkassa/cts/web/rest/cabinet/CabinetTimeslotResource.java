package uz.uzkassa.cts.web.rest.cabinet;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.cts.constants.ApiConstants;
import uz.uzkassa.cts.dto.timeslot.TimeslotDTO;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.service.TimeslotService;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.cabinetTimeslotRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CabinetTimeslotResource {

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

    @GetMapping(ApiConstants.id)
    public ResponseEntity<TimeslotDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(timeslotService.get(id));
    }

    @GetMapping("/{entityType}/{entityId}")
    public ResponseEntity<TimeslotDTO> getByEntityIdAndEntityType(@PathVariable EntityTypeEnum entityType, @PathVariable Long entityId) {
        return ResponseEntity.ok(timeslotService.getByEntityIdAndEntityType(entityId, entityType));
    }
}
