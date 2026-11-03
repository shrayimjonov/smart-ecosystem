package uz.uzkassa.cts.service;

import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.cts.domain.exportimport.ImportReport;
import uz.uzkassa.cts.dto.exportimportreport.ImportReportListDTO;
import uz.uzkassa.cts.dto.payload.ImportReportPayload;
import uz.uzkassa.cts.enums.ContentType;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.filter.BaseFilter;

public interface ImportReportService extends BaseService<ImportReport, Long> {

    void create(MultipartFile file, EntityTypeEnum entityType, ContentType contentType);

    void importReportQueue(ImportReportPayload payload);

    Page<ImportReportListDTO> getList(BaseFilter filter);
}
