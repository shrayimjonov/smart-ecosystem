package uz.uzkassa.smartpos.web.rest.admin;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.smartpos.dto.catalog.Product1CCreateDto;
import uz.uzkassa.smartpos.dto.catalog.ProductDTO;
import uz.uzkassa.smartpos.dto.filter.ImportProductsFilter;
import uz.uzkassa.smartpos.exceptions.BadRequestException;
import uz.uzkassa.smartpos.report.CatalogProductImportTemplateXLs;
import uz.uzkassa.smartpos.service.LocalizationService;
import uz.uzkassa.smartpos.service.ProductService;
import uz.uzkassa.smartpos.utils.ServerUtils;

import java.io.IOException;

import static uz.uzkassa.smartpos.security.AuthoritiesConstants.*;

@RestController
@RequestMapping("/api/admin/products")
@Secured({SMARTPOS_ADMIN, SUPPORT, SALES_MANAGER})
@RequiredArgsConstructor
public class AdminProductResource {

    private final ProductService productService;
    private final LocalizationService localizationService;
    private final CatalogProductImportTemplateXLs catalogProductImportTemplateXLs;

    @GetMapping(value = "/export")
    public ResponseEntity<Resource> exportToExcel(@RequestParam Long branchId, @RequestParam Long categoryId) throws IOException {
        return ServerUtils.getResponseEntity(catalogProductImportTemplateXLs.exportProducts(categoryId, branchId), "products");
    }


    @PostMapping(value = "/import/ref", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseBody
    public ResponseEntity<Resource> importProductsRef(@RequestParam(value = "uploadFile") MultipartFile file, @ParameterObject ImportProductsFilter filter) {
        if (filter.getBranchId() == null) {
            throw new BadRequestException(localizationService.getMessage("branch.required"));
        }
        ByteArrayResource byteArrayResource = productService.importProductsRef(file, filter);
        return ServerUtils.getResponseEntity(byteArrayResource, "products");
    }

    @PutMapping
    public ResponseEntity<ProductDTO> updateProduct(@Valid @RequestBody Product1CCreateDto productDTO) {
        if (productDTO.getId() == null) {
            throw new BadRequestException(localizationService.getMessage("id.required"));
        }
        if (productDTO.getBranchId() == null) {
            throw new BadRequestException(localizationService.getMessage("branch.required"));
        }
        ProductDTO result = productService.updateFor1C(productDTO);
        return ResponseEntity.ok().body(result);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteProduct(@RequestParam Long branchId) {
        productService.deleteAllProductFromBranch(branchId);
        return ResponseEntity.ok().build();
    }

}
