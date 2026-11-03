package uz.uzkassa.smartpos.dto.turon;

import lombok.*;

import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class MarkingRelationDto {
    private String marking;
    private String parent;
    private Set<MarkingRelationDto> children;

    public MarkingRelationDto(String marking) {
        this.marking = marking;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        MarkingRelationDto that = (MarkingRelationDto) o;
        return Objects.equals(marking, that.marking);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(marking);
    }
}
