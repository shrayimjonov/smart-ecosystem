package uz.uzkassa.cts.service.impl;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.cts.domain.CustomerBranch;
import uz.uzkassa.cts.domain.CustomerBranchApplication;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.dto.customerRequest.CustomerRequestDTO;
import uz.uzkassa.cts.dto.customerbranchapplication.admin.CustomerBranchApplicationDetailAdminDTO;
import uz.uzkassa.cts.dto.customerbranchapplication.admin.CustomerBranchApplicationListAdminDTO;
import uz.uzkassa.cts.dto.customerbranchapplication.base.CustomerBranchApplicationDTO;
import uz.uzkassa.cts.dto.customerbranchapplication.base.CustomerBranchApplicationFieldDTO;
import uz.uzkassa.cts.dto.customerbranchapplication.base.CustomerBranchApplicationUpdateDTO;
import uz.uzkassa.cts.dto.customerbranchapplication.cabinet.CustomerBranchApplicationDetailCabinetDTO;
import uz.uzkassa.cts.dto.customerbranchapplication.cabinet.CustomerBranchApplicationListCabinetDTO;
import uz.uzkassa.cts.enums.CustomerBranchApplicationStatus;
import uz.uzkassa.cts.enums.CustomerRequestType;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.repository.CustomerBranchApplicationRepository;
import uz.uzkassa.cts.service.CustomerBranchApplicationService;
import uz.uzkassa.cts.service.CustomerBranchService;
import uz.uzkassa.cts.service.CustomerRequestService;
import uz.uzkassa.cts.validator.ValidationContext;

import java.util.List;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@Service
public class CustomerBranchApplicationServiceImpl extends BaseServiceImpl<CustomerBranchApplication, Long> implements CustomerBranchApplicationService {

    private final CustomerBranchApplicationRepository customerBranchApplicationRepository;
    private final CustomerRequestService customerRequestService;
    private final CustomerBranchService customerBranchService;
    private final ValidationContext validationContext;

    public CustomerBranchApplicationServiceImpl(CustomerBranchApplicationRepository customerBranchApplicationRepository,
                                                CustomerRequestService customerRequestService, CustomerBranchService customerBranchService, ValidationContext validationContext) {
        super(customerBranchApplicationRepository);
        this.customerBranchApplicationRepository = customerBranchApplicationRepository;
        this.customerRequestService = customerRequestService;
        this.customerBranchService = customerBranchService;
        this.validationContext = validationContext;
    }

    @Override
    public Page<CustomerBranchApplicationListAdminDTO> getListForAdmin(BaseFilter filter) {
        ResultList<CustomerBranchApplication> resultList = customerBranchApplicationRepository.getResultList(filter);
        List<CustomerBranchApplicationListAdminDTO> result = resultList
                .getList()
                .stream()
                .map(customerBranchApplication -> {
                    CustomerBranchApplicationListAdminDTO customerBranchApplicationListDTO = new CustomerBranchApplicationListAdminDTO();
                    customerBranchApplicationListDTO.setId(customerBranchApplication.getId());
                    customerBranchApplicationListDTO.setManager(customerBranchApplication.getManager().toCommonDTO());
                    customerBranchApplicationListDTO.setCustomerBranch(customerBranchApplication.getCustomerBranch().toCommonDTO());
                    customerBranchApplicationListDTO.setCustomer(customerBranchApplication.getCustomerBranch().getCustomer().toShortDTO());
                    if (customerBranchApplication.getCustomerBranch().getAddress() != null) {
                        if (customerBranchApplication.getCustomerBranch().getAddress().getRegion() != null)
                            customerBranchApplicationListDTO.setRegion(customerBranchApplication.getCustomerBranch().getAddress().getRegion().toCommonDTO());
                        if (customerBranchApplication.getCustomerBranch().getAddress().getDistrict() != null)
                            customerBranchApplicationListDTO.setDistrict(customerBranchApplication.getCustomerBranch().getAddress().getDistrict().toCommonDTO());
                    }
                    customerBranchApplicationListDTO.setStatus(customerBranchApplication.getStatus());
                    if (customerBranchApplication.getReason() != null) {
                        customerBranchApplicationListDTO.setReason(customerBranchApplication.getReason().toCommonDTO());
                    }
                    customerBranchApplicationListDTO.setCreatedDate(customerBranchApplication.getCreatedDate());
                    return customerBranchApplicationListDTO;
                })
                .collect(Collectors.toList());
        return new PageImpl<>(result, filter.getOrderedPageable(), resultList.getCount());
    }

    @Override
    public Page<CustomerBranchApplicationListCabinetDTO> getListForCabinet(BaseFilter filter) {
        filter.setManagerId(getCurrentUserId());
        ResultList<CustomerBranchApplication> resultList = customerBranchApplicationRepository.getResultList(filter);
        List<CustomerBranchApplicationListCabinetDTO> result = resultList
                .getList()
                .stream()
                .map(customerBranchApplication -> {
                    CustomerBranchApplicationListCabinetDTO customerBranchApplicationListDTO = new CustomerBranchApplicationListCabinetDTO();
                    customerBranchApplicationListDTO.setId(customerBranchApplication.getId());
                    customerBranchApplicationListDTO.setCustomerBranch(customerBranchApplication.getCustomerBranch().toCommonDTO());
                    customerBranchApplicationListDTO.setCustomer(customerBranchApplication.getCustomerBranch().getCustomer().toShortDTO());
                    if (customerBranchApplication.getCustomerBranch().getAddress() != null) {
                        if (customerBranchApplication.getCustomerBranch().getAddress().getRegion() != null)
                            customerBranchApplicationListDTO.setRegion(customerBranchApplication.getCustomerBranch().getAddress().getRegion().toCommonDTO());
                        if (customerBranchApplication.getCustomerBranch().getAddress().getDistrict() != null)
                            customerBranchApplicationListDTO.setDistrict(customerBranchApplication.getCustomerBranch().getAddress().getDistrict().toCommonDTO());
                    }
                    customerBranchApplicationListDTO.setStatus(customerBranchApplication.getStatus());
                    if (customerBranchApplication.getReason() != null) {
                        customerBranchApplicationListDTO.setReason(customerBranchApplication.getReason().toCommonDTO());
                    }
                    customerBranchApplicationListDTO.setCreatedDate(customerBranchApplication.getCreatedDate());
                    return customerBranchApplicationListDTO;
                })
                .collect(Collectors.toList());
        return new PageImpl<>(result, filter.getOrderedPageable(), resultList.getCount());
    }

