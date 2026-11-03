package uz.uzkassa.horeca.dto.payload;

import lombok.*;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.horeca.enums.OrderEventType;

import java.io.Serial;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderEventPayload implements Serializable {


    @Serial
    private static final long serialVersionUID = 564564L;

    Long id;

    List<Long> orderItems;

    OrderEventType type;

    public OrderEventPayload(Long id, OrderEventType type) {
        this.id = id;
        this.type = type;
        this.orderItems = Collections.emptyList();
    }
}
