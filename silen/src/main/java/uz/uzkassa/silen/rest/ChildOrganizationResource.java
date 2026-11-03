package uz.uzkassa.silen.rest;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.silen.dto.*;
import uz.uzkassa.silen.dto.filter.GivePermissionFilter;
import uz.uzkassa.silen.dto.filter.OrganizationFilter;
import uz.uzkassa.silen.enumeration.ServiceType;
import uz.uzkassa.silen.service.OrganizationService;

import java.util.List;

/**
 * REST controller for managing {@link uz.uzkassa.silen.domain.Organization}.
 */
@RestController
@RequestMapping("/api/organization/children")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ChildOrganizationResource {
    OrganizationService organizationService;

    @PostMapping
    public ResponseEntity<String> create(@RequestBody @Valid ChildOrganizationDTO childOrganizationDTO) {
        return ResponseEntity.ok(organizationService.setParent(childOrganizationDTO));
    }

    @GetMapping
    public ResponseEntity<List<SelectItem>> getAll(@ModelAttribute @Valid OrganizationFilter filter) {
        filter.setWithoutParent(true);
        filter.setActive(true);
        filter.setWithoutChild(true);
        return ResponseEntity.ok(organizationService.getItems(filter));
    }

    @GetMapping("/items")
    public ResponseEntity<List<ChildOrganizationInfoDTO>> getChildrenByFilter(@ModelAttribute @Valid OrganizationFilter filter) {
        return ResponseEntity.ok(organizationService.getChildren(filter));
    }

    @GetMapping("/page")
    public ResponseEntity<Page<OrganizationDTO>> getChildrenByParentId(@ModelAttribute @Valid OrganizationFilter filter) {
        return ResponseEntity.ok(organizationService.getChildrenPage(filter));
    }

    @DeleteMapping("/{childId}")
    public ResponseEntity<Void> delete(@PathVariable String childId) {
        organizationService.delete(childId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/give-permission")
    public ResponseEntity<GivePermissionDTO> givePermission(@RequestBody @Valid GivePermissionDTO permissionDTO) {
        return ResponseEntity.ok(organizationService.givePermission(permissionDTO));
    }

    @GetMapping("/give-permission")
    public ResponseEntity<Page<GivePermissionDTO>> givingPermissionHistory(@ModelAttribute @Valid GivePermissionFilter filter) {
        return ResponseEntity.ok(organizationService.getChildrenGivePermissionHistory(filter));
    }

    @GetMapping("/give-permission/stats")
    public ResponseEntity<Long> givingPermissionHistoryStats(@ModelAttribute @Valid GivePermissionFilter filter) {
        return ResponseEntity.ok(organizationService.getChildrenGivePermissionStats(filter));
    }

    @GetMapping("/give-permission/types")
    public ResponseEntity<List<SelectItem>> serviceTypes() {
        return ResponseEntity.ok(ServiceType.givePermissionList());
    }
}
