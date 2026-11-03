package uz.uzkassa.smartpossupply.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.smartpossupply.constants.MessageConstants;
import uz.uzkassa.smartpossupply.domain.company.Company;
import uz.uzkassa.smartpossupply.domain.customerbranch.CustomerBranch;
import uz.uzkassa.smartpossupply.domain.planroute.PlanRouteCustomerBranch;
import uz.uzkassa.smartpossupply.domain.planroute.PlanRouteItem;
import uz.uzkassa.smartpossupply.domain.planroutetemplate.PlanRouteTemplateCustomerBranch;
import uz.uzkassa.smartpossupply.dto.base.ResultList;
import uz.uzkassa.smartpossupply.dto.customerbranch.CustomerBranchLookUpDTO;
import uz.uzkassa.smartpossupply.dto.order.mobile.MobileReturnOrderDTO;
import uz.uzkassa.smartpossupply.dto.planroute.PlanRouteCustomerBranchInfoDTO;
import uz.uzkassa.smartpossupply.dto.planroute.PlanRouteUpdateForOfflineDTO;
import uz.uzkassa.smartpossupply.filters.PlanRouteCustomerBranchFilter;
import uz.uzkassa.smartpossupply.repository.PlanRouteCustomerBranchRepository;
import uz.uzkassa.smartpossupply.repository.PlanRouteItemRepository;
import uz.uzkassa.smartpossupply.repository.PlanRouteTemplateCustomerBranchRepository;
import uz.uzkassa.smartpossupply.utils.SecurityUtils;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
//@Transactional(readOnly = true)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Slf4j
public class PlanRouteCustomerBranchService extends BaseService {

    PlanRouteItemRepository planRouteItemRepository;
    PlanRouteCustomerBranchRepository planRouteCustomerBranchRepository;
    PlanRouteTemplateCustomerBranchRepository planRouteTemplateCustomerBranchRepository;

    @Transactional
    public void create(Long planRouteItemId, Long customerBranchId, Boolean addToCurrentPlanRoute) {
        if (planRouteCustomerBranchRepository.existsByPlanRouteItemIdAndCustomerBranchIdAndDeletedIsFalse(planRouteItemId, customerBranchId)) {
            throw validationService.badRequestExceptionThrow(localizationService.localize(MessageConstants.CUSTOMER_BRANCH_ALREADY_ADDED), localizationService.localize(MessageConstants.CUSTOMER_BRANCH_ALREADY_ADDED)).get();
        }
        planRouteItemRepository.findById(planRouteItemId).ifPresentOrElse(planRouteItem -> {
            PlanRouteCustomerBranch planRouteCustomerBranch = new PlanRouteCustomerBranch();
            planRouteCustomerBranch.setUnscheduled(true);
            planRouteCustomerBranch.setPlanRouteItemId(planRouteItemId);
            planRouteCustomerBranch.setCustomerBranchId(customerBranchId);
            planRouteItem.addItem(planRouteCustomerBranch);
            planRouteItemRepository.save(planRouteItem);

            if (addToCurrentPlanRoute != null && addToCurrentPlanRoute) {
                planRouteItem.getPlanRoute().getTemplate().getItems().forEach(planRouteTemplateItem -> {
                    if (planRouteTemplateItem.getDay().getDayOfWeek().equals(planRouteItem.getVisitPlanDate().getDayOfWeek()) &&
                            !planRouteTemplateCustomerBranchRepository.existsByPlanRouteTemplateItemIdAndCustomerBranchIdAndDeletedIsFalse(planRouteTemplateItem.getId(), customerBranchId)) {
                        PlanRouteTemplateCustomerBranch planRouteTemplateCustomerBranch = new PlanRouteTemplateCustomerBranch();
                        planRouteTemplateCustomerBranch.setCustomerBranchId(customerBranchId);
                        planRouteTemplateCustomerBranch.setPlanRouteTemplateItemId(planRouteTemplateItem.getId());
                        planRouteTemplateCustomerBranchRepository.save(planRouteTemplateCustomerBranch);
                    }
                });
            }
        }, () -> {
            throw validationService.entityNotFoundExceptionThrow(PlanRouteItem.class.getSimpleName(), "id", planRouteItemId).get();
        });
    }

