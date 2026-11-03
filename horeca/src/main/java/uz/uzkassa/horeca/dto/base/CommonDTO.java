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
@ToString
@FieldDefaults(level = AccessLevel.PROTECTED)
public class CommonDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    Long id;

    String name;

    public CommonDTO(Long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CommonDTO)) {
            return false;
        }
        return Objects.equals(id, ((CommonDTO) o).id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
