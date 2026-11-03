package uz.uzkassa.cts.web.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.cts.dto.tradeincountercontract.request.TradeInCounterContractEditRequestDto;
import uz.uzkassa.cts.dto.tradeincountercontract.request.TradeInCounterContractRequestDto;
import uz.uzkassa.cts.dto.tradeincountercontract.response.TradeInCounterContractResponseDto;
import uz.uzkassa.cts.filter.TradeInCounterContractFilter;
import uz.uzkassa.cts.service.TradeInCounterContractService;

@RestController
@RequestMapping("/api/trade-in-counter-contract")
@Tag(name = "TradeIn counter contract")
@Slf4j
public class TradeInCounterContractResource {
    private final TradeInCounterContractService tradeInCounterContractService;

    public TradeInCounterContractResource(TradeInCounterContractService tradeInCounterContractService) {
        this.tradeInCounterContractService = tradeInCounterContractService;
    }

    @Operation(summary ="List")
    @GetMapping
    public ResponseEntity<Page<TradeInCounterContractResponseDto>> getAll(TradeInCounterContractFilter filter) {
        log.debug("REST request to get all TradeInCounterContract : {}", filter);
        return ResponseEntity.ok(tradeInCounterContractService.list(filter));
    }

    @Operation(summary ="Create")
    @PostMapping
    public ResponseEntity<TradeInCounterContractResponseDto> create(@Valid @RequestBody TradeInCounterContractRequestDto dto) {
        log.debug("REST request to create new TradeInCounterContractResponseDto: {}", dto);
        return ResponseEntity.ok(tradeInCounterContractService.create(dto));
    }

    @Operation(summary ="Update")
    @PutMapping
    public ResponseEntity<TradeInCounterContractResponseDto> update(@Valid @RequestBody TradeInCounterContractEditRequestDto dto) {
        log.debug("REST request to update new TradeInCounterContractEditResponseDto: {}", dto);
        return ResponseEntity.ok(tradeInCounterContractService.update(dto));
    }

    @Operation(summary ="Get by id")
    @GetMapping("/{id}")
    public ResponseEntity<TradeInCounterContractResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(tradeInCounterContractService.getById(id));
    }

    @Operation(summary ="accept")
    @PutMapping("/{id}/status/accept")
    public ResponseEntity<TradeInCounterContractResponseDto> accept(@PathVariable Long id) {
        return ResponseEntity.ok(tradeInCounterContractService.accept(id));
    }

    @Operation(summary ="reject")
    @PutMapping("/{id}/status/reject")
    public ResponseEntity<TradeInCounterContractResponseDto> reject(@PathVariable Long id) {
        return ResponseEntity.ok(tradeInCounterContractService.reject(id));
    }

    @Operation(summary ="generate template")
    @GetMapping("/{id}/generate-contract-template")
    public ResponseEntity<String> getTemplate(@PathVariable Long id) {
        return ResponseEntity.ok(tradeInCounterContractService.getContractHtmlTemplate(id));
    }

    @Operation(summary ="upload file")
    @PostMapping("/{id}/upload-contract")
    public ResponseEntity<TradeInCounterContractResponseDto> uploadFileContract(@RequestParam("file") MultipartFile file, @PathVariable Long id) {
        log.debug("REST request to upload file : {}", file);
        return ResponseEntity.ok(tradeInCounterContractService.uploadContractFile(id, file));
    }

    @Operation(summary ="upload file")
    @PostMapping("/{id}/upload-note")
    public ResponseEntity<TradeInCounterContractResponseDto> uploadNote(@RequestParam("file") MultipartFile file, @PathVariable Long id) {
        log.debug("REST request to upload file : {}", file);
        return ResponseEntity.ok(tradeInCounterContractService.uploadNote(id, file));
    }

    @Operation(summary ="upload file")
    @PostMapping("/{id}/upload-device-image")
    public ResponseEntity<TradeInCounterContractResponseDto> uploadDeviceImage(@RequestParam("file") MultipartFile file, @PathVariable Long id) {
        log.debug("REST request to upload file : {}", file);
        return ResponseEntity.ok(tradeInCounterContractService.uploadDeviceImage(id, file));
    }

    @Operation(summary ="List by tin")
    @GetMapping("/list-by-tin/{tin}")
    public ResponseEntity<Page<TradeInCounterContractResponseDto>> getAllByTin(@PathVariable String tin, TradeInCounterContractFilter filter) {
        return ResponseEntity.ok(tradeInCounterContractService.listByTin(filter, tin));
    }

//    @Operation(summary ="add device")
//    @PostMapping("/{id}/add-device")
//    public ResponseEntity<TradeInCounterContractResponseDto> addDevices(@PathVariable Long id, @Valid @RequestBody TradeInDeviceAddRequestDto dto) {
//        log.debug("REST request to add device : {}", dto);
//        return ResponseEntity.ok(tradeInCounterContractService.addDevice(id, dto));
//    }
}
