package uz.uzkassa.cts.web.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.dto.customer.CustomerBalanceDTO;
import uz.uzkassa.cts.dto.device.DeviceFiscalModuleDTO;
import uz.uzkassa.cts.dto.ofd.CustomerBranchSendDTO;
import uz.uzkassa.cts.dto.ofd.CustomerFiscalDTO;
import uz.uzkassa.cts.dto.tin.integration.OfdActivityGroupListResponseDTO;
import uz.uzkassa.cts.dto.tin.integration.OfdCheckTinResponseResultDTO;
import uz.uzkassa.cts.dto.tin.integration.OfdNeighborhoodListResponseDTO;
import uz.uzkassa.cts.dto.tin.integration.OfdRegionListResponseDTO;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.filter.CustomerBranchFilter;
import uz.uzkassa.cts.service.CustomerBranchOldService;
import uz.uzkassa.cts.service.FiscalizationService;
import uz.uzkassa.cts.service.OfdService;

import java.util.List;

@RestController
@RequestMapping("/api/ofd")
@Tag(name = "OFD")
@Slf4j
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@Deprecated
public class OfdResource {

    OfdService ofdService;
    CustomerBranchOldService customerBranchOldService;
    FiscalizationService fiscalizationService;

    @Operation(summary = "Check Customer TIN/PINFL")
    @PostMapping("/customer/check/{tinOrPinfl}")
    public ResponseEntity<OfdCheckTinResponseResultDTO> checkTin(@PathVariable(name = "tinOrPinfl") String tinOrPinfl) {
        log.debug("REST request to get info by TIN/PINFL : {}", tinOrPinfl);
        return ResponseEntity.ok(ofdService.checkCustomer(tinOrPinfl));
    }

    @Operation(summary = "Regions")
    @GetMapping("/regions/{language}")
    public ResponseEntity<OfdRegionListResponseDTO> getRegions(@PathVariable String language) {
        return ResponseEntity.ok(ofdService.getRegions(language));
    }

    @Operation(summary = "Districts")
    @GetMapping("/districts/{regionCode}/{language}")
    public ResponseEntity<OfdRegionListResponseDTO> getDistricts(@PathVariable Integer regionCode,
                                                                 @PathVariable String language) {
        return ResponseEntity.ok(ofdService.getDistricts(regionCode, language));
    }

    @Operation(summary = "Neighborhoods")
    @GetMapping("/neighborhoods/{regionCode}/{districtCode}")
    public ResponseEntity<OfdNeighborhoodListResponseDTO> getNeighborhoods(@PathVariable(value = "regionCode") Long regionCode,
                                                                           @PathVariable(value = "districtCode") Long districtCode) {
        return ResponseEntity.ok(ofdService.getNeighborhoods(regionCode, districtCode));
    }

    @Operation(summary = "Activity Type Groups")
    @GetMapping("/activity-type-groups")
    public ResponseEntity<OfdActivityGroupListResponseDTO> getActivityTypeGroups() {
        return ResponseEntity.ok(ofdService.getActivityTypeGroups());
    }

    @Operation(summary = "Activity Types")
    @GetMapping("/activity-types/{groupCode}")
    public ResponseEntity<OfdActivityGroupListResponseDTO> getActivityTypes(@PathVariable Integer groupCode) {
        return ResponseEntity.ok(ofdService.getActivityTypes(groupCode));
    }

    @Operation(summary = "Terminal Types")
    @GetMapping("/terminal-types")
    public ResponseEntity<String> getTerminalTypes() {
        return ResponseEntity.ok(ofdService.getTerminalTypes());
    }

    @Operation(summary = "Get Customer Balance")
    @GetMapping("/customer/balance/{tin}")
    public ResponseEntity<CustomerBalanceDTO> checkCustomerBalance(@PathVariable(name = "tin") String tin) {
        log.debug("REST request to get customer balance by code : {}", tin);
        return ResponseEntity.ok(ofdService.checkCustomerBalance(tin));
    }

    @Deprecated
    @Operation(summary = "Add Customer Branch(TT) on OFD", description = "New API: /api/admin/v1/ofd-customer-branch/confirm")
    @PostMapping("/customer/branch")
    public ResponseEntity<String> addCustomerBranch(@Valid @RequestBody CustomerBranchSendDTO customerBranchSendDTO) {
        log.debug("REST request to add customer branch on OFD : {}", customerBranchSendDTO);
        return ResponseEntity.ok(ofdService.sendCustomerBranch(customerBranchSendDTO));
    }

    @Operation(summary = "Update Customer Branch(TT) on OFD", description = "New API: /api/admin/v1/ofd-customer-branch/confirm")
    @PutMapping("/customer/branch")
    public ResponseEntity<String> updateCustomerBranch(@Valid @RequestBody CustomerBranchSendDTO customerBranchSendDTO) {
        log.debug("REST request to update customer branch on OFD : {}", customerBranchSendDTO);
        return ResponseEntity.ok(ofdService.sendCustomerBranch(customerBranchSendDTO));
    }

    @Deprecated
    //todo this API to Customer Branch Resources
    @Operation(summary = "Look Up Customer Branches")
    @GetMapping("/customer/branch/lookup")
    public ResponseEntity<List<CommonDTO>> lookUpCustomerBranch(@Valid CustomerBranchFilter filter) {
        log.debug("REST request to get all customer branch items : {}", filter);
        return ResponseEntity.ok(customerBranchOldService.lookupForOfd(filter));
    }

    @Operation(summary = "Make Customer Fiscal")
    @PostMapping("/customer/fiscal")
    public ResponseEntity<String> makeCustomerFiscal(@Valid @RequestBody CustomerFiscalDTO customerFiscalDTO) {
        log.debug("REST request to add customer on OFD : {}", customerFiscalDTO);
        return ResponseEntity.ok(ofdService.makeCustomerFiscal(customerFiscalDTO));
    }

    @Deprecated
    //todo this API to Fiscalization Resources
    @Operation(summary = "Look Up Fiscal Devices")
    @GetMapping("/fiscal/devices/lookup")
    public ResponseEntity<List<DeviceFiscalModuleDTO>> lookUpCustomerBranchDevices(@Valid BaseFilter filter) {
        log.debug("REST request to get all customer branch items : {}", filter);
        return ResponseEntity.ok(fiscalizationService.getLookupForOfd(filter));
    }
}
