package uz.uzkassa.cts.handlers.export.csv;

import com.opencsv.CSVWriter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.enums.ContractStatus;
import uz.uzkassa.cts.enums.XfileStatus;
import uz.uzkassa.cts.filter.TradeInInvoiceFilter;
import uz.uzkassa.cts.repository.SaleInvoiceRepository;
import uz.uzkassa.cts.utils.DateUtil;

import java.util.List;
import java.util.Optional;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class SaleInvoiceExportCsvHandler extends AbstractExportCsvHandler<TradeInInvoiceFilter> {

    SaleInvoiceRepository saleInvoiceRepository;

    @Override
    public String getFileName(TradeInInvoiceFilter filter) {
        return "Sale_Invoices.csv";
    }

    @Override
    protected List<String> getHeaders() {
        return List.of("ИНН", "Клиент", "Договор", "Дата договора", "Счет-фактура", "Дата счет-фактуры", "Сумма", "Статус", "Статус x-file");
    }

    @Override
    protected void drawItemList(TradeInInvoiceFilter filter, CSVWriter csvWriter) {
        saleInvoiceRepository
                .getResultList(filter)
                .getList()
                .forEach(saleInvoice ->
                        csvWriter.writeNext(
                                new String[]{
                                        saleInvoice.getSaleContract().getCustomer().getTin(),
                                        saleInvoice.getSaleContract().getCustomer().getName(),
                                        saleInvoice.getSaleContract().getNumber(),
                                        DateUtil.getLocalDateTimeAsStringWithoutSecond(saleInvoice.getSaleContract().getCreatedDate()),
                                        saleInvoice.getNumber(),
                                        DateUtil.getLocalDateTimeAsStringWithoutSecond(saleInvoice.getInvoiceDate()),
                                        Optional.ofNullable(saleInvoice.getTotalSellPrice()).map(Object::toString).orElse(""),
                                        Optional.ofNullable(saleInvoice.getStatus()).map(ContractStatus::getName).orElse(""),
                                        Optional.ofNullable(saleInvoice.getXfileStatus()).map(XfileStatus::getName).orElse("")
                                },
                                false
                        )
                );
    }
}
