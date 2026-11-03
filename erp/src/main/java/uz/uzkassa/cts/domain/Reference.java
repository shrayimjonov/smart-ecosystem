package uz.uzkassa.cts.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import uz.uzkassa.cts.domain.base.LocalizedEntity;
import uz.uzkassa.cts.enums.CommonStatus;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.enums.ReferenceType;
import uz.uzkassa.cts.interfaces.Auditable;

import java.io.Serial;
import java.io.Serializable;


@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "reference",
        indexes = {
                @Index(columnList = "code", name = "reference_code_idx"),
                @Index(columnList = "type", name = "reference_type_idx"),
                @Index(columnList = "parent_id", name = "reference_parent_id_idx"),
                @Index(columnList = "external_id", name = "reference_external_id_idx"),
                @Index(columnList = "external_code", name = "reference_external_code_idx"),
                @Index(columnList = "company_id", name = "reference_company_id_idx")
        })
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Reference extends LocalizedEntity implements Auditable {

    @Serial
    private static final long serialVersionUID = 4545645L;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    ReferenceType type;

    @NotNull
    @Column(name = "code", nullable = false)
    String code;

    @Column(name = "short_name")
    String shortName;

    @Column(name = "color")
    String color;

    @Column(name = "external_id")
    Long externalId;

    @Column(name = "external_code")
    String externalCode;

    @Column(name = "parent_id")
    Long parentId;

    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id", updatable = false, insertable = false)
    Reference parent;

    @Column(name = "sort_order")
    Integer sortOrder;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    CommonStatus status = CommonStatus.ACTIVE;

    @Column(name = "company_id")
    Long companyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", updatable = false, insertable = false)
    Company company;

    public void setType(ReferenceType type) {
        addChangeLog("type", this.type != null ? this.type.toString() : null, type != null ? type.toString() : null);
        this.type = type;
    }

    public void setCode(String code) {
        addChangeLog("code", this.code, code);
        this.code = code;
    }

    public void setExternalId(Long externalId) {
        addChangeLog("externalId", this.externalId != null ? this.externalId.toString() : null, externalId != null ? externalId.toString() : null);
        this.externalId = externalId;
    }

    public void setExternalCode(String externalCode) {
        addChangeLog("externalCode", this.externalCode, externalCode);
        this.externalCode = externalCode;
    }

    public void setStatus(CommonStatus status) {
        addChangeLog("status", this.status != null ? this.status.getName() : null, status != null ? status.getName() : null);
        this.status = status;
    }

    public void setShortName(String shortName) {
        addChangeLog("shortName", this.shortName, shortName);
        this.shortName = shortName;
    }

    public void setColor(String color) {
        addChangeLog("color", this.color, color);
        this.color = color;
    }

    public void setSortOrder(Integer sortOrder) {
        addChangeLog("sortOrder", this.sortOrder != null ? this.sortOrder.toString() : null, sortOrder != null ? sortOrder.toString() : null);
        this.sortOrder = sortOrder;
    }

    public void setCompany(Company company) {
        addChangeLog("company", this.company != null ? this.company.getName() : null, company != null ? company.getName() : null);
        this.company = company;
    }

    @Override
    public Long getEntityId() {
        return id;
    }

    @Override
    public EntityTypeEnum getEntityType() {
        return EntityTypeEnum.REFERENCE;
    }
}