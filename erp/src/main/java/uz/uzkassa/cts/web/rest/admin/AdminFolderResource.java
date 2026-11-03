package uz.uzkassa.cts.web.rest.admin;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.cts.constants.ApiConstants;
import uz.uzkassa.cts.dto.document.FolderDTO;
import uz.uzkassa.cts.dto.document.FolderTreeDTO;
import uz.uzkassa.cts.service.FolderService;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.adminFolderRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AdminFolderResource {

    FolderService folderService;

    @PostMapping
    public ResponseEntity<Long> create(@RequestBody FolderDTO folderDTO) {
        return ResponseEntity.ok(folderService.create(folderDTO));
    }

    @PutMapping(ApiConstants.id)
    public ResponseEntity<Long> update(@PathVariable Long id, @RequestBody FolderDTO folderDTO) {
        return ResponseEntity.ok(folderService.update(id, folderDTO));
    }

    @GetMapping(ApiConstants.tree)
    public ResponseEntity<List<FolderTreeDTO>> getTree() {
        return ResponseEntity.ok(folderService.getTree());
    }

}
