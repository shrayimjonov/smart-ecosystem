package uz.uzkassa.cts.web.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.cts.dto.bankclient.request.BankClientDto;
import uz.uzkassa.cts.dto.bankclient.response.BankClientDetailsDto;
import uz.uzkassa.cts.dto.bankclient.response.BankClientUploadDto;
import uz.uzkassa.cts.dto.bankclient.response.BankClientUploadPageableDto;
import uz.uzkassa.cts.filter.BankClientDetailFilter;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.service.BankClientDetailService;
import uz.uzkassa.cts.service.BankClientService;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/bankClientJournal")
@Tag(name = "Ledger")
@Slf4j
public class BankClientResource {

    private final BankClientService service;
    private final BankClientDetailService detailService;

    public BankClientResource(BankClientService service, BankClientDetailService detailService) {
        this.service = service;
        this.detailService = detailService;
    }

    @Operation(summary ="create new journal to upload file")
    @PostMapping
    public ResponseEntity<BankClientDto> create(@Valid @RequestBody BankClientDto dto) {
        log.debug("REST request to create BankClientDto: {}", dto);
        return ResponseEntity.ok(service.create(dto));
    }

    @Operation(summary ="list of bank client")
    @GetMapping("/list")
    public ResponseEntity<Page<BankClientDto>> list(BaseFilter filter) {
        log.debug("REST request to get list: {}", filter);
        return ResponseEntity.ok(service.list(filter));
    }

    @Operation(summary ="get by id")
    @GetMapping("/{id}")
    public ResponseEntity<BankClientDto> getById(@PathVariable Long id) {
        log.debug("REST request to get by id: {}", id);
        return ResponseEntity.ok(service.getById(id));
    }

    @Operation(summary ="get by id")
    @GetMapping("/{id}/getDetails")
    public ResponseEntity<BankClientUploadDto> getDetails(@PathVariable Long id) {
        log.debug("REST request to get by id: {}", id);
        BankClientUploadDto result = service.getDetails(id);
        return ResponseEntity.ok(result);
    }

    @Operation(summary ="get by filter")
    @GetMapping("/getDetails")
    public ResponseEntity<BankClientUploadPageableDto> getDetailsPageable(BankClientDetailFilter filter) {
        log.debug("REST request to get by filter: {}", filter);
        return ResponseEntity.ok(service.getDetails(filter));
    }

    @Operation(summary ="upload file to journal")
    @PostMapping("{id}/upload")
    public ResponseEntity<List<BankClientDetailsDto>> createJournalDetails(@PathVariable("id") Long id,
                                                                           @RequestParam("file") MultipartFile file) throws IOException {
        log.debug("REST request to create details upload file name: {}", file.getOriginalFilename());
        return ResponseEntity.ok(service.createDetails(id, file));
    }

    @Operation(summary ="upload file to journal")
    @PostMapping("{id}/uploadWithFile")
    public ResponseEntity<BankClientUploadDto> createJournalDetailsWithFiles(@PathVariable("id") Long id,
                                                                             @RequestParam("file") MultipartFile file) throws IOException {
        log.debug("REST request to create details upload file name: {}", file.getOriginalFilename());
        return ResponseEntity.ok(service.createDetailWithFile(id, file));
    }

    @Operation(summary ="approve journal rows for transaction in Ledger")
    @PostMapping("/{id}/details/approve")
    public ResponseEntity<List<BankClientDetailsDto>> approveTransactions(@PathVariable("id") Long id) {
        log.debug("REST request to approve transaction by id: {}", id);
        return ResponseEntity.ok(service.approveDetails(id));
    }

    @Operation(summary ="approve only one to for transaction in ledger")
    @PostMapping("/details/{id}/approve")
    public ResponseEntity<BankClientDetailsDto> approveTransaction(@PathVariable("id") Long id) {
        log.debug("REST request to approve transaction by id: {}", id);
        return ResponseEntity.ok(detailService.approve(id));
    }

    @Operation(summary ="approve only one to for transaction in ledger")
    @PostMapping("/upload-test")
    public ResponseEntity<String> uploadTest(@RequestParam("file") MultipartFile file) {
        log.debug("REST request to  upload test file name: {}", file.getOriginalFilename());
        service.uploadTest(file);
        return ResponseEntity.ok().build();
    }
}
