package uz.uzkassa.cts.handlers.export;

import uz.uzkassa.cts.dto.cases.caseproperty.CustomMultipartFile;
import uz.uzkassa.cts.enums.ContentType;
import uz.uzkassa.cts.enums.EntityTypeEnum;

public interface ExportStrategy {

    EntityTypeEnum getEntityType();

    CustomMultipartFile generateReport(Object filter, ContentType contentType);
}
