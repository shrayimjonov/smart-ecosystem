package uz.uzkassa.cts.handlers.export;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.dto.cases.caseproperty.CustomMultipartFile;
import uz.uzkassa.cts.enums.ContentType;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.handlers.export.csv.HolidayExportCsvHandler;
import uz.uzkassa.cts.handlers.export.excel.HolidayExportExcelHandler;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class HolidayExportStrategy extends AbstractExportStrategy {

    HolidayExportExcelHandler holidayExportExcelHandler;
    HolidayExportCsvHandler holidayExportCsvHandler;

    public HolidayExportStrategy(ObjectMapper objectMapper, HolidayExportExcelHandler holidayExportExcelHandler, HolidayExportCsvHandler holidayExportCsvHandler) {
        super(objectMapper);
        this.holidayExportExcelHandler = holidayExportExcelHandler;
        this.holidayExportCsvHandler = holidayExportCsvHandler;
    }

    @Override
    public EntityTypeEnum getEntityType() {
        return EntityTypeEnum.HOLIDAY;
    }

    @Override
    public CustomMultipartFile generateReport(Object filterObject, ContentType contentType) {
        BaseFilter filter = objectMapper.convertValue(filterObject, BaseFilter.class);
        return switch (contentType) {
            case EXCEL -> new CustomMultipartFile(
                    holidayExportExcelHandler.generate(filter),
                    holidayExportExcelHandler.getFileName(filter),
                    CONTENT_TYPE_FOR_EXCEL
            );
            case CSV -> new CustomMultipartFile(
                    holidayExportCsvHandler.generate(filter),
                    holidayExportCsvHandler.getFileName(filter),
                    CONTENT_TYPE_FOR_CSV
            );
        };
    }
}
