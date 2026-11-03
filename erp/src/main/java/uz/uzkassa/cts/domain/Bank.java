package uz.uzkassa.cts.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import uz.uzkassa.cts.domain.base.LocalizedEntity;
import uz.uzkassa.cts.enums.CommonStatus;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.interfaces.Auditable;

import java.io.Serial;

@Entity
@Table(name = "banks",
        indexes = {
                @Index(columnList = "code", name = "bank_code_idx"),
                @Index(columnList = "status", name = "bank_status_idx")
        })
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@SQLDelete(sql = "UPDATE banks SET deleted = 'true' WHERE id=?")
@SQLRestriction(value = "deleted = 'false'")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Bank extends LocalizedEntity implements Auditable {

    @Serial
    private static final long serialVersionUID = 6441981738L;

    @Column(name = "code", unique = true, nullable = false, length = 5)
    String code;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    CommonStatus status = CommonStatus.ACTIVE;

    public void setCode(String code) {
        addChangeLog("code", this.code, code);
        this.code = code;
    }

    public void setStatus(CommonStatus status) {
        addChangeLog("status", this.status != null ? status.getName() : null, status != null ? status.getName() : null);
        this.status = status;
    }

    @Override
    public Long getEntityId() {
        return id;
    }

    @Override
    public EntityTypeEnum getEntityType() {
        return EntityTypeEnum.BANK;
    }
}
