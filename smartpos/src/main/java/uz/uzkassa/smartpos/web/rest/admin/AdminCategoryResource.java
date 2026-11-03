package uz.uzkassa.smartpos.web.rest.admin;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.smartpos.dto.CategoryDTO;
import uz.uzkassa.smartpos.enums.CompanyType;
import uz.uzkassa.smartpos.exceptions.BadRequestException;
import uz.uzkassa.smartpos.exceptions.EntityNotFound;
import uz.uzkassa.smartpos.service.CategoryService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin/categories")
@RequiredArgsConstructor
public class AdminCategoryResource {
    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<CategoryDTO> createCategory(@Valid @RequestBody final CategoryDTO categoryDTO) {
        return Optional.ofNullable(categoryDTO)
            .map(categoryService::createNewCategory)
            .map(result -> ResponseEntity.status(HttpStatus.CREATED).body(result))
            .orElseThrow(BadRequestException::new);
    }

    @PutMapping
    public ResponseEntity<CategoryDTO> updateCategory(@Valid @RequestBody final CategoryDTO categoryDTO) throws EntityNotFound {
        return Optional.ofNullable(categoryDTO)
            .map(categoryService::updateCategory)
            .map(ResponseEntity::ok)
            .orElseThrow(BadRequestException::new);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable("id") Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<CategoryDTO>> getCategories(@RequestParam final CompanyType companyType) {
        return Optional.ofNullable(companyType)
            .map(categoryService::getCategories)
            .map(ResponseEntity::ok)
            .orElseThrow(BadRequestException::new);
    }
}
