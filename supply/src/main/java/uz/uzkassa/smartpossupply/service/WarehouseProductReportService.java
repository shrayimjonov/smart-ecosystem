package uz.uzkassa.smartpossupply.service;

import kotlin.collections.ArrayDeque;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import uz.uzkassa.smartpossupply.domain.CompanyCommittentWarehouse;
import uz.uzkassa.smartpossupply.domain.FileUpload;
import uz.uzkassa.smartpossupply.domain.product.Product;
import uz.uzkassa.smartpossupply.domain.product.ProductContractWarehouse;
import uz.uzkassa.smartpossupply.domain.productserial.ProductSerialWarehouse;
import uz.uzkassa.smartpossupply.domain.stock.Stock;
import uz.uzkassa.smartpossupply.domain.warehouse.WarehouseExpeditor;
import uz.uzkassa.smartpossupply.domain.warehouse.WarehouseProduct;
import uz.uzkassa.smartpossupply.dto.base.ResultList;
import uz.uzkassa.smartpossupply.dto.product.ProductPhotoListDTO;
import uz.uzkassa.smartpossupply.dto.report.MobileWarehouseProductReportDTO;
import uz.uzkassa.smartpossupply.dto.stock.MobileStockProductDTO;
import uz.uzkassa.smartpossupply.enums.ReportType;
import uz.uzkassa.smartpossupply.filters.BalanceReportFilter;
import uz.uzkassa.smartpossupply.repository.CompanyCommittentWarehouseRepository;
import uz.uzkassa.smartpossupply.repository.ProductContractWarehouseRepository;
import uz.uzkassa.smartpossupply.repository.ProductSerialWarehouseRepository;
import uz.uzkassa.smartpossupply.repository.StockInRepository;
import uz.uzkassa.smartpossupply.repository.WarehouseExpeditorRepository;
import uz.uzkassa.smartpossupply.repository.WarehouseProductRepository;
import uz.uzkassa.smartpossupply.utils.SecurityUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static lombok.AccessLevel.PRIVATE;

@Service
@Transactional(readOnly = true)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Slf4j
public class WarehouseProductReportService extends BaseService {

    WarehouseProductRepository warehouseProductRepository;
    ProductSerialWarehouseRepository productSerialWarehouseRepository;
    ProductContractWarehouseRepository productContractWarehouseRepository;
    CompanyCommittentWarehouseRepository companyCommittentWarehouseRepository;
    StockInRepository stockInRepository;
    WarehouseExpeditorRepository warehouseExpeditorRepository;
    OrderService orderService;

