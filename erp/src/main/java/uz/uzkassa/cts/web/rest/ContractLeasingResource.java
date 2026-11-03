package uz.uzkassa.cts.web.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.cts.dto.base.CommonCodeDTO;
import uz.uzkassa.cts.dto.contractforleasing.request.ContractForLeasingDeviceRequestDto;
import uz.uzkassa.cts.dto.contractforleasing.request.ContractForLeasingEarlyClosingRequestDto;
import uz.uzkassa.cts.dto.contractforleasing.request.ContractForLeasingRequestDto;
import uz.uzkassa.cts.dto.contractforleasing.response.ContractForLeasingDeviceCheckResponseDto;
import uz.uzkassa.cts.dto.contractforleasing.response.ContractForLeasingListResponseDto;
import uz.uzkassa.cts.dto.contractforleasing.response.ContractForLeasingResponseDto;
import uz.uzkassa.cts.enums.CompanyType;
import uz.uzkassa.cts.filter.ContractForLeasingFilter;
import uz.uzkassa.cts.service.ContractLeasingFactoryService;

import java.util.List;

@RestController
@RequestMapping("/api/contract-for-leasing")
@Tag(name = "Order for leasing")
@Slf4j
@AllArgsConstructor
public class ContractLeasingResource {

    private final ContractLeasingFactoryService contractLeasingService;

    @Operation(summary = "Items")
    @GetMapping("/items/{type}")
    public ResponseEntity<List<CommonCodeDTO>> getItems(@PathVariable String type) {
        log.debug("REST request to get Leasing as drop down");
        return ResponseEntity.ok(contractLeasingService.getItems(type));
    }

    @Operation(summary = "List")
    @GetMapping("/list")
    public ResponseEntity<Page<ContractForLeasingListResponseDto>> getAll(ContractForLeasingFilter filter) {
        log.debug("REST request to get all OrderForLeasing : {}", filter);
        filter.setType(CompanyType.STS.name());
        return ResponseEntity.ok(contractLeasingService.list(filter));
    }

    @Operation(summary = "Add")
    @PostMapping
    public ResponseEntity<ContractForLeasingResponseDto> create(@Valid @RequestBody ContractForLeasingRequestDto dto) {
        log.debug("REST request to create new OrderForLeasing: {}", dto);
        return ResponseEntity.ok(contractLeasingService.create(dto));
    }

    @Operation(summary = "accept")
    @PutMapping("/{id}/status/accept")
    public ResponseEntity<ContractForLeasingResponseDto> accept(@PathVariable Long id) {
        return ResponseEntity.ok(contractLeasingService.accept(id));
    }

    @Operation(summary = "change to attach")
    @PutMapping("/{id}/status/change-to-attach")
    public ResponseEntity<ContractForLeasingResponseDto> changeToAttach(@PathVariable Long id) {
        return ResponseEntity.ok(contractLeasingService.changeToAttach(id));
    }

    @Operation(summary = "reject")
    @PutMapping("/{id}/status/reject")
    public ResponseEntity<ContractForLeasingResponseDto> reject(@PathVariable Long id) {
        return ResponseEntity.ok(contractLeasingService.reject(id));
    }

    @Operation(summary = "reject")
    @PutMapping("/{id}/status/reject-with-acts")
    public ResponseEntity<ContractForLeasingResponseDto> rejectWithActs(@PathVariable Long id) {
        return ResponseEntity.ok(contractLeasingService.rejectWithActs(id));
    }

