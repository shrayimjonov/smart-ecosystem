package uz.uzkassa.cts.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import uz.uzkassa.cts.domain.base.AbstractAuditingEntity;
import uz.uzkassa.cts.enums.ActDocFlowStatus;
import uz.uzkassa.cts.enums.ContractStatus;
import uz.uzkassa.cts.enums.PrefixEnum;

import java.io.Serial;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Table(name = "rent_device_act")
@Entity
@SQLDelete(sql = "UPDATE rent_device_act SET deleted = 'true' WHERE id=?")
@SQLRestriction("deleted = 'false'")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RentDeviceAct extends AbstractAuditingEntity {

    @Serial
    private static final long serialVersionUID = 6378110131L;

    @Column(name = "total_product_count")
    Integer totalProductCount;

    @Column(name = "total_price_with_vat")
    BigDecimal totalPriceWithVat;

    @Column(name = "price_for_1_pc")
    BigDecimal priceFor1pc;

    @Column(name = "price_for_1_pc_with_vat")
    BigDecimal priceFor1pcWithVat;

    @Column(name = "vat_1pc")
    BigDecimal vat1pc;

    @Column(name = "customer_id")
    Long customerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", insertable = false, updatable = false)
    Customer customer;

    @Column(name = "branch_id")
    Long branchId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id", insertable = false, updatable = false)
    Branch branch;

    @Column(name = "company_id")
    Long companyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", insertable = false, updatable = false)
    Company company;

    @OneToOne(mappedBy = "deviceAct", cascade = CascadeType.ALL, orphanRemoval = true)
    RentDeviceActDetail rentDeviceActDetail;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    ContractStatus status = ContractStatus.CREATED;

    @Enumerated(EnumType.STRING)
    @Column(name = "doc_flow_status")
    ActDocFlowStatus docFlowStatus;

    @Column(name = "doc_flow_reason")
    String docFlowReason;

    @Column(name = "accept_date")
    LocalDateTime acceptDate;

    @Column(name = "file_uri")
    String fileUri;

    @Column(name = "file_name")
    String fileName;

    @Column(name = "power_attorney_file_uri")
    String powerAttorneyFileUri;

    @Column(name = "power_attorney_file_name")
    String powerAttorneyFileName;

    @Column(name = "closed_date")
    LocalDateTime closedDate;

    public String getNumber() {
        return PrefixEnum.ADR + "/" + this.getId();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RentDeviceAct rentAct = (RentDeviceAct) o;

        return getId() != null ? getId().equals(rentAct.getId()) : rentAct.getId() == null;
    }
}
