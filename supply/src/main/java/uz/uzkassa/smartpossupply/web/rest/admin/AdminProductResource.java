package uz.uzkassa.smartpossupply.web.rest.admin;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.smartpossupply.constants.ApiConstants;
import uz.uzkassa.smartpossupply.dto.aslbelgi.aic.ProductTnVedResponse;
import uz.uzkassa.smartpossupply.dto.product.*;
import uz.uzkassa.smartpossupply.dto.soliq.MxikDTO;
import uz.uzkassa.smartpossupply.enums.ProductType;
import uz.uzkassa.smartpossupply.filters.ProductFilter;
import uz.uzkassa.smartpossupply.service.AsilBelgisiService;
import uz.uzkassa.smartpossupply.service.ProductService;
import uz.uzkassa.smartpossupply.service.ProductSynchronisationService;

import java.util.EnumSet;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.adminProductRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AdminProductResource {

    ProductService productService;
    ProductSynchronisationService productSynchronisationService;
    AsilBelgisiService asilBelgisiService;

    @Operation(summary = ApiConstants.List)
    @GetMapping
    public ResponseEntity<Page<ProductListDTO>> getList(@ParameterObject ProductFilter filter) {
        return ResponseEntity.ok(productService.getList(filter));
    }

    @Operation(summary = ApiConstants.Create)
    @PostMapping
    public ResponseEntity<Long> create(@RequestBody ProductDTO productDTO) {
        return ResponseEntity.ok(productService.create(productDTO));
    }

    @Operation(summary = ApiConstants.Get)
    @GetMapping(ApiConstants.id)
    public ResponseEntity<ProductFullDetailDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(productService.get(id));
    }

    @Operation(summary = "Get Company Catalog")
    @GetMapping("/company/catalog")
    public ResponseEntity<List<MxikDTO>> getCompanyCatalog(@RequestParam(required = false) String tin) {
        return ResponseEntity.ok(productSynchronisationService.getCompanyCatalog(tin));
    }

    @Operation(summary = "Sync Company Catalog")
    @PostMapping("/company/catalog/sync")
    public ResponseEntity<Void> syncCompanyAllCatalog(@RequestParam(required = false) String tin) {
        productSynchronisationService.syncCompanyAllCatalog(tin);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "ReSync Company Catalog")
    @PutMapping("/company/catalog/sync")
    public ResponseEntity<Void> syncCompanyCatalogProducts(@RequestBody SyncCatalogProductDTO syncCatalogProductDTO) {
        productSynchronisationService.syncCompanyCatalogProducts(syncCatalogProductDTO);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = ApiConstants.Update)
    @PutMapping(ApiConstants.id)
    public ResponseEntity<Long> update(@PathVariable Long id, @RequestBody ProductDTO productDTO) {
        return ResponseEntity.ok(productService.update(id, productDTO));
    }

    @Operation(summary = ApiConstants.LookUp)
    @GetMapping(ApiConstants.lookUp)
    public ResponseEntity<List<ProductLookUpDTO>> lookUp(@ParameterObject ProductFilter filter) {
        return ResponseEntity.ok(productService.lookUp(filter));
    }

    @Operation(summary = ApiConstants.Types)
    @GetMapping(ApiConstants.types)
    public ResponseEntity<EnumSet<ProductType>> getTypes() {
        return ResponseEntity.ok(EnumSet.allOf(ProductType.class));
    }

    @Operation(summary = "Update Barcode")
    @PutMapping("/barcode/{id}")
    public ResponseEntity<Long> updateBarcode(@PathVariable Long id, @RequestBody ProductBarcodeDTO productBarcodeDTO) {
        return ResponseEntity.ok(productService.updateBarcode(id, productBarcodeDTO));
    }

    @Operation(summary = "Getting tnVedCode")
    @GetMapping("/tnved/{barcode}")
    public ResponseEntity<ProductTnVedResponse> getTnVedCode(@PathVariable String barcode) {
        return ResponseEntity.ok(asilBelgisiService.getTnVedCode(barcode));
    }

    @Operation(summary = "Sync product package")
    @PostMapping("/sync-package")
    public ResponseEntity<Void> syncPackage(@RequestParam String tin) {
        productSynchronisationService.syncPackage(tin);
        return ResponseEntity.ok().build();
    }
}
