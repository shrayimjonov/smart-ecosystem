package uz.uzkassa.cts.domain.exportimport;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.type.SqlTypes;
import uz.uzkassa.cts.domain.FileUpload;
import uz.uzkassa.cts.domain.base.AbstractAuditingEntity;
import uz.uzkassa.cts.enums.ContentType;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.enums.ExportReportStatus;
import uz.uzkassa.cts.interfaces.Auditable;

import java.io.Serial;
import java.time.LocalDateTime;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "export_report")
@Entity
@SQLDelete(sql = "update export_report set deleted='true' where id=?")
public class ExportReport extends AbstractAuditingEntity implements Auditable {

    @Serial
    private static final long serialVersionUID = 9778932345L;

    @NotNull
    @Column(name = "entity_type", nullable = false)
    @Enumerated(EnumType.STRING)
    EntityTypeEnum reportEntityType;

    @NotNull
    @Column(name = "content_type", nullable = false)
    @Enumerated(EnumType.STRING)
    ContentType contentType;

    @Column(name = "file_id")
    Long fileId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "file_id", insertable = false, updatable = false)
    FileUpload file;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    ExportReportStatus status = ExportReportStatus.PENDING;

    @Column(name = "error_message")
    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
    String errorMessage;

    @Column(name = "expire_date")
    LocalDateTime expireDate;

    public void setReportEntityType(EntityTypeEnum reportEntityType) {
        addChangeLog("reportEntityType", this.reportEntityType != null ? this.reportEntityType.getName() : null, reportEntityType != null ? reportEntityType.getName() : null);
        this.reportEntityType = reportEntityType;
    }

    public void setContentType(ContentType contentType) {
        addChangeLog("contentType", this.contentType != null ? this.contentType.toString() : null, contentType != null ? contentType.toString() : null);
        this.contentType = contentType;
    }

    public void setStatus(ExportReportStatus status) {
        addChangeLog("status", this.status != null ? this.status.getName() : null, status != null ? status.getName() : null);
        this.status = status;
    }

    public void setExpireDate(LocalDateTime expireDate) {
        addChangeLog("expireDate", this.expireDate != null ? this.expireDate.toString() : null, expireDate != null ? expireDate.toString() : null);
        this.expireDate = expireDate;
    }

    @Override
    public Long getEntityId() {
        return this.id;
    }

    @Override
    public EntityTypeEnum getEntityType() {
        return EntityTypeEnum.EXPORT_REPORT;
    }
}
