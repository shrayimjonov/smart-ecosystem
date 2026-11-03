package uz.uzkassa.smartpos.web.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.smartpos.constants.TokenType;
import uz.uzkassa.smartpos.dto.base.CommonDTO;
import uz.uzkassa.smartpos.dto.filter.BaseFilter;
import uz.uzkassa.smartpos.integration.enums.DeliveryType;
import uz.uzkassa.smartpos.integration.supply.SupplierBranchWithAddressDTO;
import uz.uzkassa.smartpos.integration.supply.SupplierContractLookupDTO;
import uz.uzkassa.smartpos.integration.supply.SupplyClient;
import uz.uzkassa.smartpos.integration.supply.dto.ContractDetailDTO;
import uz.uzkassa.smartpos.integration.supply.dto.RegionDTO;
import uz.uzkassa.smartpos.service.TokenStoreService;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/suppliers")
@RequiredArgsConstructor
public class SupplierResource {
    private final SupplyClient supplyClient;
    private final TokenStoreService tokenStoreService;


    @GetMapping("/warehouse/lookup")
    public ResponseEntity<List<CommonDTO>> warehouseLookUp(@ParameterObject BaseFilter filter) {
        return supplyClient.lookUpWarehouse(tokenStoreService.getToken(TokenType.SUPPLY), filter);
    }

    @GetMapping
    public ResponseEntity<Object> getSuppliers(@ParameterObject BaseFilter filter) {
        return supplyClient.getSuppliers(tokenStoreService.getToken(TokenType.SUPPLY), filter);
    }

    @GetMapping("/{supplierId}/delivery-types")
    public ResponseEntity<List<DeliveryType>> getSupplierDeliveryTypes(@PathVariable Long supplierId) {
        return supplyClient.getSupplierDeliveryTypes(tokenStoreService.getToken(TokenType.SUPPLY), supplierId);
    }


    @GetMapping("/branches/lookup/with-address")
    public ResponseEntity<List<SupplierBranchWithAddressDTO>> lookupBranchesWithAddress(@ParameterObject BaseFilter filter) {
        return supplyClient.getBranchesWithAddress(tokenStoreService.getToken(TokenType.SUPPLY), filter);
    }

    @GetMapping("/contracts/lookup")
    public ResponseEntity<List<SupplierContractLookupDTO>> contractsLookup(@RequestParam Long supplierId, @RequestParam(required = false) String customerTin) {
        return supplyClient.contractsLookup(tokenStoreService.getToken(TokenType.SUPPLY), supplierId, customerTin);
    }

    @GetMapping("/contracts/{id}")
    public ResponseEntity<ContractDetailDTO> get(@PathVariable Long id) {
        return supplyClient.contractGetById(tokenStoreService.getToken(TokenType.SUPPLY), id);
    }

    @GetMapping("/regions")
    public ResponseEntity<List<RegionDTO>> regions() {
        return ResponseEntity.ok(supplyClient.regions()
                .stream().map(commonCodeDTO -> new RegionDTO(commonCodeDTO.getId(), commonCodeDTO.getName(), Long.valueOf(commonCodeDTO.getCode())))
                .collect(Collectors.toList()));
    }

    @GetMapping("/districts/{regionId}")
    public ResponseEntity<List<RegionDTO>> districts(@PathVariable Long regionId) {
        return ResponseEntity.ok(supplyClient.districts(regionId)
                .stream().map(commonCodeDTO -> new RegionDTO(commonCodeDTO.getId(), commonCodeDTO.getName(), Long.valueOf(commonCodeDTO.getCode())))
                .collect(Collectors.toList()));
    }
}
