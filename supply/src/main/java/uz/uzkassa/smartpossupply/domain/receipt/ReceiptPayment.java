package uz.uzkassa.smartpossupply.domain.receipt;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.domain.base.SimpleEntity;
import uz.uzkassa.smartpossupply.enums.ReceiptPaymentType;

import java.io.Serial;
import java.math.BigDecimal;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 12.08.2024
 */
@Entity
@Table(name = "receipt_payment", indexes = {
        @Index(columnList = "receipt_id", name = "receipt_payment_receipt_id_idx"),
        @Index(columnList = "payment_type", name = "receipt_payment_payment_type_idx"),
})
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReceiptPayment extends SimpleEntity {

    @Serial
    private static final long serialVersionUID = 4353453L;

    @Column(name = "receipt_id")
    Long receiptId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receipt_id", updatable = false, insertable = false)
    Receipt receipt;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_type", nullable = false)
    ReceiptPaymentType paymentType;

    @NotNull
    @Column(name = "price", nullable = false, scale = 4, precision = 25)
    BigDecimal price;
}
