package uz.uzkassa.smartpossupply.domain.company;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.SQLDelete;
import uz.uzkassa.smartpossupply.domain.Bank;
import uz.uzkassa.smartpossupply.domain.base.BaseEntity;

import java.io.Serial;

@Entity
@Table(
    name = "company_bank",
    indexes = {
        @Index(columnList = "company_id", name = "company_bank_company_id_idx"),
        @Index(columnList = "bank_id", name = "company_bank_bank_id_idx"),
    }
)
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@SQLDelete(sql = "UPDATE company_bank SET deleted = true WHERE id=?")
public class CompanyBank extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 10L;

    @Column(name = "company_id")
    Long companyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", updatable = false, insertable = false)
    Company company;

    @Column(name = "bank_id")
    Long bankId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bank_id", updatable = false, insertable = false)
    Bank bank;

    @Column(name = "account_number")
    String accountNumber;

    @Column(name = "oked")
    String oked;

    @Column(name = "is_main", columnDefinition = "boolean default false")
    boolean main;
}
