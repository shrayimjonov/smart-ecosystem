package uz.uzkassa.smartpos.web.rest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.smartpos.constants.CacheConstants;
import uz.uzkassa.smartpos.dto.UnitDTO;
import uz.uzkassa.smartpos.dto.UnitSelectItem;
import uz.uzkassa.smartpos.dto.catalog.*;
import uz.uzkassa.smartpos.dto.filter.ImportProductsFilter;
import uz.uzkassa.smartpos.dto.filter.ProductFilter;
import uz.uzkassa.smartpos.dto.nic.MxikDTO;
import uz.uzkassa.smartpos.dto.product.SetProductPriceByCategoryDTO;
import uz.uzkassa.smartpos.exceptions.BadRequestException;
import uz.uzkassa.smartpos.rabbitmq.MqConstants;
import uz.uzkassa.smartpos.rabbitmq.producer.RabbitMqProducer;
import uz.uzkassa.smartpos.report.CatalogProductImportTemplateXLs;
import uz.uzkassa.smartpos.service.LocalizationService;
import uz.uzkassa.smartpos.service.MxikService;
import uz.uzkassa.smartpos.service.ProductService;
import uz.uzkassa.smartpos.service.UnitService;
import uz.uzkassa.smartpos.utils.ServerUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ProductResource {
    private final UnitService unitService;
    private final ProductService productService;
    private final RabbitMqProducer rabbitMqProducer;
    private final LocalizationService localizationService;
    private final MxikService mxikService;
    private final CatalogProductImportTemplateXLs catalogProductImportTemplateXLs;

    /**
     * {@code GET  /product/units} : get all product units.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of units in body.
     */
    @Cacheable(cacheNames = CacheConstants.UNIT_CACHE)
    @GetMapping("/product/units")
    public List<UnitDTO> getUnits() {
        return unitService.findAll();
    }

    /**
     * {@code POST  /products} : Create a new Product.
     *
     * @param productDTO the Product to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new Product
     */
    @PostMapping("/products")
    public ResponseEntity<ProductDTO> createProduct(@Valid @RequestBody Product1CCreateDto productDTO) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(productService.createFor1C(productDTO));
    }

    @GetMapping(value = "/products/export")
    public ResponseEntity<Resource> exportToExcel(@RequestParam Long branchId, @RequestParam Long categoryId) throws IOException {
        return ServerUtils.getResponseEntity(catalogProductImportTemplateXLs.exportProducts(categoryId, branchId), "products");
    }


    /**
     * {@code PUT  /products} : Updates an existing Product.
     *
     * @param productDTO the product to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated product,
     * or with status {@code 400 (Bad Request)} if the product is not valid,
     * or with status {@code 500 (Internal Server Error)} if the product couldn't be updated.
     */
    @PutMapping("/products")
    public ResponseEntity<ProductDTO> updateProduct(@Valid @RequestBody Product1CCreateDto productDTO) {
        if (productDTO.getId() == null) {
            throw new BadRequestException(localizationService.getMessage("id.required"));
        }
        ProductDTO result = productService.updateFor1C(productDTO);
        return ResponseEntity.ok().body(result);
    }

    @GetMapping("/products/tasnif")
    public ResponseEntity<List<MxikDTO>> getTasnifProducts(@RequestParam(required = false) String searchKey,
                                                           @RequestParam(required = false) String mxikCode,
                                                           @RequestParam(required = false) String barcode) {
        if (StringUtils.isAllBlank(searchKey, mxikCode, barcode)) {
            throw new BadRequestException(localizationService.getMessage("set.source.param"));
        }
        List<MxikDTO> result = mxikService.searchProduct(searchKey, mxikCode, barcode);
        return ResponseEntity.ok(result);
    }

    @PutMapping("/products/set-price")
    public ResponseEntity<Void> setPrice(@Valid @RequestBody UpdateProductPriceDto priceDto) {
        productService.setPriceExisting(priceDto.getSalesPrice(), priceDto.getProductId(), priceDto.getBranchId());
        return ResponseEntity.ok().build();
    }

    @PutMapping("/products/set-vat/batch")
    public ResponseEntity<Void> setVatBatch(@Valid @RequestBody BatchUpdateDTO dto) {
        rabbitMqProducer.send(dto, MqConstants.VAT_UPDATE_QUEUE);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/products/set-committent/batch")
    public ResponseEntity<Void> setCommittent(@Valid @RequestBody BatchUpdateDTO dto) {
        rabbitMqProducer.send(dto, MqConstants.COMMITTENT_TIN_UPDATE_QUEUE);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/products/set-product-origin/batch")
    public ResponseEntity<Void> setProductOrigin(@Valid @RequestBody BatchUpdateDTO dto) {
        rabbitMqProducer.send(dto, MqConstants.PRODUCT_ORIGIN_UPDATE_QUEUE);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/products/set-mark/batch")
    public ResponseEntity<Void> setMark(@Valid @RequestBody BatchUpdateDTO dto) {
        rabbitMqProducer.send(dto, MqConstants.MARK_UPDATE_QUEUE);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/products/set-price-category/")
    public ResponseEntity<Void> setPriceByCategoryId(@Valid @RequestBody SetProductPriceByCategoryDTO dto) {
        if (dto.getCoefficient() > 99 && dto.isDecrease()) {
            throw new BadRequestException("Вы не можете установить цену ниже 1 процента от текущей цены");
        }
        final BigDecimal coeff = BigDecimal.valueOf(dto.getCoefficient()).divide(new BigDecimal(100), 4, RoundingMode.HALF_UP);
        final boolean decrease = dto.isDecrease();
        productService.priceAdjustmentByCategoryId(coeff, dto.getCategoryId(), dto.getBranchId(), decrease);
        return ResponseEntity.ok().build();
    }

    /**
     * {@code GET  /products} : Get Product by id.
     *
     * @param id the Product id to get.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body of the Product
     */
    @GetMapping("/products/{id}")
    public ResponseEntity<ProductDTO> getProduct(@PathVariable Long id, @RequestParam(required = false) Long branchId) {
        return ResponseEntity.ok(productService.getProduct(id, branchId));
    }


    /**
     * {@code GET  /products} : Get Product by barcode.
     *
     * @param barcode the Product barcode to get.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body of the Product
     */
    @GetMapping("/products/barcode/{barcode}")
    public ResponseEntity<ProductDTO> getProductByBarcode(@PathVariable(name = "barcode") String barcode, @RequestParam Long branchId) {
        return ResponseEntity.ok(productService.getByBarcode(barcode, branchId));
    }

    /**
     * {@code POST  /products/hide:id} : Hide Product
     *
     * @param id the Products to hide.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)}
     */
    @DeleteMapping("/products/{id}")
    public ResponseEntity<Void> hideProducts(@PathVariable Long id, @RequestParam Long branchId) {
        productService.deleteProductFromBranch(id, branchId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/products/page")
    public Page<ProductDTO> getProductPage(@ParameterObject ProductFilter filter) {
        return productService.getPage(filter);
    }

    @GetMapping("/products/page/web")
    public Page<ProductDTO> getProductPageWeb(@ParameterObject ProductFilter filter) {
        return productService.getPageWeb(filter);
    }

    @GetMapping("/products/{id}/price-history")
    public List<ProductPriceHistoryDTO> getProductPriceHistory(@PathVariable Long id,
                                                               @RequestParam Long branchId) {
        return productService.getProductPriceHistory(id, branchId);
    }

    @PostMapping("/products/{id}/favorites")
    public ResponseEntity<Void> addToFavorites(@PathVariable Long id,
                                               @RequestParam Long branchId) {
        productService.addToFavorites(id, branchId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/product/productUnits/{product_id}/{branch_id}")
    @ResponseBody
    public ResponseEntity<List<UnitSelectItem>> getUnits(@PathVariable("product_id") Long productId,
                                                         @PathVariable("branch_id") Long branchId, @RequestParam(required = false, defaultValue = "false") Boolean withBalance) {
        return ResponseEntity.ok(productService.getUnits(productId, branchId, withBalance));
    }

    @DeleteMapping("/products/{id}/favorites")
    public ResponseEntity<Void> deleteFromFavorites(@PathVariable Long id,
                                                    @RequestParam Long branchId) {
        productService.deleteFromFavorites(id, branchId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/products/search")
    public ResponseEntity<List<ProductDTO>> search(@ParameterObject ProductFilter filter) {
        return ResponseEntity.ok(productService.search(filter));
    }


    @PostMapping(value = "/products/import/ref", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseBody
    public ResponseEntity<Resource> importProductsRef(@RequestParam(value = "uploadFile") MultipartFile file,
                                                      @ParameterObject ImportProductsFilter filter) {

        ByteArrayResource byteArrayResource = productService.importProductsRef(file, filter);
        return ServerUtils.getResponseEntity(byteArrayResource, "products");
    }

}
