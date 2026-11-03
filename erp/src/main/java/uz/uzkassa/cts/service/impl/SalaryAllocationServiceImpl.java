package uz.uzkassa.cts.service.impl;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import uz.uzkassa.cts.domain.hrms.SalaryAllocation;
import uz.uzkassa.cts.domain.hrms.SalaryAllocationItem;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.dto.salaryAllocation.*;
import uz.uzkassa.cts.filter.SalaryAllocationFilter;
import uz.uzkassa.cts.repository.SalaryAllocationItemRepository;
import uz.uzkassa.cts.repository.SalaryAllocationRepository;
import uz.uzkassa.cts.service.SalaryAllocationService;
import uz.uzkassa.cts.validator.ValidationContext;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class SalaryAllocationServiceImpl extends BaseServiceImpl<SalaryAllocation, Long> implements SalaryAllocationService {

    SalaryAllocationRepository salaryAllocationRepository;
    SalaryAllocationItemRepository salaryAllocationItemRepository;
    ValidationContext validationContext;

    public SalaryAllocationServiceImpl(SalaryAllocationRepository salaryAllocationRepository, SalaryAllocationItemRepository salaryAllocationItemRepository, ValidationContext validationContext) {
        super(salaryAllocationRepository);
        this.salaryAllocationRepository = salaryAllocationRepository;
        this.salaryAllocationItemRepository = salaryAllocationItemRepository;
        this.validationContext = validationContext;
    }

    @Override
    @Transactional
    public Long create(SalaryAllocationDTO salaryAllocationDTO) {
        validationContext.validate(salaryAllocationDTO);
        SalaryAllocation salaryAllocation = new SalaryAllocation();
        salaryAllocation.setDepartmentId(salaryAllocationDTO.getDepartmentId());
        salaryAllocationRepository.save(salaryAllocation);

        BigDecimal minSalaryTotalAmount = BigDecimal.ZERO;
        BigDecimal maxSalaryTotalAmount = BigDecimal.ZERO;
        Integer minEmployeeTotalCount = 0;
        Integer maxEmployeeTotalCount = 0;

        for (SalaryAllocationItemDTO itemDTO : salaryAllocationDTO.getItems()) {
            validationContext.validate(itemDTO);
            SalaryAllocationItem salaryAllocationItem = new SalaryAllocationItem();
            salaryAllocationItem.setPositionId(itemDTO.getPositionId());
            salaryAllocationItem.setGradeId(itemDTO.getGradeId());
            salaryAllocationItem.setMinSalaryAmount(itemDTO.getMinSalaryAmount());
            salaryAllocationItem.setMaxSalaryAmount(itemDTO.getMaxSalaryAmount());
            salaryAllocationItem.setMinEmployeeCount(itemDTO.getMinEmployeeCount());
            salaryAllocationItem.setMaxEmployeeCount(itemDTO.getMaxEmployeeCount());
            salaryAllocation.addItem(salaryAllocationItem);

            minSalaryTotalAmount = minSalaryTotalAmount.add(itemDTO.getMinSalaryAmount());
            maxSalaryTotalAmount = maxSalaryTotalAmount.add(itemDTO.getMaxSalaryAmount());
            minEmployeeTotalCount += itemDTO.getMinEmployeeCount();
            maxEmployeeTotalCount += itemDTO.getMaxEmployeeCount();
        }
        salaryAllocation.setMinSalaryTotalAmount(minSalaryTotalAmount);
        salaryAllocation.setMaxSalaryTotalAmount(maxSalaryTotalAmount);
        salaryAllocation.setMinEmployeeTotalCount(minEmployeeTotalCount);
        salaryAllocation.setMaxEmployeeTotalCount(maxEmployeeTotalCount);
        salaryAllocationRepository.save(salaryAllocation);

        return salaryAllocation.getId();
    }

    @Override
    @Transactional
    public Long update(Long id, SalaryAllocationDTO salaryAllocationDTO) {
        SalaryAllocation salaryAllocation = salaryAllocationRepository.findById(id).orElseThrow(validationService.entityNotFoundExceptionThrow(SALARY_ALLOCATION));
        validationContext.validate(salaryAllocationDTO);
        salaryAllocation.setDepartmentId(salaryAllocationDTO.getDepartmentId());

        BigDecimal minSalaryTotalAmount = BigDecimal.ZERO;
        BigDecimal maxSalaryTotalAmount = BigDecimal.ZERO;
        Integer minEmployeeTotalCount = 0;
        Integer maxEmployeeTotalCount = 0;

        Map<Long, SalaryAllocationItem> itemsMap = salaryAllocation
                .getItems()
                .stream()
                .collect(Collectors.toMap(SalaryAllocationItem::getId, salaryAllocationItem -> salaryAllocationItem));

        salaryAllocation.getItems().clear();
        for (SalaryAllocationItemDTO salaryAllocationItemDTO : salaryAllocationDTO.getItems()) {
            validationContext.validate(salaryAllocationItemDTO);
            SalaryAllocationItem salaryAllocationItem;

            if (salaryAllocationItemDTO.getId() != null && itemsMap.containsKey(salaryAllocationItemDTO.getId())) {
                salaryAllocationItem = itemsMap.get(salaryAllocationItemDTO.getId());
                itemsMap.remove(salaryAllocationItem.getId());
            } else {
                salaryAllocationItem = new SalaryAllocationItem();
            }

            salaryAllocationItem.setPositionId(salaryAllocationItemDTO.getPositionId());
            salaryAllocationItem.setGradeId(salaryAllocationItemDTO.getGradeId());
            salaryAllocationItem.setMinSalaryAmount(salaryAllocationItemDTO.getMinSalaryAmount());
            salaryAllocationItem.setMaxSalaryAmount(salaryAllocationItemDTO.getMaxSalaryAmount());
            salaryAllocationItem.setMinEmployeeCount(salaryAllocationItemDTO.getMinEmployeeCount());
            salaryAllocationItem.setMaxEmployeeCount(salaryAllocationItemDTO.getMaxEmployeeCount());
            salaryAllocation.addItem(salaryAllocationItem);

            minSalaryTotalAmount = minSalaryTotalAmount.add(salaryAllocationItemDTO.getMinSalaryAmount());
            maxSalaryTotalAmount = maxSalaryTotalAmount.add(salaryAllocationItemDTO.getMaxSalaryAmount());
            minEmployeeTotalCount += salaryAllocationItemDTO.getMinEmployeeCount();
            maxEmployeeTotalCount += salaryAllocationItemDTO.getMaxEmployeeCount();
        }
        if (!CollectionUtils.isEmpty(itemsMap)) {
            salaryAllocationItemRepository.deleteAllById(itemsMap.keySet());
        }

        salaryAllocation.setMinSalaryTotalAmount(minSalaryTotalAmount);
        salaryAllocation.setMaxSalaryTotalAmount(maxSalaryTotalAmount);
        salaryAllocation.setMinEmployeeTotalCount(minEmployeeTotalCount);
        salaryAllocation.setMaxEmployeeTotalCount(maxEmployeeTotalCount);
        salaryAllocationRepository.save(salaryAllocation);

        return salaryAllocation.getId();
    }


    @Override
    public SalaryAllocationDetailDTO get(Long id) {
        SalaryAllocation salaryAllocation = salaryAllocationRepository.findById(id).orElseThrow(validationService.entityNotFoundExceptionThrow(SALARY_ALLOCATION));
        SalaryAllocationDetailDTO salaryAllocationDetailDTO = new SalaryAllocationDetailDTO();
        salaryAllocationDetailDTO.setId(salaryAllocation.getId());
        salaryAllocationDetailDTO.setDepartment(salaryAllocation.getDepartment().toCommonDTO());
        salaryAllocationDetailDTO.setMinSalaryTotalAmount(salaryAllocation.getMinSalaryTotalAmount());
        salaryAllocationDetailDTO.setMaxSalaryTotalAmount(salaryAllocation.getMaxSalaryTotalAmount());
        salaryAllocationDetailDTO.setMinEmployeeTotalCount(salaryAllocation.getMinEmployeeTotalCount());
        salaryAllocationDetailDTO.setMaxEmployeeTotalCount(salaryAllocation.getMaxEmployeeTotalCount());

        salaryAllocation.getItems().forEach(item -> {
            SalaryAllocationItemDetailDTO salaryAllocationItemDTO = new SalaryAllocationItemDetailDTO();
            salaryAllocationItemDTO.setId(item.getId());
            salaryAllocationItemDTO.setPosition(item.getPosition().toCommonDTO());
            salaryAllocationItemDTO.setGrade(item.getGrade().toCommonDTO());
            salaryAllocationItemDTO.setMinSalaryAmount(item.getMinSalaryAmount());
            salaryAllocationItemDTO.setMaxSalaryAmount(item.getMaxSalaryAmount());
            salaryAllocationItemDTO.setMinEmployeeCount(item.getMinEmployeeCount());
            salaryAllocationItemDTO.setMaxEmployeeCount(item.getMaxEmployeeCount());
            salaryAllocationDetailDTO.addItem(salaryAllocationItemDTO);
        });

        return salaryAllocationDetailDTO;
    }

    @Override
    public Page<SalaryAllocationListDTO> getList(SalaryAllocationFilter filter) {
        ResultList<SalaryAllocation> resultList = salaryAllocationRepository.getResultList(filter);
        List<SalaryAllocationListDTO> result = resultList
                .getList()
                .stream()
                .map(salaryAllocation -> {
                    SalaryAllocationListDTO salaryAllocationListDTO = new SalaryAllocationListDTO();
                    salaryAllocationListDTO.setId(salaryAllocation.getId());
                    salaryAllocationListDTO.setDepartment(salaryAllocation.getDepartment().toCommonDTO());
                    salaryAllocationListDTO.setMinSalaryTotalAmount(salaryAllocation.getMinSalaryTotalAmount());
                    salaryAllocationListDTO.setMaxSalaryTotalAmount(salaryAllocation.getMaxSalaryTotalAmount());
                    salaryAllocationListDTO.setMinEmployeeTotalCount(salaryAllocation.getMinEmployeeTotalCount());
                    salaryAllocationListDTO.setMaxEmployeeTotalCount(salaryAllocation.getMaxEmployeeTotalCount());
                    return salaryAllocationListDTO;
                })
                .collect(Collectors.toList());
        return new PageImpl<>(result, filter.getOrderedPageable(), resultList.getCount());

    }
}

