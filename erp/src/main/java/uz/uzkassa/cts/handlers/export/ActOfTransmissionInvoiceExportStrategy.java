package uz.uzkassa.cts.handlers.export;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.dto.cases.caseproperty.CustomMultipartFile;
import uz.uzkassa.cts.enums.ContentType;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.filter.PaymentScheduleInvoiceFilter;
import uz.uzkassa.cts.handlers.export.csv.ActOfTransmissionInvoiceExportCsvHandler;
import uz.uzkassa.cts.handlers.export.excel.ActOfTransmissionInvoiceExportExcelHandler;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class ActOfTransmissionInvoiceExportStrategy extends AbstractExportStrategy {

    ActOfTransmissionInvoiceExportExcelHandler actOfTransmissionInvoiceExportExcelHandler;
    ActOfTransmissionInvoiceExportCsvHandler actOfTransmissionInvoiceExportCsvHandler;

    public ActOfTransmissionInvoiceExportStrategy(ObjectMapper objectMapper, ActOfTransmissionInvoiceExportExcelHandler actOfTransmissionInvoiceExportExcelHandler, ActOfTransmissionInvoiceExportCsvHandler actOfTransmissionInvoiceExportCsvHandler) {
        super(objectMapper);
        this.actOfTransmissionInvoiceExportExcelHandler = actOfTransmissionInvoiceExportExcelHandler;
        this.actOfTransmissionInvoiceExportCsvHandler = actOfTransmissionInvoiceExportCsvHandler;
    }

    @Override
    public EntityTypeEnum getEntityType() {
        return EntityTypeEnum.ACT_OF_TRANSMISSION_INVOICE;
    }

    @Override
    public CustomMultipartFile generateReport(Object filterObject, ContentType contentType) {
        PaymentScheduleInvoiceFilter filter = objectMapper.convertValue(filterObject, PaymentScheduleInvoiceFilter.class);
        return switch (contentType) {
            case EXCEL -> new CustomMultipartFile(
                    actOfTransmissionInvoiceExportExcelHandler.generate(filter),
                    actOfTransmissionInvoiceExportExcelHandler.getFileName(filter),
                    CONTENT_TYPE_FOR_EXCEL
            );
            case CSV -> new CustomMultipartFile(
                    actOfTransmissionInvoiceExportCsvHandler.generate(filter),
                    actOfTransmissionInvoiceExportCsvHandler.getFileName(filter),
                    CONTENT_TYPE_FOR_CSV
            );
        };
    }
}
