package uz.uzkassa.horeca.dto.hallitem;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.horeca.dto.base.CommonCodeDTO;
import uz.uzkassa.horeca.dto.base.CommonDTO;
import uz.uzkassa.horeca.enums.HallItemStatus;
import uz.uzkassa.horeca.enums.HallItemType;
import uz.uzkassa.horeca.enums.ShapeType;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

import static lombok.AccessLevel.PRIVATE;

@Setter
@Getter
@FieldDefaults(level = PRIVATE)
@NoArgsConstructor
public class HallItemListDTO extends CommonCodeDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 335454L;

    Long capacity;

    ShapeType shape;

    CommonDTO hall;

    Integer x;

    Integer y;

    Integer height;

    Integer width;

    Integer rotationAngle;

    HallItemType type;

    HallItemStatus status;

    String description;

    BigDecimal feePercent;

    boolean withOrder;

    String message;
}
