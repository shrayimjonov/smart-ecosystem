package uz.uzkassa.horeca.dto.order.cabinet;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uz.uzkassa.horeca.dto.base.CommonDTO;
import uz.uzkassa.horeca.enums.OrderStatus;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
public class OrderShortListCabinetDTO extends CommonDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 3433453453L;

    OrderStatus status;
}
