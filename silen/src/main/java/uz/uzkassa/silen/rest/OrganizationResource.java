package uz.uzkassa.silen.rest;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.silen.dto.OrganizationDTO;
import uz.uzkassa.silen.dto.OrganizationListDTO;
import uz.uzkassa.silen.dto.OrganizationPermissionDTO;
import uz.uzkassa.silen.dto.SelectItem;
import uz.uzkassa.silen.dto.filter.OrganizationFilter;
import uz.uzkassa.silen.dto.invoice.TINAbstractDTO;
import uz.uzkassa.silen.enumeration.OrganizationType;
import uz.uzkassa.silen.enumeration.ProductGroup;
import uz.uzkassa.silen.export.OrganizationsExcel;
import uz.uzkassa.silen.integration.billing.enums.BillingDocumentType;
import uz.uzkassa.silen.service.OrganizationService;

import java.io.IOException;
import java.util.EnumSet;
import java.util.List;

/**
 * REST controller for managing {@link uz.uzkassa.silen.domain.Organization}.
 */
@RestController
@RequestMapping("/api/organization")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrganizationResource {
    OrganizationService organizationService;
    OrganizationsExcel excelOrganizationsExportHandler;

    @PostMapping
    public ResponseEntity<OrganizationDTO> create(@RequestBody @Valid OrganizationDTO organizationDTO) {
        return ResponseEntity.ok(organizationService.save(organizationDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrganizationDTO> update(@PathVariable("id") String id, @RequestBody @Valid OrganizationDTO organizationDTO) {
        return ResponseEntity.ok(organizationService.update(id, organizationDTO));
    }

    @GetMapping
    public ResponseEntity<Page<OrganizationListDTO>> getAll(@ModelAttribute OrganizationFilter filter) {
        return ResponseEntity.ok(organizationService.findAllByFilter(filter));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrganizationDTO> get(@PathVariable String id) {
        return ResponseEntity.ok(organizationService.findOne(id));
    }

    @GetMapping("/byTin/{tin}")
    public ResponseEntity<TINAbstractDTO> getByTin(@PathVariable("tin") String tin) {
        return ResponseEntity.ok(organizationService.getCompanyByTin(tin));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        organizationService.delete(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/items")
    public ResponseEntity<List<SelectItem>> getItems(@ModelAttribute OrganizationFilter filter) {
        return ResponseEntity.ok(organizationService.getItems(filter));
    }

    @GetMapping("/utilise/items")
    public ResponseEntity<List<SelectItem>> getItemsForUtilise(@ModelAttribute OrganizationFilter filter) {
        return ResponseEntity.ok(organizationService.getItemsForUtilise(filter));
    }

    @GetMapping("/types")
    public ResponseEntity<List<SelectItem>> getTypes() {
        return ResponseEntity.ok(OrganizationType.getAll());
    }

    @GetMapping("/product-groups")
    public ResponseEntity<List<SelectItem>> getProductGroups() {
        return ResponseEntity.ok(ProductGroup.getAll());
    }

    @GetMapping("/billing-permissions")
    public ResponseEntity<List<OrganizationPermissionDTO>> getBillingPermissions(@RequestParam(name = "organizationId", required = false) String organizationId) {
        return ResponseEntity.ok(organizationService.getBillingPermissions(organizationId));
    }

    @GetMapping(value = "/export_xls")
    public ResponseEntity<Void> generateCsv(@ModelAttribute OrganizationFilter filter, HttpServletResponse response) throws IOException {
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Organization.xlsx");
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "0");
        excelOrganizationsExportHandler.writeToStream(filter, response.getOutputStream());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/billing-document-types")
    public ResponseEntity<EnumSet<BillingDocumentType>> getBillingDocumentTypes() {
        return ResponseEntity.ok(EnumSet.allOf(BillingDocumentType.class));
    }
}
