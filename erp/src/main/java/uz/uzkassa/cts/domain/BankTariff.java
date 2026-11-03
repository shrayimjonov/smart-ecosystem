package uz.uzkassa.cts.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.domain.base.SimpleEntity;
import uz.uzkassa.cts.enums.BankType;
import uz.uzkassa.cts.enums.CommonStatus;
import uz.uzkassa.cts.enums.CustomerAppType;

import java.io.Serial;
import java.math.BigDecimal;

@Entity
@Table(name = "bank_tariff")
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BankTariff extends SimpleEntity {

    @Serial
    private static final long serialVersionUID = 7179518487L;

    @Column(name = "name")
    String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "bank_type")
    BankType bankType;

    @Column(name = "humo_percentage", scale = 2, precision = 15, nullable = false)
    BigDecimal humoPercentage;

    @Column(name = "visa_percentage", scale = 2, precision = 15, nullable = false)
    BigDecimal visaPercentage;

    @Column(name = "visa_domestic_percentage", scale = 2, precision = 15, nullable = false)
    BigDecimal visaDomesticPercentage;

    @Column(name = "uzcard_percentage", scale = 2, precision = 15, nullable = false)
    BigDecimal uzcardPercentage;

    @Column(name = "xizmat_id", nullable = false)
    Long xizmatId;

    @Column(name = "xizmat_name")
    String xizmatName;

    @Column(name = "app_type")
    @Enumerated(EnumType.STRING)
    CustomerAppType appType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    CommonStatus status = CommonStatus.ACTIVE;

    @Column(name = "permission", columnDefinition = "boolean default false")
    boolean permission = false;

    @Override
    public String getName() {
        return getXizmatName();
    }
}
