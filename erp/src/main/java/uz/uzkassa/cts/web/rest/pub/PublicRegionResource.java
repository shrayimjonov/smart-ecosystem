package uz.uzkassa.cts.web.rest.pub;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.cts.constants.ApiConstants;
import uz.uzkassa.cts.constants.CacheConstants;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.service.RegionService;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.publicRegionRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class PublicRegionResource {

    RegionService regionService;

    @GetMapping(ApiConstants.items)
    @Cacheable(cacheNames = CacheConstants.REGION_ITEMS_CACHE)
    public ResponseEntity<List<CommonDTO>> getItems() {
        return ResponseEntity.ok(regionService.getItems());
    }
}
