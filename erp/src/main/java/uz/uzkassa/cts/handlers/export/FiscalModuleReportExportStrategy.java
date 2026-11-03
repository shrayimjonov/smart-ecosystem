package uz.uzkassa.cts.handlers.export;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.dto.cases.caseproperty.CustomMultipartFile;
import uz.uzkassa.cts.enums.ContentType;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.filter.FiscalModuleReportFilter;
import uz.uzkassa.cts.handlers.export.csv.FiscalModuleReportExportCsvHandler;
import uz.uzkassa.cts.handlers.export.excel.FiscalModuleReportExportExcelHandler;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class FiscalModuleReportExportStrategy extends AbstractExportStrategy {

    FiscalModuleReportExportExcelHandler fiscalModuleReportExportExcelHandler;
    FiscalModuleReportExportCsvHandler fiscalModuleReportExportCsvHandler;

    public FiscalModuleReportExportStrategy(ObjectMapper objectMapper, FiscalModuleReportExportExcelHandler fiscalModuleReportExportExcelHandler, FiscalModuleReportExportCsvHandler fiscalModuleReportExportCsvHandler) {
        super(objectMapper);
        this.fiscalModuleReportExportExcelHandler = fiscalModuleReportExportExcelHandler;
        this.fiscalModuleReportExportCsvHandler = fiscalModuleReportExportCsvHandler;
    }

    @Override
    public EntityTypeEnum getEntityType() {
        return EntityTypeEnum.FM_REPORT;
    }

    @Override
    public CustomMultipartFile generateReport(Object filterObject, ContentType contentType) {
        FiscalModuleReportFilter filter = objectMapper.convertValue(filterObject, FiscalModuleReportFilter.class);
        return switch (contentType) {
            case EXCEL -> new CustomMultipartFile(
                    fiscalModuleReportExportExcelHandler.generate(filter),
                    fiscalModuleReportExportExcelHandler.getFileName(filter),
                    CONTENT_TYPE_FOR_EXCEL
            );
            case CSV -> new CustomMultipartFile(
                    fiscalModuleReportExportCsvHandler.generate(filter),
                    fiscalModuleReportExportCsvHandler.getFileName(filter),
                    CONTENT_TYPE_FOR_CSV
            );
        };
    }
}
