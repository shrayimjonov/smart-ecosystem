package uz.uzkassa.cts.web.rest.admin;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.cts.constants.ApiConstants;
import uz.uzkassa.cts.constants.CacheConstants;
import uz.uzkassa.cts.dto.vat.admin.VatAdminDTO;
import uz.uzkassa.cts.dto.vat.admin.VatDetailAdminDTO;
import uz.uzkassa.cts.dto.vat.admin.VatListAdminDTO;
import uz.uzkassa.cts.enums.CommonStatus;
import uz.uzkassa.cts.service.VatService;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.adminVatRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AdminVatResource {

    VatService vatService;

    @PostMapping
    public ResponseEntity<Long> create(@RequestBody VatAdminDTO vatDTO) {
        return ResponseEntity.ok(vatService.create(vatDTO));
    }

    @PutMapping(ApiConstants.id)
    public ResponseEntity<Long> update(@PathVariable Long id, @RequestBody VatAdminDTO vatDTO) {
        return ResponseEntity.ok(vatService.update(id, vatDTO));
    }

    @GetMapping(ApiConstants.id)
    public ResponseEntity<VatDetailAdminDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(vatService.get(id));
    }

    @GetMapping
    @Cacheable(cacheNames = CacheConstants.VAT_LIST_CACHE)
    public ResponseEntity<List<VatListAdminDTO>> getList() {
        return ResponseEntity.ok(vatService.getList());
    }

    @PatchMapping(ApiConstants.updateStatus)
    public ResponseEntity<Void> updateStatus(@PathVariable Long id, @RequestParam("status") CommonStatus status) {
        vatService.updateStatus(id, status);
        return ResponseEntity.ok().build();
    }
}
