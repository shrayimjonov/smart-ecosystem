package uz.uzkassa.cts.handlers.export;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.dto.cases.caseproperty.CustomMultipartFile;
import uz.uzkassa.cts.enums.ContentType;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.handlers.export.csv.VacancyApplicationExportCsvHandler;
import uz.uzkassa.cts.handlers.export.excel.VacancyApplicationExportExcelHandler;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class VacancyApplicationExportStrategy extends AbstractExportStrategy {

    VacancyApplicationExportExcelHandler vacancyApplicationExportExcelHandler;
    VacancyApplicationExportCsvHandler vacancyApplicationExportCsvHandler;

    public VacancyApplicationExportStrategy(ObjectMapper objectMapper, VacancyApplicationExportExcelHandler vacancyApplicationExportExcelHandler, VacancyApplicationExportCsvHandler vacancyApplicationExportCsvHandler) {
        super(objectMapper);
        this.vacancyApplicationExportExcelHandler = vacancyApplicationExportExcelHandler;
        this.vacancyApplicationExportCsvHandler = vacancyApplicationExportCsvHandler;
    }

    @Override
    public EntityTypeEnum getEntityType() {
        return EntityTypeEnum.VACANCY_APPLICATION;
    }

    @Override
    public CustomMultipartFile generateReport(Object filterObject, ContentType contentType) {
        BaseFilter filter = objectMapper.convertValue(filterObject, BaseFilter.class);
        return switch (contentType) {
            case EXCEL -> new CustomMultipartFile(
                    vacancyApplicationExportExcelHandler.generate(filter),
                    vacancyApplicationExportExcelHandler.getFileName(filter),
                    CONTENT_TYPE_FOR_EXCEL
            );
            case CSV -> new CustomMultipartFile(
                    vacancyApplicationExportCsvHandler.generate(filter),
                    vacancyApplicationExportCsvHandler.getFileName(filter),
                    CONTENT_TYPE_FOR_CSV
            );
        };
    }
}
