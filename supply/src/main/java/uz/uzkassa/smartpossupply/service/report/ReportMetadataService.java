package uz.uzkassa.smartpossupply.service.report;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import uz.uzkassa.smartpossupply.config.ApplicationProperties;
import uz.uzkassa.smartpossupply.constants.AuthConstants;
import uz.uzkassa.smartpossupply.dto.report.metadata.ReportDatasetMetadata;
import uz.uzkassa.smartpossupply.dto.report.metadata.ReportDatasetType;
import uz.uzkassa.smartpossupply.dto.report.metadata.ReportDatasetsResponse;
import uz.uzkassa.smartpossupply.dto.report.metadata.ReportMetadataFixtures;
import uz.uzkassa.smartpossupply.dto.report.metadata.ReportLimitsMetadata;
import uz.uzkassa.smartpossupply.dto.report.metadata.ReportPeriodMetadata;
import uz.uzkassa.smartpossupply.enums.LanguageEnum;
import uz.uzkassa.smartpossupply.report.dataset.ReportDatasetCatalog;
import uz.uzkassa.smartpossupply.utils.SecurityUtils;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportMetadataService {

    private final ReportDatasetCatalog datasetCatalog;
    private final ApplicationProperties applicationProperties;

    public ReportDatasetsResponse getAvailableDatasets() {
        LanguageEnum language = resolveLanguage();
        LocalDate today = LocalDate.now();
        ReportPeriodMetadata period = new ReportPeriodMetadata(
                today.withDayOfMonth(1),
                today.withDayOfMonth(today.lengthOfMonth())
        );

        List<ReportDatasetMetadata> datasets = datasetCatalog.all().keySet().stream()
                .map(type -> metadata(type, language, period))
                .toList();
        return new ReportDatasetsResponse(datasets);
    }

    private ReportDatasetMetadata metadata(
            ReportDatasetType type,
            LanguageEnum language,
            ReportPeriodMetadata period
    ) {
        return switch (type) {
            case SALES -> ReportMetadataFixtures.sales(language, period, limits());
        };
    }

    private ReportLimitsMetadata limits() {
        ApplicationProperties.ReportEngineConfig config = applicationProperties.getReportEngine();
        return new ReportLimitsMetadata(config.getMaxCells(), config.getMaxRows(), config.getMaxPeriodDays());
    }

    private LanguageEnum resolveLanguage() {
        HttpServletRequest request = SecurityUtils.getCurrentRequest();
        String requestedLanguage = request == null ? null : request.getHeader(AuthConstants.LANGUAGE);
        if (StringUtils.isNotBlank(requestedLanguage)) {
            return LanguageEnum.getByCode(requestedLanguage);
        }
        return LanguageEnum.getByCode(SecurityUtils.getLocaleCode());
    }
}
