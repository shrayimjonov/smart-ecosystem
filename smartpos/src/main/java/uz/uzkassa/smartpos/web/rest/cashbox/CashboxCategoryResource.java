package uz.uzkassa.smartpos.web.rest.cashbox;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.smartpos.dto.catalog.CategoryListDto;
import uz.uzkassa.smartpos.service.CategoryService;

import java.util.List;

@RequestMapping("/api/v1/cashbox/categories")
@RequiredArgsConstructor
@RestController
public class CashboxCategoryResource {
    private final CategoryService categoryService;

    @GetMapping("/with-count")
    public List<CategoryListDto> findAll(Long branchId) {
        return categoryService.findAll(branchId);
    }
}
