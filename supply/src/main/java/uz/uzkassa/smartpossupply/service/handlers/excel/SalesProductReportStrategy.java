package uz.uzkassa.smartpossupply.service.handlers.excel;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.smartpossupply.enums.EntityTypeEnum;
import uz.uzkassa.smartpossupply.enums.FileType;
import uz.uzkassa.smartpossupply.filters.StockReportByProductFilter;
import uz.uzkassa.smartpossupply.service.CustomMultipartFile;
import uz.uzkassa.smartpossupply.service.handlers.excel.export.SalesProductReportExcelHandler;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SalesProductReportStrategy extends AbstractExportStrategy {

    SalesProductReportExcelHandler excelHandler;

    public SalesProductReportStrategy(SalesProductReportExcelHandler excelHandler) {
        this.excelHandler = excelHandler;
    }

    @Override
    public EntityTypeEnum getEntityType() {
        return EntityTypeEnum.SALES_PRODUCT_REPORT;
    }

    @Override
    public CustomMultipartFile generateReport(Object filter, FileType fileType) {
        StockReportByProductFilter salesReportFilter = objectMapper.convertValue(filter, StockReportByProductFilter.class);
        return switch (fileType) {
            case CSV -> null;
            case EXCEL -> new CustomMultipartFile(
                    excelHandler.generate(salesReportFilter),
                    excelHandler.getFileName(salesReportFilter),
                    CONTENT_TYPE_FOR_EXCEL
            );
        };
    }
}
