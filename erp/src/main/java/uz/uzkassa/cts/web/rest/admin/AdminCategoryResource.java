package uz.uzkassa.cts.web.rest.admin;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.cts.constants.ApiConstants;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.dto.category.CategoryDTO;
import uz.uzkassa.cts.dto.category.admin.CategoryDetailAdminDTO;
import uz.uzkassa.cts.dto.category.admin.CategoryListAdminDTO;
import uz.uzkassa.cts.dto.category.admin.CategoryTreeAdminDTO;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.service.CategoryService;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.adminCategoryRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AdminCategoryResource {

    CategoryService categoryService;

    @PostMapping
    public ResponseEntity<Long> create(@RequestBody CategoryDTO categoryDTO) {
        return ResponseEntity.ok(categoryService.create(categoryDTO));
    }

    @PutMapping(ApiConstants.id)
    public ResponseEntity<Long> update(@PathVariable Long id, @RequestBody CategoryDTO categoryDTO) {
        return ResponseEntity.ok(categoryService.update(id, categoryDTO));
    }

    @GetMapping(ApiConstants.id)
    public ResponseEntity<CategoryDetailAdminDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(categoryService.getForAdmin(id));
    }

    @GetMapping
    public ResponseEntity<Page<CategoryListAdminDTO>> getList(@ParameterObject BaseFilter filter) {
        return ResponseEntity.ok(categoryService.getListForAdmin(filter));
    }

    @GetMapping(ApiConstants.lookUp)
    public ResponseEntity<List<CommonDTO>> lookUp(@ParameterObject BaseFilter filter) {
        return ResponseEntity.ok(categoryService.lookUp(filter));
    }

    @GetMapping(ApiConstants.tree)
    public ResponseEntity<List<CategoryTreeAdminDTO>> treeList(@ParameterObject BaseFilter filter) {
        return ResponseEntity.ok(categoryService.treeList(filter));
    }
}
