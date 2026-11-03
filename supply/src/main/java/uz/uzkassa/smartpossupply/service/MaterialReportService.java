package uz.uzkassa.smartpossupply.service;

import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import uz.uzkassa.smartpossupply.domain.MaterialReport;
import uz.uzkassa.smartpossupply.domain.PackageQuantity;
import uz.uzkassa.smartpossupply.domain.product.Product;
import uz.uzkassa.smartpossupply.domain.stock.StockProduct;
import uz.uzkassa.smartpossupply.domain.warehouse.Warehouse;
import uz.uzkassa.smartpossupply.domain.warehouse.WarehouseProduct;
import uz.uzkassa.smartpossupply.dto.clickhouse.MaterialReportClickhouseDTO;
import uz.uzkassa.smartpossupply.dto.materialreport.MaterialReportDTO;
import uz.uzkassa.smartpossupply.dto.payload.CommonPayload;
import uz.uzkassa.smartpossupply.dto.payload.Payload;
import uz.uzkassa.smartpossupply.enums.PackageType;
import uz.uzkassa.smartpossupply.enums.StockStatus;
import uz.uzkassa.smartpossupply.enums.TransferType;
import uz.uzkassa.smartpossupply.filters.MaterialReportFilter;
import uz.uzkassa.smartpossupply.rabbitmq.producer.RabbitMqMaterialReportProducer;
import uz.uzkassa.smartpossupply.repository.MaterialReportRepository;
import uz.uzkassa.smartpossupply.repository.PackageQuantityRepository;
import uz.uzkassa.smartpossupply.repository.StockProductRepository;
import uz.uzkassa.smartpossupply.repository.WarehouseProductRepository;
import uz.uzkassa.smartpossupply.repository.WarehouseRepository;
import uz.uzkassa.smartpossupply.repository.clickhouse.ClickhouseMaterialReportRepository;
import uz.uzkassa.smartpossupply.utils.SecurityUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static lombok.AccessLevel.PRIVATE;

/**
 * Created time: 08.05.2025 16:44
 */

