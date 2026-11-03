package uz.uzkassa.smartpossupply.web.rest.cabinet;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.smartpossupply.dto.report.metadata.ReportDatasetsResponse;
import uz.uzkassa.smartpossupply.service.report.ReportMetadataService;
import uz.uzkassa.smartpossupply.web.rest.cabinet.contract.ReportMetadataApi;

@RestController
@RequiredArgsConstructor
public class CabinetReportMetadataResource implements ReportMetadataApi {

    private final ReportMetadataService reportMetadataService;

    @Override
    public ResponseEntity<ReportDatasetsResponse> getReportDatasets() {
        return ResponseEntity.ok(reportMetadataService.getAvailableDatasets());
    }
}
