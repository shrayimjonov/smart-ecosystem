package uz.uzkassa.silen.dto.marking;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.silen.enumeration.AggregationType;
import uz.uzkassa.silen.enumeration.PackageType;

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
public class AggregationBlockDTO implements Serializable {

    @NotNull
    String productId;

    @NotNull
    String childProductId;

    @NotNull
    @Min(1)
    @Max(Integer.MAX_VALUE)
    Integer quantity;

    @NotNull
    String printCode;

    @NotNull(message = "Тип пакета можно не выбрат")
    PackageType unit = PackageType.BLOCK;

    String serialId;

    @Size(min = 1, message = "Коды маркировки отсутствуют.")
    Set<String> marks = new LinkedHashSet<>();

    String description;

    boolean isOffline;

    LocalDate productionDate = LocalDate.now();

    LocalDate expirationDate;

    AggregationType type = AggregationType.AGGREGATION;
}
