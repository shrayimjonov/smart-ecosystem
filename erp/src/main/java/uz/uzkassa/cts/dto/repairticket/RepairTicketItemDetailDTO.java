package uz.uzkassa.cts.dto.repairticket;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.base.CommonDTO;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RepairTicketItemDetailDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 5234568709002L;

    Long id;

    CommonDTO product;

    CommonDTO unit;

    BigDecimal qty;

    BigDecimal totalAmount;

    BigDecimal vatRate;

    BigDecimal totalVatAmount;
}
