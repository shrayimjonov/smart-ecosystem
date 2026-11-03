package uz.uzkassa.smartpossupply.report.dataset;

import org.springframework.stereotype.Component;
import uz.uzkassa.smartpossupply.dto.report.metadata.ReportDatasetType;

import java.util.Map;

@Component
public class ReportDatasetCatalog {

    private final Map<ReportDatasetType, ReportDatasetDefinition> datasets = Map.of(
            ReportDatasetType.SALES, SalesReportDatasetDefinition.create()
    );

    public ReportDatasetDefinition require(ReportDatasetType type) {
        ReportDatasetDefinition definition = datasets.get(type);
        if (definition == null) {
            throw new IllegalArgumentException("Unsupported report dataset: " + type);
        }
        return definition;
    }

    public Map<ReportDatasetType, ReportDatasetDefinition> all() {
        return datasets;
    }
}
