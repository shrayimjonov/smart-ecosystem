package uz.uzkassa.cts.service.impl;

import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.cts.domain.base.SimpleEntity;
import uz.uzkassa.cts.domain.hrms.EventRequest;
import uz.uzkassa.cts.domain.hrms.EventRequestItem;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.dto.eventrequest.EventRequestDTO;
import uz.uzkassa.cts.dto.eventrequest.EventRequestItemDetailDTO;
import uz.uzkassa.cts.dto.eventrequest.admin.EventRequestDetailAdminDTO;
import uz.uzkassa.cts.dto.eventrequest.admin.EventRequestListAdminDTO;
import uz.uzkassa.cts.enums.ApproverCommonStatus;
import uz.uzkassa.cts.enums.Prefix;
import uz.uzkassa.cts.filter.EventRequestFilter;
import uz.uzkassa.cts.numbering.NumberingContext;
import uz.uzkassa.cts.repository.*;
import uz.uzkassa.cts.service.EventRequestService;
import uz.uzkassa.cts.validator.ValidationContext;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static lombok.AccessLevel.PRIVATE;
import static uz.uzkassa.cts.constants.Constants.INCREMENT_NUMBER;
import static uz.uzkassa.cts.constants.Constants.NUMBER;

/**
 * Created date time: 26.12.2024 13:29
 */

