package uz.uzkassa.smartpossupply.service.handlers.csv;

import com.opencsv.CSVWriter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.smartpossupply.filters.BalanceReportFilter;
import uz.uzkassa.smartpossupply.service.WarehouseProductService;

import java.util.LinkedList;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class StockBalanceReportCsvHandler extends AbstractCsvHandler<BalanceReportFilter> {

    WarehouseProductService warehouseProductService;

    @Override
    public String getFileName(BalanceReportFilter filter) {
        return "StockBalanceReport.csv";
    }

    @Override
    protected LinkedList<String> getHeaders() {
        LinkedList<String> header = new LinkedList<>();
        header.add("Наименование");
        header.add("ИКПУ");
        header.add("Штрих-код");
        header.add("Филиал");
        header.add("Склад");
        header.add("Кол-во");
        return header;
    }

    @Override
    protected void drawItemList(BalanceReportFilter filter, CSVWriter csvWriter) {
        warehouseProductService.getReport(filter).stream().toList().forEach(stockBalanceReport ->
                csvWriter.writeNext(
                        new String[]{
                                stockBalanceReport.getProduct().getName(),
                                stockBalanceReport.getProduct().getVatBarcode(),
                                stockBalanceReport.getProduct().getBarcode(),
                                stockBalanceReport.getBranch().getName(),
                                stockBalanceReport.getWarehouse().getName(),
                                stockBalanceReport.getQty().toPlainString(),
                        },
                        false
                )
        );
    }
}
