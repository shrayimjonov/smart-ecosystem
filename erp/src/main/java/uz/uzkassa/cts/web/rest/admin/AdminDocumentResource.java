package uz.uzkassa.cts.web.rest.admin;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.cts.constants.ApiConstants;
import uz.uzkassa.cts.dto.document.DocumentListDTO;
import uz.uzkassa.cts.filter.DocumentFilter;
import uz.uzkassa.cts.service.DocumentService;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.adminDocumentRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AdminDocumentResource {

    DocumentService documentService;

    @PostMapping("/upload/{folderId}")
    public ResponseEntity<Long> upload(@RequestParam("file") MultipartFile file, @PathVariable Long folderId) {
        return ResponseEntity.ok(documentService.upload(file, folderId));
    }

    @GetMapping
    public ResponseEntity<Page<DocumentListDTO>> getList(@ParameterObject DocumentFilter filter) {
        return ResponseEntity.ok(documentService.getList(filter));
    }

    @PatchMapping(ApiConstants.id + "/rename")
    public ResponseEntity<Void> rename(@PathVariable Long id, @RequestParam String name) {
        documentService.rename(id, name);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping(ApiConstants.id)
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        documentService.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
