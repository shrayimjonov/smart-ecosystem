package uz.uzkassa.horeca.web.rest.admin;

import io.swagger.v3.oas.annotations.Operation;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.horeca.constants.ApiConstants;
import uz.uzkassa.horeca.dto.category.*;
import uz.uzkassa.horeca.dto.tasnif.TasnifBasketProductResponseListDTO;
import uz.uzkassa.horeca.dto.tasnif.TasnifPackageDTO;
import uz.uzkassa.horeca.enums.CommonStatus;
import uz.uzkassa.horeca.filters.BaseFilter;
import uz.uzkassa.horeca.service.CategoryService;
import uz.uzkassa.horeca.service.TasnifBasketProductService;

import java.util.List;

@RestController
@RequestMapping(ApiConstants.adminCategoryRootApi)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AdminCategoryResource {

    CategoryService categoryService;
    TasnifBasketProductService tasnifBasketProductService;

    @PostMapping
    public ResponseEntity<Long> create(@RequestBody CategoryDTO categoryDTO) {
        return ResponseEntity.ok(categoryService.create(categoryDTO));
    }

    @PutMapping(ApiConstants.id)
    public ResponseEntity<Long> update(@PathVariable Long id, @RequestBody  CategoryDTO categoryDTO) {
        return ResponseEntity.ok(categoryService.update(id, categoryDTO));
    }

    @GetMapping(ApiConstants.id)
    public ResponseEntity<CategoryDetailDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(categoryService.get(id));
    }

    @GetMapping
    public ResponseEntity<Page<CategoryListDTO>> getList(@ParameterObject BaseFilter filter) {
        return ResponseEntity.ok(categoryService.getList(filter));
    }

    @DeleteMapping(ApiConstants.id)
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        categoryService.delete(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping(ApiConstants.lookUp)
    public ResponseEntity<List<CategoryLookUpDTO>> lookUp(@ParameterObject BaseFilter filter) {
        return ResponseEntity.ok(categoryService.lookUp(filter));
    }

    @PatchMapping(ApiConstants.id)
    public ResponseEntity<Void> updateStatus(@PathVariable Long id, @RequestParam("status") CommonStatus status) {
        categoryService.updateStatus(id, status);
        return ResponseEntity.ok().build();
    }

    @GetMapping(ApiConstants.tree)
    public ResponseEntity<List<CategoryTreeDTO>> treeList(@ParameterObject BaseFilter filter) {
        return ResponseEntity.ok(categoryService.treeList(filter));
    }

    @GetMapping("/tasnif")
    public ResponseEntity<List<CompanyCatalogListDTO>> getTasnifCatalog(@RequestParam(value = "companyId") Long companyId) {
        return ResponseEntity.ok(tasnifBasketProductService.getTasnifCatalog(companyId));
    }

    @PostMapping("/tasnif/sync")
    public ResponseEntity<TasnifBasketProductResponseListDTO> syncTasnifCatalog(@RequestParam(value = "companyId") Long companyId) {
        tasnifBasketProductService.sync(companyId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/tasnif/catering-service/sync")
    public ResponseEntity<Void> syncTasnifCateringService() {
        categoryService.syncTasnifCateringService();
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Get packages by catalogCode")
    @GetMapping("/packages/{tin}")
    public ResponseEntity<List<TasnifPackageDTO>> getPackages(@PathVariable String tin, @RequestParam String catalogCode) {
        return ResponseEntity.ok(categoryService.getPackages(catalogCode, tin));
    }
}
