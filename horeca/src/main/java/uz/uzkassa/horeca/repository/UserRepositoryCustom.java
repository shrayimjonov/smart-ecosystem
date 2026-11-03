package uz.uzkassa.horeca.repository;

import uz.uzkassa.horeca.domain.CompanyCustomer;
import uz.uzkassa.horeca.domain.auth.User;
import uz.uzkassa.horeca.dto.base.ResultList;
import uz.uzkassa.horeca.filters.UserFilter;

public interface UserRepositoryCustom {
    ResultList<User> getResultList(UserFilter filter);

    ResultList<CompanyCustomer> getCustomersResultList(UserFilter filter);
}
