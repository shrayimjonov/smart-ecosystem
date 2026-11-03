package uz.uzkassa.cts.handlers.export;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.dto.cases.caseproperty.CustomMultipartFile;
import uz.uzkassa.cts.enums.ContentType;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.filter.CustomerFilter;
import uz.uzkassa.cts.handlers.export.csv.CustomerExportCsvHandler;
import uz.uzkassa.cts.handlers.export.excel.CustomerExportExcelHandler;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class CustomerExportStrategy extends AbstractExportStrategy {

    CustomerExportExcelHandler customerExportExcelHandler;
    CustomerExportCsvHandler customerExportCsvHandler;

    public CustomerExportStrategy(ObjectMapper objectMapper, CustomerExportExcelHandler customerExportExcelHandler, CustomerExportCsvHandler customerExportCsvHandler) {
        super(objectMapper);
        this.customerExportExcelHandler = customerExportExcelHandler;
        this.customerExportCsvHandler = customerExportCsvHandler;
    }

    @Override
    public EntityTypeEnum getEntityType() {
        return EntityTypeEnum.CUSTOMER;
    }

    @Override
    public CustomMultipartFile generateReport(Object filterObject, ContentType contentType) {
        CustomerFilter filter = objectMapper.convertValue(filterObject, CustomerFilter.class);
        return switch (contentType) {
            case EXCEL -> new CustomMultipartFile(
                    customerExportExcelHandler.generate(filter),
                    customerExportExcelHandler.getFileName(filter),
                    CONTENT_TYPE_FOR_EXCEL
            );
            case CSV -> new CustomMultipartFile(
                    customerExportCsvHandler.generate(filter),
                    customerExportCsvHandler.getFileName(filter),
                    CONTENT_TYPE_FOR_CSV
            );
        };
    }
}
