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

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@Slf4j
@RequiredArgsConstructor
public class PartyAggregationBlockCsv extends BaseCsvExport implements IExportHandler {
    private final MarkRepository markRepository;
    private final BlockRepository blockRepository;

    @Override
    public ExportReportType getReportType() {
        return ExportReportType.CSV_PARTY_AGGREGATION_BLOCK;
    }

    protected void writeContent(final ExportReport exportReport, final CSVWriter csvWriter) {
        final MongoFilter filter = readFilter(exportReport.getFilters(), MongoFilter.class);
        final boolean isFull = CodeLength.FULL == Optional.ofNullable(filter.getCodeLength()).orElse(CodeLength.SHORT);
        boolean hasNext;
        int pageNumber = 0;
        do {
            filter.setPage(pageNumber++);
            Page<Block> allByFilter = blockRepository.findAllByFilter(filter);
            final Map<String, String> parentCodes = allByFilter
                    .stream()
                    .collect(Collectors.toMap(Block::getCode, Block::getPrintCode));

            for (final String parentCode : parentCodes.keySet()) {
                csvWriter.writeNext(new String[]{isFull ? parentCodes.get(parentCode) : parentCode}, false);
                for (Mark mark : markRepository.findAllByParentCode(parentCode)) {
                    String markCode = isFull ? mark.getPrintCode() : mark.getCode();
                    csvWriter.writeNext(new String[]{"", markCode}, false);
                }
            }
            hasNext = allByFilter.hasNext();
        } while (hasNext);
    }

    @Override
    protected void writeHeader(final ExportReport exportReport, final CSVWriter csvWriter) {
    }
}
