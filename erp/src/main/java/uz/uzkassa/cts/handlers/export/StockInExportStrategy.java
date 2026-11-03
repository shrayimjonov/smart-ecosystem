package uz.uzkassa.cts.handlers.export;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.dto.cases.caseproperty.CustomMultipartFile;
import uz.uzkassa.cts.enums.ContentType;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.filter.StockFilter;
import uz.uzkassa.cts.handlers.export.csv.StockInExportCsvHandler;
import uz.uzkassa.cts.handlers.export.excel.StockInExportExcelHandler;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class StockInExportStrategy extends AbstractExportStrategy {

    StockInExportExcelHandler stockInExportExcelHandler;
    StockInExportCsvHandler stockInExportCsvHandler;

    public StockInExportStrategy(ObjectMapper objectMapper, StockInExportExcelHandler stockInExportExcelHandler, StockInExportCsvHandler stockInExportCsvHandler) {
        super(objectMapper);
        this.stockInExportExcelHandler = stockInExportExcelHandler;
        this.stockInExportCsvHandler = stockInExportCsvHandler;
    }

    @Override
    public EntityTypeEnum getEntityType() {
        return EntityTypeEnum.STOCK_IN;
    }

    @Override
    public CustomMultipartFile generateReport(Object filterObject, ContentType contentType) {
        StockFilter filter = objectMapper.convertValue(filterObject, StockFilter.class);
        return switch (contentType) {
            case EXCEL -> new CustomMultipartFile(
                    stockInExportExcelHandler.generate(filter),
                    stockInExportExcelHandler.getFileName(filter),
                    CONTENT_TYPE_FOR_EXCEL
            );
            case CSV -> new CustomMultipartFile(
                    stockInExportCsvHandler.generate(filter),
                    stockInExportCsvHandler.getFileName(filter),
                    CONTENT_TYPE_FOR_CSV
            );
        };
    }
}
