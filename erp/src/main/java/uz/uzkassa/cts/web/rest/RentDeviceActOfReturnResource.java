package uz.uzkassa.cts.web.rest;


import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.cts.dto.actoftransmission.response.ActChangeDocFlowRequestDto;
import uz.uzkassa.cts.dto.rentDeviceAct.RentDeviceActOfReturnDTO;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.service.RentDeviceActOfReturnService;

import java.util.List;

@RestController
@RequestMapping("/api/rent-device-act-of-return")
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
class RentDeviceActOfReturnResource {

    RentDeviceActOfReturnService service;

    @Operation(summary = "List")
    @GetMapping
    public ResponseEntity<Page<RentDeviceActOfReturnDTO>> getAll(BaseFilter filter) {
        log.debug("REST request to get all TradeInContract : {}", filter);
        return ResponseEntity.ok(service.getList(filter));
    }

    @Operation(summary = "List by offerId")
    @GetMapping("/customerId/{customerId}")
    public ResponseEntity<List<RentDeviceActOfReturnDTO>> getAll(@PathVariable Long customerId) {
        return ResponseEntity.ok(service.getByCustomerId(customerId));
    }

    @Operation(summary = "Create by rent device act")
    @PostMapping(value = "/{id}")
    public ResponseEntity<RentDeviceActOfReturnDTO> create(@PathVariable Long id) {
        log.debug("REST request to create new RentDeviceActOfReturn by rent act ID: {}", id);
        return ResponseEntity.ok(service.create(id));
    }

    @Operation(summary = "Get by id")
    @GetMapping("/{id}")
    public ResponseEntity<RentDeviceActOfReturnDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @Operation(summary = "accept")
    @PutMapping("/{id}/status/accept")
    public ResponseEntity<RentDeviceActOfReturnDTO> accept(@PathVariable Long id) {
        return ResponseEntity.ok(service.accept(id));
    }

    @Operation(summary = "doc flow status")
    @PostMapping("/{id}/change-doc-flow")
    public ResponseEntity<RentDeviceActOfReturnDTO> changeDocFlowStatus(@Valid @RequestBody ActChangeDocFlowRequestDto dto) {
        return ResponseEntity.ok(service.changeDocFlowStatus(dto));
    }

    @Operation(summary = "wait for attachment")
    @PutMapping("/{id}/status/waiting-for-attachment")
    public ResponseEntity<RentDeviceActOfReturnDTO> waitingForAttachment(@PathVariable Long id) {
        return ResponseEntity.ok(service.changeToAttach(id));
    }

    @Operation(summary = "generate template")
    @GetMapping("/{id}/generate-template")
    public ResponseEntity<String> getTemplate(@PathVariable Long id) {
        return ResponseEntity.ok(service.getHtmlTemplate(id));
    }

    @Operation(summary = "upload file")
    @PostMapping("/{id}/upload")
    public ResponseEntity<RentDeviceActOfReturnDTO> uploadFile(@RequestParam("file") MultipartFile file, @PathVariable Long id) {
        return ResponseEntity.ok(service.uploadFile(id, file));
    }

    @Operation(summary = "find")
    @GetMapping("/customerId/{customerId}/device-serial-number/{deviceSerialNumber}")
    public ResponseEntity<RentDeviceActOfReturnDTO> findByCustomerIdAndDeviceSerialNumber(@PathVariable Long customerId, @PathVariable String deviceSerialNumber) {
        return ResponseEntity.ok(service.findByCustomerIdAndDeviceSerialNumber(customerId, deviceSerialNumber));
    }
}
