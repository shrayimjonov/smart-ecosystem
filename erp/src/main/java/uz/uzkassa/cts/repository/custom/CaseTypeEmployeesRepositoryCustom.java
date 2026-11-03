package uz.uzkassa.cts.repository.custom;

import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.dto.caseTypeEmployees.CaseTypeEmployeesGetDTO;
import uz.uzkassa.cts.dto.caseTypeEmployees.CaseTypeEmployeesListDTO;
import uz.uzkassa.cts.filter.BaseFilter;

import java.util.List;

@Repository
public interface CaseTypeEmployeesRepositoryCustom {

    ResultList<CaseTypeEmployeesListDTO> getResultList(BaseFilter filter);

    List<CaseTypeEmployeesGetDTO> getUsernameListByType(String caseType);
}
