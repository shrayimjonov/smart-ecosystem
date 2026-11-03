package uz.uzkassa.cts.service.impl;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import uz.uzkassa.cts.domain.CaseStatusLog;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.dto.casestatuslog.CaseStatusLogDTO;
import uz.uzkassa.cts.dto.casestatuslog.CaseStatusLogListDTO;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.repository.CaseStatusLogRepository;
import uz.uzkassa.cts.service.CaseStatusLogService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class CaseStatusLogServiceImpl extends BaseServiceImpl<CaseStatusLog, Long> implements CaseStatusLogService {

    CaseStatusLogRepository caseStatusLogRepository;

    public CaseStatusLogServiceImpl(CaseStatusLogRepository caseStatusLogRepository) {
        super(caseStatusLogRepository);
        this.caseStatusLogRepository = caseStatusLogRepository;
    }

    @Override
    public Long create(CaseStatusLogDTO caseStatusLogDTO) {
        CaseStatusLog caseStatusLog = new CaseStatusLog();
        caseStatusLog.setCaseId(caseStatusLogDTO.getCaseId());
        caseStatusLog.setStatus(caseStatusLogDTO.getStatus());
        return caseStatusLogRepository.save(caseStatusLog).getId();
    }

    @Override
    public Page<CaseStatusLogListDTO> getList(BaseFilter filter) {
        ResultList<CaseStatusLog> resultList = caseStatusLogRepository.getResultList(filter);
        List<CaseStatusLogListDTO> result = resultList
                .getList()
                .stream()
                .map(caseStatusLog -> {
                    CaseStatusLogListDTO caseStatusLogListDTO = new CaseStatusLogListDTO();
                    caseStatusLogListDTO.setCases(caseStatusLog.getCases().toCommonDTO());
                    caseStatusLogListDTO.setStatus(caseStatusLog.getStatus());
                    caseStatusLogListDTO.setCreatedDate(caseStatusLog.getCreatedDate());
                    return caseStatusLogListDTO;
                })
                .collect(Collectors.toList());
        return new PageImpl<>(result, filter.getOrderedPageable(), resultList.getCount());
    }

}

