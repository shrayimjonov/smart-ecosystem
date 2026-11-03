package uz.uzkassa.cts.service.impl;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.cts.domain.FileUpload;
import uz.uzkassa.cts.domain.exportimport.ExportReport;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.dto.cases.caseproperty.CustomMultipartFile;
import uz.uzkassa.cts.dto.exportimportreport.ExportReportListDTO;
import uz.uzkassa.cts.dto.payload.ExportReportPayload;
import uz.uzkassa.cts.enums.ContentType;
import uz.uzkassa.cts.enums.DocumentType;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.enums.ExportReportStatus;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.handlers.export.ExportStrategy;
import uz.uzkassa.cts.handlers.export.ExportStrategyFactory;
import uz.uzkassa.cts.rabbitmq.producer.RabbitMqExportReportProducer;
import uz.uzkassa.cts.repository.ExportReportRepository;
import uz.uzkassa.cts.service.ExportReportService;
import uz.uzkassa.cts.service.FileUploadService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@Service
@Slf4j
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ExportReportServiceImpl extends BaseServiceImpl<ExportReport, Long> implements ExportReportService {

    ExportReportRepository exportReportRepository;
    RabbitMqExportReportProducer rabbitMqExportReportProducer;
    ExportStrategyFactory exportStrategyFactory;
    FileUploadService fileUploadService;

    public ExportReportServiceImpl(
            ExportReportRepository exportReportRepository,
            RabbitMqExportReportProducer rabbitMqExportReportProducer,
            ExportStrategyFactory exportStrategyFactory, FileUploadService fileUploadService) {
        super(exportReportRepository);
        this.exportReportRepository = exportReportRepository;
        this.rabbitMqExportReportProducer = rabbitMqExportReportProducer;
        this.exportStrategyFactory = exportStrategyFactory;
        this.fileUploadService = fileUploadService;
    }

    @Transactional
    public Long create(EntityTypeEnum entityType, ContentType contentType) {
        ExportReport exportReport = new ExportReport();
        exportReport.setReportEntityType(entityType);
        exportReport.setContentType(contentType);
        exportReport.setExpireDate(LocalDate.now().atTime(LocalTime.MAX).plusDays(3));
        exportReport.setCreatorId(getCurrentUserId());
        return exportReportRepository.save(exportReport).getId();
    }

    @Transactional
    public void generateReport(ExportReportPayload payload) {
        ExportReport exportReport = exportReportRepository.findById(payload.getExportReportId()).orElseThrow(validationService.entityNotFoundExceptionThrow(REPORT));
        ExportStrategy exportStrategy = exportStrategyFactory.getStrategy(payload.getEntityType());
        CustomMultipartFile multipartFile = exportStrategy.generateReport(payload.getFilter(), payload.getContentType());

        try {
            FileUpload fileUpload = fileUploadService.upload(multipartFile, exportReport.getEntityType(), exportReport.getEntityId(), DocumentType.OTHER);
            if (fileUpload == null) {
                throw validationService.badRequestExceptionThrow(FILE_UPLOAD_FAILED_UNEXPECTED_ERROR).get();
            }
            exportReport.setFileId(fileUpload.getId());
            exportReport.setStatus(ExportReportStatus.COMPLETED);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            exportReport.setStatus(ExportReportStatus.ERROR);
            exportReport.setErrorMessage(e.getMessage());
        }
        exportReportRepository.save(exportReport);
    }

    @Override
    public Page<ExportReportListDTO> getList(BaseFilter filter) {
        ResultList<ExportReport> resultList = exportReportRepository.getResultList(filter);
        List<ExportReportListDTO> result = resultList
                .getList()
                .stream()
                .map(exportReport -> {
                    ExportReportListDTO exportReportListDTO = new ExportReportListDTO();
                    exportReportListDTO.setId(exportReport.getId());
                    if (exportReport.getFileId() != null) {
                        exportReportListDTO.setFile(exportReport.getFile().toFileDTO());
                    }
                    exportReportListDTO.setStatus(exportReport.getStatus());
                    exportReportListDTO.setEntityType(exportReport.getReportEntityType());
                    exportReportListDTO.setContentType(exportReport.getContentType());
                    exportReportListDTO.setCreatedDate(exportReport.getCreatedDate());
                    exportReportListDTO.setExpireDate(exportReport.getExpireDate());
                    exportReportListDTO.setCreator(exportReport.getCreator().toCommonDTO());
                    return exportReportListDTO;
                }).collect(Collectors.toList());
        return new PageImpl<>(result, filter.getOrderedPageable(), resultList.getCount());
    }

    @Override
    @Transactional
    public void export(Object filter, EntityTypeEnum entityType, ContentType contentType) {
        if (exportReportRepository.existsByReportEntityTypeAndContentTypeAndStatusAndCreatorIdAndCreatedDateGreaterThan(entityType, contentType, ExportReportStatus.PENDING, getCurrentUserId(), LocalDateTime.now().minusHours(1))) {
            throw validationService.badRequestExceptionThrow(FILE_UPLOAD_FAILED_STATUS, ExportReportStatus.PENDING.getName()).get();
        }
        rabbitMqExportReportProducer.send(new ExportReportPayload(filter, entityType, contentType, this.create(entityType, contentType)));
    }
}
