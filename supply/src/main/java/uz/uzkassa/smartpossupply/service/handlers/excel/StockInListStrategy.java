package uz.uzkassa.smartpossupply.service.handlers.excel;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.smartpossupply.enums.EntityTypeEnum;
import uz.uzkassa.smartpossupply.enums.FileType;
import uz.uzkassa.smartpossupply.filters.StockFilter;
import uz.uzkassa.smartpossupply.service.CustomMultipartFile;
import uz.uzkassa.smartpossupply.service.handlers.csv.StockInListCsvHandler;
import uz.uzkassa.smartpossupply.service.handlers.excel.export.StockInListExcelHandler;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StockInListStrategy extends AbstractExportStrategy {

    StockInListExcelHandler excelHandler;
    StockInListCsvHandler csvHandler;

    public StockInListStrategy(StockInListExcelHandler excelHandler, StockInListCsvHandler csvHandler) {
        this.excelHandler = excelHandler;
        this.csvHandler = csvHandler;
    }

    @Override
    public EntityTypeEnum getEntityType() {
        return EntityTypeEnum.STOCK_IN;
    }

    @Override
    public CustomMultipartFile generateReport(Object filter, FileType fileType) {
        StockFilter balanceReportFilter = objectMapper.convertValue(filter, StockFilter.class);
        return switch (fileType) {
            case CSV -> new CustomMultipartFile(
                    csvHandler.generate(balanceReportFilter),
                    csvHandler.getFileName(balanceReportFilter),
                    CONTENT_TYPE_FOR_CSV
            );
            case EXCEL -> new CustomMultipartFile(
                    excelHandler.generate(balanceReportFilter),
                    excelHandler.getFileName(balanceReportFilter),
                    CONTENT_TYPE_FOR_EXCEL
            );
        };
    }
}
