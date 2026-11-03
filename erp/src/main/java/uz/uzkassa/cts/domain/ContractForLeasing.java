package uz.uzkassa.cts.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import uz.uzkassa.cts.domain.base.AbstractAuditingEntity;
import uz.uzkassa.cts.dto.contract.ContractShortDTO;
import uz.uzkassa.cts.enums.*;
import uz.uzkassa.cts.interfaces.Auditable;
import uz.uzkassa.cts.utils.NumberToWordUtil;

import java.io.Serial;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Table(name = "contract_for_leasing")
@Entity
@SQLDelete(sql = "UPDATE contract_for_leasing SET deleted = 'true' WHERE id=?")
@SQLRestriction(value = "deleted = 'false'")
public class ContractForLeasing extends AbstractAuditingEntity implements Auditable {

    @Serial
    private static final long serialVersionUID = 2396456142L;

    @NotNull
    @Enumerated(EnumType.STRING)
    private ContractStatus status;

    @Column(name = "branch_id")
    private Long branchId;

    @Column(name = "end_date")
    private LocalDate endDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id", insertable = false, updatable = false)
    private Branch branch;

    @Column(name = "model_price_history_id")
    private Long modelPriceHistoryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "model_price_history_id", insertable = false, updatable = false)
    private ModelPriceHistory modelPriceHistory;

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

    @Column(name = "total_product_count")
    private Integer totalProductCount;

    @Column(name = "total_price_with_vat")
    private BigDecimal totalPriceWithVat;

    @Column(name = "total_vat_price")
    private BigDecimal totalVatPrice;

    @Column(name = "total_price")
    private BigDecimal totalPrice;

    @Column(name = "price_for_1_pc")
    private BigDecimal priceFor1pc;

    @Column(name = "price_for_1_pc_with_vat")
    private BigDecimal priceFor1pcWithVat;

    @Column(name = "first_payment_1pc")
    private BigDecimal firstPayment1pc;

    @Column(name = "period")
    private Integer period;

    @Column(name = "file_uri")
    private String fileUri;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "content_type")
    private String contentType;

    @Column(name = "warranty_file_uri")
    private String warrantyFileUri;

    @Column(name = "warranty_file_name")
    private String warrantyFileName;

    @Column(name = "warranty_content_type")
    private String warrantyContentType;

    @Enumerated(EnumType.STRING)
    @Column(name = "company_type")
    private CompanyType companyType;

    @Enumerated(EnumType.STRING)
    @Column(name = "device_type")
    private DeviceType deviceType;

    @Column(name = "activation_code")
    private String activationCode;

    @Column(name = "activation_date")
    private LocalDateTime activationDate;

    @OneToMany(mappedBy = "contract", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ActOfTransmission> actOfTransmissions = new HashSet<>();

    public void setStatus(ContractStatus status) {
        addChangeLog("status", this.status != null ? this.status.getName() : null, status != null ? status.getName() : null);
        this.status = status;
    }

    public void addAct(ActOfTransmission device) {
        device.setContract(this);
        actOfTransmissions.add(device);
    }

    public ContractShortDTO toContractShortDTO() {
        return new ContractShortDTO(this.getId(), this.getNumber(), this.getCreatedDate());
    }

    public String getNumber() {
        return PrefixEnum.CL + "/" + this.getId();
    }

    public String decimalToWord(BigDecimal amount) {
        return NumberToWordUtil.digits2Text(amount);
    }

    @Override
    public Long getEntityId() {
        return id;
    }

    @Override
    public EntityTypeEnum getEntityType() {
        return EntityTypeEnum.CONTRACT_FOR_LEASING;
    }
}
