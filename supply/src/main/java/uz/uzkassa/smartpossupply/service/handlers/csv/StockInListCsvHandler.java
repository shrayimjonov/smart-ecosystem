package uz.uzkassa.smartpossupply.service.handlers.csv;

import com.opencsv.CSVWriter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.smartpossupply.dto.stockin.StockInListDTO;
import uz.uzkassa.smartpossupply.filters.StockFilter;
import uz.uzkassa.smartpossupply.service.StockInService;

import java.util.LinkedList;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class StockInListCsvHandler extends AbstractCsvHandler<StockFilter> {

    StockInService stockInService;

    @Override
    public String getFileName(StockFilter filter) {
        StringBuilder fileName = new StringBuilder();
        fileName.append("Stock_In_List");
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
        header.add("Дата прихода");
        header.add("Поставщик");
        header.add("Филиал");
        header.add("Склад");
        header.add("Создан от");
        header.add("Сумма прихода");
        header.add("Статус");
        return header;
    }

    @Override
    protected void drawItemList(StockFilter filter, CSVWriter csvWriter) {
        List<StockInListDTO> resultList = stockInService.getList(filter).getContent();
        resultList
                .forEach(stock ->
                        csvWriter.writeNext(
                                new String[]{
                                        stock.getStockNumber(),
                                        stock.getTransferDate().toLocalDate().toString(),
                                        stock.getSupplier().getName(),
                                        stock.getBranch().getName(),
                                        stock.getWarehouse().getName(),
                                        stock.getOldStock() != null ? stock.getOldStock().getName() : "",
                                        numberFormat.format(stock.getTotalPrice()),
                                        stock.getStatus().getName(),
                                },
                                false
                        )
                );
    }
}
