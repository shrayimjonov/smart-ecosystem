package uz.uzkassa.cts.handlers.export;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.dto.cases.caseproperty.CustomMultipartFile;
import uz.uzkassa.cts.enums.ContentType;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.filter.TradeInInvoiceFilter;
import uz.uzkassa.cts.handlers.export.csv.SaleInvoiceExportCsvHandler;
import uz.uzkassa.cts.handlers.export.excel.SaleInvoiceExportExcelHandler;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class SaleInvoiceExportStrategy extends AbstractExportStrategy {

    private final SaleInvoiceExportExcelHandler saleInvoiceExportExcelHandler;
    private final SaleInvoiceExportCsvHandler saleInvoiceExportCsvHandler;

    public SaleInvoiceExportStrategy(ObjectMapper objectMapper, SaleInvoiceExportExcelHandler saleInvoiceExportExcelHandler, SaleInvoiceExportCsvHandler saleInvoiceExportCsvHandler) {
        super(objectMapper);
        this.saleInvoiceExportExcelHandler = saleInvoiceExportExcelHandler;
        this.saleInvoiceExportCsvHandler = saleInvoiceExportCsvHandler;
    }

    @Override
    public EntityTypeEnum getEntityType() {
        return EntityTypeEnum.SALE_INVOICE;
    }

    @Override
    public CustomMultipartFile generateReport(Object filterObject, ContentType contentType) {
        TradeInInvoiceFilter filter = objectMapper.convertValue(filterObject, TradeInInvoiceFilter.class);
        return switch (contentType) {
            case EXCEL -> new CustomMultipartFile(
                    saleInvoiceExportExcelHandler.generate(filter),
                    saleInvoiceExportExcelHandler.getFileName(filter),
                    CONTENT_TYPE_FOR_EXCEL
            );
            case CSV -> new CustomMultipartFile(
                    saleInvoiceExportCsvHandler.generate(filter),
                    saleInvoiceExportCsvHandler.getFileName(filter),
                    CONTENT_TYPE_FOR_CSV
            );
        };
    }
}
