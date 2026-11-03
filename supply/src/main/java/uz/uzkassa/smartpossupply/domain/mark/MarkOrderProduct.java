package uz.uzkassa.smartpossupply.domain.mark;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.SQLDelete;
import uz.uzkassa.smartpossupply.domain.product.Product;
import uz.uzkassa.smartpossupply.domain.base.BaseEntity;
import uz.uzkassa.smartpossupply.enums.BufferStatus;
import uz.uzkassa.smartpossupply.enums.CisType;
import uz.uzkassa.smartpossupply.enums.MarkOrderRateType;
import uz.uzkassa.smartpossupply.enums.SerialNumberType;

import java.io.Serial;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "mark_order_product")
@SQLDelete(sql = "UPDATE mark_order_product SET deleted = true WHERE id=?")
public class MarkOrderProduct extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 681881L;

    @Column(name = "product_id")
    Long productId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", updatable = false, insertable = false)
    Product product;

    @Column(name = "qty")
    Integer qty;

    @Column(columnDefinition = "int4 default 0")
    Integer received = 0;

    @Column(columnDefinition = "int4 default 0")
    Integer remaining = 0;

    @Enumerated(EnumType.STRING)
    @Column(name = "buffer_status")
    BufferStatus bufferStatus;

    @Column(name = "last_block_id")
    String lastBlockId;

    @Enumerated(EnumType.STRING)
    @Column(name = "serial_number_type")
    SerialNumberType serialNumberType = SerialNumberType.OPERATOR;

    @Enumerated(EnumType.STRING)
    @Column(name = "cis_type")
    CisType cisType = CisType.UNIT;

    @Enumerated(EnumType.STRING)
    @Column(name = "rate_type")
    MarkOrderRateType rateType = MarkOrderRateType.STANDARD;

    @Column(name = "mark_order_id")
    Long markOrderId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mark_order_id", updatable = false, insertable = false)
    MarkOrder markOrder;

    @Override
    public String getName() {
        return this.getProduct().getName();
    }
}
