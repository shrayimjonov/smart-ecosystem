package uz.uzkassa.cts.service;

import org.springframework.data.domain.Page;
import uz.uzkassa.cts.domain.cases.CaseTypeEmployees;
import uz.uzkassa.cts.dto.caseTypeEmployees.CaseTypeEmployeesDTO;
import uz.uzkassa.cts.dto.caseTypeEmployees.CaseTypeEmployeesListDTO;
import uz.uzkassa.cts.filter.BaseFilter;

public interface CaseTypeEmployeesService extends BaseService<CaseTypeEmployees, Long> {

    Boolean insertUpdate(CaseTypeEmployeesDTO caseTypeEmployeesDTO);

    Page<CaseTypeEmployeesListDTO> list(BaseFilter filter);
}
