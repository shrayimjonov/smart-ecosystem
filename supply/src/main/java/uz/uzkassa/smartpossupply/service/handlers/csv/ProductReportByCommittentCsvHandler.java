package uz.uzkassa.smartpossupply.service.handlers.csv;

import com.opencsv.CSVWriter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.smartpossupply.filters.BalanceReportFilter;
import uz.uzkassa.smartpossupply.service.CompanyCommittentService;

import java.util.LinkedList;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class ProductReportByCommittentCsvHandler extends AbstractCsvHandler<BalanceReportFilter> {

    CompanyCommittentService companyCommittentService;

    @Override
    public String getFileName(BalanceReportFilter filter) {
        return "ProductReportByCommittent.csv";
    }

    @Override
    protected LinkedList<String> getHeaders() {
        LinkedList<String> header = new LinkedList<>();
        header.add("Название товара");
        header.add("Штрихкод товара");
        header.add("ИКПУ");
        header.add("Комитент");
        header.add("Филиал");
        header.add("Склад");
        header.add("Кол-во");
        header.add("Цена");
        header.add("Сумма");
        return header;
    }

    @Override
    protected void drawItemList(BalanceReportFilter filter, CSVWriter csvWriter) {
        companyCommittentService
                .getReport(filter)
                .getContent()
                .forEach(productSerialReportDTO ->
                        csvWriter.writeNext(
                                new String[]{
                                        productSerialReportDTO.getProduct().getName(),
                                        productSerialReportDTO.getProduct().getBarcode(),
                                        productSerialReportDTO.getProduct().getVatBarcode(),
                                        productSerialReportDTO.getCommittent().getName().concat(" ").concat(productSerialReportDTO.getCommittent().getTin()),
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
