package uz.uzkassa.smartpos.web.rest.fashion;

import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.smartpos.dto.fashion.catalog.CashboxProductListDto;
import uz.uzkassa.smartpos.dto.filter.CashboxProductFilter;
import uz.uzkassa.smartpos.service.ProductService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/fashion/cashbox/products")
public class FashionCashboxProductsResource {
    private final ProductService productService;

    @GetMapping("/page")
    public Page<CashboxProductListDto> productPage(@ParameterObject CashboxProductFilter filter) {
        return productService.getCashboxPage(filter);
    }
}
