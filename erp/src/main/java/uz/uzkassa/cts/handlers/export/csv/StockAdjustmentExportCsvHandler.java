package uz.uzkassa.cts.handlers.export.csv;

import com.opencsv.CSVWriter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.filter.StockFilter;
import uz.uzkassa.cts.repository.StockAdjustmentRepository;
import uz.uzkassa.cts.utils.DateUtil;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class StockAdjustmentExportCsvHandler extends AbstractExportCsvHandler<StockFilter> {

    StockAdjustmentRepository stockAdjustmentRepository;

    @Override
    public String getFileName(StockFilter filter) {
        return "Stock_Adjustments.csv";
    }

    @Override
    protected List<String> getHeaders() {
        return List.of("Номер", "Компания", "Филиал", "Склад", "Дата", "Статус");
    }

    @Override
    protected void drawItemList(StockFilter filter, CSVWriter csvWriter) {
        stockAdjustmentRepository
                .getResultList(filter)
                .getList()
                .forEach(stock ->
                        csvWriter.writeNext(
                                new String[]{
                                        stock.getAdjustmentNumber(),
                                        stock.getCompany().getName(),
                                        stock.getBranch().getName(),
                                        stock.getWarehouse().getName(),
                                        DateUtil.getLocalDateTimeAsStringWithoutSecond(stock.getAdjustmentDate()),
                                        stock.getStatus().getName()
                                },
                                false
                        )
                );
    }
}
