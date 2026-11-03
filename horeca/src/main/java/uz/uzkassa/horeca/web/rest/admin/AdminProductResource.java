package uz.uzkassa.horeca.web.rest.admin;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.horeca.constants.ApiConstants;
import uz.uzkassa.horeca.dto.file.FileDTO;
import uz.uzkassa.horeca.dto.product.*;
import uz.uzkassa.horeca.dto.soliq.MxikDTO;
import uz.uzkassa.horeca.dto.soliq.ProductSearchDTO;
import uz.uzkassa.horeca.enums.CommonStatus;
import uz.uzkassa.horeca.filters.ProductFilter;
import uz.uzkassa.horeca.interfaces.CrudRestAdvanced;
import uz.uzkassa.horeca.service.CateringServiceService;
import uz.uzkassa.horeca.service.ProductService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping(ApiConstants.adminProductRootApi)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AdminProductResource implements CrudRestAdvanced<Long, ProductDTO, ProductDetailDTO, ProductListDTO, ProductFilter, CommonStatus, ProductLookUpDTO> {

    ProductService productService;
    private final CateringServiceService cateringServiceService;

    @PostMapping
    @Override
    public ResponseEntity<Long> create(@RequestBody ProductDTO productDTO) {
        return ResponseEntity.ok(productService.create(productDTO));
    }

    @PutMapping(ApiConstants.id)
    @Override
    public ResponseEntity<Long> update(@PathVariable Long id, @RequestBody ProductDTO productDTO) {
        return ResponseEntity.ok(productService.update(id, productDTO));
    }

    @Override
    @GetMapping(ApiConstants.id)
    public ResponseEntity<ProductDetailDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(productService.get(id));
    }

    @Override
    @GetMapping
    public ResponseEntity<Page<ProductListDTO>> getList(@ParameterObject ProductFilter filter) {
        return ResponseEntity.ok(productService.getList(filter));
    }

    @Override
    @DeleteMapping(ApiConstants.id)
    public ResponseEntity<Void> delete(@PathVariable Long id, @RequestParam(required = false) LocalDateTime lastModifiedDate) {
        productService.delete(id);
        return ResponseEntity.ok().build();
    }

    @Override
    @GetMapping(ApiConstants.lookUp)
    public ResponseEntity<List<ProductLookUpDTO>> lookUp(@ParameterObject ProductFilter filter) {
        return ResponseEntity.ok(productService.lookUp(filter));
    }

    @PatchMapping(ApiConstants.updateStatus)
    @Override
    public ResponseEntity<Void> updateStatus(@PathVariable Long id, @RequestParam("status") CommonStatus status) {
        productService.updateStatus(id, status);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/price")
    public ResponseEntity<Void> updatePrice(@PathVariable Long id, @RequestParam("price") BigDecimal price) {
        productService.updatePrice(id, price);
        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "/{id}/upload/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<FileDTO> uploadPhoto(@PathVariable Long id, @RequestParam("file") MultipartFile photo) {
        return ResponseEntity.ok(productService.uploadPhoto(id, photo));
    }

    @DeleteMapping("/{id}/photo")
    public ResponseEntity<Void> removePhoto(@PathVariable Long id) {
        productService.removePhoto(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/tasnif/search/by-params")
    public ResponseEntity<List<MxikDTO>> searchFromTasnifByParams(@ParameterObject ProductSearchDTO productSearchDTO) {
        return ResponseEntity.ok(productService.searchFromTasnifByParams(productSearchDTO));
    }

    @GetMapping(ApiConstants.cateringServiceApi)
    public ResponseEntity<CateringServiceDetailDTO> getCateringService(@RequestParam(required = false) Long branchId) {
        return ResponseEntity.ok(cateringServiceService.getCateringService(branchId));
    }
}
