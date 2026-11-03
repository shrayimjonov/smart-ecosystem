package uz.uzkassa.cts.service.impl;

import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.cts.domain.hrms.EquipmentRequest;
import uz.uzkassa.cts.domain.hrms.EquipmentRequestItem;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.dto.equipmentrequest.EquipmentRequestDTO;
import uz.uzkassa.cts.dto.equipmentrequest.EquipmentRequestItemDetailDTO;
import uz.uzkassa.cts.dto.equipmentrequest.admin.EquipmentRequestDetailAdminDTO;
import uz.uzkassa.cts.dto.equipmentrequest.admin.EquipmentRequestListAdminDTO;
import uz.uzkassa.cts.dto.equipmentrequest.cabinet.EquipmentRequestDetailCabinetDTO;
import uz.uzkassa.cts.dto.equipmentrequest.cabinet.EquipmentRequestListCabinetDTO;
import uz.uzkassa.cts.enums.ApproverCommonStatus;
import uz.uzkassa.cts.enums.Prefix;
import uz.uzkassa.cts.enums.Priority;
import uz.uzkassa.cts.filter.EquipmentRequestFilter;
import uz.uzkassa.cts.numbering.NumberingContext;
import uz.uzkassa.cts.repository.CompanyRepository;
import uz.uzkassa.cts.repository.EmployeeRepository;
import uz.uzkassa.cts.repository.EquipmentRequestItemRepository;
import uz.uzkassa.cts.repository.EquipmentRequestRepository;
import uz.uzkassa.cts.service.EquipmentRequestService;
import uz.uzkassa.cts.validator.ValidationContext;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static lombok.AccessLevel.PRIVATE;
import static uz.uzkassa.cts.constants.Constants.INCREMENT_NUMBER;
import static uz.uzkassa.cts.constants.Constants.NUMBER;

/**
 * Created date time: 25.12.2024 14:56
 */

