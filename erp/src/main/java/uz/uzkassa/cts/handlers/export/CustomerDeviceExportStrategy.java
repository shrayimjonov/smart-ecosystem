package uz.uzkassa.cts.handlers.export;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.dto.cases.caseproperty.CustomMultipartFile;
import uz.uzkassa.cts.enums.ContentType;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.handlers.export.csv.CustomerDeviceExportCsvHandler;
import uz.uzkassa.cts.handlers.export.excel.CustomerDeviceExportExcelHandler;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class CustomerDeviceExportStrategy extends AbstractExportStrategy {

    CustomerDeviceExportExcelHandler customerDeviceExportExcelHandler;
    CustomerDeviceExportCsvHandler customerDeviceExportCsvHandler;

    public CustomerDeviceExportStrategy(ObjectMapper objectMapper, CustomerDeviceExportExcelHandler customerDeviceExportExcelHandler, CustomerDeviceExportCsvHandler customerDeviceExportCsvHandler) {
        super(objectMapper);
        this.customerDeviceExportExcelHandler = customerDeviceExportExcelHandler;
        this.customerDeviceExportCsvHandler = customerDeviceExportCsvHandler;
    }

    @Override
    public EntityTypeEnum getEntityType() {
        return EntityTypeEnum.CUSTOMER_DEVICE;
    }

    @Override
    public CustomMultipartFile generateReport(Object filterObject, ContentType contentType) {
        BaseFilter filter = objectMapper.convertValue(filterObject, BaseFilter.class);
        return switch (contentType) {
            case EXCEL -> new CustomMultipartFile(
                    customerDeviceExportExcelHandler.generate(filter),
                    customerDeviceExportExcelHandler.getFileName(filter),
                    CONTENT_TYPE_FOR_EXCEL
            );
            case CSV -> new CustomMultipartFile(
                    customerDeviceExportCsvHandler.generate(filter),
                    customerDeviceExportCsvHandler.getFileName(filter),
                    CONTENT_TYPE_FOR_CSV
            );
        };
    }
}
