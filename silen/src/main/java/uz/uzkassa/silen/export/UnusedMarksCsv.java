package uz.uzkassa.silen.export;

import com.opencsv.CSVWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uz.uzkassa.silen.domain.ExportReport;
import uz.uzkassa.silen.domain.mongo.Mark;
import uz.uzkassa.silen.dto.filter.OrderFilter;
import uz.uzkassa.silen.enumeration.ExportReportType;
import uz.uzkassa.silen.repository.mongo.MarkRepository;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Slf4j
@Component
@RequiredArgsConstructor
public class UnusedMarksCsv extends BaseCsvExport implements IExportHandler {
    private final MarkRepository markRepository;

    @Override
    public ExportReportType getReportType() {
        return ExportReportType.CSV_UNUSED_MAKRS;
    }

    @Override
    protected void writeContent(ExportReport exportReport, CSVWriter csvWriter) {
        final OrderFilter filter = readFilter(exportReport.getFilters(), OrderFilter.class);

        for (String party : filter.getParties()) {
            Stream<Mark> marks = markRepository.findAllByPartyId(party).stream();
            marks = marks.filter(m -> !m.isUsed());
            final Set<String> codes = marks.map(Mark::getPrintCode).collect(Collectors.toSet());
            codes.forEach(printCode -> csvWriter.writeNext(new String[]{printCode}, false));
        }
    }

    @Override
    protected void writeHeader(ExportReport exportReport, CSVWriter csvWriter) {
    }
}
