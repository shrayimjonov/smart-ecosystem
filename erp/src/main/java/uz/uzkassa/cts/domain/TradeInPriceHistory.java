package uz.uzkassa.cts.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import uz.uzkassa.cts.converter.OriginConverter;
import uz.uzkassa.cts.domain.base.AbstractAuditingEntity;
import uz.uzkassa.cts.enums.ContractType;
import uz.uzkassa.cts.enums.DeviceType;
import uz.uzkassa.cts.enums.Origin;

import java.io.Serial;
import java.math.BigDecimal;

@Entity
@Table(name = "trade_in_price_history")
@Getter
@Setter
@SQLDelete(sql = "UPDATE trade_in_price_history SET deleted = 'true' WHERE id=?")
@SQLRestriction(value = "deleted = 'false'")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TradeInPriceHistory extends AbstractAuditingEntity {

    @Serial
    static final long serialVersionUID = 9475592531L;

    @NotNull
    @Column(name = "price_1pc", nullable = false)
    BigDecimal price1pc;

    @NotNull
    @Column(name = "price_1pc_with_vat", nullable = false)
    BigDecimal price1pcWithVat;

    @NotNull
    @Column(name = "vat_1pc", nullable = false)
    BigDecimal vat1pc;

    @NotNull
    @Column(name = "price_with_vat", nullable = false)
    BigDecimal priceWithVat;

    @Column(name = "is_active")
    boolean active;

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

    @NotNull
    @Column(name = "device_model_id", nullable = false)
    Long deviceModelId;

    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "device_model_id", updatable = false, insertable = false)
    DeviceModel deviceModel;

    @Column(name = "bonus", precision = 15, scale = 2)
    BigDecimal bonus = BigDecimal.ZERO;

    @Column(name = "requires_letter_of_guarantee")
    boolean requiresLetterOfGuarantee = false;

    @Convert(converter = OriginConverter.class)
    @Column(name = "Origin", columnDefinition = "int default 3")
    Origin origin;

    @Enumerated(EnumType.STRING)
    @Column(name = "contract_type")
    ContractType contractType;

    @Column(name = "device_type")
    @Enumerated(EnumType.STRING)
    DeviceType deviceType;
}
