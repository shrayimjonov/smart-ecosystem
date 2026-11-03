package uz.uzkassa.cts.web.rest.admin;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.cts.constants.ApiConstants;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.dto.stocktransfer.StockTransferDTO;
import uz.uzkassa.cts.dto.stocktransfer.StockTransferDetailDTO;
import uz.uzkassa.cts.dto.stocktransfer.StockTransferListDTO;
import uz.uzkassa.cts.enums.StockTransferStatus;
import uz.uzkassa.cts.filter.StockFilter;
import uz.uzkassa.cts.filter.StockTransferFilter;
import uz.uzkassa.cts.service.StockTransferService;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;


@RestController
@RequestMapping(ApiConstants.adminStockTransferRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AdminStockTransferResource {

    StockTransferService stockTransferService;

    @PostMapping
    public ResponseEntity<Long> create(@RequestBody StockTransferDTO stockTransferDTO) {
        return ResponseEntity.ok(stockTransferService.create(stockTransferDTO));
    }

    @PutMapping(ApiConstants.id)
    public ResponseEntity<Long> update(@PathVariable Long id, @RequestBody StockTransferDTO stockTransferDTO) {
        return ResponseEntity.ok(stockTransferService.update(id, stockTransferDTO));
    }

    @GetMapping(ApiConstants.id)
    public ResponseEntity<StockTransferDetailDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(stockTransferService.get(id));
    }

    @GetMapping
    public ResponseEntity<Page<StockTransferListDTO>> getList(@ParameterObject StockTransferFilter filter) {
        return ResponseEntity.ok(stockTransferService.getList(filter));
    }

    @GetMapping("/items" + ApiConstants.lookUp)
    public ResponseEntity<List<CommonDTO>> getItemsLookUp(@ParameterObject StockFilter filter) {
        return ResponseEntity.ok(stockTransferService.getItemsLookUp(filter));
    }

    @DeleteMapping(ApiConstants.id)
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        stockTransferService.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping(ApiConstants.lookUp)
    public ResponseEntity<List<CommonDTO>> lookUp(@ParameterObject StockTransferFilter filter) {
        return ResponseEntity.ok(stockTransferService.lookUp(filter));
    }

    @PatchMapping(ApiConstants.updateStatus)
    public ResponseEntity<Void> updateStatus(@PathVariable Long id, @RequestParam("status") StockTransferStatus status) {
        stockTransferService.updateStatus(id, status);
        return ResponseEntity.ok().build();
    }
}
