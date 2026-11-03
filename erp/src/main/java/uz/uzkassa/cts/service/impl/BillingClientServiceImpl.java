package uz.uzkassa.cts.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import uz.uzkassa.cts.domain.Customer;
import uz.uzkassa.cts.dto.integration.billing.BillingCustomerRequestDTO;
import uz.uzkassa.cts.dto.integration.billing.CustomerPublicOfferCtoIdRequestDto;
import uz.uzkassa.cts.dto.integration.billing.CustomerPublicOfferResponseDto;
import uz.uzkassa.cts.dto.integration.billing.PublicOfferResponseDto;
import uz.uzkassa.cts.integrations.BillingServiceFeignClient;
import uz.uzkassa.cts.repository.CustomerBankRepository;
import uz.uzkassa.cts.repository.CustomerRepository;
import uz.uzkassa.cts.service.BillingClientService;
import uz.uzkassa.cts.web.rest.errors.BadRequestException;
import uz.uzkassa.cts.web.rest.errors.NotFoundException;

@Slf4j
@Service
@AllArgsConstructor
public class BillingClientServiceImpl implements BillingClientService {

    private final BillingServiceFeignClient billingServiceFeignClient;
    private final CustomerBankRepository customerBankRepository;
    private final CustomerRepository customerRepository;

    @Override
    public void checkingCustomer(Customer customer) {
        try {
            ResponseEntity<Object> responseEntity = billingServiceFeignClient.customerCheck(customer.getTin());
            System.out.println("------------ Customer check response ---------------------");
            try {
                System.out.println(new ObjectMapper().writeValueAsString(responseEntity.getBody()));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        } catch (NotFoundException e) {
            BillingCustomerRequestDTO requestDto = new BillingCustomerRequestDTO();
            requestDto.setInn(customer.getTin());
            requestDto.setName(customer.getName());
            requestDto.setPhone(customer.getPhone());
            if (customer.getAddress() != null) {
                requestDto.setApartment(customer.getAddress().getApartment());
                requestDto.setStreet(customer.getAddress().getStreet());
                requestDto.setHouse(customer.getAddress().getHouse());
                if (customer.getAddress().getDistrict() != null) {
                    requestDto.setDistrict(customer.getAddress().getDistrict().getName());
                }
                if (customer.getAddress().getRegion() != null) {
                    requestDto.setRegion(customer.getAddress().getRegion().getName());
                }
            }
            customerBankRepository.findFirstByCustomerIdAndDefaultIsTrue(customer.getId())
                    .ifPresent(customerBank -> {
                        requestDto.setCustomerOKED(customerBank.getOked());
                        requestDto.setAccountNumber(customerBank.getAccountNumber());
                    });
            billingServiceFeignClient.createCustomer(requestDto);
        }
    }

    @Override
    public void createCustomer(Customer customer) {
        ResponseEntity<PublicOfferResponseDto> activeOffer = billingServiceFeignClient.getLastActiveOffer();
        if (activeOffer.getStatusCode().is2xxSuccessful() && activeOffer.getBody() != null) {
            CustomerPublicOfferCtoIdRequestDto requestDto = new CustomerPublicOfferCtoIdRequestDto();
            requestDto.setTin(customer.getTin());
            requestDto.setPublicOfferId(activeOffer.getBody().getId());
            requestDto.setName(customer.getCompanyName());
            if (customer.getAddress() != null) {
                requestDto.setApartment(customer.getAddress().getApartment());
                if (customer.getAddress().getDistrict() != null) {
                    requestDto.setDistrict(customer.getAddress().getDistrict().getName());
                }
                if (customer.getAddress().getRegion() != null) {
                    requestDto.setRegion(customer.getAddress().getRegion().getName());
                }
                requestDto.setStreet(customer.getAddress().getStreet());
                requestDto.setHouse(customer.getAddress().getHouse());
                requestDto.setCustomerOKED(customer.getOked());
                if (customer.getCustomerBank() != null) {
                    requestDto.setAccountNumber(customer.getCustomerBank().getAccountNumber());
                }
                requestDto.setPhone(customer.getPhone());
            }
            billingServiceFeignClient.createCustomerOffer(requestDto);
        } else {
            throw new BadRequestException("Billing response is not successfully");
        }
    }

    @Override
    public ResponseEntity<CustomerPublicOfferResponseDto> customerOffer(String tin) {
        try {
            return billingServiceFeignClient.getCustomerOfferByTin(tin);
        } catch (NotFoundException e) {
            this.createCustomer(customerRepository.findByTin(tin));
            return billingServiceFeignClient.getCustomerOfferByTin(tin);
        }
    }
}
