package uz.uzkassa.cts.web.rest.admin;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.cts.constants.ApiConstants;
import uz.uzkassa.cts.dto.categoryfield.CategoryFieldBatchDTO;
import uz.uzkassa.cts.dto.categoryfield.CategoryFieldDetailAdminDTO;
import uz.uzkassa.cts.dto.categoryfield.CategoryFieldValueDetailAdminDTO;
import uz.uzkassa.cts.service.CategoryFieldService;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.adminCategoryFieldRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AdminCategoryFieldResource {

    CategoryFieldService categoryFieldService;

    @PostMapping(ApiConstants.batch)
    public ResponseEntity<Void> createBatch(@RequestBody CategoryFieldBatchDTO categoryFieldBatchDTO) {
        categoryFieldService.createBatch(categoryFieldBatchDTO);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{categoryId}")
    public ResponseEntity<CategoryFieldDetailAdminDTO> getByCategoryId(@PathVariable Long categoryId) {
        return ResponseEntity.ok().body(categoryFieldService.getByCategoryId(categoryId));
    }

    @GetMapping("/values/{categoryId}")
    public ResponseEntity<CategoryFieldValueDetailAdminDTO> getFieldAndValuesByCategoryId(@PathVariable Long categoryId) {
        return ResponseEntity.ok().body(categoryFieldService.getFieldAndValuesByCategoryId(categoryId));
    }
}
