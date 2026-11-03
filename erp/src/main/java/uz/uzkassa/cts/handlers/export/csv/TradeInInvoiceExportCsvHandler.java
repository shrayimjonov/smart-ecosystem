package uz.uzkassa.cts.handlers.export.csv;

import com.opencsv.CSVWriter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.enums.ContractStatus;
import uz.uzkassa.cts.enums.XfileStatus;
import uz.uzkassa.cts.filter.TradeInInvoiceFilter;
import uz.uzkassa.cts.repository.TradeInInvoiceRepository;
import uz.uzkassa.cts.utils.DateUtil;

import java.util.List;
import java.util.Optional;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class TradeInInvoiceExportCsvHandler extends AbstractExportCsvHandler<TradeInInvoiceFilter> {

    TradeInInvoiceRepository tradeInInvoiceRepository;

    @Override
    public String getFileName(TradeInInvoiceFilter filter) {
        return "Trade_In_Invoices.csv";
    }

    @Override
    protected List<String> getHeaders() {
        return List.of("ИНН", "Клиент", "Договор", "Дата договора", "Счет-фактура", "Дата счет-фактуры", "Сумма", "Статус", "Статус x-file");
    }

    @Override
    protected void drawItemList(TradeInInvoiceFilter filter, CSVWriter csvWriter) {
        tradeInInvoiceRepository
                .getResultList(filter)
                .getList()
                .forEach(tradeInInvoice ->
                        csvWriter.writeNext(
                                new String[]{
                                        tradeInInvoice.getContract().getCustomer().getTin(),
                                        tradeInInvoice.getContract().getCustomer().getName(),
                                        tradeInInvoice.getContract().getNumber(),
                                        DateUtil.getLocalDateTimeAsStringWithoutSecond(tradeInInvoice.getContract().getCreatedDate()),
                                        tradeInInvoice.getNumber(),
                                        DateUtil.getLocalDateTimeAsStringWithoutSecond(tradeInInvoice.getInvoiceDate()),
                                        Optional.ofNullable(tradeInInvoice.getTotalSellPrice()).map(Object::toString).orElse(""),
                                        Optional.ofNullable(tradeInInvoice.getStatus()).map(ContractStatus::getName).orElse(""),
                                        Optional.ofNullable(tradeInInvoice.getXfileStatus()).map(XfileStatus::getName).orElse("")
                                },
                                false
                        )
                );
    }
}
