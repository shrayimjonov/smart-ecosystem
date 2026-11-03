package uz.uzkassa.cts.domain;


import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import uz.uzkassa.cts.converter.OriginConverter;
import uz.uzkassa.cts.domain.base.AbstractAuditingEntity;
import uz.uzkassa.cts.enums.Origin;

import java.io.Serial;
import java.math.BigDecimal;

@Entity
@Table(name = "sale_invoice_detail")
@Getter
@Setter
@SQLDelete(sql = "UPDATE sale_invoice_detail SET deleted = 'true' WHERE id=?")
@SQLRestriction(value = "deleted = 'false'")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SaleInvoiceDetail extends AbstractAuditingEntity {

    @Serial
    private static final long serialVersionUID = 3724649031L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sale_invoice_id")
    SaleInvoice saleInvoice;

    @Column(name = "total_product_count")
    private Integer totalProductCount;

    @Column(name = "price_for_1_pc")
    private BigDecimal priceFor1pc;

    @Column(name = "vat_for_1_pc")
    private BigDecimal vatFor1pc;

    @Column(name = "total_sell_price")
    private BigDecimal totalSellPrice;

    @Column(name = "total_vat_price")
    private BigDecimal totalVatPrice;

    @Column(name = "accessory_name")
    private String accessoryName;

    @Column(name = "service_name")
    private String serviceName;

    @Column(name = "catalog_code")
    String catalogCode;

    @Column(name = "catalog_name")
    String catalogName;

    @Column(name = "package_name")
    String packageName;

    @Column(name = "package_code")
    String packageCode;

    @Column(name = "code")
    String code;

    @Convert(converter = OriginConverter.class)
    @Column(name = "Origin", columnDefinition = "int default 2")
    private Origin origin;
}
