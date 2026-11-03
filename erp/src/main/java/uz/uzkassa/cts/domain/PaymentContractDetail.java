package uz.uzkassa.cts.domain;


import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import uz.uzkassa.cts.domain.base.AbstractAuditingEntity;

import java.io.Serial;
import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(name = "payment_contract_detail")
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@SQLDelete(sql = "UPDATE payment_contract_detail SET deleted = 'true' WHERE id=?")
@SQLRestriction(value = "deleted = 'false'")
public class PaymentContractDetail extends AbstractAuditingEntity {

    @Serial
    private static final long serialVersionUID = 3171008012L;

    @Column(name = "payment_contract_id")
    Long paymentContractId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_contract_id", insertable = false, updatable = false)
    PaymentContract paymentContract;

    @Column(name = "device_id")
    Long deviceId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_id", updatable = false, insertable = false)
    Device device;

    @OneToOne(mappedBy = "contractDetail", cascade = CascadeType.ALL, orphanRemoval = true)
    PaymentAct paymentAct;

    @Column(name = "total_product_count", columnDefinition = "integer default 1")
    Integer totalProductCount;

    @Column(name = "total_price_with_vat")
    BigDecimal totalPriceWithVat;

    @Column(name = "price_for_1_pc")
    BigDecimal priceFor1pc;

    @Column(name = "price_for_1_pc_with_vat")
    BigDecimal priceFor1pcWithVat;

    @Column(name = "vat_1pc")
    BigDecimal vat1pc;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PaymentContractDetail detail = (PaymentContractDetail) o;
        return Objects.equals(getId(), detail.getId());
    }
}