    @Operation(summary = "get by id")
    @GetMapping("/{id}")
    public ResponseEntity<ContractForLeasingResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(contractLeasingService.getById(id));
    }

    @Operation(summary = "generate template")
    @GetMapping("/{id}/generate-contract-template")
    public ResponseEntity<String> getTemplate(@PathVariable Long id) {
        return ResponseEntity.ok(contractLeasingService.getContractHtmlTemplate(id));
    }

    @Operation(summary = "generate template payment quote")
    @GetMapping("/{id}/payment-quote")
    public ResponseEntity<String> getPaymentQuote(@PathVariable Long id) {
        return ResponseEntity.ok(contractLeasingService.getPaymentQuote(id));
    }

    @Operation(summary = "Generate offer template")
    @GetMapping(value = "/offer-template")
    public ResponseEntity<String> getTemplate() {
        return ResponseEntity.ok(contractLeasingService.getOfferHtmlTemplate());
    }

    @Operation(summary = "generate template")
    @GetMapping("/{id}/generate-warranty-template")
    public ResponseEntity<String> getWarrantyTemplate(@PathVariable Long id) {
        return ResponseEntity.ok(contractLeasingService.getWarrantyHtmlTemplate(id));
    }

    @Operation(summary = "upload file")
    @PostMapping("/{id}/upload-contract")
    public ResponseEntity<ContractForLeasingResponseDto> uploadFileContract(@RequestParam("file") MultipartFile file, @PathVariable Long id) {
        log.debug("REST request to upload file : {}", file);
        return ResponseEntity.ok(contractLeasingService.uploadContractFile(id, file));
    }

    @Operation(summary = "upload file")
    @PostMapping("/{id}/upload-warranty")
    public ResponseEntity<ContractForLeasingResponseDto> uploadFileWarranty(@RequestParam("file") MultipartFile file, @PathVariable Long id) {
        log.debug("REST request to upload file : {}", file);
        return ResponseEntity.ok(contractLeasingService.uploadWarrantyFile(id, file));
    }

//    @Operation(summary = "List")
//    @GetMapping("/list-by-tin/{tin}")
//    public ResponseEntity<Page<ContractForLeasingListResponseDto>> getAllByTin(@PathVariable String tin, ContractForLeasingFilter filter) {
//        log.debug("REST request to get all OrderForLeasing : {}", filter);
//        return ResponseEntity.ok(contractLeasingService.listByTin(filter, tin));
//    }

    @Operation(summary = "upload file")
    @PostMapping("/{id}/add-device")
    public ResponseEntity<ContractForLeasingResponseDto> addDevices(@PathVariable Long id, ContractForLeasingDeviceRequestDto deviceRequestDto) {
        log.debug("REST request to add device : {}", deviceRequestDto);
        return ResponseEntity.ok(contractLeasingService.addDevice(id, deviceRequestDto));
    }

    @Operation(summary = "device check")
    @GetMapping("/{id}/device-check")
    public ResponseEntity<ContractForLeasingDeviceCheckResponseDto> forDeviceAdd(@PathVariable Long id) {
        log.debug("REST request to device chack : {}", id);
        return ResponseEntity.ok(contractLeasingService.getForDeviceAdd(id));
    }

    @Operation(summary = "device check")
    @GetMapping("/list-by-customer-id/{id}/{type}/{deviceType}/device-check")
    public ResponseEntity<List<ContractForLeasingDeviceCheckResponseDto>> forDeviceAddByTin(@PathVariable Long id, @PathVariable String type, @PathVariable String deviceType) {
        log.debug("REST request to device check by customerId : {}", id);
        return ResponseEntity.ok(contractLeasingService.getForDeviceAddByCustomerId(id, type, deviceType));
    }

    @Operation(summary = "upload file")
    @PutMapping("/early-closing")
    public ResponseEntity<Void> earlyClosing(@RequestBody ContractForLeasingEarlyClosingRequestDto dto) {
        log.debug("REST early closing contractId : {}", dto.getContractId());
        contractLeasingService.earlyCloseContract(dto.getContractId());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Verify code")
    @PutMapping("/verify-code/{id}/{code}")
    public ResponseEntity<Void> verifyCode(@PathVariable Long id, @PathVariable String code) {
        contractLeasingService.verifyCode(id, code);
        return ResponseEntity.ok().build();
    }
}
