package uz.uzkassa.cts.web.rest.admin;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.cts.constants.ApiConstants;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.dto.variationoption.VariationOptionDTO;
import uz.uzkassa.cts.dto.variationoption.admin.VariationOptionDetailAdminDTO;
import uz.uzkassa.cts.dto.variationoption.admin.VariationOptionListAdminDTO;
import uz.uzkassa.cts.filter.VariationOptionFilter;
import uz.uzkassa.cts.service.VariationOptionService;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.adminVariationOptionRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AdminVariationOptionResource {

    VariationOptionService variationOptionService;

    @PostMapping
    public ResponseEntity<Long> create(@RequestBody VariationOptionDTO variationOptionDTO) {
        return ResponseEntity.ok(variationOptionService.create(variationOptionDTO));
    }

    @PutMapping(ApiConstants.id)
    public ResponseEntity<Long> update(@PathVariable Long id, @RequestBody VariationOptionDTO variationOptionDTO) {
        return ResponseEntity.ok(variationOptionService.update(id, variationOptionDTO));
    }

    @GetMapping(ApiConstants.id)
    public ResponseEntity<VariationOptionDetailAdminDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(variationOptionService.getForAdmin(id));
    }

    @GetMapping
    public ResponseEntity<Page<VariationOptionListAdminDTO>> getList(@ParameterObject VariationOptionFilter filter) {
        return ResponseEntity.ok(variationOptionService.getListForAdmin(filter));
    }

    @GetMapping(ApiConstants.lookUp)
    public ResponseEntity<List<CommonDTO>> lookUp(@ParameterObject VariationOptionFilter filter) {
        return ResponseEntity.ok(variationOptionService.lookUp(filter));
    }
}
