package uz.uzkassa.silen.rest;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.silen.dto.*;
import uz.uzkassa.silen.dto.filter.ShipmentFilter;
import uz.uzkassa.silen.dto.warehouse.*;
import uz.uzkassa.silen.enumeration.DocumentType;
import uz.uzkassa.silen.enumeration.ShipmentStatus;
import uz.uzkassa.silen.service.AcceptanceTransferActService;
import uz.uzkassa.silen.service.ExportReportService;
import uz.uzkassa.silen.service.ShipmentService;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

/**
 * REST controller for managing {@link uz.uzkassa.silen.domain.Shipment}.
 */
@RestController
@RequestMapping("/api/shipment")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ShipmentResource {

    ShipmentService shipmentService;
    AcceptanceTransferActService acceptanceTransferActService;
    ExportReportService exportReportService;


    @GetMapping("/statuses")
    public List<EnumDTO> getStatuses() {
        return ShipmentStatus.getAll();
    }

    @PostMapping
    public ResponseEntity<String> create(@RequestBody @Valid ShipmentDTO shipmentDTO) {
        return ResponseEntity.ok(shipmentService.create(shipmentDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable String id, @RequestBody @Valid ShipmentDTO shipmentDTO) {
        shipmentService.update(id, shipmentDTO);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/close/{id}")
    public ResponseEntity<Void> close(@PathVariable String id) {
        shipmentService.close(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/cancel/{id}")
    public ResponseEntity<Void> cancel(@PathVariable String id) {
        shipmentService.cancel(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/copy/{id}")
    public ResponseEntity<String> copy(@PathVariable String id) {
        return ResponseEntity.ok(shipmentService.copy(id));
    }

    @PutMapping("/convert/{id}")
    public ResponseEntity<Void> convert(@PathVariable String id) {
        shipmentService.convert(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/convert/to-act/{id}")
    public ResponseEntity<String> convertToAct(@PathVariable String id) {
        return ResponseEntity.ok(acceptanceTransferActService.convertShipmentToAct(id));
    }

    @GetMapping
    public ResponseEntity<Page<ShipmentListDTO>> getAll(@ModelAttribute ShipmentFilter filter) {
        return ResponseEntity.ok(shipmentService.findAllByFilter(filter));
    }

    @GetMapping("/items")
    public ResponseEntity<List<ShipmentListDTO>> getItems(@ModelAttribute ShipmentFilter filter) {
        return ResponseEntity.ok(shipmentService.getItems(filter));
    }

    @GetMapping("/stats")
    public ResponseEntity<List<ShipmentStatsDTO>> getAllForStats(@ModelAttribute ShipmentFilter filter) {
        return ResponseEntity.ok(shipmentService.findAllForStats(filter));
    }

    @GetMapping("/all")
    public ResponseEntity<Page<ShipmentDTO>> getAllWithoutOrgFilter(@ModelAttribute ShipmentFilter filter) {
        return ResponseEntity.ok(shipmentService.findAllWithoutOrgIdFilter(filter));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ShipmentDetailsDTO> get(@PathVariable String id) {
        return ResponseEntity.ok(shipmentService.getOne(id));
    }

    @GetMapping("/sync/{id}")
    public ResponseEntity<List<ShipmentItemDTO>> sync(@PathVariable String id) {
        return ResponseEntity.ok(shipmentService.sync(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        shipmentService.delete(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/remove-deleted-marks")
    public ResponseEntity<Void> removeDeletedMarks() {
        shipmentService.removeDeletedCodes();
        return ResponseEntity.ok().build();
    }

    @GetMapping("/shipped-products")
    public ResponseEntity<Page<WarehouseSelectItem>> getShippedProductsStats(ShipmentFilter filter) {
        return ResponseEntity.ok(shipmentService.getShippedProductsStats(filter));
    }

    @GetMapping("/ship-history")
    public ResponseEntity<Page<ShipmentHistoryDTO>> getShipHistory(ShipmentFilter filter) {
        return ResponseEntity.ok(shipmentService.getShipHistory(filter));
    }

    @GetMapping("/ship-history/summary")
    public ResponseEntity<BigDecimal> getShipHistorySummary(ShipmentFilter filter) {
        return ResponseEntity.ok(shipmentService.getShipHistorySummary(filter));
    }

    @GetMapping("/document/types")
    public ResponseEntity<List<SelectItem>> getDocumentTypes() {
        return ResponseEntity.ok(Arrays.asList(DocumentType.INVOICE.toSelectItem(), DocumentType.ACCEPTANCE_TRANSFER_ACT.toSelectItem()));
    }

    @PostMapping("/export")
    public ResponseEntity<Object> exportCodes(@RequestBody ShipmentExportDTO shipmentExportDTO) {
        shipmentService.export(shipmentExportDTO);
        return ResponseEntity.ok().build();
    }
}
