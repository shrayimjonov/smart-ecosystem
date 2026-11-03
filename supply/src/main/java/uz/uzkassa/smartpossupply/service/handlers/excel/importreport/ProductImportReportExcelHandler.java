package uz.uzkassa.smartpossupply.service.handlers.excel.importreport;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.smartpossupply.constants.FileConstants;
import uz.uzkassa.smartpossupply.domain.FileUpload;
import uz.uzkassa.smartpossupply.domain.exportimport.ImportReport;
import uz.uzkassa.smartpossupply.dto.customer.CustomerImportReportDTO;
import uz.uzkassa.smartpossupply.dto.importreport.ImportReportDTO;
import uz.uzkassa.smartpossupply.dto.importreport.ProductImportReportDTO;
import uz.uzkassa.smartpossupply.dto.payload.ImportReportItemPayload;
import uz.uzkassa.smartpossupply.dto.payload.ImportReportPayload;
import uz.uzkassa.smartpossupply.dto.product.ProductDTO;
import uz.uzkassa.smartpossupply.enums.*;
import uz.uzkassa.smartpossupply.repository.BranchRepository;
import uz.uzkassa.smartpossupply.repository.CategoryRepository;
import uz.uzkassa.smartpossupply.service.ProductService;
import uz.uzkassa.smartpossupply.utils.SecurityUtils;
import uz.uzkassa.smartpossupply.web.rest.errors.BadRequestException;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 29.03.2023 14:46
 */

