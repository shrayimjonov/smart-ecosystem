package uz.uzkassa.cts.service;

import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.cts.domain.inventory.ProductPhoto;
import uz.uzkassa.cts.dto.productphoto.admin.ProductPhotoListAdminDTO;

import java.util.List;

public interface ProductPhotoService extends BaseService<ProductPhoto, Long> {

    void uploadMain(Long productId, MultipartFile file);

    void upload(Long productId, List<MultipartFile> files);

    List<ProductPhotoListAdminDTO> getListByProductId(Long productId);

    void updateMain(Long id);
}
