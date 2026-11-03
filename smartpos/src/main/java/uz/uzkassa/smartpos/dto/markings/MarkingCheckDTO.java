package uz.uzkassa.smartpos.dto.markings;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MarkingCheckDTO {
    private String code;
    private Set<String> children;
}
