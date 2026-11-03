package uz.uzkassa.silen.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import uz.uzkassa.silen.config.DbConstants;
import uz.uzkassa.silen.dto.ExportReportDTO;
import uz.uzkassa.silen.enumeration.ExportReportType;
import uz.uzkassa.silen.enumeration.FileType;
import uz.uzkassa.silen.enumeration.ReportStatus;

import java.time.LocalDateTime;

@Getter
@Setter
@Table(schema = DbConstants.CURRENT_SCHEMA, name = "export_report")
@Entity
@SQLDelete(sql = "update export_report set deleted='true' where id=?")
public class ExportReport extends BaseIdentityOrganizationEntity {

    @Column(name = "name")
    String name;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    ExportReportType type;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    ReportStatus status = ReportStatus.PENDING;

    @Column(name = "path")
    String path;

    @Column(name = "file_type")
    @Enumerated(EnumType.STRING)
    FileType fileType;

    @Column(name = "error_message", columnDefinition = "TEXT")
    String errorMessage;

    @Column(name = "expire_date")
    LocalDateTime expireDate;

    @Column(name = "filters")
    String filters;

    @Column(name = "batch_id")
    String batchId;

    public ExportReportDTO toDTO() {
        ExportReportDTO exportReportDTO = new ExportReportDTO();
        exportReportDTO.setId(getId());
        exportReportDTO.setStatus(getStatus());
        exportReportDTO.setPath(getPath());
        exportReportDTO.setType(getType());
        exportReportDTO.setFileType(getFileType());
        exportReportDTO.setCreatedDate(getCreatedDate());
        exportReportDTO.setExpireDate(getExpireDate());
        exportReportDTO.setName(getName());
        return exportReportDTO;

    }
}
