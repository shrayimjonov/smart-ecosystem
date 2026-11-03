package uz.uzkassa.smartpossupply.web.rest.pub;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.smartpossupply.constants.ApiConstants;
import uz.uzkassa.smartpossupply.dto.address.RegionDistrictDTO;
import uz.uzkassa.smartpossupply.service.RegionService;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;


@RestController
@RequestMapping(ApiConstants.publicRegionRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class PublicRegionResource {

    RegionService regionService;

    @Operation(summary = ApiConstants.Items)
    @GetMapping(ApiConstants.items)
    public ResponseEntity<List<RegionDistrictDTO>> getItems() {
        return ResponseEntity.ok(regionService.getItems());
    }

    @Operation(summary = ApiConstants.Sync)
    @PostMapping(ApiConstants.sync)
    public ResponseEntity<Void> sync() {
        regionService.syncRegions();
        return ResponseEntity.ok().build();
    }
}
