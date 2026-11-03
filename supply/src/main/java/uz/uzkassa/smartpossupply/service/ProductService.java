package uz.uzkassa.smartpossupply.service;

import org.springframework.dao.CannotAcquireLockException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import uz.uzkassa.smartpossupply.constants.MessageConstants;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import uz.uzkassa.smartpossupply.constants.Constants;
import uz.uzkassa.smartpossupply.domain.Branch;
import uz.uzkassa.smartpossupply.domain.FileUpload;
import uz.uzkassa.smartpossupply.domain.Unit;
import uz.uzkassa.smartpossupply.domain.category.Category;
import uz.uzkassa.smartpossupply.domain.product.Product;
import uz.uzkassa.smartpossupply.domain.product.ProductDetail;
import uz.uzkassa.smartpossupply.domain.warehouse.WarehouseProduct;
import uz.uzkassa.smartpossupply.dto.aic.AicProductLookUpDTO;
import uz.uzkassa.smartpossupply.dto.base.ResultList;
import uz.uzkassa.smartpossupply.dto.category.CategoryEnvironmentDTO;
import uz.uzkassa.smartpossupply.dto.markorder.MarkOrderProductLookUpDTO;
import uz.uzkassa.smartpossupply.dto.product.ProductBarcodeDTO;
import uz.uzkassa.smartpossupply.dto.product.ProductByCategoryListDTO;
import uz.uzkassa.smartpossupply.dto.product.ProductCategoryDTO;
import uz.uzkassa.smartpossupply.dto.product.ProductCommonDTO;
import uz.uzkassa.smartpossupply.dto.product.ProductDTO;
import uz.uzkassa.smartpossupply.dto.product.ProductDetailMobileDTO;
import uz.uzkassa.smartpossupply.dto.product.ProductFullDetailDTO;
import uz.uzkassa.smartpossupply.dto.product.ProductListDTO;
import uz.uzkassa.smartpossupply.dto.product.ProductListForOfflineDTO;
import uz.uzkassa.smartpossupply.dto.product.ProductListMobileDTO;
import uz.uzkassa.smartpossupply.dto.product.ProductLookUpDTO;
import uz.uzkassa.smartpossupply.dto.product.ProductLookUpWithWarehouseQtyDTO;
import uz.uzkassa.smartpossupply.dto.product.ProductPhotoListDTO;
import uz.uzkassa.smartpossupply.dto.product.ProductResidueDTO;
import uz.uzkassa.smartpossupply.dto.product.TopProductDTO;
import uz.uzkassa.smartpossupply.dto.product.WebKassaProductListDTO;
import uz.uzkassa.smartpossupply.dto.productdetail.ProductDetailDTO;
import uz.uzkassa.smartpossupply.dto.stocktransfer.StockTransferProductListDTO;
import uz.uzkassa.smartpossupply.enums.CommonStatus;
import uz.uzkassa.smartpossupply.enums.EnvironmentType;
import uz.uzkassa.smartpossupply.enums.GenericSettingsType;
import uz.uzkassa.smartpossupply.enums.ProductType;
import uz.uzkassa.smartpossupply.enums.SortOrderType;
import uz.uzkassa.smartpossupply.enums.TargetAudience;
import uz.uzkassa.smartpossupply.filters.BaseFilter;
import uz.uzkassa.smartpossupply.filters.ProductFilter;
import uz.uzkassa.smartpossupply.repository.CategoryRepository;
import uz.uzkassa.smartpossupply.repository.PriceListItemRepository;
import uz.uzkassa.smartpossupply.repository.ProductDetailRepository;
import uz.uzkassa.smartpossupply.repository.ProductRepository;
import uz.uzkassa.smartpossupply.repository.StockProductRepository;
import uz.uzkassa.smartpossupply.repository.WarehouseProductRepository;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import uz.uzkassa.smartpossupply.validator.ValidationContext;

import static lombok.AccessLevel.PRIVATE;

@Service
@Transactional(readOnly = true)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Slf4j
public class ProductService extends BaseService implements Constants {

    ProductRepository productRepository;
    WarehouseProductRepository warehouseProductRepository;
    StockProductRepository stockProductRepository;
    CategoryRepository categoryRepository;
    ProductPhotosService productPhotosService;
    ProductDetailRepository productDetailRepository;
    PriceListItemRepository priceListItemRepository;
    ValidationContext validationContext;

    private void validate(Product product, ProductDTO productDTO) {
        if (!StringUtils.hasText(productDTO.getVatBarcode())) {
            throw validationService.badRequestExceptionThrow("Требуется ИКПУ продукта").get();
        }
        if (productDTO.getId() == null) {
            if (StringUtils.hasText(productDTO.getBarcode()) && productRepository.existsByBarcodeAndBranchIdAndDeletedIsFalse(productDTO.getBarcode(), productDTO.getBranchId())) {
                throw validationService.badRequestExceptionThrow(localizationService.localize(MessageConstants.ERROR), localizationService.localize(MessageConstants.ENTITY_ALREADY_EXIST_BY_PARAM, new Object[]{localizationService.localize(Product.class.getSimpleName()), localizationService.localize(MessageConstants.BARCODE), productDTO.getBarcode()})).get();
            }
            if (StringUtils.hasText(productDTO.getVatBarcode())) {
                if (productRepository.existsByBarcodeAndVatBarcodeAndBranchIdAndDeletedIsFalse(productDTO.getBarcode(), productDTO.getVatBarcode(), productDTO.getBranchId())) {
                    throw validationService.badRequestExceptionThrow(localizationService.localize(MessageConstants.ERROR), localizationService.localize(MessageConstants.ENTITY_ALREADY_EXIST_BY_DOUBLE_PARAM, new Object[]{
                        localizationService.localize(Product.class.getSimpleName()), localizationService.localize(MessageConstants.BARCODE), productDTO.getBarcode()
                        , localizationService.localize(MessageConstants.VAT_BARCODE), productDTO.getVatBarcode()
                    })).get();
                }
            }
        } else {
            if (StringUtils.hasText(productDTO.getVatBarcode())) {
                if (productRepository.existsByBarcodeAndVatBarcodeAndBranchIdAndIdNotAndDeletedIsFalse(productDTO.getBarcode(), productDTO.getVatBarcode(), productDTO.getBranchId(), productDTO.getId())) {
                    throw validationService.badRequestExceptionThrow(localizationService.localize(MessageConstants.ERROR), localizationService.localize(MessageConstants.ENTITY_ALREADY_EXIST_BY_DOUBLE_PARAM, new Object[]{
                        localizationService.localize(Product.class.getSimpleName()), localizationService.localize(MessageConstants.BARCODE), productDTO.getBarcode()
                        , localizationService.localize(MessageConstants.VAT_BARCODE), productDTO.getVatBarcode()
                    })).get();
                }
            }
            if (productRepository.existsByBarcodeAndBranchIdAndIdNotAndDeletedIsFalse(productDTO.getBarcode(), productDTO.getBranchId(), productDTO.getId())) {
                throw validationService.badRequestExceptionThrow(localizationService.localize(MessageConstants.ERROR), localizationService.localize(MessageConstants.ENTITY_ALREADY_EXIST_BY_PARAM, new Object[]{localizationService.localize(Product.class.getSimpleName()), localizationService.localize(MessageConstants.BARCODE), productDTO.getBarcode()})).get();
            }
            if (!Objects.equals(product != null ? product.getBarcode() : null, productDTO.getBarcode())) {
                if (stockProductRepository.checkProductInStock(productDTO.getId())) {
                    throw validationService.badRequestExceptionThrow(localizationService.localize(MessageConstants.UPDATE_BARCODE_NOT_ALLOWED)).get();
                }
            }
        }
        if (productDTO.getProductPhoto() != null && (!CollectionUtils.isEmpty(productDTO.getProductPhoto().getAddedPhotoIds()) && productDTO.getProductPhoto().getMainPhotoId() == null)) {
            throw validationService.badRequestExceptionThrow("Main photo is required").get();
        }
        if (productDTO.getProductType() != null && (ProductType.BeerBlock.equals(productDTO.getProductType()) || ProductType.CigaretteBlock.equals(productDTO.getProductType()))) {
            if (productDTO.getQtyInPackage() == null) {
                throw validationService.badRequestExceptionThrow("Необходимо количество в упаковке", "Quantity in package is required").get();
            }
        }
    }

