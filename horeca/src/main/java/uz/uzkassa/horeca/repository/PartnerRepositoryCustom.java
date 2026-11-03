package uz.uzkassa.horeca.repository;

import uz.uzkassa.horeca.domain.company.Partner;
import uz.uzkassa.horeca.dto.base.ResultList;
import uz.uzkassa.horeca.filters.CompanyFilter;

public interface PartnerRepositoryCustom {
    ResultList<Partner> getResultList(CompanyFilter filter, boolean isSupplier);
}
