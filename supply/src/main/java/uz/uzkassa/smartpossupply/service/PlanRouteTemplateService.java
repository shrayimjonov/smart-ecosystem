package uz.uzkassa.smartpossupply.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import uz.uzkassa.smartpossupply.domain.planroutetemplate.PlanRouteTemplate;
import uz.uzkassa.smartpossupply.domain.planroutetemplate.PlanRouteTemplateCustomerBranch;
import uz.uzkassa.smartpossupply.domain.planroutetemplate.PlanRouteTemplateExpeditor;
import uz.uzkassa.smartpossupply.domain.planroutetemplate.PlanRouteTemplateItem;
import uz.uzkassa.smartpossupply.dto.base.ResultList;
import uz.uzkassa.smartpossupply.dto.customerbranch.CustomerBranchAddressDTO;
import uz.uzkassa.smartpossupply.dto.planroutetemplate.PlanRouteTemplateCustomerBranchDetailDTO;
import uz.uzkassa.smartpossupply.dto.planroutetemplate.PlanRouteTemplateDTO;
import uz.uzkassa.smartpossupply.dto.planroutetemplate.PlanRouteTemplateDetailDTO;
import uz.uzkassa.smartpossupply.dto.planroutetemplate.PlanRouteTemplateItemDetailDTO;
import uz.uzkassa.smartpossupply.dto.planroutetemplate.PlanRouteTemplateListDTO;
import uz.uzkassa.smartpossupply.dto.planroutetemplate.PlanRouteTemplateLookupDTO;
import uz.uzkassa.smartpossupply.enums.CommonStatus;
import uz.uzkassa.smartpossupply.enums.Prefix;
import uz.uzkassa.smartpossupply.filters.PlanRouteFilter;
import uz.uzkassa.smartpossupply.numbering.NumberingContext;
import uz.uzkassa.smartpossupply.repository.PlanRouteCustomerBranchRepository;
import uz.uzkassa.smartpossupply.repository.PlanRouteTemplateItemRepository;
import uz.uzkassa.smartpossupply.repository.PlanRouteTemplateRepository;
import uz.uzkassa.smartpossupply.validator.ValidationContext;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@Transactional(readOnly = true)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class PlanRouteTemplateService extends BaseService {

    PlanRouteTemplateRepository planRouteTemplateRepository;
    PlanRouteTemplateItemRepository planRouteTemplateItemRepository;
    PlanRouteCustomerBranchRepository planRouteCustomerBranchRepository;
    NumberingContext numberingContext;
    ValidationContext validationContext;

    @Transactional
    public Long create(PlanRouteTemplateDTO planRouteTemplateDTO) {
        validationContext.validate(planRouteTemplateDTO);
        Map<String, String> numberingMap = numberingContext.generateNumber(Prefix.PLAN_ROUTE_TEMPLATE, getCurrentCompanyId());

        PlanRouteTemplate planRouteTemplate = new PlanRouteTemplate();
        planRouteTemplate.setTemplateNumber(numberingMap.get(NUMBER));
        planRouteTemplate.setIncrementNumber(Long.valueOf(numberingMap.get(INCREMENT_NUMBER)));
        planRouteTemplate.setName(planRouteTemplateDTO.getName());
        planRouteTemplate.setAgentId(planRouteTemplateDTO.getAgentId());
        if (!CollectionUtils.isEmpty(planRouteTemplateDTO.getExpeditorIds())) {
            planRouteTemplateDTO.getExpeditorIds().forEach(expeditorId -> {
                PlanRouteTemplateExpeditor expeditor = new PlanRouteTemplateExpeditor();
                expeditor.setExpeditorId(expeditorId);
                planRouteTemplate.addExpeditor(expeditor);
            });
        }
        planRouteTemplate.setRegionId(planRouteTemplateDTO.getRegionId());
        planRouteTemplate.setBranchId(planRouteTemplateDTO.getBranchId());
        planRouteTemplateRepository.save(planRouteTemplate);
        Set<Long> customerBranches = new HashSet<>();
        planRouteTemplateDTO.getItems().forEach(planRouteItemDTO -> {
            PlanRouteTemplateItem planRouteTemplateItem = new PlanRouteTemplateItem();
            planRouteTemplateItem.setDay(planRouteItemDTO.getDay());
            planRouteTemplate.addItem(planRouteTemplateItem);
            planRouteTemplateItemRepository.save(planRouteTemplateItem);
            planRouteItemDTO.getCustomerBranches().forEach(customerBranchId -> {
                PlanRouteTemplateCustomerBranch planRouteTemplateCustomerBranch = new PlanRouteTemplateCustomerBranch();
                planRouteTemplateCustomerBranch.setCustomerBranchId(customerBranchId);
                customerBranches.add(customerBranchId);
                planRouteTemplateItem.addItem(planRouteTemplateCustomerBranch);
            });
        });
        planRouteTemplate.setCustomerBranchCount(customerBranches.size());
        return planRouteTemplate.getId();
    }

    @Transactional
    public Long update(Long id, PlanRouteTemplateDTO planRouteTemplateDTO) {
        return planRouteTemplateRepository.findById(id).map(planRouteTemplate -> {
            planRouteTemplateDTO.setId(id);
           validationContext.validate(planRouteTemplateDTO);

            planRouteTemplateItemRepository.deleteAllByPlanRouteTemplateId(id);
            planRouteTemplate.setName(planRouteTemplateDTO.getName());
            planRouteTemplate.setAgentId(planRouteTemplateDTO.getAgentId());
            planRouteTemplate.setRegionId(planRouteTemplateDTO.getRegionId());
            if (!CollectionUtils.isEmpty(planRouteTemplateDTO.getExpeditorIds())) {
                planRouteTemplate.getExpeditors().clear();
                planRouteTemplateDTO.getExpeditorIds().forEach(expeditorId -> {
                    PlanRouteTemplateExpeditor expeditor = new PlanRouteTemplateExpeditor();
                    expeditor.setExpeditorId(expeditorId);
                    planRouteTemplate.addExpeditor(expeditor);
                });
            } else {
                planRouteTemplate.getExpeditors().clear();
            }

            Set<Long> customerBranches = new HashSet<>();
            planRouteTemplateDTO.getItems().forEach(planRouteItemDTO -> {
                if (planRouteItemDTO.getId() != null) {
                    planRouteCustomerBranchRepository.deleteAllByPlanRouteItemId(planRouteItemDTO.getId());
                }
                PlanRouteTemplateItem planRouteTemplateItem = new PlanRouteTemplateItem();
                planRouteTemplateItem.setDay(planRouteItemDTO.getDay());
                planRouteTemplate.addItem(planRouteTemplateItem);
                planRouteTemplateItemRepository.save(planRouteTemplateItem);
                planRouteItemDTO.getCustomerBranches().forEach(customerBranchId -> {
                    PlanRouteTemplateCustomerBranch planRouteTemplateCustomerBranch = new PlanRouteTemplateCustomerBranch();
                    planRouteTemplateCustomerBranch.setCustomerBranchId(customerBranchId);
                    planRouteTemplateItem.addItem(planRouteTemplateCustomerBranch);
                    customerBranches.add(customerBranchId);
                });
            });
            planRouteTemplate.setCustomerBranchCount(customerBranches.size());
            planRouteTemplateRepository.save(planRouteTemplate);
            return id;
        }).orElseThrow(validationService.entityNotFoundExceptionThrow(PlanRouteTemplate.class.getSimpleName(), "id", id));
    }

    public PlanRouteTemplateDetailDTO get(Long id) {
        return planRouteTemplateRepository.getDetail(id).map(planRouteTemplate -> {
            PlanRouteTemplateDetailDTO planRouteTemplateDetailDTO = new PlanRouteTemplateDetailDTO();
            planRouteTemplateDetailDTO.setId(planRouteTemplate.getId());
            planRouteTemplateDetailDTO.setTemplateNumber(planRouteTemplate.getTemplateNumber());
            planRouteTemplateDetailDTO.setName(planRouteTemplate.getName());
            planRouteTemplateDetailDTO.setAgent(planRouteTemplate.getAgent().toCommonDTO());
            if (!CollectionUtils.isEmpty(planRouteTemplate.getExpeditors())) {
                planRouteTemplate.getExpeditors().forEach(expeditor -> planRouteTemplateDetailDTO.addExpeditor(expeditor.getExpeditor().toCommonDTO()));
            }
            planRouteTemplateDetailDTO.setBranch(planRouteTemplate.getBranch().toCommonDTO());
            if (planRouteTemplate.getRegionId() != null) {
                planRouteTemplateDetailDTO.setRegion(planRouteTemplate.getRegion().toCommonDTO());
            }
            planRouteTemplateDetailDTO.setStatus(planRouteTemplate.getStatus());
            planRouteTemplate.getItems().forEach(planRouteTemplateItem -> {
                PlanRouteTemplateItemDetailDTO planRouteTemplateItemDetailDTO = new PlanRouteTemplateItemDetailDTO();
                planRouteTemplateItemDetailDTO.setId(planRouteTemplateItem.getId());
                planRouteTemplateItemDetailDTO.setDay(planRouteTemplateItem.getDay());
                planRouteTemplateItem.getCustomerBranches().forEach(planRouteCustomerBranch -> {
                    PlanRouteTemplateCustomerBranchDetailDTO planRouteTemplateCustomerBranchDetailDTO = new PlanRouteTemplateCustomerBranchDetailDTO();
                    planRouteTemplateCustomerBranchDetailDTO.setId(planRouteCustomerBranch.getId());
                    planRouteTemplateCustomerBranchDetailDTO.setCustomerBranch(new CustomerBranchAddressDTO(planRouteCustomerBranch.getCustomerBranch().getId(), planRouteCustomerBranch.getCustomerBranch().getName(),
                            addressMapper.toDetailDTO(planRouteCustomerBranch.getCustomerBranch().getAddress()), planRouteCustomerBranch.getCustomerBranch().getPhone()));
                    planRouteTemplateItemDetailDTO.addItem(planRouteTemplateCustomerBranchDetailDTO);
                });
                planRouteTemplateDetailDTO.addItem(planRouteTemplateItemDetailDTO);
            });
            Collections.sort(planRouteTemplateDetailDTO.getItems());
            return planRouteTemplateDetailDTO;
        }).orElseThrow(validationService.entityNotFoundExceptionThrow(PlanRouteTemplate.class.getSimpleName(), "id", id));
    }

    public Page<PlanRouteTemplateListDTO> getList(PlanRouteFilter filter) {
        ResultList<PlanRouteTemplate> resultList = planRouteTemplateRepository.getResultList(filter);
        List<PlanRouteTemplateListDTO> result = resultList.getList().stream().map(planRouteTemplate -> {
            PlanRouteTemplateListDTO planRouteTemplateListDTO = new PlanRouteTemplateListDTO();
            planRouteTemplateListDTO.setId(planRouteTemplate.getId());
            planRouteTemplateListDTO.setTemplateNumber(planRouteTemplate.getTemplateNumber());
            planRouteTemplateListDTO.setName(planRouteTemplate.getName());
            planRouteTemplateListDTO.setAgent(planRouteTemplate.getAgent().toCommonDTO());
            planRouteTemplateListDTO.setCreatedDate(planRouteTemplate.getCreatedDate());
            planRouteTemplateListDTO.setStatus(planRouteTemplate.getStatus());
            planRouteTemplateListDTO.setCustomerBranchCount(planRouteTemplate.getCustomerBranchCount());
            if (planRouteTemplate.getRegionId() != null) {
                planRouteTemplateListDTO.setRegion(planRouteTemplate.getRegion().toCommonDTO());
            }
            planRouteTemplateListDTO.setBranch(planRouteTemplate.getBranch().toCommonDTO());
            return planRouteTemplateListDTO;
        }).toList();
        return new PageImpl<>(result, filter.getSortedPageable(), resultList.getCount());
    }

    public List<PlanRouteTemplateLookupDTO> lookup(PlanRouteFilter filter) {
        return planRouteTemplateRepository.lookUp(filter).stream().map(planRouteTemplate -> {
            PlanRouteTemplateLookupDTO planRouteTemplateListDTO = new PlanRouteTemplateLookupDTO();
            planRouteTemplateListDTO.setId(planRouteTemplate.getId());
            planRouteTemplateListDTO.setTemplateNumber(planRouteTemplate.getTemplateNumber());
            planRouteTemplateListDTO.setName(planRouteTemplate.getName());
            planRouteTemplateListDTO.setCreatedDate(planRouteTemplate.getCreatedDate());
            planRouteTemplateListDTO.setStatus(planRouteTemplate.getStatus());
            if (planRouteTemplate.getBranchId() != null) {
                planRouteTemplateListDTO.setBranch(planRouteTemplate.getBranch().toCommonDTO());
            }
            return planRouteTemplateListDTO;
        }).toList();
    }

    @Transactional
    public void updateStatus(Long id, CommonStatus status) {
        planRouteTemplateRepository.findById(id).ifPresentOrElse(planRouteTemplate -> {
            planRouteTemplate.setStatus(status);
            planRouteTemplateRepository.save(planRouteTemplate);
        }, () -> validationService.entityNotFoundExceptionThrow(PlanRouteTemplate.class.getSimpleName(), "id", id).get());
    }

    @Transactional
    public void delete(Long id) {
        planRouteTemplateRepository.deleteById(id);
    }
}
