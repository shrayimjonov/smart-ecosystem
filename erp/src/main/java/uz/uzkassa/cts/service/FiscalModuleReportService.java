package uz.uzkassa.cts.service;

import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.cts.domain.FiscalModuleReport;
import uz.uzkassa.cts.dto.fm.FiscalModuleReportRequestDTO;
import uz.uzkassa.cts.dto.fm.FiscalModuleReportListDTO;
import uz.uzkassa.cts.dto.payload.Payload;
import uz.uzkassa.cts.enums.ArrivalStatus;
import uz.uzkassa.cts.enums.FMReportReason;
import uz.uzkassa.cts.filter.FiscalModuleReportFilter;

public interface FiscalModuleReportService extends BaseService<FiscalModuleReport, Long> {

    Page<FiscalModuleReportListDTO> getList(FiscalModuleReportFilter filter);

    Long updateStatus(Long id, ArrivalStatus arrivalStatus, FMReportReason reason);

    Long create(FiscalModuleReportRequestDTO dto);

    byte[] importFiscalModulesFromExcel(MultipartFile file);

    void create(Payload payload);

    void importFromExcel(MultipartFile file);
}
