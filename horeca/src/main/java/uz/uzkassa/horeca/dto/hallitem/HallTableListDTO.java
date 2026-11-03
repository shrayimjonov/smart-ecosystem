package uz.uzkassa.horeca.dto.hallitem;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.horeca.dto.base.CommonDTO;
import uz.uzkassa.horeca.enums.HallItemStatus;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

import static lombok.AccessLevel.PRIVATE;

@Setter
@Getter
@FieldDefaults(level = PRIVATE)
@NoArgsConstructor
public class HallTableListDTO extends CommonDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 12133545434L;

    Long capacity;

    HallItemStatus status;

    boolean withOrder;

    String message;

    Long numberOfGuest;

    BigDecimal feePercent;
}