    private Product createEntity(ProductDTO productDTO) {
        Product product = new Product();
        product.setName(Optional.ofNullable(productDTO.getName()).map(String::trim).orElse(""));
        product.setShortName(Optional.ofNullable(Optional.ofNullable(productDTO.getShortName()).orElseGet(productDTO::getName)).map(String::trim).orElse(""));
        product.setBarcode(productDTO.getBarcode());
        product.setProductType(productDTO.getProductType());
        product.setDispenseType(productDTO.getDispenseType());
        product.setUnitId(getUnit().getId());
        product.setVat(productDTO.getVat());
        product.setPurchasePrice(productDTO.getPurchasePrice());
        product.setPrice(productDTO.getPrice());
        product.setForeignPrice(productDTO.getForeignPrice());
        product.setExchangeRate(productDTO.getExchangeRate());
        product.setCurrencyType(productDTO.getCurrencyType());
        product.setExciseRate(productDTO.getExciseRate());
        product.setExciseSum(productDTO.getExciseSum());
        product.setFavourite(productDTO.isFavourite());
        product.setHasMark(productDTO.isHasMark());
        if (CollectionUtils.isNotEmpty(productDTO.getCategoryIds())) {
            List<Category> categories = categoryRepository.findAllByIdInAndDeletedIsFalse(productDTO.getCategoryIds());
            product.setCategoryId(categories.stream().filter(category -> EnvironmentType.WEB == category.getEnvironmentType()).map(Category::getId).findFirst().orElse(null));
            product.setCategories(new HashSet<>(categories));
        }
        product.setCompanyId(Optional.ofNullable(productDTO.getCompanyId()).orElse(getCurrentCompanyId()));
        product.setBranchId(productDTO.getBranchId());
        product.setPackageName(productDTO.getPackageName());
        product.setPackageCode(productDTO.getPackageCode());
        product.setQtyInPackage(productDTO.getQtyInPackage());
        product.setQtyInBox(productDTO.getQtyInBox());
        product.setQtyInPallet(productDTO.getQtyInPallet());
        product.setTnved(productDTO.getTnved());
        product.setCatalogName(productDTO.getCatalogName());
        product.setVatBarcode(productDTO.getVatBarcode());
        product.setCapacity(productDTO.getCapacity());
        product.setCompanyCommittentId(productDTO.getCompanyCommittentId());
        product.setOrigin(productDTO.getOrigin());
        product.setParentId(productDTO.getParentId());
        product.setBoxBarcode(productDTO.getBoxBarcode());
        product.setBlockBarcode(productDTO.getBlockBarcode());
        product.setStatus(CommonStatus.ACTIVE);
        product.setWeightBrutto(productDTO.getWeightBrutto());
        product.setWeightNetto(productDTO.getWeightNetto());
        product.setAdditionalUnit();
        product.setBlockProducts(productDTO.getBlockProducts());
        product.setExpiredInMonth(productDTO.getExpiredInMonth());
        product.setBrand(productDTO.getBrand());
        if (product.getUnitId() == null) {
            Unit unit = getUnit();
            product.setUnitId(unit.getId());
        }
        productRepository.saveAndFlush(product);
        if (productDTO.getDetail() != null) {
            ProductDetail productDetail = new ProductDetail();
            ProductDetailDTO detailDTO = productDTO.getDetail();

            productDetail.setProductId(product.getId());
            productDetail.setWidth(detailDTO.getWidth());
            productDetail.setHeight(detailDTO.getHeight());
            productDetail.setLength(detailDTO.getLength());
            productDetail.setPackageWidth(detailDTO.getPackageWidth());
            productDetail.setPackageHeight(detailDTO.getPackageHeight());
            productDetail.setPackageLength(detailDTO.getPackageLength());
            productDetail.setManufacturer(detailDTO.getManufacturer());
            productDetail.setProductionCountryId(detailDTO.getProductionCountryId());
            productDetail.setArticleNumber(detailDTO.getArticleNumber());
            productDetail.setDescription(detailDTO.getDescription());
            productDetail.setMaterial(detailDTO.getMaterial());
            productDetail.setColour(detailDTO.getColour());
            productDetail.setModel(detailDTO.getModel());
            productDetail.setExpireDate(detailDTO.getExpireDate());
            productDetail.setProductionDate(detailDTO.getProductionDate());
            productDetail.setComposition(detailDTO.getComposition());
            productDetail.setRecipe(detailDTO.getRecipe());
            productDetail.setPurpose(detailDTO.getPurpose());
            productDetail.setAgeFrom(detailDTO.getAgeFrom());
            productDetail.setAgeTo(detailDTO.getAgeTo());
            if (StringUtils.hasText(detailDTO.getTargetAudience())) {
                productDetail.setTargetAudience(TargetAudience.get(detailDTO.getTargetAudience()));
            }
            productDetail.setSeoKeywords(detailDTO.getSeoKeywords());
            productDetail.setSeasonality(detailDTO.getSeasonality());
            productDetailRepository.save(productDetail);

        }

        if (productDTO.getParentId() != null) {
            productRepository.updateProductParent(product.getParentId(), product.getId());
        }
        if (productDTO.getProductPhoto() != null) {
            productPhotosService.assignPhotos(product, productDTO.getProductPhoto());
        }
        return product;
    }

