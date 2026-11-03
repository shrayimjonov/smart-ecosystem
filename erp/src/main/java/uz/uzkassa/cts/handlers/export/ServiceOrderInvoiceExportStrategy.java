package uz.uzkassa.cts.handlers.export;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.dto.cases.caseproperty.CustomMultipartFile;
import uz.uzkassa.cts.enums.ContentType;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.filter.ServiceOrderInvoiceFilter;
import uz.uzkassa.cts.handlers.export.csv.ServiceOrderInvoiceExportCsvHandler;
import uz.uzkassa.cts.handlers.export.excel.ServiceOrderInvoiceExportExcelHandler;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class ServiceOrderInvoiceExportStrategy extends AbstractExportStrategy {

    ServiceOrderInvoiceExportExcelHandler serviceOrderInvoiceExportExcelHandler;
    ServiceOrderInvoiceExportCsvHandler serviceOrderInvoiceExportCsvHandler;

    public ServiceOrderInvoiceExportStrategy(ObjectMapper objectMapper, ServiceOrderInvoiceExportExcelHandler serviceOrderInvoiceExportExcelHandler, ServiceOrderInvoiceExportCsvHandler serviceOrderInvoiceExportCsvHandler) {
        super(objectMapper);
        this.serviceOrderInvoiceExportExcelHandler = serviceOrderInvoiceExportExcelHandler;
        this.serviceOrderInvoiceExportCsvHandler = serviceOrderInvoiceExportCsvHandler;
    }

    @Override
    public EntityTypeEnum getEntityType() {
        return EntityTypeEnum.SERVICE_ORDER_INVOICE;
    }

    @Override
    public CustomMultipartFile generateReport(Object filterObject, ContentType contentType) {
        ServiceOrderInvoiceFilter filter = objectMapper.convertValue(filterObject, ServiceOrderInvoiceFilter.class);
        return switch (contentType) {
            case EXCEL -> new CustomMultipartFile(
                    serviceOrderInvoiceExportExcelHandler.generate(filter),
                    serviceOrderInvoiceExportExcelHandler.getFileName(filter),
                    CONTENT_TYPE_FOR_EXCEL
            );
            case CSV -> new CustomMultipartFile(
                    serviceOrderInvoiceExportCsvHandler.generate(filter),
                    serviceOrderInvoiceExportCsvHandler.getFileName(filter),
                    CONTENT_TYPE_FOR_CSV
            );
        };
    }
}
