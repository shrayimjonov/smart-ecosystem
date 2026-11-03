package uz.uzkassa.cts.dto.exportimportreport;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.dto.file.FileDTO;
import uz.uzkassa.cts.enums.ContentType;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.enums.ExportReportStatus;

import java.io.Serializable;
import java.time.LocalDateTime;


@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ImportReportListDTO implements Serializable {

    Long id;

    FileDTO file;

    ExportReportStatus status;

    EntityTypeEnum entityType;

    ContentType contentType;

    LocalDateTime createdDate;

    CommonDTO creator;

    Integer itemTotal;

    Integer importedTotal;

    Integer errorTotal;
}
