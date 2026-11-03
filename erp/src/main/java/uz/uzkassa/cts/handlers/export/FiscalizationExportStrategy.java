package uz.uzkassa.cts.handlers.export;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.dto.cases.caseproperty.CustomMultipartFile;
import uz.uzkassa.cts.enums.ContentType;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.handlers.export.csv.FiscalizationExportCsvHandler;
import uz.uzkassa.cts.handlers.export.excel.FiscalizationExportExcelHandler;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class FiscalizationExportStrategy extends AbstractExportStrategy {

    FiscalizationExportExcelHandler fiscalizationExportExcelHandler;
    FiscalizationExportCsvHandler fiscalizationExportCsvHandler;

    public FiscalizationExportStrategy(ObjectMapper objectMapper, FiscalizationExportExcelHandler fiscalizationExportExcelHandler, FiscalizationExportCsvHandler fiscalizationExportCsvHandler) {
        super(objectMapper);
        this.fiscalizationExportExcelHandler = fiscalizationExportExcelHandler;
        this.fiscalizationExportCsvHandler = fiscalizationExportCsvHandler;
    }

    @Override
    public EntityTypeEnum getEntityType() {
        return EntityTypeEnum.FISCALIZATION;
    }

    @Override
    public CustomMultipartFile generateReport(Object filterObject, ContentType contentType) {
        BaseFilter filter = objectMapper.convertValue(filterObject, BaseFilter.class);
        return switch (contentType) {
            case EXCEL -> new CustomMultipartFile(
                    fiscalizationExportExcelHandler.generate(filter),
                    fiscalizationExportExcelHandler.getFileName(filter),
                    CONTENT_TYPE_FOR_EXCEL
            );
            case CSV -> new CustomMultipartFile(
                    fiscalizationExportCsvHandler.generate(filter),
                    fiscalizationExportCsvHandler.getFileName(filter),
                    CONTENT_TYPE_FOR_CSV
            );
        };
    }
}
