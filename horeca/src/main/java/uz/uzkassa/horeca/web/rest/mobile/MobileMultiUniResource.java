package uz.uzkassa.horeca.web.rest.mobile;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.horeca.constants.ApiConstants;
import uz.uzkassa.horeca.dto.unit.MultiUnitListDTO;
import uz.uzkassa.horeca.filters.MultiUnitFilter;
import uz.uzkassa.horeca.service.MultiUnitService;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 16.02.2024
 */
@RestController
@RequestMapping(ApiConstants.mobileMultiUnitRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class MobileMultiUniResource {

    MultiUnitService multiUnitService;

    @Operation(operationId = "multiUnitGetList")
    @GetMapping
    public ResponseEntity<List<MultiUnitListDTO>> getList(@ParameterObject MultiUnitFilter filter) {
        return ResponseEntity.ok(multiUnitService.getList(filter.getMenuItemId()));
    }
}
