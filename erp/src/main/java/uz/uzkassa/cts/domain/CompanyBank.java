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

import java.io.Serial;

@Entity
@Table(name = "company_bank", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"company_id", "bank_id"})
}, indexes = {
        @Index(columnList = "company_id", name = "company_company_id_idx"),
        @Index(columnList = "bank_id", name = "company_bank_id_idx"),
        @Index(columnList = "account_number", name = "company_bank_account_number_idx")
})
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@SQLDelete(sql = "UPDATE company_bank SET deleted = 'true' WHERE id=?")
@SQLRestriction(value = "deleted = 'false'")
public class CompanyBank extends AbstractAuditingEntity {

    @Serial
    private static final long serialVersionUID = 428676173L;

    @NotNull
    @Column(name = "company_id", nullable = false)
    Long companyId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", updatable = false, insertable = false)
    Company company;

    @NotNull
    @Column(name = "bank_id", nullable = false)
    Long bankId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bank_id", updatable = false, insertable = false)
    Bank bank;

    @NotNull
    @Column(name = "account_number", nullable = false)
    String accountNumber;

    @Column(name = "oked")
    String oked;

    @Column(name = "is_default", columnDefinition = "boolean default false")
    boolean isDefault;

}
