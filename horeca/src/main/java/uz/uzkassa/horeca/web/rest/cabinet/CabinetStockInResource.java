package uz.uzkassa.horeca.web.rest.cabinet;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.horeca.constants.ApiConstants;
import uz.uzkassa.horeca.dto.base.CommonDTO;
import uz.uzkassa.horeca.dto.stockin.StockInDTO;
import uz.uzkassa.horeca.dto.stockin.StockInDetailDTO;
import uz.uzkassa.horeca.dto.stockin.StockInListDTO;
import uz.uzkassa.horeca.enums.CodeType;
import uz.uzkassa.horeca.enums.StockStatus;
import uz.uzkassa.horeca.filters.StockFilter;
import uz.uzkassa.horeca.interfaces.CrudRestAdvanced;
import uz.uzkassa.horeca.service.StockInService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.cabinetStockInRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CabinetStockInResource
        implements CrudRestAdvanced<Long, StockInDTO, StockInDetailDTO, StockInListDTO, StockFilter, StockStatus, CommonDTO> {

    StockInService stockInService;

    @PostMapping
    @Override
    public ResponseEntity<Long> create(@RequestBody StockInDTO stockInDTO) {
        return ResponseEntity.ok(stockInService.create(stockInDTO));
    }

    @PutMapping(ApiConstants.id)
    @Override
    public ResponseEntity<Long> update(@PathVariable Long id, @RequestBody StockInDTO stockInDTO) {
        return ResponseEntity.ok(stockInService.update(id, stockInDTO));
    }

    @Override
    @GetMapping(ApiConstants.id)
    public ResponseEntity<StockInDetailDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(stockInService.get(id));
    }

    @Override
    @GetMapping
    public ResponseEntity<Page<StockInListDTO>> getList(@ParameterObject StockFilter filter) {
        return ResponseEntity.ok(stockInService.getList(filter));
    }

    @Override
    @DeleteMapping(ApiConstants.id)
    public ResponseEntity<Void> delete(@PathVariable Long id, @RequestParam(required = false) LocalDateTime lastModifiedDate) {
        stockInService.delete(id);
        return ResponseEntity.ok().build();
    }

    @Override
    @GetMapping(ApiConstants.lookUp)
    public ResponseEntity<List<CommonDTO>> lookUp(@ParameterObject StockFilter filter) {
        return ResponseEntity.ok(stockInService.lookUp(filter));
    }

    @PatchMapping(ApiConstants.updateStatus)
    @Override
    public ResponseEntity<Void> updateStatus(@PathVariable Long id, @RequestParam("status") StockStatus status) {
        stockInService.updateStatus(id, status);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/codes/{stockItemId}")
    public ResponseEntity<Map<CodeType, List<String>>> getCodes(@PathVariable Long stockItemId) {
        return ResponseEntity.ok(stockInService.getCodes(stockItemId));
    }
}
