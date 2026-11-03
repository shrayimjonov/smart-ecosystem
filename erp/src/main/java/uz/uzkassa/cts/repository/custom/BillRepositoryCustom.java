package uz.uzkassa.cts.repository.custom;

import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.Bill;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.filter.BillFilter;

@Repository
public interface BillRepositoryCustom {

    ResultList<Bill> getResultList(BillFilter filter);
}