@Service
@Slf4j
@Transactional(readOnly = true)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class MaterialReportService extends BaseService {

    StockProductRepository stockProductRepository;
    WarehouseProductRepository warehouseProductRepository;
    ClickhouseMaterialReportRepository clickhouseMaterialReportRepository;
    WarehouseRepository warehouseRepository;
    MaterialReportRepository materialReportRepository;
    PackageQuantityRepository packageQuantityRepository;
    RabbitMqMaterialReportProducer rabbitMqMaterialReportProducer;

    @Transactional
    public void create(Long warehouseProductId) {
        WarehouseProduct warehouseProduct = warehouseProductRepository.findByIdForMaterialReport(warehouseProductId).orElseThrow(validationService.entityNotFoundExceptionThrow(WarehouseProduct.class.getSimpleName(), "id", warehouseProductId));
        Product product = warehouseProduct.getProduct();
        Warehouse warehouse = warehouseProduct.getWarehouse();

        LocalDate date = LocalDate.now().minusDays(1);
        LocalDateTime fromDate = date.atStartOfDay();
        LocalDateTime toDate = date.atTime(LocalTime.MAX);

        MaterialReportClickhouseDTO materialReportClickhouseDTO = new MaterialReportClickhouseDTO();
        materialReportClickhouseDTO.setProductId(product.getId());
        materialReportClickhouseDTO.setProductName(product.getName());
        materialReportClickhouseDTO.setProductBarcode(product.getBarcode());
        materialReportClickhouseDTO.setBranchId(warehouse.getBranchId());
        materialReportClickhouseDTO.setBranchName(warehouse.getBranch().getName());
        materialReportClickhouseDTO.setWarehouseId(warehouse.getId());
        materialReportClickhouseDTO.setWarehouseName(warehouse.getName());
        materialReportClickhouseDTO.setQty(warehouseProduct.getQty());
        materialReportClickhouseDTO.setPrice(warehouseProduct.getQty().multiply(Optional.ofNullable(product.getPrice()).orElse(BigDecimal.ZERO)));
        materialReportClickhouseDTO.setDate(date);

        BigDecimal stockInQty = BigDecimal.ZERO;
        BigDecimal stockInPrice = BigDecimal.ZERO;
        int stockInPalletPackageQty = 0;
        int stockInBlockPackageQty = 0;
        int stockInBoxPackageQty = 0;

        for (StockProduct stockProduct : stockProductRepository.findAllByStock(product.getId(), StockStatus.COMPLETED, TransferType.IN, fromDate, toDate)) {
            stockInQty = stockInQty.add(stockProduct.getQty());
            stockInPrice = stockInPrice.add(stockProduct.getTotalSellingPrice());

            List<PackageQuantity> packageQuantities = packageQuantityRepository.findById(stockProduct.getCompositeId()).filter(packageQuantity -> packageQuantity.getPackageType() != null).stream().toList();
            for (PackageQuantity packageQuantity : packageQuantities) {
                if (PackageType.PALLET == packageQuantity.getPackageType()) {
                    stockInPalletPackageQty = stockInPalletPackageQty + packageQuantity.getQuantity();
                } else if (PackageType.BLOCK == packageQuantity.getPackageType()) {
                    stockInBlockPackageQty = stockInBlockPackageQty + packageQuantity.getQuantity();
                } else if (PackageType.BOX == packageQuantity.getPackageType()) {
                    stockInBoxPackageQty = stockInBoxPackageQty + packageQuantity.getQuantity();
                }
            }
        }

        BigDecimal stockOutQty = BigDecimal.ZERO;
        BigDecimal stockOutPrice = BigDecimal.ZERO;
        int stockOutPalletPackageQty = 0;
        int stockOutBlockPackageQty = 0;
        int stockOutBoxPackageQty = 0;

        for (StockProduct stockProduct : stockProductRepository.findAllByStock(product.getId(), StockStatus.COMPLETED, TransferType.OUT, fromDate, toDate)) {
            stockOutQty = stockOutQty.add(stockProduct.getQty());
            stockOutPrice = stockOutPrice.add(stockProduct.getTotalSellingPrice());

            List<PackageQuantity> packageQuantities = packageQuantityRepository.findById(stockProduct.getCompositeId()).filter(packageQuantity -> packageQuantity.getPackageType() != null).stream().toList();
            for (PackageQuantity packageQuantity : packageQuantities) {
                if (PackageType.PALLET == packageQuantity.getPackageType()) {
                    stockOutPalletPackageQty = stockOutPalletPackageQty + packageQuantity.getQuantity();
                } else if (PackageType.BLOCK == packageQuantity.getPackageType()) {
                    stockOutBlockPackageQty = stockOutBlockPackageQty + packageQuantity.getQuantity();
                } else if (PackageType.BOX == packageQuantity.getPackageType()) {
                    stockOutBoxPackageQty = stockOutBoxPackageQty + packageQuantity.getQuantity();
                }
            }
        }


        materialReportClickhouseDTO.setStockInQty(stockInQty);
        materialReportClickhouseDTO.setStockInPrice(stockInPrice);
        materialReportClickhouseDTO.setStockOutQty(stockOutQty);
        materialReportClickhouseDTO.setStockOutPrice(stockOutPrice);

        materialReportClickhouseDTO.setStockInPalletPackageQty(stockInPalletPackageQty);
        materialReportClickhouseDTO.setStockInBlockPackageQty(stockInBlockPackageQty);
        materialReportClickhouseDTO.setStockInBoxPackageQty(stockInBoxPackageQty);
        materialReportClickhouseDTO.setStockOutPalletPackageQty(stockOutPalletPackageQty);
        materialReportClickhouseDTO.setStockOutBlockPackageQty(stockOutBlockPackageQty);
        materialReportClickhouseDTO.setStockOutBoxPackageQty(stockOutBoxPackageQty);

        MaterialReport materialReport = new MaterialReport();
        materialReport.setProductId(materialReportClickhouseDTO.getProductId());
        materialReport.setProductName(materialReportClickhouseDTO.getProductName());
        materialReport.setProductBarcode(materialReportClickhouseDTO.getProductBarcode());
        materialReport.setProductSerialNumber(materialReportClickhouseDTO.getProductSerialNumber());
        materialReport.setBranchId(materialReportClickhouseDTO.getBranchId());
        materialReport.setBranchName(materialReportClickhouseDTO.getBranchName());
        materialReport.setWarehouseId(materialReportClickhouseDTO.getWarehouseId());
        materialReport.setWarehouseName(materialReportClickhouseDTO.getWarehouseName());
        materialReport.setQty(materialReportClickhouseDTO.getQty());
        materialReport.setPrice(materialReportClickhouseDTO.getPrice());
        materialReport.setStockInQty(materialReportClickhouseDTO.getStockInQty());
        materialReport.setStockInPrice(materialReportClickhouseDTO.getStockInPrice());
        materialReport.setStockOutQty(materialReportClickhouseDTO.getStockOutQty());
        materialReport.setStockOutPrice(materialReportClickhouseDTO.getStockOutPrice());
        materialReport.setCreatedDate(materialReportClickhouseDTO.getDate());

        materialReport.setStockInPalletPackageQty(materialReportClickhouseDTO.getStockInPalletPackageQty());
        materialReport.setStockInBlockPackageQty(materialReportClickhouseDTO.getStockInBlockPackageQty());
        materialReport.setStockInBoxPackageQty(materialReportClickhouseDTO.getStockInBoxPackageQty());
        materialReport.setStockOutPalletPackageQty(materialReportClickhouseDTO.getStockOutPalletPackageQty());
        materialReport.setStockOutBlockPackageQty(materialReportClickhouseDTO.getStockOutBlockPackageQty());
        materialReport.setStockOutBoxPackageQty(materialReportClickhouseDTO.getStockOutBoxPackageQty());

        try {
            clickhouseMaterialReportRepository.insert(objectMapper.writeValueAsString(materialReportClickhouseDTO));
            materialReport.setClickhouseSync(true);
        } catch (Exception e) {
            log.error("Material report writing to the Clickhouse error: {}", e.getMessage());
        }
        materialReportRepository.save(materialReport);
    }

    @Transactional
    public void resync(List<Long> ids) {
        materialReportRepository.findAllById(ids).forEach(materialReport -> {
            MaterialReportClickhouseDTO materialReportClickhouseDTO = new MaterialReportClickhouseDTO();
            materialReportClickhouseDTO.setProductId(materialReport.getProductId());
            materialReportClickhouseDTO.setProductName(materialReport.getProductName());
            materialReportClickhouseDTO.setProductBarcode(materialReport.getProductBarcode());
            materialReportClickhouseDTO.setProductSerialNumber(materialReport.getProductSerialNumber());
            materialReportClickhouseDTO.setBranchId(materialReport.getBranchId());
            materialReportClickhouseDTO.setBranchName(materialReport.getBranchName());
            materialReportClickhouseDTO.setWarehouseId(materialReport.getId());
            materialReportClickhouseDTO.setWarehouseName(materialReport.getWarehouseName());
            materialReportClickhouseDTO.setQty(materialReport.getQty());
            materialReportClickhouseDTO.setPrice(materialReport.getPrice());
            materialReportClickhouseDTO.setStockInQty(materialReport.getStockInQty());
            materialReportClickhouseDTO.setStockInPrice(materialReport.getStockInPrice());
            materialReportClickhouseDTO.setStockOutQty(materialReport.getStockOutQty());
            materialReportClickhouseDTO.setStockOutPrice(materialReport.getStockOutPrice());
            materialReportClickhouseDTO.setDate(materialReport.getCreatedDate());

            materialReportClickhouseDTO.setStockInPalletPackageQty(materialReport.getStockInPalletPackageQty());
            materialReportClickhouseDTO.setStockInBlockPackageQty(materialReport.getStockInBlockPackageQty());
            materialReportClickhouseDTO.setStockInBoxPackageQty(materialReport.getStockInBoxPackageQty());

            materialReportClickhouseDTO.setStockOutPalletPackageQty(materialReport.getStockOutPalletPackageQty());
            materialReportClickhouseDTO.setStockOutBlockPackageQty(materialReport.getStockOutBlockPackageQty());
            materialReportClickhouseDTO.setStockOutBoxPackageQty(materialReport.getStockOutBoxPackageQty());

            try {
                clickhouseMaterialReportRepository.insert(objectMapper.writeValueAsString(materialReportClickhouseDTO));
                materialReport.setClickhouseSync(true);
            } catch (Exception e) {
                log.error("Material report writing to the Clickhouse error: {}", e.getMessage());
                log.error(materialReportClickhouseDTO.toString());
            }
            materialReportRepository.save(materialReport);
        });

    }

    public List<MaterialReportDTO> getReport(MaterialReportFilter filter) {
        if (filter.isAllBranches()) {
            filter.setBranchIds(branchRepository.getBranchIdByCompanyId(SecurityUtils.getCurrentCompanyId()));
            filter.setWarehouseIds(warehouseRepository.getAllIdsByCompanyId(SecurityUtils.getCurrentCompanyId()));
        }
        return clickhouseMaterialReportRepository.getReport(filter);
    }

    public void sync() {
        warehouseProductRepository.findAllIdsByDeletedIsFalse().forEach(id -> rabbitMqMaterialReportProducer.create(new Payload(id)));
    }

    public void resync() {
        List<Long> ids = materialReportRepository.findAllIdsToResync();
        if (!CollectionUtils.isEmpty(ids)) {
            Lists.partition(ids, 1000)
                    .forEach(ids1000 -> rabbitMqMaterialReportProducer.resync(new CommonPayload(ids)));
        }
    }
}
