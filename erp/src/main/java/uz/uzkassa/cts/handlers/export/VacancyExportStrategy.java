package uz.uzkassa.cts.handlers.export;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.dto.cases.caseproperty.CustomMultipartFile;
import uz.uzkassa.cts.enums.ContentType;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.filter.VacancyFilter;
import uz.uzkassa.cts.handlers.export.csv.VacancyExportCsvHandler;
import uz.uzkassa.cts.handlers.export.excel.VacancyExportExcelHandler;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class VacancyExportStrategy extends AbstractExportStrategy {

    VacancyExportExcelHandler vacancyExportExcelHandler;
    VacancyExportCsvHandler vacancyExportCsvHandler;

    public VacancyExportStrategy(ObjectMapper objectMapper, VacancyExportExcelHandler vacancyExportExcelHandler, VacancyExportCsvHandler vacancyExportCsvHandler) {
        super(objectMapper);
        this.vacancyExportExcelHandler = vacancyExportExcelHandler;
        this.vacancyExportCsvHandler = vacancyExportCsvHandler;
    }

    @Override
    public EntityTypeEnum getEntityType() {
        return EntityTypeEnum.VACANCY;
    }

    @Override
    public CustomMultipartFile generateReport(Object filterObject, ContentType contentType) {
        VacancyFilter filter = objectMapper.convertValue(filterObject, VacancyFilter.class);
        return switch (contentType) {
            case EXCEL -> new CustomMultipartFile(
                    vacancyExportExcelHandler.generate(filter),
                    vacancyExportExcelHandler.getFileName(filter),
                    CONTENT_TYPE_FOR_EXCEL
            );
            case CSV -> new CustomMultipartFile(
                    vacancyExportCsvHandler.generate(filter),
                    vacancyExportCsvHandler.getFileName(filter),
                    CONTENT_TYPE_FOR_CSV
            );
        };
    }
}
