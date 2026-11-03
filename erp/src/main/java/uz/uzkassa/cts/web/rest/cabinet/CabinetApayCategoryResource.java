package uz.uzkassa.cts.web.rest.cabinet;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.cts.constants.ApiConstants;
import uz.uzkassa.cts.dto.apay.category.APayCategoryDTO;
import uz.uzkassa.cts.filter.ApayCategoryFilter;
import uz.uzkassa.cts.service.APayCategoryService;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.cabinetApayCategoryRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CabinetApayCategoryResource {

    APayCategoryService aPayCategoryService;

    @GetMapping(ApiConstants.lookUp)
    public ResponseEntity<List<APayCategoryDTO>> lookUp(@ParameterObject ApayCategoryFilter filter) {
        return ResponseEntity.ok(aPayCategoryService.lookUp(filter));
    }
}
