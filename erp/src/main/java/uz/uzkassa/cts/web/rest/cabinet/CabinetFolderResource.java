package uz.uzkassa.cts.web.rest.cabinet;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.cts.constants.ApiConstants;
import uz.uzkassa.cts.dto.document.FolderTreeDTO;
import uz.uzkassa.cts.service.FolderService;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.cabinetFolderRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CabinetFolderResource {

    FolderService folderService;

    @GetMapping(ApiConstants.tree)
    public ResponseEntity<List<FolderTreeDTO>> getTree() {
        return ResponseEntity.ok(folderService.getTree());
    }

}
