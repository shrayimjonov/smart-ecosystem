package uz.uzkassa.cts.dto.repairticket;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RepairTicketItemDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 30985698376562548L;

    Long id;

    Long productId;

    BigDecimal qty;

}
