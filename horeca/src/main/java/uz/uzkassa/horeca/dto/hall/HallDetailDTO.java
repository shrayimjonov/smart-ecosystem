package uz.uzkassa.horeca.dto.hall;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.horeca.dto.base.CommonDTO;
import uz.uzkassa.horeca.enums.CommonStatus;

import java.io.Serial;
import java.io.Serializable;

import static lombok.AccessLevel.PRIVATE;

@Setter
@Getter
@FieldDefaults(level = PRIVATE)
@NoArgsConstructor
public class HallDetailDTO extends CommonDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 56455232L;

    CommonDTO branch;

    CommonStatus status;
}
