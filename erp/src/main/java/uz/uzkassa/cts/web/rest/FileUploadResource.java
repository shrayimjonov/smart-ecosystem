package uz.uzkassa.cts.web.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.cts.constants.FileConstants;
import uz.uzkassa.cts.dto.file.FileDTO;
import uz.uzkassa.cts.dto.uploadfile.response.UploadFileResponse;
import uz.uzkassa.cts.service.FileUploadService;
import uz.uzkassa.cts.utils.FileUtils;
import uz.uzkassa.cts.web.rest.errors.BadRequestException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * REST controller for managing {@link uz.uzkassa.cts.domain.FileUpload}.
 */
@RestController
@RequestMapping("/api/files")
@Tag(name = "File Upload")
@Slf4j
@Deprecated
public class FileUploadResource {

    private final FileUploadService fileUploadService;

    public FileUploadResource(FileUploadService fileUploadService) {
        this.fileUploadService = fileUploadService;
    }

    @Operation(summary = "File Upload")
    @PostMapping("/upload/{entityId}/{entityType}/{documentType}")
    public ResponseEntity<UploadFileResponse> fileUpload(@RequestParam("file") MultipartFile file,
                                                         @PathVariable Long entityId,
                                                         @PathVariable String entityType,
                                                         @PathVariable String documentType) {
        log.debug("REST request to upload file: {}", "Entity Id: " + entityId + " Entity Type: " + entityType + " Document Type: " + documentType);
        return ResponseEntity.ok(fileUploadService.upload(file, entityId, entityType, documentType));
    }

    @Operation(summary = "List Files")
    @GetMapping("/{entityId}/{entityType}")
    public ResponseEntity<List<UploadFileResponse>> list(@PathVariable Long entityId,
                                                         @PathVariable String entityType) {
        log.debug("REST request to upload file: {}", "Entity Id: " + entityId + " Entity Type: " + entityType);
        return ResponseEntity.ok(fileUploadService.list(entityId, entityType));
    }

    @Operation(summary = "Download")
    @GetMapping("/download/{path}")
    public ResponseEntity<Resource> download(@PathVariable String path) {
        try {
            Resource resource = fileUploadService.download(path);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=applicationFile.pdf")
                    .contentLength(resource.contentLength())
                    .contentType(MediaType.APPLICATION_PDF)
                    .cacheControl(CacheControl.noCache())
                    .body(resource);
        } catch (
                Exception e) {
            log.error(e.getMessage());
            throw new BadRequestException("Download file from service error: " + e.getMessage());
        }
    }

    @Operation(summary = "Convert File to Base64")
    @PostMapping("/convert/base64")
    public ResponseEntity<String> convertToBase64(@RequestBody FileDTO fileDTO) {
        return ResponseEntity.ok(FileUtils.fileToBase64(fileDTO.getUrl()));
    }

    @Operation(summary = "Convert")
    @GetMapping("/convert")
    public ResponseEntity<String> convert() {
        return ResponseEntity.ok(FileUtils.fileToBase64(FileConstants.CONTRACT_OFFER_FILE_URL));
    }

    @Operation(summary = "Convert")
    @GetMapping("/convert2")
    public ResponseEntity<String> convert2() throws IOException {
        Resource resource = new ClassPathResource("/data/contract_offer_base64.txt");
        InputStream inputStream = resource.getInputStream();
        byte[] byteArray = FileCopyUtils.copyToByteArray(inputStream);
        String data = new String(byteArray, StandardCharsets.UTF_8);
        return ResponseEntity.ok(data);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        fileUploadService.deleteById(id);
        return ResponseEntity.ok().build();
    }

}