    @Transactional
    public Long create(ProductDTO productDTO) {
        validationContext.validate(productDTO);
        this.validate(null, productDTO);
        return this.createEntity(productDTO).getId();
    }

    @Transactional
    public Product createAndReturn(ProductDTO productDTO) {
        this.validate(null, productDTO);
        return this.createEntity(productDTO);
    }

    public ProductFullDetailDTO get(Long id) {
        return productRepository.findById(id).map(product -> {
            ProductFullDetailDTO productFullDetailDTO = new ProductFullDetailDTO();
            productFullDetailDTO.setId(product.getId());
            productFullDetailDTO.setName(product.getName());
            productFullDetailDTO.setShortName(product.getShortName());
            productFullDetailDTO.setBarcode(product.getBarcode());
            productFullDetailDTO.setProductType(product.getProductType());
            productFullDetailDTO.setDispenseType(product.getDispenseType());
            productFullDetailDTO.setCategories(product.getCategories().stream().map(category -> {
                CategoryEnvironmentDTO categoryEnvironmentDTO = new CategoryEnvironmentDTO();
                categoryEnvironmentDTO.setId(category.getId());
                categoryEnvironmentDTO.setName(category.getName());
                categoryEnvironmentDTO.setEnvironmentType(category.getEnvironmentType());
                return categoryEnvironmentDTO;
            }).toList());
            productFullDetailDTO.setBranch(product.getBranch().toCommonDTO());
            productFullDetailDTO.setVat(product.getVat());
            productFullDetailDTO.setUnit(product.getUnit().toDTO());
            productFullDetailDTO.setPurchasePrice(product.getPurchasePrice());
            productFullDetailDTO.setPrice(product.getPrice());
            productFullDetailDTO.setForeignPrice(product.getForeignPrice());
            productFullDetailDTO.setExchangeRate(product.getExchangeRate());
            productFullDetailDTO.setCurrencyType(product.getCurrencyType());
            productFullDetailDTO.setExciseRate(product.getExciseRate());
            productFullDetailDTO.setExciseSum(product.getExciseSum());
            productFullDetailDTO.setFavourite(product.isFavourite());
            productFullDetailDTO.setHasMark(product.isHasMark());
            productFullDetailDTO.setVatBarcode(product.getVatBarcode());
            productFullDetailDTO.setCatalogName(product.getCatalogName());
            productFullDetailDTO.setPackageName(product.getPackageName());
            productFullDetailDTO.setPackageCode(product.getPackageCode());
            productFullDetailDTO.setQtyInPackage(product.getQtyInPackage());
            productFullDetailDTO.setQtyInBox(product.getQtyInBox());
            productFullDetailDTO.setQtyInPallet(product.getQtyInPallet());
            productFullDetailDTO.setCapacity(product.getCapacity());
            productFullDetailDTO.setTnved(product.getTnved());
            productFullDetailDTO.setOrigin(product.getOrigin());
            productFullDetailDTO.setBoxBarcode(product.getBoxBarcode());
            productFullDetailDTO.setBlockBarcode(product.getBlockBarcode());

            ProductDetail detail = product.getDetail();
            if (detail != null) {
                ProductDetailDTO detailDTO = new ProductDetailDTO();
                detailDTO.setWidth(detail.getWidth());
                detailDTO.setHeight(detail.getHeight());
                detailDTO.setLength(detail.getLength());
                detailDTO.setPackageWidth(detail.getPackageWidth());
                detailDTO.setPackageHeight(detail.getPackageHeight());
                detailDTO.setPackageLength(detail.getPackageLength());
                detailDTO.setManufacturer(detail.getManufacturer());
                detailDTO.setArticleNumber(detail.getArticleNumber());
                detailDTO.setDescription(detail.getDescription());
                detailDTO.setMaterial(detail.getMaterial());
                detailDTO.setColour(detail.getColour());
                detailDTO.setModel(detail.getModel());
                detailDTO.setExpireDate(detail.getExpireDate());
                detailDTO.setProductionDate(detail.getProductionDate());
                detailDTO.setComposition(detail.getComposition());
                detailDTO.setRecipe(detail.getRecipe());
                detailDTO.setPurpose(detail.getPurpose());
                detailDTO.setAgeFrom(detail.getAgeFrom());
                detailDTO.setAgeTo(detail.getAgeTo());
                detailDTO.setTargetAudience(String.valueOf(detail.getTargetAudience()));
                detailDTO.setSeoKeywords(detail.getSeoKeywords());
                detailDTO.setSeasonality(detail.getSeasonality());

                if (detail.getProductionCountryId() != null) {
                    detailDTO.setProductionCountryId(detail.getProductionCountryId());
                    detailDTO.setProductionCountryName(detail.getProductionCountry().getName());
                }

                productFullDetailDTO.setDetail(detailDTO);
            }
            productFullDetailDTO.setBrand(product.getBrand());

            if (product.getParentId() != null) {
                productFullDetailDTO.setParent(product.getParent().toProductCommonDTO());
                if (!StringUtils.hasText(productFullDetailDTO.getBlockBarcode())) {
                    productFullDetailDTO.setBlockBarcode(product.getParent().getBarcode());
                }
            }
            if (!CollectionUtils.isEmpty(product.getPhotos())) {
                product.getPhotos().forEach(productPhoto -> {
                    ProductPhotoListDTO productPhotoListDTO = productPhoto.toDTO();
                    if (!CollectionUtils.isEmpty(productPhoto.getPhoto().getChildren())) {
                        for (FileUpload childPhoto : productPhoto.getPhoto().getChildren()) {
                            productPhotoListDTO.addChild(new ProductPhotoListDTO(childPhoto.getId(), childPhoto.getUrl(), productPhoto.isMain(), childPhoto.getFileSizeType()));
                        }
                    }
                    productFullDetailDTO.addPhoto(productPhotoListDTO);
                });
            }
            if (product.getCompanyCommittentId() != null) {
                productFullDetailDTO.setCompanyCommittent(product.getCompanyCommittent().toLookUpDTO());
            }
            productFullDetailDTO.setStatus(product.getStatus());
            productFullDetailDTO.setWeightBrutto(product.getWeightBrutto());
            productFullDetailDTO.setWeightNetto(product.getWeightNetto());
            productFullDetailDTO.setBlockProducts(product.getBlockProducts());
            productFullDetailDTO.setExpiredInMonth(product.getExpiredInMonth());
            return productFullDetailDTO;
        }).orElseThrow(validationService.entityNotFoundExceptionThrow(Product.class.getSimpleName(), "id", id));
    }

