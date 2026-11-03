package uz.uzkassa.cts.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.SQLDelete;
import uz.uzkassa.cts.domain.base.LocalizedEntity;
import uz.uzkassa.cts.dto.vat.VatItemDTO;
import uz.uzkassa.cts.enums.CommonStatus;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.interfaces.Auditable;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "vat", indexes = {
        @Index(columnList = "percent", name = "vat_percent_idx"),
        @Index(columnList = "status", name = "vat_status_idx"),
        @Index(columnList = "active_date", name = "vat_active_date_idx")
})
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@SQLDelete(sql = "UPDATE vat SET deleted = 'true' WHERE id=?")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Vat extends LocalizedEntity implements Auditable {

    @Serial
    private static final long serialVersionUID = 5482797861L;

    public static final BigDecimal DEFAULT_VAT_RATE = new BigDecimal(12);

    @NotNull
    @Column(name = "percent", nullable = false)
    BigDecimal percent;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    CommonStatus status = CommonStatus.IN_ACTIVE;

    @Column(name = "active_date", unique = true)
    LocalDate activeDate;

    public void setPercent(BigDecimal percent) {
        addChangeLog("percent", this.percent != null ? this.percent.toString() : null, percent != null ? percent.toString() : null);
        this.percent = percent;
    }

    public void setStatus(CommonStatus status) {
        addChangeLog("status", this.status != null ? this.status.getName() : null, status != null ? status.getName() : null);
        this.status = status;
    }

    public VatItemDTO toDTO() {
        return new VatItemDTO(getId(), getName(), getPercent());
    }

    @Override
    public Long getEntityId() {
        return id;
    }

    @Override
    public EntityTypeEnum getEntityType() {
        return EntityTypeEnum.VAT;
    }
}
