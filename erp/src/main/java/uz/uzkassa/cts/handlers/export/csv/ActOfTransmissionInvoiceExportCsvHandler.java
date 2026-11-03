package uz.uzkassa.cts.handlers.export.csv;

import com.opencsv.CSVWriter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.domain.ActOfTransmission;
import uz.uzkassa.cts.domain.ContractForLeasing;
import uz.uzkassa.cts.domain.Customer;
import uz.uzkassa.cts.enums.ContractStatus;
import uz.uzkassa.cts.enums.XfileStatus;
import uz.uzkassa.cts.filter.PaymentScheduleInvoiceFilter;
import uz.uzkassa.cts.repository.ActOfTransmissionInvoiceRepository;
import uz.uzkassa.cts.utils.DateUtil;

import java.util.List;
import java.util.Optional;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class ActOfTransmissionInvoiceExportCsvHandler extends AbstractExportCsvHandler<PaymentScheduleInvoiceFilter> {

    ActOfTransmissionInvoiceRepository actOfTransmissionInvoiceRepository;

    @Override
    public String getFileName(PaymentScheduleInvoiceFilter filter) {
        return "Act_Of_Transmission_Invoices.csv";
    }

    @Override
    protected List<String> getHeaders() {
        return List.of("Контракт", "Акт", "ИНН", "Клиент", "Счет-фактура", "Дата счет-фактуры", "Сумма", "Статус", "Статус x-file");
    }

    @Override
    protected void drawItemList(PaymentScheduleInvoiceFilter filter, CSVWriter csvWriter) {
        actOfTransmissionInvoiceRepository
                .getResultList(filter)
                .getList()
                .forEach(actOfTransmissionInvoice ->
                        csvWriter.writeNext(
                                new String[]{
                                        Optional.ofNullable(actOfTransmissionInvoice.getActOfTransmission()).map(ActOfTransmission::getContract).map(ContractForLeasing::getNumber).orElse(""),
                                        Optional.ofNullable(actOfTransmissionInvoice.getActOfTransmission()).map(ActOfTransmission::getNumber).orElse(""),
                                        Optional.ofNullable(actOfTransmissionInvoice.getActOfTransmission()).map(ActOfTransmission::getContract).map(ContractForLeasing::getCustomer).map(Customer::getTin).orElse(""),
                                        Optional.ofNullable(actOfTransmissionInvoice.getActOfTransmission()).map(ActOfTransmission::getContract).map(ContractForLeasing::getCustomer).map(Customer::getName).orElse(""),
                                        actOfTransmissionInvoice.getNumber(),
                                        DateUtil.getLocalDateTimeAsStringWithoutSecond(actOfTransmissionInvoice.getInvoiceDate()),
                                        Optional.ofNullable(actOfTransmissionInvoice.getTotalSellPrice()).map(Object::toString).orElse(""),
                                        Optional.ofNullable(actOfTransmissionInvoice.getStatus()).map(ContractStatus::getName).orElse(""),
                                        Optional.ofNullable(actOfTransmissionInvoice.getXfileStatus()).map(XfileStatus::getName).orElse("")
                                },
                                false
                        )
                );
    }
}
