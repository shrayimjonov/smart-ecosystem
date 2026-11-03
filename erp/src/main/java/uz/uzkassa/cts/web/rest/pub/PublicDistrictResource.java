package uz.uzkassa.cts.web.rest.pub;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.cts.constants.ApiConstants;
import uz.uzkassa.cts.constants.CacheConstants;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.service.DistrictService;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.publicDistrictRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class PublicDistrictResource {

    DistrictService districtService;

    @GetMapping("/items/{regionId}")
    @Cacheable(cacheNames = CacheConstants.DISTRICT_BY_REGION_ID_CACHE)
    public ResponseEntity<List<CommonDTO>> getItemsByRegionId(@PathVariable Long regionId) {
        return ResponseEntity.ok(districtService.getItemsByRegionId(regionId));
    }
}
