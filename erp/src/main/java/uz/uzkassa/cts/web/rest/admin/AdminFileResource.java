package uz.uzkassa.cts.web.rest.admin;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.cts.constants.ApiConstants;
import uz.uzkassa.cts.dto.file.FileDetailDTO;
import uz.uzkassa.cts.dto.file.FileListDTO;
import uz.uzkassa.cts.enums.DocumentType;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.service.FileUploadService;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.adminFileRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AdminFileResource {

    FileUploadService fileUploadService;

    @PostMapping("/upload/{entityType}/{entityId}/{documentType}")
    public ResponseEntity<Long> upload(@RequestParam("file") MultipartFile file,
                                       @PathVariable EntityTypeEnum entityType,
                                       @PathVariable Long entityId,
                                       @PathVariable DocumentType documentType) {
        return ResponseEntity.ok(fileUploadService.upload(file, entityType, entityId, documentType).getId());
    }

    @PostMapping("/upload")
    public ResponseEntity<Long> uploadFile(@RequestParam("file") MultipartFile file,
                                           @RequestParam(value = "entityType", required = false) EntityTypeEnum entityType,
                                           @RequestParam(value = "entityId", required = false) Long entityId,
                                           @RequestParam(value = "documentType", required = false) DocumentType documentType) {
        return ResponseEntity.ok(fileUploadService.upload(file, entityType, entityId, documentType).getId());
    }

    @GetMapping("/{entityType}/{entityId}")
    public ResponseEntity<List<FileListDTO>> getList(@PathVariable EntityTypeEnum entityType, @PathVariable Long entityId) {
        return ResponseEntity.ok(fileUploadService.getList(entityType, entityId));
    }

    @GetMapping(ApiConstants.id)
    public ResponseEntity<FileDetailDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(fileUploadService.get(id));
    }

    @DeleteMapping(ApiConstants.id)
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        fileUploadService.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
