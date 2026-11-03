package uz.uzkassa.horeca.web.rest.mobile;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.horeca.constants.ApiConstants;
import uz.uzkassa.horeca.dto.base.CommonDTO;
import uz.uzkassa.horeca.dto.hall.HallDetailDTO;
import uz.uzkassa.horeca.dto.hall.HallListDTO;
import uz.uzkassa.horeca.filters.BaseFilter;
import uz.uzkassa.horeca.service.HallService;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 02.02.2024
 */
@RestController
@RequestMapping(ApiConstants.mobileHallRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class MobileHallResource {

    HallService hallService;

    @Operation(operationId = "hallGetDetail")
    @GetMapping(ApiConstants.id)
    public ResponseEntity<HallDetailDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(hallService.get(id));
    }

    @Operation(operationId = "hallGetList")
    @GetMapping
    public ResponseEntity<Page<HallListDTO>> getList(@ParameterObject BaseFilter filter) {
        return ResponseEntity.ok(hallService.getList(filter));
    }

    @Operation(operationId = "hallLookUp")
    @GetMapping(ApiConstants.lookUp)
    public ResponseEntity<List<CommonDTO>> lookUp(@ParameterObject BaseFilter filter) {
        return ResponseEntity.ok(hallService.lookUp(filter));
    }

    @Operation(operationId = "hallItems")
    @GetMapping(ApiConstants.items)
    public ResponseEntity<List<CommonDTO>> getItems(@ParameterObject BaseFilter filter) {
        return ResponseEntity.ok(hallService.getItems(filter));
    }
}
