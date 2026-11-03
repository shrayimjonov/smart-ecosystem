package uz.uzkassa.cts.domain;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import uz.uzkassa.cts.domain.base.AbstractAuditingEntity;
import uz.uzkassa.cts.enums.CustomerVatTypeEnum;
import uz.uzkassa.cts.enums.SmartOrOldDeviceEnum;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.io.Serial;
import java.math.BigDecimal;

@Entity
@Table(name = "trade_in_counter_device_price")
@Getter
@Setter
@SQLDelete(sql = "UPDATE trade_in_counter_device_price SET deleted = 'true' WHERE id=?")
@SQLRestriction(value = "deleted = 'false'")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TradeInCounterDevicePrice extends AbstractAuditingEntity {

    @Serial
    private static final long serialVersionUID = 1301794647L;

    @NotNull
    @Column(name = "price_1pc")
    private BigDecimal price1pc;
    @NotNull
    @Column(name = "price_1pc_with_vat")
    private BigDecimal price1pcWithVat;

    @Column(name = "vat_1pc")
    private BigDecimal vat1pc;
    @NotNull
    @Column(name = "price_with_vat")
    private BigDecimal priceWithVat;
    @NotNull
    @Column(name = "is_active")
    private boolean active;

    @NotNull
    @Column(name = "customer_vat_type")
    @Enumerated(EnumType.STRING)
    CustomerVatTypeEnum customerVatTypeEnum;

    @NotNull
    @Column(name = "smart_or_old_device")
    @Enumerated(EnumType.STRING)
    SmartOrOldDeviceEnum smartOrOldDeviceEnum;
}
