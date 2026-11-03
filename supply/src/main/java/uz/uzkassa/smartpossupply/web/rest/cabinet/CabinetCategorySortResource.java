package uz.uzkassa.smartpossupply.web.rest.cabinet;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.smartpossupply.constants.ApiConstants;
import uz.uzkassa.smartpossupply.dto.category.CategorySortDTO;
import uz.uzkassa.smartpossupply.service.CategorySortService;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.cabinetCategorySortRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CabinetCategorySortResource {

    CategorySortService categorySortService;

    @Operation(summary = ApiConstants.Create)
    @PostMapping
    public ResponseEntity<Void> create(@RequestBody CategorySortDTO categorySortDTO) {
        categorySortService.create(categorySortDTO);
        return ResponseEntity.ok().build();
    }
}
