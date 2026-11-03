package uz.uzkassa.cts.handlers.export;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.dto.cases.caseproperty.CustomMultipartFile;
import uz.uzkassa.cts.enums.ContentType;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.filter.OneTimeOrderFilter;
import uz.uzkassa.cts.handlers.export.csv.OneTimeOrderExportCsvHandler;
import uz.uzkassa.cts.handlers.export.excel.OneTimeOrderExportExcelHandler;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class OneTimeOrderExportStrategy extends AbstractExportStrategy {

    OneTimeOrderExportExcelHandler oneTimeOrderExportExcelHandler;
    OneTimeOrderExportCsvHandler oneTimeOrderExportCsvHandler;

    public OneTimeOrderExportStrategy(ObjectMapper objectMapper, OneTimeOrderExportExcelHandler oneTimeOrderExportExcelHandler, OneTimeOrderExportCsvHandler oneTimeOrderExportCsvHandler) {
        super(objectMapper);
        this.oneTimeOrderExportExcelHandler = oneTimeOrderExportExcelHandler;
        this.oneTimeOrderExportCsvHandler = oneTimeOrderExportCsvHandler;
    }

    @Override
    public EntityTypeEnum getEntityType() {
        return EntityTypeEnum.ONE_TIME_ORDER;
    }

    @Override
    public CustomMultipartFile generateReport(Object filterObject, ContentType contentType) {
        OneTimeOrderFilter filter = objectMapper.convertValue(filterObject, OneTimeOrderFilter.class);
        return switch (contentType) {
            case EXCEL -> new CustomMultipartFile(
                    oneTimeOrderExportExcelHandler.generate(filter),
                    oneTimeOrderExportExcelHandler.getFileName(filter),
                    CONTENT_TYPE_FOR_EXCEL
            );
            case CSV -> new CustomMultipartFile(
                    oneTimeOrderExportCsvHandler.generate(filter),
                    oneTimeOrderExportCsvHandler.getFileName(filter),
                    CONTENT_TYPE_FOR_CSV
            );
        };
    }
}
