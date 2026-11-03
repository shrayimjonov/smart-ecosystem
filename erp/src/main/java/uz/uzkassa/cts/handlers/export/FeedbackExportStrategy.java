package uz.uzkassa.cts.handlers.export;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.dto.cases.caseproperty.CustomMultipartFile;
import uz.uzkassa.cts.enums.ContentType;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.filter.FeedbackFilter;
import uz.uzkassa.cts.handlers.export.csv.FeedbackExportCsvHandler;
import uz.uzkassa.cts.handlers.export.excel.FeedbackExportExcelHandler;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class FeedbackExportStrategy extends AbstractExportStrategy {

    FeedbackExportExcelHandler feedbackExportExcelHandler;
    FeedbackExportCsvHandler feedbackExportCsvHandler;

    public FeedbackExportStrategy(ObjectMapper objectMapper, FeedbackExportExcelHandler feedbackExportExcelHandler, FeedbackExportCsvHandler feedbackExportCsvHandler) {
        super(objectMapper);
        this.feedbackExportExcelHandler = feedbackExportExcelHandler;
        this.feedbackExportCsvHandler = feedbackExportCsvHandler;
    }

    @Override
    public EntityTypeEnum getEntityType() {
        return EntityTypeEnum.FEEDBACK;
    }

    @Override
    public CustomMultipartFile generateReport(Object filterObject, ContentType contentType) {
        FeedbackFilter filter = objectMapper.convertValue(filterObject, FeedbackFilter.class);
        return switch (contentType) {
            case EXCEL -> new CustomMultipartFile(
                    feedbackExportExcelHandler.generate(filter),
                    feedbackExportExcelHandler.getFileName(filter),
                    CONTENT_TYPE_FOR_EXCEL
            );
            case CSV -> new CustomMultipartFile(
                    feedbackExportCsvHandler.generate(filter),
                    feedbackExportCsvHandler.getFileName(filter),
                    CONTENT_TYPE_FOR_CSV
            );
        };
    }
}
