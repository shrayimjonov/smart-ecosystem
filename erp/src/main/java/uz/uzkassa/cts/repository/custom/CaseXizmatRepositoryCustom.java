package uz.uzkassa.cts.repository.custom;

import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.cases.CaseXizmat;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.filter.CaseXizmatFilter;

@Repository
public interface CaseXizmatRepositoryCustom {

    ResultList<CaseXizmat> getResultList(CaseXizmatFilter filter);
}
