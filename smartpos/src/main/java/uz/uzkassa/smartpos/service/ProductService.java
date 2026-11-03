package uz.uzkassa.smartpos.service;

import com.google.common.collect.Sets;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.smartpos.domain.postgres.catalog.*;
import uz.uzkassa.smartpos.domain.postgres.common.Excise;
import uz.uzkassa.smartpos.domain.postgres.common.Unit;
import uz.uzkassa.smartpos.domain.postgres.company.Branch;
import uz.uzkassa.smartpos.domain.postgres.company.Company;
import uz.uzkassa.smartpos.domain.postgres.fashion.FashionIncomeOfProduct;
import uz.uzkassa.smartpos.domain.postgres.warehouse.Register;
import uz.uzkassa.smartpos.dto.UnitSelectItem;
import uz.uzkassa.smartpos.dto.base.PageDto;
import uz.uzkassa.smartpos.dto.catalog.*;
import uz.uzkassa.smartpos.dto.fashion.catalog.CashboxProductListDto;
import uz.uzkassa.smartpos.dto.filter.CashboxProductFilter;
import uz.uzkassa.smartpos.dto.filter.ImportProductsFilter;
import uz.uzkassa.smartpos.dto.filter.ProductFilter;
import uz.uzkassa.smartpos.dto.integration.agreement.ServiceType;
import uz.uzkassa.smartpos.dto.mq.DeleteProductBranchReferencesPayload;
import uz.uzkassa.smartpos.dto.mq.RegisterItemPayload;
import uz.uzkassa.smartpos.dto.product.AttributeValue;
import uz.uzkassa.smartpos.enums.CompanyType;
import uz.uzkassa.smartpos.enums.MarkingStatus;
import uz.uzkassa.smartpos.exceptions.BadRequestException;
import uz.uzkassa.smartpos.filter.ProductBranchFilter;
import uz.uzkassa.smartpos.filter.TerminalProductFilter;
import uz.uzkassa.smartpos.integration.tasnif.dto.MxikPackage;
import uz.uzkassa.smartpos.mappers.ProductBranchMapper;
import uz.uzkassa.smartpos.rabbitmq.MqConstants;
import uz.uzkassa.smartpos.rabbitmq.producer.RabbitMqProducer;
import uz.uzkassa.smartpos.report.ProductImportXlsParser;
import uz.uzkassa.smartpos.repository.postgres.catalog.*;
import uz.uzkassa.smartpos.repository.postgres.common.UnitRepository;
import uz.uzkassa.smartpos.repository.postgres.common.VatRepository;
import uz.uzkassa.smartpos.repository.postgres.company.ExciseRepository;
import uz.uzkassa.smartpos.repository.postgres.fashion.FashionBalanceHistoryRepository;
import uz.uzkassa.smartpos.repository.postgres.fashion.FashionIncomeOfProductRepository;
import uz.uzkassa.smartpos.repository.postgres.fashion.FashionRegisterRepository;
import uz.uzkassa.smartpos.repository.postgres.fashion.FashionStockAdjustmentItemRepository;
import uz.uzkassa.smartpos.repository.postgres.warehouse.*;
import uz.uzkassa.smartpos.security.SecurityUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static uz.uzkassa.smartpos.constants.Constants.BRANDS;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService extends SmartposService {
    private final TransferDetailRepository transferDetailRepository;
    private final StockAdjustmentItemRepository stockAdjustmentItemRepository;
    private final BalanceHistoryRepository balanceHistoryRepository;
    private final ExciseRepository exciseRepository;
    private final IncomeOfProductDetailRepository incomeOfProductDetailRepository;
    private final MxikRepository mxikRepository;
    private final ProductBranchRepository productBranchRepository;
    private final ProductUnitRepository productUnitRepository;
    private final ProductPriceHistoryRepository productPriceHistoryRepository;
    private final UnitRepository unitRepository;
    private final RegisterRepository registerRepository;
    private final CategoryBranchRepository categoryBranchRepository;
    private final RabbitMqProducer rabbitMqProducer;
    private final ProductBranchMapper productBranchMapper;
    private final CategoryRepository categoryRepository;
    private final IdGeneratorService idGeneratorService;
    private final AttributeRepository attributeRepository;
    private final FashionIncomeOfProductRepository fashionIncomeOfProductRepository;
    private final ProductColorGroupRepository productColorGroupRepository;
    private final ProductItemRepository productItemRepository;
    private final FashionRegisterRepository fashionRegisterRepository;
    private final ProductImportXlsParser productImportXlsParser;
    private final FashionBalanceHistoryRepository fashionBalanceHistoryRepository;
    private final FashionStockAdjustmentItemRepository fashionStockAdjustmentItemRepository;
    private final CompanyExtraInfoService companyExtraInfoService;
    private final VatRepository vatRepository;
    private final IncomeOfProductRepository incomeOfProductRepository;
    private final ProductForMarkingRepository productForMarkingRepository;
    private final StockAdjustmentRepository stockAdjustmentRepository;

    public ProductBranch create(Long branchId, final ProductCreateDto dto) {
        branchId = branchId != null ? branchId : getCurrentBranchId();
        final Long cityId = getBranchCityId(branchId);
        final Mxik mxik = mxikRepository.findByMxik(dto.vatBarcode()).orElseThrow(badRequest(localizationService.getMessage("ikpu.correct", dto.vatBarcode())));
        if (CollectionUtils.isEmpty(mxik.getPackages())) {
            throw badRequest(localizationService.getMessage("mxik.use.restriction", dto.vatBarcode())).get();
        }
        final Unit unit = unitRepository.findById(dto.unitId()).orElseThrow(notFound(localizationService.getMessage("unit")));
        final CategoryBranch category = categoryBranchRepository.findFirstByCategoryIdAndBranchIdAndEnabledIsTrue(dto.categoryId(), branchId)
                .orElseThrow(notFound(localizationService.getMessage("category")));
        var vatRate = dto.vatRate();
        if (vatRate != null) {
            if (vatRepository.findAll().stream().noneMatch(vat -> vat.getPercent().compareTo(vatRate) == 0)) {
                throw new BadRequestException("Вам необходимо отправить правильную ставку НДС: " + vatRate);
            }
        }
        final ProductBranch product = productBranchMapper.toEntity(dto);
        if (StringUtils.isBlank(product.getBarcode())) {
            if (mxik.getBarcode() != null) {
                product.setBarcode(mxik.getBarcode());
            } else {
                product.setBarcode(RandomStringUtils.randomNumeric(3, 12));
            }
        }
        product.setProductId(idGeneratorService.generateId());
        product.setBranchId(branchId);
        product.setCityId(cityId);
        if (product.getDefaultPackage() == null) {
            product.setDefaultPackage(mxik.getDefaultPackage());
        }
        setExcise(product);
        product.setUnit(unit);
        product.setCategory(category);
        if (CollectionUtils.isNotEmpty(dto.attributes())) {
            product.setAttributes(dto.attributes());
            final Optional<AttributeValue> brand = dto.attributes()
                    .stream()
                    .filter(a -> a.getKey().equals(BRANDS))
                    .findAny();
            brand.ifPresent(attributeValue -> createAttributeIfNotExists(attributeValue, getCurrentCompanyId()));
        }
        product.setImage(dto.image());
        productBranchRepository.save(product);
        return product;
    }

    private void createAttributeIfNotExists(final AttributeValue attribute, final Long companyId) {
        final Attribute attribute1 = attributeRepository.findFirstByKeyAndCode(attribute.getKey(), attribute.getCode())
                .orElseGet(Attribute::new);
        if (attribute1.getId() == null) {
            attribute1.setKey(attribute.getKey());
            attribute1.setCode(attribute.getCode());
            attribute1.setNameEn(attribute.getValue());
            attribute1.setNameRu(attribute.getValue());
            attribute1.setNameUz(attribute.getValue());
            attribute1.setCompanyId(companyId);
            attributeRepository.save(attribute1);
        }
    }

    @Transactional
    public ProductBranch update(Long id, Long branchId, ProductCreateDto dto) {
        branchId = branchId != null ? branchId : getCurrentBranchId();
        final Long cityId = getBranchCityId(branchId);
        ProductBranch product = productBranchRepository.findById(new ProductBranchId(id, branchId, cityId))
                .orElseThrow(notFound(localizationService.getMessage("product")));
        Mxik mxik = mxikRepository.findByMxik(dto.vatBarcode()).orElseThrow(badRequest(localizationService.getMessage("ikpu.correct", dto.vatBarcode())));
        if (CollectionUtils.isEmpty(mxik.getPackages())) {
            throw badRequest(localizationService.getMessage("mxik.use.restriction", dto.vatBarcode())).get();
        }
        unitRepository.findById(dto.unitId()).orElseThrow(notFound(localizationService.getMessage("unit")));
        categoryBranchRepository.findFirstByCategoryIdAndBranchIdAndEnabledIsTrue(dto.categoryId(), branchId).orElseThrow(notFound(localizationService.getMessage("category")));
        var vatRate = dto.vatRate();
        if (vatRate != null) {
            if (vatRepository.findAll().stream().noneMatch(vat -> vat.getPercent().compareTo(vatRate) == 0)) {
                throw new BadRequestException("Вам необходимо отправить правильную ставку НДС: " + vatRate);
            }
        }
        setPrice(dto.salesPrice(), product);
        productBranchMapper.partialUpdate(dto, product);
        if (StringUtils.isBlank(product.getBarcode())) {
            if (mxik.getBarcode() != null) {
                product.setBarcode(mxik.getBarcode());
            } else {
                product.setBarcode(RandomStringUtils.randomNumeric(3, 12));
            }
        }
        Set<Integer> packageCodes = mxik.getPackages().stream().map(MxikPackage::getCode).collect(Collectors.toSet());
        if (product.getDefaultPackage() == null || product.getDefaultPackage().getCode() == 0 || !packageCodes.contains(product.getDefaultPackage().getCode())) {
            product.setDefaultPackage(mxik.getDefaultPackage());
        }
        setExcise(product);
        product.setAttributes(dto.attributes());
        productBranchRepository.save(product);
        rabbitMqProducer.sendWithDelayTime(
                new RegisterItemPayload(
                        branchId,
                        product.getProductId(),
                        product.getVatBarcode(),
                        product.getCategoryId(),
                        product.getName(),
                        product.getUnitId(),
                        product.isHasMark()),
                MqConstants.REGISTER_UPDATE_QUEUE,
                100L);
        return product;
    }

    @Transactional
    public Long createForCabinet(Long branchId, ProductCreateDto dto) {
        return create(branchId, dto).getProductId();
    }

    @Transactional
    public ProductDTO createFor1C(final Product1CCreateDto dto) {
        final Long branchId = dto.getBranchId() == null ? getCurrentBranchId() : dto.getBranchId();
        ProductBranch productBranch = create(branchId, productBranchMapper.toCreateDto(dto));
        return productBranch.getDTO(true);
    }

    @Transactional
    public ProductDTO updateFor1C(final Product1CCreateDto productDTO) {
        ProductBranch product = update(productDTO.getId(), productDTO.getBranchId(), productBranchMapper.toCreateDto(productDTO));
        return product.getDTO(true);
    }

    private void setExcise(final ProductBranch product) {
        Long currentCompanyId = getCurrentCompanyId();
        Boolean exciseEnabled = companyExtraInfoService.defineExciseDisabledOrNot(currentCompanyId);

        // Check if exciseDisable is false or null
        if (Boolean.FALSE.equals(exciseEnabled)) {
            product.setHasExcise(false);
            product.setExciseAmount(null);
            return;
        }

        exciseRepository.findById(product.getVatBarcode()).ifPresentOrElse(excise -> {
            product.setHasExcise(true);
            product.setExciseAmount(excise.getPrice());
        }, () -> {
            product.setHasExcise(false);
            product.setExciseAmount(null);
        });
    }

    @Transactional
    public void setPriceExisting(final BigDecimal newPrice, final Long productId, final Long branchId) {
        final ProductBranch productBranch = productBranchRepository.findById(new ProductBranchId(productId, branchId, getBranchCityId(branchId)))
                .orElseThrow(notFound(localizationService.getMessage("product")));
        final boolean changed = setPrice(newPrice, productBranch);
        if (changed) {
            productBranchRepository.save(productBranch);
        }
    }

    @Transactional
    public boolean setPrice(final BigDecimal newPrice, final ProductBranch productBranch) {
        if (BigDecimal.ZERO.compareTo(newPrice) != 0 &&
            (productBranch.getSalesPrice() == null || productBranch.getSalesPrice().compareTo(newPrice) != 0)) {

            final ProductPriceHistory priceHistory = new ProductPriceHistory();
            priceHistory.setOldPrice(productBranch.getSalesPrice());
            priceHistory.setNewPrice(newPrice);
            priceHistory.setProductId(productBranch.getProductId());
            priceHistory.setBranchId(productBranch.getBranchId());
            priceHistory.setUpdatedById(getCurrentUserId());
            priceHistory.setUpdatedDateTime(LocalDateTime.now());
            productPriceHistoryRepository.save(priceHistory);
            productBranch.setSalesPrice(newPrice);
            return true;
        }
        return false;
    }

    @Transactional
    public void deleteProductFromBranch(final Long id, final Long branchId) {
        final Branch branch = branchRepository.findById(branchId)
                .orElseThrow(notFound(localizationService.getMessage("branch"), branchId));
        checkForAccessToCompany(branch.getCompanyId());
        final ProductBranch product = productBranchRepository.findById(new ProductBranchId(id, branchId, getBranchCityId(branchId)))
                .orElseThrow(notFound(localizationService.getMessage("product")));
        product.setFavorite(false);
        product.setDeleted(true);
        productBranchRepository.save(product);

        rabbitMqProducer.sendWithDelayTime(new DeleteProductBranchReferencesPayload(id, branchId), MqConstants.DELETE_PRODUCT_REFERENCES, 50L);
    }

    @Transactional
    public void deleteAllProductBranchReferencesByIds(Long cityId, Long branchId, List<Long> productIds) {
        registerRepository.deleteAllByBranchIdAndCityIdAndProductIdIn(branchId, cityId, productIds);
        balanceHistoryRepository.deleteAllByBranchIdAndCityIdAndProductIdIn(branchId, cityId, productIds);

        fashionRegisterRepository.deleteAllByBranchIdAndCityIdAndProductIdIn(branchId, cityId, productIds);
        fashionBalanceHistoryRepository.deleteAllByBranchIdAndCityIdAndProductIdIn(branchId, cityId, productIds);

        fashionStockAdjustmentItemRepository.deleteAllByBranchIdAndCityIdAndProductIdIn(branchId, cityId, productIds);
        stockAdjustmentItemRepository.deleteAllByBranchIdAndCityIdAndProductIdIn(branchId, cityId, productIds);

        transferDetailRepository.deleteAllByBranchIdAndCityIdAndProductIdIn(branchId, cityId, productIds);

        productPriceHistoryRepository.deleteAllByBranchIdAndProductIdIn(branchId, productIds);

        productForMarkingRepository.deleteAllByBranchIdAndStatusNotInAndProductIdIn(branchId, List.of(MarkingStatus.SOLD, MarkingStatus.OPENED), productIds);
    }

    public Page<ProductDTO> getPage(final ProductFilter filter) {
        if (filter.getBranchId() == null) {
            filter.setBranchId(getCurrentBranchId());
        }
        filter.setCityId(getBranchCityId(filter.getBranchId()));
        if (filter.getSize() == 0) {
            filter.setSize(500);
        }
        List<ProductUnit> productUnits;
        if (filter.getFromDateTime() != null) {
            productUnits = productUnitRepository.findAllByBranchIdAndLastModifiedDateAfterOrderBySorder(filter.getBranchId(), filter.getFromDateTime());
        } else {
            productUnits = productUnitRepository.findAllByBranchIdOrderBySorder(filter.getBranchId());
        }
        final Map<Long, List<ProductUnitDTO>> productUnitMap = productUnits.stream().map(ProductUnit::toDTO)
                .collect(Collectors.groupingBy(ProductUnitDTO::getProductId));

        final Page<ProductDTO> products = productBranchRepository.findAllByFilter(filter)
                .map(productBranch -> productBranch.getDTO(false));
        for (ProductDTO product : products.getContent()) {
            if (productUnitMap.containsKey(product.getId())) {
                product.setUnits(productUnitMap.get(product.getId()));
            } else if (product.getUnit() != null) {
                final ProductUnitDTO unitDTO = new ProductUnitDTO();
                unitDTO.setId(product.getId());
                unitDTO.setProductId(product.getId());
                unitDTO.setUnitId(product.getUnit().getId());
                unitDTO.setUnitName(product.getUnit().getName());
                unitDTO.setCoefficient(1D);
                unitDTO.setCount(1);
                unitDTO.setSorder(0);
                unitDTO.setPrice(product.getSalesPrice());
                unitDTO.setBase(true);
                product.getUnits().add(unitDTO);
            }
        }
        return products;
    }

    public Page<ProductDTO> getPageWeb(final ProductFilter filter) {
        if (filter.getBranchId() == null) {
            filter.setBranchId(getCurrentBranchId());
        }
        filter.setCityId(getBranchCityId(filter.getBranchId()));
        List<ProductUnit> productUnits;
        if (filter.getFrom() != null) {
            productUnits = productUnitRepository.findAllByBranchIdAndLastModifiedDateAfterOrderBySorder(filter.getBranchId(), filter.getFrom());
        } else {
            productUnits = productUnitRepository.findAllByBranchIdOrderBySorder(filter.getBranchId());
        }
        final Map<Long, List<ProductUnitDTO>> productUnitMap = productUnits.stream().map(ProductUnit::toDTO)
                .collect(Collectors.groupingBy(ProductUnitDTO::getProductId));

        final Page<ProductDTO> products = productBranchRepository.findAllByFilter(filter)
                .map(productBranch -> productBranch.getDTO(false));
        for (ProductDTO product : products.getContent()) {
            if (productUnitMap.containsKey(product.getId())) {
                product.setUnits(productUnitMap.get(product.getId()));
            } else if (product.getUnit() != null) {
                final ProductUnitDTO unitDTO = new ProductUnitDTO();
                unitDTO.setId(product.getId());
                unitDTO.setProductId(product.getId());
                unitDTO.setUnitId(product.getUnit().getId());
                unitDTO.setUnitName(product.getUnit().getName());
                unitDTO.setCoefficient(1D);
                unitDTO.setCount(1);
                unitDTO.setSorder(0);
                unitDTO.setPrice(product.getSalesPrice());
                unitDTO.setBase(true);
                product.getUnits().add(unitDTO);
            }
        }
        return products;
    }

    public Page<ProductDTO> getPageV2(final ProductFilter filter) {
        if (filter.getBranchId() == null) {
            filter.setBranchId(getCurrentBranchId());
        }
        filter.setCityId(getBranchCityId(filter.getBranchId()));
        final Page<ProductDTO> productDTOS = productBranchRepository.findAllByFilter(filter)
                .map(productBranch -> productBranch.getDTO(true));
        final Company company = companyRepository.getReferenceById(getCurrentCompanyId());
        if (company.isFreeTrial() || company.getPermissions().getOrDefault(ServiceType.WAREHOUSE, LocalDateTime.MIN).isAfter(LocalDateTime.now())) {
            final Map<Long, BigDecimal> registerMap = registerRepository.findAllByBranchIdAndProductIdIn(filter.getBranchId(), productDTOS.map(ProductDTO::getId).stream().collect(Collectors.toList()))
                    .stream().collect(Collectors.toMap(Register::getProductId, Register::getBalance));
            productDTOS.forEach(productDTO -> productDTO.setAmount(registerMap.getOrDefault(productDTO.getId(), BigDecimal.ZERO)));
        }
        return productDTOS;
    }


    public Page<CashboxProductListDto> getCashboxPage(final CashboxProductFilter filter) {
        if (filter.getBranchId() == null) {
            filter.setBranchId(getCurrentBranchId());
        }
        filter.setCityId(getBranchCityId(filter.getBranchId()));
        return productBranchRepository.findAllByFilterForFashion(filter);
    }

    public ProductStatisticsDTO getProductStatistics(final ProductFilter filter) {
        if (filter.getBranchId() == null) {
            filter.setBranchId(getCurrentBranchId());
        }
        filter.setCityId(getBranchCityId(filter.getBranchId()));
        return productBranchRepository.getProductStatistics(filter);
    }

    public ProductDTO getProduct(Long id, Long branchId) {
        branchId = branchId == null ? getCurrentBranchId() : branchId;
        final Long cityId = getBranchCityId(branchId);
        final ProductDTO productDTO = productBranchRepository.findById(new ProductBranchId(id, branchId, cityId))
                .map(productBranch -> productBranch.getDTO(true))
                .orElseThrow(notFound(localizationService.getMessage("product")));
        final List<ProductUnitDTO> list = productUnitRepository.findAllByBranchIdAndProductIdOrderBySorder(branchId, id)
                .stream().map(ProductUnit::toDTO).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(list)) {
            productDTO.setUnits(list);
        } else if (productDTO.getUnit() != null) {
            final ProductUnitDTO dto = new ProductUnitDTO();
            dto.setId(productDTO.getId());
            dto.setProductId(id);
            dto.setUnitId(productDTO.getUnit().getId());
            dto.setUnitName(productDTO.getUnit().getName());
            dto.setBase(true);
            dto.setCoefficient(1D);
            dto.setSorder(0);
            dto.setCount(1);
            dto.setPrice(productDTO.getSalesPrice());
            productDTO.getUnits().add(dto);
        }
        return productDTO;
    }

    public List<UnitSelectItem> getUnits(final Long productId, final Long branchId, Boolean withBalance) {
        final List<UnitSelectItem> result = productUnitRepository.findAllByBranchIdAndProductIdOrderBySorder(branchId,
                productId).stream().map(ProductUnit::toSelectItem).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(result)) {
            final Long cityId = getBranchCityId(branchId);
            final ProductBranch productBranch = productBranchRepository.findById(new ProductBranchId(productId, branchId, cityId))
                    .orElseThrow(notFound(localizationService.getMessage("product")));
            if (productBranch.getUnit() != null) {
                final UnitSelectItem unit = new UnitSelectItem(productBranch.getUnitId(), productBranch.getUnit().getName(), true);
                unit.setPrice(productBranch.getSalesPrice());
                result.add(unit);
            }
        }
        if (withBalance) {
            final Optional<Register> registerOptional = registerRepository.findOneByBranchIdAndProductId(branchId, productId);
            if (registerOptional.isPresent()) {
                for (UnitSelectItem item : result) {
                    item.setBalance(registerOptional.get().getBalance().divide(BigDecimal.valueOf(Optional.ofNullable(item.getCoefficient()).orElse(1D)), 4, RoundingMode.HALF_UP));
                }
            }
        }
        return result;
    }

    public List<ProductPriceHistoryDTO> getProductPriceHistory(final Long productId, final Long branchId) {
        final Branch branch = branchRepository.findById(branchId)
                .orElseThrow(notFound(localizationService.getMessage("branch"), branchId));
        checkForAccessToCompany(branch.getCompanyId());
        return productPriceHistoryRepository.findAllByProductIdAndBranchIdOrderByUpdatedDateTimeDesc(productId, branchId).stream()
                .map(ProductPriceHistory::getDTO).collect(Collectors.toList());
    }

    public ProductDTO getByBarcode(String barcode, Long branchId) {
        final ProductDTO product = productBranchRepository.findFirstByBarcodeAndBranchIdAndCityIdAndDeletedIsFalse(barcode,
                        branchId,
                        getBranchCityId(branchId))
                .map(productBranch -> productBranch.getDTO(true))
                .orElseThrow(notFound(localizationService.getMessage("product")));
        final Company company = companyRepository.getReferenceById(getCurrentCompanyId());
        if (company.isFreeTrial() || company.getPermissions().getOrDefault(ServiceType.WAREHOUSE, LocalDateTime.MIN).isAfter(LocalDateTime.now())) {
            product.setAmount(registerRepository.findOneByBranchIdAndProductId(product.getBranchId(), product.getId()).map(Register::getBalance).orElse(null));
        }
        return product;
    }

    @Transactional
    public void addToFavorites(Long id, Long branchId) {
        final Branch branch = branchRepository.findById(branchId)
                .orElseThrow(notFound(localizationService.getMessage("branch"), branchId));
        checkForAccessToCompany(branch.getCompanyId());
        final ProductBranch product = productBranchRepository.findById(new ProductBranchId(id, branchId, branch.getCityKeyId()))
                .orElseThrow(badRequest(localizationService.getMessage("product.not.found")));
        product.setFavorite(true);
        productBranchRepository.save(product);
    }

    @Transactional
    public void deleteFromFavorites(Long id, Long branchId) {
        final Branch branch = branchRepository.findById(branchId)
                .orElseThrow(notFound(localizationService.getMessage("branch"), branchId));
        checkForAccessToCompany(branch.getCompanyId());
        final ProductBranch product = productBranchRepository.findById(new ProductBranchId(id, branchId, branch.getCityKeyId()))
                .orElseThrow(badRequest(localizationService.getMessage("product.not.found")));
        product.setFavorite(false);
        productBranchRepository.save(product);
    }


    public List<ProductDTO> search(final ProductFilter filter) {
        filter.setBranchIds(getBranchesByGroup(filter.getBranchGroupIds(), filter.getBranchIds()));
        if (CollectionUtils.isEmpty(filter.getBranchIds())) {
            filter.setBranchIds(Sets.newHashSet(getCurrentBranchId()));
        }
        final List<Branch> branchList = branchRepository.findAllById(filter.getBranchIds());
        filter.setBranchCityMap(branchList.stream().collect(Collectors.toMap(Branch::getId, Branch::getCityKeyId)));
        return productBranchRepository.search(filter)
                .stream()
                .map(ProductBranch::getAsSelectItem)
                .collect(Collectors.toList());
    }

    public ByteArrayResource importProductsRef(final MultipartFile file, final ImportProductsFilter filter) {
        try {
            return productImportXlsParser.importProducts(file, filter);
        } catch (IOException e) {
            log.error(e.getMessage());
            return null;
        }
    }

    @Transactional
    public void priceAdjustmentByCategoryId(final BigDecimal coefficient, final Long categoryId, final Long branchId, boolean decrease) {
        Thread.startVirtualThread(() -> priceAdjustmentByCategoryId(coefficient, categoryId, branchId, getBranchCityId(branchId), decrease));
    }

    @Transactional
    public void priceAdjustmentByCategoryId(final BigDecimal coefficient, final Long categoryId, final Long branchId, final Long cityId, final boolean decrease) {
        final CategoryBranch categoryBranch = categoryBranchRepository.findById(new CategoryBranchId(categoryId, branchId))
                .orElseThrow(notFound(localizationService.getMessage("category")));

        int page = 0;
        Page<ProductBranch> productBranchesPage = productBranchRepository.findAllByBranchIdAndCityIdAndCategoryIdAndDeletedIsFalseOrderByProductId(branchId, cityId, categoryBranch.getCategoryId(), PageRequest.of(page++, 500));
        List<ProductPriceHistory> priceHistories = new ArrayList<>();
        while (!productBranchesPage.isEmpty()) {
            productBranchesPage.forEach(productBranch -> {
                BigDecimal price = productBranch.getSalesPrice();
                final ProductPriceHistory priceHistory = new ProductPriceHistory();
                priceHistory.setOldPrice(price);
                if (price != null) {
                    final BigDecimal coeff = price.multiply(coefficient);
                    price = decrease ? price.subtract(coeff) : price.add(coeff);
                }
                priceHistory.setNewPrice(price);
                priceHistory.setProductId(productBranch.getProductId());
                priceHistory.setBranchId(productBranch.getBranchId());
                priceHistory.setUpdatedById(getCurrentUserId());
                priceHistory.setUpdatedDateTime(LocalDateTime.now());
                priceHistories.add(priceHistory);
                productBranch.setSalesPrice(price);
            });
            productPriceHistoryRepository.saveAll(priceHistories);
            productBranchRepository.saveAll(productBranchesPage);
            productBranchesPage = productBranchRepository.findAllByBranchIdAndCityIdAndCategoryIdAndDeletedIsFalseOrderByProductId(branchId, cityId, categoryBranch.getCategoryId(), PageRequest.of(page++, 500));
        }

        if (CollectionUtils.isNotEmpty(categoryBranch.getChildCategories())) {
            categoryBranch.getChildCategories()
                    .forEach(category -> priceAdjustmentByCategoryId(coefficient, category.getCategoryId(), branchId, cityId, decrease));
        }
    }

    @Transactional
    public void updateSettings(final CreditSettingsDto dto) {
        final Long cityId = getBranchCityId(dto.getBranchId());
        final ProductBranch productBranch = productBranchRepository.findById(new ProductBranchId(dto.getProductId(), dto.getBranchId(), cityId))
                .orElseThrow(notFound("product"));
        productBranch.setCreditEnabled(dto.isCreditEnabled());
        productBranch.setCreditSettings(dto.getCreditSettings());
        productBranch.setAdvanceEnabled(dto.isAdvanceEnabled());
        productBranch.setAdvanceSettings(dto.getAdvanceSettings());
        productBranchRepository.save(productBranch);
    }

    @Transactional
    public void deleteAllProductFromBranch(final Long branchId) {
        Branch branch = branchRepository.findById(branchId).orElseThrow(notFound(localizationService.getMessage("branch")));

        registerRepository.deleteByBranchId(branchId);
        balanceHistoryRepository.deleteAllByBranchId(branchId);

        fashionRegisterRepository.deleteAllByBranchId(branchId);
        fashionBalanceHistoryRepository.deleteAllByBranchId(branchId);

        incomeOfProductDetailRepository.deleteByBranchId(branchId);
        incomeOfProductRepository.deleteAllByToBranchId(branchId);

        stockAdjustmentItemRepository.deleteAllByBranchId(branchId);
        stockAdjustmentRepository.deleteAllByBranchId(branchId);

        fashionStockAdjustmentItemRepository.deleteAllByBranchId(branchId);

        productUnitRepository.deleteProductUnitByBranchId(branchId);
        productBranchRepository.deleteAllByBranchIdAndCityId(branchId, branch.getCityKeyId());
        productPriceHistoryRepository.deleteAllByBranchId(branchId);

        categoryBranchRepository.deleteByBranchId(branchId);

        productForMarkingRepository.deleteAllByBranchIdAndStatusNotIn(branchId, List.of(MarkingStatus.SOLD, MarkingStatus.OPENED));
    }

    public Page<ProductListRecord> pageCabinet(final ProductBranchFilter filter) {
        if (filter.getBranchId() == null) {
            filter.setBranchId(SecurityUtils.getCurrentBranchId());
        }
        filter.setCityId(getBranchCityId(filter.getBranchId()));
        return productBranchRepository.pageCabinet(filter);
    }

    public List<ProductLookupRecord> lookup(final ProductBranchFilter filter) {
        if (filter.getBranchId() == null) {
            filter.setBranchId(SecurityUtils.getCurrentBranchId());
        }
        filter.setCityId(getBranchCityId(filter.getBranchId()));
        return productBranchRepository.lookup(filter);
    }

    public ProductDetailDto findById(final Long id, Long branchId) {
        branchId = branchId != null ? branchId : getCurrentBranchId();
        ProductBranch productBranch = productBranchRepository.findById(new ProductBranchId(id, branchId, getBranchCityId(branchId)))
                .orElseThrow(notFound(localizationService.getMessage("product")));
        return new ProductDetailDto(productBranch);
    }

    @Transactional
    public void updatePrice(final UpdateProductPriceRecord dto) {
        final Long branchId = dto.branchId() != null ? dto.branchId() : getCurrentBranchId();
        ProductBranch productBranch = productBranchRepository.findById(new ProductBranchId(dto.id(), branchId, getBranchCityId(branchId)))
                .orElseThrow(notFound(localizationService.getMessage("product")));
        if (setPrice(dto.salesPrice(), productBranch)) {
            productBranchRepository.save(productBranch);
        }
    }

    public void createHomeServiceProduct(Long branchId) {
        Branch branch = branchRepository.findById(branchId).orElseThrow(notFound(localizationService.getMessage("branch")));
        Category category = categoryRepository.findAllByCompanyType(CompanyType.HOME_SERVICE).stream().findFirst()
                .orElseThrow(notFound(localizationService.getMessage("category")));
        CategoryBranch categoryBranch = new CategoryBranch();
        categoryBranch.setBranchId(branchId);
        categoryBranch.setCategoryId(category.getId());
        categoryBranch.setName(category.getName());
        categoryBranchRepository.save(categoryBranch);
        ProductBranch productBranch = new ProductBranch();
        productBranch.setProductId(idGeneratorService.generateId());
        productBranch.setBranchId(branchId);
        productBranch.setCityId(branch.getCityKeyId());
        productBranch.setCategoryId(categoryBranch.getCategoryId());
        productBranch.setUnitId(30L);
        productBranch.setName("Кўп квартирали турар-жой биноларининг умумий мулкини сақлаш учун тўлов (умумий мулкни сақлаш бўйича ХУЖМ хизматлари)");
        productBranch.setBarcode(RandomStringUtils.randomNumeric(5));
        productBranch.setVatBarcode("09908001001000000");
        productBranch.setSalesPrice(BigDecimal.ONE);
        productBranch.setFavorite(true);
        MxikPackage defaultPackage = new MxikPackage();
        defaultPackage.setCode(1500142);
        productBranch.setDefaultPackage(defaultPackage);
        productBranch.setOrigin((short) 3);
        productBranchRepository.save(productBranch);
    }

    @Transactional
    public void updateProductPricesByIncome(Long id) {
        FashionIncomeOfProduct incomeOfProduct = fashionIncomeOfProductRepository.findById(id)
                .orElseThrow(notFound(localizationService.getMessage("income")));
        incomeOfProduct.getDetails().forEach(detail -> {
            if (detail.getProductItemId() == null && detail.getProduct() != null) {
                setPriceExisting(detail.getSalesPrice(), detail.getProductId(), detail.getBranchId());
            } else if (detail.getProductItem() != null && detail.getProductItem().getColorGroup() != null) {
                ProductItem productItem = detail.getProductItem();
                ProductColorGroup colorGroup = productItem.getColorGroup();
                if (!Objects.equals(colorGroup.getSalesPrice(), detail.getSalesPrice())) {
                    colorGroup.setSalesPrice(detail.getSalesPrice());
                    productColorGroupRepository.save(colorGroup);
                }
                if (!Objects.equals(productItem.getSalesPrice(), detail.getSalesPrice())) {
                    productItem.setSalesPrice(detail.getSalesPrice());
                    productItemRepository.save(productItem);
                }
            }
        });
    }

    public void updateProductHasExcise(Boolean exciseEnable) {
        List<Branch> branches = branchRepository.findAllByCompanyIdAndFiscalIsTrueAndDeletedIsFalse(getCurrentCompanyId());
        log.info("BRANCH SIZE: {}", branches.size());
        Map<String, Excise> exciseMap = exciseRepository.findAll().stream().collect(Collectors.toMap(Excise::getMxik, Function.identity()));
        branches.forEach(branch -> {
            List<ProductBranch> products = productBranchRepository.findAllByBranchIdAndCityIdAndVatBarcodeInAndDeletedIsFalse(branch.getId(), branch.getCityId(), exciseMap.keySet());
            log.info("PRODUCTS QUANTITY: {}", products.size());
            if (Boolean.FALSE.equals(exciseEnable)) {
                products.forEach(product -> {
                    product.setHasExcise(false);
                    product.setExciseAmount(null);
                });
            } else {
                products.forEach(productBranch -> {
                    if (exciseMap.containsKey(productBranch.getVatBarcode())) {
                        Excise excise = exciseMap.get(productBranch.getVatBarcode());
                        productBranch.setHasExcise(true);
                        productBranch.setExciseAmount(excise.getPrice());
                    }
                });
            }
            productBranchRepository.saveAll(products);
        });
    }

    public PageDto<TerminalProductDto> findAllByFilter(TerminalProductFilter filter) {
        if (filter.getBranchId() == null) {
            filter.setBranchId(getCurrentBranchId());
        }
        filter.setCityId(getBranchCityId(filter.getBranchId()));
        return productBranchRepository.findAllByFilter(filter);
    }
}
