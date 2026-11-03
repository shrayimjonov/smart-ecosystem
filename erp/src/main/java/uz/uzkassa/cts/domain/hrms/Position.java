package uz.uzkassa.cts.domain.hrms;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.SQLRestriction;
import uz.uzkassa.cts.domain.Company;
import uz.uzkassa.cts.domain.base.LocalizedEntity;
import uz.uzkassa.cts.enums.CommonStatus;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.interfaces.Auditable;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "erp_position",
        indexes = {
                @Index(columnList = "status", name = "position_status_idx"),
                @Index(columnList = "parent_id", name = "position_parent_id_idx"),
                @Index(columnList = "company_id", name = "position_company_id_idx")
        })
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Position extends LocalizedEntity implements Auditable {

    @Serial
    private static final long serialVersionUID = 6754678954L;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    CommonStatus status = CommonStatus.ACTIVE;

    @Column(name = "parent_id")
    Long parentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id", updatable = false, insertable = false)
    Position parent;

    @Column(name = "company_id")
    Long companyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", updatable = false, insertable = false)
    Company company;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    @SQLRestriction(value = "deleted = 'false'")
    List<Position> children = new ArrayList<>();

    public void setStatus(CommonStatus status) {
        addChangeLog("status", this.status != null ? this.status.getName() : null, status != null ? status.getName() : null);
        this.status = status;
    }

    public void setParent(Position parent) {
        addChangeLog("parent", this.parent != null ? this.parent.getName() : null, parent != null ? parent.getName() : null);
        this.parent = parent;
    }

    @Override
    public Long getEntityId() {
        return this.id;
    }

    @Override
    public String getEntityName() {
        return getName();
    }

    @Override
    public EntityTypeEnum getEntityType() {
        return EntityTypeEnum.POSITION;
    }
}
