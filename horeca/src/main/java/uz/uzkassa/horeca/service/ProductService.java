package uz.uzkassa.horeca.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.horeca.domain.FileUpload;
import uz.uzkassa.horeca.domain.product.BatchStatusUpdate;
import uz.uzkassa.horeca.domain.product.Product;
import uz.uzkassa.horeca.domain.techcard.TechCard;
import uz.uzkassa.horeca.dto.base.ResultList;
import uz.uzkassa.horeca.dto.file.FileDTO;
import uz.uzkassa.horeca.dto.product.ProductDTO;
import uz.uzkassa.horeca.dto.product.ProductDetailDTO;
import uz.uzkassa.horeca.dto.product.ProductListDTO;
import uz.uzkassa.horeca.dto.product.ProductLookUpDTO;
import uz.uzkassa.horeca.dto.soliq.MxikDTO;
import uz.uzkassa.horeca.dto.soliq.ProductSearchDTO;
import uz.uzkassa.horeca.enums.CommonStatus;
import uz.uzkassa.horeca.enums.ProductOrigin;
import uz.uzkassa.horeca.filters.ProductFilter;
import uz.uzkassa.horeca.integration.TasnifClient;
import uz.uzkassa.horeca.interfaces.CrudAdvanced;
import uz.uzkassa.horeca.mappers.FileUploadMapper;
import uz.uzkassa.horeca.repository.*;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@Slf4j
public class ProductService extends BaseService implements CrudAdvanced<Long, ProductDTO, ProductDetailDTO, ProductListDTO, ProductFilter, CommonStatus, ProductLookUpDTO> {

    ProductRepository productRepository;
    AmazonS3Service amazonS3Service;
    FileUploadMapper fileUploadMapper;
    CategoryRepository categoryRepository;
    TasnifClient tasnifClient;
    MenuItemRepository menuItemRepository;
    DepartmentRepository departmentRepository;
    TechCardRepository techCardRepository;

    @Override
    public void validate(ProductDTO productDTO) {
        if (StringUtils.isEmpty(productDTO.getName())) {
            throw badRequestExceptionThrow("Name is required").get();
        }
        if (StringUtils.isEmpty(productDTO.getPackageName())) {
            throw badRequestExceptionThrow("Package name is required").get();
        }
        if (StringUtils.isEmpty(productDTO.getPackageCode())) {
            throw badRequestExceptionThrow("Package code is required").get();
        }
        if (productDTO.getCategoryId() == null) {
            throw badRequestExceptionThrow("Category is required").get();
        }
        if (ProductOrigin.SERVICE != productDTO.getOrigin() && productDTO.getDepartmentId() == null) {
            throw badRequestExceptionThrow("Department is required").get();
        }
//        if (productDTO.getUnitId() == null) {
//            throw badRequestExceptionThrow("Unit is required").get();
//        }
        if (productDTO.getVat() == null) {
            throw badRequestExceptionThrow("Vat is required").get();
        }

        Long branchId = getCurrentBranchId(productDTO.getBranchId());
        if (productDTO.getId() == null && productRepository.existsByBranchIdAndNameIgnoreCaseAndDeletedIsFalse(branchId, productDTO.getName())) {
            throw conflictExceptionThrow("Товар с таким названием " + productDTO.getName() + " уже существует.").get();
        }
        if (productDTO.getId() != null && productRepository.existsByBranchIdAndNameIgnoreCaseAndIdNotAndDeletedIsFalse(branchId, productDTO.getName(), productDTO.getId())) {
            throw conflictExceptionThrow("Товар с таким названием " + productDTO.getName() + " уже существует.").get();
        }
    }

