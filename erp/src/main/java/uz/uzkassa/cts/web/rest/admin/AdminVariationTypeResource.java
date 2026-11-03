package uz.uzkassa.cts.web.rest.admin;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.cts.constants.ApiConstants;
import uz.uzkassa.cts.dto.variationtype.VariationTypeDTO;
import uz.uzkassa.cts.dto.variationtype.VariationTypeLookUpDTO;
import uz.uzkassa.cts.dto.variationtype.admin.VariationTypeDetailAdminDTO;
import uz.uzkassa.cts.dto.variationtype.admin.VariationTypeListAdminDTO;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.service.VariationTypeService;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.adminVariationTypeRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AdminVariationTypeResource {

    VariationTypeService variationTypeService;

    @PostMapping
    public ResponseEntity<Long> create(@RequestBody VariationTypeDTO variationTypeDTO) {
        return ResponseEntity.ok(variationTypeService.create(variationTypeDTO));
    }

    @PutMapping(ApiConstants.id)
    public ResponseEntity<Long> update(@PathVariable Long id, @RequestBody VariationTypeDTO variationTypeDTO) {
        return ResponseEntity.ok(variationTypeService.update(id, variationTypeDTO));
    }

    @GetMapping(ApiConstants.id)
    public ResponseEntity<VariationTypeDetailAdminDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(variationTypeService.getForAdmin(id));
    }

    @GetMapping
    public ResponseEntity<Page<VariationTypeListAdminDTO>> getList(@ParameterObject BaseFilter filter) {
        return ResponseEntity.ok(variationTypeService.getListForAdmin(filter));
    }

    @GetMapping(ApiConstants.lookUp)
    public ResponseEntity<List<VariationTypeLookUpDTO>> lookUp(@ParameterObject BaseFilter filter) {
        return ResponseEntity.ok(variationTypeService.lookUp(filter));
    }
}
