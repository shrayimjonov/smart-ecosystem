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
import uz.uzkassa.cts.domain.exportimport.ImportReport;
import uz.uzkassa.cts.domain.exportimport.ImportReportItem;
import uz.uzkassa.cts.dto.fm.FiscalModuleDTO;
import uz.uzkassa.cts.dto.payload.ImportReportItemPayload;
import uz.uzkassa.cts.dto.payload.ImportReportPayload;
import uz.uzkassa.cts.enums.ExportReportStatus;
import uz.uzkassa.cts.service.FiscalModuleService;
import uz.uzkassa.cts.service.TransactionalExecutorService;
import uz.uzkassa.cts.web.rest.errors.BadRequestException;
import uz.uzkassa.cts.web.rest.errors.NotFoundException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
@Transactional
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class FiscalModuleImportExcelHandler extends AbstractImportExcelHandler {

    FiscalModuleService fiscalModuleService;
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
                    item.addCellValue(FISCAL_MODULE.SERIAL_NUMBER, row.getCell(cellCount).getStringCellValue().trim());
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
            String serialNumber = String.valueOf(item.getCellValues().get(DEVICE.SERIAL_NUMBER));
            try {
                transactionalExecutorService.runInNewTransaction(() -> fiscalModuleService.create(new FiscalModuleDTO(serialNumber)));
                importedTotal++;
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                ImportReportItem importReportItem = new ImportReportItem();
                importReportItem.setCellValue1(serialNumber);
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
