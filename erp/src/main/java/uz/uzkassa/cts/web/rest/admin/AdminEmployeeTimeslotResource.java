package uz.uzkassa.cts.web.rest.admin;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.cts.constants.ApiConstants;
import uz.uzkassa.cts.dto.employeetimeslot.EmployeeTimeslotDTO;
import uz.uzkassa.cts.dto.timeslot.TimeslotDTO;
import uz.uzkassa.cts.service.EmployeeTimeslotService;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.adminEmployeeTimeslotRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AdminEmployeeTimeslotResource {

    EmployeeTimeslotService employeeTimeslotService;

    @PostMapping
    public ResponseEntity<Void> create(@RequestBody EmployeeTimeslotDTO employeeTimeslotDTO) {
        employeeTimeslotService.create(employeeTimeslotDTO);
        return ResponseEntity.ok().build();
    }

    @PutMapping
    public ResponseEntity<Void> update(@RequestBody EmployeeTimeslotDTO employeeTimeslotDTO) {
        employeeTimeslotService.create(employeeTimeslotDTO);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{employeeId}")
    public ResponseEntity<TimeslotDTO> get(@PathVariable Long employeeId) {
        return ResponseEntity.ok(employeeTimeslotService.get(employeeId));
    }

    @DeleteMapping("/{employeeId}")
    public ResponseEntity<Void> delete(@PathVariable Long employeeId) {
        employeeTimeslotService.delete(employeeId);
        return ResponseEntity.ok().build();
    }
}
