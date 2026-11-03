package uz.uzkassa.silen.dto.marking;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.silen.dto.SelectItem;
import uz.uzkassa.silen.enumeration.AggregationStatus;
import uz.uzkassa.silen.enumeration.AggregationType;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * A DTO for the {@link uz.uzkassa.silen.domain.Aggregation} entity.
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AggregationListDTO implements Serializable {
    String id;

    String productName;

    String barcode;

    Integer quantity;

    String printCode;

    String unitName;

    SelectItem serial;

    String serialId;

    AggregationStatus status;

    String statusName;

    String organizationName;

    String login;

    String turonAggregationId;

    LocalDateTime lastModifiedDate;

    String gcp;

    String description;

    AggregationType type;

    String typeName;
}
