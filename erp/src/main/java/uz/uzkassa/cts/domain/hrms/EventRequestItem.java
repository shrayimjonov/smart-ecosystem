package uz.uzkassa.cts.domain.hrms;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import uz.uzkassa.cts.domain.base.SimpleEntity;
import uz.uzkassa.cts.domain.inventory.Product;

import java.io.Serial;
import java.io.Serializable;

@Entity
@Table(name = "event_request_item",
        indexes = {
                @Index(columnList = "event_request_id", name = "event_request_item_event_request_id_idx"),
                @Index(columnList = "product_id", name = "event_request_item_product_id_idx")
        })
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventRequestItem extends SimpleEntity {

    @Serial
    private static final long serialVersionUID = 65468842897L;

    @Column(name = "description")
    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
    String description;

    @Column(name = "event_request_id", nullable = false)
    Long eventRequestId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_request_id", updatable = false, insertable = false)
    EventRequest eventRequest;

    @Column(name = "product_id", nullable = false)
    Long productId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", updatable = false, insertable = false)
    Product product;

    @NotNull
    @Column(name = "quantity", nullable = false)
    Integer quantity;
}
