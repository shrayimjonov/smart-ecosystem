package uz.uzkassa.cts.handlers.export;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.dto.cases.caseproperty.CustomMultipartFile;
import uz.uzkassa.cts.enums.ContentType;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.filter.TradeInInvoiceFilter;
import uz.uzkassa.cts.handlers.export.csv.TradeInInvoiceExportCsvHandler;
import uz.uzkassa.cts.handlers.export.excel.TradeInInvoiceExportExcelHandler;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class TradeInInvoiceExportStrategy extends AbstractExportStrategy {

    TradeInInvoiceExportExcelHandler tradeInInvoiceExportExcelHandler;
    TradeInInvoiceExportCsvHandler tradeInInvoiceExportCsvHandler;

    public TradeInInvoiceExportStrategy(ObjectMapper objectMapper, TradeInInvoiceExportExcelHandler tradeInInvoiceExportExcelHandler, TradeInInvoiceExportCsvHandler tradeInInvoiceExportCsvHandler) {
        super(objectMapper);
        this.tradeInInvoiceExportExcelHandler = tradeInInvoiceExportExcelHandler;
        this.tradeInInvoiceExportCsvHandler = tradeInInvoiceExportCsvHandler;
    }

    @Override
    public EntityTypeEnum getEntityType() {
        return EntityTypeEnum.TRADE_IN_INVOICE;
    }

    @Override
    public CustomMultipartFile generateReport(Object filterObject, ContentType contentType) {
        TradeInInvoiceFilter filter = objectMapper.convertValue(filterObject, TradeInInvoiceFilter.class);
        return switch (contentType) {
            case EXCEL -> new CustomMultipartFile(
                    tradeInInvoiceExportExcelHandler.generate(filter),
                    tradeInInvoiceExportExcelHandler.getFileName(filter),
                    CONTENT_TYPE_FOR_EXCEL
            );
            case CSV -> new CustomMultipartFile(
                    tradeInInvoiceExportCsvHandler.generate(filter),
                    tradeInInvoiceExportCsvHandler.getFileName(filter),
                    CONTENT_TYPE_FOR_CSV
            );
        };
    }
}
