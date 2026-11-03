package uz.uzkassa.smartpossupply.service.handlers.excel;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.smartpossupply.enums.EntityTypeEnum;
import uz.uzkassa.smartpossupply.enums.FileType;
import uz.uzkassa.smartpossupply.filters.StockAdjustmentFilter;
import uz.uzkassa.smartpossupply.service.CustomMultipartFile;
import uz.uzkassa.smartpossupply.service.handlers.csv.StockAdjustmentListCsvHandler;
import uz.uzkassa.smartpossupply.service.handlers.excel.export.StockAdjustmentListExcelHandler;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StockAdjustmentListStrategy extends AbstractExportStrategy {

    StockAdjustmentListExcelHandler excelHandler;
    StockAdjustmentListCsvHandler csvHandler;

    public StockAdjustmentListStrategy(StockAdjustmentListExcelHandler excelHandler, StockAdjustmentListCsvHandler csvHandler) {
        this.excelHandler = excelHandler;
        this.csvHandler = csvHandler;
    }

    @Override
    public EntityTypeEnum getEntityType() {
        return EntityTypeEnum.STOCK_ADJUSTMENT;
    }

    @Override
    public CustomMultipartFile generateReport(Object filter, FileType fileType) {
        StockAdjustmentFilter stockAdjustmentFilter = objectMapper.convertValue(filter, StockAdjustmentFilter.class);
        return switch (fileType) {
            case CSV -> new CustomMultipartFile(
                    csvHandler.generate(stockAdjustmentFilter),
                    csvHandler.getFileName(stockAdjustmentFilter),
                    CONTENT_TYPE_FOR_CSV
            );
            case EXCEL -> new CustomMultipartFile(
                    excelHandler.generate(stockAdjustmentFilter),
                    excelHandler.getFileName(stockAdjustmentFilter),
                    CONTENT_TYPE_FOR_EXCEL
            );
        };
    }
}