    public ProductFullDetailDTO getByBranchOrClone(Product product, Long branchId) {
        Optional<Product> productOptional = productRepository.findFirstByBarcodeAndBranchIdAndDeletedIsFalse(product.getBarcode(), branchId);
        ProductFullDetailDTO productFullDetailDTO;
        if (productOptional.isPresent()) {
            productFullDetailDTO = getProductDetailDTO(productOptional.get());
            productFullDetailDTO.setPrice(productOptional.get().getPrice());
            productFullDetailDTO.setId(productOptional.get().getId());
        } else {
            productFullDetailDTO = getProductDetailDTO(product);
            productFullDetailDTO.setPrice(BigDecimal.ZERO);
            Optional<Branch> newBranch = branchRepository.findById(branchId);
            newBranch.ifPresent(branch -> productFullDetailDTO.setBranch(branch.toCommonDTO()));
        }
        return productFullDetailDTO;
    }

    private ProductFullDetailDTO getProductDetailDTO(Product product) {
        ProductFullDetailDTO productFullDetailDTO = new ProductFullDetailDTO();
        productFullDetailDTO.setName(product.getName());
        productFullDetailDTO.setShortName(product.getShortName());
        productFullDetailDTO.setBarcode(product.getBarcode());
        productFullDetailDTO.setProductType(product.getProductType());
        productFullDetailDTO.setCategories(product.getCategories().stream().map(category -> {
            CategoryEnvironmentDTO categoryEnvironmentDTO = new CategoryEnvironmentDTO();
            categoryEnvironmentDTO.setId(category.getId());
            categoryEnvironmentDTO.setName(category.getName());
            categoryEnvironmentDTO.setEnvironmentType(category.getEnvironmentType());
            return categoryEnvironmentDTO;
        }).toList());
        productFullDetailDTO.setBranch(product.getBranch().toCommonDTO());
        productFullDetailDTO.setVat(product.getVat());
        productFullDetailDTO.setUnit(product.getUnit().toDTO());
        productFullDetailDTO.setPurchasePrice(product.getPurchasePrice());
        productFullDetailDTO.setPrice(product.getPrice());
        productFullDetailDTO.setExciseRate(product.getExciseRate());
        productFullDetailDTO.setExciseSum(product.getExciseSum());
        productFullDetailDTO.setFavourite(product.isFavourite());
        productFullDetailDTO.setHasMark(product.isHasMark());
        productFullDetailDTO.setVatBarcode(product.getVatBarcode());
        productFullDetailDTO.setCatalogName(product.getCatalogName());
        productFullDetailDTO.setPackageName(product.getPackageName());
        productFullDetailDTO.setPackageCode(product.getPackageCode());
        productFullDetailDTO.setQtyInPackage(product.getQtyInPackage());
        productFullDetailDTO.setCapacity(product.getCapacity());
        productFullDetailDTO.setTnved(product.getTnved());
        productFullDetailDTO.setOrigin(product.getOrigin());
        productFullDetailDTO.setExpiredInMonth(product.getExpiredInMonth());
        if (!CollectionUtils.isEmpty(product.getPhotos())) {
            product.getPhotos().forEach(productPhoto -> {
                ProductPhotoListDTO productPhotoListDTO = productPhoto.toDTO();
                if (!CollectionUtils.isEmpty(productPhoto.getPhoto().getChildren())) {
                    for (FileUpload childPhoto : productPhoto.getPhoto().getChildren()) {
                        productPhotoListDTO.addChild(new ProductPhotoListDTO(childPhoto.getId(), childPhoto.getUrl(), productPhoto.isMain(), childPhoto.getFileSizeType()));
                    }
                }
                productFullDetailDTO.addPhoto(productPhotoListDTO);
            });
        }
        if (product.getCompanyCommittentId() != null) {
            productFullDetailDTO.setCompanyCommittent(product.getCompanyCommittent().toLookUpDTO());
        }
        productFullDetailDTO.setBlockProducts(product.getBlockProducts());
        return productFullDetailDTO;
    }

    public ProductDetailMobileDTO getForMobile(Long id) {
        return productRepository.findById(id).map(product -> {
            ProductDetailMobileDTO productDetailDTO = new ProductDetailMobileDTO();
            productDetailDTO.setId(product.getId());
            productDetailDTO.setName(product.getName());
            productDetailDTO.setBarcode(product.getBarcode());
            productDetailDTO.setProductType(product.getProductType());
            productDetailDTO.setVat(product.getVat());
            productDetailDTO.setUnit(product.getUnit().toDTO());
            productDetailDTO.setPrice(product.getPrice());
            productDetailDTO.setExciseRate(product.getExciseRate());
            productDetailDTO.setExciseSum(product.getExciseSum());

            productDetailDTO.setVatBarcode(product.getVatBarcode());
            productDetailDTO.setCatalogName(product.getCatalogName());
            productDetailDTO.setPackageName(product.getPackageName());
            productDetailDTO.setPackageCode(product.getPackageCode());
            productDetailDTO.setQtyInPackage(product.getQtyInPackage());
            productDetailDTO.setQtyInBox(product.getQtyInBox());
            productDetailDTO.setQtyInPallet(product.getQtyInPallet());
            productDetailDTO.setCapacity(product.getCapacity());
            productDetailDTO.setFavorite(product.isFavourite());
            if (!CollectionUtils.isEmpty(product.getPhotos())) {
                product.getPhotos().forEach(productPhoto -> {
                    ProductPhotoListDTO productPhotoListDTO = productPhoto.toDTO();
                    if (!CollectionUtils.isEmpty(productPhoto.getPhoto().getChildren())) {
                        for (FileUpload childPhoto : productPhoto.getPhoto().getChildren()) {
                            productPhotoListDTO.addChild(new ProductPhotoListDTO(childPhoto.getId(), childPhoto.getUrl(), productPhoto.isMain(), childPhoto.getFileSizeType()));
                        }
                    }
                    productDetailDTO.addPhoto(productPhotoListDTO);
                });
            }
            return productDetailDTO;
        }).orElseThrow(validationService.entityNotFoundExceptionThrow(Product.class.getSimpleName(), "id", id));
    }

