package uz.uzkassa.horeca.web.rest.cabinet;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.horeca.constants.ApiConstants;
import uz.uzkassa.horeca.dto.base.CommonDTO;
import uz.uzkassa.horeca.dto.stockout.StockOutDTO;
import uz.uzkassa.horeca.dto.stockout.StockOutDetailDTO;
import uz.uzkassa.horeca.dto.stockout.StockOutListDTO;
import uz.uzkassa.horeca.enums.CodeType;
import uz.uzkassa.horeca.enums.StockStatus;
import uz.uzkassa.horeca.filters.StockFilter;
import uz.uzkassa.horeca.service.StockOutService;
import uz.uzkassa.horeca.utils.HeaderUtils;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.cabinetStockOutRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CabinetStockOutResource {

    StockOutService stockOutService;

    @PostMapping
    public ResponseEntity<Long> create(@RequestBody StockOutDTO stockDTO) {
        return ResponseEntity.ok(stockOutService.create(stockDTO));
    }

    @PutMapping(ApiConstants.id)
    public ResponseEntity<Long> update(@PathVariable Long id, @RequestBody StockOutDTO stockDTO) {
        return ResponseEntity.ok(stockOutService.update(id, stockDTO));
    }

    @GetMapping(ApiConstants.id)
    public ResponseEntity<StockOutDetailDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(stockOutService.get(id));
    }

    @GetMapping("/codes/{stockItemId}")
    public ResponseEntity<Map<CodeType, List<String>>> getCodes(@PathVariable Long stockItemId) {
        return ResponseEntity.ok(stockOutService.getCodes(stockItemId));
    }

    @GetMapping
    public ResponseEntity<Page<StockOutListDTO>> getList(@ParameterObject StockFilter filter) {
        if (HeaderUtils.getCurrentCompanyId() == null) {
            return ResponseEntity.ok(new PageImpl<>(new ArrayList<>(), filter.getPageable(), 0L));
        }
        return ResponseEntity.ok(stockOutService.getList(filter));
    }

    @GetMapping(ApiConstants.lookUp)
    public ResponseEntity<List<CommonDTO>> lookUp(@ParameterObject StockFilter filter) {
        if (HeaderUtils.getCurrentCompanyId() == null) {
            return ResponseEntity.ok(new ArrayList<>());
        }
        return ResponseEntity.ok(stockOutService.lookUp(filter));
    }

    @GetMapping(ApiConstants.statuses)
    public ResponseEntity<EnumSet<StockStatus>> getStatuses() {
        return ResponseEntity.ok(EnumSet.allOf(StockStatus.class));
    }

    @PatchMapping(ApiConstants.updateStatus)
    public ResponseEntity<Void> updateStatus(@PathVariable Long id, @RequestParam("status") StockStatus status) {
        stockOutService.updateStatus(id, status);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping(ApiConstants.id)
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        stockOutService.delete(id);
        return ResponseEntity.ok().build();
    }
}
