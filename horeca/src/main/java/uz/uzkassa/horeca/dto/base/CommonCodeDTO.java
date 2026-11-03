package uz.uzkassa.horeca.dto.base;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@FieldDefaults(level = AccessLevel.PROTECTED)
public class CommonCodeDTO extends CommonDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 7327872L;

    String code;

    public CommonCodeDTO(Long id, String name, String code) {
        super(id, name);
        this.code = code;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CommonCodeDTO)) {
            return false;
        }
        return Objects.equals(id, ((CommonCodeDTO) o).id);
    }
}
