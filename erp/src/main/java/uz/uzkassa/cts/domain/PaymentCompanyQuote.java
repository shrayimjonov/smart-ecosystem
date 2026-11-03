package uz.uzkassa.cts.domain;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import uz.uzkassa.cts.domain.base.AbstractAuditingEntity;

import java.io.Serial;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "payment_company_quote")
@Getter
@Setter
@SQLDelete(sql = "UPDATE payment_company_quote SET deleted = 'true' WHERE id=?")
@SQLRestriction(value = "deleted = 'false'")
public class PaymentCompanyQuote extends AbstractAuditingEntity {

    @Serial
    private static final long serialVersionUID = 3010057111L;

    @Column(name = "company_id", nullable = false)
    private Long companyId;

    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", updatable = false, insertable = false)
    private Company company;

    @Column(name = "qty", nullable = false)
    private Integer qty;

    @Column(name = "model_id", nullable = false)
    private Long modelId;

    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "model_id", updatable = false, insertable = false)
    private DeviceModel model;

    @OneToMany(mappedBy = "paymentCompanyQuote", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private Set<PaymentCompanyContract> paymentCompanyContracts = new HashSet<>();
}
