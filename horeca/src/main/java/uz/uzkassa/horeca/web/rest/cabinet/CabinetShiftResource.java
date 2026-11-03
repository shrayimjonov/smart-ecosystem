package uz.uzkassa.horeca.web.rest.cabinet;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.horeca.constants.ApiConstants;
import uz.uzkassa.horeca.dto.shift.ShiftDetailDTO;
import uz.uzkassa.horeca.filters.BaseFilter;
import uz.uzkassa.horeca.service.ShiftService;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 14.11.2024
 */
@RestController
@RequestMapping(ApiConstants.cabinetShiftRootApi)
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CabinetShiftResource {

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

    @GetMapping
    public ResponseEntity<Page<ShiftDetailDTO>> getList(@ParameterObject BaseFilter filter) {
        return ResponseEntity.ok(shiftService.getList(filter));
    }
}
