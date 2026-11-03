package uz.uzkassa.smartpossupply.web.rest.cabinet;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.smartpossupply.constants.ApiConstants;
import uz.uzkassa.smartpossupply.dto.lgota.LgotaListDTO;
import uz.uzkassa.smartpossupply.dto.lgota.LgotaLookUpDTO;
import uz.uzkassa.smartpossupply.dto.soliq.LgotaCompanyDTO;
import uz.uzkassa.smartpossupply.dto.soliq.LgotaProductDTO;
import uz.uzkassa.smartpossupply.filters.BaseFilter;
import uz.uzkassa.smartpossupply.integration.FacturaRestClient;
import uz.uzkassa.smartpossupply.service.LgotaService;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.cabinetLgotaRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CabinetLgotaResource {

    FacturaRestClient facturaRestClient;
    LgotaService lgotaService;

    @Operation(summary = "Get Lgota By Company")
    @GetMapping("/by-company/{tin}")
    public ResponseEntity<List<LgotaCompanyDTO>> getLgotaByCompany(@PathVariable String tin, @RequestParam("isBuyer") Boolean isBuyer) {
        return ResponseEntity.ok(facturaRestClient.getLgotaByCompany(tin, isBuyer));
    }

    @Operation(summary = "Get Lgota By Zero VAT Product")
    @GetMapping("/by-zero-vat-product/{catalogCode}")
    public ResponseEntity<List<LgotaProductDTO>> getLgotaByZeroVatProducts(@PathVariable String catalogCode) {
        return ResponseEntity.ok(facturaRestClient.getLgotaByZeroVatProducts(catalogCode));
    }

    @Operation(summary = "Get Lgota By Without VAT Product")
    @GetMapping("/by-without-vat-product/{catalogCode}")
    public ResponseEntity<List<LgotaProductDTO>> getLgotaByWithoutVatProducts(@PathVariable String catalogCode) {
        return ResponseEntity.ok(facturaRestClient.getLgotaByWithoutVatProducts(catalogCode, true));
    }

    @Operation(summary = ApiConstants.List)
    @GetMapping
    public ResponseEntity<Page<LgotaListDTO>> getList(@ParameterObject BaseFilter filter) {
        return ResponseEntity.ok(lgotaService.getList(filter));
    }

    @Operation(summary = ApiConstants.LookUp)
    @GetMapping(ApiConstants.lookUp)
    public ResponseEntity<List<LgotaLookUpDTO>> lookup(@ParameterObject BaseFilter filter) {
        return ResponseEntity.ok(lgotaService.lookup(filter));
    }
}
