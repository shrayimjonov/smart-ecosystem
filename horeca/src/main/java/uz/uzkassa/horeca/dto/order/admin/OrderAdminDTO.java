package uz.uzkassa.horeca.dto.order.admin;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.horeca.dto.order.cabinet.OrderGrandTotalDTO;
import uz.uzkassa.horeca.dto.order.cabinet.OrderItemCabinetDTO;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderAdminDTO extends OrderGrandTotalDTO implements Serializable {

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

    List<OrderItemAdminDTO> items;
}
