package uz.uzkassa.cts.web.rest.admin;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.cts.constants.ApiConstants;
import uz.uzkassa.cts.dto.integration.admin.IntegrationDTO;
import uz.uzkassa.cts.dto.integration.admin.IntegrationDetailAdminDTO;
import uz.uzkassa.cts.dto.integration.admin.IntegrationListAdminDTO;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.service.IntegrationService;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.adminIntegrationRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AdminIntegrationResource {

    IntegrationService integrationService;

    @PostMapping
    public ResponseEntity<Long> create(@RequestBody IntegrationDTO integrationDTO) {
        return ResponseEntity.ok(integrationService.create(integrationDTO));
    }

    @PutMapping(ApiConstants.id)
    public ResponseEntity<Long> update(@PathVariable Long id, @RequestBody IntegrationDTO integrationDTO) {
        return ResponseEntity.ok(integrationService.update(id, integrationDTO));
    }

    @GetMapping(ApiConstants.id)
    public ResponseEntity<IntegrationDetailAdminDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(integrationService.get(id));
    }

    @GetMapping
    public ResponseEntity<Page<IntegrationListAdminDTO>> getList(@ParameterObject BaseFilter filter) {
        return ResponseEntity.ok(integrationService.getListForAdmin(filter));
    }

    @DeleteMapping(ApiConstants.id)
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        integrationService.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping(ApiConstants.id + "/check")
    public ResponseEntity<Void> check(@PathVariable Long id) {
        integrationService.check(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping(ApiConstants.uploadFile)
    public ResponseEntity<Void> uploadLogo(@RequestParam("file") MultipartFile file, @PathVariable Long id) {
        integrationService.uploadLogo(id, file);
        return ResponseEntity.ok().build();
    }
}