@Transactional(readOnly = true)
@Service
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class EventRequestServiceImpl extends BaseServiceImpl<EventRequest, Long> implements EventRequestService {

    EventRequestRepository eventRequestRepository;
    EventRequestItemRepository eventRequestItemRepository;
    EmployeeRepository employeeRepository;
    ProductRepository productRepository;
    CompanyRepository companyRepository;
    ValidationContext validationContext;
    NumberingContext numberingContext;

    public EventRequestServiceImpl(EventRequestRepository eventRequestRepository, EventRequestItemRepository eventRequestItemRepository, EmployeeRepository employeeRepository, ProductRepository productRepository, CompanyRepository companyRepository, ValidationContext validationContext, NumberingContext numberingContext) {
        super(eventRequestRepository);
        this.eventRequestRepository = eventRequestRepository;
        this.eventRequestItemRepository = eventRequestItemRepository;
        this.employeeRepository = employeeRepository;
        this.productRepository = productRepository;
        this.companyRepository = companyRepository;
        this.validationContext = validationContext;
        this.numberingContext = numberingContext;
    }

    @Override
    @Transactional
    public Long create(EventRequestDTO eventRequestDTO) {
        validationContext.validate(eventRequestDTO);
        Map<String, String> numberingMap = numberingContext.generateNumber(Prefix.EVENT_REQUEST, eventRequestDTO.getCompanyId());
        EventRequest eventRequest = new EventRequest();
        eventRequest.setEventRequestNumber(numberingMap.get(NUMBER));
        eventRequest.setIncrementNumber(Long.valueOf(numberingMap.get(INCREMENT_NUMBER)));
        eventRequest.setSubject(eventRequestDTO.getSubject());
        eventRequest.setFromDate(eventRequestDTO.getFromDate());
        eventRequest.setToDate(eventRequestDTO.getToDate());
        eventRequest.setApproverId(eventRequestDTO.getApproverId());
        eventRequest.setMemberCount(eventRequestDTO.getMemberCount());
        eventRequest.setRoom(eventRequestDTO.getRoom());
        eventRequest.setCompanyId(eventRequestDTO.getCompanyId());
        eventRequestRepository.save(eventRequest);
        eventRequestDTO
                .getItems()
                .forEach(eventRequestItemDTO -> {
                    EventRequestItem eventRequestItem = new EventRequestItem();
                    eventRequestItem.setDescription(eventRequestItemDTO.getDescription());
                    eventRequestItem.setProductId(eventRequestItemDTO.getProductId());
                    eventRequestItem.setQuantity(eventRequestItemDTO.getQuantity());
                    eventRequest.addItem(eventRequestItem);
                });
        return eventRequestRepository.save(eventRequest).getId();
    }

    @Override
    @Transactional
    public Long update(Long id, EventRequestDTO eventRequestDTO) {
        validationContext.validate(eventRequestDTO);
        return eventRequestRepository.findById(id).map(eventRequest -> {
            eventRequest.setFromDate(eventRequestDTO.getFromDate());
            eventRequest.setToDate(eventRequestDTO.getToDate());
            eventRequest.setMemberCount(eventRequestDTO.getMemberCount());
            eventRequest.setRoom(eventRequestDTO.getRoom());
            if (!Objects.equals(eventRequest.getApproverId(), eventRequestDTO.getApproverId())) {
                eventRequest.setApprover(employeeRepository.findById(eventRequestDTO.getApproverId()).orElseThrow(validationService.entityNotFoundExceptionThrow(EMPLOYEE)));
                eventRequest.setApproverId(eventRequestDTO.getApproverId());
            }
            if (!Objects.equals(eventRequest.getCompanyId(), eventRequestDTO.getCompanyId())) {
                eventRequest.setCompany(companyRepository.findById(eventRequestDTO.getCompanyId()).orElseThrow(validationService.entityNotFoundExceptionThrow(COMPANY)));
                eventRequest.setCompanyId(eventRequestDTO.getCompanyId());
            }
            eventRequestItemRepository.deleteAllByEventRequestId(id);
            eventRequest.getItems().clear();
            eventRequestDTO
                    .getItems()
                    .forEach(eventRequestItemDTO -> {
                        EventRequestItem eventRequestItem = new EventRequestItem();
                        eventRequestItem.setDescription(eventRequestItemDTO.getDescription());
                        eventRequestItem.setProductId(eventRequestItemDTO.getProductId());
                        eventRequestItem.setQuantity(eventRequestItemDTO.getQuantity());
                        eventRequest.addItem(eventRequestItem);
                    });
            eventRequestRepository.save(eventRequest);
            return id;
        }).orElseThrow(validationService.entityNotFoundExceptionThrow(REQUEST));
    }

    @Override
    public EventRequestDetailAdminDTO getForAdmin(Long id) {
        return eventRequestRepository.findById(id).map(eventRequest -> {
            EventRequestDetailAdminDTO eventRequestDetailAdminDTO = new EventRequestDetailAdminDTO();
            eventRequestDetailAdminDTO.setId(eventRequest.getId());
            eventRequestDetailAdminDTO.setEventRequestNumber(eventRequest.getEventRequestNumber());
            eventRequestDetailAdminDTO.setCompany(eventRequest.getCompany().toCommonDTO());
            eventRequestDetailAdminDTO.setFromDate(eventRequest.getFromDate());
            eventRequestDetailAdminDTO.setToDate(eventRequest.getToDate());
            eventRequestDetailAdminDTO.setMemberCount(eventRequest.getMemberCount());
            eventRequestDetailAdminDTO.setRoom(eventRequest.getRoom());
            eventRequestDetailAdminDTO.setApprover(eventRequest.getApprover().toCommonDTO());
            eventRequestDetailAdminDTO.setCreator(eventRequest.getCreator().toCommonDTO());
            eventRequestDetailAdminDTO.setCreatedDate(eventRequest.getCreatedDate());
            eventRequestDetailAdminDTO.setItems(eventRequest.getItems().stream().map(eventRequestItem -> {
                EventRequestItemDetailDTO eventRequestItemDetailDTO = new EventRequestItemDetailDTO();
                eventRequestItemDetailDTO.setDescription(eventRequestItem.getDescription());
                eventRequestItemDetailDTO.setProduct(eventRequestItem.getProduct().toCommonDTO());
                eventRequestItemDetailDTO.setQuantity(eventRequestItem.getQuantity());
                return eventRequestItemDetailDTO;
            }).toList());
            return eventRequestDetailAdminDTO;
        }).orElseThrow(validationService.entityNotFoundExceptionThrow(REQUEST));
    }

    @Override
    public Page<EventRequestListAdminDTO> getListForAdmin(EventRequestFilter filter) {
        ResultList<EventRequest> resultList = eventRequestRepository.getResultList(filter);
        List<EventRequestListAdminDTO> result = resultList.getList().stream().map(eventRequest -> {
            EventRequestListAdminDTO eventRequestListAdminDTO = new EventRequestListAdminDTO();
            eventRequestListAdminDTO.setId(eventRequest.getId());
            eventRequestListAdminDTO.setEventRequestNumber(eventRequest.getEventRequestNumber());
            eventRequestListAdminDTO.setStatus(eventRequest.getStatus());
            eventRequestListAdminDTO.setFromDate(eventRequest.getFromDate());
            eventRequestListAdminDTO.setToDate(eventRequest.getToDate());
            eventRequestListAdminDTO.setMemberCount(eventRequest.getMemberCount());
            eventRequestListAdminDTO.setRoom(eventRequest.getRoom());
            eventRequestListAdminDTO.setApprover(eventRequest.getApprover().toCommonDTO());
            eventRequestListAdminDTO.setCreator(eventRequest.getCreator().toCommonDTO());
            eventRequestListAdminDTO.setCreatedDate(eventRequest.getCreatedDate());
            return eventRequestListAdminDTO;
        }).collect(Collectors.toList());
        return new PageImpl<>(result, filter.getOrderedPageable(), resultList.getCount());
    }

    @Override
    public List<CommonDTO> lookUp(EventRequestFilter filter) {
        filter.setStatus(ApproverCommonStatus.APPROVED.getCode());
        return eventRequestRepository.getResultList(filter).getList().stream().map(SimpleEntity::toCommonDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void updateStatus(Long id, ApproverCommonStatus status) {
        eventRequestRepository.findById(id).ifPresentOrElse(eventRequest -> {
            eventRequest.setStatus(status);
            eventRequestRepository.save(eventRequest);
        }, () -> {
            throw validationService.entityNotFoundExceptionThrow(REQUEST).get();
        });
    }
}
