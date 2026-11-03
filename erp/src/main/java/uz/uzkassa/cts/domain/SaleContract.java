package uz.uzkassa.cts.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import uz.uzkassa.cts.domain.base.AbstractAuditingEntity;
import uz.uzkassa.cts.dto.contract.ContractShortDTO;
import uz.uzkassa.cts.enums.ContractStatus;
import uz.uzkassa.cts.enums.PrefixEnum;
import uz.uzkassa.cts.utils.NumberToWordUtil;

import java.io.Serial;
import java.math.BigDecimal;

@Entity
@Table(name = "sale_contract")
@Getter
@Setter
@SQLDelete(sql = "UPDATE sale_contract SET deleted = 'true' WHERE id=?")
@SQLRestriction(value = "deleted = 'false'")
public class SaleContract extends AbstractAuditingEntity {

    @Serial
    private static final long serialVersionUID = 120046971L;

    @OneToOne(mappedBy = "saleContract", cascade = CascadeType.ALL, orphanRemoval = true)
    private SaleAct saleAct;

    @OneToOne(mappedBy = "saleContract", cascade = CascadeType.ALL, orphanRemoval = true)
    private SaleInvoice saleInvoice;

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

    @Column(name = "file_uri")
    private String fileUri;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "content_type")
    private String contentType;

    public ContractShortDTO toContractShortDTO() {
        return new ContractShortDTO(this.getId(), this.getNumber(), this.getCreatedDate());
    }

    public String getNumber() {
        return PrefixEnum.CAS + "/" + this.getId();
    }

    public String decimalToWord(BigDecimal amount) {
        return NumberToWordUtil.digits2Text(amount);
    }
}
