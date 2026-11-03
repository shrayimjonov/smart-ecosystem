package uz.uzkassa.cts.service;

import org.springframework.data.domain.Page;
import uz.uzkassa.cts.domain.cases.CaseTypeAccess;
import uz.uzkassa.cts.dto.caseTypeAccess.CaseTypeAccessDTO;
import uz.uzkassa.cts.dto.caseTypeAccess.CaseTypeAccessListDTO;
import uz.uzkassa.cts.enums.CaseType;
import uz.uzkassa.cts.filter.BaseFilter;

import java.util.EnumSet;

public interface CaseTypeAccessService extends BaseService<CaseTypeAccess, Long> {

    void insertUpdate(CaseTypeAccessDTO typeAccessDTO);

    Page<CaseTypeAccessListDTO> list(BaseFilter filter);

    EnumSet<CaseType> caseTypesByUserId();
}
