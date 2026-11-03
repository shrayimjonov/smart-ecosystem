package uz.uzkassa.smartpossupply.service.handlers.csv;

import com.opencsv.CSVWriter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.smartpossupply.filters.StockFilter;
import uz.uzkassa.smartpossupply.service.StockReturnService;

import java.util.LinkedList;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class StockReturnListCsvHandler extends AbstractCsvHandler<StockFilter> {

    StockReturnService stockReturnService;

    @Override
    public String getFileName(StockFilter filter) {
        StringBuilder fileName = new StringBuilder();
        fileName.append("Stock_Returns");
        if (filter.getFrom() != null && filter.getTo() != null) {
            fileName.append("_From_");
            fileName.append(filter.getFrom().toLocalDate().toString());
            fileName.append("_To_");
            fileName.append(filter.getTo().toLocalDate().toString());
        }
        fileName.append(".csv");
        return fileName.toString();
    }

    @Override
    protected LinkedList<String> getHeaders() {
        LinkedList<String> header = new LinkedList<>();
        header.add("Номер");
        header.add("Дата возврата");
        header.add("Клиент");
        header.add("Филиал");
        header.add("Склад");
        header.add("Статус");
        return header;
    }

    @Override
    protected void drawItemList(StockFilter filter, CSVWriter csvWriter) {
        stockReturnService
                .getList(filter)
                .getContent()
                .forEach(stock ->
                        csvWriter.writeNext(
                                new String[]{
                                        stock.getStockNumber(),
                                        stock.getTransferDate().toLocalDate().toString(),
                                        stock.getCustomer().getName(),
                                        stock.getBranch().getName(),
                                        stock.getWarehouse().getName(),
                                        stock.getStatus().getName(),
                                },
                                false
                        )
                );
    }
}
