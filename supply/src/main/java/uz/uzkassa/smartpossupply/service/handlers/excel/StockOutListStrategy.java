package uz.uzkassa.smartpossupply.service.handlers.excel;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.smartpossupply.enums.EntityTypeEnum;
import uz.uzkassa.smartpossupply.enums.FileType;
import uz.uzkassa.smartpossupply.filters.StockFilter;
import uz.uzkassa.smartpossupply.service.CustomMultipartFile;
import uz.uzkassa.smartpossupply.service.handlers.csv.StockOutListCsvHandler;
import uz.uzkassa.smartpossupply.service.handlers.excel.export.StockOutListExcelHandler;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StockOutListStrategy extends AbstractExportStrategy {

    StockOutListExcelHandler excelHandler;
    StockOutListCsvHandler csvHandler;

    public StockOutListStrategy(StockOutListExcelHandler excelHandler, StockOutListCsvHandler csvHandler) {
        this.excelHandler = excelHandler;
        this.csvHandler = csvHandler;
    }

    @Override
    public EntityTypeEnum getEntityType() {
        return EntityTypeEnum.STOCK_OUT;
    }

    @Override
    public CustomMultipartFile generateReport(Object filter, FileType fileType) {
        StockFilter stockProductFilter = objectMapper.convertValue(filter, StockFilter.class);
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
