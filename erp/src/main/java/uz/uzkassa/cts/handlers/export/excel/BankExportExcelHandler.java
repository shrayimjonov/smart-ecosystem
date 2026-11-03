package uz.uzkassa.cts.handlers.export.excel;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.domain.Bank;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.repository.BankRepository;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class BankExportExcelHandler extends AbstractExportExcelHandler<BaseFilter> {

    BankRepository bankRepository;

    @Override
    public String getFileName(BaseFilter filter) {
        return "Banks.xlsx";
    }

    @Override
    protected List<String> getHeaders() {
        return List.of("Название", "МФО", "Статус");
    }

    @Override
    protected XSSFWorkbook getXSSFWorkbook(BaseFilter filter) {
        XSSFWorkbook workbook = createWorkbook();
        XSSFSheet sheet = createSheet(workbook, getFileName(filter));
        createHeader(getHeaders(), sheet);
        filter.setPage(0);
        filter.setSize(EXCEL_EXPORT_MAX_LIMIT);
        List<Bank> result = bankRepository.getResultList(filter).getList();
        int rowIndex = 0;
        for (Bank bank : result) {
            Row row = sheet.createRow(++rowIndex);
            int cellIndex = 0;
            row.createCell(cellIndex++).setCellValue(bank.getNameRu());
            row.createCell(cellIndex++).setCellValue(bank.getCode());
            row.createCell(cellIndex).setCellValue(bank.getStatus().getName());
        }
        return workbook;
    }
}
