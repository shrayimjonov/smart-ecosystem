package uz.uzkassa.smartpossupply.dto.base;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@FieldDefaults(level = AccessLevel.PROTECTED)
public class CommonCodeDTO extends CommonDTO {

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
