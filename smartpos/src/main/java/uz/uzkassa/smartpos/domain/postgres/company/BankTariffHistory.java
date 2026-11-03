package uz.uzkassa.smartpos.domain.postgres.company;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpos.enums.BankType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Table(name = "bank_tariff_history")
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BankTariffHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "created_date")
    LocalDateTime createdDate;

    @Column(name = "company_id")
    Long companyId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", updatable = false, insertable = false)
    Company company;

    @Enumerated(EnumType.STRING)
    @Column(name = "bank_type")
    private BankType bankType;

    @Column(name = "humo_percentage")
    BigDecimal humoPercentage;

    @Column(name = "visa_percentage")
    BigDecimal visaPercentage;

    @Column(name = "visa_domestic_percentage")
    BigDecimal visaDomesticPercentage;

    @Column(name = "uzcard_percentage")
    BigDecimal uzcardPercentage;

    @Column(name = "xizmat_id", nullable = false)
    Long xizmatId;

    @Column(name = "xizmat_name")
    String xizmatName;

}
