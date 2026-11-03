package uz.uzkassa.cts.handlers.export;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.dto.cases.caseproperty.CustomMultipartFile;
import uz.uzkassa.cts.enums.ContentType;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.filter.OneTimeOrderInvoiceFilter;
import uz.uzkassa.cts.handlers.export.csv.OneTimeOrderInvoiceExportCsvHandler;
import uz.uzkassa.cts.handlers.export.excel.OneTimeOrderInvoiceExportExcelHandler;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class OneTimeOrderInvoiceExportStrategy extends AbstractExportStrategy {

    OneTimeOrderInvoiceExportExcelHandler oneTimeOrderInvoiceExportExcelHandler;
    OneTimeOrderInvoiceExportCsvHandler oneTimeOrderInvoiceExportCsvHandler;

    public OneTimeOrderInvoiceExportStrategy(ObjectMapper objectMapper, OneTimeOrderInvoiceExportExcelHandler oneTimeOrderInvoiceExportExcelHandler, OneTimeOrderInvoiceExportCsvHandler oneTimeOrderInvoiceExportCsvHandler) {
        super(objectMapper);
        this.oneTimeOrderInvoiceExportExcelHandler = oneTimeOrderInvoiceExportExcelHandler;
        this.oneTimeOrderInvoiceExportCsvHandler = oneTimeOrderInvoiceExportCsvHandler;
    }

    @Override
    public EntityTypeEnum getEntityType() {
        return EntityTypeEnum.ONE_TIME_ORDER_INVOICE;
    }

    @Override
    public CustomMultipartFile generateReport(Object filterObject, ContentType contentType) {
        OneTimeOrderInvoiceFilter filter = objectMapper.convertValue(filterObject, OneTimeOrderInvoiceFilter.class);
        return switch (contentType) {
            case EXCEL -> new CustomMultipartFile(
                    oneTimeOrderInvoiceExportExcelHandler.generate(filter),
                    oneTimeOrderInvoiceExportExcelHandler.getFileName(filter),
                    CONTENT_TYPE_FOR_EXCEL
            );
            case CSV -> new CustomMultipartFile(
                    oneTimeOrderInvoiceExportCsvHandler.generate(filter),
                    oneTimeOrderInvoiceExportCsvHandler.getFileName(filter),
                    CONTENT_TYPE_FOR_CSV
            );
        };
    }
}
