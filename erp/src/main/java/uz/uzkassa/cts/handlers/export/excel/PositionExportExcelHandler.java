package uz.uzkassa.cts.handlers.export.excel;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.domain.hrms.Position;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.repository.PositionRepository;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class PositionExportExcelHandler extends AbstractExportExcelHandler<BaseFilter> {

    PositionRepository positionRepository;

    @Override
    public String getFileName(BaseFilter filter) {
        return "Positions.xlsx";
    }

    @Override
    protected List<String> getHeaders() {
        return List.of("Название", "Статус");
    }

    @Override
    protected XSSFWorkbook getXSSFWorkbook(BaseFilter filter) {
        XSSFWorkbook workbook = createWorkbook();
        XSSFSheet sheet = createSheet(workbook, getFileName(filter));
        createHeader(getHeaders(), sheet);
        filter.setPage(0);
        filter.setSize(EXCEL_EXPORT_MAX_LIMIT);
        List<Position> result = positionRepository.findAllByDeletedIsFalse();
        int rowIndex = 0;
        for (Position position : result) {
            Row row = sheet.createRow(++rowIndex);
            int cellIndex = 0;
            row.createCell(cellIndex++).setCellValue(position.getName());
            row.createCell(cellIndex).setCellValue(position.getStatus().getName());
        }
        return workbook;
    }
}
