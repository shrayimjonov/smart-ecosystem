package uz.uzkassa.horeca.web.rest.mobile;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.horeca.constants.ApiConstants;
import uz.uzkassa.horeca.dto.product.CateringServiceDetailDTO;
import uz.uzkassa.horeca.service.CateringServiceService;
import uz.uzkassa.horeca.service.ProductService;
import uz.uzkassa.horeca.utils.HeaderUtils;

import static lombok.AccessLevel.PRIVATE;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 27.05.2024
 */
@RestController
@RequestMapping(ApiConstants.mobileProductRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class MobileProductResource {

    CateringServiceService cateringServiceService;

    @Operation(operationId = "getCateringService")
    @GetMapping(ApiConstants.cateringServiceApi)
    public ResponseEntity<CateringServiceDetailDTO> getCateringService(@RequestParam(required = false) Long branchId) {
        return ResponseEntity.ok(cateringServiceService.getCateringService(branchId));
    }
}
