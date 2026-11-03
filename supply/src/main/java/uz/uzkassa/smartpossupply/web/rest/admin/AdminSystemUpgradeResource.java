package uz.uzkassa.smartpossupply.web.rest.admin;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.smartpossupply.constants.ApiConstants;
import uz.uzkassa.smartpossupply.dto.systemupgrade.SystemUpgradeDTO;
import uz.uzkassa.smartpossupply.dto.systemupgrade.SystemUpgradeDetailDTO;
import uz.uzkassa.smartpossupply.filters.SystemUpgradeFilter;
import uz.uzkassa.smartpossupply.service.SystemUpgradeService;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 04.10.2024
 */
@RestController
@RequestMapping(ApiConstants.adminSystemUpgradeRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AdminSystemUpgradeResource {

    SystemUpgradeService systemUpgradeService;

    @PostMapping
    public ResponseEntity<Long> create(@RequestBody SystemUpgradeDTO systemUpgradeDTO) {
        return ResponseEntity.ok(systemUpgradeService.create(systemUpgradeDTO));
    }

    @GetMapping
    public ResponseEntity<Page<SystemUpgradeDTO>> getList(@ParameterObject SystemUpgradeFilter filter) {
        return ResponseEntity.ok(systemUpgradeService.getList(filter));
    }

    @PutMapping(ApiConstants.id)
    public ResponseEntity<Long> update(@PathVariable Long id, @RequestBody SystemUpgradeDTO systemUpgradeDTO) {
        return ResponseEntity.ok(systemUpgradeService.update(id, systemUpgradeDTO));
    }

    @GetMapping("/available-versions")
    public ResponseEntity<List<SystemUpgradeDetailDTO>> getAvailableVersions(@ParameterObject SystemUpgradeFilter filter) {
        return ResponseEntity.ok(systemUpgradeService.getAvailableVersions(filter));
    }

    @PatchMapping("/switch/{id}")
    public ResponseEntity<Void> updateStatus(@PathVariable Long id) {
        systemUpgradeService.updateStatus(id);
        return ResponseEntity.ok().build();
    }
}
