package uz.uzkassa.cts.handlers.export;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.dto.cases.caseproperty.CustomMultipartFile;
import uz.uzkassa.cts.enums.ContentType;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.handlers.export.csv.LeaveRequestExportCsvHandler;
import uz.uzkassa.cts.handlers.export.excel.LeaveRequestExportExcelHandler;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class LeaveRequestExportStrategy extends AbstractExportStrategy {

    LeaveRequestExportExcelHandler leaveRequestExportExcelHandler;
    LeaveRequestExportCsvHandler leaveRequestExportCsvHandler;

    public LeaveRequestExportStrategy(ObjectMapper objectMapper, LeaveRequestExportExcelHandler leaveRequestExportExcelHandler, LeaveRequestExportCsvHandler leaveRequestExportCsvHandler) {
        super(objectMapper);
        this.leaveRequestExportExcelHandler = leaveRequestExportExcelHandler;
        this.leaveRequestExportCsvHandler = leaveRequestExportCsvHandler;
    }

    @Override
    public EntityTypeEnum getEntityType() {
        return EntityTypeEnum.LEAVE_REQUEST;
    }

    @Override
    public CustomMultipartFile generateReport(Object filterObject, ContentType contentType) {
        BaseFilter filter = objectMapper.convertValue(filterObject, BaseFilter.class);
        return switch (contentType) {
            case EXCEL -> new CustomMultipartFile(
                    leaveRequestExportExcelHandler.generate(filter),
                    leaveRequestExportExcelHandler.getFileName(filter),
                    CONTENT_TYPE_FOR_EXCEL
            );
            case CSV -> new CustomMultipartFile(
                    leaveRequestExportCsvHandler.generate(filter),
                    leaveRequestExportCsvHandler.getFileName(filter),
                    CONTENT_TYPE_FOR_CSV
            );
        };
    }
}
