package uz.uzkassa.silen.dto.marking;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.silen.dto.CodeDTO;
import uz.uzkassa.silen.dto.SelectItem;
import uz.uzkassa.silen.enumeration.AggregationStatus;
import uz.uzkassa.silen.enumeration.AggregationType;
import uz.uzkassa.silen.enumeration.PackageType;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * A DTO for the {@link uz.uzkassa.silen.domain.Aggregation} entity.
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AggregationDTO implements Serializable {

    String id;

    String productName;

    String productId;

    Integer quantity;

    String printCode;

    String code;

    PackageType unit;

    String unitName;

    String serialId;

    SelectItem serial;

    @NotNull(message = "Коды маркировки отсутствуют.")
    Set<String> marks = new LinkedHashSet<>();

    Set<String> cleanMarks = new LinkedHashSet<>();

    Set<CodeDTO> markDTOs = new LinkedHashSet<>();

    Set<String> dropoutMarks = new LinkedHashSet<>();

    String description;

    String organizationName;

    String organizationId;

    AggregationStatus status;

    String statusName;

    String gcp;

    String barcode;

    String parentBarcode;

    String turonAggregationId;

    String shipmentId;

    String shipmentNumber;

    LocalDateTime aggregationDate;

    String utilisationId;

    LocalDateTime utilisationTime;

    LocalDateTime productionDate;

    LocalDateTime expirationDate;

    AggregationType type;

    String typeName;

    SelectItem partyAggregation;

    String errorMessage;

    int checked;
}
