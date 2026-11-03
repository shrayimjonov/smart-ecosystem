package uz.uzkassa.horeca.web.rest.admin;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.horeca.constants.ApiConstants;
import uz.uzkassa.horeca.repository.UserRepository;
import uz.uzkassa.horeca.service.CacheService;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PUBLIC;

@RestController
@RequestMapping(ApiConstants.adminCacheRootApi)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AdminCacheResource {

    CacheService cacheService;
    UserRepository userRepository;

    @GetMapping
    public ResponseEntity<List<String>> getList() {
        return ResponseEntity.ok(cacheService.getList());
    }

    @DeleteMapping("/{cacheName}")
    public ResponseEntity<Void> delete(@PathVariable String cacheName) {
        cacheService.clear(cacheName);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/user/{userId}")
    public ResponseEntity<Void> deleteUserCache(@PathVariable Long userId) {
        cacheService.clearUserCache(userRepository.findById(userId).orElse(null));
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/evict/{cacheName}/{cacheKey}")
    public ResponseEntity<Void> evict(@PathVariable String cacheName, String cacheKey) {
        cacheService.evictCache(cacheName, cacheKey);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/clear/{cacheName}")
    public ResponseEntity<Void> evict(@PathVariable String cacheName) {
        cacheService.clear(cacheName);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/all")
    public ResponseEntity<Void> deleteAll() {
        cacheService.clearAll();
        return ResponseEntity.ok().build();
    }
}