    @Transactional
    public Long create(ProductDTO productDTO) {
        this.validate(productDTO);
        Product product = null;
        if (productDTO.getUniqueId() != null) {
            product = productRepository.findFirstByUniqueIdAndDeletedIsFalseAndBranchId(productDTO.getUniqueId(), productDTO.getBranchId()).orElse(null);
        }
        if (product == null) {
            product = new Product();
        }
        product.setUniqueId(Optional.ofNullable(productDTO.getUniqueId()).orElse(UUID.randomUUID()));
        product.setName(productDTO.getName());
        product.setSku(productDTO.getSku());
        product.setBarcode(productDTO.getBarcode());
        product.setCatalogCode(productDTO.getCatalogCode());
        product.setCatalogName(productDTO.getCatalogName());
        product.setPackageName(productDTO.getPackageName());
        product.setPackageCode(productDTO.getPackageCode());
        product.setDescription(productDTO.getDescription());
        product.setVat(productDTO.getVat());
        product.setPrice(productDTO.getPrice());
        product.setWeight(productDTO.getWeight());
        product.setHasMark(productDTO.isHasMark());
        product.setCategoryId(productDTO.getCategoryId());
        product.setDepartmentId(productDTO.getDepartmentId());
        product.setBranchId(getCurrentBranchId(productDTO.getBranchId()));
        product.setUnitId(productDTO.getUnitId());
        product.setOrigin(productDTO.getOrigin());
        product.setProductType(productDTO.getProductType());
        product.setHasTechCard(productDTO.isHasTechCard());
        product.setStatus(product.getUnitId() != null ? CommonStatus.ACTIVE : CommonStatus.IN_ACTIVE);
        productRepository.saveAndFlush(product);
        return product.getId();
    }

    @Override
    @Transactional
    public Long update(Long id, ProductDTO productDTO) {
        productDTO.setId(id);
        this.validate(productDTO);
        return productRepository.findById(id).map(product -> {
            product.setName(productDTO.getName());
            product.setSku(productDTO.getSku());
            product.setBarcode(productDTO.getBarcode());
            product.setCatalogCode(productDTO.getCatalogCode());
            product.setCatalogName(productDTO.getCatalogName());
            product.setPackageName(productDTO.getPackageName());
            product.setPackageCode(productDTO.getPackageCode());
            product.setDescription(productDTO.getDescription());
            product.setVat(productDTO.getVat());
            product.setPrice(productDTO.getPrice());
            product.setWeight(productDTO.getWeight());
            product.setHasMark(productDTO.isHasMark());
            product.setCategoryId(productDTO.getCategoryId());
            product.setDepartmentId(productDTO.getDepartmentId());
            product.setBranchId(getCurrentBranchId(productDTO.getBranchId()));
            product.setUnitId(productDTO.getUnitId());
            product.setOrigin(productDTO.getOrigin());
            product.setProductType(productDTO.getProductType());
            product.setHasTechCard(productDTO.isHasTechCard());
            product.setStatus(productDTO.getUnitId() != null ? CommonStatus.ACTIVE : CommonStatus.IN_ACTIVE);
            productRepository.save(product);
            return id;
        }).orElseThrow(notFoundExceptionThrow(Product.class.getSimpleName(), "id", id));
    }

