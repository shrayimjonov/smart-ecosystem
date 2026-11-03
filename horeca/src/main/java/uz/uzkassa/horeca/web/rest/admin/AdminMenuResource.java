package uz.uzkassa.horeca.web.rest.admin;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.horeca.constants.ApiConstants;
import uz.uzkassa.horeca.dto.base.CommonDTO;
import uz.uzkassa.horeca.dto.file.FileDTO;
import uz.uzkassa.horeca.dto.menu.MenuDTO;
import uz.uzkassa.horeca.dto.menu.MenuDetailDTO;
import uz.uzkassa.horeca.dto.menu.MenuListDTO;
import uz.uzkassa.horeca.dto.menu.MenuTreeDTO;
import uz.uzkassa.horeca.filters.BaseFilter;
import uz.uzkassa.horeca.service.MenuService;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.adminMenuRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AdminMenuResource {

    MenuService menuService;

    @PostMapping
    public ResponseEntity<Long> create(@RequestBody MenuDTO menuDTO) {
        return ResponseEntity.ok(menuService.create(menuDTO));
    }

    @PutMapping(ApiConstants.id)
    public ResponseEntity<Long> update(@PathVariable Long id, @RequestBody MenuDTO menuDTO) {
        return ResponseEntity.ok(menuService.update(id, menuDTO));
    }

    @GetMapping(ApiConstants.id)
    public ResponseEntity<MenuDetailDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(menuService.get(id));
    }

    @GetMapping
    public ResponseEntity<Page<MenuListDTO>> getList(@ParameterObject BaseFilter filter) {
        return ResponseEntity.ok(menuService.getList(filter));
    }

    @DeleteMapping(ApiConstants.id)
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        menuService.delete(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping(ApiConstants.lookUp)
    public ResponseEntity<List<CommonDTO>> lookUp(@ParameterObject BaseFilter filter) {
        return ResponseEntity.ok(menuService.lookUp(filter));
    }

    @GetMapping(ApiConstants.tree)
    public ResponseEntity<List<MenuTreeDTO>> treeList(@ParameterObject BaseFilter filter) {
        return ResponseEntity.ok(menuService.treeList(filter));
    }

    @GetMapping(ApiConstants.current)
    public ResponseEntity<List<MenuTreeDTO>> currentActiveMenu(@ParameterObject BaseFilter filter) {
        return ResponseEntity.ok(menuService.currentActiveMenu(filter));
    }

    @PostMapping(value = "/{id}/upload/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<FileDTO> uploadPhoto(@PathVariable Long id, @RequestParam("file") MultipartFile photo) {
        return ResponseEntity.ok(menuService.uploadPhoto(id, photo));
    }

    @DeleteMapping("/{id}/photo")
    public ResponseEntity<Void> removePhoto(@PathVariable Long id) {
        menuService.removePhoto(id);
        return ResponseEntity.ok().build();
    }
}
