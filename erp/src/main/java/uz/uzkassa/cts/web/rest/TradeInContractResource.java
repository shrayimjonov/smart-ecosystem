package uz.uzkassa.cts.web.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.cts.dto.tradeincontract.request.TradeInContractRequestDto;
import uz.uzkassa.cts.dto.tradeincontract.request.TradeInDeviceAddRequestDto;
import uz.uzkassa.cts.dto.tradeincontract.response.TradeInContractResponseDto;
import uz.uzkassa.cts.filter.TradeInContractFilter;
import uz.uzkassa.cts.service.TradeInContractService;

import java.util.List;

@RestController
@RequestMapping("/api/trade-in-contract")
@Tag(name = "TradeIn contract")
@Slf4j
public class TradeInContractResource {

    private final TradeInContractService tradeInContractService;

    public TradeInContractResource(TradeInContractService tradeInContractService) {
        this.tradeInContractService = tradeInContractService;
    }

    @Operation(summary ="List")
    @GetMapping
    public ResponseEntity<Page<TradeInContractResponseDto>> getAll(TradeInContractFilter filter) {
        log.debug("REST request to get all TradeInContract : {}", filter);
        return ResponseEntity.ok(tradeInContractService.list(filter));
    }

   @Operation(summary ="List")
    @GetMapping("test-list")
    public ResponseEntity<Page<TradeInContractResponseDto>> getAllTest(TradeInContractFilter filter) {
        log.debug("REST request to get all TradeInContract : {}", filter);
        return ResponseEntity.ok(tradeInContractService.getList(filter));
    }

    @Operation(summary ="Create")
    @PostMapping
    public ResponseEntity<TradeInContractResponseDto> create(@Valid @RequestBody TradeInContractRequestDto dto) {
        log.debug("REST request to create new TradeInContractResponseDto: {}", dto);
        return ResponseEntity.ok(tradeInContractService.create(dto));
    }

    @Operation(summary ="Get by id")
    @GetMapping("/{id}")
    public ResponseEntity<TradeInContractResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(tradeInContractService.getById(id));
    }

    @Operation(summary ="accept")
    @PutMapping("/{id}/status/accept")
    public ResponseEntity<TradeInContractResponseDto> accept(@PathVariable Long id) {
        return ResponseEntity.ok(tradeInContractService.accept(id));
    }

    @Operation(summary ="reject")
    @PutMapping("/{id}/status/reject")
    public ResponseEntity<TradeInContractResponseDto> reject(@PathVariable Long id) {
        return ResponseEntity.ok(tradeInContractService.reject(id));
    }

    @Operation(summary ="wait for attachment")
    @PutMapping("/{id}/status/waiting-for-attachment")
    public ResponseEntity<TradeInContractResponseDto> waitingForAttachment(@PathVariable Long id) {
        return ResponseEntity.ok(tradeInContractService.waitingForAttach(id));
    }

    @Operation(summary ="generate template")
    @GetMapping("/{id}/generate-contract-template")
    public ResponseEntity<String> getTemplate(@PathVariable Long id) {
        return ResponseEntity.ok(tradeInContractService.getContractHtmlTemplate(id));
    }

    @Operation(summary ="upload file")
    @PostMapping("/{id}/upload-contract")
    public ResponseEntity<TradeInContractResponseDto> uploadFileContract(@RequestParam("file") MultipartFile file, @PathVariable Long id) {
        log.debug("REST request to upload file : {}", file);
        return ResponseEntity.ok(tradeInContractService.uploadContractFile(id, file));
    }

    @Operation(summary ="List by tin")
    @GetMapping("/list-by-tin/{tin}")
    public ResponseEntity<Page<TradeInContractResponseDto>> getAllByTin(@PathVariable String tin, TradeInContractFilter filter) {
        return ResponseEntity.ok(tradeInContractService.listByTin(filter, tin));
    }

    @Operation(summary ="List by tin")
    @GetMapping("/list-by-tin/{tin}/get-active")
    public ResponseEntity<List<TradeInContractResponseDto>> getActive(@PathVariable String tin) {
        return ResponseEntity.ok(tradeInContractService.getContractWithoutAct(tin));
    }

    @Operation(summary ="add device")
    @PostMapping("/{id}/add-device")
    public ResponseEntity<TradeInContractResponseDto> addDevices(@PathVariable Long id, @Valid @RequestBody TradeInDeviceAddRequestDto dto) {
        log.debug("REST request to add device : {}", dto);
        return ResponseEntity.ok(tradeInContractService.addDevice(id, dto, dto.isSkipValidation()));
    }

//    @Operation(summary ="getByIdTestHtml")
//    @GetMapping("/")
//    public ResponseEntity<String> getByIdTestHtml() {
//        return ResponseEntity.ok(tradeInContractService.contractHTMLTest(1l));
//    }




}
