package uz.uzkassa.cts.web.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.cts.dto.tradeIncounterdeviceprice.request.TradeInCounterDevicePriceRequestDTO;
import uz.uzkassa.cts.dto.tradeIncounterdeviceprice.response.TradeInCounterDevicePriceResponseDTO;
import uz.uzkassa.cts.enums.CustomerVatTypeEnum;
import uz.uzkassa.cts.enums.SmartOrOldDeviceEnum;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.service.TradeInCounterDevicePriceService;

import java.util.List;

@RestController
@RequestMapping("/api/trade-in-counter-device-price")
@Tag(name = "Trade In Counter Device Price")
@Slf4j
public class TradeInCounterDevicePriceResource {

    private final TradeInCounterDevicePriceService TradeInCounterDevicePriceService;

    public TradeInCounterDevicePriceResource(TradeInCounterDevicePriceService TradeInCounterDevicePriceService) {
        this.TradeInCounterDevicePriceService = TradeInCounterDevicePriceService;
    }

    @Operation(summary ="Create")
    @PostMapping
    public ResponseEntity<TradeInCounterDevicePriceResponseDTO> create(@Valid @RequestBody TradeInCounterDevicePriceRequestDTO tradeInCounterDevicePriceDTO) {
        log.debug("REST request to create new TradeInCounterDevicePriceDTO: {}", tradeInCounterDevicePriceDTO);
        return ResponseEntity.ok(TradeInCounterDevicePriceService.create(tradeInCounterDevicePriceDTO));
    }

    @Operation(summary = "Update")
    @PutMapping
    public ResponseEntity<TradeInCounterDevicePriceResponseDTO> update(@Valid @RequestBody TradeInCounterDevicePriceRequestDTO tradeInCounterDevicePriceDTO) {
        log.debug("REST request to update TradeInCounterDevicePriceDTO : {}", tradeInCounterDevicePriceDTO);
        return ResponseEntity.ok(TradeInCounterDevicePriceService.update(tradeInCounterDevicePriceDTO));
    }

    @Operation(summary ="List")
    @GetMapping
    public ResponseEntity<Page<TradeInCounterDevicePriceResponseDTO>> getAll(BaseFilter filter) {
        log.debug("REST request to get all TradeInCounterDevicePriceDTO : {}", filter);
        return ResponseEntity.ok(TradeInCounterDevicePriceService.list(filter));
    }

    @Operation(summary ="Get")
    @GetMapping("/{id}")
    public ResponseEntity<TradeInCounterDevicePriceResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(TradeInCounterDevicePriceService.getById(id));
    }

    @Operation(summary ="GetActiveByDeviceType")
    @GetMapping("/device-type")
    public ResponseEntity<TradeInCounterDevicePriceResponseDTO> getActiveByDeviceType(@RequestParam(value = "deviceType") SmartOrOldDeviceEnum deviceType,
                                                                                      @RequestParam(value = "customerVatTypeEnum") CustomerVatTypeEnum customerVatTypeEnum) {
        return ResponseEntity.ok(TradeInCounterDevicePriceService.findByActiveSmartOrOldDevice(deviceType, customerVatTypeEnum));
    }

    @Operation(summary ="GetActiveDevices")
    @GetMapping("/get-active-devices")
    public ResponseEntity<List<TradeInCounterDevicePriceResponseDTO>> getActiveDevices(@RequestParam(value = "deviceType", defaultValue = "") SmartOrOldDeviceEnum deviceType) {
        if (deviceType == null) {
            return ResponseEntity.ok(TradeInCounterDevicePriceService.getActiveDevices());
        }
        return ResponseEntity.ok(TradeInCounterDevicePriceService.getActiveDevicesByDeviceType(deviceType));
    }

}
