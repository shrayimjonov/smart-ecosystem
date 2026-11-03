package uz.uzkassa.horeca.domain.factura;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.SQLDelete;
import uz.uzkassa.horeca.domain.base.AbstractAuditingEntity;
import uz.uzkassa.horeca.domain.company.Company;
import uz.uzkassa.horeca.dto.contract.ContractLookUpDTO;
import uz.uzkassa.horeca.enums.ContractStatus;
import uz.uzkassa.horeca.enums.EntityTypeEnum;

import java.io.Serializable;
import java.time.LocalDate;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(
    name = "contract",
    indexes = {
        @Index(columnList = "customer_id", name = "contract_customer_id_idx"),
        @Index(columnList = "supplier_id", name = "contract_supplier_id_idx"),
        @Index(columnList = "company_id", name = "contract_company_id_idx"),
        @Index(columnList = "contract_number", name = "contract_number_idx"),
        @Index(columnList = "contract_date", name = "contract_date_idx"),
    }
)
@SQLDelete(sql = "UPDATE contract SET deleted = 'true' WHERE id=?")
public class Contract extends AbstractAuditingEntity implements Serializable {

    static final long serialVersionUID = 666775454218L;

    @Column(name = "contract_date")
    LocalDate contractDate;

    @Column(name = "contract_number")
    String contractNumber;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    ContractStatus status = ContractStatus.ACCEPTED;

    @Column(name = "customer_id")
    Long customerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", updatable = false, insertable = false)
    Company customer;

    @Column(name = "supplier_id")
    Long supplierId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id", updatable = false, insertable = false)
    Company supplier;

    @Column(name = "company_id")
    Long companyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", updatable = false, insertable = false)
    Company company;

    @Column(name = "description", length = 1024)
    String description;

    @Override
    public String getName() {
        return this.getContractNumber();
    }

    @Override
    public Long getEntityId() {
        return this.id;
    }

    @Override
    public EntityTypeEnum getEntityType() {
        return EntityTypeEnum.CONTRACT;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Contract)) {
            return false;
        }
        return id != null && id.equals(((Contract) o).id);
    }

    public ContractLookUpDTO toLookUpDTO() {
        return new ContractLookUpDTO(getId(), getContractDate(), getContractNumber());
    }
}
