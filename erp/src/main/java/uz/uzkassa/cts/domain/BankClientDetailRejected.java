package uz.uzkassa.cts.domain;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.type.SqlTypes;
import uz.uzkassa.cts.domain.base.AbstractAuditingEntity;
import uz.uzkassa.cts.enums.BankClientRejectedStatus;
import uz.uzkassa.cts.enums.ServiceType;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.io.Serial;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "bank_client_detail_rejected", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"document_no", "payer_identity_number", "payment_date", "amount"})
})
@Getter
@Setter
@SQLDelete(sql = "UPDATE bank_client_detail_rejected SET deleted = 'true' WHERE id=?")
@SQLRestriction(value = "deleted = 'false'")
public class BankClientDetailRejected extends AbstractAuditingEntity {

    @Serial
    private static final long serialVersionUID = 3451266401L;

    @Column(name = "document_no")
    private String documentNo;

    @Column(name = "journal_name")
    private String journalName;

    @Column(name = "transaction_number", unique = true)
    private String transactionNumber;

    @Size(min = 20, max = 20)
    @Column(name = "payer_account_number")
    private String payerAccountNumber;

    @NotNull
    @NotEmpty
    @Size(min = 9, max = 14)
    @Column(name = "payer_identity_number")
    private String payerIdentityNumber;

    @Column(name = "payer_name")
    private String payerName;

    @Column(name = "payer_bank_code")
    @Size(min = 5, max = 5)
    private String payerBankCode;

    @Column(name = "payer_bank_name")
    private String payerBankName;

    @Column(name = "receiver_account_number")
    @Size(min = 20, max = 20)
    private String receiverAccountNumber;

    @Column(name = "receiver_identity_number")
    @Size(min = 9, max = 14)
    private String receiverIdentityNumber;

    @Column(name = "receiver_name")
    private String receiverName;

    @Column(name = "receiver_bank_code")
    @Size(min = 5, max = 5)
    private String receiverBankCode;

    @Column(name = "receiver_bank_name")
    private String receiverBankName;

    @Column(name = "amount")
    private BigDecimal amount;

    @Column(name = "payment_date")
    private LocalDate paymentDate;

    @Column(name = "description")
    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
    private String description;

    @Column(name = "contract_number")
    private String contractNumber;

    @Column(name = "specification_number")
    private String specificationNumber;

    @Column(name = "approved")
    private boolean approved = false;

    @Column(name = "bank_client_journal_id")
    private Long bankClientJournalId;

    @Column(name = "service_type")
    @Enumerated(value = EnumType.STRING)
    private ServiceType serviceType;

    @Column(name = "status")
    private BankClientRejectedStatus status;

    @Size(min = 9, max = 14)
    @Column(name = "old_tin")
    private String oldTin;

}
