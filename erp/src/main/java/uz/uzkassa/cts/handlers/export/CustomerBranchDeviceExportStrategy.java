package uz.uzkassa.cts.handlers.export;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.dto.cases.caseproperty.CustomMultipartFile;
import uz.uzkassa.cts.enums.ContentType;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.handlers.export.csv.CustomerBranchDeviceExportCsvHandler;
import uz.uzkassa.cts.handlers.export.excel.CustomerBranchDeviceExportExcelHandler;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class CustomerBranchDeviceExportStrategy extends AbstractExportStrategy {

    CustomerBranchDeviceExportExcelHandler customerBranchDeviceExportExcelHandler;
    CustomerBranchDeviceExportCsvHandler customerBranchDeviceExportCsvHandler;

    public CustomerBranchDeviceExportStrategy(ObjectMapper objectMapper, CustomerBranchDeviceExportExcelHandler customerBranchDeviceExportExcelHandler, CustomerBranchDeviceExportCsvHandler customerBranchDeviceExportCsvHandler) {
        super(objectMapper);
        this.customerBranchDeviceExportExcelHandler = customerBranchDeviceExportExcelHandler;
        this.customerBranchDeviceExportCsvHandler = customerBranchDeviceExportCsvHandler;
    }

    @Override
    public EntityTypeEnum getEntityType() {
        return EntityTypeEnum.CUSTOMER_BRANCH_DEVICE;
    }

    @Override
    public CustomMultipartFile generateReport(Object filterObject, ContentType contentType) {
        BaseFilter filter = objectMapper.convertValue(filterObject, BaseFilter.class);
        return switch (contentType) {
            case EXCEL -> new CustomMultipartFile(
                    customerBranchDeviceExportExcelHandler.generate(filter),
                    customerBranchDeviceExportExcelHandler.getFileName(filter),
                    CONTENT_TYPE_FOR_EXCEL
            );
            case CSV -> new CustomMultipartFile(
                    customerBranchDeviceExportCsvHandler.generate(filter),
                    customerBranchDeviceExportCsvHandler.getFileName(filter),
                    CONTENT_TYPE_FOR_CSV
            );
        };
    }
}
