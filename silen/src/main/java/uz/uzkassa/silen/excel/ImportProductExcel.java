package uz.uzkassa.silen.excel;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.openxml4j.util.ZipSecureFile;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import uz.uzkassa.silen.domain.Attributes;
import uz.uzkassa.silen.domain.Product;
import uz.uzkassa.silen.dto.ProductAttributesDTO;
import uz.uzkassa.silen.dto.mq.ImportProductPayload;
import uz.uzkassa.silen.dto.mq.NotificationPayload;
import uz.uzkassa.silen.enumeration.NotificationTemplateType;
import uz.uzkassa.silen.enumeration.ProductType;
import uz.uzkassa.silen.rabbitmq.producer.RabbitMqProducer;
import uz.uzkassa.silen.repository.pg.AttributesRepository;
import uz.uzkassa.silen.repository.pg.OrganizationRepository;
import uz.uzkassa.silen.repository.pg.ProductRepository;
import uz.uzkassa.silen.security.SecurityUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Optional;

@Component
@Slf4j
@RequiredArgsConstructor
public class ImportProductExcel extends AbstractProductExcel {
    private final OrganizationRepository organizationRepository;
    private final ProductRepository productRepository;
    private final RabbitMqProducer rabbitMqProducer;
    private final AttributesRepository attributesRepository;

