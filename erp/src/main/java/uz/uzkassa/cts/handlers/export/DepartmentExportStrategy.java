package uz.uzkassa.cts.handlers.export;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.dto.cases.caseproperty.CustomMultipartFile;
import uz.uzkassa.cts.enums.ContentType;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.handlers.export.csv.DepartmentExportCsvHandler;
import uz.uzkassa.cts.handlers.export.excel.DepartmentExportExcelHandler;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class DepartmentExportStrategy extends AbstractExportStrategy {

    DepartmentExportExcelHandler departmentExcelExportHandler;
    DepartmentExportCsvHandler departmentExportCsvHandler;

    public DepartmentExportStrategy(ObjectMapper objectMapper, DepartmentExportExcelHandler departmentExcelExportHandler, DepartmentExportCsvHandler departmentExportCsvHandler) {
        super(objectMapper);
        this.departmentExcelExportHandler = departmentExcelExportHandler;
        this.departmentExportCsvHandler = departmentExportCsvHandler;
    }

    @Override
    public EntityTypeEnum getEntityType() {
        return EntityTypeEnum.DEPARTMENT;
    }

    @Override
    public CustomMultipartFile generateReport(Object filterObject, ContentType contentType) {
        BaseFilter filter = objectMapper.convertValue(filterObject, BaseFilter.class);
        return switch (contentType) {
            case EXCEL -> new CustomMultipartFile(
                    departmentExcelExportHandler.generate(filter),
                    departmentExcelExportHandler.getFileName(filter),
                    CONTENT_TYPE_FOR_EXCEL
            );
            case CSV -> new CustomMultipartFile(
                    departmentExportCsvHandler.generate(filter),
                    departmentExportCsvHandler.getFileName(filter),
                    CONTENT_TYPE_FOR_CSV
            );
        };
    }
}
