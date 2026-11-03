package uz.uzkassa.cts.repository.custom;

import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.dto.caseTypeAccess.CaseTypeAccessListDTO;
import uz.uzkassa.cts.filter.BaseFilter;

@Repository
public interface CaseTypeAccessRepositoryCustom {

    ResultList<CaseTypeAccessListDTO> getResultList(BaseFilter filter);
}