    /**
     * headers[String]{
     * "ИНН", "НАЗВАНИЕ ПРОДУКТА", "КОРОТКОЕ НАЗВАНИЕ ПРОДУКТА",
     * "ИКПУ", "ЕД. ИЗМЕРЕНИЯ", "НДС (СТАВКА %)", "ЦЕНА", "GTIN", "ТИП ПРОДУКЦИИ"
     * }
     *
     * @param payload {@link ImportProductPayload}
     */
    public void startImport(ImportProductPayload payload) {
//        Path root = ResourceUtils.getFile().toPath();
        ClassPathResource classPathResource = new ClassPathResource(payload.getFileName());
        log.debug("Classpath Resource is exists {}", classPathResource.exists());
        File receivedFile = new File(payload.getFileName());
        int savedRows = 0;

        if (receivedFile.canRead()) try (FileInputStream file = new FileInputStream(receivedFile)) {
            ZipSecureFile.setMinInflateRatio(0);
            //Create Workbook instance holding reference to .xls file
            XSSFWorkbook workbook = (XSSFWorkbook) WorkbookFactory.create(file);

            //Get first/desired sheet from the workbook
            XSSFSheet sheet = workbook.getSheetAt(0);

            //Iterate through each rows one by one
            Iterator<Row> rowIterator = sheet.iterator();

            // for header row
            readHeaders(rowIterator);

            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                //For each row, iterate through all the columns
                Iterator<Cell> cellIterator = row.cellIterator();
                while (cellIterator.hasNext()) {
                    ProductBuilder productBuilder = new ProductBuilder(cellIterator);
                    //organization
                    if (cellIterator.hasNext()) {
                        String orgTin = convertToString(cellIterator.next());
                        organizationRepository.findFistByTinAndDeletedIsFalse(orgTin).ifPresent(organization -> productBuilder.setOrganizationId(organization.getId()));
                    }
                    if (productBuilder.isOrganizationId()) {
                        //product name
                        productBuilder.setName();
                        //getShortName
                        productBuilder.setShortName();
                        //getCatalogCode
                        productBuilder.setCatalogCode();
                        //getPackageName
                        productBuilder.setPackageName();
                        //getVatRate
                        productBuilder.setVatRate();
                        //getPrice
                        productBuilder.setPrice();
                        //getBarcode
                        productBuilder.setBarcode();
                        // getProductType
                        productBuilder.setType();
                        // setAdditional Attributes
                        productBuilder.setAdditionalAttributes();

                        Product product = productBuilder.build();
                        boolean productExists = productRepository.existsByBarcodeAndOrganizationIdAndDeletedIsFalse(product.getBarcode(), product.getOrganizationId());
                        log.info("Product {} exists {}", product.getName(), productExists);
                        if (!productExists && product.getOrganizationId() != null) {
                            Integer maxCode = productRepository.findMaxCode().orElse(0);
                            product.setCode(maxCode + 1);
//                            product.setCatalogPrice(Utils.calculateCatalogPrice(product));
                            productRepository.save(product);
                            savedRows++;
                        }
                    }
                }
            }
            if (savedRows > 0) {
                NotificationPayload notify = new NotificationPayload();
                notify.setUserId(payload.getUserId());
                notify.setType(NotificationTemplateType.IMPORT_PRODUCT);
                notify.setOrganizationId(null);
                notify.addParams("count", String.valueOf(savedRows));
                rabbitMqProducer.sendNotification(notify);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readHeaders(Iterator<Row> rowIterator) {
        Row headerRow = rowIterator.next();
    }

    @Override
    public void writeBody() {
    }

    private class ProductBuilder {
        private final Product product;
        private final Iterator<Cell> cellIterator;

        public ProductBuilder(Iterator<Cell> cellIterator) {
            this.product = new Product();
            this.cellIterator = cellIterator;
        }

        public ProductBuilder setName() {
            if (cellIterator.hasNext()) {
                product.setName(convertToString(cellIterator.next()));
            }
            return this;
        }

        public ProductBuilder setShortName() {
            if (cellIterator.hasNext()) {
                product.setShortName(convertToString(cellIterator.next()));
            }
            return this;
        }


        public ProductBuilder setCatalogCode() {
            if (cellIterator.hasNext()) {
                product.setCatalogCode(convertToString(cellIterator.next()));
            }
            return this;
        }

        public ProductBuilder setPackageName() {
            if (cellIterator.hasNext()) {
                product.setPackageName(convertToString(cellIterator.next()));
            }
            return this;
        }

        public ProductBuilder setVatRate() {
            if (cellIterator.hasNext()) {
                product.setVatRate(convertToBig(cellIterator.next()));
            }
            return this;
        }

        public ProductBuilder setPrice() {
            if (cellIterator.hasNext()) {
                product.setPrice(convertToBig(cellIterator.next()));
            }
            return this;
        }

        public ProductBuilder setBarcode() {
            if (cellIterator.hasNext()) {
                product.setBarcode(convertToString(cellIterator.next()));
            }
            return this;
        }

        public ProductBuilder setType() {
            if (cellIterator.hasNext()) {
                String type = convertToString(cellIterator.next());
                Optional<ProductType> productType = Optional.ofNullable(ProductType.fromName(type));
                productType.ifPresent(product::setType);
            }
            return this;
        }

        public ProductBuilder setOrganizationId(String organizationId) {
            product.setOrganizationId(organizationId);
            return this;
        }

        public ProductBuilder setAdditionalAttributes() {
            ProductType type = product.getType();
            if (type == null) {
                cellIterator.next();
                return this;
            }
            HashMap<Integer, Attributes> attributesHashMap = new HashMap<>();
            attributesRepository.getAttributesByType(type).forEach(attr -> attributesHashMap.put(attr.getSorter(), attr));
            StringBuilder nameBuilder = new StringBuilder();
            nameBuilder.append(product.getName()).append(" ");

            final String organizationId = SecurityUtils.getCurrentOrganizationId();
            int attributeSorterIndex = 0;
            while (cellIterator.hasNext()) {
                Optional.ofNullable(attributesHashMap.get(attributeSorterIndex))
                    .ifPresent(attributes -> {
                        ProductAttributesDTO dto = new ProductAttributesDTO();
                        dto.setAttributeId(attributes.getId());
                        dto.setAttributeValue(convertToString(cellIterator.next()));
                        product.addAttribute(dto, organizationId);
                        if (attributes.isVisible()) nameBuilder.append(dto.getAttributeValue());
                    });

                attributeSorterIndex++;
            }
            nameBuilder.append(".");
            product.setVisibleName(nameBuilder.toString());
            return this;
        }

        public boolean isOrganizationId() {
            return product.getOrganizationId() != null;
        }

        public Product build() {
            return product;
        }
    }
}
