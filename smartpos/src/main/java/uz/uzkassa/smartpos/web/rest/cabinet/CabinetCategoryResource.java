package uz.uzkassa.smartpos.web.rest.cabinet;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.smartpos.dto.catalog.CategoryCreateDto;
import uz.uzkassa.smartpos.dto.catalog.CategoryListDto;
import uz.uzkassa.smartpos.dto.catalog.CategoryTreeDto;
import uz.uzkassa.smartpos.service.CategoryService;

import java.util.List;

@RequestMapping("/api/v1/cabinet/categories")
@RequiredArgsConstructor
@RestController
public class CabinetCategoryResource {
    private final CategoryService categoryService;

    @PostMapping
    public Long create(@RequestBody @Valid CategoryCreateDto dto) {
        return categoryService.create(dto);
    }

    @PutMapping("/{id}")
    public void update(@PathVariable Long id, @RequestBody @Valid CategoryCreateDto dto) {
        categoryService.update(id, dto);
    }

    @GetMapping("/with-count")
    public List<CategoryListDto> findAll(Long branchId) {
        return categoryService.findAll(branchId);
    }

    @GetMapping
    public List<CategoryTreeDto> findAllWithoutProductsCount(@RequestParam Long branchId) {
        return categoryService.findAllWithoutProductsCount(branchId);
    }
}
