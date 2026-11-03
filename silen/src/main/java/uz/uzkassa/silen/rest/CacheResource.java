package uz.uzkassa.silen.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.silen.service.CacheService;

import java.util.List;

@RestController
@RequestMapping("/api/cache")
@RequiredArgsConstructor
public class CacheResource {

    private final CacheService cacheService;

    @GetMapping
    public ResponseEntity<List<String>> getList() {
        return ResponseEntity.ok(cacheService.getList());
    }

    @DeleteMapping("/clear-all")
    public ResponseEntity<Void> clearAll() {
        cacheService.clearAll();
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/clear/by-name/{cacheName}")
    public ResponseEntity<Void> clear(@PathVariable String cacheName) {
        cacheService.clear(cacheName);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/evict/company/{tin}")
    public ResponseEntity<Void> evictCompany(@PathVariable String tin) {
        cacheService.evictCompany(tin);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/evict/{cacheName}/{cacheKey}")
    public ResponseEntity<Void> evict(@PathVariable String cacheName, @PathVariable String cacheKey) {
        cacheService.evict(cacheName, cacheKey);
        return ResponseEntity.ok().build();
    }
}
