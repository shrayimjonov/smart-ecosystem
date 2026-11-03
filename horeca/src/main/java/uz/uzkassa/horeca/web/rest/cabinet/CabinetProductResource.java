package uz.uzkassa.horeca.web.rest.cabinet;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.horeca.constants.ApiConstants;
import uz.uzkassa.horeca.domain.product.BatchStatusUpdate;
import uz.uzkassa.horeca.dto.file.FileDTO;
import uz.uzkassa.horeca.dto.product.*;
import uz.uzkassa.horeca.dto.soliq.MxikDTO;
import uz.uzkassa.horeca.dto.soliq.ProductSearchDTO;
import uz.uzkassa.horeca.filters.ProductFilter;
import uz.uzkassa.horeca.interfaces.CrudRest;
import uz.uzkassa.horeca.service.CateringServiceService;
import uz.uzkassa.horeca.service.ProductService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.cabinetProductRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CabinetProductResource implements CrudRest<Long, ProductDTO, ProductDetailDTO, ProductListDTO, ProductFilter> {

    ProductService productService;
    CateringServiceService cateringServiceService;

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

    @GetMapping(ApiConstants.lookUp)
    public ResponseEntity<List<ProductLookUpDTO>> lookUp(@ParameterObject ProductFilter filter) {
        return ResponseEntity.ok(productService.lookUp(filter));
    }

    @PatchMapping(ApiConstants.updateStatusBatch)
    public ResponseEntity<Map<Long, String>> updateStatusBatch(@RequestBody @Valid BatchStatusUpdate batchStatusUpdate) {
        return ResponseEntity.ok(productService.updateStatusBatch(batchStatusUpdate));
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

    @GetMapping(ApiConstants.cateringServiceApi)
    public ResponseEntity<CateringServiceDetailDTO> getCateringService(@RequestParam(required = false) Long branchId) {
        return ResponseEntity.ok(cateringServiceService.getCateringService(branchId));
    }

    @GetMapping("/tasnif/search/by-params")
    public ResponseEntity<List<MxikDTO>> searchFromTasnifByParams(@ParameterObject ProductSearchDTO productSearchDTO) {
        return ResponseEntity.ok(productService.searchFromTasnifByParams(productSearchDTO));
    }
}
