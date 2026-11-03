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
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.silen.dto.*;
import uz.uzkassa.silen.dto.filter.ProductFilter;
import uz.uzkassa.silen.enumeration.ProductStatus;
import uz.uzkassa.silen.excel.ProductExcelScheme;
import uz.uzkassa.silen.service.ProductService;

import java.io.IOException;
import java.util.List;

/**
 * REST controller for managing {@link uz.uzkassa.silen.domain.Product}.
 */
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductResource {
    ProductService productService;
    ProductExcelScheme excelScheme;

    @PostMapping
    public ResponseEntity<ProductDTO> create(@Valid @RequestBody ProductDTO productDTO) {
        productService.save(productDTO);
        return ResponseEntity.ok().build();
    }

    @PutMapping
    public ResponseEntity<ProductDTO> update(@Valid @RequestBody ProductDTO productDTO) {
        productService.update(productDTO);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/update-status")
    public ResponseEntity<Void> updateStatus(@RequestBody @Valid ProductStatusUpdateDTO productDTO) {
        productService.updateStatus(productDTO);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<Page<ProductListDTO>> getAll(@ModelAttribute ProductFilter filter) {
        return ResponseEntity.ok(productService.findAll(filter));
    }

    @GetMapping("/stats")
    public ResponseEntity<List<ProductSelectCountItem>> getAllCountsByType(@ModelAttribute ProductFilter filter) {
        return ResponseEntity.ok(productService.productCounts(filter));
    }

    @GetMapping("/items")
    public ResponseEntity<List<ProductSelectItem>> getItems(@ModelAttribute ProductFilter filter) {
        filter.setStatus(ProductStatus.ACCEPTED);
        return ResponseEntity.ok(productService.getProductItems(filter));
    }

    @GetMapping("/types")
    public ResponseEntity<List<SelectItem>> getTypes() {
        return ResponseEntity.ok(productService.getProductTypesByOrganization());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> get(@PathVariable String id) {
        return ResponseEntity.ok(productService.getOne(id));
    }

    @GetMapping("/barcode/{barcode}")
    public ResponseEntity<Double> getProductCapacityByBarcode(@PathVariable String barcode) {
        return ResponseEntity.ok(productService.getProductCapacityByBarcode(barcode));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        productService.delete(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/excel/scheme")
    public void getScheme(HttpServletResponse response) throws Exception {
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=scheme.xlsx");
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "0");
        excelScheme.getScheme(response);
    }

    @GetMapping("/excel/export")
    public ResponseEntity<String> export(@ModelAttribute ProductFilter filter) throws Exception {
        return ResponseEntity.ok(productService.creteExportTask(filter));
    }

    @GetMapping("/download/excel/{file}")
    public void downloadExportedProducts(@PathVariable("file") String file, HttpServletResponse response) throws IOException {
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=products.xlsx");
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "0");
        productService.downloadExportFile(file, response.getOutputStream());
    }

    @PostMapping("/excel/import")
    public ResponseEntity<String> importFromFile(@RequestParam("file") MultipartFile file) throws IOException {
        return ResponseEntity.ok(productService.createImportTask(file));
    }

    @GetMapping("/statuses")
    public List<EnumDTO> getStatuses() {
        return ProductStatus.getAll();
    }

    @GetMapping("/import-from-basket")
    public void importProductsFromBasket() {
        productService.importFromBasket();
    }

    @GetMapping("/by/{mxik}")
    public ResponseEntity<ProductDetail> getProductDetailsByCatalogCode(@PathVariable("mxik") String mxik) {
        return ResponseEntity.ok(productService.getProductDetailsByCatalogCode(mxik));
    }
}
