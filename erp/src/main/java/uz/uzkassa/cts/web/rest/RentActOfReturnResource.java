package uz.uzkassa.cts.web.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.cts.dto.actoftransmission.response.ActChangeDocFlowRequestDto;
import uz.uzkassa.cts.dto.rentactofreturn.request.RentActOfReturnRequestDto;
import uz.uzkassa.cts.dto.rentactofreturn.request.RentActOfReturnUpdateRequestDto;
import uz.uzkassa.cts.dto.rentactofreturn.response.RentActOfReturnListResponseDto;
import uz.uzkassa.cts.dto.rentactofreturn.response.RentActOfReturnResponseDto;
import uz.uzkassa.cts.dto.tradeincontract.request.TradeInActPoaRequestDto;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.service.RentActOfReturnService;

import java.util.List;

@RestController
@RequestMapping("/api/rent-act-of-return")
@Tag(name = "Rent act")
@Slf4j
class RentActOfReturnResource {
    private final RentActOfReturnService service;

    public RentActOfReturnResource(RentActOfReturnService service) {
        this.service = service;
    }

    @Operation(summary ="List")
    @GetMapping
    public ResponseEntity<Page<RentActOfReturnListResponseDto>> getAll(BaseFilter filter) {
        log.debug("REST request to get all TradeInContract : {}", filter);
        return ResponseEntity.ok(service.getList(filter));
    }

    @Operation(summary ="List by offerId")
    @GetMapping("/customerId/{customerId}")
    public ResponseEntity<List<RentActOfReturnListResponseDto>> getAll(@PathVariable Long customerId) {
        return ResponseEntity.ok(service.getByCustomerId(customerId));
    }

    @Operation(summary ="Create")
    @PostMapping
    public ResponseEntity<RentActOfReturnResponseDto> create(@Valid @RequestBody RentActOfReturnRequestDto dto) {
        log.debug("REST request to create new TradeInContractResponseDto: {}", dto);
        return ResponseEntity.ok(service.create(dto));
    }

    @Operation(summary ="Update")
    @PutMapping
    public ResponseEntity<RentActOfReturnResponseDto> update(@Valid @RequestBody RentActOfReturnUpdateRequestDto dto) {
        log.debug("REST request to create new TradeInContractResponseDto: {}", dto);
        return ResponseEntity.ok(service.update(dto));
    }

    @Operation(summary ="Get by id")
    @GetMapping("/{id}")
    public ResponseEntity<RentActOfReturnResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @Operation(summary ="accept")
    @PutMapping("/{id}/status/accept")
    public ResponseEntity<RentActOfReturnResponseDto> accept(@PathVariable Long id) {
        return ResponseEntity.ok(service.accept(id));
    }

    @Operation(summary ="doc flow status")
    @PostMapping("/{id}/change-doc-flow")
    public ResponseEntity<RentActOfReturnResponseDto> changeDocFlowStatus(@Valid @RequestBody ActChangeDocFlowRequestDto dto) {
        return ResponseEntity.ok(service.changeDocFlowStatus(dto));
    }

    @Operation(summary ="wait for attachment")
    @PutMapping("/{id}/status/waiting-for-attachment")
    public ResponseEntity<RentActOfReturnResponseDto> waitingForAttachment(@PathVariable Long id) {
        return ResponseEntity.ok(service.changeToAttach(id));
    }

    @Operation(summary ="generate template")
    @GetMapping("/{id}/generate-template")
    public ResponseEntity<String> getTemplate(@PathVariable Long id) {
        return ResponseEntity.ok(service.getHtmlTemplate(id));
    }

    @Operation(summary ="upload file")
    @PostMapping("/{id}/upload")
    public ResponseEntity<RentActOfReturnResponseDto> uploadFile(@RequestParam("file") MultipartFile file, @PathVariable Long id) {
        return ResponseEntity.ok(service.uploadFile(id, file));
    }

    @Operation(summary ="upload file")
    @PostMapping("/{id}/upload-power-attorney")
    public ResponseEntity<RentActOfReturnResponseDto> uploadPowerAttorneyFile(@RequestParam("file") MultipartFile file, @PathVariable Long id) {
        return ResponseEntity.ok(service.uploadPowerAttorneyFile(id, file));
    }

    @Operation(summary ="upload file")
    @PostMapping("/{id}/image")
    public ResponseEntity<RentActOfReturnResponseDto> uploadImage(@RequestParam("file") MultipartFile file, @PathVariable Long id) {
        return ResponseEntity.ok(service.uploadImage(id, file));
    }

    @Operation(summary ="find")
    @GetMapping("/customerId/{customerId}/device-serial-number/{deviceSerialNumber}")
    public ResponseEntity<RentActOfReturnListResponseDto> findByCustomerIdAndDeviceSerialNumber(@PathVariable Long customerId, @PathVariable String deviceSerialNumber) {
        return ResponseEntity.ok(service.findByCustomerIdAndDeviceSerialNumber(customerId, deviceSerialNumber));
    }

    @Operation(summary ="Generate Power attorney Template")
    @PostMapping("/generate/poa/template")
    public ResponseEntity<String> generatePoaTemplate(@RequestBody TradeInActPoaRequestDto dto) {
        log.debug("REST request to generate POA template : {}", dto);
        return ResponseEntity.ok(service.generatePoaTemplate(dto));
    }
}
