package uz.uzkassa.cts.dto.payload;

import lombok.*;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.enums.EntityTypeEnum;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
public class CustomerBankPayload {

    Long creatorId;

    Long importReportId;

    EntityTypeEnum entityType;

    List<CustomerBankReportItemPayload> items;
}
