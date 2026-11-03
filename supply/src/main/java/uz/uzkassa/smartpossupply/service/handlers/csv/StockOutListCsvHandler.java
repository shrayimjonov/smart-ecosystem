package uz.uzkassa.smartpossupply.service.handlers.csv;

import com.opencsv.CSVWriter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.smartpossupply.dto.stockout.StockOutListDTO;
import uz.uzkassa.smartpossupply.filters.StockFilter;
import uz.uzkassa.smartpossupply.service.StockOutService;

import java.util.LinkedList;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class StockOutListCsvHandler extends AbstractCsvHandler<StockFilter> {

    StockOutService stockOutService;

    @Override
    public String getFileName(StockFilter filter) {
        StringBuilder fileName = new StringBuilder();
        fileName.append("Stock_Out_List");
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
        header.add("Дата отгрузки");
        header.add("Клиент");
        header.add("Филиал");
        header.add("Склад");
        header.add("Заказ");
        header.add("Создана от");
        header.add("Сумма");
        header.add("Статус");
        return header;
    }

    @Override
    protected void drawItemList(StockFilter filter, CSVWriter csvWriter) {
        List<StockOutListDTO> resultList = stockOutService.getList(filter).getContent();
        resultList
                .forEach(stock ->
                        csvWriter.writeNext(
                                new String[]{
                                        stock.getStockNumber(),
                                        stock.getTransferDate().toLocalDate().toString(),
                                        stock.getCustomer().getName(),
                                        stock.getBranch().getName(),
                                        stock.getWarehouse() != null ? stock.getWarehouse().getName() : "",
                                        stock.getOrder() != null ? stock.getOrder().getName() : "",
                                        stock.getOldStock() != null ? stock.getOldStock().getName() : "",
                                        numberFormat.format(stock.getTotalPrice()),
                                        stock.getStatus().getName(),
                                },
                                false
                        )
                );
    }
}
