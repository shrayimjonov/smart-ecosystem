package uz.uzkassa.cts.handlers.export.csv;

import com.opencsv.CSVWriter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.repository.CustomerBranchDeviceRepository;
import uz.uzkassa.cts.utils.DateUtil;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CustomerBranchDeviceExportCsvHandler extends AbstractExportCsvHandler<BaseFilter> {

    CustomerBranchDeviceRepository customerBranchDeviceRepository;

    @Override
    public String getFileName(BaseFilter filter) {
        return "Customer_Branch_Devices.csv";
    }

    @Override
    protected List<String> getHeaders() {
        return List.of("Серийный номер", "Дата регистрации", "Статус", "Создатель", "Компания", "Филиал", "Клиент", "Торговая точка");
    }

    @Override
    protected void drawItemList(BaseFilter filter, CSVWriter csvWriter) {
        customerBranchDeviceRepository
                .getResultList(filter)
                .getList()
                .forEach(customerBranchDevice ->
                        csvWriter.writeNext(
                                new String[]{
                                        customerBranchDevice.getDevice().getSerialNumber(),
                                        DateUtil.getLocalDateTimeAsStringWithoutSecond(customerBranchDevice.getCreatedDate()),
                                        customerBranchDevice.getStatus().getName(),
                                        customerBranchDevice.getCreator().getName(),
                                        customerBranchDevice.getCustomerBranch().getCustomer().getCompany().getName(),
                                        customerBranchDevice.getCustomerBranch().getCustomer().getBranch().getName(),
                                        customerBranchDevice.getCustomerBranch().getCustomer().getName(),
                                        customerBranchDevice.getCustomerBranch().getName()
                                },
                                false
                        )
                );
    }
}
