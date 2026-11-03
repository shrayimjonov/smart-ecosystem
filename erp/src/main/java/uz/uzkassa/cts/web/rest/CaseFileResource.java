package uz.uzkassa.cts.web.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.cts.dto.casefile.request.CaseFileRequestDto;
import uz.uzkassa.cts.dto.casefile.response.CaseFileResponseDto;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.filter.CaseFilter;
import uz.uzkassa.cts.service.CaseFileService;
import uz.uzkassa.cts.web.rest.errors.BadRequestException;

@RestController
@RequestMapping("/api/case-file")
@Tag(name = "Case notes")
@Slf4j
@Deprecated
public class CaseFileResource {

    private final CaseFileService service;

    public CaseFileResource(CaseFileService service) {
        this.service = service;
    }

    @Operation(summary ="Get")
    @GetMapping("/{id}")
    public ResponseEntity<CaseFileResponseDto> getById(@PathVariable Long id) {
        log.debug("REST request to get CaseDetailDTO : {}", id);
        return ResponseEntity.ok(service.get(id));
    }

    @Operation(summary ="Get")
    @GetMapping("/get-by-case/{id}")
    public ResponseEntity<Page<CaseFileResponseDto>> getByCaseId(@PathVariable Long id, BaseFilter filter) {
        log.debug("REST request to get CaseDetailDTO : {}", id);
        return ResponseEntity.ok(service.getByCaseId(id, filter));
    }

    @Operation(summary ="List")
    @GetMapping
    public ResponseEntity<Page<CaseFileResponseDto>> list(CaseFilter filter) {
        log.debug("REST request to get all CaseListDTO: {}", filter);
        return ResponseEntity.ok(service.list(filter));
    }

    @Operation(summary ="upload file")
    @PostMapping("/upload")
    public ResponseEntity<CaseFileResponseDto> uploadByCaseId(@Valid @ModelAttribute CaseFileRequestDto dto, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("вы должны загрузить файл");
        }
        if (!file.getOriginalFilename().toLowerCase().endsWith("pdf")){
            throw new BadRequestException("вы должны загрузить только PDF-файл");
        }
        return ResponseEntity.ok(service.upload(dto, file));
    }

    @Operation(summary ="delete file")
    @DeleteMapping("/{id}/delete")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
