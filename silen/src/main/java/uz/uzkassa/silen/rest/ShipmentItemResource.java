package uz.uzkassa.silen.rest;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.silen.domain.mongo.ShipmentMark;
import uz.uzkassa.silen.dto.ListResult;
import uz.uzkassa.silen.dto.warehouse.CodeWithCountDTO;
import uz.uzkassa.silen.dto.warehouse.ShipmentItemCodeDTO;
import uz.uzkassa.silen.dto.warehouse.ShipmentItemDTO;
import uz.uzkassa.silen.enumeration.PackageType;
import uz.uzkassa.silen.exceptions.BadRequestException;
import uz.uzkassa.silen.service.ShipmentItemService;

import java.util.List;

/**
 * REST controller for managing {@link uz.uzkassa.silen.domain.Shipment}.
 */
@RestController
@RequestMapping("/api/shipment/item")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ShipmentItemResource {

    ShipmentItemService shipmentItemService;

    @PostMapping
    public ResponseEntity<ShipmentItemDTO> create(@RequestBody @Valid ShipmentItemDTO dto) {
        return ResponseEntity.ok(shipmentItemService.save(dto));
    }

    @GetMapping("/by-shipment/{id}")
    public ResponseEntity<List<ShipmentItemDTO>> getAllByShipmentId(@PathVariable String id) {
        return ResponseEntity.ok(shipmentItemService.findAllByShipmentId(id));
    }

    @PutMapping
    public ResponseEntity<ShipmentItemDTO> update(@RequestBody @Valid ShipmentItemDTO dto) {
        if (dto.getId() == null) {
            throw new BadRequestException("ID required");
        }
        return ResponseEntity.ok(shipmentItemService.update(dto));
    }

    @PutMapping("/add-mark")
    public ResponseEntity<CodeWithCountDTO> addMark(@RequestBody @Valid ShipmentItemCodeDTO dto) {
        if (dto.getId() == null) {
            throw new BadRequestException("Требуется идентификатор");
        }
        return ResponseEntity.ok(shipmentItemService.addMark(dto));
    }

    @PutMapping("/remove-mark")
    public ResponseEntity<CodeWithCountDTO> removeMark(@RequestBody @Valid ShipmentItemCodeDTO dto) {
        return ResponseEntity.ok(shipmentItemService.removeMark(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ShipmentItemDTO> get(@PathVariable Long id, @RequestParam(required = false) PackageType unit) {
        return ResponseEntity.ok(shipmentItemService.findOne(id, unit));
    }

    @GetMapping("/by-code")
    public ResponseEntity<ShipmentItemDTO> getByCode(@RequestParam String code) {
        return ResponseEntity.ok(shipmentItemService.getByCode(code));
    }

    @GetMapping("/{id}/codes")
    public ResponseEntity<ListResult<ShipmentMark>> getCodes(@PathVariable Long id) {
        return ResponseEntity.ok(shipmentItemService.getCodes(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        shipmentItemService.delete(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/fill-item/{id}")
    public ResponseEntity<Void> fillShipmentItemBySerialNumber(@PathVariable("id") Long id) {
        shipmentItemService.fillShipmentItem(id);
        return ResponseEntity.ok().build();
    }
}