    @Transactional
    public Long createOffline(PlanRouteUpdateForOfflineDTO planRouteUpdateForOfflineDTO) {
        Long planRouteItemId = planRouteUpdateForOfflineDTO.getPlanRouteItemId();
        Long customerBranchId = planRouteUpdateForOfflineDTO.getCustomerBranchId();

        if (planRouteCustomerBranchRepository.existsByPlanRouteItemIdAndCustomerBranchIdAndDeletedIsFalse(planRouteItemId, customerBranchId)) {
            throw validationService.badRequestExceptionThrow(localizationService.localize(MessageConstants.CUSTOMER_BRANCH_ALREADY_ADDED), localizationService.localize(MessageConstants.CUSTOMER_BRANCH_ALREADY_ADDED)).get();
        }

        PlanRouteItem planRouteItem = planRouteItemRepository.findById(planRouteItemId).orElseThrow(() -> validationService.entityNotFoundExceptionThrow(PlanRouteItem.class.getSimpleName(), "id", planRouteItemId).get());
        PlanRouteCustomerBranch planRouteCustomerBranch = new PlanRouteCustomerBranch();
        planRouteCustomerBranch.setUnscheduled(true);
        planRouteCustomerBranch.setPlanRouteItemId(planRouteItemId);
        planRouteCustomerBranch.setCustomerBranchId(customerBranchId);
        planRouteCustomerBranch.setStatus(planRouteUpdateForOfflineDTO.getStatus());
        planRouteCustomerBranch.setStartDate(planRouteUpdateForOfflineDTO.getStartDate());
        planRouteCustomerBranch.setEndDate(planRouteUpdateForOfflineDTO.getEndDate());
        planRouteCustomerBranch.setReason(planRouteUpdateForOfflineDTO.getReason());
        planRouteItem.addItem(planRouteCustomerBranch);
        planRouteCustomerBranchRepository.save(planRouteCustomerBranch);
        planRouteItemRepository.save(planRouteItem);

        if (Boolean.TRUE.equals(planRouteUpdateForOfflineDTO.getAddToCurrentPlanRoute())) {
            planRouteItem.getPlanRoute()
                    .getTemplate()
                    .getItems()
                    .forEach(planRouteTemplateItem -> {
                        if (planRouteTemplateItem.getDay().getDayOfWeek().equals(planRouteItem.getVisitPlanDate().getDayOfWeek())
                                && !planRouteTemplateCustomerBranchRepository.existsByPlanRouteTemplateItemIdAndCustomerBranchIdAndDeletedIsFalse(planRouteTemplateItem.getId(), customerBranchId)) {
                            PlanRouteTemplateCustomerBranch planRouteTemplateCustomerBranch = new PlanRouteTemplateCustomerBranch();
                            planRouteTemplateCustomerBranch.setCustomerBranchId(customerBranchId);
                            planRouteTemplateCustomerBranch.setPlanRouteTemplateItemId(planRouteTemplateItem.getId());
                            planRouteTemplateCustomerBranchRepository.save(planRouteTemplateCustomerBranch);
                        }
                    });
        }
        return planRouteCustomerBranch.getId();
    }


    @Transactional
    public void update(PlanRouteUpdateForOfflineDTO planRouteUpdateForOfflineDTO) {
        planRouteCustomerBranchRepository.findFirstByPlanRouteItemIdAndCustomerBranchIdAndDeletedIsFalse(
                        planRouteUpdateForOfflineDTO.getPlanRouteItemId(),
                        planRouteUpdateForOfflineDTO.getCustomerBranchId())
                .ifPresentOrElse(planRouteCustomerBranch -> {
                    planRouteCustomerBranch.setStatus(planRouteUpdateForOfflineDTO.getStatus());
                    planRouteCustomerBranch.setStartDate(planRouteUpdateForOfflineDTO.getStartDate());
                    planRouteCustomerBranch.setEndDate(planRouteUpdateForOfflineDTO.getEndDate());
                    planRouteCustomerBranch.setReason(planRouteUpdateForOfflineDTO.getReason());
                    planRouteCustomerBranchRepository.save(planRouteCustomerBranch);
                }, () -> validationService.entityNotFoundExceptionThrow(PlanRouteCustomerBranch.class.getSimpleName(), "id", planRouteUpdateForOfflineDTO.getPlanRouteCustomerBranchId()).get());
    }

