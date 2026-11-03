package uz.uzkassa.cts.handlers.export;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.dto.cases.caseproperty.CustomMultipartFile;
import uz.uzkassa.cts.enums.ContentType;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.filter.AttendanceFilter;
import uz.uzkassa.cts.handlers.export.csv.AttendanceExportCsvHandler;
import uz.uzkassa.cts.handlers.export.excel.AttendanceExportExcelHandler;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class AttendanceExportStrategy extends AbstractExportStrategy {

    AttendanceExportExcelHandler attendanceExportExcelHandler;
    AttendanceExportCsvHandler attendanceExportCsvHandler;

    public AttendanceExportStrategy(ObjectMapper objectMapper, AttendanceExportExcelHandler attendanceExportExcelHandler, AttendanceExportCsvHandler attendanceExportCsvHandler) {
        super(objectMapper);
        this.attendanceExportExcelHandler = attendanceExportExcelHandler;
        this.attendanceExportCsvHandler = attendanceExportCsvHandler;
    }

    @Override
    public EntityTypeEnum getEntityType() {
        return EntityTypeEnum.ATTENDANCE;
    }

    @Override
    public CustomMultipartFile generateReport(Object filterObject, ContentType contentType) {
        AttendanceFilter filter = objectMapper.convertValue(filterObject, AttendanceFilter.class);
        return switch (contentType) {
            case EXCEL -> new CustomMultipartFile(
                    attendanceExportExcelHandler.generate(filter),
                    attendanceExportExcelHandler.getFileName(filter),
                    CONTENT_TYPE_FOR_EXCEL
            );
            case CSV -> new CustomMultipartFile(
                    attendanceExportCsvHandler.generate(filter),
                    attendanceExportCsvHandler.getFileName(filter),
                    CONTENT_TYPE_FOR_CSV
            );
        };
    }
}
