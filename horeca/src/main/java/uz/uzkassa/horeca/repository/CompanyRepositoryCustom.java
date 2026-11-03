package uz.uzkassa.horeca.repository;

import uz.uzkassa.horeca.domain.company.Company;
import uz.uzkassa.horeca.dto.base.ResultList;
import uz.uzkassa.horeca.filters.CompanyFilter;

public interface CompanyRepositoryCustom {
    ResultList<Company> getResultList(CompanyFilter filter);
}
