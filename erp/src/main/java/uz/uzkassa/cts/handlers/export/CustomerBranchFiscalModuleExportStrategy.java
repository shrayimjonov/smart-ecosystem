package uz.uzkassa.cts.handlers.export;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.dto.cases.caseproperty.CustomMultipartFile;
import uz.uzkassa.cts.enums.ContentType;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.handlers.export.csv.CustomerBranchFiscalModuleExportCsvHandler;
import uz.uzkassa.cts.handlers.export.excel.CustomerBranchFiscalModuleExportExcelHandler;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class CustomerBranchFiscalModuleExportStrategy extends AbstractExportStrategy {

    CustomerBranchFiscalModuleExportExcelHandler customerBranchFiscalModuleExportExcelHandler;
    CustomerBranchFiscalModuleExportCsvHandler customerBranchFiscalModuleExportCsvHandler;

    public CustomerBranchFiscalModuleExportStrategy(ObjectMapper objectMapper, CustomerBranchFiscalModuleExportExcelHandler customerBranchFiscalModuleExportExcelHandler, CustomerBranchFiscalModuleExportCsvHandler customerBranchFiscalModuleExportCsvHandler) {
        super(objectMapper);
        this.customerBranchFiscalModuleExportExcelHandler = customerBranchFiscalModuleExportExcelHandler;
        this.customerBranchFiscalModuleExportCsvHandler = customerBranchFiscalModuleExportCsvHandler;
    }

    @Override
    public EntityTypeEnum getEntityType() {
        return EntityTypeEnum.CUSTOMER_BRANCH_FISCAL_MODULE;
    }

    @Override
    public CustomMultipartFile generateReport(Object filterObject, ContentType contentType) {
        BaseFilter filter = objectMapper.convertValue(filterObject, BaseFilter.class);
        return switch (contentType) {
            case EXCEL -> new CustomMultipartFile(
                    customerBranchFiscalModuleExportExcelHandler.generate(filter),
                    customerBranchFiscalModuleExportExcelHandler.getFileName(filter),
                    CONTENT_TYPE_FOR_EXCEL
            );
            case CSV -> new CustomMultipartFile(
                    customerBranchFiscalModuleExportCsvHandler.generate(filter),
                    customerBranchFiscalModuleExportCsvHandler.getFileName(filter),
                    CONTENT_TYPE_FOR_CSV
            );
        };
    }
}
