package uz.uzkassa.smartpossupply.domain.transaction;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.domain.order.Order;
import uz.uzkassa.smartpossupply.enums.EntityTypeEnum;

import java.io.Serial;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "order_transaction")
@Entity
public class OrderTransaction extends Transaction {

    @Serial
    private static final long serialVersionUID = 89845L;

    @Column(name = "order_id")
    Long orderId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", insertable = false, updatable = false)
    Order order;

    @Override
    public Long getEntityId() {
        return orderId;
    }

    @Override
    public EntityTypeEnum getEntityType() {
        return EntityTypeEnum.ORDER;
    }

    @Override
    public String getEntityName() {
        return getOrder().getName();
    }
}
