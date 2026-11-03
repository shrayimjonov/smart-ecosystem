package uz.uzkassa.cts.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import uz.uzkassa.cts.domain.base.AbstractAuditingEntity;

import java.io.Serial;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "ledger")
@Getter
@Setter
@SQLDelete(sql = "UPDATE Ledger SET deleted = 'true' WHERE id=?")
@SQLRestriction(value = "deleted = 'false'")
public class Ledger extends AbstractAuditingEntity {

    @Serial
    private static final long serialVersionUID = 6414742116L;

    @NotNull
    @Column(name = "reference_to")
    private String referenceTo;

    @NotNull
    @Column(name = "description", length = 1500)
    private String description;


    @Column(name = "contract_number")
    private String contractNumber;

    @Column(name = "specification_number")
    private String specificationNumber;


    @OneToMany(mappedBy = "ledger", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<LedgerDetail> details = new HashSet<>();

    public void addLedgerDetail(LedgerDetail detail) {
        detail.setLedger(this);
        this.details.add(detail);
    }
}
