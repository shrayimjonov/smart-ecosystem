package uz.uzkassa.cts.web.rest.admin;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.cts.constants.ApiConstants;
import uz.uzkassa.cts.dto.productphoto.admin.ProductPhotoListAdminDTO;
import uz.uzkassa.cts.service.ProductPhotoService;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.adminProductPhotoRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AdminProductPhotoResource {

    ProductPhotoService productPhotoService;

    @PostMapping(value = ApiConstants.upload + "/main", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> uploadMain(@RequestParam Long productId, @RequestPart MultipartFile file) {
        productPhotoService.uploadMain(productId, file);
        return ResponseEntity.ok().build();
    }

    @PostMapping(value = ApiConstants.upload, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> upload(@RequestParam Long productId, @RequestPart List<MultipartFile> files) {
        productPhotoService.upload(productId, files);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<ProductPhotoListAdminDTO>> getListByProductId(@PathVariable Long productId) {
        return ResponseEntity.ok(productPhotoService.getListByProductId(productId));
    }

    @PatchMapping("{id}/main")
    public ResponseEntity<Void> updateMain(@PathVariable Long id) {
        productPhotoService.updateMain(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping(ApiConstants.id)
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        productPhotoService.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