    @Transactional
    public Long update(Long id, ProductDTO productDTO) {
        validationContext.validate(productDTO);
        return productRepository.findById(id).map(product -> {
            productDTO.setId(id);
            this.validate(product, productDTO);
            product.setName(productDTO.getName());
            product.setShortName(Optional.ofNullable(productDTO.getShortName()).orElseGet(productDTO::getName).trim());
            product.setBarcode(productDTO.getBarcode());
            product.setProductType(productDTO.getProductType());
            product.setDispenseType(productDTO.getDispenseType());
            if (CollectionUtils.isNotEmpty(productDTO.getCategoryIds())) {
                List<Category> categories = categoryRepository.findAllByIdInAndDeletedIsFalse(productDTO.getCategoryIds());
                product.getCategories().clear();
                product.setCategoryId(categories.stream().filter(category -> EnvironmentType.WEB == category.getEnvironmentType()).map(Category::getId).findFirst().orElse(null));
                product.setCategories(new HashSet<>(categories));
            } else {
                product.setCategoryId(null);
                product.setCategories(new HashSet<>());
            }
            product.setVat(productDTO.getVat());
            product.setPurchasePrice(productDTO.getPurchasePrice());
            product.setPrice(productDTO.getPrice());
            product.setForeignPrice(productDTO.getForeignPrice());
            product.setExchangeRate(productDTO.getExchangeRate());
            product.setCurrencyType(productDTO.getCurrencyType());
            product.setExciseRate(productDTO.getExciseRate());
            product.setExciseSum(productDTO.getExciseSum());
            product.setVatBarcode(productDTO.getVatBarcode());
            product.setCatalogName(productDTO.getCatalogName());
            product.setPackageName(productDTO.getPackageName());
            product.setPackageCode(productDTO.getPackageCode());
            product.setQtyInPackage(productDTO.getQtyInPackage());
            product.setQtyInBox(productDTO.getQtyInBox());
            product.setQtyInPallet(productDTO.getQtyInPallet());
            product.setCapacity(productDTO.getCapacity());
            product.setFavourite(productDTO.isFavourite());
            product.setHasMark(productDTO.isHasMark());
            product.setTnved(productDTO.getTnved());
            product.setCompanyCommittentId(productDTO.getCompanyCommittentId());
            product.setOrigin(productDTO.getOrigin());
            product.setParentId(productDTO.getParentId());
            product.setBoxBarcode(productDTO.getBoxBarcode());
            product.setBlockBarcode(productDTO.getBlockBarcode());
            product.setWeightBrutto(productDTO.getWeightBrutto());
            product.setWeightNetto(productDTO.getWeightNetto());
            product.setAdditionalUnit();
            product.setBlockProducts(productDTO.getBlockProducts());
            product.setExpiredInMonth(productDTO.getExpiredInMonth());
            productRepository.save(product);

            if (productDTO.getDetail() != null) {
                productDetailRepository.findFirstByProductId(product.getId()).ifPresent(productDetail -> {
                    ProductDetailDTO detailDTO = productDTO.getDetail();

                    productDetail.setProductId(product.getId());
                    productDetail.setWidth(detailDTO.getWidth());
                    productDetail.setHeight(detailDTO.getHeight());
                    productDetail.setLength(detailDTO.getLength());
                    productDetail.setPackageWidth(detailDTO.getPackageWidth());
                    productDetail.setPackageHeight(detailDTO.getPackageHeight());
                    productDetail.setPackageLength(detailDTO.getPackageLength());
                    productDetail.setManufacturer(detailDTO.getManufacturer());
                    productDetail.setProductionCountryId(detailDTO.getProductionCountryId());
                    productDetail.setArticleNumber(detailDTO.getArticleNumber());
                    productDetail.setDescription(detailDTO.getDescription());
                    productDetail.setMaterial(detailDTO.getMaterial());
                    productDetail.setColour(detailDTO.getColour());
                    productDetail.setModel(detailDTO.getModel());
                    productDetail.setExpireDate(detailDTO.getExpireDate());
                    productDetail.setProductionDate(detailDTO.getProductionDate());
                    productDetail.setComposition(detailDTO.getComposition());
                    productDetail.setRecipe(detailDTO.getRecipe());
                    productDetail.setPurpose(detailDTO.getPurpose());
                    productDetail.setAgeFrom(detailDTO.getAgeFrom());
                    productDetail.setAgeTo(detailDTO.getAgeTo());
                    if (StringUtils.hasText(detailDTO.getTargetAudience())) {
                        productDetail.setTargetAudience(TargetAudience.valueOf(detailDTO.getTargetAudience()));
                    }
                    productDetail.setSeoKeywords(detailDTO.getSeoKeywords());
                    productDetail.setSeasonality(detailDTO.getSeasonality());
                    productDetailRepository.save(productDetail);
                });
            }
            if (productDTO.getParentId() != null) {
                productRepository.updateProductParent(product.getParentId(), product.getId());
            }
            if (productDTO.getProductPhoto() != null) {
                productPhotosService.assignPhotos(product, productDTO.getProductPhoto());
            }
            return product.getId();
        }).orElseThrow(validationService.entityNotFoundExceptionThrow(Product.class.getSimpleName(), "id", id));
    }

    @Transactional
    public Long updateBarcode(Long id, ProductBarcodeDTO productBarcodeDTO) {
        return productRepository.findById(id).map(product -> {
            product.setBarcode(productBarcodeDTO.getBarcode());
            productRepository.save(product);
            return product.getId();
        }).orElseThrow(validationService.entityNotFoundExceptionThrow(Product.class.getSimpleName(), "id", id));
    }

