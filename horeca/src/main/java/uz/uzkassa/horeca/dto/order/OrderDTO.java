package uz.uzkassa.horeca.dto.order;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.horeca.dto.order.cabinet.OrderGrandTotalDTO;
import uz.uzkassa.horeca.enums.PaymentStatus;
import uz.uzkassa.horeca.enums.PaymentType;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderDTO extends OrderGrandTotalDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 89238492L;

    Long id;

    Long clientId;

    Long branchId;

    Long waiterId;

    PaymentStatus paymentStatus;

    PaymentType paymentType;

    String comment;

    Long tableId;

    Long numberOfGuest;

    Long reservationId;

    String guestName;

    String guestPhone;

    List<OrderItemBaseDTO> items;
}
