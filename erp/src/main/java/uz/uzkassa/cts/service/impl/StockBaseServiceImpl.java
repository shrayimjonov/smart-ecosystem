package uz.uzkassa.cts.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.cts.constants.Constants;
import uz.uzkassa.cts.domain.Accessory;
import uz.uzkassa.cts.domain.Device;
import uz.uzkassa.cts.domain.FiscalModule;
import uz.uzkassa.cts.domain.Reference;
import uz.uzkassa.cts.domain.inventory.Product;
import uz.uzkassa.cts.dto.stock.StockItemDTO;
import uz.uzkassa.cts.dto.stockin.StockInItemLookUpDTO;
import uz.uzkassa.cts.enums.CommonStatus;
import uz.uzkassa.cts.enums.DeviceStatus;
import uz.uzkassa.cts.enums.ReferenceType;
import uz.uzkassa.cts.enums.StockItemType;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.filter.ProductFilter;
import uz.uzkassa.cts.interfaces.StockNumbering;
import uz.uzkassa.cts.numbering.NumberingContext;
import uz.uzkassa.cts.repository.*;
import uz.uzkassa.cts.service.StockBaseService;
import uz.uzkassa.cts.service.WarehouseItemService;
import uz.uzkassa.cts.validator.ValidationContext;

import java.util.List;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@Service
public abstract class StockBaseServiceImpl<T, ID> extends BaseServiceImpl<T, ID> implements StockBaseService<T, ID>, StockNumbering, Constants {

    protected final BaseRepository<T, ID> repository;
    protected final StockRepository stockRepository;
    protected final StockItemRepository stockItemRepository;
    protected final StockAdjustmentRepository stockAdjustmentRepository;
    protected final StockTransferRepository stockTransferRepository;
    protected final StockItemVariationOptionRepository stockItemVariationOptionRepository;
    protected final ProductRepository productRepository;
    protected final DeviceRepository deviceRepository;
    protected final FiscalModuleRepository fiscalModuleRepository;
    protected final AccessoryRepository accessoryRepository;
    protected final WarehouseItemService warehouseItemService;
    protected final NumberingContext numberingContext;
    protected final ValidationContext validationContext;
    protected final ReferenceRepository referenceRepository;

    public StockBaseServiceImpl(BaseRepository<T, ID> repository, StockRepository stockRepository, StockItemRepository stockItemRepository, StockAdjustmentRepository stockAdjustmentRepository, StockTransferRepository stockTransferRepository, StockItemVariationOptionRepository stockItemVariationOptionRepository, ProductRepository productRepository, DeviceRepository deviceRepository, FiscalModuleRepository fiscalModuleRepository, AccessoryRepository accessoryRepository, WarehouseItemService warehouseItemService, NumberingContext numberingContext, ValidationContext validationContext, ReferenceRepository referenceRepository) {
        super(repository);
        this.repository = repository;
        this.stockRepository = stockRepository;
        this.stockItemRepository = stockItemRepository;
        this.stockAdjustmentRepository = stockAdjustmentRepository;
        this.stockTransferRepository = stockTransferRepository;
        this.stockItemVariationOptionRepository = stockItemVariationOptionRepository;
        this.productRepository = productRepository;
        this.deviceRepository = deviceRepository;
        this.fiscalModuleRepository = fiscalModuleRepository;
        this.accessoryRepository = accessoryRepository;
        this.warehouseItemService = warehouseItemService;
        this.numberingContext = numberingContext;
        this.validationContext = validationContext;
        this.referenceRepository = referenceRepository;
    }

