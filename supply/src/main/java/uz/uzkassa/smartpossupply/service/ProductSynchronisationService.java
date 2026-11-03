package uz.uzkassa.smartpossupply.service;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.smartpossupply.constants.Constants;
import uz.uzkassa.smartpossupply.domain.company.Company;
import uz.uzkassa.smartpossupply.domain.product.Product;
import uz.uzkassa.smartpossupply.dto.payload.ProductPackageSyncPayload;
import uz.uzkassa.smartpossupply.dto.payload.SyncTasnifCatalogByProductPayload;
import uz.uzkassa.smartpossupply.dto.payload.SyncTasnifCatalogPayload;
import uz.uzkassa.smartpossupply.dto.product.SyncCatalogProductDTO;
import uz.uzkassa.smartpossupply.dto.soliq.MxikDTO;
import uz.uzkassa.smartpossupply.dto.soliq.ProductPackageSyncDTO;
import uz.uzkassa.smartpossupply.enums.CommonStatus;
import uz.uzkassa.smartpossupply.integration.TasnifClient;
import uz.uzkassa.smartpossupply.rabbitmq.producer.RabbitMqTasnifCatalogProducer;
import uz.uzkassa.smartpossupply.repository.FacturaProductRepository;
import uz.uzkassa.smartpossupply.repository.ProductRepository;
import uz.uzkassa.smartpossupply.utils.SecurityUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static lombok.AccessLevel.PRIVATE;

@Service
@Transactional(readOnly = true)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Slf4j
public class ProductSynchronisationService extends BaseService implements Constants {

    ProductRepository productRepository;
    FacturaProductRepository facturaProductRepository;
    TasnifClient tasnifClient;
    RabbitMqTasnifCatalogProducer rabbitMqTasnifCatalogProducer;


    public List<MxikDTO> getCompanyCatalog(String tin) {
        return tasnifClient.getCompanyCatalog((Optional.ofNullable(tin).orElse(getCurrentCompanyTin())));
    }

    @Transactional
    public void syncCompanyAllCatalog(String tin) {
        Company company = tin != null ? companyRepository.findByTinAndDeletedIsFalse(tin).orElse(companyRepository.findFirstByTinAndDeletedIsFalse(tin).orElseThrow(validationService.entityNotFoundExceptionThrow(Company.class.getSimpleName(), "tin", tin))) : getCurrentCompany();

        String companyTin = company.getTin();
        Long userId = SecurityUtils.getCurrentUserId();

        List<Product> products = productRepository.findAllByCompanyIdAndVatBarcodeIsNotNullAndDeletedIsFalse(company.getId());
        products.forEach(product -> rabbitMqTasnifCatalogProducer.syncTasnifCatalogByProductQueue(new SyncTasnifCatalogByProductPayload(companyTin, userId, product.getId())));
    }

    @Transactional
    public void syncTasnifCatalogByProduct(SyncTasnifCatalogByProductPayload tasnifCatalogByProductPayload) {
        productRepository.getProductByIdWithCompany(tasnifCatalogByProductPayload.getProductId()).ifPresent(product -> {
            try {
                String tin = tasnifCatalogByProductPayload.getTin() != null ? tasnifCatalogByProductPayload.getTin() : product.getCompany().getTin();
                List<MxikDTO> mxikDTOList = tasnifClient.getCompanyCatalogByProduct(tin, product.getVatBarcode());
                if (!CollectionUtils.isEmpty(mxikDTOList)) {
                    MxikDTO mxikDTO = mxikDTOList.get(0);
                    if (!CollectionUtils.isEmpty(mxikDTO.getPackageNames())) {
                        product.setTasnifId(mxikDTO.getPkey());
                        product.setCatalogName(mxikDTO.getName());
                        product.setUpdatedDate(LocalDateTime.now());
                        if (tasnifCatalogByProductPayload.getUserId() != null) {
                            product.setUpdaterId(tasnifCatalogByProductPayload.getUserId());
                        }
                        productRepository.save(product);

                        if (tasnifCatalogByProductPayload.getFacturaProductId() != null) {
                            facturaProductRepository.findById(tasnifCatalogByProductPayload.getFacturaProductId()).ifPresent(facturaProduct -> {
                                if (product.getCatalogCode() != null && product.getCatalogCode().equals(facturaProduct.getCatalogCode())) {
                                    if (product.getCatalogName() != null) {
                                        facturaProduct.setCatalogName(product.getCatalogName());
                                        facturaProductRepository.save(facturaProduct);
                                    }
                                }
                            });
                        }
                    }
                }
            } catch (Exception e) {
                log.error("SyncTasnifCatalogByProductPayload :{}", tasnifCatalogByProductPayload);
                log.error(e.getMessage(), e);
            }
        });
    }

