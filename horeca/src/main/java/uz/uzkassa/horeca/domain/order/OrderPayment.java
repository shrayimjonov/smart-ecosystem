package uz.uzkassa.horeca.domain.order;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.horeca.domain.base.SimpleEntity;
import uz.uzkassa.horeca.enums.ReceiptPaymentType;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Powered by: Rayimjonov Shuxratjon
 * Date: 09.11.2024
 */
@Entity
@Table(name = "order_payment")
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderPayment extends SimpleEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 4353453L;

    @NotNull
    @Column(name = "order_id", nullable = false)
    Long orderId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", updatable = false, insertable = false)
    Order order;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_type", nullable = false)
    ReceiptPaymentType paymentType;

    @NotNull
    @Column(name = "price", nullable = false, scale = 4, precision = 25)
    BigDecimal price;
}
