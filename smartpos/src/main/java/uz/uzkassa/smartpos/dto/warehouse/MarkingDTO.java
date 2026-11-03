package uz.uzkassa.smartpos.dto.warehouse;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MarkingDTO {
    private String marking;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MarkingDTO that = (MarkingDTO) o;
        return Objects.equals(marking, that.marking);
    }

    @Override
    public int hashCode() {
        return Objects.hash(marking);
    }
}
