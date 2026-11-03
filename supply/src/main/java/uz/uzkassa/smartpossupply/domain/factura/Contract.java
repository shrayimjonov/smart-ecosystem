package uz.uzkassa.smartpossupply.domain.factura;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.SQLDelete;
import uz.uzkassa.smartpossupply.domain.base.AbstractAuditingEntity;
import uz.uzkassa.smartpossupply.domain.company.Company;
import uz.uzkassa.smartpossupply.dto.contract.ContractLookUpDTO;
import uz.uzkassa.smartpossupply.dto.contract.ContractLookUpOfflineDTO;
import uz.uzkassa.smartpossupply.enums.ContractStatus;
import uz.uzkassa.smartpossupply.enums.CurrencyType;
import uz.uzkassa.smartpossupply.enums.EntityTypeEnum;
import uz.uzkassa.smartpossupply.interfaces.Auditable;

import java.io.Serial;
import java.math.BigDecimal;
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
@SQLDelete(sql = "UPDATE contract SET deleted = true WHERE id=?")
public class Contract extends AbstractAuditingEntity implements Auditable {

    @Serial
    private static final long serialVersionUID = 666775454218L;

    @NotNull
    @Column(name = "contract_date", nullable = false)
    LocalDate contractDate;

    @Column(name = "expiry_date")
    LocalDate expiryDate;

    @NotNull
    @Column(name = "contract_number", nullable = false)
    String contractNumber;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    ContractStatus status = ContractStatus.ACCEPTED;

    @NotNull
    @Column(name = "customer_id", nullable = false)
    Long customerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", updatable = false, insertable = false)
    Company customer;

    @NotNull
    @Column(name = "supplier_id", nullable = false)
    Long supplierId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id", updatable = false, insertable = false)
    Company supplier;

    @NotNull
    @Column(name = "company_id", nullable = false)
    Long companyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", updatable = false, insertable = false)
    Company company;

    @Column(name = "currency")
    @Enumerated(EnumType.STRING)
    CurrencyType currency;

    @Column(name = "price", precision = 25, scale = 2)
    BigDecimal price;

    @Column(name = "sale_price", precision = 25, scale = 2)
    BigDecimal salePrice;

    @Column(name = "description", length = 1024)
    String description;

    @Column(name = "mobile_idd", unique = true)
    String mobileId;

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
    public String getEntityName() {
        return getName();
    }

    public ContractLookUpDTO toLookUpDTO() {
        return new ContractLookUpDTO(getId(), getContractDate(), getContractNumber());
    }

    public ContractLookUpOfflineDTO toLookUpOfflineDTO() {
        return new ContractLookUpOfflineDTO(getId(), getContractDate(), getContractNumber(), getCustomerId());
    }
}
