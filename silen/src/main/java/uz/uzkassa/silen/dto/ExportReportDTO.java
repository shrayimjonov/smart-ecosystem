package uz.uzkassa.silen.dto;

import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.silen.enumeration.ExportReportType;
import uz.uzkassa.silen.enumeration.FileType;
import uz.uzkassa.silen.enumeration.ReportStatus;

import java.time.LocalDateTime;

@Getter
@Setter
public class ExportReportDTO {

    Long id;

    String name;

    String path;

    ExportReportType type;

    ReportStatus status;

    FileType fileType;

    LocalDateTime createdDate;

    LocalDateTime expireDate;

}
