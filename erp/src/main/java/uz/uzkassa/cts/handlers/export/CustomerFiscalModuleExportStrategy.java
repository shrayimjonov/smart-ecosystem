package uz.uzkassa.cts.handlers.export;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.dto.cases.caseproperty.CustomMultipartFile;
import uz.uzkassa.cts.enums.ContentType;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.handlers.export.csv.CustomerFiscalModuleExportCsvHandler;
import uz.uzkassa.cts.handlers.export.excel.CustomerFiscalModuleExportExcelHandler;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class CustomerFiscalModuleExportStrategy extends AbstractExportStrategy {

    CustomerFiscalModuleExportExcelHandler customerFiscalModuleExportExcelHandler;
    CustomerFiscalModuleExportCsvHandler customerFiscalModuleExportCsvHandler;

    public CustomerFiscalModuleExportStrategy(ObjectMapper objectMapper, CustomerFiscalModuleExportExcelHandler customerFiscalModuleExportExcelHandler, CustomerFiscalModuleExportCsvHandler customerFiscalModuleExportCsvHandler) {
        super(objectMapper);
        this.customerFiscalModuleExportExcelHandler = customerFiscalModuleExportExcelHandler;
        this.customerFiscalModuleExportCsvHandler = customerFiscalModuleExportCsvHandler;
    }

    @Override
    public EntityTypeEnum getEntityType() {
        return EntityTypeEnum.CUSTOMER_FISCAL_MODULE;
    }

    @Override
    public CustomMultipartFile generateReport(Object filterObject, ContentType contentType) {
        BaseFilter filter = objectMapper.convertValue(filterObject, BaseFilter.class);
        return switch (contentType) {
            case EXCEL -> new CustomMultipartFile(
                    customerFiscalModuleExportExcelHandler.generate(filter),
                    customerFiscalModuleExportExcelHandler.getFileName(filter),
                    CONTENT_TYPE_FOR_EXCEL
            );
            case CSV -> new CustomMultipartFile(
                    customerFiscalModuleExportCsvHandler.generate(filter),
                    customerFiscalModuleExportCsvHandler.getFileName(filter),
                    CONTENT_TYPE_FOR_CSV
            );
        };
    }
}
