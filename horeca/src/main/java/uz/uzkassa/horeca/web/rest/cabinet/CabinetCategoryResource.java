package uz.uzkassa.horeca.web.rest.cabinet;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.horeca.constants.ApiConstants;
import uz.uzkassa.horeca.dto.category.*;
import uz.uzkassa.horeca.dto.tasnif.TasnifBasketProductResponseListDTO;
import uz.uzkassa.horeca.dto.tasnif.TasnifPackageDTO;
import uz.uzkassa.horeca.enums.CommonStatus;
import uz.uzkassa.horeca.filters.BaseFilter;
import uz.uzkassa.horeca.interfaces.CrudRestAdvanced;
import uz.uzkassa.horeca.service.CategoryService;
import uz.uzkassa.horeca.service.TasnifBasketProductService;
import uz.uzkassa.horeca.utils.HeaderUtils;

import java.time.LocalDateTime;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.cabinetCategoryRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CabinetCategoryResource
        implements CrudRestAdvanced<Long, CategoryDTO, CategoryDetailDTO, CategoryListDTO, BaseFilter, CommonStatus, CategoryLookUpDTO> {

    CategoryService categoryService;
    TasnifBasketProductService tasnifBasketProductService;

    @Override
    @PostMapping
    public ResponseEntity<Long> create(@RequestBody CategoryDTO categoryDTO) {
        return ResponseEntity.ok(categoryService.create(categoryDTO));
    }

    @Override
    @PutMapping(ApiConstants.id)
    public ResponseEntity<Long> update(@PathVariable Long id, @RequestBody CategoryDTO categoryDTO) {
        return ResponseEntity.ok(categoryService.update(id, categoryDTO));
    }

    @Override
    @GetMapping(ApiConstants.id)
    public ResponseEntity<CategoryDetailDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(categoryService.get(id));
    }

    @Override
    @GetMapping
    public ResponseEntity<Page<CategoryListDTO>> getList(@ParameterObject BaseFilter filter) {
        return ResponseEntity.ok(categoryService.getList(filter));
    }

    @Override
    @DeleteMapping(ApiConstants.id)
    public ResponseEntity<Void> delete(@PathVariable Long id, @RequestParam(required = false) LocalDateTime lastModifiedDate) {
        categoryService.delete(id);
        return ResponseEntity.ok().build();
    }

    @Override
    @GetMapping(ApiConstants.lookUp)
    public ResponseEntity<List<CategoryLookUpDTO>> lookUp(@ParameterObject BaseFilter filter) {
        return ResponseEntity.ok(categoryService.lookUp(filter));
    }

    @Override
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
    public ResponseEntity<List<CompanyCatalogListDTO>> getTasnifCatalog() {
        return ResponseEntity.ok(tasnifBasketProductService.getTasnifCatalog(null));
    }

    @PostMapping("/tasnif/sync")
    public ResponseEntity<TasnifBasketProductResponseListDTO> syncTasnifCatalog() {
        tasnifBasketProductService.sync(null);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Get packages by catalogCode")
    @GetMapping("/packages")
    public ResponseEntity<List<TasnifPackageDTO>> getPackages(@RequestParam String catalogCode) {
        if (StringUtils.isEmpty(HeaderUtils.getCurrentCompanyTIN())) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.ok(categoryService.getPackages(catalogCode, HeaderUtils.getCurrentCompanyTIN()));
    }

    @Operation(summary = "Transfer categories")
    @PostMapping("/transfer")
    public ResponseEntity<Void> transfer(@RequestBody TransferDTO transferDTO) {
        categoryService.transferCategory(transferDTO);
        return ResponseEntity.ok().build();
    }
}
