package uz.uzkassa.cts.dto.payload;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.enums.ContentType;
import uz.uzkassa.cts.enums.EntityTypeEnum;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ExportReportPayload {

    Object filter;

    EntityTypeEnum entityType;

    ContentType contentType;

    Long exportReportId;

    public ExportReportPayload(Object filter, EntityTypeEnum entityType, ContentType contentType, Long exportReportId) {
        this.filter = filter;
        this.entityType = entityType;
        this.contentType = contentType;
        this.exportReportId = exportReportId;
    }
}
