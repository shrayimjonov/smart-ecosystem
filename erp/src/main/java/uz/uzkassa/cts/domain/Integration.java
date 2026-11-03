package uz.uzkassa.cts.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Cache;
import org.hibernate.type.SqlTypes;
import uz.uzkassa.cts.domain.base.AbstractAuditingEntity;
import uz.uzkassa.cts.enums.CommonStatus;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.enums.IntegrationType;
import uz.uzkassa.cts.interfaces.Auditable;

import java.io.Serial;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "integration")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@SQLDelete(sql = "UPDATE integration SET deleted = 'true' WHERE id=?")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Integration extends AbstractAuditingEntity implements Auditable {

    @Serial
    private static final long serialVersionUID = 7098075937L;

    @Column(name = "name", nullable = false)
    String name;

    @JdbcTypeCode(SqlTypes.LONGNVARCHAR)
    @Column(name = "description", nullable = false)
    String description;

    @Column(name = "username")
    String username;

    @Column(name = "password")
    String password;

    @Column(name = "logo_id")
    Long logoId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "logo_id", updatable = false, insertable = false)
    FileUpload logo;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    IntegrationType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    CommonStatus status = CommonStatus.IN_ACTIVE;

    @Column(name = "error_message")
    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
    String errorMessage;

    @Column(name = "error_date")
    LocalDateTime errorDate;

    public void setName(String name) {
        addChangeLog("name", this.name, name);
        this.name = name;
    }

    public void setDescription(String description) {
        addChangeLog("description", this.description, description);
        this.description = description;
    }

    public void setUsername(String username) {
        addChangeLog("username", this.username, username);
        this.username = username;
    }

    public void setLogo(FileUpload logo) {
        addChangeLog("logo", this.logo != null ? this.logo.getName() : null, logo != null ? logo.getName() : null);
        this.logo = logo;
    }

    public void setType(IntegrationType type) {
        addChangeLog("type", this.type != null ? this.type.toString() : null, type != null ? type.toString() : null);
        this.type = type;
    }

    public void setStatus(CommonStatus status) {
        addChangeLog("status", this.status != null ? this.status.getName() : null, status != null ? status.getName() : null);
        this.status = status;
    }

    public void setErrorMessage(String errorMessage) {
        addChangeLog("errorMessage", this.errorMessage, errorMessage);
        this.errorMessage = errorMessage;
    }

    public void setErrorDate(LocalDateTime errorDate) {
        addChangeLog("errorDate", this.errorDate != null ? this.errorDate.toString() : null, errorDate != null ? errorDate.toString() : null);
        this.errorDate = errorDate;
    }

    @Override
    public Long getEntityId() {
        return this.id;
    }

    @Override
    public String getEntityName() {
        return this.name;
    }

    @Override
    public EntityTypeEnum getEntityType() {
        return EntityTypeEnum.INTEGRATION;
    }
}
