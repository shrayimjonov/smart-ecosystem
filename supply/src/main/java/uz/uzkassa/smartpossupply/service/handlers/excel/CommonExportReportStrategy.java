package uz.uzkassa.smartpossupply.service.handlers.excel;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.smartpossupply.domain.FileUpload;
import uz.uzkassa.smartpossupply.domain.exportimport.ExportReport;
import uz.uzkassa.smartpossupply.dto.payload.ExportReportPayload;
import uz.uzkassa.smartpossupply.enums.ReportStatus;
import uz.uzkassa.smartpossupply.repository.ExportReportRepository;
import uz.uzkassa.smartpossupply.service.CustomMultipartFile;
import uz.uzkassa.smartpossupply.service.FileUploadService;
import uz.uzkassa.smartpossupply.web.rest.errors.BadRequestException;
import uz.uzkassa.smartpossupply.web.rest.errors.NotFoundException;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class CommonExportReportStrategy {

    ExportReportRepository exportReportRepository;

    FileUploadService fileUploadService;

    ExportStrategyFactory exportStrategyFactory;

    public CommonExportReportStrategy(ExportReportRepository exportReportRepository, FileUploadService fileUploadService, ExportStrategyFactory exportStrategyFactory) {
        this.exportReportRepository = exportReportRepository;
        this.fileUploadService = fileUploadService;
        this.exportStrategyFactory = exportStrategyFactory;
    }

    @Transactional
    public void generateReport(ExportReportPayload payload) {
        ExportReport exportReport = exportReportRepository.findById(payload.getExportReportId()).orElseThrow(() -> new NotFoundException("Export report not found"));
        ExportStrategy exportStrategy = exportStrategyFactory.getStrategy(payload.getEntityType());
        CustomMultipartFile multipartFile = exportStrategy.generateReport(payload.getFilter(), payload.getFileType());
        try {
            FileUpload fileUpload = fileUploadService.fileUpload(multipartFile, false);
            if (fileUpload == null) {
                throw new BadRequestException("An unaccepted error has occurred while uploading file", "An unaccepted error has occurred while uploading file");
            }
            exportReport.setFileId(fileUpload.getId());
            exportReport.setStatus(ReportStatus.COMPLETED);
        } catch (Exception e) {
            exportReport.setStatus(ReportStatus.ERROR);
            exportReport.setErrorMessage(e.getMessage());
            log.error(e.getMessage(), e);
        }
        exportReportRepository.save(exportReport);
    }
}
