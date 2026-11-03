package uz.uzkassa.cts.handlers.export;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.dto.cases.caseproperty.CustomMultipartFile;
import uz.uzkassa.cts.enums.ContentType;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.filter.LeadFilter;
import uz.uzkassa.cts.handlers.export.csv.LeadExportCsvHandler;
import uz.uzkassa.cts.handlers.export.excel.LeadExportExcelHandler;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class LeadExportStrategy extends AbstractExportStrategy {

    LeadExportExcelHandler leadExcelExportHandler;
    LeadExportCsvHandler leadCsvExportHandler;

    public LeadExportStrategy(ObjectMapper objectMapper, LeadExportExcelHandler leadExcelExportHandler, LeadExportCsvHandler leadCsvExportHandler) {
        super(objectMapper);
        this.leadExcelExportHandler = leadExcelExportHandler;
        this.leadCsvExportHandler = leadCsvExportHandler;
    }

    @Override
    public EntityTypeEnum getEntityType() {
        return EntityTypeEnum.LEAD;
    }

    @Override
    public CustomMultipartFile generateReport(Object filterObject, ContentType contentType) {
        LeadFilter filter = objectMapper.convertValue(filterObject, LeadFilter.class);
        return switch (contentType) {
            case EXCEL -> new CustomMultipartFile(
                    leadExcelExportHandler.generate(filter),
                    leadExcelExportHandler.getFileName(filter),
                    CONTENT_TYPE_FOR_EXCEL
            );
            case CSV -> new CustomMultipartFile(
                    leadCsvExportHandler.generate(filter),
                    leadCsvExportHandler.getFileName(filter),
                    CONTENT_TYPE_FOR_CSV
            );
        };
    }
}