@Component
@Slf4j
@Transactional
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductImportReportExcelHandler extends AbstractImportReportExcelHandler<ProductImportReportDTO> {

    ProductService productService;
    BranchRepository branchRepository;
    CategoryRepository categoryRepository;

    public ProductImportReportExcelHandler(ProductService productService, BranchRepository branchRepository, CategoryRepository categoryRepository) {
        this.productService = productService;
        this.branchRepository = branchRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public void importReport(MultipartFile file, EntityTypeEnum entityType, FileType fileType, ProductImportReportDTO importReportDTO) {
        List<ImportReportItemPayload> products = this.readFile(file, importReportDTO);
        FileUpload fileUpload = fileUploadService.fileUpload(file);
        ImportReport importReport = new ImportReport();
        importReport.setEntityType(entityType);
        importReport.setFileType(fileType);
        importReport.setCompanyId(SecurityUtils.getCurrentCompanyId());
        importReport.setFileId(fileUpload.getId());
        importReport.setItemTotal(products.size());
        importReportRepository.save(importReport);
        rabbitMqImportReportProducer.send(new ImportReportPayload(importReport.getId(), products, entityType));
    }

    @Override
    public List<ImportReportItemPayload> readFile(MultipartFile file, ProductImportReportDTO importReportDTO) {

        List<ImportReportItemPayload> products = new ArrayList<>();
        try {
            Long branchId;
            Long companyId;
            branchId = Optional.ofNullable(importReportDTO).map(ProductImportReportDTO::getBranchId).orElse(null);
            companyId = Optional.ofNullable(importReportDTO).map(ImportReportDTO::getCompanyId).orElse(null);
            if (companyId == null) {
                companyId = SecurityUtils.getCurrentCompanyId();
            }
            if (branchId == null) {
                branchId = branchRepository.getMainBranchId(SecurityUtils.getCurrentCompanyId());
            }
            XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream());
            XSSFSheet sheet = workbook.getSheetAt(0);
            int rowCount = sheet.getPhysicalNumberOfRows();
            int cellCount;
            for (int i = 1; i < rowCount; i++) {
                XSSFRow row = sheet.getRow(i);
                if (row != null) {
                    cellCount = 0;
                    ImportReportItemPayload importReportItemPayload = new ImportReportItemPayload();
                    importReportItemPayload.addCellValue(PRODUCT.BRANCH_ID, String.valueOf(branchId));
                    importReportItemPayload.addCellValue(PRODUCT.COMPANY_ID, String.valueOf(companyId));
                    if (row.getCell(cellCount) != null || String.valueOf(row.getCell(cellCount)).isEmpty()) {
                        row.getCell(cellCount).setCellType(CellType.STRING);
                        if (StringUtils.isNotEmpty(row.getCell(cellCount).getStringCellValue())) {
                            importReportItemPayload.addCellValue(PRODUCT.CATALOG_CODE, row.getCell(cellCount).getStringCellValue());
                        }
                    } else {
                        continue;
                    }
                    cellCount++;

                    if (row.getCell(cellCount) != null || String.valueOf(row.getCell(cellCount)).isEmpty()) {
                        row.getCell(cellCount).setCellType(CellType.STRING);
                        if (StringUtils.isNotEmpty(row.getCell(cellCount).getStringCellValue())) {
                            importReportItemPayload.addCellValue(PRODUCT.NAME, row.getCell(cellCount).getStringCellValue());
                        }
                    } else {
                        continue;
                    }
                    cellCount++;

                    if (row.getCell(cellCount) != null || String.valueOf(row.getCell(cellCount)).isEmpty()) {
                        row.getCell(cellCount).setCellType(CellType.STRING);
                        if (StringUtils.isNotEmpty(row.getCell(cellCount).getStringCellValue())) {
                            importReportItemPayload.addCellValue(PRODUCT.BARCODE, row.getCell(cellCount).getStringCellValue());
                        }
                    } else {
                        continue;
                    }
                    cellCount++;

                    if (row.getCell(cellCount) != null || String.valueOf(row.getCell(cellCount)).isEmpty()) {
                        row.getCell(cellCount).setCellType(CellType.STRING);
                        if (StringUtils.isNotEmpty(row.getCell(cellCount).getStringCellValue())) {
                            importReportItemPayload.addCellValue(PRODUCT.HAS_MARK, row.getCell(cellCount).getStringCellValue());
                        }
                    } else {
                        continue;
                    }
                    cellCount++;

                    if (row.getCell(cellCount) != null || String.valueOf(row.getCell(cellCount)).isEmpty()) {
                        row.getCell(cellCount).setCellType(CellType.STRING);
                        if (StringUtils.isNotEmpty(row.getCell(cellCount).getStringCellValue())) {
                            importReportItemPayload.addCellValue(PRODUCT.PRODUCT_ORIGIN, row.getCell(cellCount).getStringCellValue());
                        }
                    } else {
                        continue;
                    }
                    cellCount++;

                    if (row.getCell(cellCount) != null || String.valueOf(row.getCell(cellCount)).isEmpty()) {
                        row.getCell(cellCount).setCellType(CellType.STRING);
                        if (StringUtils.isNotEmpty(row.getCell(cellCount).getStringCellValue())) {
                            importReportItemPayload.addCellValue(PRODUCT.PRICE, row.getCell(cellCount).getStringCellValue());
                        }
                    } else {
                        importReportItemPayload.addCellValue(PRODUCT.PRICE, BigDecimal.ZERO.toString());
                    }
                    cellCount++;

                    if (row.getCell(cellCount) != null || String.valueOf(row.getCell(cellCount)).isEmpty()) {
                        row.getCell(cellCount).setCellType(CellType.STRING);
                        if (StringUtils.isNotEmpty(row.getCell(cellCount).getStringCellValue())) {
                            importReportItemPayload.addCellValue(PRODUCT.VAT, row.getCell(cellCount).getStringCellValue());
                        }
                    } else {
                        continue;
                    }
                    cellCount++;

                    if (row.getCell(cellCount) != null || String.valueOf(row.getCell(cellCount)).isEmpty()) {
                        row.getCell(cellCount).setCellType(CellType.STRING);
                        if (StringUtils.isNotEmpty(row.getCell(cellCount).getStringCellValue())) {
                            importReportItemPayload.addCellValue(PRODUCT.BRUTTO, row.getCell(cellCount).getStringCellValue());
                        }
                    } else {
                        importReportItemPayload.addCellValue(PRODUCT.BRUTTO, BigDecimal.ZERO.toString());
                    }
                    cellCount++;

                    if (row.getCell(cellCount) != null || String.valueOf(row.getCell(cellCount)).isEmpty()) {
                        row.getCell(cellCount).setCellType(CellType.STRING);
                        if (StringUtils.isNotEmpty(row.getCell(cellCount).getStringCellValue())) {
                            importReportItemPayload.addCellValue(PRODUCT.NETTO, row.getCell(cellCount).getStringCellValue());
                        }
                    } else {
                        importReportItemPayload.addCellValue(PRODUCT.NETTO, BigDecimal.ZERO.toString());
                    }
                    products.add(importReportItemPayload);
                }
            }
        } catch (IOException e) {
            throw new BadRequestException("Не удалось загрузить файл");
        }
        return products;
    }

    public void importReportQueue(ImportReportPayload importReportPayload) {
        List<ProductDTO> products = new ArrayList<>();
        for (ImportReportItemPayload importReportItemPayload : importReportPayload.getEntities()) {
            ProductDTO productDTO = new ProductDTO();
            productDTO.setBranchId(Long.valueOf(String.valueOf(importReportItemPayload.getCellValueMap().get(PRODUCT.BRANCH_ID))));
            productDTO.setCompanyId(Long.valueOf(String.valueOf(importReportItemPayload.getCellValueMap().get(PRODUCT.COMPANY_ID))));
            if (importReportItemPayload.getCellValueMap().get(PRODUCT.CATALOG_CODE) != null && StringUtils.isNotEmpty(String.valueOf(importReportItemPayload.getCellValueMap().get(PRODUCT.CATALOG_CODE)))) {
                productDTO.setVatBarcode(String.valueOf(importReportItemPayload.getCellValueMap().get(PRODUCT.CATALOG_CODE)).replace(" ", "").trim());
            }
            if (importReportItemPayload.getCellValueMap().get(PRODUCT.NAME) != null && StringUtils.isNotEmpty(String.valueOf(importReportItemPayload.getCellValueMap().get(PRODUCT.NAME)))) {
                productDTO.setName(String.valueOf(importReportItemPayload.getCellValueMap().get(PRODUCT.NAME)).replace(" ", "").trim());
            }
            if (importReportItemPayload.getCellValueMap().get(PRODUCT.BARCODE) != null && StringUtils.isNotEmpty(String.valueOf(importReportItemPayload.getCellValueMap().get(PRODUCT.BARCODE)))) {
                productDTO.setBarcode(String.valueOf(importReportItemPayload.getCellValueMap().get(PRODUCT.BARCODE)).replace(" ", "").trim());
            }
            if (importReportItemPayload.getCellValueMap().get(PRODUCT.HAS_MARK) != null && StringUtils.isNotEmpty(String.valueOf(importReportItemPayload.getCellValueMap().get(PRODUCT.HAS_MARK)))) {
                String value = String.valueOf(importReportItemPayload.getCellValueMap().get(PRODUCT.HAS_MARK));
                productDTO.setHasMark(Boolean.parseBoolean(value.substring(value.lastIndexOf("-") + 1)));
            }
            if (importReportItemPayload.getCellValueMap().get(PRODUCT.PRODUCT_ORIGIN) != null && StringUtils.isNotEmpty(String.valueOf(importReportItemPayload.getCellValueMap().get(PRODUCT.PRODUCT_ORIGIN)))) {
                String value = String.valueOf(importReportItemPayload.getCellValueMap().get(PRODUCT.PRODUCT_ORIGIN));
                productDTO.setOrigin(ProductOrigin.valueOf(value.substring(value.lastIndexOf("-") + 1)));
            }
            if (importReportItemPayload.getCellValueMap().get(PRODUCT.PRICE) != null && StringUtils.isNotEmpty(String.valueOf(importReportItemPayload.getCellValueMap().get(PRODUCT.PRICE)))) {
                productDTO.setPrice(new BigDecimal(String.valueOf(importReportItemPayload.getCellValueMap().get(PRODUCT.PRICE))));
            }
            if (importReportItemPayload.getCellValueMap().get(PRODUCT.VAT) != null && StringUtils.isNotEmpty(String.valueOf(importReportItemPayload.getCellValueMap().get(PRODUCT.VAT)))) {
                String value = String.valueOf(importReportItemPayload.getCellValueMap().get(PRODUCT.VAT));
                productDTO.setVat(Vat.valueOf(value.substring(value.lastIndexOf("-") + 1)));
            }
            if (importReportItemPayload.getCellValueMap().get(PRODUCT.BRUTTO) != null && StringUtils.isNotEmpty(String.valueOf(importReportItemPayload.getCellValueMap().get(PRODUCT.BRUTTO)))) {
                productDTO.setWeightBrutto(new BigDecimal(String.valueOf(importReportItemPayload.getCellValueMap().get(PRODUCT.BRUTTO))));
            }
            if (importReportItemPayload.getCellValueMap().get(PRODUCT.NETTO) != null && StringUtils.isNotEmpty(String.valueOf(importReportItemPayload.getCellValueMap().get(PRODUCT.NETTO)))) {
                productDTO.setWeightNetto(new BigDecimal(String.valueOf(importReportItemPayload.getCellValueMap().get(PRODUCT.NETTO))));
            }
            categoryRepository.findFirstByBranchIdAndCatalogCodeAndEnvironmentTypeAndDeletedIsFalse(productDTO.getBranchId(), productDTO.getVatBarcode(), EnvironmentType.WEB).ifPresent(category -> productDTO.setCategoryIds(List.of(category.getId())));
            if (CollectionUtils.isEmpty(productDTO.getCategoryIds())) {
                categoryRepository.findFirstByMainIsTrueAndBranchIdAndDeletedIsFalse(productDTO.getBranchId()).ifPresent(category -> productDTO.setCategoryIds(List.of(category.getId())));
            }
            products.add(productDTO);
        }
        if (!CollectionUtils.isEmpty(products)) {
            productService.create(products);
        }
    }

    @Override
    public ResponseEntity<Resource> generateErrorFile(Object entities) {
        XSSFWorkbook xssfWorkbook = getXSSFWorkbook(getErrorFileName(), getErrorFileHeaders());
        Sheet sheet = xssfWorkbook.getSheet(getErrorFileName());
        int rowIndex = 0;
        CustomerImportReportDTO[] customers = objectMapper.convertValue(entities, CustomerImportReportDTO[].class);
        for (CustomerImportReportDTO customer : customers) {
            Row row = sheet.createRow(++rowIndex);
            row.createCell(0).setCellValue(customer.getCellValue1());
        }
        try {
            Resource resource = new ByteArrayResource(getResource(xssfWorkbook));
            return ResponseEntity
                    .ok()
                    .header("Content-Disposition", "attachment; filename=" + URLEncoder.encode(getErrorFileName(), StandardCharsets.UTF_8) + ".xlsx")
                    .contentLength(resource.contentLength())
                    .contentType(MediaType.parseMediaType(FileConstants.CONTENT_TYPE_FOR_EXCEL))
                    .body(resource);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    List<String> getErrorFileHeaders() {
        return Collections.singletonList(
                "Barcode"
        );
    }

    @Override
    String getErrorFileName() {
        return "ErrorProducts";
    }
}
