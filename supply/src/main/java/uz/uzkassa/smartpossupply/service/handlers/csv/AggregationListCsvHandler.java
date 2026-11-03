package uz.uzkassa.smartpossupply.service.handlers.csv;

import com.opencsv.CSVWriter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.smartpossupply.filters.AggregationFilter;
import uz.uzkassa.smartpossupply.service.AggregationService;

import java.time.LocalDate;
import java.util.LinkedList;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AggregationListCsvHandler extends AbstractCsvHandler<AggregationFilter> {

    AggregationService aggregationService;

    @Override
    public String getFileName(AggregationFilter filter) {
        StringBuilder fileName = new StringBuilder();
        fileName.append("Aggregations_From_");
        fileName.append(LocalDate.now());
        fileName.append(".csv");
        return fileName.toString();
    }

    @Override
    protected LinkedList<String> getHeaders() {
        return new LinkedList<>();
    }

    @Override
    protected void drawItemList(AggregationFilter filter, CSVWriter csvWriter) {
        aggregationService
                .getList(filter)
                .forEach(aggregation -> csvWriter.writeNext(new String[]{aggregation.getPrintCode()}, false));
    }
}
