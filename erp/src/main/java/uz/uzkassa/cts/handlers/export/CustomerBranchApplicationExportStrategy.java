package uz.uzkassa.cts.handlers.export;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.dto.cases.caseproperty.CustomMultipartFile;
import uz.uzkassa.cts.enums.ContentType;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.handlers.export.csv.CustomerBranchApplicationExportCsvHandler;
import uz.uzkassa.cts.handlers.export.excel.CustomerBranchApplicationExportExcelHandler;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class CustomerBranchApplicationExportStrategy extends AbstractExportStrategy {

    CustomerBranchApplicationExportExcelHandler customerBranchApplicationExportExcelHandler;
    CustomerBranchApplicationExportCsvHandler customerBranchApplicationExportCsvHandler;

    public CustomerBranchApplicationExportStrategy(ObjectMapper objectMapper, CustomerBranchApplicationExportExcelHandler customerBranchApplicationExportExcelHandler, CustomerBranchApplicationExportCsvHandler customerBranchApplicationExportCsvHandler) {
        super(objectMapper);
        this.customerBranchApplicationExportExcelHandler = customerBranchApplicationExportExcelHandler;
        this.customerBranchApplicationExportCsvHandler = customerBranchApplicationExportCsvHandler;
    }

    @Override
    public EntityTypeEnum getEntityType() {
        return EntityTypeEnum.CUSTOMER_BRANCH_APPLICATION;
    }

    @Override
    public CustomMultipartFile generateReport(Object filterObject, ContentType contentType) {
        BaseFilter filter = objectMapper.convertValue(filterObject, BaseFilter.class);
        return switch (contentType) {
            case EXCEL -> new CustomMultipartFile(
                    customerBranchApplicationExportExcelHandler.generate(filter),
                    customerBranchApplicationExportExcelHandler.getFileName(filter),
                    CONTENT_TYPE_FOR_EXCEL
            );
            case CSV -> new CustomMultipartFile(
                    customerBranchApplicationExportCsvHandler.generate(filter),
                    customerBranchApplicationExportCsvHandler.getFileName(filter),
                    CONTENT_TYPE_FOR_CSV
            );
        };
    }
}
