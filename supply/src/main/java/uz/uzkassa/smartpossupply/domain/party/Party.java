package uz.uzkassa.smartpossupply.domain.party;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.SQLDelete;
import uz.uzkassa.smartpossupply.domain.Branch;
import uz.uzkassa.smartpossupply.domain.base.AbstractAuditingEntity;
import uz.uzkassa.smartpossupply.domain.company.Company;
import uz.uzkassa.smartpossupply.domain.stock.Stock;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.enums.EntityTypeEnum;
import uz.uzkassa.smartpossupply.enums.PartyStatus;
import uz.uzkassa.smartpossupply.interfaces.Auditable;

import java.io.Serial;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "party", indexes = {
        @Index(columnList = "party_number", name = "party_number_idx"),
        @Index(columnList = "status", name = "party_status_idx"),
        @Index(columnList = "company_id", name = "party_company_id_idx")
})
@SQLDelete(sql = "UPDATE party SET deleted = true WHERE id=?")
public class Party extends AbstractAuditingEntity implements Auditable {

    @Serial
    private static final long serialVersionUID = 9457654844L;

    @Column(name = "party_number")
    String partyNumber;

    @Column(name = "increment_number")
    Long incrementNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    PartyStatus status;

    @Column(name = "company_id")
    Long companyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", updatable = false, insertable = false)
    Company company;

    @Column(name = "stock_id")
    Long stockId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_id", updatable = false, insertable = false)
    Stock stock;

    @Column(name = "branch_id")
    Long branchId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id", updatable = false, insertable = false)
    Branch branch;

    @OneToMany(mappedBy = "party", cascade = CascadeType.ALL, orphanRemoval = true)
    Set<PartyProduct> products = new LinkedHashSet<>();

    public void addProduct(PartyProduct partyProduct) {
        partyProduct.setParty(this);
        this.products.add(partyProduct);
    }

    @Override
    public CommonDTO toCommonDTO() {
        return new CommonDTO(this.getId(), this.getPartyNumber());
    }

    @Override
    public String getName() {
        return this.getPartyNumber();
    }

    @Override
    public Long getEntityId() {
        return this.id;
    }

    @Override
    public EntityTypeEnum getEntityType() {
        return EntityTypeEnum.PARTY;
    }

    @Override
    public String getEntityName() {
        return getName();
    }

    public void setStatus(PartyStatus status) {
        addLogHistory("status", this.status != null ? this.status.getName() : null, status != null ? status.getName() : null);
        this.status = status;
    }
}
