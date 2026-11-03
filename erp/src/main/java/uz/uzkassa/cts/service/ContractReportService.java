package uz.uzkassa.cts.service;

import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.cts.domain.report.ContractReport;
import uz.uzkassa.cts.dto.device.CustomerDeviceDTO;
import uz.uzkassa.cts.dto.payload.ContractReportPayload;
import uz.uzkassa.cts.dto.payload.Payload;
import uz.uzkassa.cts.dto.report.ContractReportDTO;
import uz.uzkassa.cts.dto.report.ContractReportListDTO;
import uz.uzkassa.cts.filter.ContractReportFilter;

public interface ContractReportService extends BaseService<ContractReport, Long> {

    Page<ContractReportListDTO> list(ContractReportFilter filter);

    ContractReportDTO get(Long id);

    Long create(ContractReportDTO dto);

    Long update(ContractReportDTO dto);

    byte[] exportExcel(ContractReportFilter filter);

    Boolean importExcel(MultipartFile file);

    void sync();

    void create(ContractReportPayload payload);

    void createThroughRegister(CustomerDeviceDTO customerDeviceDTO);
}