@Transactional(readOnly = true)
@Service
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class EquipmentRequestServiceImpl extends BaseServiceImpl<EquipmentRequest, Long> implements EquipmentRequestService {

    EquipmentRequestRepository equipmentRequestRepository;
    EquipmentRequestItemRepository equipmentRequestItemRepository;
    EmployeeRepository employeeRepository;
    CompanyRepository companyRepository;
    ValidationContext validationContext;
    NumberingContext numberingContext;

    public EquipmentRequestServiceImpl(EquipmentRequestRepository equipmentRequestRepository, EquipmentRequestItemRepository equipmentRequestItemRepository, EmployeeRepository employeeRepository, CompanyRepository companyRepository, ValidationContext validationContext, NumberingContext numberingContext) {
        super(equipmentRequestRepository);
        this.equipmentRequestRepository = equipmentRequestRepository;
        this.equipmentRequestItemRepository = equipmentRequestItemRepository;
        this.employeeRepository = employeeRepository;
        this.companyRepository = companyRepository;
        this.validationContext = validationContext;
        this.numberingContext = numberingContext;
    }

    @Override
    @Transactional
    public Long create(EquipmentRequestDTO equipmentRequestDTO) {
        validationContext.validate(equipmentRequestDTO);
        Map<String, String> numberingMap = numberingContext.generateNumber(Prefix.EQUIPMENT_REQUEST, equipmentRequestDTO.getCompanyId());
        EquipmentRequest equipmentRequest = new EquipmentRequest();
        equipmentRequest.setEquipmentRequestNumber(numberingMap.get(NUMBER));
        equipmentRequest.setIncrementNumber(Long.valueOf(numberingMap.get(INCREMENT_NUMBER)));
        equipmentRequest.setDescription(equipmentRequestDTO.getDescription());
        equipmentRequest.setPriority(Optional.ofNullable(equipmentRequestDTO.getPriority()).orElse(Priority.LOW));
        equipmentRequest.setRequestDate(equipmentRequestDTO.getRequestDate());
        equipmentRequest.setApproverId(equipmentRequestDTO.getApproverId());
        equipmentRequest.setCompanyId(equipmentRequestDTO.getCompanyId());
        equipmentRequestRepository.save(equipmentRequest);
        equipmentRequestDTO
                .getItems()
                .forEach(equipmentRequestItemDTO -> {
                    EquipmentRequestItem equipmentRequestItem = new EquipmentRequestItem();
                    equipmentRequestItem.setDescription(equipmentRequestItemDTO.getDescription());
                    equipmentRequestItem.setProductId(equipmentRequestItemDTO.getProductId());
                    equipmentRequestItem.setQuantity(equipmentRequestItemDTO.getQuantity());
                    equipmentRequest.addItem(equipmentRequestItem);
                });
        return equipmentRequest.getId();
    }

    @Override
    @Transactional
    public Long update(Long id, EquipmentRequestDTO equipmentRequestDTO) {
        validationContext.validate(equipmentRequestDTO);
        return equipmentRequestRepository.findById(id).map(equipmentRequest -> {
            equipmentRequest.setDescription(equipmentRequestDTO.getDescription());
            equipmentRequest.setRequestDate(equipmentRequestDTO.getRequestDate());
            equipmentRequest.setPriority(Optional.ofNullable(equipmentRequestDTO.getPriority()).orElse(Priority.LOW));
            if (!Objects.equals(equipmentRequest.getCompanyId(), equipmentRequestDTO.getCompanyId())) {
                equipmentRequest.setCompany(companyRepository.findById(equipmentRequestDTO.getCompanyId()).orElseThrow(validationService.entityNotFoundExceptionThrow(COMPANY)));
                equipmentRequest.setCompanyId(equipmentRequestDTO.getCompanyId());
            }
            if (!Objects.equals(equipmentRequest.getApproverId(), equipmentRequestDTO.getApproverId())) {
                equipmentRequest.setApprover(employeeRepository.findById(equipmentRequestDTO.getApproverId()).orElseThrow(validationService.entityNotFoundExceptionThrow(EMPLOYEE)));
                equipmentRequest.setApproverId(equipmentRequestDTO.getApproverId());
            }
            equipmentRequestItemRepository.deleteAllByEquipmentRequestId(id);
            equipmentRequest.getItems().clear();
            equipmentRequestDTO
                    .getItems()
                    .forEach(equipmentRequestItemDTO -> {
                        EquipmentRequestItem equipmentRequestItem = new EquipmentRequestItem();
                        equipmentRequestItem.setDescription(equipmentRequestItemDTO.getDescription());
                        equipmentRequestItem.setProductId(equipmentRequestItemDTO.getProductId());
                        equipmentRequestItem.setQuantity(equipmentRequestItemDTO.getQuantity());
                        equipmentRequest.addItem(equipmentRequestItem);
                    });
            equipmentRequestRepository.save(equipmentRequest);
            return id;
        }).orElseThrow(validationService.entityNotFoundExceptionThrow(REQUEST));
    }

    @Override
    public EquipmentRequestDetailAdminDTO getForAdmin(Long id) {
        return equipmentRequestRepository.findById(id).map(equipmentRequest -> {
            EquipmentRequestDetailAdminDTO equipmentRequestDetailAdminDTO = new EquipmentRequestDetailAdminDTO();
            equipmentRequestDetailAdminDTO.setId(equipmentRequest.getId());
            equipmentRequestDetailAdminDTO.setEquipmentRequestNumber(equipmentRequest.getEquipmentRequestNumber());
            equipmentRequestDetailAdminDTO.setDescription(equipmentRequest.getDescription());
            equipmentRequestDetailAdminDTO.setPriority(equipmentRequest.getPriority());
            equipmentRequestDetailAdminDTO.setRequestDate(equipmentRequest.getRequestDate());
            equipmentRequestDetailAdminDTO.setCompany(equipmentRequest.getCompany().toCommonDTO());
            equipmentRequestDetailAdminDTO.setApprover(equipmentRequest.getApprover().toCommonDTO());
            equipmentRequestDetailAdminDTO.setCreator(equipmentRequest.getCreator().toCommonDTO());
            equipmentRequestDetailAdminDTO.setCreatedDate(equipmentRequest.getCreatedDate());
            equipmentRequestDetailAdminDTO.setItems(equipmentRequest.getItems().stream().map(equipmentRequestItem -> {
                EquipmentRequestItemDetailDTO equipmentRequestItemDetailDTO = new EquipmentRequestItemDetailDTO();
                equipmentRequestItemDetailDTO.setDescription(equipmentRequestItem.getDescription());
                equipmentRequestItemDetailDTO.setProduct(equipmentRequestItem.getProduct().toCommonDTO());
                equipmentRequestItemDetailDTO.setQuantity(equipmentRequestItem.getQuantity());
                return equipmentRequestItemDetailDTO;
            }).toList());
            return equipmentRequestDetailAdminDTO;
        }).orElseThrow(validationService.entityNotFoundExceptionThrow(REQUEST));
    }

    @Override
    public Page<EquipmentRequestListAdminDTO> getListForAdmin(EquipmentRequestFilter filter) {
        ResultList<EquipmentRequest> resultList = equipmentRequestRepository.getResultList(filter);
        List<EquipmentRequestListAdminDTO> result = resultList.getList().stream().map(equipmentRequest -> {
            EquipmentRequestListAdminDTO equipmentRequestListAdminDTO = new EquipmentRequestListAdminDTO();
            equipmentRequestListAdminDTO.setId(equipmentRequest.getId());
            equipmentRequestListAdminDTO.setEquipmentRequestNumber(equipmentRequest.getEquipmentRequestNumber());
            equipmentRequestListAdminDTO.setDescription(equipmentRequest.getDescription());
            equipmentRequestListAdminDTO.setStatus(equipmentRequest.getStatus());
            equipmentRequestListAdminDTO.setPriority(equipmentRequest.getPriority());
            equipmentRequestListAdminDTO.setRequestDate(equipmentRequest.getRequestDate());
            equipmentRequestListAdminDTO.setApprover(equipmentRequest.getApprover().toCommonDTO());
            equipmentRequestListAdminDTO.setCreator(equipmentRequest.getCreator().toCommonDTO());
            equipmentRequestListAdminDTO.setCreatedDate(equipmentRequest.getCreatedDate());
            return equipmentRequestListAdminDTO;
        }).collect(Collectors.toList());
        return new PageImpl<>(result, filter.getOrderedPageable(), resultList.getCount());
    }

    @Override
    public EquipmentRequestDetailCabinetDTO getForCabinet(Long id) {
        return equipmentRequestRepository.findById(id).map(equipmentRequest -> {
            EquipmentRequestDetailCabinetDTO equipmentRequestDetailCabinetDTO = new EquipmentRequestDetailCabinetDTO();
            equipmentRequestDetailCabinetDTO.setId(equipmentRequest.getId());
            equipmentRequestDetailCabinetDTO.setEquipmentRequestNumber(equipmentRequest.getEquipmentRequestNumber());
            equipmentRequestDetailCabinetDTO.setDescription(equipmentRequest.getDescription());
            equipmentRequestDetailCabinetDTO.setPriority(equipmentRequest.getPriority());
            equipmentRequestDetailCabinetDTO.setRequestDate(equipmentRequest.getRequestDate());
            equipmentRequestDetailCabinetDTO.setCompany(equipmentRequest.getCompany().toCommonDTO());
            equipmentRequestDetailCabinetDTO.setApprover(equipmentRequest.getApprover().toCommonDTO());
            equipmentRequestDetailCabinetDTO.setCreator(equipmentRequest.getCreator().toCommonDTO());
            equipmentRequestDetailCabinetDTO.setCreatedDate(equipmentRequest.getCreatedDate());
            equipmentRequestDetailCabinetDTO.setItems(equipmentRequest
                    .getItems()
                    .stream().map(equipmentRequestItem -> {
                        EquipmentRequestItemDetailDTO equipmentRequestItemDetailDTO = new EquipmentRequestItemDetailDTO();
                        equipmentRequestItemDetailDTO.setDescription(equipmentRequestItem.getDescription());
                        equipmentRequestItemDetailDTO.setProduct(equipmentRequestItem.getProduct().toCommonDTO());
                        equipmentRequestItemDetailDTO.setQuantity(equipmentRequestItem.getQuantity());
                        return equipmentRequestItemDetailDTO;
                    }).toList());
            return equipmentRequestDetailCabinetDTO;
        }).orElseThrow(validationService.entityNotFoundExceptionThrow(REQUEST));
    }

    @Override
    public Page<EquipmentRequestListCabinetDTO> getListForCabinet(EquipmentRequestFilter filter) {
        ResultList<EquipmentRequest> resultList = equipmentRequestRepository.getResultList(filter);
        List<EquipmentRequestListCabinetDTO> result = resultList.getList().stream().map(equipmentRequest -> {
            EquipmentRequestListCabinetDTO equipmentRequestListCabinetDTO = new EquipmentRequestListCabinetDTO();
            equipmentRequestListCabinetDTO.setId(equipmentRequest.getId());
            equipmentRequestListCabinetDTO.setEquipmentRequestNumber(equipmentRequest.getEquipmentRequestNumber());
            equipmentRequestListCabinetDTO.setDescription(equipmentRequest.getDescription());
            equipmentRequestListCabinetDTO.setStatus(equipmentRequest.getStatus());
            equipmentRequestListCabinetDTO.setPriority(equipmentRequest.getPriority());
            equipmentRequestListCabinetDTO.setRequestDate(equipmentRequest.getRequestDate());
            equipmentRequestListCabinetDTO.setApprover(equipmentRequest.getApprover().toCommonDTO());
            equipmentRequestListCabinetDTO.setCreator(equipmentRequest.getCreator().toCommonDTO());
            equipmentRequestListCabinetDTO.setCreatedDate(equipmentRequest.getCreatedDate());
            return equipmentRequestListCabinetDTO;
        }).collect(Collectors.toList());
        return new PageImpl<>(result, filter.getOrderedPageable(), resultList.getCount());
    }

    @Override
    @Transactional
    public void updateStatus(Long id, ApproverCommonStatus status) {
        equipmentRequestRepository.findById(id).ifPresentOrElse(equipmentRequest -> {
            equipmentRequest.setStatus(status);
            equipmentRequestRepository.save(equipmentRequest);
        }, () -> {
            throw validationService.entityNotFoundExceptionThrow(REQUEST).get();
        });
    }
}
