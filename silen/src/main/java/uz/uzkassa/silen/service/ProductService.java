package uz.uzkassa.silen.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.ServletOutputStream;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.silen.domain.Organization;
import uz.uzkassa.silen.domain.Product;
import uz.uzkassa.silen.dto.*;
import uz.uzkassa.silen.dto.filter.ProductFilter;
import uz.uzkassa.silen.dto.invoice.*;
import uz.uzkassa.silen.dto.mq.*;
import uz.uzkassa.silen.enumeration.*;
import uz.uzkassa.silen.excel.ExportProductExcel;
import uz.uzkassa.silen.excel.ImportProductExcel;
import uz.uzkassa.silen.exceptions.BadRequestException;
import uz.uzkassa.silen.integration.FacturaClient;
import uz.uzkassa.silen.integration.TasnifClient;
import uz.uzkassa.silen.mapper.ProductMapper;
import uz.uzkassa.silen.repository.pg.AggregationRepository;
import uz.uzkassa.silen.repository.pg.AttributesRepository;
import uz.uzkassa.silen.repository.pg.ProductRepository;
import uz.uzkassa.silen.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service Implementation for managing {@link Product}.
 */
@Slf4j
@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class ProductService extends BaseService {
    ProductRepository productRepository;
    ProductMapper productMapper;
    AttributesRepository attributesRepository;
    AggregationRepository aggregationRepository;
    TasnifClient tasnifClient;
    FacturaClient facturaClient;
    ExportProductExcel exportProductExcel;
    ImportProductExcel importProductExcel;

    @Transactional
    public ProductDTO save(ProductDTO productDTO) {
        log.debug("Request to save Product : {}", productDTO);
        if (!isAgency()) {
            productDTO.setOrganizationId(getCurrentOrganizationId());
        }
        if (StringUtils.isNotEmpty(productDTO.getBarcode())) {
            Optional<Product> productOptional = productRepository.findFirstByBarcodeAndOrganizationIdAndDeletedIsFalse(productDTO.getBarcode(), productDTO.getOrganizationId());
            if (productOptional.isPresent()) {
                throw badRequest("Товар с таким баркодом уже существует").get();
            }
        }
        Product product = new Product();
        Integer maxCode = productRepository.findMaxCode().orElse(0);
        product.setCode(maxCode + 1);
//        if (isAgency()) {
        product.setStatus(ProductStatus.ACCEPTED);
//        } else {
//            product.setStatus(ProductStatus.PENDING);
//        }
        product.setExpiredInMonth(productDTO.getExpiredInMonth());
        product.setOrganizationId(productDTO.getOrganizationId());
        product.setName(productDTO.getName());
        product.setType(productDTO.getType());
        product.setShortName(productDTO.getShortName());
        product.setBarcode(productDTO.getBarcode());
        product.setParentBarcode(productDTO.getParentBarcode());
        product.setCatalogCode(productDTO.getCatalogCode());
        product.setCatalogName(productDTO.getCatalogName());
        product.setPackageCode(productDTO.getPackageCode());
        product.setPackageName(productDTO.getPackageName());
        product.setPrice(productDTO.getPrice());
        product.setExciseRate(productDTO.getExciseRate());
        product.setVatRate(productDTO.getVatRate());
        product.setCatalogPrice(Utils.calculateCatalogPrice(product));
        product.setQtyInAggregation(productDTO.getQtyInAggregation());
        product.setQtyInBlock(productDTO.getQtyInBlock());
        product.setBasePrice(Optional.ofNullable(productDTO.getBasePrice()).orElse(BigDecimal.ZERO));
        product.setProfitRate(Optional.ofNullable(productDTO.getProfitRate()).orElse(BigDecimal.ZERO));
        product.setWeightBrutto(productDTO.getWeightBrutto());
        product.setWeightNetto(productDTO.getWeightNetto());


        if (productDTO.getAttributes() != null) {
            product.getAttributes().clear();
            product = productRepository.saveAndFlush(product);
            for (ProductAttributesDTO attribute : productDTO.getAttributes()) {
                product.addAttribute(attribute, productDTO.getOrganizationId());
            }
            product = productRepository.save(product);

            List<AttributeValueDTO> productAttributes = attributesRepository.getAttributesValue(product.getId());
            StringBuilder nameBuilder = new StringBuilder();
            nameBuilder.append(product.getName()).append(" ");
            Iterator<AttributeValueDTO> iterator = productAttributes.listIterator();
            while (iterator.hasNext()) {
                AttributeValueDTO attributesValue = iterator.next();
                if (attributesValue.getPrefix() != null) nameBuilder.append(attributesValue.getPrefix()).append(" ");
                nameBuilder.append(attributesValue.getValue());
                if (attributesValue.getPostfix() != null) nameBuilder.append(attributesValue.getPostfix());
                if (iterator.hasNext()) {
                    nameBuilder.append(", ");
                } else nameBuilder.append(".");
            }
            product.setVisibleName(nameBuilder.toString());
        } else {
            product.setVisibleName(productDTO.getName());
        }
        productRepository.save(product);

        if (productDTO.getOrganizationId() != null) {
            //Send notification
            NotificationPayload payload = new NotificationPayload();
            payload.setOrganizationId(productDTO.getOrganizationId());
            payload.setType(NotificationTemplateType.PRODUCT_ADD);
            payload.addParams("productName", product.getVisibleName());
            rabbitMqProducer.sendNotification(payload);
        }

        if (ProductStatus.ACCEPTED.equals(product.getStatus())) {
            rabbitMqProducer.sendAddProduct(new Payload(product.getOrganizationId(), product.getId()));
        }
        return product.toDto();
    }

    @Transactional
    public void update(ProductDTO productDTO) {
        log.debug("Request to save Product : {}", productDTO);

        if (StringUtils.isNotEmpty(productDTO.getBarcode())) {
            Optional<Product> productOptional = productRepository.findFirstByBarcodeAndOrganizationIdAndDeletedIsFalse(productDTO.getBarcode(), productDTO.getOrganizationId());
            if (productOptional.isPresent() && (productDTO.getId() == null || !productDTO.getId().equals(productOptional.get().getId()))) {
                throw badRequest("Товар с таким баркодом уже существует").get();
            }
        }
        if (!isAgency()) {
            productDTO.setOrganizationId(getCurrentOrganizationId());
        }
        Product product = productRepository.findById(productDTO.getId()).orElseThrow(notFound());
        product.setOrganizationId(productDTO.getOrganizationId());
        product.setExpiredInMonth(productDTO.getExpiredInMonth());
        product.setName(productDTO.getName());
        product.setShortName(productDTO.getShortName());
        product.setBarcode(productDTO.getBarcode());
        product.setParentBarcode(productDTO.getParentBarcode());
        product.setType(productDTO.getType());
        product.setCatalogCode(productDTO.getCatalogCode());
        product.setCatalogName(productDTO.getCatalogName());
        product.setPackageCode(productDTO.getPackageCode());
        product.setPackageName(productDTO.getPackageName());
        product.setPrice(productDTO.getPrice());
        product.setExciseRate(productDTO.getExciseRate());
        product.setVatRate(productDTO.getVatRate());
        product.setCatalogPrice(Utils.calculateCatalogPrice(product));
        product.setQtyInAggregation(productDTO.getQtyInAggregation());
        product.setQtyInBlock(productDTO.getQtyInBlock());
        product.setBasePrice(Optional.ofNullable(productDTO.getBasePrice()).orElse(BigDecimal.ZERO));
        product.setProfitRate(Optional.ofNullable(productDTO.getProfitRate()).orElse(BigDecimal.ZERO));
        product.setWeightBrutto(productDTO.getWeightBrutto());
        product.setWeightNetto(productDTO.getWeightNetto());
        if (productDTO.getAttributes() != null) {
            product.getAttributes().clear();
            product = productRepository.saveAndFlush(product);
            for (ProductAttributesDTO attribute : productDTO.getAttributes()) {
                product.addAttribute(attribute, product.getOrganizationId());
            }
            product = productRepository.save(product);

            List<AttributeValueDTO> productAttributes = attributesRepository.getAttributesValue(product.getId());

            StringBuilder nameBuilder = new StringBuilder();
            nameBuilder.append(product.getName()).append(" ");
            Iterator<AttributeValueDTO> iterator = productAttributes.listIterator();
            while (iterator.hasNext()) {
                AttributeValueDTO attributesValue = iterator.next();
                if (attributesValue.getPrefix() != null) nameBuilder.append(attributesValue.getPrefix()).append(" ");
                nameBuilder.append(attributesValue.getValue());
                if (attributesValue.getPostfix() != null) nameBuilder.append(attributesValue.getPostfix());
                if (iterator.hasNext()) {
                    nameBuilder.append(", ");
                } else nameBuilder.append(".");
            }
            product.setVisibleName(nameBuilder.toString());
        }
    }

    @Transactional
    public void updateStatus(ProductStatusUpdateDTO dto) {
        log.debug("Request to update Product status : {}", dto);
        productRepository.findById(dto.getId()).ifPresent(product -> {
            product.setStatus(dto.getStatus());
            productRepository.save(product);
            if (ProductStatus.ACCEPTED.equals(product.getStatus())) {
                rabbitMqProducer.sendAddProduct(new Payload(product.getOrganizationId(), product.getId()));
            }
        });
    }

    public Page<ProductListDTO> findAll(ProductFilter filter) {
        return productRepository.findAllByFilter(filter).map(product -> {
            ProductListDTO productListDTO = new ProductListDTO();
            productListDTO.setId(product.getId());
            productListDTO.setName(product.getName());
            productListDTO.setBarcode(product.getBarcode());
            productListDTO.setParentBarcode(product.getParentBarcode());
            productListDTO.setType(product.getType());
            if (product.getType() != null) {
                productListDTO.setTypeName(product.getType().getNameRu());
            }
            productListDTO.setCatalogCode(product.getCatalogCode());
            productListDTO.setStatus(product.getStatus());
            if (product.getStatus() != null) {
                productListDTO.setStatusName(product.getStatus().getText());
            }
            if (isAgency()) {
                productListDTO.setOrganizationId(product.getId());
                productListDTO.setOrganizationTin(product.getOrganization().getTin());
                productListDTO.setOrganizationName(product.getOrganization().getName());
            }
            return productListDTO;
        });
    }

    public List<ProductSelectItem> getProductItems(ProductFilter filter) {
        return productRepository.findAllByFilter(filter)
                .map(product -> {
                    ProductSelectItem selectItem = new ProductSelectItem();
                    selectItem.setId(product.getId());
                    selectItem.setName(product.getVisibleName());
                    selectItem.setBarcode(product.getBarcode());
                    selectItem.setParentBarcode(product.getParentBarcode());
                    selectItem.setQtyInAggregation(product.getQtyInAggregation());
                    selectItem.setQtyInBlock(product.getQtyInBlock());
                    selectItem.setExpiredInMonth(product.getExpiredInMonth());
                    selectItem.setProductType(product.getType());
                    return selectItem;
                }).getContent();
    }

    public ProductDTO getOne(String id) {
        return productRepository.findById(id).map(productMapper::toDto).orElseThrow(notFound());
    }

    public Double getProductCapacityByBarcode(String barcode) {
        Product product = productRepository.getByBarcodeAndOrganizationIdAndDeletedIsFalse(barcode, getCurrentOrganizationId()).orElseThrow(notFound());
        return product.getCapacity();
    }

    @Transactional
    public void delete(String id) {
        if (aggregationRepository.existsAggregationByProductId(id)) {
            throw badRequest("Данный продукт невозможно удалить, т.к. используется на производстве").get();
        }
        productRepository.deleteById(id);
    }

    public List<ProductSelectCountItem> productCounts(ProductFilter filter) {
        return productRepository.getCounts(filter);
    }

    @Transactional
    public void updateCode() {
        int maxCode = productRepository.findMaxCode().orElse(0) + 1;
        List<Product> products = productRepository.findAllByCodeIsNull();
        for (Product p : products) {
            p.setCode(maxCode++);
        }
        productRepository.saveAll(products);
    }

    public void checkProductChanges(UserDTO syncUserDto) throws JsonProcessingException {
        ProductFilter filter = new ProductFilter();
        boolean isLast;
        do {
            Page<SelectItem> selectItemPage = productRepository.selectCatalogCodeItems(filter);

            List<String> pageList = selectItemPage.getContent().stream().map(SelectItem::getId).collect(Collectors.toList());
            MxikListDTO syncPackages = tasnifClient.syncPackages(pageList);
            //update packages
            updatePackages(syncPackages);
            filter.setPage(filter.getPage() + 1);
            isLast = selectItemPage.isLast();
        } while (!isLast);

        if (syncUserDto != null) {
            NotificationPayload payload = new NotificationPayload();
            payload.setType(NotificationTemplateType.SYNC_PACKAGES);
            payload.addParams("userName", syncUserDto.getLogin()).addParams("userId", syncUserDto.getId());
            payload.setUserId(syncUserDto.getId());
            payload.setOrganizationId(null);
            rabbitMqProducer.sendNotification(payload);
        }
    }

    public void updatePackages(MxikListDTO dto) {
        if (dto.getData() != null) {
            dto.getData().forEach(mxikListItemDTO -> {
                if (mxikListItemDTO.getInfo() != null) {
                    List<Product> products = productRepository.findAllByCatalogCodeEquals(mxikListItemDTO.getMxikCode());
                    products.forEach(product -> {
                        if (mxikListItemDTO.getInfo() != null) {
                            PackageDTO packageDTO = mxikListItemDTO.getInfo().getPackageNames().getFirst();
                            boolean update = false;
                            if (!Objects.equals(product.getCatalogName(), mxikListItemDTO.getInfo().getNameRu())) {
                                update = true;
                                product.setCatalogName(mxikListItemDTO.getInfo().getNameRu());
                            }
                            if (!Objects.equals(product.getPackageCode(), packageDTO.getCode())) {
                                update = true;
                                product.setPackageCode(packageDTO.getCode());
                            }
                            if (!Objects.equals(product.getPackageName(), packageDTO.getNameRu())) {
                                update = true;
                                product.setPackageName(packageDTO.getNameRu());
                            }
                            if (update) {
                                productRepository.saveAndFlush(product);
                                log.info("Product is updated {}", product.getId());
                            }
                        }
                    });
                }
            });
        }
    }

    public String creteExportTask(ProductFilter filter) {
        ExportProductPayload payload = new ExportProductPayload();
        payload.setProductFilter(filter);
        payload.setUserId(getCurrentUserId());
        rabbitMqProducer.sendProductExportTask(payload);
        return "Экспорт начался! Попробуйте скачать его через несколько минут";
    }

    public void exportProductsToExcel(final ExportProductPayload payload) {
        try {
            Files.createDirectories(ResourceUtils.getFile("/export/").toPath());
            final File file = new File("export/" + System.currentTimeMillis() + ".xlsx");
            log.debug("File created {}", file);
            final OutputStream outputStream = Files.newOutputStream(file.toPath());
            exportProductExcel.exportProducts(payload.getProductFilter(), outputStream);
            log.debug("File name {}", file.getAbsolutePath());
//            final String swaggerHost = jHipsterProperties.getSwagger().getHost();
//            final String[] swaggerProtocols = jHipsterProperties.getSwagger().getProtocols();

            final NotificationPayload notificationPayload = new NotificationPayload();
            notificationPayload.setType(NotificationTemplateType.EXPORT_PRODUCT);
//            notificationPayload.addParams("url", swaggerHost + "://" + swaggerProtocols + "/api/products/download/excel/" + file.getName());
            notificationPayload.setUserId(payload.getUserId());
            notificationPayload.setOrganizationId(null);
            log.debug("notificationPayload {}", notificationPayload);
            rabbitMqProducer.sendNotification(notificationPayload);
        } catch (IOException | NoSuchMethodException e) {
            log.error("Export products to excel error", e);
        }
    }

    public void downloadExportFile(String fileName, ServletOutputStream response) {
        File file = new File("export/" + fileName);
        try {
            FileUtils.copyFile(file, response);
        } catch (IOException e) {
            log.error("Download products to excel error", e);
        }
    }


    public String createImportTask(MultipartFile fileInput) throws IOException {
        Path root = Paths.get("import/");
        Path name = root.resolve("import_" + System.currentTimeMillis() + ".xlsx");
        Files.copy(fileInput.getInputStream(), name);

        ImportProductPayload payload = new ImportProductPayload();
        payload.setFileName(name.toString());
        payload.setUserId(getCurrentUserId());
        rabbitMqProducer.sendProductImportTask(payload);
        return "Импорт начался! Вы можете проверить его через несколько минут";
    }

    public void startImport(ImportProductPayload payload) throws IOException {
        importProductExcel.startImport(payload);
    }

    public List<SelectItem> getProductTypesByOrganization() {
        if (isAgency()) {
            return ProductType.getAll();
        } else {
            if (getCurrentOrganization().getTypes() != null) {
                EnumSet<OrganizationType> enumSet = EnumSet.copyOf(getCurrentOrganization().getTypes());
                return ProductType.byOrganizationTypes(enumSet).stream().map(ProductType::toSelectItem).collect(Collectors.toList());
            }
        }
        return null;
    }

    @Transactional
    public void updateVatRates(VatRatePayload vatRatePayload) {
        List<Product> productList = productRepository.findAllByVatRate(vatRatePayload.getOldAmount());
        productList.forEach(product -> {
            product.setVatRate(vatRatePayload.getNewAmount());
            productRepository.save(product);
        });
    }

    public void importFromBasket() {
        rabbitMqProducer.sendImportFromBasket(getCurrentOrganizationId());
    }

    public void importFromBasket(final String organizationId) {
        final Organization organization = organizationRepository.getReferenceById(organizationId);
        final ProductType type = organization.getProductGroup() != null ? organization.getProductGroup().getProductTypes()[0] : ProductType.Other;
        MxikResponseDTO mxikResponseDTO = tasnifClient.getCompanyCatalog(organization.getTin(), null);
        if (mxikResponseDTO == null) return;
        List<MxikDTO> mxiks = mxikResponseDTO.getData();
        List<Product> products = new ArrayList<>();
        int i = 0;
        Product product;
        Integer maxCode = productRepository.findMaxCode().orElse(0);
        for (MxikDTO dto : mxiks) {
            if (StringUtils.isEmpty(dto.getInternationalCode())) {
                continue;
            }
            Optional<Product> productOptional = productRepository.findFirstByBarcodeAndCatalogCodeAndOrganizationIdAndDeletedIsFalse(dto.getInternationalCode(), dto.getMxikCode(), organizationId);
            if (productOptional.isPresent()) {
                continue;
            }
            product = new Product();
            product.setCode(++maxCode);
            product.setStatus(ProductStatus.ACCEPTED);
            product.setOrganizationId(organizationId);
            product.setName(dto.getName());
            product.setType(type);
            product.setShortName(dto.getSubPositionName());
            product.setBarcode(dto.getInternationalCode());
            product.setCatalogCode(dto.getMxikCode());
            product.setCatalogName(dto.getGroupName());
            products.add(product);
            i++;
            if (i == 50) {
                productRepository.saveAll(products);
                products.clear();
                i = 0;
            }
        }
        if (CollectionUtils.isNotEmpty(products)) {
            productRepository.saveAll(products);
        }
    }

    public ProductDetail getProductDetailsByCatalogCode(String mxikCode) {
        Optional<Product> product = productRepository.findFirstByOrganizationIdAndCatalogCodeEqualsAndDeletedFalse(getCurrentOrganizationId(), mxikCode);
        ProductDetail productDetail = new ProductDetail();
        if (product.isPresent()) {
            productDetail.setProductName(product.get().getName());
            productDetail.setBarcode(product.get().getBarcode());
            productDetail.setParentBarcode(product.get().getParentBarcode());
            productDetail.setPrice(product.get().getPrice());
            productDetail.setCatalogCode(product.get().getCatalogCode());
            productDetail.setCatalogName(product.get().getCatalogName());
        }
        productDetail.setOrigin(1);
        productDetail.setOriginName(ProductOrigin.SELF_MADE.getRuText());
        productDetail.setLgotaType(1);
        productDetail.setHasMedical(Utils.pharmaMxikMatcher(mxikCode));
        try {
            List<LgotaDTO> productLgotas = facturaClient.getLgota(mxikCode);
            if (CollectionUtils.isNotEmpty(productLgotas)) {
                productDetail.setLgotas(productLgotas);
            }
        } catch (BadRequestException e) {
            log.error("Lgota not found by mxik code {}", mxikCode);
        }
        return productDetail;
    }

}
