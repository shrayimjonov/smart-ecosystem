package uz.uzkassa.cts.service;

import org.springframework.data.domain.Page;
import uz.uzkassa.cts.domain.hrms.Candidate;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.dto.candidate.CandidateDTO;
import uz.uzkassa.cts.dto.candidate.CandidateDetailAdminDTO;
import uz.uzkassa.cts.dto.candidate.CandidateListAdminDTO;
import uz.uzkassa.cts.enums.CandidateStatus;
import uz.uzkassa.cts.filter.BaseFilter;

import java.util.List;

public interface CandidateService extends BaseService<Candidate, Long> {

    Long create(CandidateDTO candidateDTO);

    Long update(Long id, CandidateDTO candidateDTO);

    CandidateDetailAdminDTO getForAdmin(Long id);

    Page<CandidateListAdminDTO> getListForAdmin(BaseFilter filter);

    List<CommonDTO> lookUp(BaseFilter filter);

    void updateStatus(Long id, CandidateStatus status);

}
