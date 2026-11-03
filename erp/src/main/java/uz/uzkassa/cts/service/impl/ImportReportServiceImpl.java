package uz.uzkassa.cts.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.cts.domain.FileUpload;
import uz.uzkassa.cts.domain.exportimport.ImportReport;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.dto.exportimportreport.ImportReportListDTO;
import uz.uzkassa.cts.dto.payload.ImportReportPayload;
import uz.uzkassa.cts.enums.ContentType;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.handlers.imports.ImportStrategy;
import uz.uzkassa.cts.handlers.imports.ImportStrategyFactory;
import uz.uzkassa.cts.rabbitmq.producer.RabbitMqImportReportProducer;
import uz.uzkassa.cts.repository.ImportReportRepository;
import uz.uzkassa.cts.service.FileUploadService;
import uz.uzkassa.cts.service.ImportReportService;

import java.util.List;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@Service
public class ImportReportServiceImpl extends BaseServiceImpl<ImportReport, Long> implements ImportReportService {

    private final ImportReportRepository importReportRepository;
    private final RabbitMqImportReportProducer rabbitMqImportReportProducer;
    private final FileUploadService fileUploadService;
    private final ImportStrategyFactory importStrategyFactory;


    public ImportReportServiceImpl(ImportReportRepository importReportRepository,
                                   RabbitMqImportReportProducer rabbitMqImportReportProducer,
                                   FileUploadService fileUploadService,
                                   ImportStrategyFactory importStrategyFactory) {
        super(importReportRepository);
        this.importReportRepository = importReportRepository;
        this.rabbitMqImportReportProducer = rabbitMqImportReportProducer;
        this.fileUploadService = fileUploadService;
        this.importStrategyFactory = importStrategyFactory;
    }

    @Override
    @Transactional
    public void create(MultipartFile file, EntityTypeEnum entityType, ContentType contentType) {
        ImportReport importReport = new ImportReport();
        importReport.setEntityType(entityType);
        importReport.setContentType(contentType);
        FileUpload fileUpload = fileUploadService.upload(file);
        importReport.setFileId(fileUpload.getId());
        importReport.setFile(fileUpload);
        importReportRepository.save(importReport);

        ImportStrategy importStrategy = importStrategyFactory.getStrategy(entityType);
        rabbitMqImportReportProducer.send(new ImportReportPayload(importReport.getId(), entityType, importStrategy.getItems(file, contentType)));
    }

    @Override
    @Transactional
    public void importReportQueue(ImportReportPayload payload) {
        ImportStrategy importStrategy = importStrategyFactory.getStrategy(payload.getEntityType());
        importStrategy.importReportQueue(payload);
    }

    @Override
    public Page<ImportReportListDTO> getList(BaseFilter filter) {
        ResultList<ImportReport> resultList = importReportRepository.getResultList(filter);
        List<ImportReportListDTO> result = resultList
                .getList()
                .stream()
                .map(importReport -> {
                    ImportReportListDTO importReportListDTO = new ImportReportListDTO();
                    importReportListDTO.setId(importReport.getId());
                    if (importReport.getFileId() != null) {
                        importReportListDTO.setFile(importReport.getFile().toFileDTO());
                    }
                    importReportListDTO.setStatus(importReport.getStatus());
                    importReportListDTO.setEntityType(importReport.getEntityType());
                    importReportListDTO.setContentType(importReport.getContentType());
                    importReportListDTO.setCreatedDate(importReport.getCreatedDate());
                    importReportListDTO.setCreator(importReport.getCreator().toCommonDTO());
                    importReportListDTO.setItemTotal(importReport.getItemTotal());
                    importReportListDTO.setImportedTotal(importReport.getImportedTotal());
                    importReportListDTO.setErrorTotal(importReport.getErrorTotal());
                    return importReportListDTO;
                }).collect(Collectors.toList());
        return new PageImpl<>(result, filter.getOrderedPageable(), resultList.getCount());
    }
}
