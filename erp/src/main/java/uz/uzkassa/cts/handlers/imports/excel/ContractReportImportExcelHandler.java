package uz.uzkassa.cts.handlers.imports.excel;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.cts.constants.ImportConstants;
import uz.uzkassa.cts.domain.exportimport.ImportReport;
import uz.uzkassa.cts.dto.payload.ImportReportItemPayload;
import uz.uzkassa.cts.dto.payload.ImportReportPayload;
import uz.uzkassa.cts.enums.ExportReportStatus;
import uz.uzkassa.cts.repository.ContractReportRepository;
import uz.uzkassa.cts.service.ContractReportService;
import uz.uzkassa.cts.service.TransactionalExecutorService;
import uz.uzkassa.cts.web.rest.errors.BadRequestException;
import uz.uzkassa.cts.web.rest.errors.NotFoundException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created date time: 18.03.2025 16:18
 */

@Component
@Slf4j
@Transactional
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class ContractReportImportExcelHandler extends AbstractImportExcelHandler {

    ContractReportService contractReportService;
    ContractReportRepository contractReportRepository;
    TransactionalExecutorService transactionalExecutorService;

    @Override
    public List<ImportReportItemPayload> readFile(MultipartFile file) {
        List<ImportReportItemPayload> items = new ArrayList<>();
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
                    ImportReportItemPayload item = new ImportReportItemPayload();
                    row.getCell(cellCount).setCellType(CellType.STRING);
                    item.addCellValue(ImportConstants.CONTRACT_REPORT.SERIAL_NUMBER, row.getCell(cellCount).getStringCellValue().trim());
                    cellCount++;
                    if (row.getCell(cellCount) != null) {
                        row.getCell(cellCount).setCellType(CellType.STRING);
                        if (StringUtils.isNotEmpty(row.getCell(cellCount).getStringCellValue())) {
                            item.addCellValue(ImportConstants.CONTRACT_REPORT.TIN, row.getCell(cellCount).getStringCellValue().trim());
                        }
                    }
                    cellCount++;
                    if (row.getCell(cellCount) != null) {
                        row.getCell(cellCount).setCellType(CellType.STRING);
                        if (StringUtils.isNotEmpty(row.getCell(cellCount).getStringCellValue())) {
                            item.addCellValue(ImportConstants.CONTRACT_REPORT.NAME, row.getCell(cellCount).getStringCellValue().trim());
                        }
                    }
                    cellCount++;
                    if (row.getCell(cellCount) != null) {
                        row.getCell(cellCount).setCellType(CellType.STRING);
                        if (StringUtils.isNotEmpty(row.getCell(cellCount).getStringCellValue())) {
                            item.addCellValue(ImportConstants.CONTRACT_REPORT.CONTRACT_NUMBER, row.getCell(cellCount).getStringCellValue().trim());
                        }
                    }
                    cellCount++;
                    if (row.getCell(cellCount) != null) {
                        row.getCell(cellCount).setCellType(CellType.STRING);
                        if (StringUtils.isNotEmpty(row.getCell(cellCount).getStringCellValue())) {
                            item.addCellValue(ImportConstants.CONTRACT_REPORT.AMOUNT, row.getCell(cellCount).getStringCellValue().trim());
                        }
                    }
                    cellCount++;
                    if (row.getCell(cellCount) != null) {
                        row.getCell(cellCount).setCellType(CellType.STRING);
                        if (StringUtils.isNotEmpty(row.getCell(cellCount).getStringCellValue())) {
                            item.addCellValue(ImportConstants.CONTRACT_REPORT.INVOICE_STATUS, row.getCell(cellCount).getStringCellValue().trim());
                        }
                    }
                    cellCount++;
                    if (row.getCell(cellCount) != null) {
                        row.getCell(cellCount).setCellType(CellType.STRING);
                        if (StringUtils.isNotEmpty(row.getCell(cellCount).getStringCellValue())) {
                            item.addCellValue(ImportConstants.CONTRACT_REPORT.REALISATION, row.getCell(cellCount).getStringCellValue().trim());
                        }
                    }
                    cellCount++;
                    if (row.getCell(cellCount) != null) {
                        row.getCell(cellCount).setCellType(CellType.STRING);
                        if (StringUtils.isNotEmpty(row.getCell(cellCount).getStringCellValue())) {
                            item.addCellValue(ImportConstants.CONTRACT_REPORT.REGION_ID, row.getCell(cellCount).getStringCellValue().trim());
                        }
                    }
                    cellCount++;
                    if (row.getCell(cellCount) != null) {
                        row.getCell(cellCount).setCellType(CellType.STRING);
                        if (StringUtils.isNotEmpty(row.getCell(cellCount).getStringCellValue())) {
                            item.addCellValue(ImportConstants.CONTRACT_REPORT.COMMENT, row.getCell(cellCount).getStringCellValue().trim());
                        }
                    }
                    cellCount++;
                    if (row.getCell(cellCount) != null) {
                        row.getCell(cellCount).setCellType(CellType.STRING);
                        if (StringUtils.isNotEmpty(row.getCell(cellCount).getStringCellValue())) {
                            item.addCellValue(ImportConstants.CONTRACT_REPORT.DOCUMENT_STATUS, row.getCell(cellCount).getStringCellValue().trim());
                        }
                    }
                    cellCount++;
                    if (row.getCell(cellCount) != null) {
                        row.getCell(cellCount).setCellType(CellType.STRING);
                        if (StringUtils.isNotEmpty(row.getCell(cellCount).getStringCellValue())) {
                            item.addCellValue(ImportConstants.CONTRACT_REPORT.SALE_STATUS, row.getCell(cellCount).getStringCellValue().trim());
                        }
                    }
                    cellCount++;
                    if (row.getCell(cellCount) != null) {
                        row.getCell(cellCount).setCellType(CellType.STRING);
                        if (StringUtils.isNotEmpty(row.getCell(cellCount).getStringCellValue())) {
                            item.addCellValue(ImportConstants.CONTRACT_REPORT.DEVICE_REPLACEMENT, row.getCell(cellCount).getStringCellValue().trim());
                        }
                    }
                    cellCount++;
                    if (row.getCell(cellCount) != null) {
                        row.getCell(cellCount).setCellType(CellType.STRING);
                        if (StringUtils.isNotEmpty(row.getCell(cellCount).getStringCellValue())) {
                            item.addCellValue(ImportConstants.CONTRACT_REPORT.REASON, row.getCell(cellCount).getStringCellValue().trim());
                        }
                    }
                    cellCount++;
                    if (row.getCell(cellCount) != null) {
                        row.getCell(cellCount).setCellType(CellType.STRING);
                        if (StringUtils.isNotEmpty(row.getCell(cellCount).getStringCellValue())) {
                            item.addCellValue(ImportConstants.CONTRACT_REPORT.DATE, row.getCell(cellCount).getStringCellValue().trim());
                        }
                    }
                    cellCount++;
                    if (row.getCell(cellCount) != null) {
                        row.getCell(cellCount).setCellType(CellType.STRING);
                        if (StringUtils.isNotEmpty(row.getCell(cellCount).getStringCellValue())) {
                            item.addCellValue(ImportConstants.CONTRACT_REPORT.CHECK_STATUS, row.getCell(cellCount).getStringCellValue().trim());
                        }
                    }
                    items.add(item);
                }
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new BadRequestException("Не удалось загрузить файл");
        }
        return items;
    }

    @Override
    public void importReportQueue(ImportReportPayload payload) {
        ImportReport importReport = importReportRepository.findById(payload.getImportReportId()).orElseThrow(() -> new NotFoundException(ImportReport.class.getSimpleName()));
        int importedTotal = 0;
        int errorTotal = 0;
//        for (ImportReportItemPayload item : payload.getItems()) {
//
//            String serialNumber = String.valueOf(item.getCellValues().get(ImportConstants.CONTRACT_REPORT.SERIAL_NUMBER));
//            String modelCode = String.valueOf(item.getCellValues().get(ImportConstants.CONTRACT_REPORT.MODEL_CODE));
//
//            DeviceModel deviceModel = deviceModelRepository.findFirstByCode(modelCode);
//            DeviceDTO deviceDTO = new DeviceDTO();
//            deviceDTO.setSerialNumber(serialNumber);
//            deviceDTO.setModelId(deviceModel.getId());
//            deviceDTO.setType(DeviceType.PHYSICAL);
//            try {
//                transactionalExecutorService.runInNewTransaction(() -> deviceService.create(deviceDTO));
//                importedTotal++;
//            } catch (Exception e) {
//                log.error(e.getMessage(), e);
//                ImportReportItem importReportItem = new ImportReportItem();
//                importReportItem.setCellValue1(serialNumber);
//                importReportItem.setCellValue2(modelCode);
//                if (StringUtils.isNotEmpty(e.getMessage())) {
//                    importReportItem.setErrorMessage(e.getMessage());
//                }
//                importReportItem.setImportReportId(importReport.getId());
//                importReportItemRepository.save(importReportItem);
//                errorTotal++;
//            }
//        }
        importReport.setItemTotal(payload.getItems().size());
        importReport.setImportedTotal(importedTotal);
        importReport.setErrorTotal(errorTotal);
        importReport.setStatus(ExportReportStatus.COMPLETED);
        importReportRepository.save(importReport);
    }
}
