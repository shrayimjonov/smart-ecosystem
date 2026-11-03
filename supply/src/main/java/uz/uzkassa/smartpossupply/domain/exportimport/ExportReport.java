package uz.uzkassa.smartpossupply.domain.exportimport;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.type.SqlTypes;
import uz.uzkassa.smartpossupply.domain.FileUpload;
import uz.uzkassa.smartpossupply.domain.base.AbstractAuditingEntity;
import uz.uzkassa.smartpossupply.domain.company.Company;
import uz.uzkassa.smartpossupply.enums.EntityTypeEnum;
import uz.uzkassa.smartpossupply.enums.FileType;
import uz.uzkassa.smartpossupply.enums.ReportStatus;
import uz.uzkassa.smartpossupply.interfaces.Auditable;

import java.io.Serial;
import java.time.LocalDateTime;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 15.02.2023 14:46
 */
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "export_report", indexes = {
        @Index(columnList = "entity_type", name = "export_report_entity_type_idx"),
        @Index(columnList = "company_id", name = "export_report_company_id_idx")
})
@Entity
@SQLDelete(sql = "update export_report set deleted='true' where id=?")
public class ExportReport extends AbstractAuditingEntity implements Auditable {

    @Serial
    private static final long serialVersionUID = 93059034534L;

    @Column(name = "entity_type")
    @Enumerated(EnumType.STRING)
    EntityTypeEnum entityType;

    @Column(name = "file_type")
    @Enumerated(EnumType.STRING)
    FileType fileType;

    @Column(name = "file_id")
    Long fileId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "file_id", insertable = false, updatable = false)
    FileUpload file;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    ReportStatus status = ReportStatus.PENDING;

    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
    @Column(name = "error_message")
    String errorMessage;

    @Column(name = "company_id")
    Long companyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", updatable = false, insertable = false)
    Company company;

    @Column(name = "expire_date")
    LocalDateTime expireDate;

    public void setStatus(ReportStatus status) {
        if (ReportStatus.COMPLETED.equals(status)) {
            this.expireDate = LocalDateTime.now().plusDays(3);
        }
        this.status = status;
    }

    @Override
    public Long getEntityId() {
        return this.id;
    }

    @Override
    public EntityTypeEnum getEntityType() {
        return this.entityType;
    }
}
