package uz.uzkassa.cts.handlers.imports;

import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.cts.dto.payload.ImportReportItemPayload;
import uz.uzkassa.cts.dto.payload.ImportReportPayload;
import uz.uzkassa.cts.enums.ContentType;
import uz.uzkassa.cts.enums.EntityTypeEnum;

import java.util.List;

public interface ImportStrategy {

    EntityTypeEnum getEntityType();

    List<ImportReportItemPayload> getItems(MultipartFile file, ContentType contentType);

    void importReportQueue(ImportReportPayload payload);
}
