package uz.uzkassa.cts.handlers.export.excel;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.domain.User;
import uz.uzkassa.cts.filter.EmployeeFilter;
import uz.uzkassa.cts.repository.EmployeeRepository;
import uz.uzkassa.cts.utils.DateUtil;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class EmployeeExportExcelHandler extends AbstractExportExcelHandler<EmployeeFilter> {

    EmployeeRepository employeeRepository;

    @Override
    public String getFileName(EmployeeFilter filter) {
        return "Employees.xlsx";
    }

    @Override
    protected List<String> getHeaders() {
        return List.of("ФИО", "Компания", "Филиал", "Отдел", "Должность", "Логин", "Номер телефона", "Почта", "Статус", "Дата добавления");
    }

    @Override
    protected XSSFWorkbook getXSSFWorkbook(EmployeeFilter filter) {
        XSSFWorkbook workbook = createWorkbook();
        XSSFSheet sheet = createSheet(workbook, getFileName(filter));
        createHeader(getHeaders(), sheet);
        filter.setPage(0);
        filter.setSize(EXCEL_EXPORT_MAX_LIMIT);
        List<User> result = employeeRepository.getResultList(filter).getList();
        int rowIndex = 0;
        for (User employee : result) {
            Row row = sheet.createRow(++rowIndex);
            int cellIndex = 0;
            row.createCell(cellIndex++).setCellValue(employee.getFullName());
            row.createCell(cellIndex++).setCellValue(employee.getCompany().getName());
            row.createCell(cellIndex++).setCellValue(employee.getBranch().getName());
            row.createCell(cellIndex++).setCellValue(employee.getDepartment().getName());
            row.createCell(cellIndex++).setCellValue(employee.getPosition().getName());
            row.createCell(cellIndex++).setCellValue(employee.getLogin());
            row.createCell(cellIndex++).setCellValue(employee.getPhone());
            row.createCell(cellIndex++).setCellValue(employee.getEmail());
            row.createCell(cellIndex++).setCellValue(employee.getStatus().getName());
            row.createCell(cellIndex).setCellValue(DateUtil.getLocalDateTimeAsStringWithoutSecond(employee.getCreatedDate()));
        }
        return workbook;
    }
}