    public Page<ProductListDTO> getList(ProductFilter filter) {
        filter.setEnabledSorting(companySettingsRepository.existsByCompanyIdAndSettings_SettingsType(getCurrentCompanyId(), GenericSettingsType.SORTING_PRODUCTS));
        filter.setSortOrderType(SortOrderType.LIST.getCode());
        ResultList<Product> resultList = productRepository.getResultList(filter);
        List<ProductListDTO> result = resultList.getList().stream().map(product -> {
            ProductListDTO productListDTO = new ProductListDTO();
            productListDTO.setId(product.getId());
            productListDTO.setName(product.getName());
            productListDTO.setShortName(product.getShortName());
            productListDTO.setBarcode(product.getBarcode());
            productListDTO.setVatBarcode(product.getVatBarcode());
            productListDTO.setCategories(product.getCategories().stream().map(Category::toCommonDTO).toList());
            productListDTO.setBranch(product.getBranch().toCommonDTO());
            productListDTO.setVat(product.getVat());
            productListDTO.setUnit(product.getUnit().toDTO());
            productListDTO.setPrice(product.getPrice());
            productListDTO.setForeignPrice(product.getForeignPrice());
            productListDTO.setExchangeRate(product.getExchangeRate());
            productListDTO.setCurrencyType(product.getCurrencyType());
            productListDTO.setExciseRate(product.getExciseRate());
            productListDTO.setExciseSum(product.getExciseSum());
            productListDTO.setCapacity(product.getCapacity());
            productListDTO.setHasMark(product.isHasMark());
            productListDTO.setStatus(product.getStatus());
            productListDTO.setBlockProducts(product.getBlockProducts());
            return productListDTO;
        }).toList();
        return new PageImpl<>(result, filter.getSortedPageable(), resultList.getCount());
    }

    public Page<ProductByCategoryListDTO> getListByCategory(ProductFilter filter) {
        filter.setEnabledSorting(companySettingsRepository.existsByCompanyIdAndSettings_SettingsType(getCurrentCompanyId(), GenericSettingsType.SORTING_PRODUCTS));
        filter.setSortOrderType(SortOrderType.TREE.getCode());
        ResultList<Product> resultList = productRepository.getResultList(filter);

        List<ProductByCategoryListDTO> result = resultList.getList().stream().map(product -> {
            ProductByCategoryListDTO productByCategoryListDTO = new ProductByCategoryListDTO();
            productByCategoryListDTO.setId(product.getId());
            productByCategoryListDTO.setName(product.getName());
            productByCategoryListDTO.setPurchasePrice(product.getPurchasePrice());
            productByCategoryListDTO.setPrice(product.getPrice());
            productByCategoryListDTO.setCapacity(product.getCapacity());
            return productByCategoryListDTO;
        }).toList();
        return new PageImpl<>(result, filter.getSortedPageable(), resultList.getCount());
    }

    public Page<ProductListMobileDTO> getListForMobile(ProductFilter filter) {
        filter.setEnabledSorting(companySettingsRepository.existsByCompanyIdAndSettings_SettingsType(getCurrentCompanyId(), GenericSettingsType.SORTING_PRODUCTS));
        filter.setSortOrderType(filter.getSortOrderType() == null ? SortOrderType.TREE.getCode() : filter.getSortOrderType());
        filter.setEnvironmentType(filter.getEnvironmentType() == null ? EnvironmentType.WEB.getCode() : filter.getEnvironmentType());
        ResultList<Product> resultList = productRepository.getResultList(filter);
        List<ProductListMobileDTO> result = resultList.getList().stream().map(product -> {
            ProductListMobileDTO productListDTO = new ProductListMobileDTO();
            productListDTO.setId(product.getId());
            productListDTO.setName(product.getName());
            productListDTO.setPrice(product.getPrice());
            if (product.getUnit() != null) {
                productListDTO.setUnit(product.getUnit().toDTO());
            }
            if (!CollectionUtils.isEmpty(product.getPhotos())) {
                product.getPhotos().forEach(productPhoto -> {
                    ProductPhotoListDTO productPhotoListDTO = productPhoto.toDTO();
                    if (!CollectionUtils.isEmpty(productPhoto.getPhoto().getChildren())) {
                        for (FileUpload childPhoto : productPhoto.getPhoto().getChildren()) {
                            productPhotoListDTO.addChild(new ProductPhotoListDTO(childPhoto.getId(), childPhoto.getUrl(), productPhoto.isMain(), childPhoto.getFileSizeType()));
                        }
                    }
                    productListDTO.addPhoto(productPhotoListDTO);
                });
            }
            return productListDTO;
        }).toList();
        return new PageImpl<>(result, filter.getSortedPageable(), resultList.getCount());
    }

    public Page<ProductListForOfflineDTO> getListForMobileOffline(ProductFilter filter) {
        ResultList<ProductListForOfflineDTO> resultList = productRepository.getResultListForOffline(filter);
        return new PageImpl<>(resultList.getList(), filter.getSortedPageable(), resultList.getCount());
    }

