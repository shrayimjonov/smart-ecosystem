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
import uz.uzkassa.cts.enums.ActDocFlowStatus;
import uz.uzkassa.cts.enums.ContractStatus;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.enums.PrefixEnum;
import uz.uzkassa.cts.enums.apay.APayStatus;
import uz.uzkassa.cts.interfaces.Auditable;
import uz.uzkassa.cts.utils.NumberToWordUtil;

import java.io.Serial;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Table(name = "payment_act")
@Entity
@SQLDelete(sql = "UPDATE payment_act SET deleted = 'true' WHERE id=?")
@SQLRestriction(value = "deleted = 'false'")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaymentAct extends AbstractAuditingEntity implements Auditable {

    @Serial
    private static final long serialVersionUID = 3924398947L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contract_id")
    PaymentContract contract;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contract_detail_id")
    PaymentContractDetail contractDetail;

    @NotNull
    @Column(name = "device_id")
    Long deviceId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_id", updatable = false, insertable = false)
    Device device;

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

    @Column(name = "payment_status")
    @Enumerated(EnumType.STRING)
    APayStatus paymentStatus = APayStatus.PENDING;

    public String getNumber() {
        return PrefixEnum.AP + "/" + this.getId();
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
        return EntityTypeEnum.PAYMENT_ACT;
    }
}
