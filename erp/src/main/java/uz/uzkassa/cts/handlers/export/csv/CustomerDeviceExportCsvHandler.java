package uz.uzkassa.cts.handlers.export.csv;

import com.opencsv.CSVWriter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.repository.CustomerDeviceRepository;
import uz.uzkassa.cts.utils.DateUtil;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CustomerDeviceExportCsvHandler extends AbstractExportCsvHandler<BaseFilter> {

    CustomerDeviceRepository customerDeviceRepository;

    @Override
    public String getFileName(BaseFilter filter) {
        return "Customer_Devices.csv";
    }

    @Override
    protected List<String> getHeaders() {
        return List.of("Серийный номер", "Дата регистрации", "Статус", "Создатель", "Компания", "Филиал", "Клиент");
    }

    @Override
    protected void drawItemList(BaseFilter filter, CSVWriter csvWriter) {
        customerDeviceRepository
                .getResultList(filter)
                .getList()
                .forEach(customerDevice ->
                        csvWriter.writeNext(
                                new String[]{
                                        customerDevice.getDevice().getSerialNumber(),
                                        DateUtil.getLocalDateTimeAsStringWithoutSecond(customerDevice.getCreatedDate()),
                                        customerDevice.getStatus().getName(),
                                        customerDevice.getCreator().getName(),
                                        customerDevice.getCustomer().getCompany().getName(),
                                        customerDevice.getCustomer().getBranch().getName(),
                                        customerDevice.getCustomer().getName()
                                },
                                false
                        )
                );
    }
}
