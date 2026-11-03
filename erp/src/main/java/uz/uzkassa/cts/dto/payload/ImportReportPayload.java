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
public class ImportReportPayload {

    Long importReportId;

    EntityTypeEnum entityType;

    List<ImportReportItemPayload> items;
}
