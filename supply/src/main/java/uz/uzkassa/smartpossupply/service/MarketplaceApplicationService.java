package uz.uzkassa.smartpossupply.service;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.smartpossupply.domain.MarketplaceApplication;
import uz.uzkassa.smartpossupply.domain.product.Product;
import uz.uzkassa.smartpossupply.dto.base.ResultList;
import uz.uzkassa.smartpossupply.dto.marketplaceapplication.MarketplaceApplicationBatchDTO;
import uz.uzkassa.smartpossupply.dto.marketplaceapplication.MarketplaceApplicationDTO;
import uz.uzkassa.smartpossupply.dto.marketplaceapplication.MarketplaceApplicationDetailDTO;
import uz.uzkassa.smartpossupply.dto.marketplaceapplication.MarketplaceApplicationListDTO;
import uz.uzkassa.smartpossupply.dto.marketplaceapplication.MarketplaceApplicationPhotoDTO;
import uz.uzkassa.smartpossupply.dto.marketplaceapplication.MarketplaceApplicationStatisticDTO;
import uz.uzkassa.smartpossupply.dto.marketplaceapplication.MarketplaceApplicationStatusUpdateDTO;
import uz.uzkassa.smartpossupply.dto.sync.marketplace.MarketplaceApplicationPhotoSyncDTO;
import uz.uzkassa.smartpossupply.dto.sync.marketplace.MarketplaceApplicationSyncDTO;
import uz.uzkassa.smartpossupply.enums.*;
import uz.uzkassa.smartpossupply.filters.BaseFilter;
import uz.uzkassa.smartpossupply.integration.MarketplaceClient;
import uz.uzkassa.smartpossupply.rabbitmq.producer.RabbitMqMarketplaceApplicationProducer;
import uz.uzkassa.smartpossupply.repository.CategoryRepository;
import uz.uzkassa.smartpossupply.repository.FileUploadRepository;
import uz.uzkassa.smartpossupply.repository.MarketplaceApplicationRepository;
import uz.uzkassa.smartpossupply.repository.ProductRepository;
import uz.uzkassa.smartpossupply.validator.ValidationContext;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Service
@Transactional(readOnly = true)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class MarketplaceApplicationService extends BaseService {

    MarketplaceApplicationRepository marketplaceApplicationRepository;
    ProductRepository productRepository;
    CategoryRepository categoryRepository;
    ValidationContext validationContext;
    FileUploadService fileUploadService;
    MarketplaceClient marketplaceClient;
    FileUploadRepository fileUploadRepository;
    RabbitMqMarketplaceApplicationProducer rabbitMqMarketplaceApplicationProducer;
    CompanyService companyService;

    @Transactional
    public void create(MarketplaceApplicationBatchDTO marketplaceApplicationBatchDTO) {
        List<Product> products;
        if (!CollectionUtils.isEmpty(marketplaceApplicationBatchDTO.getProductIds())) {
            products = productRepository.findAllById(marketplaceApplicationBatchDTO.getProductIds());
        } else if (marketplaceApplicationBatchDTO.getCategoryId() != null) {
            List<Long> childIds = categoryRepository.findIdsByParentId(marketplaceApplicationBatchDTO.getCategoryId());
            childIds.add(marketplaceApplicationBatchDTO.getCategoryId());
            products = productRepository.findAllByCategoryIdInAndDeletedIsFalse(childIds);
        } else if (marketplaceApplicationBatchDTO.getBranchId() != null) {
            products = productRepository.findAllByBranchIdAndDeletedIsFalse(marketplaceApplicationBatchDTO.getBranchId());
        } else {
            throw validationService.badRequestExceptionThrow("One of branchId, categoryId, productIds is required").get();
        }
        List<MarketplaceApplication> marketplaceApplications = products.stream().map(product -> {
            MarketplaceApplication marketplaceApplication = marketplaceApplicationRepository.findFirstByProductId(product.getId()).orElseGet(MarketplaceApplication::new);
            marketplaceApplication.setProductId(product.getId());

            marketplaceApplication.setNameUz(product.getName());
            marketplaceApplication.setNameCyrillic(product.getName());
            marketplaceApplication.setNameRu(product.getName());

            marketplaceApplication.setPrice(product.getPrice());
            marketplaceApplication.setCatalogPrice(product.getPrice());
            marketplaceApplication.setBarcode(product.getBarcode());
            marketplaceApplication.setCatalogCode(product.getCatalogCode());
            marketplaceApplication.setCatalogName(product.getCatalogName());
            marketplaceApplication.setPackageCode(product.getPackageCode());
            marketplaceApplication.setPackageName(product.getPackageName());
            marketplaceApplication.setVat(product.getVat());
            marketplaceApplication.setUnitId(product.getUnitId());
            if (product.getDetail() != null) {
                marketplaceApplication.setDescriptionUz(product.getDetail().getDescription());
                marketplaceApplication.setDescriptionCyrillic(product.getDetail().getDescription());
                marketplaceApplication.setDescriptionRu(product.getDetail().getDescription());
                marketplaceApplication.setCountryId(product.getDetail().getProductionCountryId());
            }
            return marketplaceApplication;
        }).toList();
        if (!CollectionUtils.isEmpty(marketplaceApplications)) {
            marketplaceApplicationRepository.saveAll(marketplaceApplications);
        }
    }

    @Transactional
    public void updateStatus(Long id, MarketplaceApplicationStatusUpdateDTO marketplaceApplicationStatusUpdateDTO) {
        marketplaceApplicationRepository.findById(id).ifPresentOrElse(marketplaceApplication -> {
            marketplaceApplication.setStatus(marketplaceApplicationStatusUpdateDTO.getStatus());
            marketplaceApplication.setRejectedReason(marketplaceApplicationStatusUpdateDTO.getRejectedReason());
            marketplaceApplication.setAction(null);
            marketplaceApplicationRepository.save(marketplaceApplication);
        }, () -> validationService.entityNotFoundExceptionThrow(MarketplaceApplication.class.getSimpleName(), "id", id).get());

    }

    public Page<MarketplaceApplicationListDTO> getList(BaseFilter filter) {
        ResultList<MarketplaceApplication> resultList = marketplaceApplicationRepository.getResultList(filter);
        List<MarketplaceApplicationListDTO> result = resultList
            .getList()
            .stream()
            .map(marketplaceApplication -> {
                MarketplaceApplicationListDTO marketplaceApplicationListDTO = new MarketplaceApplicationListDTO();
                marketplaceApplicationListDTO.setId(marketplaceApplication.getId());
                marketplaceApplicationListDTO.setName(marketplaceApplication.getName());
                marketplaceApplicationListDTO.setBarcode(marketplaceApplication.getBarcode());
                marketplaceApplicationListDTO.setPrice(marketplaceApplication.getPrice());
                marketplaceApplicationListDTO.setCategoryName(marketplaceApplication.getCatalogName());
                marketplaceApplicationListDTO.setCreatedDate(marketplaceApplication.getCreatedDate());
                marketplaceApplicationListDTO.setStatus(marketplaceApplication.getStatus());
                return marketplaceApplicationListDTO;
            })
            .toList();
        return new PageImpl<>(result, filter.getSortedPageable(), resultList.getCount());
    }

    @Transactional
    public Long update(Long id, MarketplaceApplicationDTO marketplaceApplicationDTO, List<MultipartFile> photos) {
        if (!CollectionUtils.isEmpty(photos)) {
            fileUploadService.fileUpload(photos, id, EntityTypeEnum.MARKETPLACE_APPLICATION, true, true);
        }
        if (marketplaceApplicationDTO != null) {
            validationContext.validate(marketplaceApplicationDTO);
            MarketplaceApplication marketplaceApplication = marketplaceApplicationRepository.findById(id).orElseThrow(validationService.entityNotFoundExceptionThrow(MarketplaceApplication.class.getSimpleName(), "id", id));
            marketplaceApplication.setNameUz(marketplaceApplicationDTO.getNameUz());
            marketplaceApplication.setNameCyrillic(marketplaceApplicationDTO.getNameCyrillic());
            marketplaceApplication.setNameRu(marketplaceApplicationDTO.getNameRu());
            marketplaceApplication.setDescriptionUz(marketplaceApplicationDTO.getDescriptionUz());
            marketplaceApplication.setDescriptionCyrillic(marketplaceApplicationDTO.getDescriptionCyrillic());
            marketplaceApplication.setDescriptionRu(marketplaceApplicationDTO.getDescriptionRu());
            marketplaceApplication.setPrice(marketplaceApplicationDTO.getPrice());
            marketplaceApplication.setCountryId(marketplaceApplicationDTO.getCountryId());
            marketplaceApplication.setStatus(MarketplaceApplicationStatus.FILLED);
            marketplaceApplication.setAction(null);
            marketplaceApplicationRepository.save(marketplaceApplication);

            if (!CollectionUtils.isEmpty(marketplaceApplicationDTO.getDeletedPhotoIds())) {
                fileUploadService.deleteByIds(marketplaceApplicationDTO.getDeletedPhotoIds());
            }
        }
        return id;
    }

    public MarketplaceApplicationDetailDTO get(Long id) {
        return marketplaceApplicationRepository.getDetail(id).map(marketplaceApplication -> {
            MarketplaceApplicationDetailDTO marketplaceApplicationDetailDTO = new MarketplaceApplicationDetailDTO();
            marketplaceApplicationDetailDTO.setId(marketplaceApplication.getId());
            marketplaceApplicationDetailDTO.setNameUz(marketplaceApplication.getNameUz());
            marketplaceApplicationDetailDTO.setNameCyrillic(marketplaceApplication.getNameCyrillic());
            marketplaceApplicationDetailDTO.setNameRu(marketplaceApplication.getNameRu());
            marketplaceApplicationDetailDTO.setDescriptionUz(marketplaceApplication.getDescriptionUz());
            marketplaceApplicationDetailDTO.setDescriptionCyrillic(marketplaceApplication.getDescriptionCyrillic());
            marketplaceApplicationDetailDTO.setDescriptionRu(marketplaceApplication.getDescriptionRu());
            marketplaceApplicationDetailDTO.setBarcode(marketplaceApplication.getBarcode());
            marketplaceApplicationDetailDTO.setCatalogCode(marketplaceApplication.getCatalogCode());
            marketplaceApplicationDetailDTO.setCatalogName(marketplaceApplication.getCatalogName());
            marketplaceApplicationDetailDTO.setPackageCode(marketplaceApplication.getPackageCode());
            marketplaceApplicationDetailDTO.setPackageName(marketplaceApplication.getPackageName());
            marketplaceApplicationDetailDTO.setVat(marketplaceApplication.getVat());
            if (marketplaceApplication.getUnit() != null) {
                marketplaceApplicationDetailDTO.setUnit(marketplaceApplication.getUnit().toDTO());
            }
            marketplaceApplicationDetailDTO.setPrice(marketplaceApplication.getPrice());
            marketplaceApplicationDetailDTO.setCatalogPrice(marketplaceApplication.getProduct().getPrice());
            if (marketplaceApplication.getCountry() != null) {
                marketplaceApplicationDetailDTO.setCountry(marketplaceApplication.getCountry().toCommonDTO());
            }
            marketplaceApplicationDetailDTO.setRejectedReason(marketplaceApplication.getRejectedReason());
            marketplaceApplicationDetailDTO.setStatus(marketplaceApplication.getStatus());
            return marketplaceApplicationDetailDTO;
        }).orElseThrow(validationService.entityNotFoundExceptionThrow(MarketplaceApplication.class.getSimpleName(), "id", id));
    }

    public MarketplaceApplicationStatisticDTO getStatistics(BaseFilter filter) {
        return marketplaceApplicationRepository.getStatistics(filter);
    }

    @Transactional
    public void send(List<Long> ids) {
        if (!CollectionUtils.isEmpty(ids)) {
            ids.forEach(id -> sendQueue(id));
        }
    }

    @Transactional
    public void sendQueue(Long id) {
        MarketplaceApplication marketplaceApplication = marketplaceApplicationRepository.getDetail(id).orElseThrow(validationService.entityNotFoundExceptionThrow(MarketplaceApplication.class.getSimpleName(), "id", id));
        MarketplaceApplicationSyncDTO marketplaceApplicationSyncDTO = new MarketplaceApplicationSyncDTO();
        final Long companyId = marketplaceApplication.getProduct().getCompanyId();
        marketplaceApplicationSyncDTO.setCompanyId(companyId);
        marketplaceApplicationSyncDTO.setId(marketplaceApplication.getId());
        marketplaceApplicationSyncDTO.setNameUz(marketplaceApplication.getNameUz());
        marketplaceApplicationSyncDTO.setNameCyrillic(marketplaceApplication.getNameCyrillic());
        marketplaceApplicationSyncDTO.setNameRu(marketplaceApplication.getNameRu());
        marketplaceApplicationSyncDTO.setDescriptionUz(marketplaceApplication.getDescriptionUz());
        marketplaceApplicationSyncDTO.setDescriptionCyrillic(marketplaceApplication.getDescriptionCyrillic());
        marketplaceApplicationSyncDTO.setDescriptionRu(marketplaceApplication.getDescriptionRu());
        marketplaceApplicationSyncDTO.setBarcode(marketplaceApplication.getBarcode());
        marketplaceApplicationSyncDTO.setCatalogCode(marketplaceApplication.getCatalogCode());
        marketplaceApplicationSyncDTO.setCatalogName(marketplaceApplication.getCatalogName());
        marketplaceApplicationSyncDTO.setPackageCode(marketplaceApplication.getPackageCode());
        marketplaceApplicationSyncDTO.setPackageName(marketplaceApplication.getPackageName());
        marketplaceApplicationSyncDTO.setVat(marketplaceApplication.getVat());
        if (marketplaceApplication.getUnit() != null) {
            marketplaceApplicationSyncDTO.setMeasureId(marketplaceApplication.getUnit().getMeasureId());
        }
        marketplaceApplicationSyncDTO.setPrice(marketplaceApplication.getPrice());
        if (marketplaceApplication.getCountry() != null) {
            marketplaceApplicationSyncDTO.setCountryCode(marketplaceApplication.getCountry().getCode());
        }

        List<MarketplaceApplicationPhotoSyncDTO> photos = fileUploadRepository.findAllByEntityIdAndEntityTypeAndParentIsNullAndDeletedIsFalse(id, EntityTypeEnum.MARKETPLACE_APPLICATION).stream().map(photo -> {
            MarketplaceApplicationPhotoSyncDTO marketplaceApplicationPhotoDTO = new MarketplaceApplicationPhotoSyncDTO();
            marketplaceApplicationPhotoDTO.setId(photo.getId());
            marketplaceApplicationPhotoDTO.setName(photo.getName());
            marketplaceApplicationPhotoDTO.setUrl(photo.getUrl());
            marketplaceApplicationPhotoDTO.setContentType(photo.getContentType());
            marketplaceApplicationPhotoDTO.setSizeType(photo.getFileSizeType());
            List<MarketplaceApplicationPhotoSyncDTO> children = photo.getChildren().stream().map(child -> {
                MarketplaceApplicationPhotoSyncDTO marketplaceApplicationPhotoSyncDTO = new MarketplaceApplicationPhotoSyncDTO();
                marketplaceApplicationPhotoSyncDTO.setId(child.getId());
                marketplaceApplicationPhotoSyncDTO.setName(child.getName());
                marketplaceApplicationPhotoSyncDTO.setUrl(child.getUrl());
                marketplaceApplicationPhotoSyncDTO.setContentType(child.getContentType());
                marketplaceApplicationPhotoSyncDTO.setSizeType(child.getFileSizeType());
                return marketplaceApplicationPhotoSyncDTO;
            }).toList();
            marketplaceApplicationPhotoDTO.setChildren(children);
            return marketplaceApplicationPhotoDTO;
        }).toList();
        marketplaceApplicationSyncDTO.setPhotos(photos);
        Long marketplaceId = marketplaceClient.sendApplication(marketplaceApplicationSyncDTO);
        marketplaceApplication.setMarketplaceId(marketplaceId);
        marketplaceApplication.setStatus(MarketplaceApplicationStatus.IN_MODERATION);
        marketplaceApplicationRepository.save(marketplaceApplication);
    }

    public List<MarketplaceApplicationPhotoDTO> getPhotos(Long id) {
        return fileUploadRepository.findAllByEntityIdAndEntityTypeAndFileSizeTypeAndDeletedIsFalse(id, EntityTypeEnum.MARKETPLACE_APPLICATION, FileSizeType.ORIGINAL).stream().map(photo -> {
            MarketplaceApplicationPhotoDTO marketplaceApplicationPhotoDTO = new MarketplaceApplicationPhotoDTO();
            marketplaceApplicationPhotoDTO.setId(photo.getId());
            marketplaceApplicationPhotoDTO.setName(photo.getName());
            marketplaceApplicationPhotoDTO.setUrl(photo.getUrl());
            marketplaceApplicationPhotoDTO.setContentType(photo.getContentType());
            marketplaceApplicationPhotoDTO.setSizeType(photo.getFileSizeType());
            return marketplaceApplicationPhotoDTO;
        }).toList();
    }

    @Transactional
    public void remove(Long id) {
        marketplaceApplicationRepository.findById(id).ifPresentOrElse(marketplaceApplication -> {
            marketplaceClient.removeApplication(id);
            marketplaceApplication.setStatus(MarketplaceApplicationStatus.REMOVED);
            marketplaceApplication.setAction(MarketplaceApplicationAction.REMOVE);
        }, () -> validationService.entityNotFoundExceptionThrow(MarketplaceApplication.class.getSimpleName(), "id", id).get());
    }

    @Transactional
    public void returnBack(Long id) {
        marketplaceApplicationRepository.findById(id).ifPresentOrElse(marketplaceApplication -> {
            marketplaceClient.returnBackApplication(id);
            marketplaceApplication.setStatus(MarketplaceApplicationStatus.IN_MODERATION);
            marketplaceApplication.setAction(MarketplaceApplicationAction.RETURN);
        }, () -> validationService.entityNotFoundExceptionThrow(MarketplaceApplication.class.getSimpleName(), "id", id).get());
    }
}
