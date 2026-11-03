package uz.uzkassa.smartpossupply.service.handlers.excel;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uz.uzkassa.smartpossupply.constants.FileConstants;
import uz.uzkassa.smartpossupply.domain.exportimport.ExportReport;
import uz.uzkassa.smartpossupply.dto.payload.ExportReportPayload;
import uz.uzkassa.smartpossupply.enums.EntityTypeEnum;
import uz.uzkassa.smartpossupply.enums.FileType;
import uz.uzkassa.smartpossupply.rabbitmq.producer.RabbitMqExportReportProducer;
import uz.uzkassa.smartpossupply.repository.ExportReportRepository;
import uz.uzkassa.smartpossupply.utils.SecurityUtils;

@Component
@Slf4j
public abstract class AbstractExportStrategy implements ExportStrategy, FileConstants {

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    RabbitMqExportReportProducer rabbitMqExportReportProducer;

    @Autowired
    ExportReportRepository exportReportRepository;

    public void generate(Object filter, FileType fileType) {
        rabbitMqExportReportProducer.send(new ExportReportPayload(filter, getEntityType(), fileType, this.create(getEntityType(), fileType)));
    }

    public Long create(EntityTypeEnum entityType, FileType fileType) {
        ExportReport exportReport = new ExportReport();
        exportReport.setCompanyId(SecurityUtils.getCurrentCompanyId());
        exportReport.setEntityType(entityType);
        exportReport.setFileType(fileType);
        exportReportRepository.save(exportReport);
        return exportReport.getId();
    }
}