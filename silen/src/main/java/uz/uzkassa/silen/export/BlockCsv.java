package uz.uzkassa.silen.export;

import com.opencsv.CSVWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

import java.util.Optional;


@Slf4j
@Component
@RequiredArgsConstructor
public class BlockCsv extends BaseCsvExport implements IExportHandler {
    private final BlockRepository blockRepository;
    private final MarkRepository markRepository;

    @Override
    public ExportReportType getReportType() {
        return ExportReportType.CSV_MARK_BLOCK;
    }

    @Override
    protected void writeContent(final ExportReport exportReport, final CSVWriter csvWriter) {
        final MongoFilter filter = readFilter(exportReport.getFilters(), MongoFilter.class);
        boolean hasNext;
        int pageNumber = 0;
        final boolean isShort = Optional.ofNullable(filter.getCodeLength()).orElse(CodeLength.SHORT).equals(CodeLength.SHORT);
        do {
            filter.setPage(pageNumber++);
            final Page<Block> byFilter = blockRepository.findAllByFilter(filter);
            for (Block block : byFilter) {
                csvWriter.writeNext(new String[]{isShort ? block.getCode() : block.getPrintCode()}, false);
                for (Mark mark : markRepository.findAllByParentCode(block.getCode())) {
                    csvWriter.writeNext(new String[]{"", isShort ? mark.getCode() : mark.getPrintCode()}, false);
                }
            }
            hasNext = byFilter.hasNext();
        } while (hasNext);
    }

    @Override
    protected void writeHeader(ExportReport exportReport, CSVWriter csvWriter) {

    }
}
