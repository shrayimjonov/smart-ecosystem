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
import uz.uzkassa.cts.domain.exportimport.ImportReportItem;
import uz.uzkassa.cts.dto.fm.FiscalModuleReportRequestDTO;
import uz.uzkassa.cts.dto.payload.ImportReportItemPayload;
import uz.uzkassa.cts.dto.payload.ImportReportPayload;
import uz.uzkassa.cts.enums.ArrivalStatus;
import uz.uzkassa.cts.enums.ExportReportStatus;
import uz.uzkassa.cts.enums.FMReportReason;
import uz.uzkassa.cts.enums.SoliqTransferStatus;
import uz.uzkassa.cts.repository.CustomerRepository;
import uz.uzkassa.cts.service.FiscalModuleReportService;
import uz.uzkassa.cts.service.TransactionalExecutorService;
import uz.uzkassa.cts.web.rest.errors.BadRequestException;
import uz.uzkassa.cts.web.rest.errors.NotFoundException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


@Component
@Slf4j
@Transactional
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class FiscalModuleReportImportExcelHandler extends AbstractImportExcelHandler {

    FiscalModuleReportService fiscalModuleReportService;
    CustomerRepository customerRepository;
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
                    item.addCellValue(FISCAL_MODULE_REPORT.TIN, row.getCell(cellCount).getStringCellValue().trim());
                    cellCount++;
                    if (row.getCell(cellCount) != null) {
                        row.getCell(cellCount).setCellType(CellType.STRING);
                        if (StringUtils.isNotEmpty(row.getCell(cellCount).getStringCellValue())) {
                            item.addCellValue(FISCAL_MODULE_REPORT.DEVICE_SERIAL_NUMBER, row.getCell(cellCount).getStringCellValue().trim());
                        }
                    }
                    cellCount++;
                    if (row.getCell(cellCount) != null) {
                        row.getCell(cellCount).setCellType(CellType.STRING);
                        if (StringUtils.isNotEmpty(row.getCell(cellCount).getStringCellValue())) {
                            item.addCellValue(FISCAL_MODULE_REPORT.FM_SERIAL_NUMBER, row.getCell(cellCount).getStringCellValue().trim());
                        }
                    }
                    cellCount++;
                    if (row.getCell(cellCount) != null) {
                        row.getCell(cellCount).setCellType(CellType.STRING);
                        if (StringUtils.isNotEmpty(row.getCell(cellCount).getStringCellValue())) {
                            item.addCellValue(FISCAL_MODULE_REPORT.FM_REPORT_REASON, row.getCell(cellCount).getStringCellValue().split(" - ")[1].trim());
                        }
                    }
                    cellCount++;
                    if (row.getCell(cellCount) != null) {
                        row.getCell(cellCount).setCellType(CellType.STRING);
                        if (StringUtils.isNotEmpty(row.getCell(cellCount).getStringCellValue())) {
                            item.addCellValue(FISCAL_MODULE_REPORT.ARRIVAL_STATUS, row.getCell(cellCount).getStringCellValue().split(" - ")[1].trim());
                        }
                    }
                    cellCount++;
                    if (row.getCell(cellCount) != null) {
                        row.getCell(cellCount).setCellType(CellType.STRING);
                        if (StringUtils.isNotEmpty(row.getCell(cellCount).getStringCellValue())) {
                            item.addCellValue(FISCAL_MODULE_REPORT.SOLIQ_TRANSFER_STATUS, (row.getCell(cellCount).getStringCellValue().split(" - ")[1].trim()));
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
        for (ImportReportItemPayload item : payload.getItems()) {

            Long customerId = customerRepository.getIdByTin(String.valueOf(item.getCellValues().get(FISCAL_MODULE_REPORT.TIN)));
            String deviceSerialNumber = String.valueOf(item.getCellValues().get(ImportConstants.FISCAL_MODULE_REPORT.DEVICE_SERIAL_NUMBER));
            String fmSerialNumber = String.valueOf(item.getCellValues().get(ImportConstants.FISCAL_MODULE_REPORT.FM_SERIAL_NUMBER));
            FMReportReason fmReportReason = FMReportReason.get(String.valueOf(item.getCellValues().get(FISCAL_MODULE_REPORT.FM_REPORT_REASON)));
            ArrivalStatus arrivalStatus = ArrivalStatus.get(String.valueOf(item.getCellValues().get(FISCAL_MODULE_REPORT.ARRIVAL_STATUS)));
            SoliqTransferStatus soliqTransferStatus = SoliqTransferStatus.get(String.valueOf(item.getCellValues().get(FISCAL_MODULE_REPORT.SOLIQ_TRANSFER_STATUS)));

            FiscalModuleReportRequestDTO fiscalModuleReportRequestDTO = new FiscalModuleReportRequestDTO();
            fiscalModuleReportRequestDTO.setCustomerId(customerId);
            fiscalModuleReportRequestDTO.setDeviceSerialNumber(deviceSerialNumber);
            fiscalModuleReportRequestDTO.setFmSerialNumber(fmSerialNumber);
            fiscalModuleReportRequestDTO.setReason(fmReportReason);
            fiscalModuleReportRequestDTO.setStatus(arrivalStatus);
            fiscalModuleReportRequestDTO.setSoliqTransferStatus(soliqTransferStatus);

            try {
                transactionalExecutorService.runInNewTransaction(() -> fiscalModuleReportService.create(fiscalModuleReportRequestDTO));
                importedTotal++;
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                ImportReportItem importReportItem = new ImportReportItem();
                importReportItem.setCellValue1(String.valueOf(customerId));
                importReportItem.setCellValue2(deviceSerialNumber);
                importReportItem.setCellValue3(fmSerialNumber);
                importReportItem.setCellValue4(String.valueOf(fmReportReason));
                importReportItem.setCellValue5(String.valueOf(arrivalStatus));
                importReportItem.setCellValue6(String.valueOf(soliqTransferStatus));
                if (StringUtils.isNotEmpty(e.getMessage())) {
                    importReportItem.setErrorMessage(e.getMessage());
                }
                importReportItem.setImportReportId(importReport.getId());
                importReportItemRepository.save(importReportItem);
                errorTotal++;
            }
        }
        importReport.setItemTotal(payload.getItems().size());
        importReport.setImportedTotal(importedTotal);
        importReport.setErrorTotal(errorTotal);
        importReport.setStatus(ExportReportStatus.COMPLETED);
        importReportRepository.save(importReport);
    }
}
