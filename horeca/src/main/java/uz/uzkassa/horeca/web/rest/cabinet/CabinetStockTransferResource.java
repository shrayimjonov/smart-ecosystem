package uz.uzkassa.horeca.web.rest.cabinet;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.horeca.constants.ApiConstants;
import uz.uzkassa.horeca.dto.base.CommonDTO;
import uz.uzkassa.horeca.dto.stocktransfer.StockTransferDTO;
import uz.uzkassa.horeca.dto.stocktransfer.StockTransferDetailDTO;
import uz.uzkassa.horeca.dto.stocktransfer.StockTransferListDTO;
import uz.uzkassa.horeca.dto.stocktransfer.StockTransferUpdateStatusDTO;
import uz.uzkassa.horeca.enums.StockTransferStatus;
import uz.uzkassa.horeca.enums.StockTransferType;
import uz.uzkassa.horeca.filters.StockTransferFilter;
import uz.uzkassa.horeca.interfaces.CrudRestAdvanced;
import uz.uzkassa.horeca.service.StockTransferService;

import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.cabinetStockTransferRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CabinetStockTransferResource
        implements
        CrudRestAdvanced<Long, StockTransferDTO, StockTransferDetailDTO, StockTransferListDTO, StockTransferFilter, StockTransferUpdateStatusDTO, CommonDTO> {

    StockTransferService stockTransferService;

    @PostMapping
    @Override
    public ResponseEntity<Long> create(@RequestBody StockTransferDTO stockTransferDTO) {
        return ResponseEntity.ok(stockTransferService.create(stockTransferDTO));
    }

    @PutMapping(ApiConstants.id)
    @Override
    public ResponseEntity<Long> update(@PathVariable Long id, @RequestBody StockTransferDTO stockTransferDTO) {
        return ResponseEntity.ok(stockTransferService.update(id, stockTransferDTO));
    }

    @Override
    @GetMapping(ApiConstants.id)
    public ResponseEntity<StockTransferDetailDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(stockTransferService.get(id));
    }

    @Override
    @GetMapping
    public ResponseEntity<Page<StockTransferListDTO>> getList(@ParameterObject StockTransferFilter filter) {
        return ResponseEntity.ok(stockTransferService.getList(filter));
    }

    @Override
    @DeleteMapping(ApiConstants.id)
    public ResponseEntity<Void> delete(@PathVariable Long id, @RequestParam(required = false) LocalDateTime lastModifiedDate) {
        stockTransferService.delete(id);
        return ResponseEntity.ok().build();
    }

    @Override
    @GetMapping(ApiConstants.lookUp)
    public ResponseEntity<List<CommonDTO>> lookUp(@ParameterObject StockTransferFilter filter) {
        return ResponseEntity.ok(stockTransferService.lookUp(filter));
    }

    @PatchMapping(ApiConstants.updateStatus)
    @Override
    public ResponseEntity<Void> updateStatus(@PathVariable Long id, @RequestBody StockTransferUpdateStatusDTO updateStatusDTO) {
        stockTransferService.updateStatus(id, updateStatusDTO);
        return ResponseEntity.ok().build();
    }

    @GetMapping(ApiConstants.statuses)
    public ResponseEntity<EnumSet<StockTransferStatus>> getStatuses() {
        return ResponseEntity.ok(EnumSet.allOf(StockTransferStatus.class));
    }

    @GetMapping(ApiConstants.types)
    public ResponseEntity<EnumSet<StockTransferType>> getTypes() {
        return ResponseEntity.ok(EnumSet.allOf(StockTransferType.class));
    }
}
