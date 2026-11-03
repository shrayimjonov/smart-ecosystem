package uz.uzkassa.cts.handlers.export.excel;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.domain.Fiscalization;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.repository.FiscalizationRepository;
import uz.uzkassa.cts.utils.DateUtil;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class FiscalizationExportExcelHandler extends AbstractExportExcelHandler<BaseFilter> {

    FiscalizationRepository fiscalizationRepository;

    @Override
    public String getFileName(BaseFilter filter) {
        return "Fiscalization.xlsx";
    }

    @Override
    protected List<String> getHeaders() {
        return List.of("Серийный номер", "Фискальный модуль", "Компания", "Филиал", "Клиент", "ТТ", "Дата", "Статус", "Кто создал");
    }

    @Override
    protected XSSFWorkbook getXSSFWorkbook(BaseFilter filter) {
        XSSFWorkbook workbook = createWorkbook();
        XSSFSheet sheet = createSheet(workbook, getFileName(filter));
        createHeader(getHeaders(), sheet);
        filter.setPage(0);
        filter.setSize(EXCEL_EXPORT_MAX_LIMIT);
        List<Fiscalization> result = fiscalizationRepository.getResultList(filter).getList();
        int rowIndex = 0;
        for (Fiscalization fiscalization : result) {
            Row row = sheet.createRow(++rowIndex);
            int cellIndex = 0;
            row.createCell(cellIndex++).setCellValue(fiscalization.getDevice().getSerialNumber());
            row.createCell(cellIndex++).setCellValue(fiscalization.getFiscalModule().getSerialNumber());
            row.createCell(cellIndex++).setCellValue(fiscalization.getBranch().getCompany().getName());
            row.createCell(cellIndex++).setCellValue(fiscalization.getBranch().getName());
            row.createCell(cellIndex++).setCellValue(fiscalization.getCustomer().getName());
            row.createCell(cellIndex++).setCellValue(fiscalization.getCustomerBranch().getName());
            row.createCell(cellIndex++).setCellValue(DateUtil.getLocalDateTimeAsStringWithoutSecond(fiscalization.getCreatedDate()));
            row.createCell(cellIndex++).setCellValue(fiscalization.getStatus().getName());
            row.createCell(cellIndex).setCellValue(fiscalization.getCreator().getFullName());
        }
        return workbook;
    }
}
