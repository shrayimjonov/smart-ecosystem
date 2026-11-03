package uz.uzkassa.silen.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.silen.enumeration.AggregationStatus;

import java.time.LocalDateTime;


@Getter
@Setter
@ToString(onlyExplicitlyIncluded = true, doNotUseGetters = true)
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AggregationHistoryDTO {
    String id;

    String aggregationId;

    AggregationStatus status;

    String statusName;

    LocalDateTime createdDate;

    String printCode;

    String login;

    Integer unit;

    String unitName;

    Integer quantity;

    String productName;

    String organizationId;

    String organizationName;

    String turonAggregationId;

    String serialId;

    String serialNumber;

    String shipmentId;

    String shipmentNumber;

    Long invoiceId;

    String invoiceNumber;

}
