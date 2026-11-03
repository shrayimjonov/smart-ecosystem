package uz.uzkassa.cts.handlers.export.csv;

import com.opencsv.CSVWriter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.filter.CustomerRequestFilter;
import uz.uzkassa.cts.repository.CustomerRequestRepository;
import uz.uzkassa.cts.utils.DateUtil;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CustomerRequestExportCsvHandler extends AbstractExportCsvHandler<CustomerRequestFilter> {

    CustomerRequestRepository customerRequestRepository;

    @Override
    public String getFileName(CustomerRequestFilter filter) {
        return "Customer_Requests.csv";
    }

    @Override
    protected List<String> getHeaders() {
        return List.of("Название организации", "ИНН", "Номер", "Дата", "Тип", "Статус по звонку", "Время обработки", "Статус по результату обзвона");
    }

    @Override
    protected void drawItemList(CustomerRequestFilter filter, CSVWriter csvWriter) {
        customerRequestRepository
                .getResultList(filter)
                .getList()
                .forEach(customerRequest ->
                        csvWriter.writeNext(
                                new String[]{
                                        customerRequest.getCustomer().getName(),
                                        customerRequest.getCustomer().getTin(),
                                        customerRequest.getCustomer().getPhone(),
                                        DateUtil.getLocalDateTimeAsStringWithoutSecond(customerRequest.getCreatedDate()),
                                        customerRequest.getRequestType().getName(),
                                        customerRequest.getCallStatus().getName(),
                                        DateUtil.getLocalDateTimeAsStringWithoutSecond(customerRequest.getResolvedDate()),
                                        customerRequest.getStatus().getName()
                                },
                                false
                        )
                );
    }
}
