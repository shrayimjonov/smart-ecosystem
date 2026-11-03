package uz.uzkassa.smartpossupply.web.rest.pub;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.smartpossupply.constants.ApiConstants;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.filters.BaseFilter;
import uz.uzkassa.smartpossupply.service.ActivityTypeService;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 03.02.2023 13:05
 */

@RestController
@RequestMapping(ApiConstants.publicActivityTypeRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class PublicActivityTypeResource {

    ActivityTypeService activityTypeService;

    @GetMapping
    public ResponseEntity<List<CommonDTO>> getList(@ParameterObject BaseFilter filter) {
        return ResponseEntity.ok(activityTypeService.getList(filter));
    }
}
