package uz.uzkassa.smartpossupply.service.handlers.csv;

import com.opencsv.CSVWriter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import uz.uzkassa.smartpossupply.dto.stockadjustment.StockAdjustmentAggregationCodesDTO;
import uz.uzkassa.smartpossupply.filters.StockAdjustmentFilter;
import uz.uzkassa.smartpossupply.service.StockAdjustmentService;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class StockAdjustmentAggregationCodesCsvHandler extends AbstractCsvHandler<StockAdjustmentFilter> {

    StockAdjustmentService stockAdjustmentService;

    @Override
    public String getFileName(StockAdjustmentFilter filter) {
        return "Stock_Adjustment_Aggregation_Codes.csv";
    }

    @Override
    protected LinkedList<String> getHeaders() {
        LinkedList<String> header = new LinkedList<>();
        header.add("Коды");
        return header;
    }

    @Override
    protected void drawItemList(StockAdjustmentFilter filter, CSVWriter csvWriter) {
        List<String> stockAdjustments = stockAdjustmentService.getCodes(filter.getId()).stream().map(StockAdjustmentAggregationCodesDTO::getCodes).flatMap(Collection::stream).toList();
        stockAdjustments.forEach(code -> {
                    if (StringUtils.isNumeric(code)) {
                        code = "=\"" + code + "\"";
                    }
                    csvWriter.writeNext(
                            new String[]{
                                    code
                            },
                            false
                    );
                }
        );
    }
}