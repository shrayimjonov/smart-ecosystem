package uz.uzkassa.smartpossupply.web.rest.cabinet;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.smartpossupply.constants.ApiConstants;
import uz.uzkassa.smartpossupply.dto.aic.AicProductLookUpDTO;
import uz.uzkassa.smartpossupply.dto.aslbelgi.aic.ProductTnVedResponse;
import uz.uzkassa.smartpossupply.dto.file.FileDTO;
import uz.uzkassa.smartpossupply.dto.importreport.ProductImportReportDTO;
import uz.uzkassa.smartpossupply.dto.product.ProductByCategoryListDTO;
import uz.uzkassa.smartpossupply.dto.product.ProductCategoryDTO;
import uz.uzkassa.smartpossupply.dto.product.ProductDTO;
import uz.uzkassa.smartpossupply.dto.product.ProductFullDetailDTO;
import uz.uzkassa.smartpossupply.dto.product.ProductListDTO;
import uz.uzkassa.smartpossupply.dto.product.ProductLookUpDTO;
import uz.uzkassa.smartpossupply.dto.product.ProductLookUpWithWarehouseQtyDTO;
import uz.uzkassa.smartpossupply.dto.product.ProductPhotoDTO;
import uz.uzkassa.smartpossupply.dto.product.SyncCatalogProductDTO;
import uz.uzkassa.smartpossupply.dto.product.WebKassaProductListDTO;
import uz.uzkassa.smartpossupply.dto.soliq.MxikDTO;
import uz.uzkassa.smartpossupply.enums.CommonStatus;
import uz.uzkassa.smartpossupply.enums.DispenseType;
import uz.uzkassa.smartpossupply.enums.EntityTypeEnum;
import uz.uzkassa.smartpossupply.enums.EnvironmentType;
import uz.uzkassa.smartpossupply.enums.FileType;
import uz.uzkassa.smartpossupply.enums.Month;
import uz.uzkassa.smartpossupply.enums.ProductGroup;
import uz.uzkassa.smartpossupply.enums.ProductType;
import uz.uzkassa.smartpossupply.enums.TargetAudience;
import uz.uzkassa.smartpossupply.filters.ProductFilter;
import uz.uzkassa.smartpossupply.service.AsilBelgisiService;
import uz.uzkassa.smartpossupply.service.ProductPhotosService;
import uz.uzkassa.smartpossupply.service.ProductService;
import uz.uzkassa.smartpossupply.service.ProductSynchronisationService;
import uz.uzkassa.smartpossupply.service.WarehouseProductService;
import uz.uzkassa.smartpossupply.service.handlers.excel.importreport.ProductImportReportExcelHandler;
import uz.uzkassa.smartpossupply.service.handlers.excel.template.ProductImportTemplateExcelHandler;
import uz.uzkassa.smartpossupply.utils.SecurityUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.cabinetProductRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CabinetProductResource {

    ProductService productService;
    ProductImportTemplateExcelHandler productImportTemplateExcelHandler;
    ProductImportReportExcelHandler productImportReportExcelHandler;
    ProductPhotosService productPhotosService;
    ProductSynchronisationService productSynchronisationService;
    WarehouseProductService warehouseProductService;
    AsilBelgisiService asilBelgisiService;

    @Operation(summary = ApiConstants.Create)
    @PostMapping
    public ResponseEntity<Long> create(@Valid @RequestBody ProductDTO productDTO) {
        return ResponseEntity.ok(productService.create(productDTO));
    }

    @Operation(summary = ApiConstants.Get)
    @GetMapping(ApiConstants.id)
    public ResponseEntity<ProductFullDetailDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(productService.get(id));
    }

    @Operation(summary = ApiConstants.Update)
    @PutMapping(ApiConstants.id)
    public ResponseEntity<Long> update(@PathVariable Long id, @Valid @RequestBody ProductDTO productDTO) {
        return ResponseEntity.ok(productService.update(id, productDTO));
    }

    @Operation(summary = ApiConstants.List)
    @GetMapping
    public ResponseEntity<Page<ProductListDTO>> getList(@ParameterObject ProductFilter filter) {
        if (SecurityUtils.getCurrentCompanyId() == null) {
            return ResponseEntity.ok(new PageImpl<>(new ArrayList<>(), filter.getPageable(), 0L));
        }
        return ResponseEntity.ok(productService.getList(filter));
    }

    @Operation(summary = "Product list by category")
    @GetMapping("/category")
    public ResponseEntity<Page<ProductByCategoryListDTO>> getListByCategory(@ParameterObject ProductFilter filter) {
        if (SecurityUtils.getCurrentCompanyId() == null) {
            return ResponseEntity.ok(new PageImpl<>(new ArrayList<>(), filter.getPageable(), 0L));
        }
        return ResponseEntity.ok(productService.getListByCategory(filter));
    }

    @Operation(summary = "Assign category to products")
    @PostMapping("/assign/category")
    public ResponseEntity<Void> assignCategory(@RequestBody ProductCategoryDTO productCategoryDTO) {
        productService.assignCategory(productCategoryDTO);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = ApiConstants.LookUp)
    @GetMapping(ApiConstants.lookUp)
    public ResponseEntity<List<ProductLookUpDTO>> lookUp(@ParameterObject ProductFilter filter) {
        if (SecurityUtils.getCurrentCompanyId() == null) {
            return ResponseEntity.ok(new ArrayList<>());
        }
        filter.setStatus(CommonStatus.ACTIVE.getCode());
        filter.setEnvironmentType(EnvironmentType.WEB.getCode());
        return ResponseEntity.ok(productService.lookUp(filter));
    }

    @Operation(summary = "Products Look Up For Aic")
    @GetMapping("/aic/lookup")
    public ResponseEntity<List<AicProductLookUpDTO>> getLookUpToAic(@ParameterObject ProductFilter filter) {
        if (SecurityUtils.getCurrentCompanyId() == null) {
            return ResponseEntity.ok(new ArrayList<>());
        }
        filter.setHasMark(true);
        return ResponseEntity.ok(productService.getLookUpToAic(filter));
    }

    @Operation(summary = "Look Up (Warehouse Qty)")
    @GetMapping("/lookup/warehouse-qty")
    public ResponseEntity<List<ProductLookUpWithWarehouseQtyDTO>> lookUpWarehouseQty(@ParameterObject ProductFilter filter) {
        if (SecurityUtils.getCurrentCompanyId() == null) {
            return ResponseEntity.ok(new ArrayList<>());
        }
        filter.setEnvironmentType(EnvironmentType.WEB.getCode());
        return ResponseEntity.ok(productService.getProductLookUpWithWarehouseQty(filter));
    }

    @Operation(summary = "Warehouse Qty")
    @GetMapping("/warehouse-qty/{warehouseId}/{productId}")
    public ResponseEntity<BigDecimal> getWarehouseQty(@PathVariable Long warehouseId, @PathVariable Long productId) {
        return ResponseEntity.ok(warehouseProductService.getWarehouseQty(warehouseId, productId));
    }

    @Deprecated
    @Operation(summary = ApiConstants.Types)
    @GetMapping(ApiConstants.types)
    public ResponseEntity<EnumSet<ProductType>> getTypes() {
        return ResponseEntity.ok(EnumSet.of(ProductType.Alcohol, ProductType.Cigarette, ProductType.Beer, ProductType.Pharma, ProductType.Water, ProductType.Appliances, ProductType.Fertilizers));
    }

    @Operation(summary = ApiConstants.Groups)
    @GetMapping(ApiConstants.groups)
    public ResponseEntity<List<Map<String, String>>> getGroups() {
        return ResponseEntity.ok(ProductGroup.getProductGroupList());
    }

    @Operation(summary = ApiConstants.Delete)
    @DeleteMapping(ApiConstants.id)
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        productService.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Get Company Catalog")
    @GetMapping("/company/catalog")
    public ResponseEntity<List<MxikDTO>> getCompanyCatalog(@RequestParam(required = false) String tin) {
        if (SecurityUtils.getCurrentCompanyId() == null) {
            return ResponseEntity.ok(new ArrayList<>());
        }
        return ResponseEntity.ok(productSynchronisationService.getCompanyCatalog(tin));
    }

    @Operation(summary = "Sync Company Catalog")
    @PostMapping("/company/catalog/sync")
    public ResponseEntity<Void> syncCompanyAllCatalog(@RequestParam(required = false) String tin) {
        if (SecurityUtils.getCurrentCompanyId() == null) {
            return ResponseEntity.ok().build();
        }
        productSynchronisationService.syncCompanyAllCatalog(tin);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "ReSync Company Catalog")
    @PutMapping("/company/catalog/sync")
    public ResponseEntity<Void> syncCompanyCatalogProducts(@RequestBody SyncCatalogProductDTO syncCatalogProductDTO) {
        productSynchronisationService.syncCompanyCatalogProducts(syncCatalogProductDTO);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/favorite" + ApiConstants.id)
    public ResponseEntity<Void> favorite(@PathVariable Long id) {
        productService.favorite(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/web-kassa")
    public ResponseEntity<Page<WebKassaProductListDTO>> getWebKassaProductList(@ParameterObject ProductFilter filter) {
        return ResponseEntity.ok(productService.getWebKassaProductList(filter));
    }

    @Operation(summary = "Upload Product Photos")
    @PostMapping(value = "/upload/photos", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<List<FileDTO>> uploadPhoto(@RequestPart("files") MultipartFile[] files) {
        return ResponseEntity.ok(productPhotosService.uploadPhoto(List.of(files)));
    }

    @Operation(summary = "Assign photos to product")
    @PostMapping("/{id}/assign/photos")
    public ResponseEntity<Void> assignPhotos(@PathVariable Long id, @RequestBody ProductPhotoDTO productPhotoDTO) {
        productPhotosService.assignPhotos(id, productPhotoDTO);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Getting tnVedCode")
    @GetMapping("/tnved/{barcode}")
    public ResponseEntity<ProductTnVedResponse> getTnVedCode(@PathVariable String barcode) {
        return ResponseEntity.ok(asilBelgisiService.getTnVedCode(barcode));
    }

    @Operation(summary = "Update status")
    @PatchMapping(ApiConstants.updateStatus)
    public ResponseEntity<Long> updateStatus(@PathVariable Long id, @RequestParam("status") String status) {
        return ResponseEntity.ok(productService.updateStatus(id, CommonStatus.valueOf(status)));
    }

    @Operation(summary = "Sync product package")
    @PostMapping("/sync-package")
    public ResponseEntity<Void> syncPackage() {
        //productService.syncPackage(SecurityUtils.getCurrentCompanyTin());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = ApiConstants.Template)
    @GetMapping("/download-template")
    public ResponseEntity<Resource> getTemplate() {
        return productImportTemplateExcelHandler.download();
    }

    @Operation(summary = ApiConstants.Import)
    @PostMapping(value = ApiConstants.importStr, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> importProducts(@RequestPart("file") MultipartFile file, @RequestPart(name = "importReportDTO", required = false) ProductImportReportDTO importReportDTO) {
        productImportReportExcelHandler.importReport(file, EntityTypeEnum.PRODUCT, FileType.EXCEL, importReportDTO);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/months")
    public ResponseEntity<EnumSet<Month>> getMonths() {
        return ResponseEntity.ok(EnumSet.allOf(Month.class));
    }

    @GetMapping("/target-audience")
    public ResponseEntity<EnumSet<TargetAudience>> getTargetAudience() {
        return ResponseEntity.ok(EnumSet.allOf(TargetAudience.class));
    }

    @GetMapping("/dispense-types")
    public ResponseEntity<EnumSet<DispenseType>> getDispenseTypes() {
        return ResponseEntity.ok(EnumSet.allOf(DispenseType.class));
    }
}
