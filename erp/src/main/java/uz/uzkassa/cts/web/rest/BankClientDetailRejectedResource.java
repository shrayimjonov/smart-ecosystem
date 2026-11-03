package uz.uzkassa.cts.web.rest;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.cts.dto.bankclientdetailrejected.request.BankClientDetailsRejectEditRequestDto;
import uz.uzkassa.cts.dto.bankclientdetailrejected.request.BankClientDetailsRejectRequestDto;
import uz.uzkassa.cts.dto.bankclientdetailrejected.response.BankClientDetailRejectedResponseDto;
import uz.uzkassa.cts.filter.BankClientDetailRejectedFilter;
import uz.uzkassa.cts.service.BankClientDetailRejectedService;

@RestController
@RequestMapping("/api/bankClientDetailRejected")
@Tag(name = "bank client detail rejected")
@Slf4j
public class BankClientDetailRejectedResource {

    private final BankClientDetailRejectedService service;

    public BankClientDetailRejectedResource(BankClientDetailRejectedService service) {
        this.service = service;
    }

    @Operation(summary = "list of bank client")
    @GetMapping("/list")
    public ResponseEntity<Page<BankClientDetailRejectedResponseDto>> list(BankClientDetailRejectedFilter filter) {
        log.debug("REST request to get list: {}", filter);
        return ResponseEntity.ok(service.list(filter));
    }

    @Operation(summary = "list of bank client")
    @PostMapping("/resend")
    public ResponseEntity<Void> list(@RequestBody BankClientDetailsRejectRequestDto ids) {
        log.debug("REST request to resend to bankClient: {}", ids.getIds().toArray());
        service.resend(ids.getIds());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Change status")
    @PutMapping("/update-status/{id}")
    public ResponseEntity<Void> list(@PathVariable Long id) {
        log.debug("REST request to resend to BankClientDetailRejectedResource ID: {}", id);
        service.changeStatus(id);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "delete order")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.debug("REST request to to delete Order by id : {}", id);
        service.deleteIfStatusPending(id);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "edit order")
    @PostMapping
    public ResponseEntity<Void> edit(@RequestBody BankClientDetailsRejectEditRequestDto dto) {
        log.debug("REST request to to delete Order by id : {}", dto);
        service.updateInn(dto);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "list of bank client")
    @GetMapping("/download-report")
    public ResponseEntity<byte[]> download(BankClientDetailRejectedFilter filter) {
        log.debug("REST request to get list: {}", filter);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"TemporaryRejected.xlsx\"")
                .body(service.download(filter));
    }

}
