package uz.uzkassa.cts.web.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.cts.dto.tradeinpricehistory.request.TradeInPriceHistoryRequestDTO;
import uz.uzkassa.cts.dto.tradeinpricehistory.response.TradeInPriceHistoryResponseDTO;
import uz.uzkassa.cts.enums.ContractType;
import uz.uzkassa.cts.filter.TradeInPriceHistoryListFilter;
import uz.uzkassa.cts.service.TradeInPriceHistoryService;

import java.util.List;

@RestController
@RequestMapping("/api/trade-in-price-history")
@Tag(name = "TradeIn price history")
@Slf4j
public class TradeInPriceHistoryResource {
    private final TradeInPriceHistoryService tradeInPriceHistoryService;

    public TradeInPriceHistoryResource(TradeInPriceHistoryService tradeInPriceHistoryService) {
        this.tradeInPriceHistoryService = tradeInPriceHistoryService;
    }

    @Operation(summary = "Create")
    @PostMapping
    public ResponseEntity<TradeInPriceHistoryResponseDTO> create(@Valid @RequestBody TradeInPriceHistoryRequestDTO tradeInPriceHistoryDTO) {
        log.debug("REST request to create new TradeInPriceHistoryDTO: {}", tradeInPriceHistoryDTO);
        return ResponseEntity.ok(tradeInPriceHistoryService.create(tradeInPriceHistoryDTO));
    }

    @Operation(summary = "Create")
    @PostMapping(value = "/payment-create")
    public ResponseEntity<TradeInPriceHistoryResponseDTO> paymentCreate(@Valid @RequestBody TradeInPriceHistoryRequestDTO tradeInPriceHistoryDTO) {
        log.debug("REST request to create new TradeInPriceHistoryDTO: {}", tradeInPriceHistoryDTO);
        return ResponseEntity.ok(tradeInPriceHistoryService.paymentCreate(tradeInPriceHistoryDTO));
    }

    @Operation(summary = "Update")
    @PutMapping
    public ResponseEntity<TradeInPriceHistoryResponseDTO> update(@Valid @RequestBody TradeInPriceHistoryRequestDTO tradeInPriceHistoryDTO) {
        log.debug("REST request to update TradeInPriceHistoryDTO : {}", tradeInPriceHistoryDTO);
        return ResponseEntity.ok(tradeInPriceHistoryService.update(tradeInPriceHistoryDTO));
    }

    @Operation(summary = "List")
    @GetMapping
    public ResponseEntity<Page<TradeInPriceHistoryResponseDTO>> getAll(TradeInPriceHistoryListFilter filter) {
        log.debug("REST request to get all trade in TradeInPriceHistoryDTO : {}", filter);
        filter.setContractType(ContractType.TRADE_IN_CONTRACT);
        return ResponseEntity.ok(tradeInPriceHistoryService.list(filter));
    }

    @Operation(summary = "List")
    @GetMapping(value = "/payment-list")
    public ResponseEntity<Page<TradeInPriceHistoryResponseDTO>> getAllPaymentList(TradeInPriceHistoryListFilter filter) {
        log.debug("REST request to get all payment contract TradeInPriceHistoryDTO : {}", filter);
        filter.setContractType(ContractType.PAYMENT_CONTRACT);
        return ResponseEntity.ok(tradeInPriceHistoryService.list(filter));
    }

    @Operation(summary = "lookup")
    @GetMapping(value = "/payment-lookup")
    public ResponseEntity<List<TradeInPriceHistoryResponseDTO>> getLookUp(@ParameterObject TradeInPriceHistoryListFilter filter) {
        filter.setContractType(ContractType.PAYMENT_CONTRACT);
        return ResponseEntity.ok(tradeInPriceHistoryService.lookUp(filter));
    }

    @Operation(summary = "Get")
    @GetMapping("/{id}")
    public ResponseEntity<TradeInPriceHistoryResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(tradeInPriceHistoryService.getById(id));
    }

    @Operation(summary = "GetActiveByDeviceModelId")
    @GetMapping("/deviceModel/{id}")
    public ResponseEntity<TradeInPriceHistoryResponseDTO> getActiveByDeviceModelId(@PathVariable("id") Long deviceModelId) {
        return ResponseEntity.ok(tradeInPriceHistoryService.getActiveByDeviceModelId(deviceModelId));
    }
}
