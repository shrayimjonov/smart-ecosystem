package uz.uzkassa.horeca.domain.company;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.SQLDelete;
import uz.uzkassa.horeca.domain.Bank;
import uz.uzkassa.horeca.domain.base.BaseEntity;

import java.io.Serial;
import java.io.Serializable;


@Entity
@Table(name = "company_bank",
        indexes = {
                @Index(columnList = "bank_id", name = "company_bank_bank_id_idx"),
                @Index(columnList = "account_number", name = "company_bank_account_number_idx"),
                @Index(columnList = "company_id", name = "company_bank_company_id_idx")
        })
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@SQLDelete(sql = "UPDATE company_bank set deleted='true' WHERE id=?")
public class CompanyBank extends BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 10L;

    @NotNull
    @Column(name = "bank_id", nullable = false)
    Long bankId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bank_id", insertable = false, updatable = false)
    Bank bank;

    @Column(name = "account_number")
    String accountNumber;

    @NotNull
    @Column(name = "company_id", nullable = false)
    Long companyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", updatable = false, insertable = false)
    Company company;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CompanyBank)) {
            return false;
        }
        return id != null && id.equals(((CompanyBank) o).id);
    }
}
