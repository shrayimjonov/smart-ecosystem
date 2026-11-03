package uz.uzkassa.cts.handlers.export.csv;

import com.opencsv.CSVWriter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.domain.ActivityType;
import uz.uzkassa.cts.domain.Branch;
import uz.uzkassa.cts.domain.User;
import uz.uzkassa.cts.enums.BusinessType;
import uz.uzkassa.cts.filter.CustomerFilter;
import uz.uzkassa.cts.repository.CustomerRepository;
import uz.uzkassa.cts.utils.DateUtil;

import java.util.List;
import java.util.Optional;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CustomerExportCsvHandler extends AbstractExportCsvHandler<CustomerFilter> {

    CustomerRepository customerRepository;

    @Override
    public String getFileName(CustomerFilter filter) {
        return "Customers.csv";
    }

    @Override
    protected List<String> getHeaders() {
        return List.of("ИНН", "Вид компании", "Клиент", "Филиал", "Телефон", "Сфера деятельности", "Оператор", "Менеджер", "Дата регистрации");
    }

    @Override
    protected void drawItemList(CustomerFilter filter, CSVWriter csvWriter) {
        customerRepository
                .getResultList(filter)
                .getList()
                .forEach(customer ->
                        csvWriter.writeNext(
                                new String[]{
                                        customer.getTin(),
                                        Optional.ofNullable(customer.getBusinessType()).map(BusinessType::getName).orElse(""),
                                        customer.getName(),
                                        Optional.ofNullable(customer.getBranch()).map(Branch::getName).orElse(""),
                                        customer.getPhone(),
                                        customer.getActivityType() != null ? customer.getActivityType().getName() : "",
                                        Optional.ofNullable(customer.getActivityType()).map(ActivityType::getName).orElse(""),
                                        Optional.ofNullable(customer.getCreator()).map(User::getName).orElse(""),
                                        DateUtil.getLocalDateTimeAsStringWithoutSecond(customer.getCreatedDate())
                                },
                                false
                        )
                );
    }
}
