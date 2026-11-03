package uz.uzkassa.cts.handlers.export;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.dto.cases.caseproperty.CustomMultipartFile;
import uz.uzkassa.cts.enums.ContentType;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.handlers.export.csv.DeviceExportCsvHandler;
import uz.uzkassa.cts.handlers.export.excel.DeviceExportExcelHandler;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class DeviceExportStrategy extends AbstractExportStrategy {

    DeviceExportExcelHandler deviceExcelExportHandler;
    DeviceExportCsvHandler deviceCsvExportHandler;

    public DeviceExportStrategy(ObjectMapper objectMapper, DeviceExportExcelHandler deviceExcelExportHandler, DeviceExportCsvHandler deviceCsvExportHandler) {
        super(objectMapper);
        this.deviceExcelExportHandler = deviceExcelExportHandler;
        this.deviceCsvExportHandler = deviceCsvExportHandler;
    }

    @Override
    public EntityTypeEnum getEntityType() {
        return EntityTypeEnum.DEVICE;
    }

    @Override
    public CustomMultipartFile generateReport(Object filterObject, ContentType contentType) {
        BaseFilter filter = objectMapper.convertValue(filterObject, BaseFilter.class);
        return switch (contentType) {
            case EXCEL -> new CustomMultipartFile(
                    deviceExcelExportHandler.generate(filter),
                    deviceExcelExportHandler.getFileName(filter),
                    CONTENT_TYPE_FOR_EXCEL
            );
            case CSV -> new CustomMultipartFile(
                    deviceCsvExportHandler.generate(filter),
                    deviceCsvExportHandler.getFileName(filter),
                    CONTENT_TYPE_FOR_CSV
            );
        };
    }
}
