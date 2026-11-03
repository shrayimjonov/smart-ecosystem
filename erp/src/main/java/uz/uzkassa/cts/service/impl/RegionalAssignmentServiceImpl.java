package uz.uzkassa.cts.service.impl;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import uz.uzkassa.cts.domain.User;
import uz.uzkassa.cts.domain.regionalassignment.RegionalAssignment;
import uz.uzkassa.cts.domain.regionalassignment.RegionalAssignmentDistrict;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.dto.regionalassignment.RegionalAssignmentDTO;
import uz.uzkassa.cts.dto.regionalassignment.RegionalAssignmentDetailDTO;
import uz.uzkassa.cts.dto.regionalassignment.RegionalAssignmentListDTO;
import uz.uzkassa.cts.enums.CommonStatus;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.filter.RegionalAssignmentFilter;
import uz.uzkassa.cts.repository.RegionalAssignmentRepository;
import uz.uzkassa.cts.service.RegionalAssignmentService;
import uz.uzkassa.cts.service.ValidationService;
import uz.uzkassa.cts.validator.ValidationContext;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RegionalAssignmentServiceImpl extends BaseServiceImpl<RegionalAssignment, Long> implements RegionalAssignmentService {

    RegionalAssignmentRepository regionalAssignmentRepository;
    ValidationContext validationContext;
    ValidationService validationService;

    public RegionalAssignmentServiceImpl(RegionalAssignmentRepository regionalAssignmentRepository, ValidationContext validationContext, ValidationService validationService) {
        super(regionalAssignmentRepository);
        this.regionalAssignmentRepository = regionalAssignmentRepository;
        this.validationContext = validationContext;
        this.validationService = validationService;
    }

    @Override
    @Transactional
    public Long create(RegionalAssignmentDTO regionalAssignmentDTO) {
        validationContext.validate(regionalAssignmentDTO);
        if (regionalAssignmentRepository.existsByEmployeeId(regionalAssignmentDTO.getEmployeeId())) {
           throw validationService.entityAlreadyExistExceptionThrow(EMPLOYEE).get();
        }
        RegionalAssignment regionalAssignment = new RegionalAssignment();
        regionalAssignment.setRegionId(regionalAssignmentDTO.getRegionId());
        regionalAssignment.setEmployeeId(regionalAssignmentDTO.getEmployeeId());
        regionalAssignmentRepository.save(regionalAssignment);
        regionalAssignmentDTO.getDistrictIds().forEach(districtId -> {
            RegionalAssignmentDistrict regionalAssignmentDistrict = new RegionalAssignmentDistrict();
            regionalAssignmentDistrict.setDistrictId(districtId);
            regionalAssignment.addRegionalAssignmentDistrict(regionalAssignmentDistrict);
        });
        return regionalAssignment.getId();
    }

    @Override
    @Transactional
    public Long update(Long id, RegionalAssignmentDTO regionalAssignmentDTO) {
        validationContext.validate(regionalAssignmentDTO);
        return regionalAssignmentRepository.findById(id).map(regionalAssignment -> {
            regionalAssignment.setRegionId(regionalAssignmentDTO.getRegionId());
            regionalAssignment.setEmployeeId(regionalAssignmentDTO.getEmployeeId());
            if (!CollectionUtils.isEmpty(regionalAssignmentDTO.getDistrictIds())) {
                regionalAssignment.getRegionalAssignmentDistricts().clear();
            }
            regionalAssignmentDTO.getDistrictIds().forEach(districtId -> {
                RegionalAssignmentDistrict regionalAssignmentDistrict = new RegionalAssignmentDistrict();
                regionalAssignmentDistrict.setDistrictId(districtId);
                regionalAssignment.addRegionalAssignmentDistrict(regionalAssignmentDistrict);
            });
            return regionalAssignmentRepository.save(regionalAssignment).getId();
        }).orElseThrow(() -> validationService.entityNotFoundExceptionThrow(RegionalAssignment.class.getName()).get());
    }

    @Override
    public RegionalAssignmentDetailDTO get(Long id) {
        return regionalAssignmentRepository.findById(id).map(regionalAssignment -> {
            RegionalAssignmentDetailDTO regionalAssignmentDetailDTO = new RegionalAssignmentDetailDTO();
            regionalAssignmentDetailDTO.setId(regionalAssignment.getId());
            regionalAssignmentDetailDTO.setCreator(regionalAssignment.getCreator().toCommonDTO());
            regionalAssignmentDetailDTO.setRegion(regionalAssignment.getRegion().toCommonDTO());
            regionalAssignmentDetailDTO.setStatus(regionalAssignment.getStatus());
            regionalAssignmentDetailDTO.setCreatedDate(regionalAssignment.getCreatedDate());
            regionalAssignmentDetailDTO.setEmployee(regionalAssignment.getEmployee().toCommonDTO());
            regionalAssignment.getRegionalAssignmentDistricts().forEach(regionalAssignmentDistrict -> regionalAssignmentDetailDTO.addDistrict(regionalAssignmentDistrict.getDistrict().toCommonDTO()));
            return regionalAssignmentDetailDTO;
        }).orElseThrow(() -> validationService.entityNotFoundExceptionThrow(RegionalAssignment.class.getName()).get());
    }

    @Override
    public Page<RegionalAssignmentListDTO> getList(RegionalAssignmentFilter filter) {
        ResultList<RegionalAssignment> resultList = regionalAssignmentRepository.getResultList(filter);
        List<RegionalAssignmentListDTO> result = resultList
                .getList()
                .stream()
                .map(regionalAssignment -> {
                    RegionalAssignmentListDTO regionalAssignmentListDTO = new RegionalAssignmentListDTO();
                    regionalAssignmentListDTO.setId(regionalAssignment.getId());
                    regionalAssignmentListDTO.setCreator(regionalAssignment.getCreator().toCommonDTO());
                    regionalAssignmentListDTO.setEmployee(regionalAssignment.getEmployee().toCommonDTO());
                    regionalAssignmentListDTO.setRegion(regionalAssignment.getRegion().toCommonDTO());
                    regionalAssignmentListDTO.setStatus(regionalAssignment.getStatus());
                    regionalAssignmentListDTO.setCreatedDate(regionalAssignment.getCreatedDate());
                    regionalAssignmentListDTO.setUpdatedDate(regionalAssignment.getLastModifiedDate());
                    return regionalAssignmentListDTO;
                })
                .collect(Collectors.toList());
        return new PageImpl<>(result, filter.getOrderedPageable(), resultList.getCount());
    }

    @Override
    @Transactional
    public void updateStatus(Long id, CommonStatus status) {
        regionalAssignmentRepository.findById(id).ifPresentOrElse(regionalAssignment -> {
            regionalAssignment.setStatus(status);
            regionalAssignmentRepository.save(regionalAssignment);
        }, () -> validationService.entityNotFoundExceptionThrow(RegionalAssignment.class.getName()).get());
    }
}
