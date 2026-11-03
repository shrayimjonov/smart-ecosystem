package uz.uzkassa.cts.service;

import org.springframework.data.domain.Page;
import uz.uzkassa.cts.domain.Customer;
import uz.uzkassa.cts.dto.auth.CustomerActivationDTO;
import uz.uzkassa.cts.dto.company.CompanyCommonDTO;
import uz.uzkassa.cts.dto.customer.CustomerDTO;
import uz.uzkassa.cts.dto.customer.CustomerQuickDTO;
import uz.uzkassa.cts.dto.customer.admin.CustomerDetailAdminDTO;
import uz.uzkassa.cts.dto.customer.admin.CustomerListAdminDTO;
import uz.uzkassa.cts.dto.customer.admin.CustomerStatisticsAdminDTO;
import uz.uzkassa.cts.dto.customer.cabinet.CustomerDetailCabinetDTO;
import uz.uzkassa.cts.dto.customer.cabinet.CustomerListCabinetDTO;
import uz.uzkassa.cts.dto.user.PhoneDTO;
import uz.uzkassa.cts.enums.CustomerStatus;
import uz.uzkassa.cts.filter.CustomerFilter;

import java.util.List;

public interface CustomerService extends BaseService<Customer, Long> {

    Long create(CustomerDTO customerDTO);

    Long quickRegister(CustomerQuickDTO customerQuickDTO);

    Customer createEntity(CustomerDTO customerDTO);

    Long update(Long id, CustomerDTO customerDTO);

    Customer updateEntity(Long id, CustomerDTO customerDTO);

    CustomerDetailAdminDTO getForAdmin(Long id);

    CustomerDetailCabinetDTO getForCabinet(Long id);

    Page<CustomerListAdminDTO> getListForAdmin(CustomerFilter filter);

    Page<CustomerListCabinetDTO> getListForCabinet(CustomerFilter filter);

    List<CompanyCommonDTO> lookUp(CustomerFilter filter);

    void updateStatus(Long id, CustomerStatus status);

    String getCustomerByTinFromNic(String tin);

    void attachManager(String tin, Long managerId);

    Customer getByTin(String tin);

    void updatePhone(Long id, PhoneDTO phoneDTO);

    void whiteList(Long id, boolean whiteList);

    void sendActivationCode(Long id);

    void activate(CustomerActivationDTO customerActivationDTO);

    CustomerStatisticsAdminDTO getStatistics(Long id);
}
