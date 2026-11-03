package uz.uzkassa.cts.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.cts.domain.Customer;
import uz.uzkassa.cts.integrations.OfdClient;
import uz.uzkassa.cts.repository.CustomerBranchRentRepository;
import uz.uzkassa.cts.repository.CustomerBranchRepository;
import uz.uzkassa.cts.repository.CustomerRepository;
import uz.uzkassa.cts.repository.FileUploadRepository;
import uz.uzkassa.cts.service.OfdCustomerService;

@Transactional(readOnly = true)
@Service
@Slf4j
public class OfdCustomerServiceImpl extends BaseServiceImpl<Customer, Long> implements OfdCustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerBranchRepository customerBranchRepository;
    private final FileUploadRepository fileUploadRepository;
    private final OfdClient ofdClient;
    private final CustomerBranchRentRepository customerBranchRentRepository;


    public OfdCustomerServiceImpl(CustomerRepository customerRepository, CustomerBranchRepository customerBranchRepository, FileUploadRepository fileUploadRepository, OfdClient ofdClient, CustomerBranchRentRepository customerBranchRentRepository) {
        super(customerRepository);
        this.customerRepository = customerRepository;
        this.customerBranchRepository = customerBranchRepository;
        this.fileUploadRepository = fileUploadRepository;
        this.ofdClient = ofdClient;
        this.customerBranchRentRepository = customerBranchRentRepository;
    }



}
