package uz.uzkassa.smartpos.web.rest.fashion;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.smartpos.dto.UnitDTO;
import uz.uzkassa.smartpos.dto.filter.ProductFilter;
import uz.uzkassa.smartpos.dto.product.AttributeDTO;
import uz.uzkassa.smartpos.dto.product.ProductItemDTO;
import uz.uzkassa.smartpos.enums.CompanyType;
import uz.uzkassa.smartpos.service.ProductItemService;
import uz.uzkassa.smartpos.service.UnitService;
import uz.uzkassa.smartpos.service.fashion.AttributeService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/fashion")
@RequiredArgsConstructor
public class FashionProductResource {
    private final ProductItemService productItemService;
    private final AttributeService attributeService;
    private final UnitService unitService;

    @GetMapping("/products/attributes")
    public List<AttributeDTO> getAttributes() {
        return attributeService.getAllAttributes();
    }

    @GetMapping("/products/page")
    public Page<ProductItemDTO> getProductPage(@ParameterObject ProductFilter filter) {
        return productItemService.getPage(filter);
    }

    @GetMapping("/products/units")
    public List<UnitDTO> getUnits() {
        return unitService.findAllByCompanyType(CompanyType.FASHION);
    }
}
