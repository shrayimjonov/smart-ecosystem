package uz.uzkassa.cts.handlers.export.csv;

import com.opencsv.CSVWriter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.filter.StockFilter;
import uz.uzkassa.cts.repository.StockRepository;
import uz.uzkassa.cts.utils.DateUtil;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class StockOutExportCsvHandler extends AbstractExportCsvHandler<StockFilter> {

    StockRepository stockRepository;

    @Override
    public String getFileName(StockFilter filter) {
        return "Stock_Outs.csv";
    }

    @Override
    protected List<String> getHeaders() {
        return List.of("Номер", "Компания", "Филиал", "Склад", "Отдел", "Дата", "Статус");
    }

    @Override
    protected void drawItemList(StockFilter filter, CSVWriter csvWriter) {
        stockRepository
                .getResultList(filter)
                .getList()
                .forEach(stock ->
                        csvWriter.writeNext(
                                new String[]{
                                        stock.getStockNumber(),
                                        stock.getCompany().getName(),
                                        stock.getBranch().getName(),
                                        stock.getWarehouse().getName(),
                                        stock.getDepartment().getName(),
                                        DateUtil.getLocalDateTimeAsStringWithoutSecond(stock.getStockDate()),
                                        stock.getStatus().getName()
                                },
                                false
                        )
                );
    }
}
