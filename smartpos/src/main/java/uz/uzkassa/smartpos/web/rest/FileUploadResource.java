package uz.uzkassa.smartpos.web.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.smartpos.integration.apay.FileDTO;
import uz.uzkassa.smartpos.service.FileUploadService;

@RestController
@RequestMapping("/api/v1/files")
@RequiredArgsConstructor
public class FileUploadResource {
    private final FileUploadService fileUploadService;

    @PostMapping(value = "/public/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public FileDTO uploadToPublic(@RequestParam MultipartFile file) {
        return new FileDTO(fileUploadService.fileUpload(file, null, null, true));
    }

    @PostMapping(value = "/private/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public FileDTO uploadToPrivate(@RequestParam MultipartFile file) {
        return new FileDTO(fileUploadService.fileUpload(file, null, null, false));
    }

    @DeleteMapping("/{id}")
    public void deleteFile(@PathVariable Long id) {
        fileUploadService.deleteFile(id);
    }
}
