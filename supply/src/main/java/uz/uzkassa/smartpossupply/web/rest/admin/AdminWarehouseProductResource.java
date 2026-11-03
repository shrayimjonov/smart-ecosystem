package uz.uzkassa.smartpossupply.web.rest.admin;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.smartpossupply.constants.ApiConstants;
import uz.uzkassa.smartpossupply.dto.warehouseproduct.WarehouseProductDTO;
import uz.uzkassa.smartpossupply.service.CompanyCommittentService;
import uz.uzkassa.smartpossupply.service.ProductContractService;
import uz.uzkassa.smartpossupply.service.ProductSerialService;
import uz.uzkassa.smartpossupply.service.WarehouseProductService;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.adminWarehouseProductRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AdminWarehouseProductResource {

    WarehouseProductService warehouseProductService;
    ProductSerialService productSerialService;
    ProductContractService productContractService;
    CompanyCommittentService companyCommittentService;

    @PostMapping
    public ResponseEntity<Void> create(@RequestBody WarehouseProductDTO warehouseProductDTO) {
        warehouseProductService.create(warehouseProductDTO);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{productId}")
    public ResponseEntity<Void> updateQty(@PathVariable Long productId) {
        warehouseProductService.updateQty(productId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/by-serial/{productSerialId}")
    public ResponseEntity<Void> updateQtyBySerial(@PathVariable Long productSerialId) {
        productSerialService.updateQty(productSerialId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/by-contract/{productContractId}")
    public ResponseEntity<Void> updateQtyByContract(@PathVariable Long productContractId) {
        productContractService.updateQty(productContractId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/by-comittent/{committentId}")
    public ResponseEntity<Void> updateQtyComittent(@PathVariable Long committentId) {
        companyCommittentService.updateQty(committentId);
        return ResponseEntity.ok().build();
    }
}
