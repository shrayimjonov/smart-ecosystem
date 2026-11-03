package uz.uzkassa.silen.export;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import uz.uzkassa.silen.domain.Aggregation;
import uz.uzkassa.silen.domain.ExportReport;
import uz.uzkassa.silen.domain.mongo.Mark;
import uz.uzkassa.silen.dto.filter.AggregationFilter;
import uz.uzkassa.silen.enumeration.CodeLength;
import uz.uzkassa.silen.enumeration.ExportReportType;
import uz.uzkassa.silen.repository.mongo.MarkRepository;
import uz.uzkassa.silen.repository.pg.AggregationRepository;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;


@Slf4j
@Component
@RequiredArgsConstructor
public class AggregationExcel extends BaseExcelExport implements IExportHandler {
    private final AggregationRepository aggregationRepository;
    private final MarkRepository markRepository;

    @Override
    public ExportReportType getReportType() {
        return ExportReportType.EXCEL_AGGREGATION_BY_PRODUCT;
    }

    protected void writeHeader(ExportReport exportReport, XSSFSheet sheet) {
    }

    protected void writeBody(ExportReport exportReport, XSSFSheet sheet) {
        final AggregationFilter filter = readFilter(exportReport.getFilters(), AggregationFilter.class);
        final boolean isFull = CodeLength.FULL == Optional.ofNullable(filter.getCodeLength()).orElse(CodeLength.SHORT);
        boolean hasNext;
        int pageNumber = 0;
        int rowIndex = 0;
        do {
            filter.setPage(pageNumber++);
            Page<Aggregation> allByFilter = aggregationRepository.findAllByFilter(filter);
            final Map<String, String> parentCodes = allByFilter
                    .stream().collect(Collectors.toMap(Aggregation::getCode, Aggregation::getPrintCode));

            for (String aggregation : parentCodes.keySet()) {
                XSSFRow row = sheet.createRow(rowIndex++);
                XSSFCell cell = row.createCell(0);
                cell.setCellValue(isFull ? parentCodes.get(aggregation) : aggregation);
                if (filter.isWithChild()) {
                    for (Mark mark : markRepository.findAllByParentCode(aggregation)) {
                        String markCode = isFull ? mark.getPrintCode() : mark.getCode();
                        XSSFRow markRow = sheet.createRow(rowIndex++);
                        XSSFCell cell10 = markRow.createCell(1);
                        cell10.setCellValue(markCode);
                    }
                }
            }
            hasNext = allByFilter.hasNext();
        } while (hasNext);
    }
}
