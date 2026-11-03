package uz.uzkassa.smartpos.web.rest;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.smartpos.domain.postgres.receipt.ReceiptTemplate;
import uz.uzkassa.smartpos.dto.receipt.ReceiptTemplateDTO;
import uz.uzkassa.smartpos.integration.apay.FileDTO;
import uz.uzkassa.smartpos.service.ReceiptTemplateService;

import static uz.uzkassa.smartpos.security.AuthoritiesConstants.OWNER;

/**
 * REST controller for managing {@link ReceiptTemplate}.
 */
@RestController
@RequestMapping("/api/receipt-template")
@Slf4j
public class ReceiptTemplateResource {
    private final ReceiptTemplateService receiptTemplateService;

    public ReceiptTemplateResource(ReceiptTemplateService receiptTemplateService) {
        this.receiptTemplateService = receiptTemplateService;
    }

    @PostMapping(value = "/header-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<FileDTO> uploadHeaderImage(@RequestParam(required = false) Long receiptTemplateId, @RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(receiptTemplateService.uploadHeaderImage(receiptTemplateId, file));
    }

    @PostMapping(value = "/footer-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<FileDTO> uploadFooterImage(@RequestParam(required = false) Long receiptTemplateId, @RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(receiptTemplateService.uploadFooterImage(receiptTemplateId, file));
    }

    @PutMapping
    @Secured({OWNER})
    public ResponseEntity<ReceiptTemplateDTO> update(@Valid @RequestBody ReceiptTemplateDTO receiptTemplateDTO) {
        ReceiptTemplateDTO result = receiptTemplateService.update(receiptTemplateDTO);
        return ResponseEntity.ok(result);
    }

    /**
     * {@code GET  /receipt_template} : get the receipt template.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with bondy the receipt_template, or with status {@code 404 (Not Found)}.
     */
    @GetMapping
    public ResponseEntity<ReceiptTemplateDTO> getReceiptTemplate() {
        ReceiptTemplateDTO receiptTemplate = receiptTemplateService.getByCompany();
        return ResponseEntity.ok(receiptTemplate);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        receiptTemplateService.delete(id);
        return ResponseEntity.ok().build();
    }
}
