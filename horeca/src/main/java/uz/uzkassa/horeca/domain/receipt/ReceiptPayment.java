package uz.uzkassa.horeca.domain.receipt;

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

@Entity
@Table(name = "receipt_payment")
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReceiptPayment extends SimpleEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 4353453L;

    @Column(name = "receipt_id", nullable = false, updatable = false, insertable = false)
    Long receiptId;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JoinColumn(name = "receipt_id")
    Receipt receipt;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_type", nullable = false)
    ReceiptPaymentType paymentType;

    @NotNull
    @Column(name = "price", nullable = false, scale = 4, precision = 25)
    BigDecimal price;
}
