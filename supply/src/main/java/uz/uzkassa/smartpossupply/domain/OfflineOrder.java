package uz.uzkassa.smartpossupply.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import uz.uzkassa.smartpossupply.domain.base.SimpleEntity;
import uz.uzkassa.smartpossupply.domain.order.Order;
import uz.uzkassa.smartpossupply.enums.OfflineOrderStatus;

import java.io.Serial;
import java.time.LocalDateTime;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "offline_order")
@Entity
public class OfflineOrder extends SimpleEntity {

    @Serial
    private static final long serialVersionUID = 7654654564L;

    @Column(name = "order_id")
    Long orderId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", updatable = false, insertable = false)
    Order order;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    OfflineOrderStatus status = OfflineOrderStatus.PENDING;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "order_json", columnDefinition = "jsonb")
    Object orderJson;

    @Column(name = "mobile_id", nullable = false)
    String mobileId;

    @Column(name = "created_date")
    LocalDateTime createdDate = LocalDateTime.now();

    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
    @Column(name = "error_message")
    String errorMessage;
}
