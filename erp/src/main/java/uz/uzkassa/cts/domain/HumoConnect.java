package uz.uzkassa.cts.domain;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.SQLDelete;
import uz.uzkassa.cts.domain.address.BaseAddress;
import uz.uzkassa.cts.domain.base.AbstractAuditingEntity;
import uz.uzkassa.cts.enums.*;

import java.io.Serial;
import java.time.LocalDateTime;

@Getter
@Setter
@Table(name = "humo_connect")
@Entity
@SQLDelete(sql = "UPDATE humo_connect SET deleted = 'true' WHERE id=?")
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class HumoConnect extends AbstractAuditingEntity {

    @Serial
    private static final long serialVersionUID = 1139201773L;

    @Column(name = "case_id")
    Long caseId;

    @Column(name = "device_serial_number")
    String deviceSerialNumber;

    @Column(name = "phone")
    String phone;

    @Column(name = "account_number")
    String accountNumber;

    @Column(name = "mfo")
    String mfo;

    @Column(name = "status")
    HumoConnectStatus status;

    @Column(name = "operator_status")
    HumoConnectOperatorStatus operatorStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "business_type")
    BusinessType businessType;

    @Column(name = "app_type")
    @Enumerated(EnumType.STRING)
    CustomerAppType appType;

    @Enumerated(EnumType.STRING)
    @Column(name = "bank_type")
    BankType bankType;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_type")
    PaymentType paymentType;

    @Embedded
    BaseAddress address;

    @Enumerated(EnumType.STRING)
    @Column(name = "doc_flow_status")
    DocFlowStatus docFlowStatus;

    @Column(name = "file_url")
    String fileUrl;

    @Column(name = "file_name")
    String fileName;

    @Column(name = "last_receipt_date")
    LocalDateTime lastReceiptDate;

    @Column(name = "last_receipt_fiscal_url")
    String lastReceiptFiscalUrl;

    @Column(name = "first_receipt_date")
    LocalDateTime firstReceiptDate;

    @Column(name = "first_receipt_fiscal_url")
    String firstReceiptFiscalUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "case_type")
    CaseType caseType;

    @Column(name = "customer_id")
    Long customerId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", insertable = false, updatable = false)
    Customer customer;

    @Column(name = "customer_branch_id")
    Long customerBranchId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_branch_id", insertable = false, updatable = false)
    CustomerBranch customerBranch;

    @Column(name = "case_creator_id")
    Long caseCreatorId;
    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "case_creator_id", updatable = false, insertable = false)
    User caseCreator;
}
