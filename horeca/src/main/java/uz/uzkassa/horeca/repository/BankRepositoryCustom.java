package uz.uzkassa.horeca.repository;

import uz.uzkassa.horeca.domain.Bank;
import uz.uzkassa.horeca.dto.base.ResultList;
import uz.uzkassa.horeca.filters.BaseFilter;

public interface BankRepositoryCustom {
    ResultList<Bank> getResultList(BaseFilter filter);
}
