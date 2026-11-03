package uz.uzkassa.cts.handlers.export;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.dto.cases.caseproperty.CustomMultipartFile;
import uz.uzkassa.cts.enums.ContentType;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.handlers.export.csv.FiscalModuleExportCsvHandler;
import uz.uzkassa.cts.handlers.export.excel.FiscalModuleExportExcelHandler;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class FiscalModuleExportStrategy extends AbstractExportStrategy {

    FiscalModuleExportExcelHandler fiscalModuleExcelExportHandler;
    FiscalModuleExportCsvHandler fiscalModuleExportCsvHandler;

    public FiscalModuleExportStrategy(ObjectMapper objectMapper, FiscalModuleExportExcelHandler fiscalModuleExcelExportHandler, FiscalModuleExportCsvHandler fiscalModuleExportCsvHandler) {
        super(objectMapper);
        this.fiscalModuleExcelExportHandler = fiscalModuleExcelExportHandler;
        this.fiscalModuleExportCsvHandler = fiscalModuleExportCsvHandler;
    }

    @Override
    public EntityTypeEnum getEntityType() {
        return EntityTypeEnum.FISCAL_MODULE;
    }

    @Override
    public CustomMultipartFile generateReport(Object filterObject, ContentType contentType) {
        BaseFilter filter = objectMapper.convertValue(filterObject, BaseFilter.class);
        return switch (contentType) {
            case EXCEL -> new CustomMultipartFile(
                    fiscalModuleExcelExportHandler.generate(filter),
                    fiscalModuleExcelExportHandler.getFileName(filter),
                    CONTENT_TYPE_FOR_EXCEL
            );
            case CSV -> new CustomMultipartFile(
                    fiscalModuleExportCsvHandler.generate(filter),
                    fiscalModuleExportCsvHandler.getFileName(filter),
                    CONTENT_TYPE_FOR_CSV
            );
        };
    }
}
