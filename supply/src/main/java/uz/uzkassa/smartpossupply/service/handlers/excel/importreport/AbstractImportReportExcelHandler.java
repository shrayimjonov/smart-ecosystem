package uz.uzkassa.smartpossupply.service.handlers.excel.importreport;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.smartpossupply.constants.ImportConstants;
import uz.uzkassa.smartpossupply.dto.importreport.ImportReportDTO;
import uz.uzkassa.smartpossupply.dto.payload.ImportReportItemPayload;
import uz.uzkassa.smartpossupply.enums.EntityTypeEnum;
import uz.uzkassa.smartpossupply.enums.FileType;
import uz.uzkassa.smartpossupply.rabbitmq.producer.RabbitMqImportReportProducer;
import uz.uzkassa.smartpossupply.repository.ImportReportItemRepository;
import uz.uzkassa.smartpossupply.repository.ImportReportRepository;
import uz.uzkassa.smartpossupply.service.FileUploadService;
import uz.uzkassa.smartpossupply.service.handlers.excel.template.AbstractImportTemplateExcelHandler;

import java.util.List;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 29.03.2023 14:46
 */

@Component
@Slf4j
public abstract class AbstractImportReportExcelHandler<T extends ImportReportDTO> extends AbstractImportTemplateExcelHandler implements ImportConstants {

    @Autowired
    protected ImportReportRepository importReportRepository;

    @Autowired
    protected ImportReportItemRepository importReportItemRepository;

    @Autowired
    protected FileUploadService fileUploadService;

    @Autowired
    protected RabbitMqImportReportProducer rabbitMqImportReportProducer;

    @Autowired
    protected ObjectMapper objectMapper;

    abstract List<ImportReportItemPayload> readFile(MultipartFile file, T importReportDTO);

    abstract void importReport(MultipartFile file, EntityTypeEnum entityTypeEnum, FileType fileType, T importReportDTO);

    abstract List<String> getErrorFileHeaders();

    abstract String getErrorFileName();

    abstract ResponseEntity<Resource> generateErrorFile(Object entity);
}
