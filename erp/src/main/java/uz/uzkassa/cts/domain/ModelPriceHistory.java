package uz.uzkassa.cts.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import uz.uzkassa.cts.domain.base.AbstractAuditingEntity;
import uz.uzkassa.cts.dto.base.CommonCodeDTO;
import uz.uzkassa.cts.enums.CompanyType;
import uz.uzkassa.cts.enums.DeviceType;
import uz.uzkassa.cts.enums.OrderType;
import uz.uzkassa.cts.enums.PeriodType;

import java.io.Serial;
import java.math.BigDecimal;

@Entity
@Table(name = "model_price_history")
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@SQLDelete(sql = "UPDATE model_price_history SET deleted = 'true' WHERE id=?")
@SQLRestriction(value = "deleted = 'false'")
public class ModelPriceHistory extends AbstractAuditingEntity {

    @Serial
    private static final long serialVersionUID = 2248044746L;

    @NotNull
    String name;

    @NotNull
    Integer period;

    @NotNull
    @Column(name = "period_type")
    @Enumerated(EnumType.STRING)
    PeriodType periodType;

    @NotNull
    BigDecimal price;

    BigDecimal firstPaymentForLeasing;

    @NotNull
    @Column(name = "model_id")
    Long modelId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "model_id", insertable = false, updatable = false)
    DeviceModel deviceModel;

    @Column(name = "order_type")
    @Enumerated(EnumType.STRING)
    OrderType orderType;

    @Column(name = "bonus", precision = 15, scale = 2)
    BigDecimal bonus = BigDecimal.ZERO;

    @Column(name = "catalog_code")
    String catalogCode;

    @Column(name = "catalog_name")
    String catalogName;

    @Column(name = "package_name")
    String packageName;

    @Column(name = "package_code")
    String packageCode;

    @Column(name = "code")
    String code;

    @Column(name = "active")
    Boolean active = true;

    @Enumerated(EnumType.STRING)
    @Column(name = "company_type")
    private CompanyType companyType;

    @Column(name = "device_type")
    @Enumerated(EnumType.STRING)
    DeviceType deviceType;

    public CommonCodeDTO toCommonCodeDTO() {
        return new CommonCodeDTO(getId(), getName(), getOrderType() != null ? getOrderType().getCode() : null);
    }
}
