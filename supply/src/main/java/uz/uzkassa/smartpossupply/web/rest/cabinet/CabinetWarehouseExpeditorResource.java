package uz.uzkassa.smartpossupply.web.rest.cabinet;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.smartpossupply.constants.ApiConstants;
import uz.uzkassa.smartpossupply.dto.warehouseexpeditor.WarehouseExpeditorCarListDTO;
import uz.uzkassa.smartpossupply.dto.warehouseexpeditor.WarehouseExpeditorListDTO;
import uz.uzkassa.smartpossupply.dto.warehouseexpeditor.WarehouseExpeditorStatisticsDTO;
import uz.uzkassa.smartpossupply.filters.StockFilter;
import uz.uzkassa.smartpossupply.service.WarehouseExpeditorService;
import uz.uzkassa.smartpossupply.utils.SecurityUtils;

import java.util.ArrayList;

import static lombok.AccessLevel.PRIVATE;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 29.11.2024
 */
@RestController
@RequestMapping(ApiConstants.cabinetWarehouseExpeditorRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CabinetWarehouseExpeditorResource {

    WarehouseExpeditorService warehouseExpeditorService;

    @GetMapping
    public ResponseEntity<Page<WarehouseExpeditorListDTO>> getList(@ParameterObject StockFilter filter) {
        if (SecurityUtils.getCurrentCompanyId() == null) {
            return ResponseEntity.ok(new PageImpl<>(new ArrayList<>(), filter.getPageable(), 0L));
        }
        return ResponseEntity.ok(warehouseExpeditorService.getList(filter));
    }

    @GetMapping(ApiConstants.statistics)
    public ResponseEntity<WarehouseExpeditorStatisticsDTO> getStatistics(@ParameterObject StockFilter filter) {
        if (SecurityUtils.getCurrentCompanyId() == null) {
            return ResponseEntity.ok(new WarehouseExpeditorStatisticsDTO());
        }
        return ResponseEntity.ok(warehouseExpeditorService.getStatistics(filter));
    }

    @GetMapping("/cars")
    public ResponseEntity<Page<WarehouseExpeditorCarListDTO>> getCarList(StockFilter filter) {
        if (SecurityUtils.getCurrentCompanyId() == null) {
            return ResponseEntity.ok(new PageImpl<>(new ArrayList<>(), filter.getPageable(), 0L));
        }
        return ResponseEntity.ok(warehouseExpeditorService.getCarList(filter));
    }
}
