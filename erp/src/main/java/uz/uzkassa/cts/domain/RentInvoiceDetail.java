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

@Entity
@Table(name = "rent_invoice_detail")
@Getter
@Setter
@SQLDelete(sql = "UPDATE rent_invoice_detail SET deleted = 'true' WHERE id=?")
@SQLRestriction(value = "deleted = 'false'")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RentInvoiceDetail extends AbstractAuditingEntity {

    @Serial
    private static final long serialVersionUID = 4610863403L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id")
    private RentInvoice invoice;

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

    @Column(name = "device_serial_number")
    private String deviceSerialNumber;

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

    @Column(name = "Origin")
    private Integer origin;
}
