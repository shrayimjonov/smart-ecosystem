package uz.uzkassa.cts.handlers.export;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.dto.cases.caseproperty.CustomMultipartFile;
import uz.uzkassa.cts.enums.ContentType;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.filter.StockFilter;
import uz.uzkassa.cts.handlers.export.csv.StockAdjustmentExportCsvHandler;
import uz.uzkassa.cts.handlers.export.excel.StockAdjustmentExportExcelHandler;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class StockAdjustmentExportStrategy extends AbstractExportStrategy {

    StockAdjustmentExportExcelHandler stockAdjustmentExportExcelHandler;
    StockAdjustmentExportCsvHandler stockAdjustmentExportCsvHandler;

    public StockAdjustmentExportStrategy(ObjectMapper objectMapper, StockAdjustmentExportExcelHandler stockAdjustmentExportExcelHandler, StockAdjustmentExportCsvHandler stockAdjustmentExportCsvHandler) {
        super(objectMapper);
        this.stockAdjustmentExportExcelHandler = stockAdjustmentExportExcelHandler;
        this.stockAdjustmentExportCsvHandler = stockAdjustmentExportCsvHandler;
    }

    @Override
    public EntityTypeEnum getEntityType() {
        return EntityTypeEnum.STOCK_ADJUSTMENT;
    }

    @Override
    public CustomMultipartFile generateReport(Object filterObject, ContentType contentType) {
        StockFilter filter = objectMapper.convertValue(filterObject, StockFilter.class);
        return switch (contentType) {
            case EXCEL -> new CustomMultipartFile(
                    stockAdjustmentExportExcelHandler.generate(filter),
                    stockAdjustmentExportExcelHandler.getFileName(filter),
                    CONTENT_TYPE_FOR_EXCEL
            );
            case CSV -> new CustomMultipartFile(
                    stockAdjustmentExportCsvHandler.generate(filter),
                    stockAdjustmentExportCsvHandler.getFileName(filter),
                    CONTENT_TYPE_FOR_CSV
            );
        };
    }
}
