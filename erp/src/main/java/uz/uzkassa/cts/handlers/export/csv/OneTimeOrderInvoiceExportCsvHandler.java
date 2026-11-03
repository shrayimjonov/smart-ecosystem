package uz.uzkassa.cts.handlers.export.csv;

import com.opencsv.CSVWriter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.enums.ContractStatus;
import uz.uzkassa.cts.enums.XfileStatus;
import uz.uzkassa.cts.filter.OneTimeOrderInvoiceFilter;
import uz.uzkassa.cts.repository.OneTimeOrderInvoiceRepository;
import uz.uzkassa.cts.utils.DateUtil;

import java.util.List;
import java.util.Optional;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class OneTimeOrderInvoiceExportCsvHandler extends AbstractExportCsvHandler<OneTimeOrderInvoiceFilter> {

    OneTimeOrderInvoiceRepository oneTimeOrderInvoiceRepository;

    @Override
    public String getFileName(OneTimeOrderInvoiceFilter filter) {
        return "One_Time_Order_Invoices.csv";
    }

    @Override
    protected List<String> getHeaders() {
        return List.of("Номер", "ИНН", "Клиент", "Договор", "Дата договора", "Счет-фактура", "Дата счет-фактуры", "Сумма", "Статус", "Статус x-file");
    }

    @Override
    protected void drawItemList(OneTimeOrderInvoiceFilter filter, CSVWriter csvWriter) {
        oneTimeOrderInvoiceRepository
                .getResultList(filter)
                .getList()
                .forEach(oneTimeOrderInvoice ->
                        csvWriter.writeNext(
                                new String[]{
                                        oneTimeOrderInvoice.getId().toString(),
                                        oneTimeOrderInvoice.getContract().getCustomer().getTin(),
                                        oneTimeOrderInvoice.getContract().getCustomer().getName(),
                                        oneTimeOrderInvoice.getContract().getNumber(),
                                        DateUtil.getLocalDateTimeAsStringWithoutSecond(oneTimeOrderInvoice.getContract().getCreatedDate()),
                                        oneTimeOrderInvoice.getNumber(),
                                        DateUtil.getLocalDateTimeAsStringWithoutSecond(oneTimeOrderInvoice.getInvoiceDate()),
                                        Optional.ofNullable(oneTimeOrderInvoice.getTotalSellPrice()).map(Object::toString).orElse(""),
                                        Optional.ofNullable(oneTimeOrderInvoice.getStatus()).map(ContractStatus::getName).orElse(""),
                                        Optional.ofNullable(oneTimeOrderInvoice.getXfileStatus()).map(XfileStatus::getName).orElse("")
                                },
                                false
                        )
                );
    }
}
