package uz.uzkassa.smartpossupply.service.handlers.csv;

import com.opencsv.CSVWriter;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import uz.uzkassa.smartpossupply.enums.CodeType;
import uz.uzkassa.smartpossupply.filters.StockProductFilter;
import uz.uzkassa.smartpossupply.repository.StockProductAggregationBlockRepository;
import uz.uzkassa.smartpossupply.repository.StockProductAggregationRepository;
import uz.uzkassa.smartpossupply.repository.StockProductMarkRepository;

import java.util.*;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 12.06.2022 15:59
 */
@Component
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class StockInDetailCsvHandler extends AbstractCsvHandler<StockProductFilter> {

    StockProductAggregationRepository stockProductAggregationRepository;
    StockProductMarkRepository stockProductMarkRepository;
    StockProductAggregationBlockRepository stockProductAggregationBlockRepository;

    @Override
    public String getFileName(StockProductFilter filter) {
        return "Stock_In_Detail.csv";
    }

    @Override
    protected LinkedList<String> getHeaders() {
        return new LinkedList<>();
    }

    @Override
    protected void drawItemList(StockProductFilter filter, CSVWriter csvWriter) {
        Map<CodeType, List<String>> result = new HashMap<>();
        result.put(
                CodeType.AGGREGATION,
                new ArrayList<>(stockProductAggregationRepository.findAllCodesByStockProductIdAndDeletedIsFalse(filter.getStockProductId()))
        );
        if (!CollectionUtils.isEmpty(result)) {
            result.forEach((codeType, strings) -> strings.forEach(code -> csvWriter.writeNext(new String[]{code})));
            return;
        }
        result.put(
                CodeType.MARK,
                new ArrayList<>(stockProductMarkRepository.findAllCodesByStockProductIdAndDeletedIsFalse(filter.getStockProductId()))
        );
        if (!CollectionUtils.isEmpty(result)) {
            result.forEach((codeType, strings) -> strings.forEach(code -> csvWriter.writeNext(new String[]{code})));
            return;
        }
        result.put(
                CodeType.BLOCK,
                new ArrayList<>(stockProductAggregationBlockRepository.findAllCodesByStockProductIdAndDeletedIsFalse(filter.getStockProductId()))
        );
        if (!CollectionUtils.isEmpty(result)) {
            result.forEach((codeType, strings) -> strings.forEach(code -> csvWriter.writeNext(new String[]{code})));
        }
    }
}
