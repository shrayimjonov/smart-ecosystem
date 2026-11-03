package uz.uzkassa.cts.handlers.export;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.dto.cases.caseproperty.CustomMultipartFile;
import uz.uzkassa.cts.enums.ContentType;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.filter.StockFilter;
import uz.uzkassa.cts.handlers.export.csv.StockOutExportCsvHandler;
import uz.uzkassa.cts.handlers.export.excel.StockOutExportExcelHandler;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class StockOutExportStrategy extends AbstractExportStrategy {

    StockOutExportExcelHandler stockOutExportExcelHandler;
    StockOutExportCsvHandler stockOutExportCsvHandler;

    public StockOutExportStrategy(ObjectMapper objectMapper, StockOutExportExcelHandler stockOutExportExcelHandler, StockOutExportCsvHandler stockOutExportCsvHandler) {
        super(objectMapper);
        this.stockOutExportExcelHandler = stockOutExportExcelHandler;
        this.stockOutExportCsvHandler = stockOutExportCsvHandler;
    }

    @Override
    public EntityTypeEnum getEntityType() {
        return EntityTypeEnum.STOCK_OUT;
    }

    @Override
    public CustomMultipartFile generateReport(Object filterObject, ContentType contentType) {
        StockFilter filter = objectMapper.convertValue(filterObject, StockFilter.class);
        return switch (contentType) {
            case EXCEL -> new CustomMultipartFile(
                    stockOutExportExcelHandler.generate(filter),
                    stockOutExportExcelHandler.getFileName(filter),
                    CONTENT_TYPE_FOR_EXCEL
            );
            case CSV -> new CustomMultipartFile(
                    stockOutExportCsvHandler.generate(filter),
                    stockOutExportCsvHandler.getFileName(filter),
                    CONTENT_TYPE_FOR_CSV
            );
        };
    }
}
