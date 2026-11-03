package uz.uzkassa.smartpossupply.dto.base;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PROTECTED)
public class SelectItemDTO extends CommonCodeDTO {

    boolean selected;

    public SelectItemDTO(Long id, String name, String code, boolean selected) {
        super(id, name, code);
        this.selected = selected;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SelectItemDTO)) {
            return false;
        }
        return Objects.equals(id, ((SelectItemDTO) o).id);
    }
}
