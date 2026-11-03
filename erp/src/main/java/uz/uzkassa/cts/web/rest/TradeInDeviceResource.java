package uz.uzkassa.cts.web.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.cts.dto.tradeindevice.TradeInDeviceDTO;
import uz.uzkassa.cts.filter.TradeInDeviceListFilter;
import uz.uzkassa.cts.service.TradeInDeviceService;

@RestController
@RequestMapping("/api/trade-in-devices")
@Tag(name = "TradeIn device")
@Slf4j
public class TradeInDeviceResource {
    private final TradeInDeviceService tradeInDeviceService;

    public TradeInDeviceResource(TradeInDeviceService tradeInDeviceService) {
        this.tradeInDeviceService = tradeInDeviceService;
    }

    @Operation(summary ="Create")
    @PostMapping
    public ResponseEntity<TradeInDeviceDTO> create(@Valid @RequestBody TradeInDeviceDTO tradeInDeviceDTO) {
        log.debug("REST request to create new TradeInDeviceDTO: {}", tradeInDeviceDTO);
        return ResponseEntity.ok(tradeInDeviceService.create(tradeInDeviceDTO));
    }

    @Operation(summary = "Update")
    @PutMapping
    public ResponseEntity<TradeInDeviceDTO> update(@Valid @RequestBody TradeInDeviceDTO tradeInDeviceDTO) {
        log.debug("REST request to update TradeInDeviceDTO : {}", tradeInDeviceDTO);
        return ResponseEntity.ok(tradeInDeviceService.update(tradeInDeviceDTO));
    }

    @Operation(summary ="List")
    @GetMapping
    public ResponseEntity<Page<TradeInDeviceDTO>> getAll(TradeInDeviceListFilter filter) {
        log.debug("REST request to get all TradeInDeviceDTO : {}", filter);
        return ResponseEntity.ok(tradeInDeviceService.list(filter));
    }

    @Operation(summary ="Get")
    @GetMapping("/{id}")
    public ResponseEntity<TradeInDeviceDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(tradeInDeviceService.getById(id));
    }
}
