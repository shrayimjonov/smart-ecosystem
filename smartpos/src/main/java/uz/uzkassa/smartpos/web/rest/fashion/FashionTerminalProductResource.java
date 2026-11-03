package uz.uzkassa.smartpos.web.rest.fashion;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.smartpos.dto.filter.ProductFilter;
import uz.uzkassa.smartpos.dto.product.*;
import uz.uzkassa.smartpos.service.ProductItemService;
import uz.uzkassa.smartpos.service.fashion.AttributeService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/fashion/terminals/products")
@RequiredArgsConstructor
public class FashionTerminalProductResource {
    private final AttributeService attributeService;
    private final ProductItemService productItemService;

    @GetMapping("/attributes")
    public List<AttributeDTO> getAttributes(@RequestParam final String key) {
        return attributeService.getAttributesForCabinet(key);
    }

    @PostMapping("/{productId}/color-group")
    public void createColorGroup(@PathVariable final Long productId, @RequestParam(required = false) final Long branchId, @RequestBody @Valid final VariationsDTO dto) {
        productItemService.createColorGroup(productId, branchId, dto);
    }

    @GetMapping("/{productId}/color-group")
    public Page<ProductColorGroupDTO> getColorGroups(@PathVariable final Long productId, @ParameterObject final ProductFilter filter) {
        return productItemService.getColorGroups(productId, filter);
    }

    @GetMapping("/{productId}/color-group/list")
    public List<ProductColorGroupDTO> getColorGroupList(@PathVariable final Long productId, @ParameterObject final ProductFilter filter) {
        return productItemService.getColorGroupList(productId, filter);
    }

    @GetMapping("/color-group/{colorGroupId}")
    public ProductColorGroupDTO getColorGroup(@PathVariable final Long colorGroupId, @RequestParam final Long branchId) {
        return productItemService.getColorGroup(colorGroupId, branchId);
    }

    @PutMapping("/color-group/{colorGroupId}")
    public void updateColorGroup(@PathVariable final Long colorGroupId, @RequestBody @Valid final ColorGroupUpdateDTO dto) {
        productItemService.updateColorGroup(colorGroupId, dto);
    }

    @DeleteMapping("/color-group/{colorGroupId}")
    public void deleteColorGroup(@PathVariable final Long colorGroupId, @RequestParam final Long branchId) {
        productItemService.deleteColorGroup(colorGroupId, branchId);
    }

    @GetMapping("/{productId}/items")
    public Page<ProductItemDTO> getProductItems(@PathVariable final Long productId, @ParameterObject final ProductFilter filter) {
        return productItemService.getItems(productId, filter);
    }

    @PutMapping("/items/{productItemId}")
    public ProductItemDTO updateProductItem(@PathVariable final Long productItemId, @RequestBody final ProductItemDTO dto) {
        return productItemService.updateItem(productItemId, dto);
    }

    @DeleteMapping("/items/{productItemId}")
    public void deleteProductItem(@PathVariable final Long productItemId, @RequestParam final Long branchId) {
        productItemService.deleteItem(productItemId, branchId);
    }

    @DeleteMapping("/color-group/images/{groupImageId}")
    public void deleteGroupImage(@PathVariable final Long groupImageId) {
        productItemService.deleteGroupImage(groupImageId);
    }
}
