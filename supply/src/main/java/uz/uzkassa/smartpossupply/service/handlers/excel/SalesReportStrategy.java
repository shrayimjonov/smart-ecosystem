package uz.uzkassa.smartpossupply.service.handlers.excel;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.smartpossupply.enums.EntityTypeEnum;
import uz.uzkassa.smartpossupply.enums.FileType;
import uz.uzkassa.smartpossupply.filters.PlanRouteFilter;
import uz.uzkassa.smartpossupply.service.CustomMultipartFile;
import uz.uzkassa.smartpossupply.service.handlers.csv.SalesReportCsvHandler;
import uz.uzkassa.smartpossupply.service.handlers.excel.export.SalesReportExcelHandler;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SalesReportStrategy extends AbstractExportStrategy {

    SalesReportExcelHandler excelHandler;
    SalesReportCsvHandler csvHandler;

    public SalesReportStrategy(SalesReportExcelHandler excelHandler, SalesReportCsvHandler csvHandler) {
        this.excelHandler = excelHandler;
        this.csvHandler = csvHandler;
    }

    @Override
    public EntityTypeEnum getEntityType() {
        return EntityTypeEnum.SALES_REPORT;
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
