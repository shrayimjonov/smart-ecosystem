package uz.uzkassa.smartpossupply.web.rest.cabinet;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.smartpossupply.constants.ApiConstants;
import uz.uzkassa.smartpossupply.dto.shift.ShiftDTO;
import uz.uzkassa.smartpossupply.service.ShiftService;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 27.12.2022 14:40
 */
@RestController
@RequestMapping(ApiConstants.shiftRootApi)
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CabinetShiftResource {

    ShiftService shiftService;

    @PostMapping
    public ResponseEntity<Long> create(@RequestBody ShiftDTO shiftDTO) {
        return ResponseEntity.ok(shiftService.create(shiftDTO));
    }

    @PutMapping("/close")
    public ResponseEntity<Void> close(@RequestParam Long fiscalNumber) {
        shiftService.close(fiscalNumber);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/close-session")
    public ResponseEntity<Void> closeSession() {
        shiftService.closeSession();
        return ResponseEntity.ok().build();
    }
}
