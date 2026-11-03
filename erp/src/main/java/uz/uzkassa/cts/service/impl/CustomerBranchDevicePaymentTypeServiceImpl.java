package uz.uzkassa.cts.service.impl;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.cts.domain.CustomerBranchDevicePaymentType;
import uz.uzkassa.cts.dto.customerBranchDevicePaymentType.CustomerBranchDevicePaymentTypeDTO;
import uz.uzkassa.cts.repository.CustomerBranchDevicePaymentTypeRepository;
import uz.uzkassa.cts.service.CustomerBranchDevicePaymentTypeService;

@Transactional
@Service
public class CustomerBranchDevicePaymentTypeServiceImpl extends BaseServiceImpl<CustomerBranchDevicePaymentType, Long> implements CustomerBranchDevicePaymentTypeService {

    private final CustomerBranchDevicePaymentTypeRepository customerBranchDevicePaymentTypeRepository;

    public CustomerBranchDevicePaymentTypeServiceImpl(CustomerBranchDevicePaymentTypeRepository customerBranchDevicePaymentTypeRepository) {
        super(customerBranchDevicePaymentTypeRepository);
        this.customerBranchDevicePaymentTypeRepository = customerBranchDevicePaymentTypeRepository;
    }

    @Override
    public void create(Long customerBranchDeviceId) {
        CustomerBranchDevicePaymentType customerBranchDevicePaymentType = new CustomerBranchDevicePaymentType();
        customerBranchDevicePaymentType.setCustomerBranchDeviceId(customerBranchDeviceId);
        save(customerBranchDevicePaymentType);
    }

    @Override
    public void update(CustomerBranchDevicePaymentTypeDTO customerBranchDevicePaymentTypeDTO) {
        if (CollectionUtils.isNotEmpty(customerBranchDevicePaymentTypeDTO.getPaymentTypes())) {
            customerBranchDevicePaymentTypeRepository.deleteAllByCustomerBranchDeviceId(customerBranchDevicePaymentTypeDTO.getCustomerBranchDeviceId());
            customerBranchDevicePaymentTypeRepository.flush();
            customerBranchDevicePaymentTypeDTO.getPaymentTypes().forEach(paymentType -> {
                CustomerBranchDevicePaymentType entity = new CustomerBranchDevicePaymentType();
                entity.setCustomerBranchDeviceId(customerBranchDevicePaymentTypeDTO.getCustomerBranchDeviceId());
                entity.setPaymentType(paymentType);
                save(entity);
            });
        }
    }
}
