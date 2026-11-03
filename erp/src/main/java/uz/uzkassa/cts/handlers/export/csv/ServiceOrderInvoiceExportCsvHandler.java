package uz.uzkassa.cts.handlers.export.csv;

import com.opencsv.CSVWriter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.enums.ContractStatus;
import uz.uzkassa.cts.enums.XfileStatus;
import uz.uzkassa.cts.filter.ServiceOrderInvoiceFilter;
import uz.uzkassa.cts.repository.ServiceOrderInvoiceRepository;
import uz.uzkassa.cts.utils.DateUtil;

import java.util.List;
import java.util.Optional;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class ServiceOrderInvoiceExportCsvHandler extends AbstractExportCsvHandler<ServiceOrderInvoiceFilter> {

    ServiceOrderInvoiceRepository serviceOrderInvoiceRepository;

    @Override
    public String getFileName(ServiceOrderInvoiceFilter filter) {
        return "Service_Order_Invoices.csv";
    }

    @Override
    protected List<String> getHeaders() {
        return List.of("Номер", "ИНН", "Клиент", "Договор", "Дата договора", "Счет-фактура", "Дата счет-фактуры", "Сумма", "Статус", "Статус x-file");
    }

    @Override
    protected void drawItemList(ServiceOrderInvoiceFilter filter, CSVWriter csvWriter) {
        serviceOrderInvoiceRepository
                .getResultList(filter)
                .getList()
                .forEach(serviceOrderInvoice ->
                        csvWriter.writeNext(
                                new String[]{
                                        serviceOrderInvoice.getId().toString(),
                                        serviceOrderInvoice.getContract().getCustomer().getTin(),
                                        serviceOrderInvoice.getContract().getCustomer().getName(),
                                        serviceOrderInvoice.getContract().getNumber(),
                                        DateUtil.getLocalDateTimeAsStringWithoutSecond(serviceOrderInvoice.getContract().getCreatedDate()),
                                        serviceOrderInvoice.getNumber(),
                                        DateUtil.getLocalDateTimeAsStringWithoutSecond(serviceOrderInvoice.getInvoiceDate()),
                                        Optional.ofNullable(serviceOrderInvoice.getTotalSellPrice()).map(Object::toString).orElse(""),
                                        Optional.ofNullable(serviceOrderInvoice.getStatus()).map(ContractStatus::getName).orElse(""),
                                        Optional.ofNullable(serviceOrderInvoice.getXfileStatus()).map(XfileStatus::getName).orElse("")
                                },
                                false
                        )
                );
    }
}
