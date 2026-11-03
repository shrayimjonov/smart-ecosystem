package uz.uzkassa.silen.dto.marking;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.silen.enumeration.PackageType;

/**
 * A DTO for the {@link uz.uzkassa.silen.domain.BatchAggregation} entity.
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BatchAggregationDTO {

    PackageType unit;

    Integer quantity;

    @Size(max = 256)
    String description;

    String gcp;

    @JsonIgnore
    boolean exportToFile = false;
}
