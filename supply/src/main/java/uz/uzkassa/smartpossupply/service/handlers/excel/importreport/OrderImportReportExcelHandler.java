package uz.uzkassa.smartpossupply.service.handlers.excel.importreport;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.smartpossupply.constants.FileConstants;
import uz.uzkassa.smartpossupply.domain.FileUpload;
import uz.uzkassa.smartpossupply.domain.exportimport.ImportReport;
import uz.uzkassa.smartpossupply.domain.exportimport.ImportReportItem;
import uz.uzkassa.smartpossupply.dto.customer.CustomerImportReportDTO;
import uz.uzkassa.smartpossupply.dto.importreport.OrderImportReportDTO;
import uz.uzkassa.smartpossupply.dto.order.onec.Order1CDTO;
import uz.uzkassa.smartpossupply.dto.order.onec.OrderContract1CDTO;
import uz.uzkassa.smartpossupply.dto.order.onec.OrderProduct1CDTO;
import uz.uzkassa.smartpossupply.dto.payload.ImportReportItemPayload;
import uz.uzkassa.smartpossupply.dto.payload.ImportReportPayload;
import uz.uzkassa.smartpossupply.enums.EntityTypeEnum;
import uz.uzkassa.smartpossupply.enums.FileType;
import uz.uzkassa.smartpossupply.enums.OrderSource;
import uz.uzkassa.smartpossupply.service.OrderService;
import uz.uzkassa.smartpossupply.utils.DateUtils;
import uz.uzkassa.smartpossupply.utils.SecurityUtils;
import uz.uzkassa.smartpossupply.web.rest.errors.BadRequestException;
import uz.uzkassa.smartpossupply.web.rest.errors.NotFoundException;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 29.03.2023 14:46
 */