    @Transactional
    public void transfer(Long id, LocalDate visitPlanDate, Boolean addToCurrentPlanRoute) {
        LocalDate today = LocalDate.now();
        // Find Sunday of current week
        LocalDate sunday = today.with(DayOfWeek.SUNDAY);
        if (visitPlanDate.isBefore(today) || visitPlanDate.isAfter(sunday)) {
            throw validationService.badRequestExceptionThrow(localizationService.localize(MessageConstants.CANNOT_TRANSFER_THE_PLAN_ROUTE_TITLE),localizationService.localize(MessageConstants.CANNOT_TRANSFER_THE_PLAN_ROUTE)).get();
        }
        planRouteCustomerBranchRepository.findByIdAndDeletedIsFalse(id).ifPresentOrElse(planRouteCustomerBranch -> {
            if (planRouteCustomerBranchRepository.existsByPlanRouteItemIdAndCustomerBranchIdAndPlanRouteItem_VisitPlanDateAndDeletedIsFalse(planRouteCustomerBranch.getPlanRouteItemId(), planRouteCustomerBranch.getCustomerBranchId(), visitPlanDate)) {
                throw validationService.badRequestExceptionThrow(localizationService.localize(MessageConstants.CUSTOMER_BRANCH_ALREADY_ADDED), localizationService.localize(MessageConstants.CUSTOMER_BRANCH_ALREADY_ADDED)).get();
            }
            PlanRouteItem planRouteItem = planRouteItemRepository.findFirstByPlanRouteIdAndVisitPlanDateAndDeletedIsFalse(planRouteCustomerBranch.getPlanRouteItem().getPlanRouteId(), visitPlanDate).orElseGet(PlanRouteItem::new);
            if (planRouteItem.getPlanRouteCustomerBranches().stream().map(PlanRouteCustomerBranch::getCustomerBranch).map(CustomerBranch::getId).toList().contains(planRouteCustomerBranch.getCustomerBranchId())) {
                throw validationService.badRequestExceptionThrow(localizationService.localize(MessageConstants.CUSTOMER_BRANCH_ALREADY_ADDED), localizationService.localize(MessageConstants.CUSTOMER_BRANCH_ALREADY_ADDED)).get();
            }
            if (planRouteItem.getId() == null) {
                planRouteItem.setPlanRouteId(planRouteCustomerBranch.getPlanRouteItem().getPlanRouteId());
                planRouteItem.setPlanRoute(planRouteCustomerBranch.getPlanRouteItem().getPlanRoute());
                planRouteItem.setVisitPlanDate(visitPlanDate);
                planRouteItemRepository.save(planRouteItem);
            }
            PlanRouteCustomerBranch planRouteCustomerBranchNext = new PlanRouteCustomerBranch();
            planRouteCustomerBranchNext.setCustomerBranchId(planRouteCustomerBranch.getCustomerBranchId());
            planRouteCustomerBranchNext.setPlanRouteItemId(planRouteItem.getId());
            planRouteCustomerBranchRepository.save(planRouteCustomerBranchNext);
            planRouteItem.getPlanRouteCustomerBranches().add(planRouteCustomerBranchNext);

            if (addToCurrentPlanRoute != null && addToCurrentPlanRoute) {
                planRouteItem.getPlanRoute().getTemplate().getItems().forEach(planRouteTemplateItem -> {
                    if (planRouteTemplateItem.getDay().getDayOfWeek().equals(planRouteItem.getVisitPlanDate().getDayOfWeek()) &&
                            !planRouteTemplateCustomerBranchRepository.existsByPlanRouteTemplateItemIdAndCustomerBranchIdAndDeletedIsFalse(planRouteItem.getId(), planRouteCustomerBranch.getCustomerBranchId())) {
                        PlanRouteTemplateCustomerBranch planRouteTemplateCustomerBranch = new PlanRouteTemplateCustomerBranch();
                        planRouteTemplateCustomerBranch.setCustomerBranchId(planRouteCustomerBranch.getCustomerBranchId());
                        planRouteTemplateCustomerBranch.setPlanRouteTemplateItemId(planRouteTemplateItem.getId());
                        planRouteTemplateCustomerBranchRepository.save(planRouteTemplateCustomerBranch);
                    }
                });
                planRouteCustomerBranchRepository.deleteById(planRouteCustomerBranch.getId());
            }
        }, () -> {
            throw validationService.entityNotFoundExceptionThrow(PlanRouteCustomerBranch.class.getSimpleName(), "id", id).get();
        });
    }

