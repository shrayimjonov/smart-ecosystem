package uz.uzkassa.smartpossupply.web.rest.cabinet;


import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.smartpossupply.constants.ApiConstants;
import uz.uzkassa.smartpossupply.dto.base.EnumDTO;
import uz.uzkassa.smartpossupply.dto.waybill.*;
import uz.uzkassa.smartpossupply.enums.*;
import uz.uzkassa.smartpossupply.filters.WaybillFilter;
import uz.uzkassa.smartpossupply.service.WaybillService;
import uz.uzkassa.smartpossupply.utils.SecurityUtils;

import java.util.EnumSet;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.cabinetWaybillRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CabinetWaybillResource {

    WaybillService waybillService;

    @PostMapping
    public ResponseEntity<Long> create(@Valid @RequestBody WaybillDTO waybillDTO) {
        return ResponseEntity.ok(waybillService.create(waybillDTO));
    }

    @PutMapping(ApiConstants.id)
    public ResponseEntity<Long> update(@PathVariable Long id, @RequestBody @Valid WaybillDTO waybillDTO) {
        return ResponseEntity.ok(waybillService.update(id, waybillDTO));
    }

    @GetMapping
    public ResponseEntity<Page<WaybillListDTO>> getList(@ParameterObject WaybillFilter filter) {
        if (SecurityUtils.getCurrentCompanyId() == null) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.ok(waybillService.getList(filter));
    }

    @GetMapping(ApiConstants.id)
    public ResponseEntity<SupplyWaybillDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(waybillService.get(id));
    }

    @PostMapping(ApiConstants.send + "/{id}")
    public ResponseEntity<Void> send(@RequestBody @Valid WaybillSendDTO waybillSendDTO, @PathVariable Long id) {
        waybillSendDTO.setClientIp(SecurityUtils.getCurrentUserIP());

        waybillService.send(id, waybillSendDTO);
        return ResponseEntity.ok().build();
    }

    @PostMapping(ApiConstants.cancel + "/{id}")
    public ResponseEntity<Void> cancel(@Valid @RequestBody WaybillSendDTO dto, @PathVariable Long id) {
        dto.setClientIp(SecurityUtils.getCurrentUserIP());
        waybillService.cancel(dto, id);
        return ResponseEntity.ok().build();
    }

    @PostMapping(ApiConstants.accept + "/{id}")
    public ResponseEntity<Void> accept(@Valid @RequestBody WaybillSendDTO dto, @PathVariable Long id) {
        dto.setWaybillLocalSignType("ConsigneeAccepted");
        dto.setClientIp(SecurityUtils.getCurrentUserIP());
        waybillService.acceptOrReject(dto, WaybillStatus.ConsigneeAccepted, id);
        return ResponseEntity.ok().build();
    }

    @PostMapping(ApiConstants.reject + "/{id}")
    public ResponseEntity<Void> reject(@Valid @RequestBody WaybillSendDTO dto, @PathVariable Long id) {
        dto.setWaybillLocalSignType("ConsigneeRejected");
        dto.setClientIp(SecurityUtils.getCurrentUserIP());
        waybillService.acceptOrReject(dto, WaybillStatus.ConsigneeRejected, id);
        return ResponseEntity.ok().build();
    }

    @GetMapping(ApiConstants.statuses)
    public ResponseEntity<List<EnumDTO>> getStatuses() {
        return ResponseEntity.ok(WaybillStatus.getAll());
    }

    @GetMapping("/trailer-types")
    public ResponseEntity<List<EnumDTO>> getTrailerTypes() {
        return ResponseEntity.ok(TrailerType.getAll());
    }

    @GetMapping("/delivery-types")
    public ResponseEntity<List<EnumDTO>> getDeliveryTypes() {
        return ResponseEntity.ok(WaybillDeliveryType.getAll());
    }

    @GetMapping("/transport-types")
    public ResponseEntity<List<EnumDTO>> getTransportTypes() {
        return ResponseEntity.ok(TransportType.getAll());
    }

    @GetMapping("/types")
    public ResponseEntity<List<EnumDTO>> getTypes() {
        return ResponseEntity.ok(WaybillType.getAll());
    }

    @Operation(summary = ApiConstants.Delete)
    @DeleteMapping(ApiConstants.id)
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        waybillService.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/convert/factura/{facturaId}")
    public ResponseEntity<Long> convertFromFactura(@PathVariable Long facturaId) {
        return ResponseEntity.ok(waybillService.convertFromFactura(facturaId));
    }

    @PostMapping("/convert/acceptance-transfer-act/{acceptanceTransferActId}")
    public ResponseEntity<Long> convertFromAcceptanceTransferAct(@PathVariable Long acceptanceTransferActId) {
        return ResponseEntity.ok(waybillService.convertFromAcceptanceTransferAct(acceptanceTransferActId));
    }

    @PostMapping("/convert/stock-transfer/{stockTransferId}")
    public ResponseEntity<Long> convertFromStockTransfer(@PathVariable Long stockTransferId) {
        return ResponseEntity.ok(waybillService.convertFromStockTransfer(stockTransferId));
    }

    @PostMapping("/convert/stock-out/{stockOutId}")
    public ResponseEntity<Long> convertFromStockOut(@PathVariable Long stockOutId) {
        return ResponseEntity.ok(waybillService.convertFromStockOut(stockOutId));
    }

    @GetMapping("/driver/transports")
    public ResponseEntity<WaybillDriverTransportDTO[]> getDriverTransports(@RequestParam String tin) {
        return ResponseEntity.ok(waybillService.getDriverTransports(tin));
    }

    @PostMapping("/transports")
    public ResponseEntity<List<TransportCheckResponseDTO>> getTransports(@RequestBody TransportCheckRequestDTO transportCheckRequestDTO) {
        return ResponseEntity.ok(waybillService.getTransports(transportCheckRequestDTO));
    }

    @GetMapping("/empowerments")
    public ResponseEntity<WaybillEmpowermentDTO[]> getCompanyEmpowerments(@RequestParam String tin) {
        return ResponseEntity.ok(waybillService.getCompanyEmpowerments(tin));
    }

    @Operation(summary = "Waybill Local Types")
    @GetMapping("/local-types")
    public ResponseEntity<EnumSet<WaybillLocalType>> getLocalTypes() {
        return ResponseEntity.ok(EnumSet.allOf(WaybillLocalType.class));
    }

    @Operation(summary = "Drafts Ids")
    @GetMapping("/drafts-ids")
    public ResponseEntity<List<Long>> getDraftIds() {
        return ResponseEntity.ok(waybillService.getDraftIds());
    }
}
