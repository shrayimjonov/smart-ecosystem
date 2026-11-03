package uz.uzkassa.cts.service;

import org.springframework.data.domain.Page;
import uz.uzkassa.cts.domain.exportimport.ExportReport;
import uz.uzkassa.cts.dto.exportimportreport.ExportReportListDTO;
import uz.uzkassa.cts.dto.payload.ExportReportPayload;
import uz.uzkassa.cts.enums.ContentType;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.filter.BaseFilter;

public interface ExportReportService extends BaseService<ExportReport, Long> {

    void export(Object filter, EntityTypeEnum entityType, ContentType contentType);

    void generateReport(ExportReportPayload payload);

    Page<ExportReportListDTO> getList(BaseFilter filter);
}