    public Page<MobileWarehouseProductReportDTO> getReport(BalanceReportFilter filter) {
        if (isExpeditor()) {
            filter.setBranchId(SecurityUtils.getCurrentBranchId());
            WarehouseExpeditor warehouseExpeditor = warehouseExpeditorRepository.findFirstByExpeditorIdAndWarehouseIdOrderByTransferDateDesc(SecurityUtils.getCurrentUserId(), SecurityUtils.getCurrentWarehouseId()).orElse(null);
            if (warehouseExpeditor == null) {
                return new PageImpl<>(new ArrayDeque<>(), filter.getSortedPageable(), 0);
            } else {
                filter.setWarehouseId(warehouseExpeditor.getCarId());
            }
        }
        if (StringUtils.isEmpty(filter.getReportType())) {
            filter.setReportType(ReportType.BY_PRODUCT.getCode());
        }
        List<MobileWarehouseProductReportDTO> result;
        Long count = 0L;
        switch (ReportType.valueOf(filter.getReportType())) {
            case BY_SERIAL -> {
                ResultList<ProductSerialWarehouse> resultList = productSerialWarehouseRepository.getResultList(filter);
                result = resultList
                        .getList()
                        .stream()
                        .map(productSerialWarehouse -> {
                            MobileWarehouseProductReportDTO productSerialReportDTO = new MobileWarehouseProductReportDTO();
                            productSerialReportDTO.setId(productSerialWarehouse.getId());
                            productSerialReportDTO.setProduct(productSerialWarehouse.getProductSerial().getProduct().toProductCommonDTO());
                            productSerialReportDTO.setQty(productSerialWarehouse.getQty());
                            productSerialReportDTO.setBranch(productSerialWarehouse.getProductSerial().getProduct().getBranch().toCommonDTO());
                            productSerialReportDTO.setWarehouse(productSerialWarehouse.getWarehouse().toCommonDTO());
                            productSerialReportDTO.setProductSerial(productSerialWarehouse.getProductSerial().toCommonDTO());
                            productSerialReportDTO.setPrice(productSerialWarehouse.getProductSerial().getProduct().getPrice());
                            productSerialReportDTO.setTotalPrice(Optional.ofNullable(productSerialReportDTO.getPrice()).orElse(BigDecimal.ZERO).multiply(Optional.ofNullable(productSerialReportDTO.getQty()).orElse(BigDecimal.ZERO)));
                            productSerialReportDTO.setHasMark(productSerialWarehouse.getProductSerial().getProduct().isHasMark());
                            productSerialReportDTO.setPrice(productSerialWarehouse.getProductSerial().getProduct().getPrice());
                            productSerialReportDTO.setTotalPrice(Optional.ofNullable(productSerialWarehouse.getProductSerial().getProduct().getPrice()).orElse(BigDecimal.ZERO).multiply(Optional.ofNullable(productSerialWarehouse.getProductSerial().getQty()).orElse(BigDecimal.ZERO)));
                            productSerialReportDTO.setCapacity(productSerialWarehouse.getProductSerial().getProduct().getCapacity());
                            productSerialReportDTO.setProductType(productSerialWarehouse.getProductSerial().getProduct().getProductType());
                            productSerialReportDTO.setBoxBarcode(productSerialWarehouse.getProductSerial().getProduct().getBoxBarcode());
                            productSerialReportDTO.setBlockBarcode(productSerialWarehouse.getProductSerial().getProduct().getBlockBarcode());
                            productSerialReportDTO.setVat(productSerialWarehouse.getProductSerial().getProduct().getVat());
                            if (productSerialWarehouse.getProductSerial().getProduct().getParent() != null) {
                                productSerialReportDTO.setParent(productSerialWarehouse.getProductSerial().getProduct().getParent().toProductCommonDTO());
                                if (StringUtils.isEmpty(productSerialReportDTO.getBlockBarcode())) {
                                    productSerialReportDTO.setBlockBarcode(productSerialWarehouse.getProductSerial().getProduct().getParent().getBarcode());
                                }
                            }
                            if (!CollectionUtils.isEmpty(productSerialWarehouse.getProductSerial().getProduct().getPhotos())) {
                                productSerialWarehouse.getProductSerial().getProduct().getPhotos().forEach(productPhoto -> {
                                    ProductPhotoListDTO productPhotoListDTO = productPhoto.toDTO();
                                    if (!CollectionUtils.isEmpty(productPhoto.getPhoto().getChildren())) {
                                        for (FileUpload childPhoto : productPhoto.getPhoto().getChildren()) {
                                            productPhotoListDTO.addChild(new ProductPhotoListDTO(childPhoto.getId(), childPhoto.getUrl(), productPhoto.isMain(), childPhoto.getFileSizeType()));
                                        }
                                    }
                                    productSerialReportDTO.addPhoto(productPhotoListDTO);
                                });
                            }
                            return productSerialReportDTO;
                        }).toList();
                count = resultList.getCount();
            }
            case BY_CONTRACT -> {
                ResultList<ProductContractWarehouse> resultList = productContractWarehouseRepository.getResultList(filter);
                result = resultList
                        .getList()
                        .stream()
                        .map(productContractReport -> {
                            MobileWarehouseProductReportDTO productContractReportDTO = new MobileWarehouseProductReportDTO();
                            productContractReportDTO.setId(productContractReport.getId());
                            productContractReportDTO.setProduct(productContractReport.getProductContract().getProduct().toProductCommonDTO());
                            productContractReportDTO.setQty(productContractReport.getQty());
                            productContractReportDTO.setBranch(productContractReport.getProductContract().getProduct().getBranch().toCommonDTO());
                            productContractReportDTO.setWarehouse(productContractReport.getWarehouse().toCommonDTO());
                            productContractReportDTO.setProductContract(productContractReport.getProductContract().toCommonDTO());
                            productContractReportDTO.setPrice(Optional.ofNullable(productContractReport.getProductContract().getPrice()).orElse(BigDecimal.ZERO));
                            productContractReportDTO.setTotalPrice(Optional.ofNullable(productContractReportDTO.getPrice()).orElse(BigDecimal.ZERO).multiply(Optional.ofNullable(productContractReportDTO.getQty()).orElse(BigDecimal.ZERO)));
                            productContractReportDTO.setHasMark(productContractReport.getProductContract().getProduct().isHasMark());
                            productContractReportDTO.setPrice(productContractReport.getProductContract().getProduct().getPrice());
                            productContractReportDTO.setTotalPrice(Optional.ofNullable(productContractReport.getProductContract().getProduct().getPrice()).orElse(BigDecimal.ZERO).multiply(Optional.ofNullable(productContractReport.getQty()).orElse(BigDecimal.ZERO)));
                            productContractReportDTO.setCapacity(productContractReport.getProductContract().getProduct().getCapacity());
                            productContractReportDTO.setProductType(productContractReport.getProductContract().getProduct().getProductType());
                            productContractReportDTO.setBoxBarcode(productContractReport.getProductContract().getProduct().getBoxBarcode());
                            productContractReportDTO.setVat(productContractReport.getProductContract().getProduct().getVat());
                            productContractReportDTO.setBlockBarcode(productContractReport.getProductContract().getProduct().getBlockBarcode());
                            if (productContractReport.getProductContract().getProduct().getParent() != null) {
                                productContractReportDTO.setParent(productContractReport.getProductContract().getProduct().getParent().toProductCommonDTO());
                                if (StringUtils.isEmpty(productContractReportDTO.getBlockBarcode())) {
                                    productContractReportDTO.setBlockBarcode(productContractReport.getProductContract().getProduct().getParent().getBarcode());
                                }
                            }
                            if (!CollectionUtils.isEmpty(productContractReport.getProductContract().getProduct().getPhotos())) {
                                productContractReport.getProductContract().getProduct().getPhotos().forEach(productPhoto -> {
                                    ProductPhotoListDTO productPhotoListDTO = productPhoto.toDTO();
                                    if (!CollectionUtils.isEmpty(productPhoto.getPhoto().getChildren())) {
                                        for (FileUpload childPhoto : productPhoto.getPhoto().getChildren()) {
                                            productPhotoListDTO.addChild(new ProductPhotoListDTO(childPhoto.getId(), childPhoto.getUrl(), productPhoto.isMain(), childPhoto.getFileSizeType()));
                                        }
                                    }
                                    productContractReportDTO.addPhoto(productPhotoListDTO);
                                });
                            }
                            return productContractReportDTO;
                        }).toList();
                count = resultList.getCount();
            }
            case BY_COMITTENT -> {
                ResultList<CompanyCommittentWarehouse> resultList = companyCommittentWarehouseRepository.getResultList(filter);
                result = resultList
                        .getList()
                        .stream()
                        .map(companyCommittentWarehouse -> {
                            MobileWarehouseProductReportDTO companyCommittentReportDTO = new MobileWarehouseProductReportDTO();
                            companyCommittentReportDTO.setId(companyCommittentWarehouse.getId());
                            companyCommittentReportDTO.setProduct(companyCommittentWarehouse.getProduct().toProductCommonDTO());
                            companyCommittentReportDTO.setCommittent(companyCommittentWarehouse.getCompanyCommittent().getCommittent().toCommonBaseDTO());
                            companyCommittentReportDTO.setQty(companyCommittentWarehouse.getQty());
                            companyCommittentReportDTO.setBranch(companyCommittentWarehouse.getProduct().getBranch().toCommonDTO());
                            companyCommittentReportDTO.setWarehouse(companyCommittentWarehouse.getWarehouse().toCommonDTO());
                            companyCommittentReportDTO.setPrice(companyCommittentWarehouse.getProduct().getPrice());
                            companyCommittentReportDTO.setTotalPrice(Optional.ofNullable(companyCommittentReportDTO.getPrice()).orElse(BigDecimal.ZERO).multiply(Optional.ofNullable(companyCommittentReportDTO.getQty()).orElse(BigDecimal.ZERO)));
                            companyCommittentReportDTO.setHasMark(companyCommittentWarehouse.getProduct().isHasMark());
                            companyCommittentReportDTO.setPrice(companyCommittentWarehouse.getProduct().getPrice());
                            companyCommittentReportDTO.setTotalPrice(Optional.ofNullable(companyCommittentWarehouse.getProduct().getPrice()).orElse(BigDecimal.ZERO).multiply(Optional.ofNullable(companyCommittentWarehouse.getQty()).orElse(BigDecimal.ZERO)));
                            companyCommittentReportDTO.setCapacity(companyCommittentWarehouse.getProduct().getCapacity());
                            companyCommittentReportDTO.setProductType(companyCommittentWarehouse.getProduct().getProductType());
                            companyCommittentReportDTO.setVat(companyCommittentWarehouse.getProduct().getVat());
                            companyCommittentReportDTO.setBoxBarcode(companyCommittentWarehouse.getProduct().getBoxBarcode());
                            companyCommittentReportDTO.setBlockBarcode(companyCommittentWarehouse.getProduct().getBlockBarcode());
                            if (companyCommittentWarehouse.getProduct().getParent() != null) {
                                companyCommittentReportDTO.setParent(companyCommittentWarehouse.getProduct().getParent().toProductCommonDTO());
                                if (StringUtils.isEmpty(companyCommittentReportDTO.getBlockBarcode())) {
                                    companyCommittentReportDTO.setBlockBarcode(companyCommittentWarehouse.getProduct().getParent().getBarcode());
                                }
                            }
                            if (!CollectionUtils.isEmpty(companyCommittentWarehouse.getProduct().getPhotos())) {
                                companyCommittentWarehouse.getProduct().getPhotos().forEach(productPhoto -> {
                                    ProductPhotoListDTO productPhotoListDTO = productPhoto.toDTO();
                                    if (!CollectionUtils.isEmpty(productPhoto.getPhoto().getChildren())) {
                                        for (FileUpload childPhoto : productPhoto.getPhoto().getChildren()) {
                                            productPhotoListDTO.addChild(new ProductPhotoListDTO(childPhoto.getId(), childPhoto.getUrl(), productPhoto.isMain(), childPhoto.getFileSizeType()));
                                        }
                                    }
                                    companyCommittentReportDTO.addPhoto(productPhotoListDTO);
                                });
                            }
                            return companyCommittentReportDTO;
                        }).toList();
                count = resultList.getCount();

            }
            default -> {
                ResultList<WarehouseProduct> resultList = warehouseProductRepository.getResultList(filter);
                result = resultList
                        .getList()
                        .stream()
                        .map(warehouseProduct -> {
                            MobileWarehouseProductReportDTO warehouseProductReportDTO = new MobileWarehouseProductReportDTO();
                            warehouseProductReportDTO.setId(warehouseProduct.getId());
                            warehouseProductReportDTO.setProduct(warehouseProduct.getProduct().toProductCommonDTO());
                            warehouseProductReportDTO.setQty(warehouseProduct.getQty());
                            warehouseProductReportDTO.setBranch(warehouseProduct.getProduct().getBranch().toCommonDTO());
                            warehouseProductReportDTO.setWarehouse(warehouseProduct.getWarehouse().toCommonDTO());
                            warehouseProductReportDTO.setHasMark(warehouseProduct.getProduct().isHasMark());
                            warehouseProductReportDTO.setQtyInPackage(warehouseProduct.getProduct().getQtyInPackage());
                            warehouseProductReportDTO.setQtyInBox(warehouseProduct.getProduct().getQtyInBox());
                            warehouseProductReportDTO.setQtyInPallet(warehouseProduct.getProduct().getQtyInPallet());
                            warehouseProductReportDTO.setPrice(warehouseProduct.getProduct().getPrice());
                            warehouseProductReportDTO.setTotalPrice(Optional.ofNullable(warehouseProduct.getProduct().getPrice()).orElse(BigDecimal.ZERO).multiply(Optional.ofNullable(warehouseProduct.getQty()).orElse(BigDecimal.ZERO)));
                            warehouseProductReportDTO.setCapacity(warehouseProduct.getProduct().getCapacity());
                            warehouseProductReportDTO.setProductType(warehouseProduct.getProduct().getProductType());
                            warehouseProductReportDTO.setBoxBarcode(warehouseProduct.getProduct().getBoxBarcode());
                            warehouseProductReportDTO.setVat(warehouseProduct.getProduct().getVat());
                            warehouseProductReportDTO.setBlockBarcode(warehouseProduct.getProduct().getBlockBarcode());
                            warehouseProductReportDTO.setQtyInOrders(orderService.checkQtyInOrders(warehouseProduct.getProductId(), null, warehouseProduct.getWarehouseId()));
                            if (warehouseProduct.getProduct().getParent() != null) {
                                warehouseProductReportDTO.setParent(warehouseProduct.getProduct().getParent().toProductCommonDTO());
                                if (StringUtils.isEmpty(warehouseProductReportDTO.getBlockBarcode())) {
                                    warehouseProductReportDTO.setBlockBarcode(warehouseProduct.getProduct().getParent().getBarcode());
                                }
                            }
                            if (!CollectionUtils.isEmpty(warehouseProduct.getProduct().getPhotos())) {
                                warehouseProduct.getProduct().getPhotos().forEach(productPhoto -> {
                                    ProductPhotoListDTO productPhotoListDTO = productPhoto.toDTO();
                                    if (!CollectionUtils.isEmpty(productPhoto.getPhoto().getChildren())) {
                                        for (FileUpload childPhoto : productPhoto.getPhoto().getChildren()) {
                                            productPhotoListDTO.addChild(new ProductPhotoListDTO(childPhoto.getId(), childPhoto.getUrl(), productPhoto.isMain(), childPhoto.getFileSizeType()));
                                        }
                                    }
                                    warehouseProductReportDTO.addPhoto(productPhotoListDTO);
                                });
                            }
                            return warehouseProductReportDTO;
                        }).toList();
            }
        }

        return new PageImpl<>(result, filter.getSortedPageable(), count);
    }

