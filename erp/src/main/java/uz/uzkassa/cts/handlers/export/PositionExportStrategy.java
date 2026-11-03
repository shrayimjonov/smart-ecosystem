package uz.uzkassa.cts.handlers.export;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.dto.cases.caseproperty.CustomMultipartFile;
import uz.uzkassa.cts.enums.ContentType;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.handlers.export.csv.PositionExportCsvHandler;
import uz.uzkassa.cts.handlers.export.excel.PositionExportExcelHandler;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class PositionExportStrategy extends AbstractExportStrategy {

    PositionExportExcelHandler positionExportExcelHandler;
    PositionExportCsvHandler positionExportCsvHandler;

    public PositionExportStrategy(ObjectMapper objectMapper, PositionExportExcelHandler positionExportExcelHandler, PositionExportCsvHandler positionExportCsvHandler) {
        super(objectMapper);
        this.positionExportExcelHandler = positionExportExcelHandler;
        this.positionExportCsvHandler = positionExportCsvHandler;
    }

    @Override
    public EntityTypeEnum getEntityType() {
        return EntityTypeEnum.POSITION;
    }

    @Override
    public CustomMultipartFile generateReport(Object filterObject, ContentType contentType) {
        BaseFilter filter = objectMapper.convertValue(filterObject, BaseFilter.class);
        return switch (contentType) {
            case EXCEL -> new CustomMultipartFile(
                    positionExportExcelHandler.generate(filter),
                    positionExportExcelHandler.getFileName(filter),
                    CONTENT_TYPE_FOR_EXCEL
            );
            case CSV -> new CustomMultipartFile(
                    positionExportCsvHandler.generate(filter),
                    positionExportCsvHandler.getFileName(filter),
                    CONTENT_TYPE_FOR_CSV
            );
        };
    }
}