@Component
@Slf4j
@Transactional
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderImportReportExcelHandler extends AbstractImportReportExcelHandler<OrderImportReportDTO> {

    OrderService orderService;

    public OrderImportReportExcelHandler(OrderService orderService) {
        this.orderService = orderService;
    }

    private String getDateCellValue(XSSFCell cell) {
        if (cell == null) return null;
        if (cell.getCellType() == CellType.NUMERIC) {
            LocalDate date = LocalDate.of(1899, 12, 30).plusDays((long) cell.getNumericCellValue());
            return date.format(DateUtils.DAY_MONTH_YEAR_DOT_FORMATTER);
        }
        cell.setCellType(CellType.STRING);
        String val = cell.getStringCellValue().trim();
        return val.isEmpty() ? null : val;
    }

    @Override
    public void importReport(MultipartFile file, EntityTypeEnum entityType, FileType fileType, OrderImportReportDTO importReportDTO) {
        List<ImportReportItemPayload> orders = this.readFile(file, importReportDTO);
        FileUpload fileUpload = fileUploadService.fileUpload(file);
        ImportReport importReport = new ImportReport();
        importReport.setEntityType(entityType);
        importReport.setFileType(fileType);
        importReport.setCompanyId(SecurityUtils.getCurrentCompanyId());
        importReport.setFileId(fileUpload.getId());
        importReport.setItemTotal(orders.size());
        importReportRepository.save(importReport);
        rabbitMqImportReportProducer.send(new ImportReportPayload(importReport.getId(), orders, entityType));
    }

    public void importReport(MultipartFile file, EntityTypeEnum entityType, FileType fileType, String jsonString, OrderImportReportDTO orderImportReportDTO) {
        OrderImportReportDTO importReportDTO;
        if (orderImportReportDTO == null) {
            try {
                importReportDTO = objectMapper.readValue(jsonString, OrderImportReportDTO.class);
            } catch (JsonProcessingException e) {
                throw new BadRequestException("Reading object error: {}", e.getMessage());
            }
        } else {
            importReportDTO = orderImportReportDTO;
        }
        importReportDTO.setCompanyId(SecurityUtils.getCurrentCompanyId());
        this.importReport(file, entityType, fileType, importReportDTO);
    }

    @Override
    public List<ImportReportItemPayload> readFile(MultipartFile file, OrderImportReportDTO orderDTO) {
        if (orderDTO == null) {
            orderDTO = new OrderImportReportDTO();
        }
        orderDTO.setBranchId(Optional.ofNullable(orderDTO.getBranchId()).orElse(SecurityUtils.getCurrentBranchId()));
        orderDTO.setWarehouseId(Optional.ofNullable(orderDTO.getWarehouseId()).orElse(SecurityUtils.getCurrentWarehouseId()));
        if (orderDTO.getBranchId() == null || orderDTO.getWarehouseId() == null) {
            throw new BadRequestException("Branch id or warehouse id is null");
        }
        List<ImportReportItemPayload> orders = new ArrayList<>();
        try {
            XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream());
            XSSFSheet sheet = workbook.getSheetAt(0);
            int rowCount = sheet.getPhysicalNumberOfRows();
            int cellCount;
            for (int i = 1; i < rowCount; i++) {
                XSSFRow row = sheet.getRow(i);
                if (row != null) {
                    cellCount = 0;
                    if (row.getCell(cellCount) == null || String.valueOf(row.getCell(cellCount)).isEmpty()) {
                        continue;
                    }
                    ImportReportItemPayload importReportItemPayload = new ImportReportItemPayload();
                    importReportItemPayload.addCellValue(ORDER.BRANCH_ID, orderDTO.getBranchId().toString());
                    importReportItemPayload.addCellValue(ORDER.WAREHOUSE_ID, orderDTO.getWarehouseId().toString());
                    importReportItemPayload.addCellValue(ORDER.CREATE_STOCK_IN, String.valueOf(orderDTO.isCreateStockIn()));

                    // ORDER_NUMBER — use safe helper instead of setCellType(STRING) to avoid "1.2052026E7"
                    String orderNumber = getStringCellValue(row.getCell(cellCount));
                    if (StringUtils.isEmpty(orderNumber)) {
                        continue;
                    }
                    importReportItemPayload.addCellValue(ORDER.ORDER_NUMBER, orderNumber);
                    cellCount++;

                    String orderDate = getDateCellValue(row.getCell(cellCount));
                    if (StringUtils.isNotEmpty(orderDate)) {
                        importReportItemPayload.addCellValue(ORDER.ORDER_DATE, orderDate);
                    }
                    cellCount++;
                    if (row.getCell(cellCount) != null) {
                        row.getCell(cellCount).setCellType(CellType.STRING);
                        if (StringUtils.isNotEmpty(row.getCell(cellCount).getStringCellValue())) {
                            importReportItemPayload.addCellValue(ORDER.CONTRACT_NUMBER, row.getCell(cellCount).getStringCellValue().trim());
                        }
                    } else {
                        continue;
                    }
                    cellCount++;
                    String contractDate = getDateCellValue(row.getCell(cellCount));
                    if (StringUtils.isNotEmpty(contractDate)) {
                        importReportItemPayload.addCellValue(ORDER.CONTRACT_DATE, contractDate);
                    } else {
                        continue;
                    }
                    cellCount++;

                    // CUSTOMER_TIN — optional if CUSTOMER_CODE is present (do NOT continue here)
                    String tinValue = null;
                    if (row.getCell(cellCount) != null) {
                        row.getCell(cellCount).setCellType(CellType.STRING);
                        tinValue = row.getCell(cellCount).getStringCellValue().trim();
                        if (StringUtils.isNotEmpty(tinValue)) {
                            importReportItemPayload.addCellValue(ORDER.CUSTOMER_TIN, tinValue);
                        }
                    }
                    cellCount++;

                    // CUSTOMER_CODE
                    String customerCodeValue = null;
                    if (row.getCell(cellCount) != null) {
                        row.getCell(cellCount).setCellType(CellType.STRING);
                        customerCodeValue = row.getCell(cellCount).getStringCellValue().trim();
                        if (StringUtils.isNotEmpty(customerCodeValue)) {
                            importReportItemPayload.addCellValue(ORDER.CUSTOMER_CODE, customerCodeValue);
                        }
                    }
                    cellCount++;

                    // At least one of TIN / customerCode must be present
                    if (StringUtils.isEmpty(tinValue) && StringUtils.isEmpty(customerCodeValue)) {
                        log.warn("Row {} skipped: both ИНН and Код покупателя are empty", i);
                        continue;
                    }

                    if (row.getCell(cellCount) != null) {
                        row.getCell(cellCount).setCellType(CellType.STRING);
                        if (StringUtils.isNotEmpty(row.getCell(cellCount).getStringCellValue())) {
                            importReportItemPayload.addCellValue(ORDER.BRANCH_CODE, row.getCell(cellCount).getStringCellValue().trim());
                        }
                    }
                    cellCount++;

                    if (row.getCell(cellCount) != null) {
                        row.getCell(cellCount).setCellType(CellType.STRING);
                        if (StringUtils.isNotEmpty(row.getCell(cellCount).getStringCellValue())) {
                            importReportItemPayload.addCellValue(ORDER.PRODUCT_NAME, row.getCell(cellCount).getStringCellValue().trim());
                        }
                    }
                    cellCount++;

                    if (row.getCell(cellCount) != null) {
                        row.getCell(cellCount).setCellType(CellType.STRING);
                        if (StringUtils.isNotEmpty(row.getCell(cellCount).getStringCellValue())) {
                            importReportItemPayload.addCellValue(ORDER.PRODUCT_VAT_BARCODE, row.getCell(cellCount).getStringCellValue().trim());
                        }
                    }
                    cellCount++;

                    if (row.getCell(cellCount) != null) {
                        row.getCell(cellCount).setCellType(CellType.STRING);
                        if (StringUtils.isNotEmpty(row.getCell(cellCount).getStringCellValue())) {
                            importReportItemPayload.addCellValue(ORDER.PRODUCT_BARCODE, row.getCell(cellCount).getStringCellValue().trim());
                        }
                    } else {
                        continue;
                    }
                    cellCount++;

                    if (row.getCell(cellCount) != null) {
                        row.getCell(cellCount).setCellType(CellType.STRING);
                        if (StringUtils.isNotEmpty(row.getCell(cellCount).getStringCellValue())) {
                            importReportItemPayload.addCellValue(ORDER.PRODUCT_UNIT_CODE, row.getCell(cellCount).getStringCellValue().trim());
                        }
                    }
                    cellCount++;

                    if (row.getCell(cellCount) != null) {
                        row.getCell(cellCount).setCellType(CellType.STRING);
                        if (StringUtils.isNotEmpty(row.getCell(cellCount).getStringCellValue())) {
                            importReportItemPayload.addCellValue(ORDER.PRODUCT_UNIT_NAME, row.getCell(cellCount).getStringCellValue().trim());
                        }
                    }
                    cellCount++;

                    if (row.getCell(cellCount) != null) {
                        row.getCell(cellCount).setCellType(CellType.STRING);
                        if (StringUtils.isNotEmpty(row.getCell(cellCount).getStringCellValue())) {
                            importReportItemPayload.addCellValue(ORDER.QTY, row.getCell(cellCount).getStringCellValue().trim());
                        }
                    } else {
                        continue;
                    }
                    cellCount++;

                    if (row.getCell(cellCount) != null) {
                        row.getCell(cellCount).setCellType(CellType.STRING);
                        if (StringUtils.isNotEmpty(row.getCell(cellCount).getStringCellValue())) {
                            importReportItemPayload.addCellValue(ORDER.PRODUCT_SERIAL, row.getCell(cellCount).getStringCellValue().trim());
                        }
                    }
                    cellCount++;


                    if (row.getCell(cellCount) != null) {
                        row.getCell(cellCount).setCellType(CellType.STRING);
                        if (StringUtils.isNotEmpty(row.getCell(cellCount).getStringCellValue())) {
                            importReportItemPayload.addCellValue(ORDER.BASE_SUMMA, row.getCell(cellCount).getStringCellValue().trim());
                        }
                    }
                    cellCount++;


                    if (row.getCell(cellCount) != null) {
                        row.getCell(cellCount).setCellType(CellType.STRING);
                        if (StringUtils.isNotEmpty(row.getCell(cellCount).getStringCellValue())) {
                            importReportItemPayload.addCellValue(ORDER.PROFIT_RATE, row.getCell(cellCount).getStringCellValue().trim());
                        }
                    }
                    cellCount++;

                    if (row.getCell(cellCount) != null) {
                        row.getCell(cellCount).setCellType(CellType.STRING);
                        if (StringUtils.isNotEmpty(row.getCell(cellCount).getStringCellValue())) {
                            importReportItemPayload.addCellValue(ORDER.PRICE, row.getCell(cellCount).getStringCellValue().trim());
                        }
                    } else {
                        continue;
                    }
                    cellCount++;

                    if (row.getCell(cellCount) != null) {
                        row.getCell(cellCount).setCellType(CellType.STRING);
                        if (StringUtils.isNotEmpty(row.getCell(cellCount).getStringCellValue())) {
                            importReportItemPayload.addCellValue(ORDER.DELIVER_SUM, row.getCell(cellCount).getStringCellValue().trim());
                        }
                    } else {
                        continue;
                    }
                    cellCount++;

                    if (row.getCell(cellCount) != null) {
                        row.getCell(cellCount).setCellType(CellType.STRING);
                        if (StringUtils.isNotEmpty(row.getCell(cellCount).getStringCellValue())) {
                            importReportItemPayload.addCellValue(ORDER.VAT_RATE, row.getCell(cellCount).getStringCellValue().trim());
                        }
                    } else {
                        continue;
                    }
                    cellCount++;

                    if (row.getCell(cellCount) != null) {
                        row.getCell(cellCount).setCellType(CellType.STRING);
                        if (StringUtils.isNotEmpty(row.getCell(cellCount).getStringCellValue())) {
                            importReportItemPayload.addCellValue(ORDER.VAT_PRICE, row.getCell(cellCount).getStringCellValue().trim());
                        }
                    } else {
                        continue;
                    }
                    cellCount++;

                    if (row.getCell(cellCount) != null) {
                        row.getCell(cellCount).setCellType(CellType.STRING);
                        if (StringUtils.isNotEmpty(row.getCell(cellCount).getStringCellValue())) {
                            importReportItemPayload.addCellValue(ORDER.DELIVER_SUM_WITH_VAT, row.getCell(cellCount).getStringCellValue().trim());
                        }
                    } else {
                        continue;
                    }

                    orders.add(importReportItemPayload);
                }
            }
        } catch (IOException e) {
            throw new BadRequestException("Не удалось загрузить файл");
        }
        return orders;
    }

    public void importReportItemQueue(ImportReportPayload payload) {
        importReportRepository.findById(payload.getImportReportId())
                .map(importReport -> {
                    List<Order1CDTO> orders = this.importReportQueue(payload);
                    for (Order1CDTO order1CDTO : orders) {
                        try {
                            order1CDTO.setSource(OrderSource.FILE_IMPORT.getCode());
                            orderService.create(order1CDTO, importReport.getCompanyId());
                            importReport.setImportedTotal(importReport.getImportedTotal() + 1);
                        } catch (Exception e) {
                            log.error(e.getMessage(), e);
                            ImportReportItem importReportItem = new ImportReportItem();
                            importReportItem.setCellValue1(e.toString());
                            if (StringUtils.isNotEmpty(e.getMessage())) {
                                importReportItem.setErrorMessage(e.getMessage());
                            }
                            importReportItem.setImportReportId(importReport.getId());
                            importReportItemRepository.save(importReportItem);
                            importReport.setErrorTotal(importReport.getErrorTotal() + 1);
                        }
                    }

                    importReportRepository.save(importReport);
                    return importReport;
                })
                .orElseThrow(() -> new NotFoundException("Import report not found"));
    }

    public List<Order1CDTO> importReportQueue(ImportReportPayload importReportPayload) {
        List<Order1CDTO> orders = new ArrayList<>();
        importReportPayload
                .getEntities().forEach(importReportItemPayload -> {
                    Order1CDTO order1CDTO = orders.stream().filter(orderDTO -> orderDTO.getOrderNumber().equals(importReportItemPayload.getCellValueMap().get(ORDER.ORDER_NUMBER))
                            && orderDTO.getOrderDate().equals(LocalDate.parse(String.valueOf(importReportItemPayload.getCellValueMap().get(ORDER.ORDER_DATE)), DateUtils.DAY_MONTH_YEAR_DOT_FORMATTER).atStartOfDay())).findFirst().orElse(new Order1CDTO());
                    order1CDTO.setBranchId(Long.valueOf(String.valueOf(importReportItemPayload.getCellValueMap().get(ORDER.BRANCH_ID))));
                    order1CDTO.setWarehouseId(Long.valueOf(String.valueOf(importReportItemPayload.getCellValueMap().get(ORDER.WAREHOUSE_ID))));
                    order1CDTO.setCreateStockIn(Boolean.parseBoolean(String.valueOf(importReportItemPayload.getCellValueMap().get(ORDER.CREATE_STOCK_IN))));
                    if (StringUtils.isEmpty(order1CDTO.getOrderNumber())) {
                        order1CDTO.setOrderNumber(String.valueOf(importReportItemPayload.getCellValueMap().get(ORDER.ORDER_NUMBER)));
                        if (StringUtils.isNotEmpty(String.valueOf(importReportItemPayload.getCellValueMap().get(ORDER.ORDER_DATE)))) {
                            order1CDTO.setOrderDate(LocalDate.parse(String.valueOf(importReportItemPayload.getCellValueMap().get(ORDER.ORDER_DATE)), DateUtils.DAY_MONTH_YEAR_DOT_FORMATTER).atStartOfDay());
                        }
                        if (StringUtils.isNotEmpty(String.valueOf(importReportItemPayload.getCellValueMap().get(ORDER.CONTRACT_NUMBER)))) {
                            order1CDTO.setContract(new OrderContract1CDTO(String.valueOf(importReportItemPayload.getCellValueMap().get(ORDER.CONTRACT_NUMBER)), LocalDate.parse(String.valueOf(importReportItemPayload.getCellValueMap().get(ORDER.CONTRACT_DATE)), DateUtils.DAY_MONTH_YEAR_DOT_FORMATTER)));
                        }
                        String tin          = Objects.toString(importReportItemPayload.getCellValueMap().get(ORDER.CUSTOMER_TIN), null);
                        String customerCode = Objects.toString(importReportItemPayload.getCellValueMap().get(ORDER.CUSTOMER_CODE), null);
                        String branchCode   = Objects.toString(importReportItemPayload.getCellValueMap().get(ORDER.BRANCH_CODE), null);

                        if (StringUtils.isNotEmpty(tin)) {
                            order1CDTO.setCustomerTin(tin);
                        }
                        if (StringUtils.isNotEmpty(customerCode)) {
                            order1CDTO.setCustomerCode(customerCode);
                        }
                        if (StringUtils.isEmpty(tin) && StringUtils.isEmpty(customerCode)) {
                            throw new BadRequestException("Отсутствует ИНН и Код покупателя для заказа: " + importReportItemPayload.getCellValueMap().get(ORDER.ORDER_NUMBER));
                        }
                        if (StringUtils.isNotEmpty(branchCode)) {
                            order1CDTO.setDeliveryBranchCode(branchCode);
                        }
                        orders.add(order1CDTO);
                    }
                    OrderProduct1CDTO orderProduct1CDTO = new OrderProduct1CDTO();
                    orderProduct1CDTO.setBarcode(String.valueOf(importReportItemPayload.getCellValueMap().get(ORDER.PRODUCT_BARCODE)));
                    orderProduct1CDTO.setCatalogCode(String.valueOf(importReportItemPayload.getCellValueMap().get(ORDER.PRODUCT_VAT_BARCODE)));
                    if (importReportItemPayload.getCellValueMap().get(ORDER.PRODUCT_NAME) != null) {
                        orderProduct1CDTO.setName(String.valueOf(importReportItemPayload.getCellValueMap().get(ORDER.PRODUCT_NAME)));
                    }
                    if (importReportItemPayload.getCellValueMap().get(ORDER.PRODUCT_SERIAL) != null) {
                        orderProduct1CDTO.setSerial(String.valueOf(importReportItemPayload.getCellValueMap().get(ORDER.PRODUCT_SERIAL)));
                    }
                    if (importReportItemPayload.getCellValueMap().get(ORDER.QTY) != null && importReportItemPayload.getCellValueMap().get(ORDER.QTY) != null) {
                        orderProduct1CDTO.setCount(new BigDecimal(String.valueOf(importReportItemPayload.getCellValueMap().get(ORDER.QTY))));
                    }
                    if (importReportItemPayload.getCellValueMap().get(ORDER.PRICE) != null && StringUtils.isNotEmpty(String.valueOf(importReportItemPayload.getCellValueMap().get(ORDER.PRICE)))) {
                        orderProduct1CDTO.setSumma(new BigDecimal(String.valueOf(importReportItemPayload.getCellValueMap().get(ORDER.PRICE))));
                    }
                    if (importReportItemPayload.getCellValueMap().get(ORDER.BASE_SUMMA) != null && StringUtils.isNotEmpty(String.valueOf(importReportItemPayload.getCellValueMap().get(ORDER.BASE_SUMMA)))) {
                        orderProduct1CDTO.setBaseSumma(new BigDecimal(String.valueOf(importReportItemPayload.getCellValueMap().get(ORDER.BASE_SUMMA))));
                    }
                    if (importReportItemPayload.getCellValueMap().get(ORDER.PROFIT_RATE) != null && StringUtils.isNotEmpty(String.valueOf(importReportItemPayload.getCellValueMap().get(ORDER.PROFIT_RATE)))) {
                        orderProduct1CDTO.setProfitRate(new BigDecimal(String.valueOf(importReportItemPayload.getCellValueMap().get(ORDER.PROFIT_RATE))));
                    }
                    if (importReportItemPayload.getCellValueMap().get(ORDER.DELIVER_SUM) != null && StringUtils.isNotEmpty(String.valueOf(importReportItemPayload.getCellValueMap().get(ORDER.DELIVER_SUM)))) {
                        orderProduct1CDTO.setDeliverySum(new BigDecimal(String.valueOf(importReportItemPayload.getCellValueMap().get(ORDER.DELIVER_SUM))));
                    }

                    if (importReportItemPayload.getCellValueMap().get(ORDER.VAT_PRICE) != null && StringUtils.isNotEmpty(String.valueOf(importReportItemPayload.getCellValueMap().get(ORDER.VAT_PRICE)))) {
                        orderProduct1CDTO.setVatSum(new BigDecimal(String.valueOf(importReportItemPayload.getCellValueMap().get(ORDER.VAT_PRICE))));
                    }

                    if (importReportItemPayload.getCellValueMap().get(ORDER.VAT_RATE) != null && StringUtils.isNotEmpty(String.valueOf(importReportItemPayload.getCellValueMap().get(ORDER.VAT_RATE)))) {
                        orderProduct1CDTO.setVatRate(new BigDecimal(String.valueOf(importReportItemPayload.getCellValueMap().get(ORDER.VAT_RATE))));
                    }

                    if (importReportItemPayload.getCellValueMap().get(ORDER.DELIVER_SUM_WITH_VAT) != null && StringUtils.isNotEmpty(String.valueOf(importReportItemPayload.getCellValueMap().get(ORDER.DELIVER_SUM_WITH_VAT)))) {
                        orderProduct1CDTO.setDeliverySumWithVat(new BigDecimal(String.valueOf(importReportItemPayload.getCellValueMap().get(ORDER.DELIVER_SUM_WITH_VAT))));
                    }
                    order1CDTO.addProduct(orderProduct1CDTO);

                });
        return orders;
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
                "ИНН"
        );
    }

    @Override
    String getErrorFileName() {
        return "ErrorCustomers";
    }

    private String getStringCellValue(XSSFCell cell) {
        if (cell == null) return null;
        if (cell.getCellType() == CellType.NUMERIC) {
            double d = cell.getNumericCellValue();
            if (d == Math.floor(d) && !Double.isInfinite(d)) {
                return String.valueOf((long) d);
            }
            return BigDecimal.valueOf(d).toPlainString();
        }
        cell.setCellType(CellType.STRING);
        String v = cell.getStringCellValue();
        return v == null ? null : v.trim();
    }
}
