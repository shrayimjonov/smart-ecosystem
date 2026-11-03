package uz.uzkassa.cts.handlers.export.csv;

import com.opencsv.CSVWriter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.filter.EmployeeFilter;
import uz.uzkassa.cts.repository.EmployeeRepository;
import uz.uzkassa.cts.utils.DateUtil;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class EmployeeExportCsvHandler extends AbstractExportCsvHandler<EmployeeFilter> {

    EmployeeRepository employeeRepository;

    @Override
    public String getFileName(EmployeeFilter filter) {
        return "Employees.csv";
    }

    @Override
    protected List<String> getHeaders() {
        return List.of("ФИО", "Компания", "Филиал", "Отдел", "Должность", "Логин", "Номер телефона", "Почта", "Статус", "Дата добавления");
    }

    @Override
    protected void drawItemList(EmployeeFilter filter, CSVWriter csvWriter) {
        employeeRepository
                .getResultList(filter)
                .getList()
                .forEach(employee ->
                        csvWriter.writeNext(
                                new String[]{
                                        employee.getFullName(),
                                        employee.getCompany().getName(),
                                        employee.getBranch().getName(),
                                        employee.getDepartment().getName(),
                                        employee.getPosition().getName(),
                                        employee.getLogin(),
                                        employee.getPhone(),
                                        employee.getEmail(),
                                        employee.getStatus().getName(),
                                        DateUtil.getLocalDateTimeAsStringWithoutSecond(employee.getCreatedDate())
                                },
                                false
                        )
                );
    }
}
