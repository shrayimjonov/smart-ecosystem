package uz.uzkassa.cts.web.rest.pub;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.cts.constants.ApiConstants;
import uz.uzkassa.cts.constants.CacheConstants;
import uz.uzkassa.cts.dto.base.CommonCodeDTO;
import uz.uzkassa.cts.enums.ReferenceType;
import uz.uzkassa.cts.filter.ReferenceFilter;
import uz.uzkassa.cts.service.ReferenceService;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.publicReferenceRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class PublicReferenceResource {

    ReferenceService referenceService;

    @GetMapping(ApiConstants.lookUp)
    public ResponseEntity<List<CommonCodeDTO>> lookUp(@ParameterObject ReferenceFilter filter) {
        return ResponseEntity.ok(referenceService.lookUp(filter));
    }

    @GetMapping(ApiConstants.items + "/{type}")
    @Cacheable(cacheNames = CacheConstants.REFERENCE_ITEMS_BY_TYPE_CACHE)
    public ResponseEntity<List<CommonCodeDTO>> getItems(@PathVariable ReferenceType type) {
        return ResponseEntity.ok(referenceService.getItems(type));
    }
}
