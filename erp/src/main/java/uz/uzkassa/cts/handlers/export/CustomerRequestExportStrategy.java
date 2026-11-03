package uz.uzkassa.cts.handlers.export;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.dto.cases.caseproperty.CustomMultipartFile;
import uz.uzkassa.cts.enums.ContentType;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.filter.CustomerRequestFilter;
import uz.uzkassa.cts.handlers.export.csv.CustomerRequestExportCsvHandler;
import uz.uzkassa.cts.handlers.export.excel.CustomerRequestExportExcelHandler;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class CustomerRequestExportStrategy extends AbstractExportStrategy {

    CustomerRequestExportExcelHandler customerRequestExportExcelHandler;
    CustomerRequestExportCsvHandler customerRequestExportCsvHandler;

    public CustomerRequestExportStrategy(ObjectMapper objectMapper, CustomerRequestExportExcelHandler customerRequestExportExcelHandler, CustomerRequestExportCsvHandler customerRequestExportCsvHandler) {
        super(objectMapper);
        this.customerRequestExportExcelHandler = customerRequestExportExcelHandler;
        this.customerRequestExportCsvHandler = customerRequestExportCsvHandler;
    }

    @Override
    public EntityTypeEnum getEntityType() {
        return EntityTypeEnum.CUSTOMER_REQUEST;
    }

    @Override
    public CustomMultipartFile generateReport(Object filterObject, ContentType contentType) {
        CustomerRequestFilter filter = objectMapper.convertValue(filterObject, CustomerRequestFilter.class);
        return switch (contentType) {
            case EXCEL -> new CustomMultipartFile(
                    customerRequestExportExcelHandler.generate(filter),
                    customerRequestExportExcelHandler.getFileName(filter),
                    CONTENT_TYPE_FOR_EXCEL
            );
            case CSV -> new CustomMultipartFile(
                    customerRequestExportCsvHandler.generate(filter),
                    customerRequestExportCsvHandler.getFileName(filter),
                    CONTENT_TYPE_FOR_CSV
            );
        };
    }
}
