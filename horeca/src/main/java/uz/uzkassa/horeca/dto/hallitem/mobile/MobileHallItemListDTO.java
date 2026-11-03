package uz.uzkassa.horeca.dto.hallitem.mobile;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.horeca.dto.base.CommonCodeDTO;
import uz.uzkassa.horeca.dto.base.CommonDTO;
import uz.uzkassa.horeca.enums.HallItemStatus;

import java.io.Serial;
import java.io.Serializable;

import static lombok.AccessLevel.PRIVATE;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 02.02.2024
 */
@Setter
@Getter
@FieldDefaults(level = PRIVATE)
@NoArgsConstructor
public class MobileHallItemListDTO extends CommonCodeDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 335458454L;

    Long capacity;

    CommonDTO hall;

    HallItemStatus status;
}
