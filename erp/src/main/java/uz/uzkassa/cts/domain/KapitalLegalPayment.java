package uz.uzkassa.cts.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import uz.uzkassa.cts.domain.base.AbstractAuditingEntity;
import uz.uzkassa.cts.enums.kapitalbank.KapitalLegalDayStatus;

import java.io.Serial;
import java.math.BigInteger;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "kapital_legal_payment")
@Getter
@Setter
@SQLDelete(sql = "UPDATE kapital_legal_payment SET deleted = 'true' WHERE id=?")
@SQLRestriction(value = "deleted = 'false'")
public class KapitalLegalPayment extends AbstractAuditingEntity {

    @Serial
    private static final long serialVersionUID = 7047248680L;

    @Enumerated(EnumType.STRING)
    @Column(name = "day_status")
    private KapitalLegalDayStatus dayStatus;
    @Column(name = "oper_day", unique = true)
    private String operDay;
    @Column(name = "saldo_in")
    private BigInteger saldoIn;
    @Column(name = "saldo_out")
    private BigInteger saldoOut;
    @Column(name = "total_credit")
    private BigInteger totalCredit;
    @Column(name = "totanal_debit")
    private BigInteger totalDebit;
    @Column(name = "error_code")
    private Long errorCode;
    @Column(name = "error_message")
    private String errorMessage;
    @Column(name = "response_id")
    private Long responseId;

    @OneToMany(mappedBy = "kapitalLegalPayment", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<KapitalLegalPaymentDetail> kapitalLegalPaymentDetails = new HashSet<>();

}
