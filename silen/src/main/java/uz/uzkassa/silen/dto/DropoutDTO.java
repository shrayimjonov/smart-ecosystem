package uz.uzkassa.silen.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.silen.enumeration.DropoutReason;

import java.util.HashSet;
import java.util.Set;


@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DropoutDTO {

    Set<String> aggregations = new HashSet<>();

    Set<String> blocks = new HashSet<>();

    Set<String> marks = new HashSet<>();

    DropoutReason dropoutReason;
}
