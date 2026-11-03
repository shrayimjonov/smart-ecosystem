package uz.uzkassa.cts.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.cts.domain.Branch;
import uz.uzkassa.cts.domain.Customer;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.dto.customer.CustomerLookUpDTO;
import uz.uzkassa.cts.enums.Role;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.repository.CustomerRepository;
import uz.uzkassa.cts.repository.UserRepository;
import uz.uzkassa.cts.security.SecurityUtils;
import uz.uzkassa.cts.service.BranchCustomerService;

@Transactional(readOnly = true)
@Service
public class BranchCustomerServiceImpl extends BaseServiceImpl<Customer, Long> implements BranchCustomerService {

    private final CustomerRepository customerRepository;

    public BranchCustomerServiceImpl(CustomerRepository customerRepository,
                                     UserRepository userRepository) {
        super(customerRepository);
        this.customerRepository = customerRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Page<CustomerLookUpDTO> lookUp(BaseFilter filter) {
        Long branchId = SecurityUtils.getBranchId();
        if (!SecurityUtils.isCurrentUserInRoles(Role.SUPER_ADMIN.getRoleCode(), Role.ADMIN.getRoleCode())) {
            if (branchId == null) {
                throw validationService.entityNotFoundExceptionThrow(BRANCH).get();
            }
        }
        filter.setBranchId(branchId);
        ResultList<CustomerLookUpDTO> resultList = customerRepository.getLookUpBranchCustomer(filter);
        return new PageImpl<>
                (resultList.getList(), filter.getOrderedPageable(), resultList.getCount());
    }
}
