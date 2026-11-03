package uz.uzkassa.smartpossupply.web.rest.admin;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.smartpossupply.constants.ApiConstants;
import uz.uzkassa.smartpossupply.dto.factura.supply.FacturaHostDTO;
import uz.uzkassa.smartpossupply.service.FacturaHostService;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;


@RestController
@RequestMapping(ApiConstants.adminFacturaHostRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AdminFacturaHostResource {

    FacturaHostService facturaHostService;

    @GetMapping
    public ResponseEntity<List<FacturaHostDTO>> getList() {
        return ResponseEntity.ok(facturaHostService.getList());
    }

    @PutMapping(ApiConstants.id)
    public ResponseEntity<Void> update(@PathVariable Long id) {
        facturaHostService.update(id);
        return ResponseEntity.ok().build();
    }
}
