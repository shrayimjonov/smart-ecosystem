package uz.uzkassa.smartpossupply.dto.payload;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.enums.EntityTypeEnum;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ImportReportPayload {

    Long importReportId;

    EntityTypeEnum entityType;

    List<ImportReportItemPayload> entities = new ArrayList<>();

    public ImportReportPayload(Long importReportId, List<ImportReportItemPayload> entities, EntityTypeEnum entityType) {
        this.importReportId = importReportId;
        this.entities = entities;
        this.entityType = entityType;
    }
}
