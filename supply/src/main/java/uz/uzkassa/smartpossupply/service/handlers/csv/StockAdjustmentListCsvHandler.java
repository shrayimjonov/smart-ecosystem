package uz.uzkassa.smartpossupply.service.handlers.csv;

import com.opencsv.CSVWriter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.smartpossupply.dto.stockadjustment.StockAdjustmentListDTO;
import uz.uzkassa.smartpossupply.filters.StockAdjustmentFilter;
import uz.uzkassa.smartpossupply.service.StockAdjustmentService;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class StockAdjustmentListCsvHandler extends AbstractCsvHandler<StockAdjustmentFilter> {

    StockAdjustmentService stockAdjustmentService;

    @Override
    public String getFileName(StockAdjustmentFilter filter) {
        return "Stock_Adjustments.csv";
    }

    @Override
    protected LinkedList<String> getHeaders() {
        LinkedList<String> header = new LinkedList<>();
        header.add("Номер");
        header.add("Дата корректировки");
        header.add("Филиал");
        header.add("Склад");
        header.add("Количество");
        header.add("Статус");
        return header;
    }

    @Override
    protected void drawItemList(StockAdjustmentFilter filter, CSVWriter csvWriter) {
        List<StockAdjustmentListDTO> stockAdjustments = stockAdjustmentService.getList(filter).getContent();
        stockAdjustments.forEach(stockAdjustment ->
                csvWriter.writeNext(
                        new String[]{
                                stockAdjustment.getAdjustmentNumber(),
                                stockAdjustment.getAdjustmentDate().toLocalDate().toString(),
                                stockAdjustment.getBranch().getName(),
                                stockAdjustment.getWarehouse().getName(),
                                numberFormat.format(stockAdjustment.getTotalQty()),
                                Optional.ofNullable(stockAdjustment.getStatus().getName()).orElse("")
                        },
                        false
                )
        );
    }
}
