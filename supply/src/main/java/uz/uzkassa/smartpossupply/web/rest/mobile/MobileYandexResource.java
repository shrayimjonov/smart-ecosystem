package uz.uzkassa.smartpossupply.web.rest.mobile;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.smartpossupply.constants.ApiConstants;
import uz.uzkassa.smartpossupply.dto.address.nominatim.NominatimPoints;
import uz.uzkassa.smartpossupply.dto.address.yandex.YandexLocationNameRequest;
import uz.uzkassa.smartpossupply.dto.address.yandex.YandexLocationNameResponse;
import uz.uzkassa.smartpossupply.service.YandexService;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 26.04.2023
 */
@RestController
@RequestMapping(ApiConstants.mobileYandexRootApi)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class MobileYandexResource {

    YandexService yandexService;

    @Deprecated
    @Operation(summary = "Get location name by longitude and latitude", operationId = "getLocationNameByLongitudeAndLatitude")
    @PostMapping("/location/name")
    public ResponseEntity<YandexLocationNameResponse> getLocationName(@Valid @RequestBody YandexLocationNameRequest yandexLocationNameRequest) {
        return ResponseEntity.ok(yandexService.getLocationName(yandexLocationNameRequest));
    }

    @Deprecated
    @Operation(summary = "Get location points by district id", operationId = "getPointsByDistrictId")
    @GetMapping("/points/{districtId}")
    public ResponseEntity<NominatimPoints> getPointsByDistrictId(@PathVariable Long districtId) {
        return ResponseEntity.ok(yandexService.getPointsById(districtId));
    }
}
