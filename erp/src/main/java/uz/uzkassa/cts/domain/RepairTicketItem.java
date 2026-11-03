package uz.uzkassa.cts.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.SQLDelete;
import uz.uzkassa.cts.domain.base.BaseEntity;
import uz.uzkassa.cts.domain.inventory.Product;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "repair_ticket_item",
        indexes = {
                @Index(columnList = "repair_ticket_id", name = "repair_ticket_item_repair_ticket_id_idx"),
                @Index(columnList = "product_id", name = "repair_ticket_item_product_id_idx"),
                @Index(columnList = "unit_id", name = "repair_ticket_item_unit_id_idx")
        })
@Entity
@SQLDelete(sql = "UPDATE repair_ticket_item SET deleted='true' WHERE id=?")
public class RepairTicketItem extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 838009812365837545L;

    @NotNull
    @Column(name = "repair_ticket_id", nullable = false)
    Long repairTicketId;

    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "repair_ticket_id", updatable = false, insertable = false)
    RepairTicket repairTicket;

    @NotNull
    @Column(name = "product_id", nullable = false)
    Long productId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", updatable = false, insertable = false)
    Product product;

    @NotNull
    @Column(name = "unit_id", nullable = false)
    Long unitId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unit_id", updatable = false, insertable = false)
    Reference unit;

    @Column(name = "qty", scale = 2, precision = 25)
    BigDecimal qty;

    @Column(name = "price", scale = 2, precision = 25)
    BigDecimal price;

    @Column(name = "total_amount", scale = 2, precision = 25)
    BigDecimal totalAmount;

    @Column(name = "vat_rate", scale = 2, precision = 25)
    BigDecimal vatRate;

    @Column(name = "vat_price", scale = 2, precision = 25)
    BigDecimal vatPrice;

    @Column(name = "total_vat_amount", scale = 2, precision = 25)
    BigDecimal totalVatAmount;

}