    public List<ProductLookUpDTO> lookUp(ProductFilter filter) {
        filter.setEnabledSorting(companySettingsRepository.existsByCompanyIdAndSettings_SettingsType(getCurrentCompanyId(), GenericSettingsType.SORTING_PRODUCTS));
        filter.setProductTypes(ProductType.getBlockProductTypes());
        filter.setSortOrderType(SortOrderType.LIST.getCode());
        return productRepository
            .getResultList(filter)
            .getList()
            .stream()
            .map(product -> {
                ProductLookUpDTO productLookUpDTO = new ProductLookUpDTO();
                productLookUpDTO.setId(product.getId());
                productLookUpDTO.setName(product.getName());
                productLookUpDTO.setVat(product.getVat());
                productLookUpDTO.setUnit(product.getUnit().toDTO());
                productLookUpDTO.setPurchasePrice(product.getPurchasePrice());
                productLookUpDTO.setPrice(product.getPrice());
                productLookUpDTO.setForeignPrice(product.getForeignPrice());
                productLookUpDTO.setExchangeRate(product.getExchangeRate());
                productLookUpDTO.setCurrencyType(product.getCurrencyType());
                productLookUpDTO.setExciseRate(product.getExciseRate());
                productLookUpDTO.setExciseSum(product.getExciseSum());
                productLookUpDTO.setBarcode(product.getBarcode());
                productLookUpDTO.setVatBarcode(product.getVatBarcode());
                productLookUpDTO.setCatalogName(product.getCatalogName());
                productLookUpDTO.setPackageName(product.getPackageName());
                productLookUpDTO.setPackageCode(product.getPackageCode());
                productLookUpDTO.setCapacity(product.getCapacity());
                productLookUpDTO.setProductType(product.getProductType());
                productLookUpDTO.setHasMark(product.isHasMark());
                productLookUpDTO.setOrigin(product.getOrigin());
                if (product.getCompanyCommittentId() != null) {
                    productLookUpDTO.setCompanyCommittent(product.getCompanyCommittent().toLookUpDTO());
                }
                productLookUpDTO.setBoxBarcode(product.getBoxBarcode());
                productLookUpDTO.setBlockBarcode(product.getBlockBarcode());
                if (product.getParent() != null) {
                    productLookUpDTO.setParent(product.getParent().toProductCommonDTO());
                    if (StringUtils.isEmpty(productLookUpDTO.getBlockBarcode())) {
                        productLookUpDTO.setBlockBarcode(product.getParent().getBarcode());
                    }
                }
                if (filter.getWarehouseId() != null) {
                    productLookUpDTO.setQtyInWarehouse(warehouseProductRepository.getQty(filter.getWarehouseId(), product.getId(), product.getUnitId()));
                }
                productLookUpDTO.setWeightBrutto(product.getWeightBrutto());
                productLookUpDTO.setWeightNetto(product.getWeightNetto());
                productLookUpDTO.setQtyInPackage(product.getQtyInPackage());
                productLookUpDTO.setQtyInBox(product.getQtyInBox());
                productLookUpDTO.setQtyInPallet(product.getQtyInPallet());
                productLookUpDTO.setBlockProducts(product.getBlockProducts());
                productLookUpDTO.setDispenseType(product.getDispenseType());
                return productLookUpDTO;
            }).toList();
    }

    public List<ProductLookUpWithWarehouseQtyDTO> getProductLookUpWithWarehouseQty(ProductFilter filter) {
        filter.setEnabledSorting(companySettingsRepository.existsByCompanyIdAndSettings_SettingsType(getCurrentCompanyId(), GenericSettingsType.SORTING_PRODUCTS));
        filter.setProductTypes(ProductType.getBlockProductTypes());
        return productRepository.getProductLookUpWithWarehouseQty(filter);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        Product product = productRepository.findById(id).orElseThrow(validationService.entityNotFoundExceptionThrow(Product.class.getSimpleName(), "id", id));
        WarehouseProduct warehouseProduct = warehouseProductRepository.findFirstByProductIdAndDeletedIsFalse(product.getId()).orElse(null);
        if (warehouseProduct != null && warehouseProduct.getQty().compareTo(BigDecimal.ZERO) != 0) {
            throw validationService.badRequestExceptionThrow(localizationService.localize(MessageConstants.ERROR), localizationService.localize(MessageConstants.DELETING_PRODUCT, new Object[]{warehouseProduct.getWarehouse().getName(), warehouseProduct.getQty()})).get();
        }
        if (stockProductRepository.checkProductInStock(product.getId())) {
            throw validationService.badRequestExceptionThrow(localizationService.localize(MessageConstants.ERROR), localizationService.localize(MessageConstants.DELETING_PRODUCT_CHECK_ERROR)).get();
        }
        if (priceListItemRepository.existsByProductId(product.getId())) {
            throw validationService.badRequestExceptionThrow(localizationService.localize(MessageConstants.ERROR), localizationService.localize(MessageConstants.DELETING_PRODUCT_CHECK_ERROR)).get();
        }
        product.setDeleted(true);
        productRepository.save(product);
    }

    public List<ProductResidueDTO> getProductResidueList(BaseFilter filter) {
        return productRepository.getProductResidueList(filter);
    }

    @Retryable(
            retryFor = {CannotAcquireLockException.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 200))
    public List<TopProductDTO> getTopProductList(BaseFilter filter) {
        return productRepository.getTopProductList(filter);
    }

    public Page<StockTransferProductListDTO> getListToStockTransfer(BaseFilter filter) {
        ResultList<StockTransferProductListDTO> resultList = productRepository.getListToStockTransfer(filter);
        return new PageImpl<>(resultList.getList(), filter.getSortedPageable(), resultList.getCount());
    }

    public List<StockTransferProductListDTO> getLookupToStockTransfer(BaseFilter filter) {
        return productRepository.getListToStockTransfer(filter).getList();
    }

    public List<MarkOrderProductLookUpDTO> getLookUpToMarking(ProductFilter filter) {
        filter.setSortOrderType(SortOrderType.LIST.getCode());
        return productRepository.getResultList(filter).getList().stream().map(product -> {
            MarkOrderProductLookUpDTO markOrderProductLookUpDTO = new MarkOrderProductLookUpDTO();
            markOrderProductLookUpDTO.setId(product.getId());
            markOrderProductLookUpDTO.setName(product.getName());
            markOrderProductLookUpDTO.setBarcode(product.getBarcode());
            markOrderProductLookUpDTO.setProductType((product.getProductType() != null) ? product.getProductType().getCode() : null);
            markOrderProductLookUpDTO.setExpiredInMonth(product.getExpiredInMonth());
            markOrderProductLookUpDTO.setQtyInPallet(product.getQtyInPallet());
            markOrderProductLookUpDTO.setQtyInBox(product.getQtyInBox());
            return markOrderProductLookUpDTO;

        }).toList();
    }

    public List<AicProductLookUpDTO> getLookUpToAic(ProductFilter filter) {
        filter.setBranchId(getCurrentMainBranchId());
        filter.setSortOrderType(SortOrderType.LIST.getCode());
        return productRepository.getResultList(filter).getList().stream()
            .map(product -> new AicProductLookUpDTO(product.getId(), product.getName(), product.getBarcode(), product.getTnved(), product.getProductType(), product.getCatalogCode(), product.getCatalogName(), product.getPackageCode(), product.getPackageName(), product.getAdditionalUnit()))
            .toList();
    }

    @Transactional
    public void favorite(Long id) {
        productRepository.findById(id).map(product -> {
            product.setFavourite(!product.isFavourite());
            return product;
        }).orElseThrow(validationService.entityNotFoundExceptionThrow(Product.class.getSimpleName(), "id", id));
    }

    public Page<WebKassaProductListDTO> getWebKassaProductList(ProductFilter filter) {
        ResultList<WebKassaProductListDTO> resultList = productRepository.getWebKassaProductList(filter);
        return new PageImpl<>(resultList.getList(), filter.getSortedPageable(), resultList.getCount());
    }

