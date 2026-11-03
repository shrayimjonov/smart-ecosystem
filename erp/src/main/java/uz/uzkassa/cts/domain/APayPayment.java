package uz.uzkassa.cts.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import uz.uzkassa.cts.domain.base.AbstractAuditingEntity;
import uz.uzkassa.cts.enums.apay.APayPaymentStatus;
import uz.uzkassa.cts.enums.apay.APayStatus;

import java.io.Serial;
import java.math.BigDecimal;

@Entity
@Table(name = "a_pay_payments")
@Getter
@Setter
@SQLDelete(sql = "UPDATE a_pay_payments SET deleted = 'true' WHERE id=?")
@SQLRestriction(value = "deleted = 'false'")
public class APayPayment extends AbstractAuditingEntity {

    @Serial
    private static final long serialVersionUID = 970468767L;

    private String tin;
    @Column(name = "device_serial_number")
    private String deviceSerialNumber;
    @Column(name = "company_name")
    private String companyName;
    @Column(name = "account_number")
    private String accountNumber;
    @Column(name = "bank_code")
    private String bankCode;
    @Column(name = "bank_name")
    private String bankName;
    @Column(name = "masked_phone_number")
    private String maskedPhoneNumber;
    private String pan;
    private String expiry;
    private BigDecimal amount;
    private String billId;
    @Column(name = "pay_status")
    @Enumerated(EnumType.STRING)
    private APayStatus payStatus;
    @Column(name = "check_status")
    @Enumerated(EnumType.STRING)
    private APayStatus checkStatus;
    @Column(name = "check_message")
    private String checkMessage;
    @Column(name = "a_pay_payment_status")
    @Enumerated(EnumType.STRING)
    private APayPaymentStatus aPayPaymentStatus;

    public String getPaymentType() {
        return "APAY";
    }

    @Override
    public String toString() {
        return "APayPayment{" +
                "tin='" + tin + '\'' +
                ", deviceSerialNumber='" + deviceSerialNumber + '\'' +
                ", companyName='" + companyName + '\'' +
                ", accountNumber='" + accountNumber + '\'' +
                ", bankCode='" + bankCode + '\'' +
                ", bankName='" + bankName + '\'' +
                ", maskedPhoneNumber='" + maskedPhoneNumber + '\'' +
                ", pan='" + pan + '\'' +
                ", expiry='" + expiry + '\'' +
                ", amount=" + amount +
                ", billId='" + billId + '\'' +
                ", payStatus=" + payStatus +
                ", checkStatus=" + checkStatus +
                ", checkMessage='" + checkMessage + '\'' +
                ", aPayPaymentStatus=" + aPayPaymentStatus +
                '}';
    }
}