    @Transactional
    public void syncCompanyCatalogProducts(SyncCatalogProductDTO syncCatalogProductDTO) {
        List<Product> products = productRepository.findAllById(syncCatalogProductDTO.getIds());
        if (CollectionUtils.isEmpty(products)) {
            log.info("Products by given IDs are null");
            return;
        }

        String tin = getCurrentCompanyTin();
        Long userId = getCurrentUserId();
        products.forEach(product -> rabbitMqTasnifCatalogProducer.syncTasnifCatalogByProductQueue(new SyncTasnifCatalogByProductPayload(tin, userId, product.getId())));
    }

    @Transactional
    public void syncTasnifCatalog(SyncTasnifCatalogPayload syncTasnifCatalogPayload) {
        List<Product> products = productRepository.findAllByCompanyIdAndVatBarcodeIsNotNullAndDeletedIsFalse(syncTasnifCatalogPayload.getCompanyId());
        int i = 0;
        for (Product product : products) {
            rabbitMqTasnifCatalogProducer.syncTasnifCatalogByProductQueue(new SyncTasnifCatalogByProductPayload(syncTasnifCatalogPayload.getTin(), product.getUpdaterId(), product.getId()), i++);
        }
    }

    /**
     * Main method during creating product.
     * Be carefull while updating code.
     */
    public Product getProductByBarcodeAndVatBarcode(String barcode, String vatBarcode, Long branchId) {
        if (StringUtils.isNotEmpty(barcode) && StringUtils.isNotEmpty(vatBarcode)) {
            Product product = productRepository.findFirstByBarcodeAndVatBarcodeAndBranchIdAndDeletedIsFalse(barcode.trim(), vatBarcode.trim(), branchId).orElse(null);
            if (product != null) {
                return product;
            }
        }
        if (StringUtils.isNotEmpty(barcode)) {
            Product product = productRepository.findFirstByBarcodeAndBranchIdAndDeletedIsFalse(barcode.trim(), branchId).orElse(null);
            if (product != null) {
                return product;
            }
        }
        if (StringUtils.isNotEmpty(barcode) && barcode.startsWith("0")) {
            Product product = productRepository.findFirstByBarcodeAndBranchIdAndDeletedIsFalse(barcode.trim().substring(1), branchId).orElse(null);
            if (product != null) {
                return product;
            }
        }
        if (StringUtils.isNotEmpty(barcode) && !barcode.startsWith("0") && barcode.trim().length() == 12) {
            Product product = productRepository.findFirstByBarcodeAndBranchIdAndDeletedIsFalse("0".concat(barcode.trim()), branchId).orElse(null);
            if (product != null) {
                return product;
            }
        }
        if (StringUtils.isNotEmpty(barcode)) {
            return null;
        }
        if (StringUtils.isNotEmpty(vatBarcode)) {
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
    public void syncPackage(ProductPackageSyncPayload payload) {
        List<Product> products = productRepository.findAllByCompanyIdAndVatBarcodeIsNotNullAndDeletedIsFalse(payload.getCompanyId());
        this.syncPackage(products);
    }

    public void syncPackage(String tin) {
        companyRepository.findFirstByTinAndDeletedIsFalse(tin).ifPresent(company -> rabbitMqTasnifCatalogProducer.syncProductPackage(new ProductPackageSyncPayload(company.getId())));
    }

    private void syncPackage(List<Product> products) {
        for (Product product : products) {
            ProductPackageSyncDTO productPackageSyncDTO = tasnifClient.getProductPackageByCatalogCode(product.getVatBarcode());
            if (productPackageSyncDTO != null && !CollectionUtils.isEmpty(productPackageSyncDTO.getPackages())) {
                productPackageSyncDTO.getPackages().stream().filter(packageSyncDTO -> StringUtils.isEmpty(packageSyncDTO.getParentCode())).findFirst()
                        .ifPresent(packageSyncDTO -> {
                            product.setPackageCode(packageSyncDTO.getCode());
                            product.setPackageName(packageSyncDTO.getName());
                            productRepository.save(product);
                        });
            }
        }
    }
}
