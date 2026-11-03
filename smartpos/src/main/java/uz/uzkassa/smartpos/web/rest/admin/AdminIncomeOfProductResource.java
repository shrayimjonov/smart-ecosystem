package uz.uzkassa.smartpos.web.rest.admin;

import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.smartpos.dto.warehouse.v2.IncomeOfProductDtoV2;
import uz.uzkassa.smartpos.dto.warehouse.v2.IncomeOfProductListDto;
import uz.uzkassa.smartpos.filter.IncomeOfProductFilterV2;
import uz.uzkassa.smartpos.service.IncomeOfProductServiceV2;

@RequestMapping("/api/v1/admin/income-of-products")
@RestController
@RequiredArgsConstructor
public class AdminIncomeOfProductResource {
    private final IncomeOfProductServiceV2 incomeOfProductService;

    @GetMapping
    public Page<IncomeOfProductListDto> findAllByFilter(@ParameterObject IncomeOfProductFilterV2 filter) {
        return incomeOfProductService.findAllByFilter(filter);
    }

    @GetMapping("/{id}")
    public IncomeOfProductDtoV2 findById(@PathVariable Long id) {
        return incomeOfProductService.getById(id);
    }
}
