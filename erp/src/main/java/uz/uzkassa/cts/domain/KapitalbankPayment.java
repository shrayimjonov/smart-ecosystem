package uz.uzkassa.cts.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import uz.uzkassa.cts.domain.base.AbstractAuditingEntity;
import uz.uzkassa.cts.enums.KapitalbankPaymentStatus;

import java.io.Serial;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "kapitalbank_payments")
@Getter
@Setter
@SQLDelete(sql = "UPDATE kapitalbank_payments SET deleted = 'true' WHERE id=?")
@SQLRestriction(value = "deleted = 'false'")
public class KapitalbankPayment extends AbstractAuditingEntity {

    @Serial
    private static final long serialVersionUID = 2558339468L;

    private String tin;
    private String deviceSerialNumber;
    private String companyName;
    private String accountNumber;
    private String bankCode;
    private String bankName;
    private String pan;
    private String expiry;
    private BigDecimal amount;
    private Integer transactionId;
    private String maskedPhoneNumber;
    private String errorMessage;
    private String status;
    private LocalDateTime timestamp;

    private String payData;
    private String payErrorMessage;
    private String payStatus;
    private LocalDateTime payTimestamp;
    private KapitalbankPaymentStatus kapitalbankPaymentStatus;

    private String checkData;
    private String checkErrorMessage;
    private String checkStatus;
    private LocalDateTime checkTimestamp;

    private Long bankClientId;

    public String getPaymentType(){
        return "KAPITAL";
    }

    @Override
    public String toString() {
        return "KapitalbankPayment{" +
                "tin='" + tin + '\'' +
                ", deviceSerialNumber='" + deviceSerialNumber + '\'' +
                ", companyName='" + companyName + '\'' +
                ", accountNumber='" + accountNumber + '\'' +
                ", bankCode='" + bankCode + '\'' +
                ", bankName='" + bankName + '\'' +
                ", pan='" + pan + '\'' +
                ", expiry='" + expiry + '\'' +
                ", amount=" + amount +
                ", transactionId=" + transactionId +
                ", maskedPhoneNumber='" + maskedPhoneNumber + '\'' +
                ", errorMessage='" + errorMessage + '\'' +
                ", status='" + status + '\'' +
                ", timestamp=" + timestamp +
                ", payData='" + payData + '\'' +
                ", payErrorMessage='" + payErrorMessage + '\'' +
                ", payStatus='" + payStatus + '\'' +
                ", payTimestamp=" + payTimestamp +
                ", kapitalbankPaymentStatus=" + kapitalbankPaymentStatus +
                ", checkData='" + checkData + '\'' +
                ", checkErrorMessage='" + checkErrorMessage + '\'' +
                ", checkStatus='" + checkStatus + '\'' +
                ", checkTimestamp=" + checkTimestamp +
                '}';
    }
}
