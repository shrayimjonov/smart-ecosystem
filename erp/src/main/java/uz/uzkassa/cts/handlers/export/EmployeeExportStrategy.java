package uz.uzkassa.cts.handlers.export;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.dto.cases.caseproperty.CustomMultipartFile;
import uz.uzkassa.cts.enums.ContentType;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.filter.EmployeeFilter;
import uz.uzkassa.cts.handlers.export.csv.EmployeeExportCsvHandler;
import uz.uzkassa.cts.handlers.export.excel.EmployeeExportExcelHandler;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class EmployeeExportStrategy extends AbstractExportStrategy {

    EmployeeExportExcelHandler employeeExcelExportHandler;
    EmployeeExportCsvHandler employeeExportCsvHandler;

    public EmployeeExportStrategy(ObjectMapper objectMapper, EmployeeExportExcelHandler employeeExcelExportHandler, EmployeeExportCsvHandler employeeExportCsvHandler) {
        super(objectMapper);
        this.employeeExcelExportHandler = employeeExcelExportHandler;
        this.employeeExportCsvHandler = employeeExportCsvHandler;
    }

    @Override
    public EntityTypeEnum getEntityType() {
        return EntityTypeEnum.EMPLOYEE;
    }

    @Override
    public CustomMultipartFile generateReport(Object filterObject, ContentType contentType) {
        EmployeeFilter filter = objectMapper.convertValue(filterObject, EmployeeFilter.class);
        return switch (contentType) {
            case EXCEL -> new CustomMultipartFile(
                    employeeExcelExportHandler.generate(filter),
                    employeeExcelExportHandler.getFileName(filter),
                    CONTENT_TYPE_FOR_EXCEL
            );
            case CSV -> new CustomMultipartFile(
                    employeeExportCsvHandler.generate(filter),
                    employeeExportCsvHandler.getFileName(filter),
                    CONTENT_TYPE_FOR_CSV
            );
        };
    }
}
