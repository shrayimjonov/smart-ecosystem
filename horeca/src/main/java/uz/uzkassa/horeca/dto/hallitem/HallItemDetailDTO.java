package uz.uzkassa.horeca.dto.hallitem;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.horeca.dto.base.CommonDTO;
import uz.uzkassa.horeca.enums.HallItemType;
import uz.uzkassa.horeca.enums.ShapeType;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import static lombok.AccessLevel.PRIVATE;

@Setter
@Getter
@FieldDefaults(level = PRIVATE)
@NoArgsConstructor
public class HallItemDetailDTO extends CommonDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 754331L;

    String code;

    Long capacity;

    CommonDTO hall;

    Integer x;

    Integer y;

    Integer height;

    Integer width;

    Integer rotationAngle;

    HallItemType type;

    ShapeType shape;

    LocalDateTime lastModifiedDate;

    BigDecimal feePercent;

    CommonDTO mergedTable;
}
