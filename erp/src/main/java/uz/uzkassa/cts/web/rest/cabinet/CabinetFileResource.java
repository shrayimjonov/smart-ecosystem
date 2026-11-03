package uz.uzkassa.cts.web.rest.cabinet;

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
@RequestMapping(ApiConstants.cabinetFileRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CabinetFileResource {

    FileUploadService fileUploadService;

    @PostMapping("/upload/{entityType}/{entityId}/{documentType}")
    public ResponseEntity<Void> upload(@RequestParam("file") MultipartFile file,
                                       @PathVariable EntityTypeEnum entityType,
                                       @PathVariable Long entityId,
                                       @PathVariable DocumentType documentType) {
        fileUploadService.upload(file, entityType, entityId, documentType);
        return ResponseEntity.ok().build();
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
