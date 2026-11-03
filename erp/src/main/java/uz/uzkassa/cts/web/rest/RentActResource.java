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
import uz.uzkassa.cts.dto.rentact.request.RentActCreateRequestDto;
import uz.uzkassa.cts.dto.rentact.request.RentActRequestDto;
import uz.uzkassa.cts.dto.rentact.request.RentActUpdateRequestDto;
import uz.uzkassa.cts.dto.rentact.response.RentActListResponseDto;
import uz.uzkassa.cts.dto.rentact.response.RentActResponseDto;
import uz.uzkassa.cts.dto.tradeincontract.request.TradeInActPoaRequestDto;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.service.RentActService;

import java.util.List;

@RestController
@RequestMapping("/api/rent-act")
@Tag(name = "Rent act")
@Slf4j
public class RentActResource {

    private final RentActService service;

    public RentActResource(RentActService service) {
        this.service = service;
    }

    @Operation(summary = "List")
    @GetMapping
    public ResponseEntity<Page<RentActListResponseDto>> getAll(BaseFilter filter) {
        log.debug("REST request to get all TradeInContract : {}", filter);
        return ResponseEntity.ok(service.getList(filter));
    }

    @Operation(summary = "List by offerId")
    @GetMapping("/by-offer-id/{id}")
    public ResponseEntity<List<RentActListResponseDto>> getAll(@PathVariable Long id) {
        return ResponseEntity.ok(service.getByOfferId(id));
    }

    @Operation(summary = "List by offerId")
    @GetMapping("/get-with-accessory/customer-id/{customerId}")
    public ResponseEntity<List<RentActResponseDto>> getWithAccessory(@PathVariable Long customerId) {
        return ResponseEntity.ok(service.getActsToAddDevices(customerId));
    }

    @Operation(summary = "Create")
    @PostMapping
    public ResponseEntity<RentActResponseDto> create(@Valid @RequestBody RentActCreateRequestDto dto) {
        log.debug("REST request to create new TradeInContractResponseDto: {}", dto);
        return ResponseEntity.ok(service.create(dto));
    }

    @Operation(summary = "Update")
    @PutMapping
    public ResponseEntity<RentActResponseDto> update(@Valid @RequestBody RentActUpdateRequestDto dto) {
        log.debug("REST request to create new TradeInContractResponseDto: {}", dto);
        return ResponseEntity.ok(service.update(dto));
    }

    @Operation(summary = "Update")
    @PostMapping("/add-device")
    public ResponseEntity<RentActResponseDto> addDevice(@Valid @RequestBody RentActRequestDto dto) {
        log.debug("REST request to create new TradeInContractResponseDto: {}", dto);
        return ResponseEntity.ok(service.addDevice(dto));
    }

    @Operation(summary = "Get by id")
    @GetMapping("/{id}")
    public ResponseEntity<RentActResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @Operation(summary = "accept")
    @PutMapping("/{id}/status/accept")
    public ResponseEntity<RentActResponseDto> accept(@PathVariable Long id) {
        return ResponseEntity.ok(service.accept(id));
    }

    @Operation(summary = "doc flow status")
    @PostMapping("/{id}/change-doc-flow")
    public ResponseEntity<RentActResponseDto> changeDocFlowStatus(@Valid @RequestBody ActChangeDocFlowRequestDto dto) {
        return ResponseEntity.ok(service.changeDocFlowStatus(dto));
    }

    @Operation(summary = "wait for attachment")
    @PutMapping("/{id}/status/waiting-for-attachment")
    public ResponseEntity<RentActResponseDto> waitingForAttachment(@PathVariable Long id) {
        return ResponseEntity.ok(service.changeToAttach(id));
    }

    @Operation(summary = "generate template")
    @GetMapping("/{id}/generate-template")
    public ResponseEntity<String> getTemplate(@PathVariable Long id) {
        return ResponseEntity.ok(service.getHtmlTemplate(id));
    }

    //
    @Operation(summary = "upload file")
    @PostMapping("/{id}/upload")
    public ResponseEntity<RentActResponseDto> uploadFile(@RequestParam("file") MultipartFile file, @PathVariable Long id) {
        return ResponseEntity.ok(service.uploadFile(id, file));
    }

    @Operation(summary = "upload file")
    @PostMapping("/{id}/upload-power-attorney")
    public ResponseEntity<RentActResponseDto> uploadPowerAttorneyFile(@RequestParam("file") MultipartFile file, @PathVariable Long id) {
        return ResponseEntity.ok(service.uploadPowerAttorneyFile(id, file));
    }

    @Operation(summary = "upload file")
    @PostMapping("/{id}/image")
    public ResponseEntity<RentActResponseDto> uploadImage(@RequestParam("file") MultipartFile file, @PathVariable Long id) {
        return ResponseEntity.ok(service.uploadImage(id, file));
    }

    @Operation(summary = "List by tin")
    @GetMapping("/list-by-tin/{tin}")
    public ResponseEntity<Page<RentActListResponseDto>> getAllByTin(@PathVariable String tin, BaseFilter filter) {
        return ResponseEntity.ok(service.listByTin(tin, filter));
    }

    @Operation(summary = "Generate Power attorney Template")
    @PostMapping("/generate/poa/template")
    public ResponseEntity<String> generatePoaTemplate(@RequestBody TradeInActPoaRequestDto dto) {
        log.debug("REST request to generate POA template : {}", dto);
        return ResponseEntity.ok(service.generatePoaTemplate(dto));
    }
}