    @Override
    public ProductDetailDTO get(Long id) {
        return productRepository.findById(id).map(product -> {
            ProductDetailDTO productDetailDTO = new ProductDetailDTO();
            productDetailDTO.setId(product.getId());
            productDetailDTO.setName(product.getName());
            productDetailDTO.setSku(product.getSku());
            productDetailDTO.setBarcode(product.getBarcode());
            productDetailDTO.setCategory(product.getCategory().toLookUpDTO());
            productDetailDTO.setCatalogName(product.getCatalogName());
            productDetailDTO.setCatalogCode(product.getCatalogCode());
            productDetailDTO.setPackageName(product.getPackageName());
            productDetailDTO.setPackageCode(product.getPackageCode());
            productDetailDTO.setVat(product.getVat());
            productDetailDTO.setPrice(product.getPrice());
            productDetailDTO.setWeight(product.getWeight());
            productDetailDTO.setHasMark(product.isHasMark());
            productDetailDTO.setDescription(product.getDescription());
            productDetailDTO.setBranch(product.getBranch().toCommonDTO());
            productDetailDTO.setOrigin(product.getOrigin());
            productDetailDTO.setProductType(product.getProductType());
            productDetailDTO.setHasTechCard(product.isHasTechCard());
            productDetailDTO.setStatus(product.getStatus());
            if (!product.isHasTechCard()) {
                if (product.getUnitId() != null) {
                    productDetailDTO.setUnit(product.getUnit().toCommonDTO());
                }
            }
            if (product.getDepartment() != null) {
                productDetailDTO.setDepartment(product.getDepartment().toCommonDTO());
            }
            if (product.getPhotoId() != null) {
                productDetailDTO.setPhoto(product.getPhoto().toFileDTO());
            }
            return productDetailDTO;
        }).orElseThrow(notFoundExceptionThrow(Product.class.getSimpleName(), "id", id));
    }

    @Override
    public Page<ProductListDTO> getList(ProductFilter filter) {
        ResultList<Product> resultList = productRepository.getResultList(filter);

        List<ProductListDTO> result = resultList.getList().stream().map(product -> {
            ProductListDTO productListDTO = new ProductListDTO();
            productListDTO.setId(product.getId());
            productListDTO.setName(product.getName());
            productListDTO.setSku(product.getSku());
            productListDTO.setCategory(product.getCategory().toLookUpDTO());
            productListDTO.setPrice(product.getPrice());
            productListDTO.setStatus(product.getStatus());
            productListDTO.setHasMark(product.isHasMark());
            productListDTO.setHasTechCard(product.isHasTechCard());
            if (!product.isHasTechCard() && product.getUnitId() != null) {
                productListDTO.setUnit(product.getUnit().toCommonDTO());
            }
            productListDTO.setBarcode(product.getBarcode());
            productListDTO.setCatalogCode(product.getCatalogCode());
            if (product.getPhotoId() != null) {
                productListDTO.setPhoto(product.getPhoto().toFileDTO());
            }
            return productListDTO;
        }).collect(Collectors.toList());
        return new PageImpl<>(result, filter.getSortedPageable(), resultList.getCount());
    }

    @Override
    @Transactional
    public void delete(Long id) {
        productRepository.deleteById(id);
        menuItemRepository.updateMenuItemsByProductId(id);
    }

    @Override
    public List<ProductLookUpDTO> lookUp(ProductFilter filter) {
        return productRepository.getResultList(filter).getList().stream().map(this::toProductLookUpDTO).collect(Collectors.toList());
    }

    @Override
    public void updateStatus(Long id, CommonStatus status) {
        productRepository
                .findById(id)
                .map(product -> {
                    product.setStatus(status);
                    productRepository.save(product);
                    return product;
                })
                .orElseThrow(notFoundExceptionThrow(Product.class.getSimpleName(), "id", id));
    }

    @Transactional
    public Map<Long, String> updateStatusBatch(BatchStatusUpdate batchStatusUpdate) {
        List<Product> allById = productRepository.findAllById(batchStatusUpdate.getIds());
        if (allById.isEmpty()) notFoundExceptionThrow(Product.class.getSimpleName()).get();
        final CommonStatus status = batchStatusUpdate.getStatus();
        Map<Long, String> errors = new HashMap<>();
        for (Product product : allById) {
            if (status == product.getStatus()) continue;

            if (status == CommonStatus.ACTIVE) {
                if (product.isHasTechCard()) {
                    if (techCardRepository.findAllByProductIdAndDeletedIsFalse(product.getId()).isEmpty()) {
                        errors.put(product.getId(), "Tech cards are empty");
                    }
                } else if (product.getUnitId() == null) {
                    errors.put(product.getId(), "Unit is empty");
                }
            }
            if (!errors.containsKey(product.getId())) {
                product.setStatus(status);
                productRepository.save(product);
            }
        }
        return errors;
    }

