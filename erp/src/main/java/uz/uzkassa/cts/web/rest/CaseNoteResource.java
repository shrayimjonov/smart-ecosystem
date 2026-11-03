package uz.uzkassa.cts.web.rest;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.cts.dto.casenote.request.CaseNoteRequestDto;
import uz.uzkassa.cts.dto.casenote.response.CaseNoteResponseDto;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.service.CaseNoteService;

@RestController
@RequestMapping("/api/case-note")
@Tag(name = "Case notes")
@Slf4j
@Deprecated
public class CaseNoteResource {

    private final CaseNoteService service;

    public CaseNoteResource(CaseNoteService service) {
        this.service = service;
    }

    @Operation(summary ="Get")
    @GetMapping("/{id}")
    public ResponseEntity<CaseNoteResponseDto> getById(@PathVariable Long id) {
        log.debug("REST request to get CaseDetailDTO : {}", id);
        return ResponseEntity.ok(service.get(id));
    }

    @Operation(summary ="Get")
    @GetMapping("/get-by-case/{id}")
    public ResponseEntity<Page<CaseNoteResponseDto>> getByCaseId(@PathVariable Long id, BaseFilter filter) {
        log.debug("REST request to get CaseDetailDTO : {}", id);
        return ResponseEntity.ok(service.getByCaseId(id, filter));
    }

    @Operation(summary ="create")
    @PostMapping("/create")
    public ResponseEntity<CaseNoteResponseDto> createByCaseId(@RequestBody CaseNoteRequestDto dto) {
        return ResponseEntity.ok(service.create(dto));
    }

    @Operation(summary ="upload file")
    @PostMapping("/case-id/{id}/upload")
    public ResponseEntity<CaseNoteResponseDto> uploadByCaseId(@RequestParam("file") MultipartFile file, @PathVariable Long id) {
        return ResponseEntity.ok(service.uploadByCaseId(id, file));
    }

    @Operation(summary ="delete file")
    @DeleteMapping("{id}/delete")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