    public List<ProductCommonDTO> lookUpToSerial(ProductFilter filter) {
        filter.setBranchId(getCurrentMainBranchId());
        filter.setSortOrderType(SortOrderType.LIST.getCode());
        return productRepository.getResultList(filter).getList().stream().map(Product::toProductCommonDTO).toList();
    }

    /**
     * Main method during creating product.
     * Be carefull while updating code.
     */
    public Product getProductByBarcodeAndVatBarcode(String barcode, String vatBarcode, Long branchId) {
        if (StringUtils.hasText(barcode) && StringUtils.hasText(vatBarcode)) {
            Product product = productRepository.findFirstByBarcodeAndVatBarcodeAndBranchIdAndDeletedIsFalse(barcode.trim(), vatBarcode.trim(), branchId).orElse(null);
            if (product != null) {
                return product;
            }
        }
        if (StringUtils.hasText(barcode)) {
            Product product = productRepository.findFirstByBarcodeAndBranchIdAndDeletedIsFalse(barcode.trim(), branchId).orElse(null);
            if (product != null) {
                return product;
            }
        }
        if (StringUtils.hasText(barcode) && barcode.startsWith("0")) {
            Product product = productRepository.findFirstByBarcodeAndBranchIdAndDeletedIsFalse(barcode.trim().substring(1), branchId).orElse(null);
            if (product != null) {
                return product;
            }
        }
        if (StringUtils.hasText(barcode) && !barcode.startsWith("0") && barcode.trim().length() == 12) {
            Product product = productRepository.findFirstByBarcodeAndBranchIdAndDeletedIsFalse("0".concat(barcode.trim()), branchId).orElse(null);
            if (product != null) {
                return product;
            }
        }
        if (StringUtils.hasText(barcode)) {
            return null;
        }
        if (StringUtils.hasText(vatBarcode)) {
            return productRepository.findFirstByVatBarcodeAndBranchIdAndDeletedIsFalse(vatBarcode, branchId).orElse(null);
        }
        return null;
    }

    @Transactional
    public Long updateStatus(Long id, CommonStatus status) {
        return productRepository
            .findById(id)
            .map(product -> {
                product.setStatus(status);
                productRepository.save(product);
                return product.getId();
            })
            .orElseThrow(validationService.entityNotFoundExceptionThrow(Product.class.getSimpleName(), "id", id));
    }

    @Transactional
    public void create(List<ProductDTO> productDTOS) {
        for (ProductDTO productDTO : productDTOS) {
            try {
                this.create(productDTO);
            } catch (Exception e) {
                log.error("Product create error: {}", e.getMessage());
            }
        }
    }

    @Transactional
    public void assignCategory(ProductCategoryDTO productCategoryDTO) {
        categoryRepository.findById(productCategoryDTO.getCategoryId()).ifPresentOrElse(category -> {
            List<Product> products = productRepository.findAllById(productCategoryDTO.getProductIds());
            products.forEach(product -> product.setCategories(Stream.concat(product.getCategories().stream().filter(productCategory -> category.getEnvironmentType() != productCategory.getEnvironmentType()), Stream.of(category)).collect(Collectors.toSet())));
            productRepository.saveAll(products);
        }, () -> {
            throw validationService.entityNotFoundExceptionThrow(Category.class.getSimpleName(), "id", productCategoryDTO.getCategoryId()).get();
        });
    }

    @Transactional
    public void transfer(Set<Product> products, Category category, Branch branch) {
        products.forEach(fromProduct -> {
            Product product = productRepository.findFirstByVatBarcodeAndBranchIdAndDeletedIsFalse(fromProduct.getVatBarcode(), branch.getId()).orElseGet(Product::new);
            product.setName(fromProduct.getName());
            product.setShortName(fromProduct.getShortName());
            product.setBrand(fromProduct.getBrand());
            product.setBarcode(fromProduct.getBarcode());
            product.setVatBarcode(fromProduct.getVatBarcode());
            product.setCatalogName(fromProduct.getCatalogName());
            product.setPackageName(fromProduct.getPackageName());
            product.setPackageCode(fromProduct.getPackageCode());
            product.setQtyInPackage(fromProduct.getQtyInPackage());
            product.setQtyInBox(fromProduct.getQtyInBox());
            product.setQtyInPallet(fromProduct.getQtyInPallet());
            product.setCapacity(fromProduct.getCapacity());
            product.setProductType(fromProduct.getProductType());
            product.setUnitId(fromProduct.getUnitId());
            product.setVat(fromProduct.getVat());
            product.setPurchasePrice(fromProduct.getPurchasePrice());
            product.setPrice(fromProduct.getPrice());
            product.setForeignPrice(fromProduct.getForeignPrice());
            product.setCurrencyType(fromProduct.getCurrencyType());
            product.setExchangeRate(fromProduct.getExchangeRate());
            if (EnvironmentType.WEB == category.getEnvironmentType()) {
                product.setCategoryId(category.getId());
            }
            product.getCategories().add(category);
            product.setBranchId(branch.getId());
            product.setCompanyId(branch.getCompanyId());
            product.setCompanyCommittentId(fromProduct.getCompanyCommittentId());
            product.setFavourite(fromProduct.isFavourite());
            product.setHasMark(fromProduct.isHasMark());
            product.setDiscount(fromProduct.isDiscount());
            product.setTnved(fromProduct.getTnved());
            product.setBoxBarcode(fromProduct.getBoxBarcode());
            product.setBlockBarcode(fromProduct.getBlockBarcode());
            product.setOrigin(fromProduct.getOrigin());
            product.setWeightBrutto(fromProduct.getWeightBrutto());
            product.setWeightNetto(fromProduct.getWeightNetto());
            product.setExciseRate(fromProduct.getExciseRate());
            product.setExciseSum(fromProduct.getExciseSum());
            product.setAdditionalUnit(fromProduct.getAdditionalUnit());
            product.setBlockProducts(fromProduct.getBlockProducts());
            product.setExpiredInMonth(fromProduct.getExpiredInMonth());
            product.setDispenseType(fromProduct.getDispenseType());
            productRepository.save(product);

            if (CollectionUtils.isNotEmpty(fromProduct.getPhotos())) {
                productPhotosService.transfer(fromProduct.getPhotos(), product.getId());
            }
        });
    }
}