    @Override
    public StockItemDTO getItem(Long itemId, StockItemType stockItemType) {
        switch (stockItemType) {
            case PRODUCT -> {
                Product product = productRepository.findById(itemId).orElseThrow(validationService.entityNotFoundExceptionThrow(PRODUCT));
                StockItemDTO stockItemDTO = new StockItemDTO();
                stockItemDTO.setId(product.getId());
                stockItemDTO.setName(product.getName());
                stockItemDTO.setType(stockItemType);
                return stockItemDTO;
            }
            case DEVICE -> {
                Device device = deviceRepository.findById(itemId).orElseThrow(validationService.entityNotFoundExceptionThrow(DEVICE));
                StockItemDTO stockItemDTO = new StockItemDTO();
                stockItemDTO.setId(device.getId());
                stockItemDTO.setName(device.getName());
                stockItemDTO.setType(stockItemType);
                return stockItemDTO;
            }
            case FISCAL_MODULE -> {
                FiscalModule fiscalModule = fiscalModuleRepository.findById(itemId).orElseThrow(validationService.entityNotFoundExceptionThrow(FISCAL_MODULE));
                StockItemDTO stockItemDTO = new StockItemDTO();
                stockItemDTO.setId(fiscalModule.getId());
                stockItemDTO.setName(fiscalModule.getName());
                stockItemDTO.setType(stockItemType);
                return stockItemDTO;
            }
            case ACCESSORY -> {
                Accessory accessory = accessoryRepository.findById(itemId).orElseThrow(validationService.entityNotFoundExceptionThrow(ACCESSORIES));
                StockItemDTO stockItemDTO = new StockItemDTO();
                stockItemDTO.setId(accessory.getId());
                stockItemDTO.setName(accessory.getName());
                stockItemDTO.setType(stockItemType);
                return stockItemDTO;
            }
            default -> throw validationService.badRequestExceptionThrow(INVALID_INPUT, TRANSFER_TYPE).get();
        }
    }

    @Override
    public List<StockInItemLookUpDTO> getItemsLookUp(ProductFilter filter) {
        Reference unit = referenceRepository.findFirstByTypeAndCodeAndDeletedIsFalse(ReferenceType.UNIT_TYPE, "PC");
        switch (filter.getItemType()) {
            case PRODUCT -> {
                return productRepository.getResultList(filter).getList().stream().map(product -> {
                    StockInItemLookUpDTO stockInItemLookUpDTO = new StockInItemLookUpDTO();
                    stockInItemLookUpDTO.setId(product.getId());
                    stockInItemLookUpDTO.setName(product.getName());
                    if (product.getUnitId() != null) {
                        stockInItemLookUpDTO.setUnit(product.getUnit().toCommonDTO());
                    }
                    return stockInItemLookUpDTO;
                }).collect(Collectors.toList());
            }
            case DEVICE -> {
                BaseFilter deviceFilter = new BaseFilter();
                deviceFilter.setStatus(DeviceStatus.NOT_REGISTERED.getCode());
                return deviceRepository.getResultList(deviceFilter).getList().stream().map(device -> {
                    StockInItemLookUpDTO stockInItemLookUpDTO = new StockInItemLookUpDTO();
                    stockInItemLookUpDTO.setId(device.getId());
                    stockInItemLookUpDTO.setName(device.getName());
                    stockInItemLookUpDTO.setUnit(unit.toCommonDTO());
                    return stockInItemLookUpDTO;
                }).toList();
            }
            case FISCAL_MODULE -> {
                BaseFilter fiscalModuleFilter = new BaseFilter();
                fiscalModuleFilter.setStatus(DeviceStatus.NOT_REGISTERED.getCode());
                return fiscalModuleRepository.getResultList(fiscalModuleFilter).getList().stream().map(fiscalModule -> {
                    StockInItemLookUpDTO stockInItemLookUpDTO = new StockInItemLookUpDTO();
                    stockInItemLookUpDTO.setId(fiscalModule.getId());
                    stockInItemLookUpDTO.setName(fiscalModule.getName());
                    stockInItemLookUpDTO.setUnit(unit.toCommonDTO());
                    return stockInItemLookUpDTO;
                }).toList();
            }
            case ACCESSORY -> {
                BaseFilter accessoryFilter = new BaseFilter();
                accessoryFilter.setStatus(CommonStatus.ACTIVE.getCode());
                return accessoryRepository.getResultList(accessoryFilter).getList().stream().map(accessory -> {
                    StockInItemLookUpDTO stockInItemLookUpDTO = new StockInItemLookUpDTO();
                    stockInItemLookUpDTO.setId(accessory.getId());
                    stockInItemLookUpDTO.setName(accessory.getName());
                    stockInItemLookUpDTO.setUnit(unit.toCommonDTO());
                    return stockInItemLookUpDTO;
                }).toList();
            }
            default -> throw validationService.badRequestExceptionThrow(INVALID_INPUT, TRANSFER_TYPE).get();
        }
    }

}
