package uz.uzkassa.silen.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import uz.uzkassa.silen.config.DbConstants;
import uz.uzkassa.silen.dto.warehouse.PurchaseOrderProductDetailDTO;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * A Order.
 */
@Getter
@Setter
@Entity
@Table(schema = DbConstants.CURRENT_SCHEMA, name = "purchase_order_product",
        indexes = {
                @Index(name = "purchase_order_product_order_id_product_id_idx", columnList = "order_id,product_id"),
        },
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"product_id", "order_id"})
        })
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class PurchaseOrderProduct implements Serializable {

    @Serial
    private static final long serialVersionUID = 11413341234123435L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_id")
    private Long orderId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", insertable = false, updatable = false)
    private PurchaseOrder order;

    @Column(name = "product_id")
    private String productId;

    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", updatable = false, insertable = false)
    private Product product;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "serial_id")
    private String serialId;

    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "serial_id", updatable = false, insertable = false)
    private SerialNumber serialNumber;

    @Column(name = "vat_rate_id")
    private Long vatRateId;

    @JoinColumn(name = "vat_rate_id", updatable = false, insertable = false)
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    private VatRate vatRate;

    @Column(name = "vat_sum")
    private BigDecimal vatSum;

    @Column(name = "profit_rate")
    private BigDecimal profitRate;

    @Column(name = "without_vat")
    private BigDecimal withoutVat;

    @Column(name = "base_summa")
    private BigDecimal baseSumma;

    @Column(name = "summa")
    private BigDecimal summa;

    public PurchaseOrderProductDetailDTO toDto() {
        PurchaseOrderProductDetailDTO dto = new PurchaseOrderProductDetailDTO();
        dto.setId(getId());
        dto.setCount(BigDecimal.valueOf(getQuantity()));
        if (getSerialNumber() != null) {
            dto.setSerial(getSerialNumber().toSelectItem());
        }
        if (getProduct() != null) {
            dto.setProduct(getProduct().toSelectItem());
            dto.setProfitRate(getProduct().getProfitRate());
        }
        if (getVatRate() != null) {
            dto.setVatRate(getVatRate().toDTO());
        }
        dto.setVatSum(getVatSum());

        dto.setBaseSumma(getBaseSumma());
        dto.setWithoutVat(getWithoutVat());
        dto.setSumma(getSumma());

        return dto;
    }
}
