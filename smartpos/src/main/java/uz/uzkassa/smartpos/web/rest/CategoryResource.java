package uz.uzkassa.smartpos.web.rest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.smartpos.dto.CategoryDTO;
import uz.uzkassa.smartpos.dto.TransferCatalogDTO;
import uz.uzkassa.smartpos.dto.base.CommonDTO;
import uz.uzkassa.smartpos.dto.filter.BaseFilter;
import uz.uzkassa.smartpos.exceptions.EntityNotFound;
import uz.uzkassa.smartpos.service.CategoryService;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CategoryResource {
    private final CategoryService categoryService;

    @PostMapping("/categories")
    public ResponseEntity<CategoryDTO> createCategory(@Valid @RequestBody CategoryDTO categoryDTO) {
        CategoryDTO result = categoryService.createNew(categoryDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @DeleteMapping("/categories/delete")
    public ResponseEntity<Void> deleteCategory(@RequestParam Long categoryId, @RequestParam Long branchId) {
        categoryService.delete(categoryId, branchId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/categories")
    public ResponseEntity<CategoryDTO> updateCategory(@Valid @RequestBody CategoryDTO categoryDTO) throws EntityNotFound {
        CategoryDTO result = categoryService.update(categoryDTO);
        return ResponseEntity.ok().body(result);
    }

    @PutMapping("/categories/transfer-catalog")
    public ResponseEntity<Void> transferCatalog(@Valid @RequestBody TransferCatalogDTO dto) {
        if (CollectionUtils.isNotEmpty(dto.getToBranchIds())) {
            dto.getToBranchIds().remove(dto.getFromBranchId());
        }
        categoryService.transferCatalog(dto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/categories/enabled")
    public List<CategoryDTO> getEnabledCategories(@RequestParam(required = false) Long branchId) {
        return categoryService.getEnabledCategories(branchId);
    }

    @GetMapping("/categories/search")
    public ResponseEntity<List<CommonDTO>> search(@ParameterObject BaseFilter filter) {
        return ResponseEntity.ok(categoryService.choose(filter));
    }
}
