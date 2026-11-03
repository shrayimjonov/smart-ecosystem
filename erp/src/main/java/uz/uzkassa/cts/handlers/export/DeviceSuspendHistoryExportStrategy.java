package uz.uzkassa.cts.handlers.export;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.dto.cases.caseproperty.CustomMultipartFile;
import uz.uzkassa.cts.enums.ContentType;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.filter.DeviceSuspendHistoryFilter;
import uz.uzkassa.cts.handlers.export.csv.DeviceSuspendHistoryExportCsvHandler;
import uz.uzkassa.cts.handlers.export.excel.DeviceSuspendHistoryExportExcelHandler;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class DeviceSuspendHistoryExportStrategy extends AbstractExportStrategy {

    private final DeviceSuspendHistoryExportExcelHandler deviceSuspendHistoryExportExcelHandler;
    private final DeviceSuspendHistoryExportCsvHandler deviceSuspendHistoryExportCsvHandler;

    public DeviceSuspendHistoryExportStrategy(ObjectMapper objectMapper, DeviceSuspendHistoryExportExcelHandler deviceSuspendHistoryExportExcelHandler, DeviceSuspendHistoryExportCsvHandler deviceSuspendHistoryExportCsvHandler) {
        super(objectMapper);
        this.deviceSuspendHistoryExportExcelHandler = deviceSuspendHistoryExportExcelHandler;
        this.deviceSuspendHistoryExportCsvHandler = deviceSuspendHistoryExportCsvHandler;
    }

    @Override
    public EntityTypeEnum getEntityType() {
        return EntityTypeEnum.DEVICE_SUSPEND_HISTORY;
    }

    @Override
    public CustomMultipartFile generateReport(Object filterObject, ContentType contentType) {
        DeviceSuspendHistoryFilter filter = objectMapper.convertValue(filterObject, DeviceSuspendHistoryFilter.class);
        return switch (contentType) {
            case EXCEL -> new CustomMultipartFile(
                    deviceSuspendHistoryExportExcelHandler.generate(filter),
                    deviceSuspendHistoryExportExcelHandler.getFileName(filter),
                    CONTENT_TYPE_FOR_EXCEL
            );
            case CSV -> new CustomMultipartFile(
                    deviceSuspendHistoryExportCsvHandler.generate(filter),
                    deviceSuspendHistoryExportCsvHandler.getFileName(filter),
                    CONTENT_TYPE_FOR_CSV
            );
        };
    }
}
