package uz.uzkassa.smartpossupply.service;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.smartpossupply.domain.exportimport.ExportReport;
import uz.uzkassa.smartpossupply.dto.base.ResultList;
import uz.uzkassa.smartpossupply.dto.payload.ExportReportPayload;
import uz.uzkassa.smartpossupply.dto.report.ExportReportListDTO;
import uz.uzkassa.smartpossupply.enums.EntityTypeEnum;
import uz.uzkassa.smartpossupply.enums.FileType;
import uz.uzkassa.smartpossupply.filters.BaseFilter;
import uz.uzkassa.smartpossupply.rabbitmq.producer.RabbitMqExportReportProducer;
import uz.uzkassa.smartpossupply.repository.ExportReportRepository;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 16.02.2023 15:00
 */
@Service
@Transactional(readOnly = true)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@Slf4j
@RequiredArgsConstructor
public class ExportReportService extends BaseService {

    ExportReportRepository exportReportRepository;
    RabbitMqExportReportProducer rabbitMqExportReportProducer;

    @Transactional
    public Long create(EntityTypeEnum entityType, FileType fileType) {
        ExportReport exportReport = new ExportReport();
        exportReport.setCompanyId(getCurrentCompanyId());
        exportReport.setEntityType(entityType);
        exportReport.setFileType(fileType);
        exportReportRepository.save(exportReport);
        return exportReport.getId();
    }

    public Page<ExportReportListDTO> getList(BaseFilter filter) {
        ResultList<ExportReport> resultList = exportReportRepository.getResultList(filter);
        List<ExportReportListDTO> result = resultList
                .getList()
                .stream()
                .map(exportReport -> {
                    ExportReportListDTO exportReportListDTO = new ExportReportListDTO();
                    exportReportListDTO.setId(exportReport.getId());
                    if (exportReport.getFile() != null) {
                        exportReportListDTO.setFile(exportReport.getFile().toFileDTO());
                    }
                    exportReportListDTO.setStatus(exportReport.getStatus());
                    exportReportListDTO.setEntityType(exportReport.getEntityType());
                    exportReportListDTO.setFileType(exportReport.getFileType());
                    exportReportListDTO.setCreatedDate(exportReport.getCreatedDate());
                    exportReportListDTO.setExpireDate(exportReport.getExpireDate());
                    return exportReportListDTO;
                }).toList();
        return new PageImpl<>(result, filter.getSortedPageable(), resultList.getCount());
    }

    public void export(Object filter, EntityTypeEnum entityType, FileType fileType, String attachmentName) {
        rabbitMqExportReportProducer.send(new ExportReportPayload(filter, entityType, attachmentName, fileType, this.create(entityType, fileType)));
    }
}
