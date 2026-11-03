package uz.uzkassa.smartpos.web.rest.cabinet;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.smartpos.dto.catalog.*;
import uz.uzkassa.smartpos.dto.filter.ProductFilter;
import uz.uzkassa.smartpos.filter.ProductBranchFilter;
import uz.uzkassa.smartpos.service.ProductService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/cabinet/products")
@RequiredArgsConstructor
public class CabinetProductResource {
    private final ProductService productService;

    @GetMapping("/statistics")
    public ProductStatisticsDTO statistics(@ParameterObject ProductFilter filter) {
        return productService.getProductStatistics(filter);
    }

    @GetMapping
    public Page<ProductListRecord> page(@ParameterObject final ProductBranchFilter filter) {
        return productService.pageCabinet(filter);
    }

    @GetMapping("/lookup")
    public List<ProductLookupRecord> lookup(@ParameterObject final ProductBranchFilter filter) {
        return productService.lookup(filter);
    }

    @PostMapping
    public Long create(@RequestParam(required = false) final Long branchId, @RequestBody @Valid final ProductCreateDto dto) {
        return productService.createForCabinet(branchId, dto);
    }

    @PutMapping("/{id}")
    public void update(@PathVariable final Long id, @RequestParam(required = false) final Long branchId, @RequestBody @Valid final ProductCreateDto dto) {
        productService.update(id, branchId, dto);
    }

    @GetMapping("/{id}")
    public ProductDetailDto findById(@PathVariable final Long id, @RequestParam(required = false) final Long branchId) {
        return productService.findById(id, branchId);
    }

    @PatchMapping
    public void updatePrice(@RequestBody @Valid final UpdateProductPriceRecord dto) {
        productService.updatePrice(dto);
    }
}

