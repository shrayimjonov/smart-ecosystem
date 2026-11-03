package uz.uzkassa.silen.dto.filter;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.silen.enumeration.*;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AggregationFilter extends Filter implements Serializable {

    String productId;

    String batchId;

    String serialId;

    AggregationStatus aggregationStatus;

    @Schema(hidden = true)
    List<AggregationStatus> aggregationStatusList;

    PackageType unit;

    String login;

    FileType fileType = FileType.CSV;

    AggregationType type;

    String partyAggregationId;

    String partitionId;

    CodeLength codeLength = CodeLength.SHORT;

    boolean withChild = false;
}
