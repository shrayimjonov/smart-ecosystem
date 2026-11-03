package uz.uzkassa.cts.web.rest.admin;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.cts.constants.ApiConstants;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.service.CacheService;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.adminCacheRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AdminCacheResource {

    CacheService cacheService;

    @GetMapping
    public ResponseEntity<List<String>> getList() {
        return ResponseEntity.ok(cacheService.getList());
    }

    @DeleteMapping("/clear/{cacheName}")
    public ResponseEntity<Void> clear(@PathVariable String cacheName) {
        cacheService.clear(cacheName);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/clear/batch")
    public ResponseEntity<Void> clearBatch() {
        cacheService.clearBatch();
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/evict/{cacheName}/{key}")
    public ResponseEntity<Void> evictByKey(@PathVariable String cacheName, @PathVariable String key) {
        cacheService.evict(cacheName, key);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/clear/entity-type/{entityType}")
    public ResponseEntity<Void> clearByEntityType(@PathVariable EntityTypeEnum entityType) {
        cacheService.clearByEntityType(entityType);
        return ResponseEntity.ok().build();
    }

}
