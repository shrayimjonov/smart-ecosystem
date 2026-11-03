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
import uz.uzkassa.silen.enumeration.ExportReportType;
import uz.uzkassa.silen.repository.mongo.BlockRepository;

@Slf4j
@Component
@RequiredArgsConstructor
public class BlockExcel extends BaseExcelExport implements IExportHandler {

    private final BlockRepository blockRepository;

    @Override
    public ExportReportType getReportType() {
        return ExportReportType.EXCEL_MARK_BLOCK;
    }

    @Override
    protected void writeHeader(final ExportReport exportReport, final XSSFSheet sheet) {
        sheet.setHorizontallyCenter(true);
        sheet.setVerticallyCenter(true);
    }

    @Override
    protected void writeBody(final ExportReport exportReport, final XSSFSheet sheet) {
        final MongoFilter filter = readFilter(exportReport.getFilters(), MongoFilter.class);
        int rowIndex = 0;
        int cellIndex = 0;
        boolean hasNext;
        int pageNumber = 0;
        do {
            filter.setPage(pageNumber++);
            final Page<Block> allByFilter = blockRepository.findAllByFilter(filter);
            for (Mark mark : allByFilter) {
                final XSSFRow row = sheet.createRow(rowIndex++);
                final XSSFCell cell = row.createCell(cellIndex);
                cell.setCellValue(mark.getPrintCode());
            }
            hasNext = allByFilter.hasNext();
        } while (hasNext);
    }
}
