package uz.uzkassa.smartpossupply.service.handlers.csv;

import com.opencsv.CSVWriter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.smartpossupply.dto.stocktransfer.StockTransferListDTO;
import uz.uzkassa.smartpossupply.filters.StockTransferFilter;
import uz.uzkassa.smartpossupply.service.StockTransferService;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class StockTransferListCsvHandler extends AbstractCsvHandler<StockTransferFilter> {

    StockTransferService stockTransferService;

    @Override
    public String getFileName(StockTransferFilter filter) {
        return "Stock_Transfers.csv";
    }

    @Override
    protected LinkedList<String> getHeaders() {
        LinkedList<String> header = new LinkedList<>();
        header.add("Номер");
        header.add("Филиал отгрузка ");
        header.add("Склад отгрузка");
        header.add("Филиал приход");
        header.add("Склад приход");
        header.add("Количество");
        header.add("Дата");
        header.add("Статус");
        return header;
    }

    @Override
    protected void drawItemList(StockTransferFilter filter, CSVWriter csvWriter) {
        List<StockTransferListDTO> resultList = stockTransferService.getList(filter).getContent();
        resultList
                .forEach(stockTransfer ->
                        csvWriter.writeNext(
                                new String[]{
                                        stockTransfer.getTransferNumber(),
                                        stockTransfer.getFromBranch().getName(),
                                        stockTransfer.getFromWarehouse().getName(),
                                        stockTransfer.getToBranch().getName(),
                                        stockTransfer.getToWarehouse().getName(),
                                        numberFormat.format(stockTransfer.getTotalQty()),
                                        stockTransfer.getTransferDate().toLocalDate().toString(),
                                        Optional.ofNullable(stockTransfer.getStatus().getName()).orElse(""),
                                },
                                false
                        )
                );
    }
}
