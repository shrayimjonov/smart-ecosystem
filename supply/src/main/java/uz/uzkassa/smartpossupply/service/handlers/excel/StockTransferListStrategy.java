package uz.uzkassa.smartpossupply.service.handlers.excel;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.smartpossupply.enums.EntityTypeEnum;
import uz.uzkassa.smartpossupply.enums.FileType;
import uz.uzkassa.smartpossupply.filters.StockTransferFilter;
import uz.uzkassa.smartpossupply.service.CustomMultipartFile;
import uz.uzkassa.smartpossupply.service.handlers.csv.StockTransferListCsvHandler;
import uz.uzkassa.smartpossupply.service.handlers.excel.export.StockTransferListExcelHandler;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StockTransferListStrategy extends AbstractExportStrategy {

    StockTransferListExcelHandler excelHandler;
    StockTransferListCsvHandler csvHandler;

    public StockTransferListStrategy(StockTransferListExcelHandler excelHandler, StockTransferListCsvHandler csvHandler) {
        this.excelHandler = excelHandler;
        this.csvHandler = csvHandler;
    }

    @Override
    public EntityTypeEnum getEntityType() {
        return EntityTypeEnum.STOCK_TRANSFER;
    }

    @Override
    public CustomMultipartFile generateReport(Object filter, FileType fileType) {
        StockTransferFilter stockTransferFilter = objectMapper.convertValue(filter, StockTransferFilter.class);
        return switch (fileType) {
            case CSV -> new CustomMultipartFile(
                    csvHandler.generate(stockTransferFilter),
                    csvHandler.getFileName(stockTransferFilter),
                    CONTENT_TYPE_FOR_CSV
            );
            case EXCEL -> new CustomMultipartFile(
                    excelHandler.generate(stockTransferFilter),
                    excelHandler.getFileName(stockTransferFilter),
                    CONTENT_TYPE_FOR_EXCEL
            );
        };
    }
}
