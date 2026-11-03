package uz.uzkassa.silen.dto.marking;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.silen.dto.BaseUuidDTO;
import uz.uzkassa.silen.enumeration.*;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * A DTO for the {@link uz.uzkassa.silen.domain.Party} entity.
 */
@Getter
@Setter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PartyDTO extends BaseUuidDTO implements Serializable {
    String orderProductId;
    String productId;
    String productName;
    LocalDateTime createdDate;
    Integer quantity;
    RateType rateType;
    SerialNumberType serialNumberType;
    CisType cisType;
    String cisTypeName;
    PartyStatus status;
    String statusName;
    String reportId;
    LocalDateTime reportTime;
    UsageType usageType;
    String turonPartyId;
    String description;
    AggregationStatus jobStatus;
    String jobStatusName;
    String errorMessage;
}
