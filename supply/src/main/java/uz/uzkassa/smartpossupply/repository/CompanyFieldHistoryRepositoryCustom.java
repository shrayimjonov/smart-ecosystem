package uz.uzkassa.smartpossupply.repository;

import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.company.CompanyFieldHistory;
import uz.uzkassa.smartpossupply.filters.CompanyFieldHistoryFilter;

import java.util.List;

@Repository
public interface CompanyFieldHistoryRepositoryCustom {

    List<CompanyFieldHistory> lookUp(CompanyFieldHistoryFilter filter);
}