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
import uz.uzkassa.cts.enums.*;

import java.io.Serial;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tariff")
@Getter
@Setter
@SQLDelete(sql = "UPDATE tariff SET deleted = 'true' WHERE id=?")
@SQLRestriction(value = "deleted = 'false'")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Tariff extends AbstractAuditingEntity {

    @Serial
    private static final long serialVersionUID = 5906416585L;

    @NotNull
    @Column(name = "name")
    String name;

    @Column(name = "code", length = 11)
    String code;

    @NotNull
    @Column(name = "price")
    BigDecimal price;

    @NotNull
    @Column(name = "discount")
    Double discount;

    @NotNull
    @Column(name = "period")
    Integer period;

    @Column(name = "free_trail_period")
    Integer freeTrialPeriod;

    @Column(name = "free_trail_period_type")
    @Enumerated(EnumType.STRING)
    FreeTrialPeriodType freeTrialPeriodType;

    @NotNull
    @Column(name = "discount_type")
    @Enumerated(EnumType.STRING)
    DiscountType discountType;

    @NotNull
    @Column(name = "period_type")
    @Enumerated(EnumType.STRING)
    PeriodType periodType;

    @NotNull
    @Column(name = "tariff_special_type")
    @Enumerated(EnumType.STRING)
    TariffSpecialType tariffSpecialType;

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

    @Column(name = "category")
    @Enumerated(EnumType.STRING)
    private SupplierCategory category;

    @ManyToMany
    @JoinTable(
            name = "tariff_services",
            joinColumns = {@JoinColumn(name = "tariff_id")},
            inverseJoinColumns = {@JoinColumn(name = "cts_service_id")}
    )
    //todo use Set
    //https://thorben-janssen.com/best-practices-for-many-to-many-associations-with-hibernate-and-jpa/
    List<CtsService> ctsServices = new ArrayList<>();

    @Convert(converter = OriginConverter.class)
    @Column(name = "Origin", columnDefinition = "int default 3")
    private Origin origin;

}
