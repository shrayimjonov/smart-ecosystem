package uz.uzkassa.cts.service;

import org.springframework.http.ResponseEntity;
import uz.uzkassa.cts.domain.Customer;
import uz.uzkassa.cts.dto.integration.billing.CustomerPublicOfferResponseDto;

public interface BillingClientService {

    void checkingCustomer(Customer customer);

    void createCustomer(Customer customer);

    ResponseEntity<CustomerPublicOfferResponseDto> customerOffer(String tin);
}
