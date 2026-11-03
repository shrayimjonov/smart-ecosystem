package uz.uzkassa.horeca.web.rest.cabinet;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.horeca.constants.ApiConstants;
import uz.uzkassa.horeca.dto.stock.StockBalanceReportDTO;
import uz.uzkassa.horeca.dto.warehouse.WarehouseCheckDTO;
import uz.uzkassa.horeca.dto.warehouse.WarehouseCheckResponseDTO;
import uz.uzkassa.horeca.filters.BaseFilter;
import uz.uzkassa.horeca.service.WarehouseItemService;
import uz.uzkassa.horeca.utils.HeaderUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.cabinetWarehouseItemRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CabinetWarehouseItemResource {

    WarehouseItemService warehouseItemService;

    @GetMapping("/report")
    public ResponseEntity<Page<StockBalanceReportDTO>> getReport(BaseFilter filter) {
        if (HeaderUtils.getCurrentBranchId() == null) {
            return ResponseEntity.ok(new PageImpl<>(new ArrayList<>(), filter.getPageable(), 0L));
        }
        return ResponseEntity.ok(warehouseItemService.getReport(filter));
    }

    @GetMapping("/report-total-qty")
    public ResponseEntity<BigDecimal> getReportTotalQty(BaseFilter filter) {
        if (HeaderUtils.getCurrentBranchId() == null) {
            return ResponseEntity.ok(new BigDecimal(0));
        }
        return ResponseEntity.ok(warehouseItemService.getReportTotalQty(filter));
    }

    @GetMapping(ApiConstants.check)
    public ResponseEntity<List<WarehouseCheckResponseDTO>> checkExistingCount(@RequestBody WarehouseCheckDTO warehouseCheckDTO) {
        return ResponseEntity.ok(warehouseItemService.checkExistingCount(warehouseCheckDTO));
    }
}
