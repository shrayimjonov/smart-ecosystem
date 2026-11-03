package uz.uzkassa.smartpossupply.service.handlers.csv;

import com.opencsv.CSVWriter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.smartpossupply.filters.AggregationBlockFilter;
import uz.uzkassa.smartpossupply.service.AggregationBlockService;

import java.time.LocalDate;
import java.util.LinkedList;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AggregationBlockListCsvHandler extends AbstractCsvHandler<AggregationBlockFilter> {

    AggregationBlockService aggregationBlockService;

    @Override
    public String getFileName(AggregationBlockFilter filter) {
        StringBuilder fileName = new StringBuilder();
        fileName.append("Aggregation_Blocks_From_");
        fileName.append(LocalDate.now());
        fileName.append(".csv");
        return fileName.toString();
    }

    @Override
    protected LinkedList<String> getHeaders() {
        return new LinkedList<>();
    }

    @Override
    protected void drawItemList(AggregationBlockFilter filter, CSVWriter csvWriter) {
        aggregationBlockService
                .getList(filter)
                .toList()
                .forEach(aggregation -> csvWriter.writeNext(new String[]{aggregation.getPrintCode()}, false));
    }
}
