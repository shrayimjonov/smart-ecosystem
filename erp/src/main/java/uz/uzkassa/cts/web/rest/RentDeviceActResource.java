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
import uz.uzkassa.cts.dto.actoftransmission.response.ActChangDocFlowRequestDto;
import uz.uzkassa.cts.dto.rentDeviceAct.RentDeviceActDTO;
import uz.uzkassa.cts.dto.rentDeviceAct.RentDeviceActListDTO;
import uz.uzkassa.cts.dto.rentDeviceAct.RentDeviceActPowerOfAttorneyDTO;
import uz.uzkassa.cts.filter.RentActFilter;
import uz.uzkassa.cts.service.RentDeviceActService;

@RestController
@RequestMapping("/api/rent-device-act")
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RentDeviceActResource {

    RentDeviceActService service;

    @Operation(summary = "List")
    @GetMapping
    public ResponseEntity<Page<RentDeviceActListDTO>> getAll(RentActFilter filter) {
        return ResponseEntity.ok(service.getList(filter));
    }

    @Operation(summary = "Get by id")
    @GetMapping("/{id}")
    public ResponseEntity<RentDeviceActDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @Operation(summary = "accept")
    @PutMapping("/{id}/status/accept")
    public ResponseEntity<RentDeviceActDTO> accept(@PathVariable Long id) {
        return ResponseEntity.ok(service.accept(id));
    }

    @Operation(summary = "reject")
    @PutMapping("/{id}/status/reject")
    public ResponseEntity<RentDeviceActDTO> reject(@PathVariable Long id) {
        return ResponseEntity.ok(service.reject(id));
    }

    @Operation(summary = "doc flow status")
    @PostMapping("/{id}/change-doc-flow")
    public ResponseEntity<RentDeviceActDTO> changeDocFlowStatus(@Valid @RequestBody ActChangDocFlowRequestDto dto) {
        return ResponseEntity.ok(service.changeDocFlowStatus(dto));
    }

    @Operation(summary = "wait for attachment")
    @PutMapping("/{id}/status/waiting-for-attachment")
    public ResponseEntity<RentDeviceActDTO> waitingForAttachment(@PathVariable Long id) {
        return ResponseEntity.ok(service.changeToAttach(id));
    }

    @Operation(summary = "generate template")
    @GetMapping("/{id}/generate-template")
    public ResponseEntity<String> getTemplate(@PathVariable Long id) {
        return ResponseEntity.ok(service.getHtmlTemplate(id));
    }

    @Operation(summary = "upload file")
    @PostMapping("/{id}/upload")
    public ResponseEntity<RentDeviceActDTO> uploadFile(@RequestParam("file") MultipartFile file, @PathVariable Long id) {
        return ResponseEntity.ok(service.uploadFile(id, file));
    }

    @Operation(summary = "Generate POA Template")
    @PostMapping("/generate/poa/template")
    public ResponseEntity<String> generatePoaTemplate(@RequestBody RentDeviceActPowerOfAttorneyDTO dto) {
        return ResponseEntity.ok(service.generatePowerOfAttorneyTemplate(dto));
    }

    @Operation(summary = "Upload Power of Attorney")
    @PostMapping("/{id}/upload-power-attorney")
    public ResponseEntity<RentDeviceActDTO> uploadPowerOfAttorneyFile(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(service.uploadPowerAttorneyFile(id, file));
    }
}
