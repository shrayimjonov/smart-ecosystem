package uz.uzkassa.smartpossupply.service.handlers.csv;

import com.opencsv.CSVWriter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.smartpossupply.filters.BalanceReportFilter;
import uz.uzkassa.smartpossupply.service.ProductSerialService;

import java.util.LinkedList;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class ProductReportBySerialCsvHandler extends AbstractCsvHandler<BalanceReportFilter> {

    ProductSerialService productSerialService;

    @Override
    public String getFileName(BalanceReportFilter filter) {
        return "ProductReportBySerial.csv";
    }

    @Override
    protected LinkedList<String> getHeaders() {
        LinkedList<String> header = new LinkedList<>();
        header.add("Название товара");
        header.add("Штрихкод товара");
        header.add("ИКПУ");
        header.add("Серия");
        header.add("Филиал");
        header.add("Склад");
        header.add("Кол-во");
        header.add("Цена");
        header.add("Сумма");
        return header;
    }

    @Override
    protected void drawItemList(BalanceReportFilter filter, CSVWriter csvWriter) {
        productSerialService
                .getReport(filter)
                .getContent()
                .forEach(productSerialReportDTO ->
                        csvWriter.writeNext(
                                new String[]{
                                        productSerialReportDTO.getProduct().getName(),
                                        productSerialReportDTO.getProduct().getBarcode(),
                                        productSerialReportDTO.getProduct().getVatBarcode(),
                                        productSerialReportDTO.getProductSerial().getName(),
                                        productSerialReportDTO.getBranch().getName(),
                                        productSerialReportDTO.getWarehouse().getName(),
                                        productSerialReportDTO.getQty().toString(),
                                        productSerialReportDTO.getPrice().toString(),
                                        productSerialReportDTO.getTotalPrice().toString(),
                                },
                                false
                        )
                );
    }
}
