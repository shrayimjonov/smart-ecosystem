package uz.uzkassa.silen.integration.turon;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReportInfoResponse {
    String omsId;
    String reportId;
    String reportStatus;
    String errorReason;
}
