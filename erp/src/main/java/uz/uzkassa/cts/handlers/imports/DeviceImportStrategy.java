package uz.uzkassa.cts.handlers.imports;

import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.cts.dto.payload.ImportReportItemPayload;
import uz.uzkassa.cts.dto.payload.ImportReportPayload;
import uz.uzkassa.cts.enums.ContentType;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.handlers.imports.excel.DeviceImportExcelHandler;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class DeviceImportStrategy implements ImportStrategy {

    DeviceImportExcelHandler deviceImportExcelHandler;

    public DeviceImportStrategy(DeviceImportExcelHandler deviceImportExcelHandler) {
        this.deviceImportExcelHandler = deviceImportExcelHandler;
    }

    @Override
    public EntityTypeEnum getEntityType() {
        return EntityTypeEnum.DEVICE;
    }

    @Override
    public List<ImportReportItemPayload> getItems(MultipartFile file, ContentType contentType) {
        if (ContentType.EXCEL == contentType) {
            return deviceImportExcelHandler.readFile(file);
        }
        return null;
    }

    @Override
    public void importReportQueue(ImportReportPayload payload) {
        deviceImportExcelHandler.importReportQueue(payload);
    }
}
