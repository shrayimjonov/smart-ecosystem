package uz.uzkassa.silen.dto.marking;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.silen.enumeration.AggregationType;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * A DTO for the {@link uz.uzkassa.silen.domain.Aggregation} entity.
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PROTECTED)
public class AggregationUpdateDTO implements Serializable {

    String description;

    @NotNull(message = "Код аггрегатсия отсутствуют.")
    String aggregation;

    boolean isOffline;

    Set<String> childMarks = new LinkedHashSet<>();

    LocalDate productionDate = LocalDate.now();

    LocalDate expirationDate;

    AggregationType type = AggregationType.AGGREGATION;
}
