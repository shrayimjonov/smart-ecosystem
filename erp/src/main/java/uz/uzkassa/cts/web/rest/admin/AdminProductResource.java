package uz.uzkassa.cts.web.rest.admin;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.cts.constants.ApiConstants;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.dto.product.ProductDTO;
import uz.uzkassa.cts.dto.product.admin.ProductDetailAdminDTO;
import uz.uzkassa.cts.dto.product.admin.ProductListAdminDTO;
import uz.uzkassa.cts.enums.ContentType;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.filter.ProductFilter;
import uz.uzkassa.cts.service.ExportReportService;
import uz.uzkassa.cts.service.ProductService;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.adminProductRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AdminProductResource {

    ProductService productService;
    ExportReportService exportReportService;

    @PostMapping
    public ResponseEntity<Long> create(@RequestBody ProductDTO productDTO) {
        return ResponseEntity.ok(productService.create(productDTO));
    }

    @PutMapping(ApiConstants.id)
    public ResponseEntity<Long> update(@PathVariable Long id, @RequestBody ProductDTO productDTO) {
        return ResponseEntity.ok(productService.update(id, productDTO));
    }

    @GetMapping(ApiConstants.id)
    public ResponseEntity<ProductDetailAdminDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getForAdmin(id));
    }

    @GetMapping
    public ResponseEntity<Page<ProductListAdminDTO>> getList(@ParameterObject ProductFilter filter) {
        return ResponseEntity.ok(productService.getListForAdmin(filter));
    }

    @GetMapping(ApiConstants.lookUp)
    public ResponseEntity<List<CommonDTO>> lookUp(@ParameterObject ProductFilter filter) {
        return ResponseEntity.ok(productService.lookUp(filter));
    }

    @GetMapping(ApiConstants.exportExcel)
    public ResponseEntity<Void> exportToExcel(@ParameterObject ProductFilter filter) {
        exportReportService.export(filter, EntityTypeEnum.PRODUCT, ContentType.EXCEL);
        return ResponseEntity.ok().build();
    }

    @GetMapping(ApiConstants.exportCSV)
    public ResponseEntity<Void> exportToCSV(@ParameterObject ProductFilter filter) {
        exportReportService.export(filter, EntityTypeEnum.PRODUCT, ContentType.CSV);
        return ResponseEntity.ok().build();
    }
}
