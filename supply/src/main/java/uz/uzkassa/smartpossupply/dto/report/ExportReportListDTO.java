package uz.uzkassa.smartpossupply.dto.report;

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
 * Date: 15.02.2023 20:06
 */
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ExportReportListDTO {

    Long id;

    FileDTO file;

    ReportStatus status;

    EntityTypeEnum entityType;

    FileType fileType;

    LocalDateTime createdDate;

    LocalDateTime expireDate;
}
