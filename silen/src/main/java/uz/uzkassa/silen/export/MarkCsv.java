package uz.uzkassa.silen.export;

import com.opencsv.CSVWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import uz.uzkassa.silen.domain.ExportReport;
import uz.uzkassa.silen.domain.mongo.Mark;
import uz.uzkassa.silen.dto.filter.MongoFilter;
import uz.uzkassa.silen.enumeration.ExportReportType;
import uz.uzkassa.silen.repository.mongo.MarkRepository;


@Slf4j
@Component
@RequiredArgsConstructor
public class MarkCsv extends BaseCsvExport implements IExportHandler {
    private final MarkRepository markRepository;

    @Override
    public ExportReportType getReportType() {
        return ExportReportType.CSV_MARK_KM;
    }

    @Override
    protected void writeContent(ExportReport exportReport, CSVWriter csvWriter) {
        final MongoFilter filter = readFilter(exportReport.getFilters(), MongoFilter.class);
        boolean hasNext;
        filter.setSize(10000);
        int pageNumber = 0;
        do {
            filter.setPage(pageNumber++);
            final Page<Mark> allByFilter = markRepository.findAllByFilter(filter);
            allByFilter.forEach(mark -> csvWriter.writeNext(new String[]{mark.getPrintCode()}, false));
            hasNext = allByFilter.hasNext();
        } while (hasNext);
    }

    @Override
    protected void writeHeader(ExportReport exportReport, CSVWriter csvWriter) {
    }
}
