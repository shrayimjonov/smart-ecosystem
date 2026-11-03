package uz.uzkassa.horeca.dto.hallitem;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.horeca.dto.base.CommonCodeDTO;
import uz.uzkassa.horeca.dto.cateringsettings.CateringServiceDTO;

import java.io.Serial;
import java.io.Serializable;

import static lombok.AccessLevel.PRIVATE;

@Setter
@Getter
@FieldDefaults(level = PRIVATE)
@NoArgsConstructor
public class HallItemDTO extends CommonCodeDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 4345433L;

    Long capacity;

    Integer x;

    Integer y;

    Integer height;

    Integer width;

    Integer rotationAngle;

    String type;

    String shape;

    @JsonIgnore
    Long hallId;

    String description;

    CateringServiceDTO settings;
}
