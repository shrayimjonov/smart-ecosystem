package uz.uzkassa.smartpossupply.dto.report.metadata;

import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;
import java.util.List;

@Schema(name = "ReportDatasetsResponseV1", description = "Datasets supported by the report constructor.")
public record ReportDatasetsResponse(
        List<ReportDatasetMetadata> datasets
) implements Serializable {

    public ReportDatasetsResponse {
        datasets = datasets == null ? List.of() : List.copyOf(datasets);
    }
}
