package uz.uzkassa.cts.web.rest.admin;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.cts.constants.ApiConstants;
import uz.uzkassa.cts.service.NeighborhoodService;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.adminNeighborhoodRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AdminNeighborhoodResource {

    NeighborhoodService neighborhoodService;

    @GetMapping("/sync")
    public ResponseEntity<Void> sync() {
        neighborhoodService.sync();
        return ResponseEntity.ok().build();
    }

    @GetMapping("/sync/{districtId}")
    public ResponseEntity<Void> syncByDistrict(@PathVariable Long districtId) {
        neighborhoodService.syncByDistrict(districtId);
        return ResponseEntity.ok().build();
    }
}
