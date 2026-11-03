package uz.uzkassa.cts.web.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.dto.document.DocumentDTO;
import uz.uzkassa.cts.dto.document.DocumentListDTO;
import uz.uzkassa.cts.filter.DocumentFilter;
import uz.uzkassa.cts.service.DocumentService;

@RestController
@RequestMapping("/api/company/documents")
@Tag(name = "Company Documents")
@Slf4j
@Deprecated
public class CompanyDocumentResource {

    private final DocumentService documentService;

    public CompanyDocumentResource(DocumentService documentService) {
        this.documentService = documentService;
    }

    @Operation(summary ="List")
    @GetMapping
    public ResponseEntity<Page<DocumentListDTO>> getList(DocumentFilter filter) {
        log.debug("REST request to get DocumentListDTO : {}", filter);
        return ResponseEntity.ok(documentService.getList(filter));
    }

    @Operation(summary ="Upload")
    @PostMapping("/upload/{folderId}")
    public ResponseEntity<Long> upload(@RequestParam("file") MultipartFile file, @PathVariable Long folderId) {
        log.debug("REST request to upload new document {}", folderId);
        return ResponseEntity.ok(documentService.upload(file, folderId));
    }

    @Operation(summary ="Delete")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.debug("REST request to delete document : {}", id);
        documentService.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
