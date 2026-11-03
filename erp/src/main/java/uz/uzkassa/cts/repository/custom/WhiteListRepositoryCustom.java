package uz.uzkassa.cts.repository.custom;

import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.WhiteList;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.filter.WhiteListFilter;

@Repository
public interface WhiteListRepositoryCustom {

    ResultList<WhiteList> getResultList(WhiteListFilter filter);

}
