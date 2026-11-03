package uz.uzkassa.silen.dto.marking;

import lombok.*;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.silen.domain.BatchAggregation;
import uz.uzkassa.silen.dto.BaseUuidDTO;
import uz.uzkassa.silen.enumeration.BatchAggregationStatus;
import uz.uzkassa.silen.enumeration.PackageType;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * A DTO for the {@link uz.uzkassa.silen.domain.BatchAggregation} entity.
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BatchAggregationListDTO extends BaseUuidDTO implements Serializable {
    String number;

    PackageType unit;

    String unitName;

    Integer quantity;

    Integer used;

    String description;

    String gcp;

    String statusName;

    BatchAggregationStatus status;

    LocalDateTime createdDate;

    String errorMessage;

    boolean exportToFile;

    public BatchAggregationListDTO(String id, String number, String gcp, Integer unit, Integer quantity, Integer unUsed, String description, LocalDateTime createdDate) {
        this.id = id;
        this.number = number;
        this.gcp = gcp;
        PackageType unitEnum = PackageType.fromCode(unit);
        this.unit = unitEnum;
        this.unitName = unitEnum.getNameRu();
        this.quantity = quantity;
        this.used = quantity - unUsed;
        this.description = description;
        this.createdDate = createdDate;
    }

    public BatchAggregationListDTO(final BatchAggregation entity) {
        this.id = entity.getId();
        this.number = entity.getNumber();
        this.gcp = entity.getGcp();
        PackageType unitEnum = PackageType.fromCode(entity.getUnit());
        this.unit = unitEnum;
        this.unitName = unitEnum.getNameRu();
        this.quantity = entity.getQuantity();
        this.description = entity.getDescription();
        this.createdDate = entity.getCreatedDate();
        this.errorMessage = entity.getErrorMessage();
        this.status = entity.getStatus();
        this.statusName = entity.getStatus() != null ? entity.getStatus().getRuText() : "";
    }
}
