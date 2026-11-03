package uz.uzkassa.cts.handlers.export;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.dto.cases.caseproperty.CustomMultipartFile;
import uz.uzkassa.cts.enums.ContentType;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.handlers.export.csv.BonusExportCsvHandler;
import uz.uzkassa.cts.handlers.export.excel.BonusExportExcelHandler;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class BonusExportStrategy extends AbstractExportStrategy {

    BonusExportExcelHandler bonusExportExcelHandler;
    BonusExportCsvHandler bonusExportCsvHandler;

    public BonusExportStrategy(ObjectMapper objectMapper, BonusExportExcelHandler bonusExportExcelHandler, BonusExportCsvHandler bonusExportCsvHandler) {
        super(objectMapper);
        this.bonusExportExcelHandler = bonusExportExcelHandler;
        this.bonusExportCsvHandler = bonusExportCsvHandler;
    }

    @Override
    public EntityTypeEnum getEntityType() {
        return EntityTypeEnum.BONUS;
    }

    @Override
    public CustomMultipartFile generateReport(Object filterObject, ContentType contentType) {
        BaseFilter filter = objectMapper.convertValue(filterObject, BaseFilter.class);
        return switch (contentType) {
            case EXCEL -> new CustomMultipartFile(
                    bonusExportExcelHandler.generate(filter),
                    bonusExportExcelHandler.getFileName(filter),
                    CONTENT_TYPE_FOR_EXCEL
            );
            case CSV -> new CustomMultipartFile(
                    bonusExportCsvHandler.generate(filter),
                    bonusExportCsvHandler.getFileName(filter),
                    CONTENT_TYPE_FOR_CSV
            );
        };
    }
}
