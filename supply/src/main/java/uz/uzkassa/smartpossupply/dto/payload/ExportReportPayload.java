package uz.uzkassa.smartpossupply.dto.payload;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.enums.EntityTypeEnum;
import uz.uzkassa.smartpossupply.enums.FileType;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ExportReportPayload {

    Object filter;

    EntityTypeEnum entityType;

    String originalName;

    FileType fileType;

    Long exportReportId;

    public ExportReportPayload(Object filter, EntityTypeEnum entityType, String originalName, FileType fileType, Long exportReportId) {
        this.filter = filter;
        this.entityType = entityType;
        this.originalName = originalName;
        this.fileType = fileType;
        this.exportReportId = exportReportId;
    }

    public ExportReportPayload(Object filter, EntityTypeEnum entityType, FileType fileType, Long exportReportId) {
        this.filter = filter;
        this.entityType = entityType;
        this.fileType = fileType;
        this.exportReportId = exportReportId;
    }
}
