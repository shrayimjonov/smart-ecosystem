package uz.uzkassa.horeca.web.rest.cabinet;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.horeca.constants.ApiConstants;
import uz.uzkassa.horeca.dto.factura.FacturaDTO;
import uz.uzkassa.horeca.dto.factura.FacturaLookUpDTO;
import uz.uzkassa.horeca.dto.factura.FacturaSendDTO;
import uz.uzkassa.horeca.dto.factura.Status;
import uz.uzkassa.horeca.dto.factura.supply.SupplyFacturaDTO;
import uz.uzkassa.horeca.dto.factura.supply.SupplyFacturaListDTO;
import uz.uzkassa.horeca.dto.factura.sync.FacturaSyncDTO;
import uz.uzkassa.horeca.enums.FacturaType;
import uz.uzkassa.horeca.filters.FacturaFilter;
import uz.uzkassa.horeca.service.FacturaService;

import java.util.EnumSet;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.cabinetFacturaRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CabinetFacturaResource {

    FacturaService facturaService;

    @GetMapping
    public ResponseEntity<Page<SupplyFacturaListDTO>> getList(@ParameterObject FacturaFilter filter) {
        return ResponseEntity.ok(facturaService.getList(filter));
    }

    @GetMapping(ApiConstants.id)
    public ResponseEntity<SupplyFacturaDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(facturaService.get(id));
    }

    @PostMapping
    public ResponseEntity<Long> create(@RequestBody FacturaDTO facturaDTO) {
        return ResponseEntity.ok(facturaService.create(facturaDTO));
    }

    @PutMapping(ApiConstants.id)
    public ResponseEntity<Long> update(@PathVariable Long id, @RequestBody FacturaDTO facturaDTO) {
        return ResponseEntity.ok(facturaService.update(id, facturaDTO));
    }

    @GetMapping(ApiConstants.lookUp)
    public ResponseEntity<List<FacturaLookUpDTO>> lookUp(FacturaFilter filter) {
        filter.setStatus(Status.ACCEPTED.name());
        return ResponseEntity.ok(facturaService.lookUp(filter));
    }

    @GetMapping(ApiConstants.types)
    public ResponseEntity<EnumSet<FacturaType>> getTypes() {
        return ResponseEntity.ok(EnumSet.allOf(FacturaType.class));
    }

    @PostMapping(ApiConstants.sync)
    public ResponseEntity<Void> sync(@RequestBody FacturaSyncDTO facturaSyncDTO) {
        facturaService.sync(facturaSyncDTO);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/sync/incoming/{id}")
    public ResponseEntity<Void> syncIncoming(@PathVariable Long id) {
        facturaService.syncById(id, true);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/sync/outgoing/{id}")
    public ResponseEntity<Void> syncOutgoing(@PathVariable Long id) {
        facturaService.syncById(id, false);
        return ResponseEntity.ok().build();
    }

    @PostMapping(ApiConstants.send)
    public ResponseEntity<Long> send(@RequestBody FacturaSendDTO facturaSendDTO) {
        return ResponseEntity.ok(facturaService.send(facturaSendDTO));
    }

    @PostMapping("/cancel")
    public ResponseEntity<Long> cancel(@RequestBody FacturaSendDTO facturaSendDTO) {
        return ResponseEntity.ok(facturaService.changeStatus(facturaSendDTO, Status.CANCELLED));
    }

    @PostMapping("/cancel-draft")
    public ResponseEntity<Long> cancelDraft(@RequestBody FacturaSendDTO facturaSendDTO) {
        return ResponseEntity.ok(facturaService.cancelDraft(facturaSendDTO));
    }

    @PostMapping("/accept")
    public ResponseEntity<Long> accept(@RequestBody FacturaSendDTO facturaSendDTO) {
        facturaSendDTO.setAction("accept");
        return ResponseEntity.ok(facturaService.changeStatus(facturaSendDTO, Status.ACCEPTED));
    }

    @PostMapping("/reject")
    public ResponseEntity<Long> reject(@RequestBody FacturaSendDTO facturaSendDTO) {
        facturaSendDTO.setAction("reject");
        return ResponseEntity.ok(facturaService.changeStatus(facturaSendDTO, Status.REJECTED));
    }

    @DeleteMapping(ApiConstants.id)
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        facturaService.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
