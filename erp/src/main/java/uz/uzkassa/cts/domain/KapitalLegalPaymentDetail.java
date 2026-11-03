package uz.uzkassa.cts.domain;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.type.SqlTypes;
import uz.uzkassa.cts.domain.base.AbstractAuditingEntity;
import uz.uzkassa.cts.enums.kapitalbank.KapitalLegalDirectionType;
import uz.uzkassa.cts.enums.kapitalbank.KapitalLegalDocumentState;
import uz.uzkassa.cts.enums.kapitalbank.KapitalLegalDocumentType;

import jakarta.persistence.*;

import java.io.Serial;

@Entity
@Table(name = "kapital_legal_payment_detail")
@Getter
@Setter
@SQLDelete(sql = "UPDATE kapital_legal_payment_detail SET deleted = 'true' WHERE id=?")
@SQLRestriction(value = "deleted = 'false'")
public class KapitalLegalPaymentDetail extends AbstractAuditingEntity {

    @Serial
    private static final long serialVersionUID = 2874037387L;


    /**
     * Счет получателя
     */
    @Column(name = "account_credit")
    private String accountCredit;

    /**
     * Счет отправителя
     */
    @Column(name = "account_debit")
    private String accountDebit;

    /**
     * Сумма операции
     */
    private String amount;

    /**
     * МФО
     */
    private String branch;

    /**
     * Дата документа
     */
    @Column(name = "document_date")
    private String documentDate;

    /**
     * 0 - переброска между своими счетами,
     * 1 - исходящий платеж,
     * 2 - входящий платеж
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "direction_type")
    private KapitalLegalDirectionType directionType;

    /**
     * Код типадокумента .
     * "01"-Платежное поручение
     * "02"-Платежное требованиe
     * "05"-Заявление на аккредитив
     * "06"-Мемориальный ордер
     * "08"-Исправительный мемориальный ор
     * "11"-Инкассовое поручение
     * "14"-Расчетные чеки коммерческих б
     * "21"-Платеж 24/7 через систему ANOR,
     * "35"-простое платежное поручение,
     * "97"-пополнение пластиковой карты,
     * "98"-платеж в казначейство,
     * "99"-платеж в бюджет
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "document_type")
    private KapitalLegalDocumentType documentType;

    /**
     * Код ошибки, возникшей при действии с документом в АБС
     */
    @Column(name = "abs_error")
    private String absError;

    /**
     * Описание ошибки АБС
     */
    @Column(name = "abs_error_msg_description")
    private String absErrorMsgDescription;

    /**
     * ID документа в АБС
     */
    @Column(name = "abs_id")
    private String absId;

    /**
     * ИНН получателя
     */
    @Column(name = "tin_credit")
    private String tinCredit;
    /**
     * ИНН отправителя
     */
    @Column(name = "tin_debit")
    private String tinDebit;
    //
    /**
     * МФО получателя
     */
    @Column(name = "mfo_credit")
    private String mfoCredit;

    /**
     * МФО отправителя
     */
    @Column(name = "mfo_debit")
    private String mfoDebit;

    /**
     * Наименование получателя
     */
    @Column(name = "name_credit")
    private String nameCredit;

    /**
     * Наименование отправителя
     */
    @Column(name = "name_debit")
    private String nameDebit;

    /**
     * Номер документа
     */
    @Column(name = "document_number")
    private String documentNumber;

    /**
     * Код назначения платежа
     */
    @Column(name = "purp_code")
    private String purpCode;

    /**
     * Назначение платежа
     */
    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
    private String purpose;
    /**
     * Cостояние платежа
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "state")
    private KapitalLegalDocumentState state;

    /**
     * Уникальный номер документа, присваиваемый клиентской стороной при создании платежного поручения
     */
    private String uniq;

    /**
     * Дата и время проведения платежа
     */
    private String time;

    @Column(name = "sent_to_billing", columnDefinition = "boolean default false")
    private boolean sentToBilling = false;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "kapital_legal_payment_id")
    private KapitalLegalPayment kapitalLegalPayment;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        KapitalLegalPaymentDetail that = (KapitalLegalPaymentDetail) o;

        return absId.equals(that.absId);
    }

    @Override
    public int hashCode() {
        return absId.hashCode();
    }

    @Override
    public String toString() {
        return "KapitalLegalPaymentDetail{" +
            "accountCredit='" + accountCredit + '\'' +
            ", accountDebit='" + accountDebit + '\'' +
            ", amount='" + amount + '\'' +
            ", branch='" + branch + '\'' +
            ", documentDate='" + documentDate + '\'' +
            ", directionType=" + directionType +
            ", documentType=" + documentType +
            ", absError='" + absError + '\'' +
            ", absErrorMsgDescription='" + absErrorMsgDescription + '\'' +
            ", absId='" + absId + '\'' +
            ", tinCredit='" + tinCredit + '\'' +
            ", tinDebit='" + tinDebit + '\'' +
            ", mfoCredit='" + mfoCredit + '\'' +
            ", mfoDebit='" + mfoDebit + '\'' +
            ", nameCredit='" + nameCredit + '\'' +
            ", nameDebit='" + nameDebit + '\'' +
            ", documentNumber='" + documentNumber + '\'' +
            ", purpCode='" + purpCode + '\'' +
            ", purpose='" + purpose + '\'' +
            ", state=" + state +
            ", uniq='" + uniq + '\'' +
            ", time='" + time + '\'' +
            ", sentToBilling=" + sentToBilling +
            ", kapitalLegalPayment=" + kapitalLegalPayment +
            '}';
    }
}
