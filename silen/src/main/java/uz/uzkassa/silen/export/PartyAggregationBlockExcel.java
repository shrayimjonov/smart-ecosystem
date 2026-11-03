package uz.uzkassa.silen.export;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import uz.uzkassa.silen.domain.ExportReport;
import uz.uzkassa.silen.domain.mongo.Block;
import uz.uzkassa.silen.domain.mongo.Mark;
import uz.uzkassa.silen.dto.filter.MongoFilter;
import uz.uzkassa.silen.enumeration.CodeLength;
import uz.uzkassa.silen.enumeration.ExportReportType;
import uz.uzkassa.silen.repository.mongo.BlockRepository;
import uz.uzkassa.silen.repository.mongo.MarkRepository;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;


@Slf4j
@Component
@RequiredArgsConstructor
public class PartyAggregationBlockExcel extends BaseExcelExport implements IExportHandler {

    private final BlockRepository blockRepository;
    private final MarkRepository markRepository;

    @Override
    public ExportReportType getReportType() {
        return ExportReportType.EXCEL_PARTY_AGGREGATION_BLOCK;
    }

    @Override
    protected void writeHeader(final ExportReport exportReport, final XSSFSheet sheet) {
        sheet.setHorizontallyCenter(true);
        sheet.setVerticallyCenter(true);
    }

    @Override
    protected void writeBody(final ExportReport exportReport, final XSSFSheet sheet) {
        final MongoFilter filter = readFilter(exportReport.getFilters(), MongoFilter.class);
        final boolean isFull = CodeLength.FULL == Optional.ofNullable(filter.getCodeLength()).orElse(CodeLength.SHORT);
        int rowIndex = 0;
        boolean hasNext;
        int pageNumber = 0;
        do {
            filter.setPage(pageNumber++);
            Page<Block> allByFilter = blockRepository.findAllByFilter(filter);
            final Map<String, String> parentCodes = allByFilter.stream().collect(Collectors.toMap(Block::getCode, Block::getPrintCode));

            for (String block : parentCodes.keySet()) {
                XSSFRow row = sheet.createRow(rowIndex++);
                XSSFCell cell = row.createCell(0);
                cell.setCellValue(isFull ? parentCodes.get(block) : block);
                for (Mark mark : markRepository.findAllByParentCode(block)) {
                    String markCode = isFull ? mark.getPrintCode() : mark.getCode();
                    XSSFRow markRow = sheet.createRow(rowIndex++);
                    XSSFCell cell10 = markRow.createCell(1);
                    cell10.setCellValue(markCode);
                }
            }
            hasNext = allByFilter.hasNext();
        } while (hasNext);
    }
}
