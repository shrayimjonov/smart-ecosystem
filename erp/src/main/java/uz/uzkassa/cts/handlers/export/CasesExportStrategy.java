package uz.uzkassa.cts.handlers.export;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.dto.cases.caseproperty.CustomMultipartFile;
import uz.uzkassa.cts.enums.ContentType;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.filter.CaseFilter;
import uz.uzkassa.cts.handlers.export.csv.CaseExportCsvHandler;
import uz.uzkassa.cts.handlers.export.excel.CaseExportExcelHandler;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class CasesExportStrategy extends AbstractExportStrategy {

    CaseExportExcelHandler caseExportExcelHandler;
    CaseExportCsvHandler caseExportCsvHandler;

    public CasesExportStrategy(ObjectMapper objectMapper, CaseExportExcelHandler caseExportExcelHandler, CaseExportCsvHandler caseExportCsvHandler) {
        super(objectMapper);
        this.caseExportExcelHandler = caseExportExcelHandler;
        this.caseExportCsvHandler = caseExportCsvHandler;
    }

    @Override
    public EntityTypeEnum getEntityType() {
        return EntityTypeEnum.CASES;
    }

    @Override
    public CustomMultipartFile generateReport(Object filterObject, ContentType contentType) {
        CaseFilter filter = objectMapper.convertValue(filterObject, CaseFilter.class);
        return switch (contentType) {
            case EXCEL -> new CustomMultipartFile(
                    caseExportExcelHandler.generate(filter),
                    caseExportExcelHandler.getFileName(filter),
                    CONTENT_TYPE_FOR_EXCEL
            );
            case CSV -> new CustomMultipartFile(
                    caseExportCsvHandler.generate(filter),
                    caseExportCsvHandler.getFileName(filter),
                    CONTENT_TYPE_FOR_CSV
            );
        };
    }
}
