package uz.uzkassa.silen.dto.marking;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
public class AggregationCreateDTO implements Serializable {

    @NotNull
    String productId;

    Integer quantity;

    String printCode;

    @NotNull(message = "Тип пакета не выбран")
    PackageType unit;

    String serialId;

    @Size(min = 1, message = "Коды маркировки отсутствуют.")
    Set<String> marks = new LinkedHashSet<>();

    String description;

    String gcp;

    boolean isOffline;

    LocalDate productionDate = LocalDate.now();

    LocalDate expirationDate;

    AggregationType type = AggregationType.AGGREGATION;

    String aggregationPartyId;

    @JsonIgnore
    String organizationId;

    @JsonIgnore
    String login;
}
