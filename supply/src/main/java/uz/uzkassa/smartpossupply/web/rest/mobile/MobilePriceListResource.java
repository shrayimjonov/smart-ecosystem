package uz.uzkassa.smartpossupply.web.rest.mobile;

import io.swagger.v3.oas.annotations.Operation;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.smartpossupply.constants.ApiConstants;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.dto.pricelist.PriceListDetailDTO;
import uz.uzkassa.smartpossupply.dto.pricelist.PriceListListOfflineDTO;
import uz.uzkassa.smartpossupply.filters.PriceListFilter;
import uz.uzkassa.smartpossupply.service.PriceListService;
import uz.uzkassa.smartpossupply.utils.SecurityUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 29.05.2023
 */
@RestController
@RequestMapping(ApiConstants.mobilePriceListRootApi)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class MobilePriceListResource {

    PriceListService priceListService;

    @Operation(summary = ApiConstants.LookUp, operationId = "priceListLookUp")
    @GetMapping(ApiConstants.lookUp)
    public ResponseEntity<List<CommonDTO>> lookUp(@ParameterObject PriceListFilter filter) {
        if (SecurityUtils.getCurrentCompanyId() == null) {
            return ResponseEntity.ok(new ArrayList<>());
        }
        return ResponseEntity.ok(priceListService.lookUp(filter));
    }

    @GetMapping(ApiConstants.lookUp + ApiConstants.offline)
    public ResponseEntity<List<PriceListListOfflineDTO>> lookUpForOffline(@ParameterObject PriceListFilter filter) {
        if (SecurityUtils.getCurrentCompanyId() == null) {
            return ResponseEntity.ok(new ArrayList<>());
        }
        return ResponseEntity.ok(priceListService.lookUpForOffline(filter));
    }

    @Operation(summary = ApiConstants.Get, operationId = "getPriceList")
    @GetMapping(ApiConstants.id)
    public ResponseEntity<PriceListDetailDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(priceListService.get(id));
    }
}
