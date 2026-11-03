package uz.uzkassa.silen.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import uz.uzkassa.silen.domain.ExportReport;
import uz.uzkassa.silen.dto.ExportReportDTO;
import uz.uzkassa.silen.dto.filter.Filter;
import uz.uzkassa.silen.dto.filter.ReportFilter;
import uz.uzkassa.silen.dto.filter.UnitCodesReportFilter;
import uz.uzkassa.silen.dto.mq.ExportReportPayload;
import uz.uzkassa.silen.enumeration.ExportReportType;
import uz.uzkassa.silen.enumeration.FileType;
import uz.uzkassa.silen.enumeration.PackageType;
import uz.uzkassa.silen.enumeration.ReportStatus;
import uz.uzkassa.silen.exceptions.BadRequestException;
import uz.uzkassa.silen.export.IExportHandler;
import uz.uzkassa.silen.repository.pg.ExportReportRepository;
import uz.uzkassa.silen.repository.pg.PartyAggregationRepository;
import uz.uzkassa.silen.security.SecurityUtils;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@RequiredArgsConstructor
public class ExportReportService extends BaseService {

    ExportReportUploadService exportReportUploadService;
    ExportReportRepository exportReportRepository;
    PartyAggregationRepository partyAggregationRepository;

    Map<ExportReportType, IExportHandler> exportHandlers = new HashMap<>();

    public void registrar(ExportReportType exportReportType, IExportHandler exportHandler) {
        this.exportHandlers.put(exportReportType, exportHandler);
    }

    public <T extends Filter> void export(final T filter, final ExportReportType exportReportType, final FileType fileType, final String attachmentName) {
        final String organizationId = SecurityUtils.getCurrentOrganizationId();
        exportReportRepository.findFirstByOrganizationIdAndStatusOrderByCreatedDateDesc(organizationId, ReportStatus.PENDING)
                .ifPresent(exportReport -> {
                    if (exportReport.getCreatedDate().isAfter(LocalDateTime.now().minusMinutes(10))) {
                        throw badRequest("Действие выполнить невозможн. Ожидайте генерации предыдущего файла.").get();
                    } else {
                        exportReport.setStatus(ReportStatus.ERROR);
                        exportReportRepository.save(exportReport);
                    }
                });
        String filters = "";
        try {
            filters = objectMapper.writeValueAsString(filter);
        } catch (Exception ignored) {
        }
        ExportReport exportReport = new ExportReport();
        exportReport.setType(exportReportType);
        exportReport.setFileType(fileType);
        exportReport.setName(attachmentName + fileType.getExtension());
        exportReport.setOrganizationId(organizationId);
        exportReport.setFilters(filters);
        exportReportRepository.save(exportReport);
        final ExportReportPayload payload = new ExportReportPayload(filter, exportReport.getId());
        rabbitMqProducer.sendExportReport(payload);
    }

    public void startGenerate(final ExportReportPayload payload) {
        final Optional<ExportReport> exportReportOptional = exportReportRepository.findById(payload.getExportReportId());
        if (exportReportOptional.isEmpty()) return;
        final ExportReport exportReport = exportReportOptional.get();

        try (final ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            if (this.exportHandlers.containsKey(exportReport.getType())) {
                IExportHandler exportHandlerInterface = exportHandlers.get(exportReport.getType());
                exportHandlerInterface.writeToStream(exportReport, outputStream);
            }

            final CustomMultipartFile multipartFile = new CustomMultipartFile();
            multipartFile.setContentType(exportReport.getFileType().getContentType());
            multipartFile.setOriginalName(exportReport.getName());

            multipartFile.setBytes(outputStream.toByteArray());

            final String fileUploadedPath = exportReportUploadService.fileUpload(multipartFile, exportReport.getName(), exportReport.getId().toString());
            if (fileUploadedPath == null) {
                throw new BadRequestException("An unaccepted error has occurred while uploading file");
            }
            exportReport.setPath(fileUploadedPath);
            exportReport.setStatus(ReportStatus.COMPLETED);
            exportReport.setExpireDate(LocalDateTime.now().plusDays(3));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            exportReport.setStatus(ReportStatus.ERROR);
            exportReport.setErrorMessage(e.getMessage());
        }
        exportReportRepository.save(exportReport);
    }

    public Page<ExportReportDTO> getList(final ReportFilter filter) {
        return exportReportRepository.findAllByFilter(filter)
                .map(ExportReport::toDTO);
    }

    public void createUnitReport(UnitCodesReportFilter unitCodesReportFilter) {
        if (unitCodesReportFilter.getPartyAggregationId() != null) {
            partyAggregationRepository.findById(unitCodesReportFilter.getPartyAggregationId()).ifPresent(partyAggregation -> {
                unitCodesReportFilter.setUnit(PackageType.fromCode(partyAggregation.getUnit()));
            });
        }
        export(unitCodesReportFilter, ExportReportType.EXCEL_UNIT_CODES_REPORT, FileType.EXCEL, "Unit_Codes_Report");
    }
}
