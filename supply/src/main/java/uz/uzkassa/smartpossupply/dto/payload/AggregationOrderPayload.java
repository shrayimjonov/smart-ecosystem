package uz.uzkassa.smartpossupply.dto.payload;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AggregationOrderPayload implements Serializable {

    Long aggregationOrderId;

    Long companyId;

    Integer qty;

    String packageType;

    Long creatorId;
}
