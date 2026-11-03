package uz.uzkassa.smartpossupply.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.domain.base.SimpleEntity;
import uz.uzkassa.smartpossupply.domain.company.Company;
import uz.uzkassa.smartpossupply.enums.AccountType;

import java.io.Serial;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "account",
        indexes = {
                @Index(columnList = "account_type", name = "account_account_type_idx"),
                @Index(columnList = "company_id", name = "account_company_id_idx"),
        })
public class Account extends SimpleEntity {

    @Serial
    private static final long serialVersionUID = 8593845333L;

    @Enumerated(EnumType.STRING)
    @Column(name = "account_type")
    AccountType accountType;

    @Column(name = "company_id")
    Long companyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", insertable = false, updatable = false)
    Company company;

    @Override
    public String getName() {
        return accountType.getName();
    }

}
