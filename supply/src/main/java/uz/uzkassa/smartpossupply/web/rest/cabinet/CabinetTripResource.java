package uz.uzkassa.smartpossupply.web.rest.cabinet;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.smartpossupply.constants.ApiConstants;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.dto.trip.TripSwapDTO;
import uz.uzkassa.smartpossupply.enums.TripStatus;
import uz.uzkassa.smartpossupply.service.TripService;

import java.util.EnumSet;
import java.util.List;

@RestController
@RequestMapping(ApiConstants.cabinetTripRootApi)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CabinetTripResource {

    TripService tripService;

    @GetMapping(ApiConstants.items)
    public ResponseEntity<List<CommonDTO>> getItems(@RequestParam("expeditorId") Long expeditorId,
                                                    @RequestParam List<TripStatus> statuses) {
        return ResponseEntity.ok(tripService.getItems(expeditorId, statuses));
    }

    @PatchMapping("/swap")
    public ResponseEntity<Void> swap(@RequestBody @Valid TripSwapDTO tripSwapDTO) {
        tripService.swap(tripSwapDTO);
        return ResponseEntity.ok().build();
    }

    @GetMapping(ApiConstants.statuses)
    public ResponseEntity<EnumSet<TripStatus>> getStatuses() {
        return ResponseEntity.ok(EnumSet.allOf(TripStatus.class));
    }
}
