package uz.uzkassa.cts.service;

import org.springframework.data.domain.Page;
import uz.uzkassa.cts.domain.CaseStatusLog;
import uz.uzkassa.cts.dto.casestatuslog.CaseStatusLogDTO;
import uz.uzkassa.cts.dto.casestatuslog.CaseStatusLogListDTO;
import uz.uzkassa.cts.filter.BaseFilter;


public interface CaseStatusLogService extends BaseService<CaseStatusLog, Long> {

    Long create(CaseStatusLogDTO caseStatusLogDTO);

    Page<CaseStatusLogListDTO> getList(BaseFilter filter);
}
