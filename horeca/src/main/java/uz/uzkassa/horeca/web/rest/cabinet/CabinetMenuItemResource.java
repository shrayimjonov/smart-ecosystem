package uz.uzkassa.horeca.web.rest.cabinet;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.horeca.constants.ApiConstants;
import uz.uzkassa.horeca.domain.product.BatchStatusUpdate;
import uz.uzkassa.horeca.dto.file.FileDTO;
import uz.uzkassa.horeca.dto.menu.*;
import uz.uzkassa.horeca.dto.soliq.ProductSearchDTO;
import uz.uzkassa.horeca.filters.MenuItemFilter;
import uz.uzkassa.horeca.service.MenuItemService;

import java.util.List;
import java.util.Map;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.cabinetMenuItemRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CabinetMenuItemResource {

    MenuItemService menuItemService;

    @PostMapping
    public ResponseEntity<Long> create(@RequestBody MenuItemDTO menuItemDTO) {
        return ResponseEntity.ok(menuItemService.create(menuItemDTO));
    }

    @PutMapping(ApiConstants.id)
    public ResponseEntity<Long> update(@PathVariable Long id, @RequestBody MenuItemDTO menuItemDTO) {
        return ResponseEntity.ok(menuItemService.update(id, menuItemDTO));
    }

    @GetMapping(ApiConstants.id)
    public ResponseEntity<MenuItemDetailDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(menuItemService.get(id));
    }

    @GetMapping
    public ResponseEntity<Page<MenuItemListDTO>> getList(@ParameterObject MenuItemFilter filter) {
        return ResponseEntity.ok(menuItemService.getList(filter));
    }

    @DeleteMapping(ApiConstants.id)
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        menuItemService.delete(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "/{id}/upload/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<FileDTO> uploadPhoto(@PathVariable Long id, @RequestParam("file") MultipartFile photo) {
        return ResponseEntity.ok(menuItemService.uploadPhoto(id, photo));
    }

    @PostMapping(value = "/{id}/upload/external/photo")
    public ResponseEntity<FileDTO> uploadPhoto(@PathVariable Long id, @RequestBody ExternalPhotoUploadDTO photo) {
        return ResponseEntity.ok(menuItemService.uploadExternalPhoto(id, photo));
    }

    @DeleteMapping("/{id}/photo")
    public ResponseEntity<Void> removePhoto(@PathVariable Long id) {
        menuItemService.removePhoto(id);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/favourite")
    public ResponseEntity<Void> favourite(@PathVariable Long id) {
        menuItemService.favorite(id);
        return ResponseEntity.ok().build();
    }


    @GetMapping("/search/by-params")
    public ResponseEntity<List<MenuItemListDTO>> searchByParams(@ParameterObject ProductSearchDTO productSearchDTO) {
        return ResponseEntity.ok(menuItemService.searchByParams(productSearchDTO));
    }

    @PatchMapping(ApiConstants.updateStatusBatch)
    public ResponseEntity<Map<Long, String>> updateStatusBatch(@RequestBody @Valid BatchStatusUpdate batchStatusUpdate) {
        return ResponseEntity.ok(menuItemService.updateStatusBatch(batchStatusUpdate));
    }

}
