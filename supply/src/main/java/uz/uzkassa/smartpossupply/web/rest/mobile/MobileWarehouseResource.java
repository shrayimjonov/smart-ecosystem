package uz.uzkassa.smartpossupply.web.rest.mobile;

import io.swagger.v3.oas.annotations.Operation;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.smartpossupply.constants.ApiConstants;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.dto.warehouse.WarehouseLookUpDTO;
import uz.uzkassa.smartpossupply.filters.BaseFilter;
import uz.uzkassa.smartpossupply.filters.WarehouseLookUpOfflineFilter;
import uz.uzkassa.smartpossupply.service.WarehouseService;
import uz.uzkassa.smartpossupply.utils.SecurityUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 03.02.2023 16:37
 */
@RestController
@RequestMapping(ApiConstants.mobileWarehouseRootApi)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class MobileWarehouseResource {

    WarehouseService warehouseService;

    @Operation(summary = ApiConstants.LookUp, operationId ="warehouseLookup")
    @GetMapping(ApiConstants.lookUp)
    public ResponseEntity<List<CommonDTO>> lookUp(@ParameterObject BaseFilter filter) {
        if (SecurityUtils.getCurrentCompanyId() == null) {
            return ResponseEntity.ok(new ArrayList<>());
        }
        return ResponseEntity.ok(warehouseService.lookUp(filter));
    }

    @GetMapping(ApiConstants.lookUp + "/offline")
    public ResponseEntity<List<WarehouseLookUpDTO>> lookUpForOffline(@ParameterObject WarehouseLookUpOfflineFilter filter) {
        if (CollectionUtils.isEmpty(SecurityUtils.getCurrentBranchIds())) {
            return ResponseEntity.ok(new ArrayList<>());
        }
        filter.setBranchIds(SecurityUtils.getCurrentBranchIds());
        return ResponseEntity.ok(warehouseService.lookUpForOffline(filter));
    }

}
