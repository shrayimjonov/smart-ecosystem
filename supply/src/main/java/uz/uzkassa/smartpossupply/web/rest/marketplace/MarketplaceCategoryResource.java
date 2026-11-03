package uz.uzkassa.smartpossupply.web.rest.marketplace;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.smartpossupply.constants.ApiConstants;
import uz.uzkassa.smartpossupply.dto.category.CategoryListDTO;
import uz.uzkassa.smartpossupply.filters.BaseFilter;
import uz.uzkassa.smartpossupply.service.CategoryService;

import java.util.ArrayList;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.marketplaceCategoryRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class MarketplaceCategoryResource {

    CategoryService categoryService;

    @GetMapping
    public ResponseEntity<Page<CategoryListDTO>> getList(@ParameterObject BaseFilter filter) {
        if (filter.getBranchId() == null) {
            return ResponseEntity.ok(new PageImpl<>(new ArrayList<>(), filter.getPageable(), 0L));
        }
        return ResponseEntity.ok(categoryService.getList(filter));
    }
}
