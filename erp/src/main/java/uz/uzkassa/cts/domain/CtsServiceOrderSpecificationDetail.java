package uz.uzkassa.cts.domain;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import jakarta.persistence.*;
import uz.uzkassa.cts.domain.base.AbstractAuditingEntity;

import java.io.Serial;
import java.math.BigDecimal;

@Getter
@Setter
@Table(name = "cts_service_order_specification_detail")
@Entity
@SQLDelete(sql = "UPDATE cts_service_order_specification_detail SET deleted = 'true' WHERE id=?")
@SQLRestriction(value = "deleted = 'false'")
public class CtsServiceOrderSpecificationDetail extends AbstractAuditingEntity {

    @Serial
    private static final long serialVersionUID = 1923218823L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_order_specification_id")
    private CtsServiceOrderSpecification serviceOrderSpecification;

    @Column(name = "tariff_id")
    private Long tariffId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tariff_id", insertable = false, updatable = false)
    private Tariff tariff;

    @Column(name = "tariff_name")
    private String tariffName;

    @Column(name = "device_count")
    private Integer productCount;
    // Price without VAT
    @Column(name = "price_1_pc")
    private BigDecimal price1pc;
    // Price with VAT
    @Column(name = "sell_price_1_pc")
    private BigDecimal sellPrice1pc;
    // VAT price for 1 pc
    @Column(name = "vat_price_1_pc")
    private BigDecimal vatPrice1pc;
    // vatPrice1pc * deviceCount
    @Column(name = "overall_vat_price")
    private BigDecimal overallVatPrice;
    // rice1pc * deviceCount
    @Column(name = "overall_price")
    private BigDecimal overallPrice;
    // sellPrice1pc * deviceCount
    @Column(name = "overall_sell_price")
    private BigDecimal overallSellPrice;
    // vatRate gets from vat table
    @Column(name = "vat_rate")
    private BigDecimal vatRate;

}
