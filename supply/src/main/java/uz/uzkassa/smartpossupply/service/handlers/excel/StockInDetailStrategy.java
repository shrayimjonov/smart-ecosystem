package uz.uzkassa.smartpossupply.service.handlers.excel;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.smartpossupply.enums.EntityTypeEnum;
import uz.uzkassa.smartpossupply.enums.FileType;
import uz.uzkassa.smartpossupply.filters.StockProductFilter;
import uz.uzkassa.smartpossupply.service.CustomMultipartFile;
import uz.uzkassa.smartpossupply.service.handlers.csv.StockInDetailCsvHandler;
import uz.uzkassa.smartpossupply.service.handlers.excel.export.StockInDetailExcelHandler;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StockInDetailStrategy extends AbstractExportStrategy {

    StockInDetailExcelHandler excelHandler;
    StockInDetailCsvHandler csvHandler;

    public StockInDetailStrategy(StockInDetailExcelHandler excelHandler, StockInDetailCsvHandler csvHandler) {
        this.excelHandler = excelHandler;
        this.csvHandler = csvHandler;
    }

    @Override
    public EntityTypeEnum getEntityType() {
        return EntityTypeEnum.STOCK_IN_PRODUCT;
    }

    @Override
    public CustomMultipartFile generateReport(Object filter, FileType fileType) {
        StockProductFilter stockProductFilter = objectMapper.convertValue(filter, StockProductFilter.class);
        return switch (fileType) {
            case CSV -> new CustomMultipartFile(
                    csvHandler.generate(stockProductFilter),
                    csvHandler.getFileName(stockProductFilter),
                    CONTENT_TYPE_FOR_CSV
            );
            case EXCEL -> new CustomMultipartFile(
                    excelHandler.generate(stockProductFilter),
                    excelHandler.getFileName(stockProductFilter),
                    CONTENT_TYPE_FOR_EXCEL
            );
        };
    }
}