    @Override
    @Transactional
    public Long create(CustomerBranchApplicationDTO customerBranchApplicationDTO) {
        validationContext.validate(customerBranchApplicationDTO);
        CustomerBranch customerBranch = customerBranchService.findById(customerBranchApplicationDTO.getCustomerBranchId());
        CustomerBranchApplication customerBranchApplication = new CustomerBranchApplication();
        customerBranchApplication.setCustomerBranchId(customerBranchApplicationDTO.getCustomerBranchId());
        customerBranchApplication.setManagerId(getCurrentUserId());
        customerBranchApplication.setFields(customerBranchApplicationDTO.getFields());
        customerBranchApplicationRepository.save(customerBranchApplication);

        String phone = customerBranch.getPhone();
        for (CustomerBranchApplicationFieldDTO customerBranchApplicationFieldDTO : customerBranchApplicationDTO.getFields()) {
            if ("phone".equals(customerBranchApplicationFieldDTO.getFieldName())) {
                phone = customerBranchApplicationFieldDTO.getNewValue();
            }
        }
        CustomerRequestDTO customerRequestDTO = new CustomerRequestDTO();
        customerRequestDTO.setCustomerId(customerBranch.getCustomerId());
        customerRequestDTO.setInfo(phone);
        customerRequestDTO.setRequestType(CustomerRequestType.PROMO_MANAGER);
        customerRequestService.create(customerRequestDTO);

        return customerBranchApplication.getId();
    }

    @Override
    @Transactional
    public Long update(Long id, CustomerBranchApplicationDTO customerBranchApplicationDTO) {
        customerBranchApplicationDTO.setId(id);
        validationContext.validate(customerBranchApplicationDTO);
        return customerBranchApplicationRepository.findById(id).map(customerBranchApplication -> {
            customerBranchApplication.setCustomerBranchId(customerBranchApplicationDTO.getCustomerBranchId());
            customerBranchApplication.setFields(customerBranchApplicationDTO.getFields());
            customerBranchApplicationRepository.save(customerBranchApplication);
            return customerBranchApplication.getId();
        }).orElseThrow(validationService.entityNotFoundExceptionThrow(APPLICATION));
    }

    @Override
    public CustomerBranchApplicationDetailCabinetDTO getForCabinet(Long id) {
        return customerBranchApplicationRepository.findById(id).map(customerBranchApplication -> {
            CustomerBranchApplicationDetailCabinetDTO customerBranchApplicationDetailDTO = new CustomerBranchApplicationDetailCabinetDTO();
            customerBranchApplicationDetailDTO.setId(customerBranchApplication.getId());
            customerBranchApplicationDetailDTO.setCustomerBranch(customerBranchApplication.getCustomerBranch().toCommonDTO());
            customerBranchApplicationDetailDTO.setFields(customerBranchApplication.getFields());
            return customerBranchApplicationDetailDTO;
        }).orElseThrow(validationService.entityNotFoundExceptionThrow(APPLICATION));
    }

    @Override
    public CustomerBranchApplicationDetailAdminDTO getForAdmin(Long id) {
        return customerBranchApplicationRepository.findById(id).map(customerBranchApplication -> {
            CustomerBranchApplicationDetailAdminDTO customerBranchApplicationDetailDTO = new CustomerBranchApplicationDetailAdminDTO();
            customerBranchApplicationDetailDTO.setId(customerBranchApplication.getId());
            customerBranchApplicationDetailDTO.setCustomerBranch(customerBranchApplication.getCustomerBranch().toCommonDTO());
            customerBranchApplicationDetailDTO.setFields(customerBranchApplication.getFields());
            if (customerBranchApplication.getReason() != null) {
                customerBranchApplicationDetailDTO.setReason(customerBranchApplication.getReason().toCommonDTO());
            }
            return customerBranchApplicationDetailDTO;
        }).orElseThrow(validationService.entityNotFoundExceptionThrow(APPLICATION));
    }

    @Override
    @Transactional
    public void updateStatus(Long id, CustomerBranchApplicationUpdateDTO dto) {
        customerBranchApplicationRepository
                .findById(id)
                .map(customerBranchApplication -> {
                    customerBranchApplication.setStatus(CustomerBranchApplicationStatus.valueOf(dto.getStatus()));
                    customerBranchApplication.setReasonId(dto.getReasonId());
                    customerBranchApplicationRepository.save(customerBranchApplication);
                    return customerBranchApplication.getId();
                })
                .orElseThrow(validationService.entityNotFoundExceptionThrow(APPLICATION));
    }
}
