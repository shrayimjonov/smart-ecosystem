package uz.uzkassa.smartpossupply.web.rest.admin;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.smartpossupply.constants.ApiConstants;
import uz.uzkassa.smartpossupply.dto.factura.FacturaLookUpDTO;
import uz.uzkassa.smartpossupply.dto.factura.Status;
import uz.uzkassa.smartpossupply.dto.factura.supply.SupplyFacturaDTO;
import uz.uzkassa.smartpossupply.dto.factura.supply.SupplyFacturaListDTO;
import uz.uzkassa.smartpossupply.enums.FacturaType;
import uz.uzkassa.smartpossupply.enums.InvoiceStatus;
import uz.uzkassa.smartpossupply.filters.FacturaFilter;
import uz.uzkassa.smartpossupply.service.FacturaService;

import java.util.EnumSet;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.adminFacturaRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AdminFacturaResource {

    FacturaService facturaService;

    @Operation(summary = ApiConstants.List)
    @GetMapping
    public ResponseEntity<Page<SupplyFacturaListDTO>> getList(@ParameterObject FacturaFilter filter) {
        return ResponseEntity.ok(facturaService.getList(filter));
    }

    @Operation(summary = ApiConstants.Get)
    @GetMapping(ApiConstants.id)
    public ResponseEntity<SupplyFacturaDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(facturaService.get(id));
    }

    @Operation(summary = ApiConstants.Get)
    @GetMapping("/with-sign" + ApiConstants.id)
    public ResponseEntity<SupplyFacturaDTO> getWithSign(@PathVariable Long id) {
        return ResponseEntity.ok(facturaService.get(id));
    }

    @Operation(summary = ApiConstants.LookUp)
    @GetMapping(ApiConstants.lookUp)
    public ResponseEntity<List<FacturaLookUpDTO>> lookUp(@ParameterObject FacturaFilter filter) {
        return ResponseEntity.ok(facturaService.lookUp(filter));
    }

    @Operation(summary = ApiConstants.Statuses)
    @GetMapping(ApiConstants.statuses)
    public ResponseEntity<EnumSet<InvoiceStatus>> getStatuses() {
        return ResponseEntity.ok(EnumSet.allOf(InvoiceStatus.class));
    }

    @Operation(summary = ApiConstants.UpdateStatus)
    @PatchMapping(ApiConstants.updateStatus)
    public ResponseEntity<Void> updateStatus(@PathVariable Long id, @RequestParam("status") String status) {
        facturaService.updateStatus(id, Status.valueOf(status));
        return ResponseEntity.ok().build();
    }

    @Operation(summary = ApiConstants.Types)
    @GetMapping(ApiConstants.types)
    public ResponseEntity<EnumSet<FacturaType>> getType() {
        return ResponseEntity.ok(EnumSet.allOf(FacturaType.class));
    }

    @Operation(summary = "Sync Vat Registration Codes")
    @GetMapping("/sync/vat-registration-codes")
    public ResponseEntity<Void> syncVatRegistrationCodes() {
        facturaService.syncVatRegistrationCodes();
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Sync Factura Guids")
    @GetMapping("/sync/factura-guids")
    public ResponseEntity<Void> syncFacturaGUID() {
        facturaService.syncFacturaGUID();
        return ResponseEntity.ok().build();
    }
}
