package uz.uzkassa.cts.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.cts.domain.Customer;
import uz.uzkassa.cts.domain.customer.CustomerRequest;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.dto.customer.CustomerShortDTO;
import uz.uzkassa.cts.dto.customerRequest.CustomerRequestDTO;
import uz.uzkassa.cts.dto.customerRequest.admin.CustomerRequestDetailAdminDTO;
import uz.uzkassa.cts.dto.customerRequest.admin.CustomerRequestListAdminDTO;
import uz.uzkassa.cts.dto.note.NoteDTO;
import uz.uzkassa.cts.enums.CallStatus;
import uz.uzkassa.cts.enums.CustomerRequestStatus;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.filter.CustomerRequestFilter;
import uz.uzkassa.cts.repository.CustomerRequestRepository;
import uz.uzkassa.cts.service.CustomerRequestService;
import uz.uzkassa.cts.service.NoteService;
import uz.uzkassa.cts.validator.ValidationContext;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@Service
public class CustomerRequestServiceImpl extends BaseServiceImpl<CustomerRequest, Long> implements CustomerRequestService {

    private final CustomerRequestRepository customerRequestRepository;
    private final NoteService noteService;
    private final ValidationContext validationContext;

    public CustomerRequestServiceImpl(CustomerRequestRepository customerRequestRepository,
                                      NoteService noteService, ValidationContext validationContext) {
        super(customerRequestRepository);
        this.customerRequestRepository = customerRequestRepository;
        this.noteService = noteService;
        this.validationContext = validationContext;
    }

    @Override
    @Transactional
    public Long create(CustomerRequestDTO customerRequestDTO) {
        validationContext.validate(customerRequestDTO);
        CustomerRequest customerRequest = new CustomerRequest();
        customerRequest.setRequestType(customerRequestDTO.getRequestType());
        customerRequest.setCustomerId(customerRequestDTO.getCustomerId());
        return save(customerRequest).getId();
    }

    @Override
    @Transactional
    public Long update(Long id, CustomerRequestDTO customerRequestDTO) {
        return customerRequestRepository
                .findById(id)
                .map(customerRequest -> {
                    customerRequest.setCallStatus(customerRequestDTO.getCallStatus());
                    customerRequest.setStatus(customerRequestDTO.getStatus());
                    customerRequest.setResolvedDate(LocalDateTime.now());
                    save(customerRequest);
                    if (StringUtils.isNotEmpty(customerRequestDTO.getDescription())) {
                        NoteDTO noteDTO = new NoteDTO();
                        noteDTO.setEntityId(id);
                        noteDTO.setEntityType(EntityTypeEnum.CUSTOMER_REQUEST);
                        noteDTO.setDescription(customerRequestDTO.getDescription());
                        noteService.create(noteDTO);
                    }
                    return id;
                }).orElseThrow(validationService.entityNotFoundExceptionThrow(REQUEST));
    }

    @Override
    public Page<CustomerRequestListAdminDTO> getList(CustomerRequestFilter filter) {
        ResultList<CustomerRequest> resultList = customerRequestRepository.getResultList(filter);
        List<CustomerRequestListAdminDTO> result = resultList.getList().stream().map(customerRequest -> {
            CustomerRequestListAdminDTO customerRequestListAdminDTO = new CustomerRequestListAdminDTO();
            Customer customer = customerRequest.getCustomer();
            customerRequestListAdminDTO.setId(customerRequest.getId());
            customerRequestListAdminDTO.setStatus(customerRequest.getStatus());
            customerRequestListAdminDTO.setCallStatus(customerRequest.getCallStatus());
            customerRequestListAdminDTO.setRequestType(customerRequest.getRequestType());
            customerRequestListAdminDTO.setCreator(customerRequest.getCreator().toCommonDTO());
            customerRequestListAdminDTO.setLastModifier(customerRequest.getLastModifier().toCommonDTO());
            customerRequestListAdminDTO.setCreatedDate(customerRequest.getCreatedDate());
            customerRequestListAdminDTO.setLastModifiedDate(customerRequest.getLastModifiedDate());
            customerRequestListAdminDTO.setResolvedDate(customerRequest.getResolvedDate());
            customerRequestListAdminDTO.setInfo(customerRequest.getInfo());
            customerRequestListAdminDTO.setCustomer(new CustomerShortDTO(customer.getId(), customer.getName(), customer.getTin(), customer.getPhone()));
            return customerRequestListAdminDTO;
        }).collect(Collectors.toList());
        return new PageImpl<>(result, filter.getOrderedPageable(), resultList.getCount());
    }

    @Override
    public CustomerRequestDetailAdminDTO get(Long id) {
        return customerRequestRepository.findById(id).map(customerRequest -> {
            CustomerRequestDetailAdminDTO customerRequestDetailAdminDTO = new CustomerRequestDetailAdminDTO();
            Customer customer = customerRequest.getCustomer();
            customerRequestDetailAdminDTO.setId(customerRequest.getId());
            customerRequestDetailAdminDTO.setStatus(customerRequest.getStatus());
            customerRequestDetailAdminDTO.setCallStatus(customerRequest.getCallStatus());
            customerRequestDetailAdminDTO.setRequestType(customerRequest.getRequestType());
            customerRequestDetailAdminDTO.setCreator(customerRequest.getCreator().toCommonDTO());
            customerRequestDetailAdminDTO.setCustomer(new CustomerShortDTO(customer.getId(), customer.getName(), customer.getTin(), customer.getPhone()));
            return customerRequestDetailAdminDTO;
        }).orElseThrow(validationService.entityNotFoundExceptionThrow(REQUEST));
    }

    @Override
    @Transactional
    public void updateCallStatus(Long id, CallStatus callStatus) {
        customerRequestRepository.findById(id).ifPresentOrElse(
                customerRequest -> {
                    customerRequest.setCallStatus(callStatus);
                    save(customerRequest);
                },
                () -> validationService.entityNotFoundExceptionThrow(REQUEST).get()
        );
    }

    @Override
    @Transactional
    public void updateStatus(Long id, CustomerRequestStatus status) {
        customerRequestRepository.findById(id).ifPresentOrElse(
                customerRequest -> {
                    customerRequest.setCallStatus(CallStatus.PROCESSED);
                    customerRequest.setResolvedDate(LocalDateTime.now());
                    customerRequest.setStatus(status);
                    save(customerRequest);
                },
                () -> validationService.entityNotFoundExceptionThrow(REQUEST).get()
        );
    }
}
