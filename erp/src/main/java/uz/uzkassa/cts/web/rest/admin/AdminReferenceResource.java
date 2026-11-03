package uz.uzkassa.cts.web.rest.admin;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.cts.constants.ApiConstants;
import uz.uzkassa.cts.dto.reference.ReferenceDTO;
import uz.uzkassa.cts.dto.reference.admin.ReferenceDetailAdminDTO;
import uz.uzkassa.cts.dto.reference.admin.ReferenceListAdminDTO;
import uz.uzkassa.cts.enums.CommonStatus;
import uz.uzkassa.cts.filter.ReferenceFilter;
import uz.uzkassa.cts.service.ReferenceService;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.adminReferenceRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AdminReferenceResource {

    ReferenceService referenceService;

    @PostMapping
    public ResponseEntity<Long> create(@RequestBody ReferenceDTO referenceDTO) {
        return ResponseEntity.ok(referenceService.create(referenceDTO));
    }

    @PutMapping(ApiConstants.id)
    public ResponseEntity<Long> update(@PathVariable Long id, @RequestBody ReferenceDTO referenceDTO) {
        return ResponseEntity.ok(referenceService.update(id, referenceDTO));
    }

    @GetMapping(ApiConstants.id)
    public ResponseEntity<ReferenceDetailAdminDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(referenceService.get(id));
    }

    @GetMapping
    public ResponseEntity<Page<ReferenceListAdminDTO>> getList(@ParameterObject ReferenceFilter filter) {
        return ResponseEntity.ok(referenceService.getListForAdmin(filter));
    }

    @PatchMapping(ApiConstants.updateStatus)
    public ResponseEntity<Void> updateStatus(@PathVariable Long id, @RequestParam("status") CommonStatus status) {
        referenceService.updateStatus(id, status);
        return ResponseEntity.ok().build();
    }
}
