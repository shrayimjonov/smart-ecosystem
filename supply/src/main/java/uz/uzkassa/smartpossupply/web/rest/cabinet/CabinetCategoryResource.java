package uz.uzkassa.smartpossupply.web.rest.cabinet;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.smartpossupply.constants.ApiConstants;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.dto.category.CategoryDTO;
import uz.uzkassa.smartpossupply.dto.category.CategoryDetailDTO;
import uz.uzkassa.smartpossupply.dto.category.CategoryListDTO;
import uz.uzkassa.smartpossupply.dto.category.CategoryTreeDTO;
import uz.uzkassa.smartpossupply.dto.payload.CategoryTransferPayload;
import uz.uzkassa.smartpossupply.dto.soliq.MxikDTO;
import uz.uzkassa.smartpossupply.dto.soliq.TasnifPackageDTO;
import uz.uzkassa.smartpossupply.enums.CommonStatus;
import uz.uzkassa.smartpossupply.filters.BaseFilter;
import uz.uzkassa.smartpossupply.service.CategoryService;
import uz.uzkassa.smartpossupply.utils.SecurityUtils;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.cabinetCategoryRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CabinetCategoryResource {

    CategoryService categoryService;

    @PostMapping
    public ResponseEntity<Long> create(@Valid @RequestBody CategoryDTO categoryDTO) {
        return ResponseEntity.ok(categoryService.create(categoryDTO));
    }

    @GetMapping(ApiConstants.id)
    public ResponseEntity<CategoryDetailDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(categoryService.get(id));
    }

    @PutMapping(ApiConstants.id)
    public ResponseEntity<Long> update(@PathVariable Long id, @Valid @RequestBody CategoryDTO categoryDTO) {
        return ResponseEntity.ok(categoryService.update(id, categoryDTO));
    }

    @GetMapping(ApiConstants.statuses)
    public ResponseEntity<EnumSet<CommonStatus>> getStatuses() {
        return ResponseEntity.ok(EnumSet.allOf(CommonStatus.class));
    }

    @PatchMapping(ApiConstants.updateStatus)
    public ResponseEntity<Void> updateStatus(@PathVariable Long id, @RequestParam("status") String status) {
        categoryService.updateStatus(id, CommonStatus.valueOf(status));
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<Page<CategoryListDTO>> getList(@ParameterObject BaseFilter filter) {
        if (SecurityUtils.getCurrentCompanyId() == null) {
            return ResponseEntity.ok(new PageImpl<>(new ArrayList<>(), filter.getPageable(), 0L));
        }
        return ResponseEntity.ok(categoryService.getList(filter));
    }

    @GetMapping(ApiConstants.lookUp)
    public ResponseEntity<List<CommonDTO>> lookUp(@ParameterObject BaseFilter filter) {
        if (SecurityUtils.getCurrentCompanyId() == null) {
            return ResponseEntity.ok(new ArrayList<>());
        }
        return ResponseEntity.ok(categoryService.lookUp(filter));
    }

    @PostMapping(ApiConstants.sync + "/{branchId}")
    public ResponseEntity<Void> sync(@PathVariable Long branchId) {
        categoryService.sync(branchId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/tree")
    public ResponseEntity<List<CategoryTreeDTO>> treeList(@ParameterObject BaseFilter filter) {
        return ResponseEntity.ok(categoryService.treeList(filter));
    }

    @DeleteMapping(ApiConstants.id)
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        categoryService.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping(ApiConstants.transfer)
    public ResponseEntity<Void> transfer(@RequestBody CategoryTransferPayload categoryTransferPayload) {
        categoryService.transfer(categoryTransferPayload);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/packages")
    public ResponseEntity<List<TasnifPackageDTO>> getPackages(@RequestParam String catalogCode) {
        if (!StringUtils.hasText(SecurityUtils.getCurrentCompanyTin())) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.ok(categoryService.getPackages(catalogCode, SecurityUtils.getCurrentCompanyTin()));
    }

    @GetMapping("/tasnif-categories")
    public ResponseEntity<List<MxikDTO>> getCategories(@RequestParam String tin) {
        return ResponseEntity.ok(categoryService.getCategories(tin));
    }
}
