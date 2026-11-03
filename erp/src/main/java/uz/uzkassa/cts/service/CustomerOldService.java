package uz.uzkassa.cts.service;

import org.springframework.data.domain.Page;
import uz.uzkassa.cts.domain.Customer;
import uz.uzkassa.cts.dto.apay.ApayCompanyEnableDTO;
import uz.uzkassa.cts.dto.auth.ActivateDTO;
import uz.uzkassa.cts.dto.customer.*;
import uz.uzkassa.cts.dto.payload.CustomerPayload;
import uz.uzkassa.cts.dto.soliq.SoliqCompanyDTO;
import uz.uzkassa.cts.dto.sync.SyncCustomerListDTO;
import uz.uzkassa.cts.enums.CustomerLegalStatus;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.filter.CustomerFilter;

import java.util.List;

public interface CustomerOldService extends BaseService<Customer, Long> {

    Page<CustomerShortDTO> listByEmployee(BaseFilter filter);

    List<String> getTinListInterval(int start, int limit);

    List<String> getPhoneNumberListInterval(int start, int limit);

    void activate(ActivateDTO activateDTO);

    Page<CompanyCustomerListDTO> getAll(CustomerFilter filter);

    Long getListCountToSync();

    Page<SyncCustomerListDTO> getListToSync(BaseFilter filter);

    void whiteList(WhiteListDTO whiteListDTO);

    void syncApay(ApayCompanyEnableDTO apayCompanyEnableDTO);

    void updateAppType(ChangeAppTypeRequestDto dto);

    void updateAppType(Long id, String appType);

    void updateCustomerStatus(Long id, CustomerLegalStatus status);

    void updateCustomerStatusByPayment(String tin);

    Page<String> getAllToSync(BaseFilter filter);

    SoliqCompanyDTO check(String tin);

    void syncTax(CustomerPayload payload);

    void syncByCustomerTin();

    void updateAPayTariff(UpdateCustomerAPayTariffDTO dto);

    void sendPublicOffer(Long customerId);

    void verifyPublicOffer(PublicOfferVerifyRequestDto code);

    void attachmentBankTariff(Long id, String serialNumber);

    CustomerResponseDTO customerStatus(String tin);
}