    @Transactional
    public void delete(Long planRouteId, Long customerBranchId) {
        planRouteCustomerBranchRepository.deleteByPlanRouteItemIdAndCustomerBranchIdAndUnscheduled(planRouteId, customerBranchId, true);
    }

    public Page<PlanRouteCustomerBranchInfoDTO> getPlanRouteCustomerBranches(PlanRouteCustomerBranchFilter filter) {
        filter.setCompanyId(SecurityUtils.getCurrentCompanyId());
        ResultList<PlanRouteCustomerBranch> resultList = planRouteCustomerBranchRepository.getPlanRoutesCustomerBranches(filter);
        List<PlanRouteCustomerBranchInfoDTO> result = resultList.getList().stream().map(planRouteCustomerBranch -> {
            PlanRouteCustomerBranchInfoDTO planRouteCustomerBranchInfoDTO = new PlanRouteCustomerBranchInfoDTO();
            planRouteCustomerBranchInfoDTO.setPlanRouteCustomerBranchId(planRouteCustomerBranch.getId());
            if (planRouteCustomerBranch.getCustomerBranch() != null) {
                CustomerBranchLookUpDTO customerBranchLookUpDTO = new CustomerBranchLookUpDTO();
                customerBranchLookUpDTO.setId(planRouteCustomerBranch.getCustomerBranch().getId());
                customerBranchLookUpDTO.setName(planRouteCustomerBranch.getCustomerBranch().getName());
                customerBranchLookUpDTO.setAddress(addressMapper.toDetailDTO(planRouteCustomerBranch.getCustomerBranch().getAddress()));
                planRouteCustomerBranchInfoDTO.setCustomerBranch(customerBranchLookUpDTO);
                planRouteCustomerBranchInfoDTO.setCustomer(Optional.ofNullable(planRouteCustomerBranch.getCustomerBranch().getCustomer()).map(Company::toCommonDTO).orElse(null));
            }
            if (planRouteCustomerBranch.getOrderId() != null) {
                planRouteCustomerBranchInfoDTO.setOrder(new MobileReturnOrderDTO(planRouteCustomerBranch.getOrderId(), planRouteCustomerBranch.getOrder().getOrderNumber()));
            }
            planRouteCustomerBranchInfoDTO.setStatus(planRouteCustomerBranch.getStatus());
            return planRouteCustomerBranchInfoDTO;
        }).toList();
        return new PageImpl<>(result, filter.getSortedPageable(), resultList.getCount());
    }

    @Transactional
    public void syncOffline(List<PlanRouteUpdateForOfflineDTO> planRouteUpdateForOfflineDTOList) {
        planRouteUpdateForOfflineDTOList.forEach(planRouteUpdateForOfflineDTO -> {
            if (planRouteUpdateForOfflineDTO.isDeleted()) {
                this.delete(planRouteUpdateForOfflineDTO.getPlanRouteId(), planRouteUpdateForOfflineDTO.getCustomerBranchId());
            } else if (planRouteUpdateForOfflineDTO.isUpdated()) {
                this.update(planRouteUpdateForOfflineDTO);
            } else if (planRouteUpdateForOfflineDTO.getPlanRouteItemId() == null) {
                this.transfer(planRouteUpdateForOfflineDTO.getPlanRouteCustomerBranchId(), planRouteUpdateForOfflineDTO.getVisitPlanDate(), planRouteUpdateForOfflineDTO.getAddToCurrentPlanRoute());
            }
        });
    }
}
