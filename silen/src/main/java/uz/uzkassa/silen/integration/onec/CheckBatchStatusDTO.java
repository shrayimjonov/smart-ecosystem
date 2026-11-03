package uz.uzkassa.silen.integration.onec;

import lombok.*;
import uz.uzkassa.silen.enumeration.BatchAggregationStatus;
import uz.uzkassa.silen.enumeration.ReportStatus;

@ToString
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CheckBatchStatusDTO {

    String id;
    BatchAggregationStatus status;
    ReportStatus fileStatus;
    Long fileId;
}
