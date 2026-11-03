package uz.uzkassa.horeca.web.rest.mobile;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.horeca.constants.ApiConstants;
import uz.uzkassa.horeca.dto.shift.ShiftDetailDTO;
import uz.uzkassa.horeca.service.ShiftService;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 14.11.2024
 */
@RestController
@RequestMapping(ApiConstants.mobileShiftRootApi)
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MobileShiftResource {

    ShiftService shiftService;

    @PatchMapping("/close")
    public ResponseEntity<Void> close() {
        shiftService.close();
        return ResponseEntity.ok().build();
    }

    @GetMapping("/current")
    public ResponseEntity<ShiftDetailDTO> getCurrentShift() {
        return ResponseEntity.ok(shiftService.getCurrentShift());
    }
}
