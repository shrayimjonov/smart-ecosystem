package uz.uzkassa.cts.handlers.export;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.dto.cases.caseproperty.CustomMultipartFile;
import uz.uzkassa.cts.enums.ContentType;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.handlers.export.csv.CandidateExportCsvHandler;
import uz.uzkassa.cts.handlers.export.excel.CandidateExportExcelHandler;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class CandidateExportStrategy extends AbstractExportStrategy {

    CandidateExportExcelHandler candidateExcelExportHandler;
    CandidateExportCsvHandler candidateExportCsvHandler;

    public CandidateExportStrategy(ObjectMapper objectMapper, CandidateExportExcelHandler candidateExcelExportHandler, CandidateExportCsvHandler candidateExportCsvHandler) {
        super(objectMapper);
        this.candidateExcelExportHandler = candidateExcelExportHandler;
        this.candidateExportCsvHandler = candidateExportCsvHandler;
    }

    @Override
    public EntityTypeEnum getEntityType() {
        return EntityTypeEnum.CANDIDATE;
    }

    @Override
    public CustomMultipartFile generateReport(Object filterObject, ContentType contentType) {
        BaseFilter filter = objectMapper.convertValue(filterObject, BaseFilter.class);
        return switch (contentType) {
            case EXCEL -> new CustomMultipartFile(
                    candidateExcelExportHandler.generate(filter),
                    candidateExcelExportHandler.getFileName(filter),
                    CONTENT_TYPE_FOR_EXCEL
            );
            case CSV -> new CustomMultipartFile(
                    candidateExportCsvHandler.generate(filter),
                    candidateExportCsvHandler.getFileName(filter),
                    CONTENT_TYPE_FOR_CSV
            );
        };
    }
}
