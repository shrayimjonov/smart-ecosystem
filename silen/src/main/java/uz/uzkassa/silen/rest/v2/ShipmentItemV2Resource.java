package uz.uzkassa.silen.rest.v2;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.silen.dto.warehouse.ShipmentItemCodeV2DTO;
import uz.uzkassa.silen.dto.warehouse.ShipmentItemDTO;
import uz.uzkassa.silen.exceptions.BadRequestException;
import uz.uzkassa.silen.service.ShipmentItemServiceV2;

/**
 * REST controller for managing {@link uz.uzkassa.silen.domain.ShipmentItem}.
 */
@Slf4j
@RestController
@RequestMapping("/api/v2/shipment/item")
@RequiredArgsConstructor
public class ShipmentItemV2Resource {
    private final ShipmentItemServiceV2 shipmentItemService;

    @PutMapping("/add-mark")
    public ResponseEntity<ShipmentItemDTO> addMark(@RequestBody @Valid ShipmentItemCodeV2DTO dto) {
        if (dto.getShipmentId() == null) {
            throw new BadRequestException("Требуется идентификатор");
        }
        return ResponseEntity.ok(shipmentItemService.addMark(dto));
    }
}
