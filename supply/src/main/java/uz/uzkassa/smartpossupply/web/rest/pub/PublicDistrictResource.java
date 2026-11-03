package uz.uzkassa.smartpossupply.web.rest.pub;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.smartpossupply.constants.ApiConstants;
import uz.uzkassa.smartpossupply.dto.address.RegionDistrictDTO;
import uz.uzkassa.smartpossupply.service.DistrictService;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.publicDistrictRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class PublicDistrictResource {

    DistrictService districtService;

    @Operation(summary = ApiConstants.Items)
    @GetMapping("/items/{regionId}")
    public ResponseEntity<List<RegionDistrictDTO>> getItemsByRegionId(@PathVariable Long regionId) {
        return ResponseEntity.ok(districtService.getItemsByRegionId(regionId));
    }

    @Operation(summary = ApiConstants.Sync)
    @PostMapping(ApiConstants.sync)
    public ResponseEntity<Void> sync() {
        districtService.syncDistricts();
        return ResponseEntity.ok().build();
    }
}
