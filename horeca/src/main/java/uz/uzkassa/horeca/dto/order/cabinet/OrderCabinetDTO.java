package uz.uzkassa.horeca.dto.order.cabinet;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderCabinetDTO extends OrderGrandTotalDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 89238492L;

    Long id;

    Long clientId;

    Long branchId;

    Long waiterId;

    String paymentStatus;

    String paymentType;

    String comment;

    Long tableId;

    Long numberOfGuest;

    Long reservationId;

    List<OrderItemCabinetDTO> items;
}
