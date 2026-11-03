package uz.uzkassa.smartpos.service;

import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.smartpos.domain.postgres.catalog.*;
import uz.uzkassa.smartpos.domain.postgres.company.Company;
import uz.uzkassa.smartpos.domain.postgres.fashion.FashionRegister;
import uz.uzkassa.smartpos.dto.TransferCatalogDTO;
import uz.uzkassa.smartpos.dto.fashion.CountItem;
import uz.uzkassa.smartpos.dto.filter.ProductFilter;
import uz.uzkassa.smartpos.dto.integration.agreement.ServiceType;
import uz.uzkassa.smartpos.dto.product.*;
import uz.uzkassa.smartpos.enums.SortTypeEnum;
import uz.uzkassa.smartpos.exceptions.BadRequestException;
import uz.uzkassa.smartpos.filter.FashionRegisterFilter;
import uz.uzkassa.smartpos.rabbitmq.MqConstants;
import uz.uzkassa.smartpos.rabbitmq.producer.RabbitMqProducer;
import uz.uzkassa.smartpos.repository.postgres.catalog.ColorGroupImageRepository;
import uz.uzkassa.smartpos.repository.postgres.catalog.ProductBranchRepository;
import uz.uzkassa.smartpos.repository.postgres.catalog.ProductColorGroupRepository;
import uz.uzkassa.smartpos.repository.postgres.catalog.ProductItemRepository;
import uz.uzkassa.smartpos.repository.postgres.fashion.FashionBalanceHistoryRepository;
import uz.uzkassa.smartpos.repository.postgres.fashion.FashionRegisterRepository;
import uz.uzkassa.smartpos.utils.ServerUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductItemService extends SmartposService {
    private final ProductBranchRepository productBranchRepository;
    private final ProductColorGroupRepository productColorGroupRepository;
    private final ColorGroupImageRepository colorGroupImageRepository;
    private final ProductItemRepository productItemRepository;
    private final FashionRegisterRepository fashionRegisterRepository;

    private final FileUploadService fileUploadService;
    private final IdGeneratorService idGeneratorService;
    private final RabbitMqProducer rabbitMqProducer;
    private final FashionBalanceHistoryRepository fashionBalanceHistoryRepository;

    @Transactional
    public void createColorGroup(final Long productId, Long branchId, final VariationsDTO dto) {
        final Long finalBranchId = branchId != null ? branchId : getCurrentBranchId();
        final Long cityId = getBranchCityId(finalBranchId);
        final ProductBranch productBranch = productBranchRepository.findById(new ProductBranchId(productId, finalBranchId, cityId))
                .orElseThrow(notFound(localizationService.getMessage("product")));

        final String name = productBranch.getName();
        final BigDecimal price = productBranch.getSalesPrice();
        final List<ProductColorGroup> colorGroups = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(dto.getPrints())) {
            dto.getPrints().forEach(print -> dto.getColours()
                    .forEach(variationValue -> colorGroups.add(new ProductColorGroup(productId,
                            finalBranchId,
                            cityId,
                            variationValue.getCode(),
                            variationValue.getValue(),
                            price,
                            print,
                            dto.getSizeKey()))));
        } else {
            dto.getColours().forEach(variationValue -> colorGroups.add(new ProductColorGroup(productId,
                    finalBranchId,
                    cityId,
                    variationValue.getCode(),
                    variationValue.getValue(),
                    price,
                    null,
                    dto.getSizeKey())));
        }
        colorGroups.forEach(colorGroup -> {
            colorGroup.setGenericId(idGeneratorService.generateId());
            dto.getSizes().forEach(size -> colorGroup.addProductItem(generateProductItem(colorGroup, name, price, size)));
        });
        productColorGroupRepository.saveAll(colorGroups);
    }

    @Transactional
    public void updateColorGroup(final Long colorGroupId, final ColorGroupUpdateDTO dto) {
        final ProductColorGroup colorGroup = productColorGroupRepository.findById(new ProductCompositeId(colorGroupId, dto.getBranchId()))
                .orElseThrow(notFound("Color Group"));

        final ProductBranch productBranch = colorGroup.getProductBranch();
        final String name = productBranch.getName();
        final BigDecimal price = Optional.ofNullable(dto.getPrice()).orElse(productBranch.getSalesPrice());

        colorGroup.setPrintName(dto.getPrintName());
        colorGroup.setSalesPrice(dto.getPrice());

        colorGroup.getImages().clear();
        dto.getImages().stream()
                .map(im -> ColorGroupImage.builder()
                        .fileId(im.getFileId())
                        .main(im.isMain())
                        .build())
                .forEach(colorGroup::addImage);

        Map<String, ProductItem> productItemMap = colorGroup.getProductItems().stream().collect(Collectors.toMap(ProductItem::getSizeCode, Function.identity()));
        colorGroup.getProductItems().clear();
        dto.getSizes().forEach(attributeValue -> {
            ProductItem productItem;
            if (productItemMap.containsKey(attributeValue.getCode())) {
                productItem = productItemMap.get(attributeValue.getCode());
                productItem.setSalesPrice(price);
                productItem.setSizeValue(attributeValue.getValue());
                productItem.setSorder(attributeValue.getSorder());
            } else {
                productItem = generateProductItem(colorGroup, name, price, attributeValue);
            }
            colorGroup.addProductItem(productItem);
        });
        productColorGroupRepository.save(colorGroup);
    }

    private ProductItem generateProductItem(final ProductColorGroup colorGroup, String name, final BigDecimal price, AttributeValue size) {
        final ProductItem item = new ProductItem();
        item.setGenericId(idGeneratorService.generateId());
        item.setColorGroupId(colorGroup.getGenericId());
        item.setProductId(colorGroup.getProductId());
        item.setCityId(colorGroup.getCityId());
        item.setBranchId(colorGroup.getBranchId());
        item.setBarcode(ServerUtils.generateUniqueBarcode());
        item.setSalesPrice(price);
        item.setName(StringUtils.join(List.of(name, colorGroup.getName(), size.getValue()), " "));
        item.setSizeKey(colorGroup.getSizeKey());
        item.setSizeCode(size.getCode());
        item.setSizeValue(size.getValue());
        item.setSorder(size.getSorder());
        return item;
    }

    public ProductColorGroupDTO getColorGroup(final Long colorGroupId, final Long branchId) {
        return productColorGroupRepository.findById(new ProductCompositeId(colorGroupId, branchId))
                .map(ProductColorGroup::toDTO)
                .orElseThrow(notFound("Color Group"));
    }

    @Transactional
    public void deleteColorGroup(final Long colorGroupId, final Long branchId) {
        final ProductColorGroup colorGroup = productColorGroupRepository.findById(new ProductCompositeId(colorGroupId, branchId))
                .orElseThrow(notFound("Color Group"));
        boolean exists = fashionRegisterRepository.getByProductItemColorGroupId(colorGroupId);
        if (exists) {
            throw new BadRequestException("Этот товар существует в складе его невозможно удалить");
        }
        rabbitMqProducer.sendWithDelayTime(colorGroupId, MqConstants.DELETE_FASHION_DATA_BY_COLOR_GROUP, 200L);
        productColorGroupRepository.delete(colorGroup);
    }

    public Page<ProductColorGroupDTO> getColorGroups(final Long productId, final ProductFilter filter) {
        filter.setProductId(productId);
        if (filter.getBranchId() == null) {
            filter.setBranchId(getCurrentBranchId());
        }
        return productColorGroupRepository.findAll(getProductColorGroupSpecification(filter), filter.getPageable())
                .map(ProductColorGroup::toDTO);
    }

    public List<ProductColorGroupDTO> getColorGroupList(final Long productId, final ProductFilter filter) {
        filter.setProductId(productId);
        if (filter.getBranchId() == null) {
            filter.setBranchId(getCurrentBranchId());
        }
        return productColorGroupRepository.findAll(getProductColorGroupSpecification(filter)).stream()
                .map(ProductColorGroup::toDTO).toList();
    }

    public List<ProductColorGroupLookupDTO> getColorGroupLookup(Long productId, Long branchId) {
        return productColorGroupRepository.lookup(productId, branchId);
    }


    private static @NotNull Specification<ProductColorGroup> getProductColorGroupSpecification(ProductFilter filter) {
        return (root, query, criteriaBuilder) -> {
            var predicates = new ArrayList<Predicate>();
            predicates.add(criteriaBuilder.equal(root.get(ProductColorGroup_.productId), filter.getProductId()));
            predicates.add(criteriaBuilder.equal(root.get(ProductColorGroup_.branchId), filter.getBranchId()));
            predicates.add(criteriaBuilder.isFalse(root.get(ProductColorGroup_.deleted)));
            if (StringUtils.isNotBlank(filter.getSearch())) {
                predicates.add(criteriaBuilder.like(root.get(ProductColorGroup_.colorName), filter.getSearchForQuery()));
            }
            if (StringUtils.isNotBlank(filter.getOrderBy())) {
                if (SortTypeEnum.asc.equals(filter.getSortOrder())) {
                    query.orderBy(criteriaBuilder.asc(root.get(filter.getOrderBy())));
                } else {
                    query.orderBy(criteriaBuilder.desc(root.get(filter.getOrderBy())));
                }
            } else {
                query.orderBy(criteriaBuilder.desc(root.get(ProductColorGroup_.colorName)));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    public Page<ProductItemDTO> getPage(final ProductFilter filter) {
        if (filter.getBranchId() == null) {
            filter.setBranchId(getCurrentBranchId());
        }
        filter.setCityId(getBranchCityId(filter.getBranchId()));
        if (filter.getSize() == 0) {
            filter.setSize(500);
        }

        return productItemRepository.findAllByFilter(filter)
                .map(ProductItem::getDTO);
    }

    public Page<ProductItemDTO> getItems(final Long productId, final ProductFilter filter) {
        filter.setProductId(productId);
        if (filter.getBranchId() == null) {
            filter.setBranchId(getCurrentBranchId());
        }
        filter.setCityId(getBranchCityId(filter.getBranchId()));
        return productItemRepository.findAllByFilter(filter)
                .map(ProductItem::getDTO);
    }

    @Transactional
    public ProductItemDTO updateItem(final Long productItemId, final ProductItemDTO dto) {
        final ProductItem entity = productItemRepository.findById(new ProductCompositeId(productItemId, dto.getBranchId()))
                .orElseThrow(notFound("Product Item"));
        entity.setSalesPrice(dto.getSalesPrice());
        productItemRepository.save(entity);
        return entity.getDTO();
    }

    @Transactional
    public void deleteItem(final Long productItemId, final Long branchId) {
        final ProductItem entity = productItemRepository.findById(new ProductCompositeId(productItemId, branchId))
                .orElseThrow(notFound("Product Item"));
        boolean exists = fashionRegisterRepository.getByProductItemId(productItemId);
        if (exists) {
            throw new BadRequestException("Этот товар существует в складе его невозможно удалить");
        }
        entity.setDeleted(true);
        rabbitMqProducer.sendWithDelayTime(productItemId, MqConstants.DELETE_FASHION_DATA_BY_PRODUCT_ITEM, 200L);
        productItemRepository.save(entity);
    }

    @Transactional
    public void deleteGroupImage(final Long groupImageId) {
        var colorGroupImage = colorGroupImageRepository.findById(groupImageId)
                .orElseThrow(notFound("Color Group Image"));
        colorGroupImageRepository.delete(colorGroupImage);
        fileUploadService.removeObject(colorGroupImage.getFile());
    }

    public ProductItemSearchDTO productSearch(final String barcode, Long branchId) {
        Optional<ProductItem> productItemOptional = productItemRepository.findOneByBarcode(barcode);
        if (productItemOptional.isPresent()) {
            ProductItemSearchDTO product = productItemOptional
                    .map(ProductItem::getSearchDto)
                    .orElseThrow(notFound("product"));
            BigDecimal stockAmount = fashionRegisterRepository.getStockAmountByBranchIdAndProductItemId(product.getBranchId(), product.getId());
            product.setStockAmount(stockAmount);
            return product;
        } else {
            branchId = Optional.ofNullable(branchId).orElseGet(this::getCurrentBranchId);
            ProductBranch productBranch = productBranchRepository.findFirstByBarcodeAndBranchIdAndCityIdAndDeletedIsFalse(barcode, branchId, getBranchCityId(branchId))
                    .orElseThrow(notFound("product"));
            ProductItemSearchDTO product = new ProductItemSearchDTO(productBranch);
            BigDecimal stockAmount = fashionRegisterRepository.getStockAmountByBranchIdAndProductId(product.getBranchId(), product.getId());
            product.setStockAmount(stockAmount);
            return product;
        }
    }

    public List<ProductItemSearchDTO> productLookup(final String barcode, Long branchId) {
        var itemIds = new ArrayList<Long>();
        branchId = Optional.ofNullable(branchId).orElseGet(this::getCurrentBranchId);
        var list = productItemRepository.findAllLikeBarcode("%" + barcode + "%", branchId)
                .stream()
                .peek(item -> itemIds.add(item.getGenericId()))
                .map(ProductItem::getSearchDto).toList();
        if (CollectionUtils.isNotEmpty(list)) {
            Map<Long, BigDecimal> registerMap = fashionRegisterRepository.findAllByBranchIdAndProductItemIdIn(branchId, itemIds).stream()
                    .collect(Collectors.toMap(FashionRegister::getProductItemId, FashionRegister::getQty));
            list.forEach(item -> item.setStockAmount(registerMap.getOrDefault(item.getId(), BigDecimal.ZERO)));
            return list;
        } else {
            List<ProductItemSearchDTO> dtos = productBranchRepository.findAllLikeBarcode(barcode, branchId, getBranchCityId(branchId))
                    .stream()
                    .map(ProductItemSearchDTO::new)
                    .toList();
            Map<Long, BigDecimal> stock = fashionRegisterRepository.getProductsStockAmount(branchId, dtos.stream().map(searchDto -> searchDto.getProduct().getId()).toList());
            dtos.forEach(dto -> dto.setStockAmount(stock.getOrDefault(dto.getProduct().getId(), BigDecimal.ZERO)));
            return dtos;
        }
    }

    public ProductItemInfoDTO productInfo(final Long productId, final Long paramBranchId) {
        final Long branchId = Optional.ofNullable(paramBranchId).orElseGet(this::getCurrentBranchId);
        final Long cityId = getBranchCityId(branchId);
        final ProductBranch productBranch = productBranchRepository.findById(new ProductBranchId(productId, branchId, cityId))
                .orElseThrow(notFound(localizationService.getMessage("product")));
        final ProductItemInfoDTO infoDTO = new ProductItemInfoDTO();
        infoDTO.setProduct(productBranch.getDTO(false));
        final Company company = companyRepository.findById(getCurrentCompanyId()).orElseThrow(notFound(localizationService.getMessage("company")));
        final boolean warehouseEnabled = company.isFreeTrial() || company.getPermissions().getOrDefault(ServiceType.WAREHOUSE, LocalDateTime.MIN).isAfter(LocalDateTime.now());
        infoDTO.setWarehouseEnabled(warehouseEnabled);
        final List<ProductColorGroup> colorGroups = productColorGroupRepository.findAllByProductIdAndBranchIdAndDeletedIsFalse(productId, branchId);
        if (warehouseEnabled) {
            infoDTO.getProduct().setAmount(fashionRegisterRepository.getQtyByProduct(branchId, productId));
        }
        final FashionRegisterFilter filter = new FashionRegisterFilter();
        filter.setCompanyId(getCurrentCompanyId());
        filter.setProductId(productId);
        if (paramBranchId != null) {
            filter.setBranchIds(Set.of(paramBranchId));
        }
        final List<FashionRegister> fashionRegisters = fashionRegisterRepository.colorGroupItemsRegisters(filter);
        Map<Long, BigDecimal> itemRegisterMap;
        if (warehouseEnabled) {
            itemRegisterMap = fashionRegisters.stream().collect(Collectors.toMap(FashionRegister::getProductItemId, FashionRegister::getQty, BigDecimal::add));
        } else {
            itemRegisterMap = new HashMap<>();
        }
        if (CollectionUtils.isNotEmpty(colorGroups)) {
            final List<ProductItemStockInfo> stockInfos = new ArrayList<>();
            colorGroups.forEach(colorGroup -> {
                final ProductItemStockInfo stockInfo = new ProductItemStockInfo();
                stockInfo.setColorCode(colorGroup.getColorCode());
                stockInfo.setColorName(colorGroup.getColorName());
                stockInfo.setPrintName(colorGroup.getPrintName());
                stockInfo.setSalesPrice(colorGroup.getSalesPrice());
                stockInfo.setSizeKey(colorGroup.getSizeKey());
                stockInfo.setItems(colorGroup.getProductItems()
                        .stream()
                        .map(item -> CountItem.builder()
                                .id(item.getGenericId())
                                .sizeCode(item.getSizeCode())
                                .sizeValue(item.getSizeValue())
                                .sorder(item.getSorder())
                                .count(warehouseEnabled ? itemRegisterMap.getOrDefault(item.getGenericId(), BigDecimal.ZERO) : null)
                                .build())
                        .toList());
                stockInfo.setImages(colorGroup.getImages().stream()
                        .map(ColorGroupImageDTO::new)
                        .toList());
                stockInfos.add(stockInfo);
            });
            infoDTO.setStock(stockInfos);
        }
        return infoDTO;
    }

    public List<ProductItemBranchStockDTO> productItemBranchStock(final Long productItemId) {
        return fashionRegisterRepository.productItemBranchStock(productItemId);
    }

    @Transactional
    public void transferItems(TransferCatalogDTO dto) {
        final Long fromBranchId = dto.getFromBranchId();
        final Long toBranchId = dto.getToBranchIds().stream().findFirst().orElseThrow();
        final Long cityId = getBranchCityId(toBranchId);
        int page = 0;
        Page<ProductColorGroup> colorGroups = productColorGroupRepository.findAllByBranchIdAndDeletedIsFalse(fromBranchId, PageRequest.of(page++, 500));
        while (!colorGroups.isEmpty()) {
            List<ProductColorGroup> colorGroupList = colorGroups.map(colorGroup -> {
                ProductColorGroup newProductColorGroup = new ProductColorGroup(colorGroup, toBranchId, cityId);
                newProductColorGroup.setProductItems(colorGroup.getProductItems().stream()
                        .map(productItem -> new ProductItem(productItem, toBranchId, cityId)).collect(Collectors.toSet()));
                return newProductColorGroup;
            }).toList();
            productColorGroupRepository.saveAll(colorGroupList);
            colorGroups = productColorGroupRepository.findAllByBranchIdAndDeletedIsFalse(fromBranchId, PageRequest.of(page++, 500));
        }
    }

    public void deleteFashionDataByColorGroup(Long colorGroupId) {
        fashionRegisterRepository.deleteAllByProductItemColorGroupId(colorGroupId);
        fashionBalanceHistoryRepository.deleteAllByProductItemColorGroupId(colorGroupId);
    }

    public void deleteFashionDataByProductItem(Long productItemId) {
        fashionRegisterRepository.deleteAllByProductItemId(productItemId);
        fashionBalanceHistoryRepository.deleteAllByProductItemId(productItemId);
    }
}
