package uz.uzkassa.horeca.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.horeca.domain.CompanyCustomer;
import uz.uzkassa.horeca.domain.auth.User;
import uz.uzkassa.horeca.domain.order.Order;
import uz.uzkassa.horeca.dto.base.CommonDTO;
import uz.uzkassa.horeca.dto.base.ResultList;
import uz.uzkassa.horeca.dto.client.CustomerDetailCabinetDTO;
import uz.uzkassa.horeca.dto.client.CustomerListCabinetDTO;
import uz.uzkassa.horeca.dto.reservation.CustomerDTO;
import uz.uzkassa.horeca.dto.reservation.CustomerDetailDTO;
import uz.uzkassa.horeca.enums.Role;
import uz.uzkassa.horeca.filters.UserFilter;
import uz.uzkassa.horeca.repository.CompanyCustomerRepository;
import uz.uzkassa.horeca.repository.OrderRepository;
import uz.uzkassa.horeca.repository.UserRepository;
import uz.uzkassa.horeca.utils.SecurityUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class CustomerService extends BaseService {

    UserRepository userRepository;
    PasswordEncoder passwordEncoder;
    OrderRepository orderRepository;
    CompanyCustomerRepository companyCustomerRepository;

    @Transactional
    public User createEntity(CustomerDTO customerDTO) {
        User customer = userRepository.findFirstByLoginAndDeletedIsFalse(customerDTO.getPhone()).orElse(null);
        if (customer == null) {
            customer = new User();
            customer.setRole(Role.ROLE_CLIENT);
            customer.setFirstName(customerDTO.getName());
            customer.setPhone(customerDTO.getPhone());
            customer.setLogin(customerDTO.getPhone());
            customer.setPassword(passwordEncoder.encode(SecurityUtils.generateActivationCode()));
            customer.setFirstVisitDate(LocalDateTime.now());
        }
        if (customerDTO.getGender() != null) {
            customer.setGender(customerDTO.getGender());
        }
        if (customerDTO.getAgeGroup() != null) {
            customer.setAgeGroup(customerDTO.getAgeGroup());
        }
        customer.setGuestName(customerDTO.getName());
        userRepository.save(customer);
        if (!companyCustomerRepository.existsByCompanyIdAndCustomerIdAndBranchId(getCurrentCompanyId(), customer.getId(), Optional.ofNullable(customerDTO.getBranchId()).orElse(getCurrentBranchId()))) {
            CompanyCustomer companyCustomer = new CompanyCustomer();
            companyCustomer.setCustomerId(customer.getId());
            companyCustomer.setCompanyId(getCurrentCompanyId());
            companyCustomer.setBranchId(Optional.ofNullable(customerDTO.getBranchId()).orElse(getCurrentBranchId()));
            companyCustomerRepository.save(companyCustomer);
        }
        cacheService.clearUserCache(customer.getLogin());
        return customer;
    }

    @Transactional
    public Long create(CustomerDTO customerDTO) {
        return this.createEntity(customerDTO).getId();
    }

    public Page<CustomerListCabinetDTO> getListForCabinet(UserFilter filter) {
        ResultList<CompanyCustomer> resultList = userRepository.getCustomersResultList(filter);
        List<CustomerListCabinetDTO> result = resultList
                .getList()
                .stream()
                .map(companyCustomer -> {
                    CustomerListCabinetDTO clientListDTO = new CustomerListCabinetDTO();
                    clientListDTO.setId(companyCustomer.getCustomer().getId());
                    clientListDTO.setName(companyCustomer.getCustomer().getName());
                    clientListDTO.setPhone(companyCustomer.getCustomer().getPhone());
                    clientListDTO.setTotalSpent(BigDecimal.ZERO);
                    clientListDTO.setAgeGroup(companyCustomer.getCustomer().getAgeGroup());
                    clientListDTO.setGender(companyCustomer.getCustomer().getGender());
                    clientListDTO.setFirstVisitDate(companyCustomer.getCustomer().getFirstVisitDate());
                    return clientListDTO;
                })
                .collect(Collectors.toList());
        return new PageImpl<>(result, filter.getSortedPageable(), resultList.getCount());
    }

    public CustomerDetailCabinetDTO getForCabinet(Long id) {
        return userRepository.findById(id)
                .map(customer -> {
                    CustomerDetailCabinetDTO customerDetailCabinetDTO = new CustomerDetailCabinetDTO();
                    customerDetailCabinetDTO.setId(customer.getId());
                    customerDetailCabinetDTO.setName(customer.getName());
                    customerDetailCabinetDTO.setPhone(customer.getPhone());
                    customerDetailCabinetDTO.setTotalSpent(BigDecimal.ZERO);
                    customerDetailCabinetDTO.setAgeGroup(customer.getAgeGroup());
                    customerDetailCabinetDTO.setGender(customer.getGender());
                    customerDetailCabinetDTO.setFirstVisitDate(customer.getFirstVisitDate());
                    return customerDetailCabinetDTO;
                }).orElseThrow(notFoundExceptionThrow(User.class.getSimpleName(), "id", id));
    }

    @Transactional
    public Long update(Long id, CustomerDTO customerDetailDTO) {
        return userRepository.findById(id)
                .map(client -> {
                    client.setPhone(customerDetailDTO.getPhone());
                    client.setLogin(customerDetailDTO.getPhone());
                    client.setPassword(passwordEncoder.encode(SecurityUtils.generateActivationCode()));
                    client.setGuestName(customerDetailDTO.getName());
                    userRepository.save(client);
                    cacheService.clearUserCache(client.getLogin());
                    return client.getId();
                }).orElseThrow(notFoundExceptionThrow(User.class.getSimpleName(), "id", id));
    }

    @Transactional
    public Long assign(Long orderId, CustomerDTO customerDTO) {
        return orderRepository.findById(orderId)
                .map(order -> {
                    customerDTO.setBranchId(order.getBranchId());
                    User customer = this.createEntity(customerDTO);
                    order.setClientId(customer.getId());
                    orderRepository.save(order);
                    return order.getId();
                }).orElseThrow(notFoundExceptionThrow(Order.class.getSimpleName(), "id", orderId));
    }


    @Transactional
    public void delete(Long id) {
        userRepository.deleteById(id);
    }

    public List<CommonDTO> lookUp(UserFilter filter) {
        List<CommonDTO> list = new ArrayList<>();
        for (CompanyCustomer companyCustomer : userRepository.getCustomersResultList(filter).getList()) {
            CommonDTO commonDTO = companyCustomer.getCustomer().toCommonDTO();
            list.add(commonDTO);
        }
        return list;
    }
}
