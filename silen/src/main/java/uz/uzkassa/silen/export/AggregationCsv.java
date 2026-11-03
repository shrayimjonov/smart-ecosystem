package uz.uzkassa.silen.export;

import com.opencsv.CSVWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
public class AggregationCsv extends BaseCsvExport implements IExportHandler {

    private final MarkRepository markRepository;
    private final AggregationRepository aggregationRepository;

    @Override
    public ExportReportType getReportType() {
        return ExportReportType.CSV_AGGREGATION_BY_PRODUCT;
    }

    protected void writeContent(ExportReport exportReport, CSVWriter csvWriter) {
        final AggregationFilter filter = readFilter(exportReport.getFilters(), AggregationFilter.class);
        final boolean isFull = CodeLength.FULL == Optional.ofNullable(filter.getCodeLength()).orElse(CodeLength.SHORT);
        boolean hasNext;
        int pageNumber = 0;
        do {
            filter.setPage(pageNumber++);
            Page<Aggregation> allByFilter = aggregationRepository.findAllByFilter(filter);

            final Map<String, String> parentCodes = allByFilter.stream()
                    .collect(Collectors.toMap(Aggregation::getCode, Aggregation::getPrintCode));

            for (final String parentCode : parentCodes.keySet()) {
                csvWriter.writeNext(new String[]{isFull ? parentCodes.get(parentCode) : parentCode}, false);
                if (filter.isWithChild()) {
                    for (Mark mark : markRepository.findAllByParentCode(parentCode)) {
                        String markCode = isFull ? mark.getPrintCode() : mark.getCode();
                        csvWriter.writeNext(new String[]{"", markCode}, false);
                    }
                }
            }
            hasNext = allByFilter.hasNext();
        } while (hasNext);
    }

    protected void writeHeader(ExportReport exportReport, CSVWriter csvWriter) {
    }
}
