package uz.uzkassa.smartpos.web.rest.terminal;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.smartpos.dto.catalog.CategoryCreateDto;
import uz.uzkassa.smartpos.dto.catalog.CategoryTreeDto;
import uz.uzkassa.smartpos.service.CategoryService;

import java.util.List;

@RequestMapping("/api/v1/terminals/categories")
@RequiredArgsConstructor
@RestController
public class TerminalCategoryResource {
    private final CategoryService categoryService;

    @PostMapping
    public Long create(@RequestBody @Valid CategoryCreateDto dto) {
        return categoryService.create(dto);
    }

    @PutMapping("/{id}")
    public void update(@PathVariable Long id, @RequestBody @Valid CategoryCreateDto dto) {
        categoryService.update(id, dto);
    }

    @GetMapping
    public List<CategoryTreeDto> findAllWithoutProductsCount(@RequestParam Long branchId) {
        return categoryService.findAllWithoutProductsCount(branchId);
    }
}
