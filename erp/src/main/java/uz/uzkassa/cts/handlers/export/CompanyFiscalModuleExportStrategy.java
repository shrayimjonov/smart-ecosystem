package uz.uzkassa.cts.handlers.export;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.dto.cases.caseproperty.CustomMultipartFile;
import uz.uzkassa.cts.enums.ContentType;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.handlers.export.csv.CompanyFiscalModuleExportCsvHandler;
import uz.uzkassa.cts.handlers.export.excel.CompanyFiscalModuleExportExcelHandler;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class CompanyFiscalModuleExportStrategy extends AbstractExportStrategy {

    CompanyFiscalModuleExportCsvHandler companyFiscalModuleExportCsvHandler;
    CompanyFiscalModuleExportExcelHandler companyFiscalModuleExportExcelHandler;

    public CompanyFiscalModuleExportStrategy(ObjectMapper objectMapper, CompanyFiscalModuleExportCsvHandler companyFiscalModuleExportCsvHandler, CompanyFiscalModuleExportExcelHandler companyFiscalModuleExportExcelHandler) {
        super(objectMapper);
        this.companyFiscalModuleExportCsvHandler = companyFiscalModuleExportCsvHandler;
        this.companyFiscalModuleExportExcelHandler = companyFiscalModuleExportExcelHandler;
    }

    @Override
    public EntityTypeEnum getEntityType() {
        return EntityTypeEnum.COMPANY_FISCAL_MODULE;
    }

    @Override
    public CustomMultipartFile generateReport(Object filterObject, ContentType contentType) {
        BaseFilter filter = objectMapper.convertValue(filterObject, BaseFilter.class);
        return switch (contentType) {
            case EXCEL -> new CustomMultipartFile(
                    companyFiscalModuleExportCsvHandler.generate(filter),
                    companyFiscalModuleExportCsvHandler.getFileName(filter),
                    CONTENT_TYPE_FOR_EXCEL
            );
            case CSV -> new CustomMultipartFile(
                    companyFiscalModuleExportExcelHandler.generate(filter),
                    companyFiscalModuleExportExcelHandler.getFileName(filter),
                    CONTENT_TYPE_FOR_CSV
            );
        };
    }
}
