package uz.uzkassa.cts.repository.custom;

import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.Customer;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.dto.customer.CompanyCustomerListDTO;
import uz.uzkassa.cts.dto.customer.CustomerLookUpDTO;
import uz.uzkassa.cts.dto.customer.CustomerShortDTO;
import uz.uzkassa.cts.dto.sync.SyncCustomerListDTO;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.filter.CustomerFilter;

import java.util.List;

@Repository
public interface CustomerRepositoryCustom {

    ResultList<Customer> getResultList(CustomerFilter filter);

    ResultList<String> getAllToSync(BaseFilter filter);

    ResultList<CustomerShortDTO> getCustomerListByEmployee(BaseFilter filter);

    ResultList<CompanyCustomerListDTO> getCustomerListByEmployee(CustomerFilter filter, boolean isOfdStatusSelected);

    List<String> getTinListInterval(int start, int limit);

    List<String> getPhoneNumberListInterval(int start, int limit);

    List<SyncCustomerListDTO> getCustomerListToSync(BaseFilter filter);

    ResultList<CustomerLookUpDTO> getLookUpBranchCustomer(BaseFilter filter);
}
