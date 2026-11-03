package uz.uzkassa.smartpossupply.web.rest.admin;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.smartpossupply.constants.ApiConstants;
import uz.uzkassa.smartpossupply.enums.EntityTypeEnum;
import uz.uzkassa.smartpossupply.service.CacheService;

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
    public ResponseEntity<Void> clearByEntityType(@PathVariable String entityType) {
        cacheService.clearByEntityType(EntityTypeEnum.valueOf(entityType));
        return ResponseEntity.ok().build();
    }
}
