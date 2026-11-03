package uz.uzkassa.cts.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.cts.domain.FileUpload;
import uz.uzkassa.cts.domain.inventory.ProductPhoto;
import uz.uzkassa.cts.dto.productphoto.admin.ProductPhotoListAdminDTO;
import uz.uzkassa.cts.enums.DocumentType;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.repository.FileUploadRepository;
import uz.uzkassa.cts.repository.ProductPhotoRepository;
import uz.uzkassa.cts.service.FileUploadService;
import uz.uzkassa.cts.service.ProductPhotoService;

import java.util.List;

@Transactional(readOnly = true)
@Service
public class ProductPhotoServiceImpl extends BaseServiceImpl<ProductPhoto, Long> implements ProductPhotoService {

    private final ProductPhotoRepository productPhotoRepository;
    private final FileUploadService fileUploadService;
    private final FileUploadRepository fileUploadRepository;

    public ProductPhotoServiceImpl(ProductPhotoRepository productPhotoRepository, FileUploadService fileUploadService, FileUploadRepository fileUploadRepository) {
        super(productPhotoRepository);
        this.productPhotoRepository = productPhotoRepository;
        this.fileUploadService = fileUploadService;
        this.fileUploadRepository = fileUploadRepository;
    }

    @Override
    @Transactional
    public void uploadMain(Long productId, MultipartFile file) {
        if (productPhotoRepository.existsByProductIdAndMainIsTrueAndDeletedIsFalse(productId)) {
            throw validationService.entityAlreadyExistExceptionThrow(MAIN_PHOTO).get();
        }
        FileUpload photo = fileUploadService.upload(file);
        ProductPhoto productPhoto = new ProductPhoto();
        productPhoto.setProductId(productId);
        productPhoto.setMain(true);
        productPhoto.setPhotoId(photo.getId());
        productPhotoRepository.save(productPhoto);

        photo.setEntityId(productPhoto.getId());
        photo.setEntityType(EntityTypeEnum.PRODUCT_PHOTO);
        photo.setDocumentType(DocumentType.PRODUCT_PHOTO);
        fileUploadRepository.save(photo);
    }

    @Override
    @Transactional
    public void upload(Long productId, List<MultipartFile> files) {
        if (files.size() > 10) {
            throw validationService.badRequestExceptionThrow(CANNOT_UPLOAD_MORE_THAN_AMOUNT_PHOTOS, String.valueOf(10)).get();
        }
        for (MultipartFile file : files) {
            FileUpload photo = fileUploadService.upload(file);
            ProductPhoto productPhoto = new ProductPhoto();
            productPhoto.setProductId(productId);
            productPhoto.setMain(false);
            productPhoto.setPhotoId(photo.getId());
            productPhotoRepository.save(productPhoto);

            photo.setEntityId(productPhoto.getId());
            photo.setEntityType(EntityTypeEnum.PRODUCT_PHOTO);
            photo.setDocumentType(DocumentType.PRODUCT_PHOTO);
            fileUploadRepository.save(photo);
        }
    }

    @Override
    public List<ProductPhotoListAdminDTO> getListByProductId(Long productId) {
        return productPhotoRepository.findAllByProductIdAndDeletedIsFalse(productId)
                .stream()
                .map(productPhoto -> {
                    ProductPhotoListAdminDTO productPhotoListAdminDTO = new ProductPhotoListAdminDTO();
                    productPhotoListAdminDTO.setId(productPhoto.getId());
                    productPhotoListAdminDTO.setMain(productPhoto.isMain());
                    productPhotoListAdminDTO.setPhoto(productPhoto.getPhoto().toFileDTO());
                    return productPhotoListAdminDTO;
                }).toList();
    }

    @Override
    @Transactional
    public void updateMain(Long id) {
        ProductPhoto productPhoto = this.findById(id);
        productPhoto.setMain(true);
        productPhotoRepository.save(productPhoto);
        productPhotoRepository.update(productPhoto.getId(), productPhoto.getProductId(), false);
    }
}
