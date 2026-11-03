package uz.uzkassa.smartpos.web.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.smartpos.constants.CacheConstants;
import uz.uzkassa.smartpos.domain.postgres.common.Region;
import uz.uzkassa.smartpos.dto.FullNameDTO;
import uz.uzkassa.smartpos.dto.RegionDTO;
import uz.uzkassa.smartpos.dto.launcher.ActiveVersionDTO;
import uz.uzkassa.smartpos.dto.receipt.ReceiptDTO;
import uz.uzkassa.smartpos.dto.receipt.TransitReceiptDTO;
import uz.uzkassa.smartpos.repository.postgres.common.RegionRepository;
import uz.uzkassa.smartpos.service.LauncherService;
import uz.uzkassa.smartpos.service.ReceiptService;
import uz.uzkassa.smartpos.service.UserService;

import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/public")
@RequiredArgsConstructor
public class PublicResource {
    private static final String uuid = UUID.randomUUID().toString();
    private final ReceiptService receiptService;
    private final UserService userService;
    private final LauncherService launcherService;
    private final RegionRepository regionRepository;

    @GetMapping(value = "/receipt/{uid}")
    public ResponseEntity<ReceiptDTO> getByUid(@PathVariable("uid") String uid) {
        return ResponseEntity.ok(receiptService.getByUid(uid));
    }

    @GetMapping(value = "/receipt/hash")
    public ResponseEntity<ReceiptDTO> getByHash(@RequestParam String hash) {
        String uid = new String(Base64.getDecoder().decode(hash));
        return ResponseEntity.ok(receiptService.getByUid(uid));
    }

    @GetMapping("/transit/check/{facturaId}")
    public List<TransitReceiptDTO> checkTransitReceipt(@PathVariable String facturaId) {
        return receiptService.findAllReceiptsByFacturaId(facturaId);
    }

    @GetMapping(value = "/time")
    public LocalDateTime getTime() {
        return LocalDateTime.now();
    }

    @GetMapping("/manager")
    public FullNameDTO managerSearch(@RequestParam String managerLogin) {
        return userService.managerSearch(managerLogin);
    }

    @Cacheable(cacheNames = CacheConstants.REGION_CACHE)
    @GetMapping("/regions")
    public List<RegionDTO> getAllRegions() {
        return regionRepository.findAll().stream().map(Region::getDTO).toList();
    }

    @GetMapping("/version")
    public String getUuidVersion() {
        return uuid;
    }

    @GetMapping("/terminals/launcher/get-active-version")
    public ResponseEntity<ActiveVersionDTO> findAccount(@RequestParam(name = "packageName") final String packageName) {
        return ResponseEntity.ok(launcherService.getActiveVersion(packageName));
    }
}
