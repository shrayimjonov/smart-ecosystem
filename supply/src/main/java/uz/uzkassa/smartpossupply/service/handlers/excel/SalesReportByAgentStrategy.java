package uz.uzkassa.smartpossupply.service.handlers.excel;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.smartpossupply.enums.EntityTypeEnum;
import uz.uzkassa.smartpossupply.enums.FileType;
import uz.uzkassa.smartpossupply.filters.PlanRouteFilter;
import uz.uzkassa.smartpossupply.service.CustomMultipartFile;
import uz.uzkassa.smartpossupply.service.handlers.csv.SalesReportByAgentCsvHandler;
import uz.uzkassa.smartpossupply.service.handlers.excel.export.SalesReportByAgentExcelHandler;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SalesReportByAgentStrategy extends AbstractExportStrategy {

    SalesReportByAgentExcelHandler excelHandler;
    SalesReportByAgentCsvHandler csvHandler;

    public SalesReportByAgentStrategy(SalesReportByAgentExcelHandler excelHandler, SalesReportByAgentCsvHandler csvHandler) {
        this.excelHandler = excelHandler;
        this.csvHandler = csvHandler;
    }

    @Override
    public EntityTypeEnum getEntityType() {
        return EntityTypeEnum.SALES_REPORT_BY_AGENT;
    }

    @Override
    public CustomMultipartFile generateReport(Object filter, FileType fileType) {
        PlanRouteFilter salesReportFilter = objectMapper.convertValue(filter, PlanRouteFilter.class);
        return switch (fileType) {
            case CSV -> new CustomMultipartFile(
                    csvHandler.generate(salesReportFilter),
                    csvHandler.getFileName(salesReportFilter),
                    CONTENT_TYPE_FOR_CSV
            );
            case EXCEL -> new CustomMultipartFile(
                    excelHandler.generate(salesReportFilter),
                    excelHandler.getFileName(salesReportFilter),
                    CONTENT_TYPE_FOR_EXCEL
            );
        };
    }
}