    @Transactional
    public void updatePrice(Long id, BigDecimal price) {
        productRepository.findById(id).map(product -> {
            product.setPrice(price);
            productRepository.save(product);
            return product;
        }).orElseThrow(notFoundExceptionThrow(Product.class.getSimpleName(), "id", id));
    }

    @Transactional
    public void favorite(Long id) {
        productRepository.findById(id).map(product -> {
            product.setFavourite(!product.isFavourite());
            productRepository.save(product);
            return product;
        }).orElseThrow(notFoundExceptionThrow(Product.class.getSimpleName(), "id", id));
    }

    @Transactional
    public FileDTO uploadPhoto(Long id, MultipartFile photo) {
        return productRepository.findById(id).map(product -> {
            if (product.getPhotoId() != null) {
                amazonS3Service.delete(product.getPhotoId());
            }
            FileUpload fileUpload = amazonS3Service.uploadProductPhoto(photo, product.getId());
            product.setPhotoId(fileUpload.getId());
            product.setPhoto(fileUpload);
            productRepository.save(product);
            return fileUploadMapper.toFileDTO(fileUpload);
        }).orElseThrow(notFoundExceptionThrow(Product.class.getSimpleName(), "id", id));
    }

    @Transactional
    public void removePhoto(Long id) {
        productRepository.findById(id).map(product -> {
            FileUpload photo = product.getPhoto();
            if (photo != null) {
                product.setPhoto(null);
                product.setPhotoId(null);
                productRepository.save(product);
                amazonS3Service.delete(photo.getId());
            }
            return product;
        }).orElseThrow(notFoundExceptionThrow(Product.class.getSimpleName(), "id", id));
    }

    public List<MxikDTO> searchFromTasnifByParams(ProductSearchDTO searchDTO) {
        return tasnifClient.searchByParams(searchDTO);
    }

    @Transactional
    public void syncProduct(Long fromBranchId, Long toBranchId) {
        List<Product> products = productRepository.findAllByBranchIdAndDeletedIsFalse(fromBranchId);
        if (!CollectionUtils.isEmpty(products)) {
            for (Product product : products) {
                ProductDTO productDTO = new ProductDTO();
                if (product.getCategory().getUniqueId() != null) {
                    categoryRepository.findFirstByUniqueIdAndDeletedIsFalseAndBranchId(product.getCategory().getUniqueId(), toBranchId).ifPresent(category -> productDTO.setCategoryId(category.getId()));
                }
                if (product.getDepartment().getUniqueId() != null) {
                    departmentRepository.findFirstByUniqueIdAndBranchIdAndDeletedIsFalse(product.getCategory().getUniqueId(), toBranchId).ifPresent(department -> productDTO.setDepartmentId(department.getId()));
                }
                productDTO.setName(product.getName());
                productDTO.setSku(product.getSku());
                productDTO.setBarcode(product.getBarcode());
                productDTO.setCatalogCode(product.getCatalogCode());
                productDTO.setCatalogName(product.getCatalogName());
                productDTO.setPackageName(product.getPackageName());
                productDTO.setPackageCode(product.getPackageCode());
                productDTO.setDescription(product.getDescription());
                productDTO.setVat(product.getVat());
                productDTO.setPrice(product.getPrice());
                productDTO.setWeight(product.getWeight());
                productDTO.setHasMark(product.isHasMark());
                productDTO.setBranchId(toBranchId);
                productDTO.setUnitId(product.getUnitId());
                productDTO.setOrigin(product.getOrigin());
                productDTO.setProductType(product.getProductType());
                productDTO.setUniqueId(product.getUniqueId());
                try {
                    this.create(productDTO);
                } catch (Exception e) {
                    log.error("Product error: {}", e.getMessage());
                }
            }
        }
    }
}
