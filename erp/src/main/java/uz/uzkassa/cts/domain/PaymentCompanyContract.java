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
import uz.uzkassa.cts.enums.ContractStatus;
import uz.uzkassa.cts.enums.PrefixEnum;

import java.io.Serial;

@Entity
@Table(name = "payment_company_contract")
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@SQLDelete(sql = "UPDATE payment_company_contract SET deleted = 'true' WHERE id=?")
@SQLRestriction(value = "deleted = 'false'")
public class PaymentCompanyContract extends AbstractAuditingEntity {

    @Serial
    private static final long serialVersionUID = 4972335460L;

    @Column(name = "device_id", nullable = false)
    private Long deviceId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_id", insertable = false, updatable = false)
    private Device device;

    @Column(name = "payment_company_quote_id", nullable = false)
    private Long paymentCompanyQuoteId;

    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_company_quote_id", updatable = false, insertable = false)
    private PaymentCompanyQuote paymentCompanyQuote;

    @Column(name = "file_uri")
    private String fileUri;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private ContractStatus status;

    @NotNull
    @Column(name = "company_id", nullable = false)
    Long companyId;

    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", updatable = false, insertable = false)
    Company company;

    public String getNumber() {
        return PrefixEnum.CCP + "/" + this.getId();
    }
}
