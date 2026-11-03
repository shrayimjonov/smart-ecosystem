package uz.uzkassa.cts.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.domain.base.SimpleEntity;
import uz.uzkassa.cts.enums.BankTariffHistoryType;

import java.io.Serial;
import java.time.LocalDateTime;

@Getter
@Setter
@Table(name = "bank_tariff_history")
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BankTariffHistory extends SimpleEntity {

    @Serial
    private static final long serialVersionUID = 5320202771L;

    @Column(name = "created_date")
    LocalDateTime createdDate;

    @Column(name = "customer_id")
    Long customerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", updatable = false, insertable = false)
    Customer customer;

    @Column(name = "bank_tariff_id")
    Long bankTariffId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bank_tariff_id", updatable = false, insertable = false)
    BankTariff bankTariff;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    BankTariffHistoryType status;
}
