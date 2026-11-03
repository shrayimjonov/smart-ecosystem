package uz.uzkassa.smartpossupply.web.rest.terminal;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.smartpossupply.constants.ApiConstants;
import uz.uzkassa.smartpossupply.dto.stock.CheckStatusResponseDTO;
import uz.uzkassa.smartpossupply.dto.stocktransfer.StockTransferUpdateStatusDTO;
import uz.uzkassa.smartpossupply.dto.stocktransfer.TerminalStockTransferDetailDTO;
import uz.uzkassa.smartpossupply.dto.stocktransfer.TerminalStockTransferListDTO;
import uz.uzkassa.smartpossupply.enums.StockTransferReason;
import uz.uzkassa.smartpossupply.enums.StockTransferStatus;
import uz.uzkassa.smartpossupply.filters.StockTransferFilter;
import uz.uzkassa.smartpossupply.service.StockTransferService;
import uz.uzkassa.smartpossupply.utils.SecurityUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.EnumSet;

import static lombok.AccessLevel.PRIVATE;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 20.11.2024
 */
@RestController
@RequestMapping(ApiConstants.terminalStockTransferRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class TerminalStockTransferResource {

    StockTransferService stockTransferService;

    @Operation(summary = ApiConstants.Get)
    @GetMapping(ApiConstants.id)
    public ResponseEntity<TerminalStockTransferDetailDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(stockTransferService.getForTerminal(id));
    }

    @Operation(summary = ApiConstants.List)
    @GetMapping
    public ResponseEntity<Page<TerminalStockTransferListDTO>> getList(@ParameterObject StockTransferFilter filter) {
        if (SecurityUtils.getCurrentCompanyId() == null) {
            return ResponseEntity.ok(new PageImpl<>(new ArrayList<>(), filter.getPageable(), 0L));
        }
        filter.setExcludeStatus(StockTransferStatus.DRAFT.getCode());
        return ResponseEntity.ok(stockTransferService.getListForTerminal(filter));
    }

    @Operation(summary = ApiConstants.Statuses)
    @GetMapping(ApiConstants.statuses)
    public ResponseEntity<EnumSet<StockTransferStatus>> getStatuses() {
        return ResponseEntity.ok(EnumSet.allOf(StockTransferStatus.class));
    }

    @Operation(summary = ApiConstants.Reasons)
    @GetMapping(ApiConstants.reasons)
    public ResponseEntity<EnumSet<StockTransferReason>> getReason() {
        return ResponseEntity.ok(EnumSet.allOf(StockTransferReason.class));
    }

    @Operation(summary = ApiConstants.UpdateStatus)
    @PatchMapping(ApiConstants.updateStatus)
    public ResponseEntity<CheckStatusResponseDTO> updateStatus(@PathVariable Long id, @RequestBody StockTransferUpdateStatusDTO updateStatusDTO, @RequestParam("lastModifiedDate") LocalDateTime lastModifiedDate) {
        return ResponseEntity.ok(stockTransferService.updateStatus(id, updateStatusDTO, lastModifiedDate));
    }

    @Operation(summary = ApiConstants.Delete)
    @DeleteMapping(ApiConstants.id)
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        stockTransferService.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