    public List<MobileWarehouseProductReportDTO> getReportList(BalanceReportFilter filter) {
        if (isExpeditor()) {
            filter.setBranchId(SecurityUtils.getCurrentBranchId());
            WarehouseExpeditor warehouseExpeditor = warehouseExpeditorRepository.findFirstByExpeditorIdAndWarehouseIdOrderByTransferDateDesc(SecurityUtils.getCurrentUserId(), SecurityUtils.getCurrentWarehouseId()).orElse(null);
            if (warehouseExpeditor == null) {
                return new ArrayList<>();
            } else {
                filter.setWarehouseId(warehouseExpeditor.getCarId());
            }
        }
        if (StringUtils.isEmpty(filter.getReportType())) {
            filter.setReportType(ReportType.BY_PRODUCT.getCode());
        }
        List<MobileWarehouseProductReportDTO> result;
        switch (ReportType.valueOf(filter.getReportType())) {
            case BY_SERIAL -> {
                ResultList<ProductSerialWarehouse> resultList = productSerialWarehouseRepository.getResultList(filter);
                result = resultList
                        .getList()
                        .stream()
                        .map(productSerialWarehouse -> {
                            MobileWarehouseProductReportDTO productSerialReportDTO = new MobileWarehouseProductReportDTO();
                            productSerialReportDTO.setId(productSerialWarehouse.getId());
                            productSerialReportDTO.setProduct(productSerialWarehouse.getProductSerial().getProduct().toProductCommonDTO());
                            productSerialReportDTO.setQty(productSerialWarehouse.getQty());
                            productSerialReportDTO.setBranch(productSerialWarehouse.getProductSerial().getProduct().getBranch().toCommonDTO());
                            productSerialReportDTO.setWarehouse(productSerialWarehouse.getWarehouse().toCommonDTO());
                            productSerialReportDTO.setProductSerial(productSerialWarehouse.getProductSerial().toCommonDTO());
                            productSerialReportDTO.setPrice(productSerialWarehouse.getProductSerial().getProduct().getPrice());
                            productSerialReportDTO.setTotalPrice(Optional.ofNullable(productSerialReportDTO.getPrice()).orElse(BigDecimal.ZERO).multiply(Optional.ofNullable(productSerialReportDTO.getQty()).orElse(BigDecimal.ZERO)));
                            productSerialReportDTO.setHasMark(productSerialWarehouse.getProductSerial().getProduct().isHasMark());
                            productSerialReportDTO.setPrice(productSerialWarehouse.getProductSerial().getProduct().getPrice());
                            productSerialReportDTO.setTotalPrice(Optional.ofNullable(productSerialWarehouse.getProductSerial().getProduct().getPrice()).orElse(BigDecimal.ZERO).multiply(Optional.ofNullable(productSerialWarehouse.getProductSerial().getQty()).orElse(BigDecimal.ZERO)));
                            productSerialReportDTO.setCapacity(productSerialWarehouse.getProductSerial().getProduct().getCapacity());
                            productSerialReportDTO.setProductType(productSerialWarehouse.getProductSerial().getProduct().getProductType());
                            productSerialReportDTO.setVat(productSerialWarehouse.getProductSerial().getProduct().getVat());
                            productSerialReportDTO.setBoxBarcode(productSerialWarehouse.getProductSerial().getProduct().getBoxBarcode());
                            productSerialReportDTO.setBlockBarcode(productSerialWarehouse.getProductSerial().getProduct().getBlockBarcode());
                            if (productSerialWarehouse.getProductSerial().getProduct().getParent() != null) {
                                productSerialReportDTO.setParent(productSerialWarehouse.getProductSerial().getProduct().getParent().toProductCommonDTO());
                                if (StringUtils.isEmpty(productSerialReportDTO.getBlockBarcode())) {
                                    productSerialReportDTO.setBlockBarcode(productSerialWarehouse.getProductSerial().getProduct().getParent().getBarcode());
                                }
                            }
                            if (!CollectionUtils.isEmpty(productSerialWarehouse.getProductSerial().getProduct().getPhotos())) {
                                productSerialWarehouse.getProductSerial().getProduct().getPhotos().forEach(productPhoto -> {
                                    ProductPhotoListDTO productPhotoListDTO = productPhoto.toDTO();
                                    if (!CollectionUtils.isEmpty(productPhoto.getPhoto().getChildren())) {
                                        for (FileUpload childPhoto : productPhoto.getPhoto().getChildren()) {
                                            productPhotoListDTO.addChild(new ProductPhotoListDTO(childPhoto.getId(), childPhoto.getUrl(), productPhoto.isMain(), childPhoto.getFileSizeType()));
                                        }
                                    }
                                    productSerialReportDTO.addPhoto(productPhotoListDTO);
                                });
                            }
                            return productSerialReportDTO;
                        }).toList();
            }
            case BY_CONTRACT -> {
                ResultList<ProductContractWarehouse> resultList = productContractWarehouseRepository.getResultList(filter);
                result = resultList
                        .getList()
                        .stream()
                        .map(productContractReport -> {
                            MobileWarehouseProductReportDTO productContractReportDTO = new MobileWarehouseProductReportDTO();
                            productContractReportDTO.setId(productContractReport.getId());
                            productContractReportDTO.setProduct(productContractReport.getProductContract().getProduct().toProductCommonDTO());
                            productContractReportDTO.setQty(productContractReport.getQty());
                            productContractReportDTO.setBranch(productContractReport.getProductContract().getProduct().getBranch().toCommonDTO());
                            productContractReportDTO.setWarehouse(productContractReport.getWarehouse().toCommonDTO());
                            productContractReportDTO.setProductContract(productContractReport.getProductContract().toCommonDTO());
                            productContractReportDTO.setPrice(Optional.ofNullable(productContractReport.getProductContract().getPrice()).orElse(BigDecimal.ZERO));
                            productContractReportDTO.setTotalPrice(Optional.ofNullable(productContractReportDTO.getPrice()).orElse(BigDecimal.ZERO).multiply(Optional.ofNullable(productContractReportDTO.getQty()).orElse(BigDecimal.ZERO)));
                            productContractReportDTO.setHasMark(productContractReport.getProductContract().getProduct().isHasMark());
                            productContractReportDTO.setPrice(productContractReport.getProductContract().getProduct().getPrice());
                            productContractReportDTO.setTotalPrice(Optional.ofNullable(productContractReport.getProductContract().getProduct().getPrice()).orElse(BigDecimal.ZERO).multiply(Optional.ofNullable(productContractReport.getQty()).orElse(BigDecimal.ZERO)));
                            productContractReportDTO.setCapacity(productContractReport.getProductContract().getProduct().getCapacity());
                            productContractReportDTO.setProductType(productContractReport.getProductContract().getProduct().getProductType());
                            productContractReportDTO.setVat(productContractReport.getProductContract().getProduct().getVat());
                            productContractReportDTO.setBoxBarcode(productContractReport.getProductContract().getProduct().getBoxBarcode());
                            productContractReportDTO.setBlockBarcode(productContractReport.getProductContract().getProduct().getBlockBarcode());
                            if (productContractReport.getProductContract().getProduct().getParent() != null) {
                                productContractReportDTO.setParent(productContractReport.getProductContract().getProduct().getParent().toProductCommonDTO());
                                if (StringUtils.isEmpty(productContractReportDTO.getBlockBarcode())) {
                                    productContractReportDTO.setBlockBarcode(productContractReport.getProductContract().getProduct().getParent().getBarcode());
                                }
                            }
                            if (!CollectionUtils.isEmpty(productContractReport.getProductContract().getProduct().getPhotos())) {
                                productContractReport.getProductContract().getProduct().getPhotos().forEach(productPhoto -> {
                                    ProductPhotoListDTO productPhotoListDTO = productPhoto.toDTO();
                                    if (!CollectionUtils.isEmpty(productPhoto.getPhoto().getChildren())) {
                                        for (FileUpload childPhoto : productPhoto.getPhoto().getChildren()) {
                                            productPhotoListDTO.addChild(new ProductPhotoListDTO(childPhoto.getId(), childPhoto.getUrl(), productPhoto.isMain(), childPhoto.getFileSizeType()));
                                        }
                                    }
                                    productContractReportDTO.addPhoto(productPhotoListDTO);
                                });
                            }
                            return productContractReportDTO;
                        }).toList();
            }
            case BY_COMITTENT -> {
                ResultList<CompanyCommittentWarehouse> resultList = companyCommittentWarehouseRepository.getResultList(filter);
                result = resultList
                        .getList()
                        .stream()
                        .map(companyCommittentWarehouse -> {
                            MobileWarehouseProductReportDTO companyCommittentReportDTO = new MobileWarehouseProductReportDTO();
                            companyCommittentReportDTO.setId(companyCommittentWarehouse.getId());
                            companyCommittentReportDTO.setProduct(companyCommittentWarehouse.getProduct().toProductCommonDTO());
                            companyCommittentReportDTO.setCommittent(companyCommittentWarehouse.getCompanyCommittent().getCommittent().toCommonBaseDTO());
                            companyCommittentReportDTO.setQty(companyCommittentWarehouse.getQty());
                            companyCommittentReportDTO.setBranch(companyCommittentWarehouse.getProduct().getBranch().toCommonDTO());
                            companyCommittentReportDTO.setWarehouse(companyCommittentWarehouse.getWarehouse().toCommonDTO());
                            companyCommittentReportDTO.setPrice(companyCommittentWarehouse.getProduct().getPrice());
                            companyCommittentReportDTO.setTotalPrice(Optional.ofNullable(companyCommittentReportDTO.getPrice()).orElse(BigDecimal.ZERO).multiply(Optional.ofNullable(companyCommittentReportDTO.getQty()).orElse(BigDecimal.ZERO)));
                            companyCommittentReportDTO.setHasMark(companyCommittentWarehouse.getProduct().isHasMark());
                            companyCommittentReportDTO.setPrice(companyCommittentWarehouse.getProduct().getPrice());
                            companyCommittentReportDTO.setTotalPrice(Optional.ofNullable(companyCommittentWarehouse.getProduct().getPrice()).orElse(BigDecimal.ZERO).multiply(Optional.ofNullable(companyCommittentWarehouse.getQty()).orElse(BigDecimal.ZERO)));
                            companyCommittentReportDTO.setCapacity(companyCommittentWarehouse.getProduct().getCapacity());
                            companyCommittentReportDTO.setProductType(companyCommittentWarehouse.getProduct().getProductType());
                            companyCommittentReportDTO.setVat(companyCommittentWarehouse.getProduct().getVat());
                            companyCommittentReportDTO.setBoxBarcode(companyCommittentWarehouse.getProduct().getBoxBarcode());
                            companyCommittentReportDTO.setBlockBarcode(companyCommittentWarehouse.getProduct().getBlockBarcode());
                            if (companyCommittentWarehouse.getProduct().getParent() != null) {
                                companyCommittentReportDTO.setParent(companyCommittentWarehouse.getProduct().getParent().toProductCommonDTO());
                                if (StringUtils.isEmpty(companyCommittentReportDTO.getBlockBarcode())) {
                                    companyCommittentReportDTO.setBlockBarcode(companyCommittentWarehouse.getProduct().getParent().getBarcode());
                                }
                            }
                            if (!CollectionUtils.isEmpty(companyCommittentWarehouse.getProduct().getPhotos())) {
                                companyCommittentWarehouse.getProduct().getPhotos().forEach(productPhoto -> {
                                    ProductPhotoListDTO productPhotoListDTO = productPhoto.toDTO();
                                    if (!CollectionUtils.isEmpty(productPhoto.getPhoto().getChildren())) {
                                        for (FileUpload childPhoto : productPhoto.getPhoto().getChildren()) {
                                            productPhotoListDTO.addChild(new ProductPhotoListDTO(childPhoto.getId(), childPhoto.getUrl(), productPhoto.isMain(), childPhoto.getFileSizeType()));
                                        }
                                    }
                                    companyCommittentReportDTO.addPhoto(productPhotoListDTO);
                                });
                            }
                            return companyCommittentReportDTO;
                        }).toList();

            }
            default -> {
                ResultList<WarehouseProduct> resultList = warehouseProductRepository.getResultList(filter);
                result = resultList
                        .getList()
                        .stream()
                        .map(warehouseProduct -> {
                            MobileWarehouseProductReportDTO warehouseProductReportDTO = new MobileWarehouseProductReportDTO();
                            warehouseProductReportDTO.setId(warehouseProduct.getId());
                            warehouseProductReportDTO.setProduct(warehouseProduct.getProduct().toProductCommonDTO());
                            warehouseProductReportDTO.setQty(warehouseProduct.getQty());
                            warehouseProductReportDTO.setBranch(warehouseProduct.getProduct().getBranch().toCommonDTO());
                            warehouseProductReportDTO.setWarehouse(warehouseProduct.getWarehouse().toCommonDTO());
                            warehouseProductReportDTO.setHasMark(warehouseProduct.getProduct().isHasMark());
                            warehouseProductReportDTO.setQtyInPackage(warehouseProduct.getProduct().getQtyInPackage());
                            warehouseProductReportDTO.setQtyInBox(warehouseProduct.getProduct().getQtyInBox());
                            warehouseProductReportDTO.setQtyInPallet(warehouseProduct.getProduct().getQtyInPallet());
                            warehouseProductReportDTO.setPrice(warehouseProduct.getProduct().getPrice());
                            warehouseProductReportDTO.setTotalPrice(Optional.ofNullable(warehouseProduct.getProduct().getPrice()).orElse(BigDecimal.ZERO).multiply(Optional.ofNullable(warehouseProduct.getQty()).orElse(BigDecimal.ZERO)));
                            warehouseProductReportDTO.setCapacity(warehouseProduct.getProduct().getCapacity());
                            warehouseProductReportDTO.setProductType(warehouseProduct.getProduct().getProductType());
                            warehouseProductReportDTO.setVat(warehouseProduct.getProduct().getVat());
                            warehouseProductReportDTO.setBrutto(warehouseProduct.getProduct().getWeightBrutto());
                            warehouseProductReportDTO.setNetto(warehouseProduct.getProduct().getWeightNetto());
                            warehouseProductReportDTO.setBoxBarcode(warehouseProduct.getProduct().getBoxBarcode());
                            warehouseProductReportDTO.setBlockBarcode(warehouseProduct.getProduct().getBlockBarcode());
                            if (warehouseProduct.getProduct().getParent() != null) {
                                warehouseProductReportDTO.setParent(warehouseProduct.getProduct().getParent().toProductCommonDTO());
                                if (StringUtils.isEmpty(warehouseProductReportDTO.getBlockBarcode())) {
                                    warehouseProductReportDTO.setBlockBarcode(warehouseProduct.getProduct().getParent().getBarcode());
                                }
                            }
                            if (!CollectionUtils.isEmpty(warehouseProduct.getProduct().getPhotos())) {
                                warehouseProduct.getProduct().getPhotos().forEach(productPhoto -> {
                                    ProductPhotoListDTO productPhotoListDTO = productPhoto.toDTO();
                                    if (!CollectionUtils.isEmpty(productPhoto.getPhoto().getChildren())) {
                                        for (FileUpload childPhoto : productPhoto.getPhoto().getChildren()) {
                                            productPhotoListDTO.addChild(new ProductPhotoListDTO(childPhoto.getId(), childPhoto.getUrl(), productPhoto.isMain(), childPhoto.getFileSizeType()));
                                        }
                                    }
                                    warehouseProductReportDTO.addPhoto(productPhotoListDTO);
                                });
                            }
                            return warehouseProductReportDTO;
                        }).toList();
            }
        }
        return result;
    }

