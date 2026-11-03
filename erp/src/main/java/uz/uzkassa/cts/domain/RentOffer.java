package uz.uzkassa.cts.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import uz.uzkassa.cts.domain.base.AbstractAuditingEntity;
import uz.uzkassa.cts.enums.ContractStatus;
import uz.uzkassa.cts.enums.PrefixEnum;

import java.io.Serial;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Table(name = "rent_offer")
@Entity
@SQLDelete(sql = "UPDATE rent_offer SET deleted = 'true' WHERE id=?")
@SQLRestriction(value = "deleted = 'false'")
public class RentOffer extends AbstractAuditingEntity {

    @Serial
    private static final long serialVersionUID = 8598872992L;

    public String getNumber() {
        return PrefixEnum.CR + "/" + this.getId();
    }

    @Column(name = "branch_id")
    private Long branchId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id", insertable = false, updatable = false)
    private Branch branch;

    @NotNull
    @Enumerated(EnumType.STRING)
    private ContractStatus status;

    @Column(name = "company_id")
    private Long companyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", insertable = false, updatable = false)
    private Company company;

    @NotNull
    @Column(name = "customer_id")
    private Long customerId;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "customer_id", insertable = false, updatable = false)
    private Customer customer;

    @OneToMany(mappedBy = "offer", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<RentAct> rentActs = new HashSet<>();
}
