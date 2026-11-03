package uz.uzkassa.cts.service;

import org.springframework.data.domain.Page;
import uz.uzkassa.cts.domain.cases.CaseXizmat;
import uz.uzkassa.cts.dto.caseXizmat.CaseXizmatDTO;
import uz.uzkassa.cts.dto.caseXizmat.CaseXizmatDetailAdminDTO;
import uz.uzkassa.cts.dto.caseXizmat.CaseXizmatListAdminDTO;
import uz.uzkassa.cts.enums.CaseType;
import uz.uzkassa.cts.enums.CaseXizmatType;
import uz.uzkassa.cts.enums.CommonStatus;
import uz.uzkassa.cts.enums.FMReportReason;
import uz.uzkassa.cts.filter.CaseXizmatFilter;

public interface CaseXizmatService extends BaseService<CaseXizmat, Long> {

    Page<CaseXizmatListAdminDTO> getListForAdmin(CaseXizmatFilter filter);

    Long create(CaseXizmatDTO caseTypeXizmatDTO);

    Long update(Long id, CaseXizmatDTO caseTypeXizmatDTO);

    CaseXizmatDetailAdminDTO getForAdmin(Long id);

    CaseXizmat get(CaseType caseType, FMReportReason reason, CaseXizmatType type);

    void updateStatus(Long id, CommonStatus status);

    void updateReason(Long id, FMReportReason reason);
}
