package uz.uzkassa.smartpos.web.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.smartpos.domain.postgres.company.Branch;
import uz.uzkassa.smartpos.domain.postgres.company.Company;
import uz.uzkassa.smartpos.enums.SyncType;
import uz.uzkassa.smartpos.service.*;
import uz.uzkassa.smartpos.utils.MarkingUtils;

import java.time.LocalDateTime;
import java.util.EnumSet;

import static uz.uzkassa.smartpos.security.AuthoritiesConstants.SMARTPOS_ADMIN;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CoreResource {
    private final ProductBranchService productBranchService;
    private final CategoryService categoryService;
    private final CacheManager cacheManager;
    private final BranchService branchService;
    private final UserService userService;
    private final CompanyService companyService;

    @GetMapping("/sync")
    public ResponseEntity<EnumSet<SyncType>> sync(@RequestParam LocalDateTime lastSyncDate) {
        boolean categoryChanged = categoryService.isCategoryChanged(lastSyncDate);
        if (categoryChanged) {
            return ResponseEntity.ok(EnumSet.of(SyncType.CATEGORY));
        }
        boolean productChanged = productBranchService.isProductChanged(lastSyncDate);
        if (productChanged) {
            return ResponseEntity.ok(EnumSet.of(SyncType.PRODUCT));
        }
        return ResponseEntity.ok().build();
    }

    @GetMapping("/clear-cache")
    public void clearUnitCache(@RequestParam String cacheName) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache != null) {
            cache.clear();
        }
    }

    @GetMapping("/clear-cache-key")
    public void clearUnitCache(@RequestParam String cacheName, @RequestParam String key) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache != null) {
            cache.evict(key);
        }
    }

    @GetMapping("/clear-marking-code")
    public ResponseEntity<String> clearMarkingCode(@RequestParam String markingCode) {
        return ResponseEntity.ok(MarkingUtils.clearWithProductGroup(markingCode, null));
    }


    @GetMapping("/branch/clear-cache")
    @Secured(SMARTPOS_ADMIN)
    public void clearBranchCache() {
        branchService.clearCache(Branch.class.getName());
    }


    @PutMapping("/user/clear-cache/{login}")
    public ResponseEntity<Void> clearUserToken(@PathVariable String login) {
        userService.clearCache(login);
        return ResponseEntity.ok().build();
    }


    @GetMapping("/company/clear-cache")
    @Secured(SMARTPOS_ADMIN)
    public void clearCompanyCache() {
        companyService.clearCache(Company.class.getName());
    }

}
