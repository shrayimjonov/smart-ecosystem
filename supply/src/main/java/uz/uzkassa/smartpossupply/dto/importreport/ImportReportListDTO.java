package uz.uzkassa.smartpossupply.dto.importreport;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.file.FileDTO;
import uz.uzkassa.smartpossupply.enums.EntityTypeEnum;
import uz.uzkassa.smartpossupply.enums.FileType;
import uz.uzkassa.smartpossupply.enums.ReportStatus;

import java.time.LocalDateTime;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 3.03.2023 15:37
 */
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ImportReportListDTO {

    Long id;

    FileDTO file;

    ReportStatus status;

    EntityTypeEnum entityType;

    FileType fileType;

    LocalDateTime createdDate;

    Integer itemTotal;

    Integer importedTotal;

    Integer errorTotal;
}
