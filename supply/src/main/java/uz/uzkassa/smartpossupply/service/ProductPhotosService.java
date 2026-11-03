package uz.uzkassa.smartpossupply.service;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.smartpossupply.constants.Constants;
import uz.uzkassa.smartpossupply.domain.FileUpload;
import uz.uzkassa.smartpossupply.domain.product.Product;
import uz.uzkassa.smartpossupply.domain.product.ProductPhoto;
import uz.uzkassa.smartpossupply.dto.file.FileDTO;
import uz.uzkassa.smartpossupply.dto.payload.ProductPhotoSyncPayload;
import uz.uzkassa.smartpossupply.dto.product.ProductPhotoDTO;
import uz.uzkassa.smartpossupply.enums.EntityTypeEnum;
import uz.uzkassa.smartpossupply.rabbitmq.producer.RabbitMqProductPhotoProducer;
import uz.uzkassa.smartpossupply.repository.FileUploadRepository;
import uz.uzkassa.smartpossupply.repository.ProductPhotoRepository;
import uz.uzkassa.smartpossupply.repository.ProductRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static lombok.AccessLevel.PRIVATE;

@Service
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Slf4j
public class ProductPhotosService extends BaseService implements Constants {

    ProductRepository productRepository;
    ProductPhotoRepository productPhotoRepository;
    RabbitMqProductPhotoProducer rabbitMqProductPhotoProducer;
    FileUploadService fileUploadService;
    FileUploadRepository fileUploadRepository;

    @Transactional
    public void transfer(Set<ProductPhoto> productPhotos, Long productId){
        productPhotoRepository.deleteAllByProductId(productId);
        productPhotos.forEach(fromProductPhoto -> {
            FileUpload fromPhoto = fromProductPhoto.getPhoto();
            FileUpload toPhoto = new FileUpload();
            toPhoto.setName(fromPhoto.getName());
            toPhoto.setObjectName(fromPhoto.getObjectName());
            toPhoto.setUrl(fromPhoto.getUrl());
            toPhoto.setPath(fromPhoto.getPath());
            toPhoto.setKey(fromPhoto.getKey());
            toPhoto.setExtension(fromPhoto.getExtension());
            toPhoto.setContentType(fromPhoto.getContentType());
            toPhoto.setFileId(fromPhoto.getFileId());
            toPhoto.setSize(fromPhoto.getSize());
            toPhoto.setFileSizeType(fromPhoto.getFileSizeType());
            toPhoto.setPublic(fromPhoto.isPublic());
            toPhoto.setEntityId(productId);
            toPhoto.setEntityType(EntityTypeEnum.PRODUCT);
            fileUploadRepository.save(toPhoto);

            ProductPhoto productPhoto = new ProductPhoto();
            productPhoto.setProductId(productId);
            productPhoto.setPhotoId(toPhoto.getId());
            productPhoto.setMain(fromProductPhoto.isMain());
            productPhotoRepository.save(productPhoto);
        });
    }

    @Transactional
    public void syncProductPhotos(ProductPhotoSyncPayload productPhotoSyncPayload) {
        this.syncProductPhotos(productPhotoSyncPayload.getProductId(), productPhotoSyncPayload.getBranchIds());
    }

    public void syncProductPhotos(Long productId, List<Long> branchIds) {
        productRepository.findById(productId).ifPresent(product -> {
            Map<Long, ProductPhoto> productPhotoMap = new HashMap<>();
            List<ProductPhoto> productPhotoList = new ArrayList<>();
            for (Product copyProduct : productRepository.findAllByBranchIdInAndBarcodeAndBarcodeIsNotNullAndDeletedIsFalse(branchIds, product.getBarcode())) {
                productPhotoMap.putAll(copyProduct.getPhotos().stream().collect(Collectors.toMap(ProductPhoto::getId, p -> p)));
                for (ProductPhoto photo : product.getPhotos()) {
                    ProductPhoto productPhoto = productPhotoRepository.findFirstByProductIdAndPhotoId(copyProduct.getId(), photo.getPhotoId()).orElseGet(ProductPhoto::new);
                    if (productPhoto.getPhotoId() != null) {
                        productPhotoMap.remove(productPhoto.getId());
                    } else {
                        productPhoto.setProductId(copyProduct.getId());
                        productPhoto.setPhotoId(photo.getPhotoId());
                    }
                    productPhoto.setMain(photo.isMain());
                    productPhotoList.add(productPhoto);
                }
            }
            productPhotoRepository.saveAll(productPhotoList);
            if (!productPhotoMap.isEmpty()) {
                productPhotoRepository.deleteAllById(productPhotoMap.keySet());
            }
        });
    }

    @Transactional
    public void assignPhotos(Product product, ProductPhotoDTO productPhotoDTO) {
        if (!CollectionUtils.isEmpty(productPhotoDTO.getDeletedPhotoIds())) {
            productPhotoRepository.deleteAllByProductIdAndPhotoIdIn(product.getId(), productPhotoDTO.getDeletedPhotoIds());
        }
        if (!CollectionUtils.isEmpty(productPhotoDTO.getAddedPhotoIds())) {
            List<ProductPhoto> productPhotos = productPhotoDTO.getAddedPhotoIds().stream().map(photoId -> {
                ProductPhoto productPhoto = productPhotoRepository.findFirstByProductIdAndPhotoId(product.getId(), photoId).orElseGet(ProductPhoto::new);
                productPhoto.setProductId(product.getId());
                productPhoto.setPhotoId(photoId);
                productPhoto.setMain(photoId.equals(productPhotoDTO.getMainPhotoId()));
                return productPhoto;
            }).toList();
            productPhotoRepository.saveAll(productPhotos);
        }
        if (productPhotoDTO.isApplyToAllTheSameProducts()) {
            List<Long> branchIds = productPhotoDTO.getBranchIds();
            if (CollectionUtils.isEmpty(branchIds)) {
                branchIds = branchRepository.getBranchIdByCompanyId(getCurrentCompanyId());
            }
            rabbitMqProductPhotoProducer.sync(new ProductPhotoSyncPayload(branchIds, product.getId()));
        }
    }

    @Transactional
    public void assignPhotos(Long id, ProductPhotoDTO productPhotoDTO) {
        productRepository.findById(id).ifPresent(product -> this.assignPhotos(product, productPhotoDTO));
    }

    @Transactional
    public List<FileDTO> uploadPhoto(List<MultipartFile> files) {
        return fileUploadService.fileUpload(files, null, EntityTypeEnum.PRODUCT, true, true);
    }
}
