package uz.uzkassa.smartpossupply.web.rest.admin;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.smartpossupply.constants.ApiConstants;
import uz.uzkassa.smartpossupply.domain.FileUpload;
import uz.uzkassa.smartpossupply.service.FileUploadService;

import static lombok.AccessLevel.PRIVATE;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 27.02.2023 17:10
 */

@RestController
@RequestMapping(ApiConstants.adminFileUploadRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AdminFileUploadResource {

    FileUploadService fileUploadService;

    @Operation(summary = "Public File Upload")
    @PostMapping("/to-public")
    public ResponseEntity<FileUpload> publicFileUpload(@RequestPart("file") MultipartFile file) {
        return ResponseEntity.ok(fileUploadService.fileUpload(file, true));
    }

    @Operation(summary = "Private File Upload")
    @PostMapping("/to-private")
    public ResponseEntity<FileUpload> privateFileUpload(@RequestPart("file") MultipartFile file) {
        return ResponseEntity.ok(fileUploadService.fileUpload(file, false));
    }

    @Operation(summary = "Download")
    @GetMapping(ApiConstants.download + ApiConstants.id)
    public ResponseEntity<?> download(@PathVariable Long id) {
        return fileUploadService.download(id);
    }

    @Operation(summary = ApiConstants.Delete)
    @DeleteMapping(ApiConstants.id)
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        fileUploadService.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
