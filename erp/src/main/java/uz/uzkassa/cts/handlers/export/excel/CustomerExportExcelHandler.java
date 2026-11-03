package uz.uzkassa.cts.handlers.export.excel;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.domain.ActivityType;
import uz.uzkassa.cts.domain.Branch;
import uz.uzkassa.cts.domain.Customer;
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
public class CustomerExportExcelHandler extends AbstractExportExcelHandler<CustomerFilter> {

    CustomerRepository customerRepository;

    @Override
    public String getFileName(CustomerFilter filter) {
        return "Customers.xlsx";
    }

    @Override
    protected List<String> getHeaders() {
        return List.of("ИНН", "Вид компании", "Клиент", "Филиал", "Телефон", "Сфера деятельности", "Оператор", "Менеджер", "Дата регистрации");
    }

    @Override
    protected XSSFWorkbook getXSSFWorkbook(CustomerFilter filter) {
        XSSFWorkbook workbook = createWorkbook();
        XSSFSheet sheet = createSheet(workbook, getFileName(filter));
        createHeader(getHeaders(), sheet);
        filter.setPage(0);
        filter.setSize(EXCEL_EXPORT_MAX_LIMIT);
        List<Customer> result = customerRepository.getResultList(filter).getList();
        int rowIndex = 0;
        for (Customer customer : result) {
            Row row = sheet.createRow(++rowIndex);
            int cellIndex = 0;
            row.createCell(cellIndex++).setCellValue(customer.getTin());
            row.createCell(cellIndex++).setCellValue(Optional.ofNullable(customer.getBusinessType()).map(BusinessType::getName).orElse(""));
            row.createCell(cellIndex++).setCellValue(customer.getName());
            row.createCell(cellIndex++).setCellValue(Optional.ofNullable(customer.getBranch()).map(Branch::getName).orElse(""));
            row.createCell(cellIndex++).setCellValue(customer.getPhone());
            row.createCell(cellIndex++).setCellValue(Optional.ofNullable(customer.getActivityType()).map(ActivityType::getName).orElse(""));
            row.createCell(cellIndex++).setCellValue(Optional.ofNullable(customer.getCreator()).map(User::getName).orElse(""));
            row.createCell(cellIndex++).setCellValue(Optional.ofNullable(customer.getManager()).map(User::getName).orElse(""));
            row.createCell(cellIndex).setCellValue(DateUtil.getLocalDateTimeAsStringWithoutSecond(customer.getCreatedDate()));
        }
        return workbook;
    }
}
