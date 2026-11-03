package uz.uzkassa.cts.web.rest;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.cts.dto.rentoffer.request.RentOfferRequestDto;
import uz.uzkassa.cts.dto.rentoffer.response.RentOfferResponseDto;
import uz.uzkassa.cts.filter.TradeInContractFilter;
import uz.uzkassa.cts.service.RentOfferService;

@RestController
@RequestMapping("/api/rent-offer")
@Tag(name = "Rent offer")
@Slf4j
public class RentOfferResource {

    private final RentOfferService service;

    public RentOfferResource(RentOfferService service) {
        this.service = service;
    }

    @Operation(summary ="List")
    @GetMapping
    public ResponseEntity<Page<RentOfferResponseDto>> getAll(TradeInContractFilter filter) {
        log.debug("REST request to get all TradeInContract : {}", filter);
        return ResponseEntity.ok(service.list(filter));
    }

    @Operation(summary ="Create")
    @PostMapping
    public ResponseEntity<RentOfferResponseDto> create(@Valid @RequestBody RentOfferRequestDto dto) {
        log.debug("REST request to create new TradeInContractResponseDto: {}", dto);
        return ResponseEntity.ok(service.create(dto));
    }

    @Operation(summary ="Get by id")
    @GetMapping("/{id}")
    public ResponseEntity<RentOfferResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @Operation(summary ="accept")
    @PutMapping("/{id}/status/accept")
    public ResponseEntity<RentOfferResponseDto> accept(@PathVariable Long id) {
        return ResponseEntity.ok(service.accept(id));
    }

    @Operation(summary ="reject")
    @PutMapping("/{id}/status/reject")
    public ResponseEntity<RentOfferResponseDto> reject(@PathVariable Long id) {
        return ResponseEntity.ok(service.reject(id));
    }

    @Operation(summary ="wait for attachment")
    @PutMapping("/{id}/status/waiting-for-attachment")
    public ResponseEntity<RentOfferResponseDto> waitingForAttachment(@PathVariable Long id) {
        return ResponseEntity.ok(service.waitingForAttach(id));
    }

    @Operation(summary ="generate template")
    @GetMapping("/{id}/generate-template")
    public ResponseEntity<String> getTemplate(@PathVariable Long id) {
        return ResponseEntity.ok(service.getHtmlTemplate(id));
    }

    @Operation(summary ="upload file")
    @PostMapping("/{id}/upload-contract")
    public ResponseEntity<RentOfferResponseDto> uploadFileContract(@RequestParam("file") MultipartFile file, @PathVariable Long id) {
        log.debug("REST request to upload file : {}", file);
        return ResponseEntity.ok(service.uploadContractFile(id, file));
    }

    @Operation(summary ="List by tin")
    @GetMapping("/list-by-tin/{tin}")
    public ResponseEntity<Page<RentOfferResponseDto>> getAllByTin(@PathVariable String tin, TradeInContractFilter filter) {
        return ResponseEntity.ok(service.listByTin(filter, tin));
    }

}
