package uz.uzkassa.silen.dto.mongo;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.silen.dto.CodeDTO;
import uz.uzkassa.silen.enumeration.AggregationType;
import uz.uzkassa.silen.enumeration.MarkStatus;
import uz.uzkassa.silen.enumeration.PackageType;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * A DTO for the {@link uz.uzkassa.silen.domain.mongo.Mark} entity.
 */
@Getter
@Setter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BlockDTO extends MarkDTO {

    String barcode;
    String parentBarcode;

    String aggregationCode;

    LocalDateTime lastModifiedDate;

    LocalDateTime aggregationDate;

    String statusName;

    MarkStatus markStatus;

    Integer quantity;

    String login;

    @NotNull(message = "Коды маркировки отсутствуют.")
    Set<String> marks = new LinkedHashSet<>();

    Set<String> cleanMarks = new LinkedHashSet<>();

    Set<String> dropoutMarks = new LinkedHashSet<>();

    Set<CodeDTO> markDTOs = new LinkedHashSet<>();

    String unitName;

    String turonAggregationId;

    PackageType unit;

    String utilisationId;
    LocalDateTime utilisationTime;
    LocalDateTime productionDate;
    LocalDateTime expirationDate;
    String errorMessage;
    String description;

    AggregationType type;

    String typeName;

    int checked;
}
