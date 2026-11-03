package uz.uzkassa.silen.dto.filter;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.silen.enumeration.*;

import java.util.List;

@Getter
@Setter
@ToString(callSuper = true)
@FieldDefaults(level = AccessLevel.PROTECTED)
public class MongoFilter extends Filter {

    String productId;

    String orderProductId;

    String partyId;

    String batchId;

    String serialId;

    Boolean used;

    AggregationStatus aggregationStatus;

    @Schema(hidden = true)
    List<AggregationStatus> aggregationStatusList;

    PackageType unit;

    BufferStatus status;

    String gtin;

    String login;
    String partyAggregationId;

    FileType fileType = FileType.CSV;

    String partitionId;

    AggregationType type;

    CodeLength codeLength = CodeLength.SHORT;

}
