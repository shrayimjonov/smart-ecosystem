package uz.uzkassa.silen.dto.mongo;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.silen.enumeration.AggregationStatus;
import uz.uzkassa.silen.enumeration.MarkStatus;
import uz.uzkassa.silen.enumeration.PackageType;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * A DTO for the {@link uz.uzkassa.silen.domain.mongo.Mark} entity.
 */
@Getter
@Setter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MarkDTO implements Serializable {
    String id;
    String code;
    String printCode;
    String orderProductId;
    String partyId;

    String productId;
    String productName;
    String parentBarcode;

    String parentCode;
    boolean used;
    String organizationId;
    String organizationName;
    LocalDateTime createdDate;
    AggregationStatus status;
    String statusName;
    MarkStatus turonStatus;
    String turonStatusName;
    PackageType packageType;

    String gtin;
    String serialId;
    String serialNumber;
    String packUnit;
    String other;

    String utilisationId;
    LocalDateTime utilisationTime;
}
