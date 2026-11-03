package uz.uzkassa.cts.handlers.export;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.dto.cases.caseproperty.CustomMultipartFile;
import uz.uzkassa.cts.enums.ContentType;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.handlers.export.csv.CompanyDeviceExportCsvHandler;
import uz.uzkassa.cts.handlers.export.excel.CompanyDeviceExportExcelHandler;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class CompanyDeviceExportStrategy extends AbstractExportStrategy {

    CompanyDeviceExportExcelHandler companyDeviceExportExcelHandler;
    CompanyDeviceExportCsvHandler companyDeviceExportCsvHandler;

    public CompanyDeviceExportStrategy(ObjectMapper objectMapper, CompanyDeviceExportExcelHandler companyDeviceExportExcelHandler, CompanyDeviceExportCsvHandler companyDeviceExportCsvHandler) {
        super(objectMapper);
        this.companyDeviceExportExcelHandler = companyDeviceExportExcelHandler;
        this.companyDeviceExportCsvHandler = companyDeviceExportCsvHandler;
    }

    @Override
    public EntityTypeEnum getEntityType() {
        return EntityTypeEnum.COMPANY_DEVICE;
    }

    @Override
    public CustomMultipartFile generateReport(Object filterObject, ContentType contentType) {
        BaseFilter filter = objectMapper.convertValue(filterObject, BaseFilter.class);
        return switch (contentType) {
            case EXCEL -> new CustomMultipartFile(
                    companyDeviceExportExcelHandler.generate(filter),
                    companyDeviceExportExcelHandler.getFileName(filter),
                    CONTENT_TYPE_FOR_EXCEL
            );
            case CSV -> new CustomMultipartFile(
                    companyDeviceExportCsvHandler.generate(filter),
                    companyDeviceExportCsvHandler.getFileName(filter),
                    CONTENT_TYPE_FOR_CSV
            );
        };
    }
}