    public BigDecimal getReportTotalQty(BalanceReportFilter filter) {
        if (StringUtils.isEmpty(filter.getReportType())) {
            filter.setReportType(ReportType.BY_PRODUCT.getCode());
        }
        return switch (ReportType.valueOf(filter.getReportType())) {
            case BY_SERIAL -> productSerialWarehouseRepository.getReportTotalQty(filter);
            case BY_CONTRACT -> productContractWarehouseRepository.getReportTotalQty(filter);
            case BY_COMITTENT -> companyCommittentWarehouseRepository.getReportTotalQty(filter);
            default -> warehouseProductRepository.getReportTotalQty(filter);
        };
    }

    public MobileStockProductDTO getStockProduct(Long stockId, Long productId) {
        return stockInRepository.getDetail(stockId).map(stock -> stock.getProducts()
                        .stream().
                        filter(stockProduct -> stockProduct.getProductId().equals(productId))
                        .map(stockProduct -> {
                            MobileStockProductDTO mobileStockProductDTO = new MobileStockProductDTO();
                            mobileStockProductDTO.setStock(stock.toCommonDTO());
                            mobileStockProductDTO.setStatus(stock.getStatus());
                            mobileStockProductDTO.setCustomer(stock.getCustomer().toCommonBaseDTO());
                            mobileStockProductDTO.setBranch(stock.getBranch().toCommonDTO());
                            mobileStockProductDTO.setWarehouse(stock.getWarehouse().toCommonDTO());
                            mobileStockProductDTO.setProduct(stockProduct.getProduct().toProductCommonDTO());
                            mobileStockProductDTO.setQty(stockProduct.getQty());
                            mobileStockProductDTO.setPrice(stockProduct.getPurchasePrice());
                            mobileStockProductDTO.setPriceWithoutVat(stockProduct.getTotalPurchasePriceWithoutVat());
                            mobileStockProductDTO.setPriceWithVat(stockProduct.getTotalPurchasePrice());
                            mobileStockProductDTO.setDiscountPercent(stockProduct.getMarkupPercent());
                            mobileStockProductDTO.setDiscountPrice(stockProduct.getMarkupPrice());
                            mobileStockProductDTO.setVat(stockProduct.getProduct().getVat());
                            mobileStockProductDTO.setSellingPrice(stockProduct.getTotalSellingPrice());
                            if (stockProduct.getProductSerial() != null) {
                                mobileStockProductDTO.setSerial(stockProduct.getProductSerial().toCommonDTO());
                            }
                            if (stockProduct.getProductContract() != null) {
                                mobileStockProductDTO.setProductContract(stockProduct.getProductContract().toCommonDTO());
                            }
                            if (stockProduct.getCompanyCommittent() != null && stockProduct.getCompanyCommittent().getCommittent() != null) {
                                mobileStockProductDTO.setCommittent(stockProduct.getCompanyCommittent().getCommittent().toCommonBaseDTO());
                            }
                            return mobileStockProductDTO;
                        }).findFirst().orElseThrow(validationService.entityNotFoundExceptionThrow(Product.class.getSimpleName(), "id", productId)))
                .orElseThrow(validationService.entityNotFoundExceptionThrow(Stock.class.getSimpleName(), "id", stockId));
    }

}
