package uz.uzkassa.cts.handlers.export;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.dto.cases.caseproperty.CustomMultipartFile;
import uz.uzkassa.cts.enums.ContentType;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.filter.EmployeeReportFilter;
import uz.uzkassa.cts.handlers.export.csv.EmployeeReportExportCsvHandler;
import uz.uzkassa.cts.handlers.export.excel.EmployeeReportExportExcelHandler;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class EmployeeReportExportStrategy extends AbstractExportStrategy {

    EmployeeReportExportExcelHandler employeeReportExportExcelHandler;
    EmployeeReportExportCsvHandler employeeReportExportCsvHandler;

    public EmployeeReportExportStrategy(ObjectMapper objectMapper, EmployeeReportExportExcelHandler employeeReportExportExcelHandler, EmployeeReportExportCsvHandler employeeReportExportCsvHandler) {
        super(objectMapper);
        this.employeeReportExportExcelHandler = employeeReportExportExcelHandler;
        this.employeeReportExportCsvHandler = employeeReportExportCsvHandler;
    }

    @Override
    public EntityTypeEnum getEntityType() {
        return EntityTypeEnum.EMPLOYEE_REPORT;
    }

    @Override
    public CustomMultipartFile generateReport(Object filterObject, ContentType contentType) {
        EmployeeReportFilter filter = objectMapper.convertValue(filterObject, EmployeeReportFilter.class);
        return switch (contentType) {
            case EXCEL -> new CustomMultipartFile(
                    employeeReportExportExcelHandler.generate(filter),
                    employeeReportExportExcelHandler.getFileName(filter),
                    CONTENT_TYPE_FOR_EXCEL
            );
            case CSV -> new CustomMultipartFile(
                    employeeReportExportCsvHandler.generate(filter),
                    employeeReportExportCsvHandler.getFileName(filter),
                    CONTENT_TYPE_FOR_CSV
            );
        };
    }
}
