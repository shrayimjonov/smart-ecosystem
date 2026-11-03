package uz.uzkassa.smartpossupply.service;

import uz.uzkassa.smartpossupply.constants.MessageConstants;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.smartpossupply.domain.FileUpload;
import uz.uzkassa.smartpossupply.domain.address.Region;
import uz.uzkassa.smartpossupply.domain.auth.User;
import uz.uzkassa.smartpossupply.domain.base.SimpleEntity;
import uz.uzkassa.smartpossupply.domain.company.Company;
import uz.uzkassa.smartpossupply.domain.customerbranch.CustomerBranch;
import uz.uzkassa.smartpossupply.domain.planroute.PlanRoute;
import uz.uzkassa.smartpossupply.domain.planroute.PlanRouteCustomerBranch;
import uz.uzkassa.smartpossupply.domain.planroute.PlanRouteCustomerBranchAttachment;
import uz.uzkassa.smartpossupply.domain.planroute.PlanRouteExpeditor;
import uz.uzkassa.smartpossupply.domain.planroute.PlanRouteItem;
import uz.uzkassa.smartpossupply.domain.planroutetemplate.PlanRouteTemplate;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.dto.base.ResultList;
import uz.uzkassa.smartpossupply.dto.customer.CustomerWithLimitDTO;
import uz.uzkassa.smartpossupply.dto.customerbranch.CustomerBranchAddressDTO;
import uz.uzkassa.smartpossupply.dto.equipment.EquipmentDetailDTO;
import uz.uzkassa.smartpossupply.dto.file.FileDTO;
import uz.uzkassa.smartpossupply.dto.file.PhotoByteArrayDTO;
import uz.uzkassa.smartpossupply.dto.file.PhotoDTO;
import uz.uzkassa.smartpossupply.dto.payload.Payload;
import uz.uzkassa.smartpossupply.dto.planroute.MobilePlanRouteDTO;
import uz.uzkassa.smartpossupply.dto.planroute.PlanRouteCheckLocationDTO;
import uz.uzkassa.smartpossupply.dto.planroute.PlanRouteCustomerBranchAttachmentDTO;
import uz.uzkassa.smartpossupply.dto.planroute.PlanRouteCustomerBranchDetailDTO;
import uz.uzkassa.smartpossupply.dto.planroute.PlanRouteCustomerStatisticsDTO;
import uz.uzkassa.smartpossupply.dto.planroute.PlanRouteDTO;
import uz.uzkassa.smartpossupply.dto.planroute.PlanRouteDetailDTO;
import uz.uzkassa.smartpossupply.dto.planroute.PlanRouteFinishDTO;
import uz.uzkassa.smartpossupply.dto.planroute.PlanRouteItemDetailDTO;
import uz.uzkassa.smartpossupply.dto.planroute.PlanRouteListDTO;
import uz.uzkassa.smartpossupply.dto.planroute.PlanRouteLookUpDTO;
import uz.uzkassa.smartpossupply.dto.planroute.PlanRouteMobileLookUpDTO;
import uz.uzkassa.smartpossupply.dto.planroute.PlanRouteReportDTO;
import uz.uzkassa.smartpossupply.dto.planroute.PlanRouteReportTotalQtyDTO;
import uz.uzkassa.smartpossupply.enums.DayOfWeekEnum;
import uz.uzkassa.smartpossupply.enums.PlanRouteAttachmentType;
import uz.uzkassa.smartpossupply.enums.PlanRouteCustomerBranchStatus;
import uz.uzkassa.smartpossupply.enums.PlanRouteReason;
import uz.uzkassa.smartpossupply.enums.PlanRouteStatus;
import uz.uzkassa.smartpossupply.enums.Prefix;
import uz.uzkassa.smartpossupply.filters.BaseFilter;
import uz.uzkassa.smartpossupply.filters.CompanyFilter;
import uz.uzkassa.smartpossupply.filters.PlanRouteFilter;
import uz.uzkassa.smartpossupply.interfaces.Crud;
import uz.uzkassa.smartpossupply.numbering.NumberingContext;
import uz.uzkassa.smartpossupply.repository.CustomerBranchRepository;
import uz.uzkassa.smartpossupply.repository.PartnerRepository;
import uz.uzkassa.smartpossupply.repository.PlanRouteCustomerBranchAttachmentRepository;
import uz.uzkassa.smartpossupply.repository.PlanRouteCustomerBranchRepository;
import uz.uzkassa.smartpossupply.repository.PlanRouteItemRepository;
import uz.uzkassa.smartpossupply.repository.PlanRouteRepository;
import uz.uzkassa.smartpossupply.repository.PlanRouteTemplateRepository;
import uz.uzkassa.smartpossupply.repository.RegionRepository;
import uz.uzkassa.smartpossupply.utils.SecurityUtils;
import uz.uzkassa.smartpossupply.validator.ValidationContext;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 20.01.2023 10:38
 */
@Service
@Transactional(readOnly = true)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Slf4j
public class PlanRouteService extends BaseService implements Crud<Long, PlanRouteDTO, PlanRouteDetailDTO, PlanRouteListDTO, PlanRouteFilter> {

    PlanRouteRepository planRouteRepository;
    PlanRouteTemplateRepository planRouteTemplateRepository;
    PlanRouteItemRepository planRouteItemRepository;
    PlanRouteCustomerBranchRepository planRouteCustomerBranchRepository;
    FileUploadService fileUploadService;
    CustomerBranchRepository customerBranchRepository;
    PlanRouteCustomerBranchAttachmentRepository planRouteCustomerBranchAttachmentRepository;
    PartnerRepository partnerRepository;
    RegionRepository regionRepository;
    NumberingContext numberingContext;
    ValidationContext validationContext;

    @Override
    public void validate(PlanRouteDTO planRouteDTO) {
        if (planRouteDTO.getTemplateId() == null) {
            throw validationService.badRequestExceptionThrow("Template is required").get();
        }
        if (planRouteDTO.getAgentId() == null) {
            throw validationService.badRequestExceptionThrow("Agent is required").get();
        }
        if (planRouteDTO.getFromDate() == null) {
            throw validationService.badRequestExceptionThrow("From date is required").get();
        }
        if (planRouteDTO.getToDate() == null) {
            throw validationService.badRequestExceptionThrow("To date is required").get();
        }
        if (CollectionUtils.isEmpty(planRouteDTO.getItems())) {
            throw validationService.badRequestExceptionThrow("Items are required").get();
        }
        if (planRouteDTO.getId() == null && planRouteRepository.existsByAgentIdAndStatusAndDeletedIsFalse(planRouteDTO.getAgentId(), PlanRouteStatus.OPEN)) {
            throw validationService.badRequestExceptionThrow("Этот агент имеет открытый маршрут", "Agent already has plan route").get();
        }
        if (planRouteDTO.getId() != null && planRouteRepository.existsByAgentIdAndStatusAndDeletedIsFalseAndIdNot(planRouteDTO.getAgentId(), PlanRouteStatus.OPEN, planRouteDTO.getId())) {
            throw validationService.badRequestExceptionThrow("Этот агент имеет открытый маршрут", "Agent already has plan route").get();
        }
    }

    @Transactional
    public Long create(PlanRouteDTO planRouteDTO) {
        validationContext.validate(planRouteDTO);
        Map<String, String> numberingMap = numberingContext.generateNumber(Prefix.PLAN_ROUTE, getCurrentCompanyId());
        PlanRoute planRoute = new PlanRoute();
        planRoute.setIncrementNumber(Long.valueOf(numberingMap.get(INCREMENT_NUMBER)));
        planRoute.setTemplateId(planRouteDTO.getTemplateId());
        planRoute.setAgentId(planRouteDTO.getAgentId());
        planRoute.setCompanyId(getCurrentCompanyId());
        planRoute.setFromDate(planRouteDTO.getFromDate());
        planRoute.setToDate(planRouteDTO.getToDate());
        planRoute.setRegionId(planRouteDTO.getRegionId());
        planRouteTemplateRepository.findById(planRouteDTO.getTemplateId()).ifPresent(planRouteTemplate -> planRoute.setPlanRouteNumber(planRouteTemplate.getName()));
        if (StringUtils.isEmpty(planRoute.getPlanRouteNumber())) {
            planRoute.setPlanRouteNumber(numberingMap.get(NUMBER));
        }
        planRouteRepository.save(planRoute);
        if (!CollectionUtils.isEmpty(planRouteDTO.getExpeditorIds())) {
            planRouteDTO.getExpeditorIds().forEach(expeditorId -> {
                PlanRouteExpeditor expeditor = new PlanRouteExpeditor();
                expeditor.setExpeditorId(expeditorId);
                planRoute.addExpeditor(expeditor);
            });
        }

        planRouteDTO.getItems().forEach(planRouteItemDTO -> {
            PlanRouteItem planRouteItem = new PlanRouteItem();
            planRouteItem.setVisitPlanDate(planRouteItemDTO.getVisitPlanDate());
            planRoute.addItem(planRouteItem);
            planRouteItemRepository.save(planRouteItem);
            planRouteItemDTO.getCustomerBranches().forEach(planRouteCustomerBranchDTO -> {
                PlanRouteCustomerBranch planRouteCustomerBranch = new PlanRouteCustomerBranch();
                planRouteCustomerBranch.setCustomerBranchId(planRouteCustomerBranchDTO.getCustomerBranchId());
                if (planRouteItem.getVisitPlanDate().isBefore(LocalDate.now())) {
                    planRouteCustomerBranch.setStatus(PlanRouteCustomerBranchStatus.NOT_VISITED);
                }
                planRouteItem.addItem(planRouteCustomerBranch);
            });
        });
        return planRoute.getId();
    }

    @Transactional
    public void createQueue(Payload payload) {
        this.createByTemplateId(payload.getId());
    }

    @Transactional
    public Long createByTemplateId(Long templateId) {
        return planRouteTemplateRepository.findById(templateId).map(planRouteTemplate -> {
            if (planRouteRepository.existsByAgentIdAndStatusAndDeletedIsFalse(planRouteTemplate.getAgentId(), PlanRouteStatus.OPEN)) {
                log.error("Этот агент имеет открытый маршрут");
                throw validationService.badRequestExceptionThrow("Этот агент имеет открытый маршрут").get();
            }
            log.info("Plan route template: {}", planRouteTemplate.getName());
            Map<String, String> numberingMap = numberingContext.generateNumber(Prefix.PLAN_ROUTE, getCurrentCompanyId());
            //LocalDate firstDayOfWeek = LocalDateTime.now().with(ChronoField.DAY_OF_WEEK, 1).toLocalDate();

            LocalDate now = LocalDate.now();
            LocalDate firstDayOfWeek = now.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
            LocalDate lastDayOfWeek = now.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

            PlanRoute planRoute = new PlanRoute();
            planRoute.setPlanRouteNumber(Optional.ofNullable(planRouteTemplate.getName()).orElse(numberingMap.get(NUMBER)));
            planRoute.setIncrementNumber(Long.valueOf(numberingMap.get(INCREMENT_NUMBER)));
            planRoute.setTemplateId(planRouteTemplate.getId());
            planRoute.setAgentId(planRouteTemplate.getAgentId());
            if (!CollectionUtils.isEmpty(planRouteTemplate.getExpeditors())) {
                planRouteTemplate.getExpeditors().forEach(expeditor -> {
                    PlanRouteExpeditor planRouteExpeditor = new PlanRouteExpeditor();
                    planRouteExpeditor.setExpeditorId(expeditor.getExpeditorId());
                    planRoute.addExpeditor(planRouteExpeditor);
                });
            }
            //planRoute.setFromDate(firstDayOfWeek);
            //planRoute.setToDate(firstDayOfWeek.plusDays(6));
            planRoute.setFromDate(firstDayOfWeek);
            planRoute.setToDate(lastDayOfWeek);

            planRoute.setRegionId(planRouteTemplate.getRegionId());
            planRoute.setCompanyId(planRouteTemplate.getBranch().getCompanyId());
            planRouteRepository.save(planRoute);

            Collections.sort(planRouteTemplate.getItems());
            planRouteTemplate.getItems().forEach(planRouteTemplateItem -> {
                PlanRouteItem planRouteItem = new PlanRouteItem();
                DayOfWeek dayOfWeek = planRouteTemplateItem.getDay().getDayOfWeek();
                planRouteItem.setVisitPlanDate(firstDayOfWeek.with(dayOfWeek));
                planRoute.addItem(planRouteItem);
                planRouteItemRepository.save(planRouteItem);
                planRouteTemplateItem.getCustomerBranches().forEach(planRouteTemplateCustomerBranch -> {
                    PlanRouteCustomerBranch planRouteCustomerBranch = new PlanRouteCustomerBranch();
                    planRouteCustomerBranch.setCustomerBranchId(planRouteTemplateCustomerBranch.getCustomerBranchId());
                    if (planRouteItem.getVisitPlanDate().isBefore(LocalDate.now())) {
                        planRouteCustomerBranch.setStatus(PlanRouteCustomerBranchStatus.NOT_VISITED);
                    }
                    planRouteItem.addItem(planRouteCustomerBranch);
                });
            });
            return planRoute.getId();
        }).orElseThrow(validationService.entityNotFoundExceptionThrow(PlanRouteTemplate.class.getSimpleName(), "id", templateId));
    }

    @Transactional
    public Long update(Long id, PlanRouteDTO planRouteDTO) {
        planRouteDTO.setId(id);
        validationContext.validate(planRouteDTO);
        return planRouteRepository.findById(id).map(planRoute -> {
            User agent = userRepository.findById(planRouteDTO.getAgentId()).orElseThrow(validationService.entityNotFoundExceptionThrow(User.class.getSimpleName(), "id", planRouteDTO.getAgentId()));
            planRoute.setAgent(agent);
            planRoute.setAgentId(agent.getId());
            if (!CollectionUtils.isEmpty(planRouteDTO.getExpeditorIds())) {
                planRoute.getExpeditors().clear();
                planRouteDTO.getExpeditorIds().forEach(expeditorId -> {
                    PlanRouteExpeditor expeditor = new PlanRouteExpeditor();
                    expeditor.setExpeditorId(expeditorId);
                    planRoute.addExpeditor(expeditor);
                });
            } else {
                planRoute.getExpeditors().clear();
            }
            planRoute.setFromDate(planRouteDTO.getFromDate());
            planRoute.setToDate(planRouteDTO.getToDate());

            if (planRouteDTO.getRegionId() != null) {
                Region region = regionRepository.findById(planRouteDTO.getRegionId()).orElseThrow(validationService.entityNotFoundExceptionThrow(Region.class.getSimpleName(), "id", planRouteDTO.getRegionId()));
                planRoute.setRegion(region);
                planRoute.setRegionId(region.getId());
            }

            HashMap<Long, PlanRouteItem> planRouteItemsMap = new HashMap<>();
            HashMap<Long, PlanRouteCustomerBranch> planRouteCustomerBranchMap = new HashMap<>();
            planRoute.getItems().forEach(planRouteItem -> planRouteItemsMap.put(planRouteItem.getId(), planRouteItem));
            planRoute.getItems().forEach(planRouteItem -> planRouteItem.getPlanRouteCustomerBranches().forEach(planRouteCustomerBranch ->
                    planRouteCustomerBranchMap.put(planRouteCustomerBranch.getId(), planRouteCustomerBranch)));
            planRouteDTO.getItems()
                    .forEach(planRouteItemDTO -> {
                        PlanRouteItem planRouteItem;
                        if (planRouteItemDTO.getId() != null && planRouteItemsMap.get(planRouteItemDTO.getId()) != null) {
                            planRouteItem = planRouteItemsMap.get(planRouteItemDTO.getId());
                        } else {
                            planRouteItem = new PlanRouteItem();
                        }
                        planRouteItem.setVisitPlanDate(planRouteItemDTO.getVisitPlanDate());
                        planRouteItemDTO.getCustomerBranches().forEach(planRouteCustomerBranchDTO -> {
                            PlanRouteCustomerBranch planRouteCustomerBranch;
                            if (planRouteCustomerBranchDTO.getId() != null && planRouteCustomerBranchMap.get(planRouteCustomerBranchDTO.getId()) != null) {
                                planRouteCustomerBranch = planRouteCustomerBranchMap.get(planRouteCustomerBranchDTO.getId());
                            } else {
                                planRouteCustomerBranch = new PlanRouteCustomerBranch();
                            }
                            planRouteCustomerBranch.setPlanRouteItemId(planRouteItem.getId());
                            planRouteCustomerBranch.setCustomerBranchId(planRouteCustomerBranchDTO.getCustomerBranchId());
                            if (planRouteItem.getVisitPlanDate().isBefore(LocalDate.now())) {
                                planRouteCustomerBranch.setStatus(PlanRouteCustomerBranchStatus.NOT_VISITED);
                            }
                            planRouteItem.addItem(planRouteCustomerBranch);
                            if (planRouteCustomerBranch.getId() != null) {
                                planRouteCustomerBranchMap.remove(planRouteCustomerBranch.getId());
                            }
                        });
                        planRoute.addItem(planRouteItem);
                        if (planRouteItem.getId() != null) {
                            planRouteItemsMap.remove(planRouteItem.getId());
                        }
                    });
            planRouteRepository.save(planRoute);
            if (!planRouteCustomerBranchMap.isEmpty()) {
                planRouteCustomerBranchRepository.deleteAllByIdInBatch(planRouteCustomerBranchMap.keySet());
            }
            if (!planRouteItemsMap.isEmpty()) {
                planRouteItemRepository.deleteAllByIdInBatch(planRouteItemsMap.keySet());
            }
            return planRoute.getId();
        }).orElseThrow(validationService.entityNotFoundExceptionThrow(PlanRoute.class.getSimpleName(), "id", id));
    }

    @Transactional
    public Long addCustomerBranch(Long id, MobilePlanRouteDTO planRouteDTO) {
        PlanRouteItem planRouteItem = planRouteItemRepository.findById(id).orElseThrow(validationService.entityNotFoundExceptionThrow(PlanRouteItem.class.getSimpleName(), "id", id));
        if (planRouteItem.getPlanRouteCustomerBranches().stream().anyMatch(planRouteCustomerBranch -> planRouteCustomerBranch.getCustomerBranchId().equals(planRouteDTO.getCustomerBranchId()))) {
            throw validationService.badRequestExceptionThrow(localizationService.localize(MessageConstants.CUSTOMER_BRANCH_ALREADY_ADDED), localizationService.localize(MessageConstants.CUSTOMER_BRANCH_ALREADY_ADDED)).get();
        }
        PlanRouteCustomerBranch planRouteCustomerBranch = new PlanRouteCustomerBranch();
        planRouteCustomerBranch.setUnscheduled(true);
        planRouteCustomerBranch.setPlanRouteItemId(planRouteItem.getId());
        planRouteCustomerBranch.setCustomerBranchId(planRouteDTO.getCustomerBranchId());
        planRouteItem.addItem(planRouteCustomerBranch);
        planRouteItemRepository.save(planRouteItem);
        return planRouteItem.getId();
    }

    @Transactional
    public Long deleteCustomerBranch(Long id, MobilePlanRouteDTO planRouteDTO) {
        PlanRouteItem planRouteItem = planRouteItemRepository.findById(id).orElseThrow(validationService.entityNotFoundExceptionThrow(PlanRouteItem.class.getSimpleName(), "id", id));
        planRouteItem.getPlanRouteCustomerBranches().
                removeIf(planRouteCustomerBranch -> planRouteCustomerBranch.isUnscheduled()
                        && planRouteCustomerBranch.getCustomerBranchId().equals(planRouteDTO.getCustomerBranchId()));
        planRouteItemRepository.save(planRouteItem);
        return planRouteItem.getId();
    }

    public PlanRouteDetailDTO getByTemplateId(Long templateId) {
        return planRouteRepository.findFirstByTemplateIdAndStatusAndDeletedIsFalseOrderByIdDesc(templateId, PlanRouteStatus.OPEN).map(this::getDetails).orElse(null);
    }

    public PlanRouteDetailDTO get(Long id) {
        return planRouteRepository.getDetail(id).map(this::getDetails).orElseThrow(validationService.entityNotFoundExceptionThrow(PlanRoute.class.getSimpleName(), "id", id));
    }

    private PlanRouteDetailDTO getDetails(PlanRoute planRoute) {
        PlanRouteDetailDTO planRouteDetailDTO = new PlanRouteDetailDTO();
        planRouteDetailDTO.setId(planRoute.getId());
        planRouteDetailDTO.setPlanRouteNumber(planRoute.getPlanRouteNumber());
        if (planRoute.getAgentId() != null) {
            planRouteDetailDTO.setAgent(planRoute.getAgent().toCommonDTO());
        }
        if (!CollectionUtils.isEmpty(planRoute.getExpeditors())) {
            planRoute.getExpeditors().forEach(expeditor -> planRouteDetailDTO.addExpeditor(expeditor.getExpeditor().toCommonDTO()));
        }
        planRouteDetailDTO.setFromDate(planRoute.getFromDate());
        planRouteDetailDTO.setToDate(planRoute.getToDate());
        planRouteDetailDTO.setStatus(planRoute.getStatus());
        if (planRoute.getRegionId() != null) {
            planRouteDetailDTO.setRegion(planRoute.getRegion().toCommonDTO());
        }
        if (planRoute.getTemplateId() != null) {
            planRouteDetailDTO.setTemplate(planRoute.getTemplate().toCommonDTO());
        }
        Collections.sort(planRoute.getItems());
        AtomicInteger totalCount = new AtomicInteger(0);
        AtomicInteger totalVisitedCount = new AtomicInteger(0);
        Set<Long> totalCustomerBranches = new HashSet<>();
        planRoute.getItems().forEach(planRouteItem -> {
            PlanRouteItemDetailDTO planRouteItemDetailDTO = new PlanRouteItemDetailDTO();
            planRouteItemDetailDTO.setId(planRouteItem.getId());
            planRouteItemDetailDTO.setVisitPlanDate(planRouteItem.getVisitPlanDate());
            planRouteItem.getPlanRouteCustomerBranches().forEach(planRouteCustomerBranch -> {
                PlanRouteCustomerBranchDetailDTO planRouteCustomerBranchDetailDTO = new PlanRouteCustomerBranchDetailDTO();
                planRouteCustomerBranchDetailDTO.setId(planRouteCustomerBranch.getId());
                if (planRouteCustomerBranch.getCustomerBranchId() != null) {
                    totalCustomerBranches.add(planRouteCustomerBranch.getCustomerBranchId());
                    planRouteCustomerBranchDetailDTO.setCustomerBranch(new CustomerBranchAddressDTO(planRouteCustomerBranch.getCustomerBranch().getId(), planRouteCustomerBranch.getCustomerBranch().getName(),
                            addressMapper.toDetailDTO(planRouteCustomerBranch.getCustomerBranch().getAddress()), planRouteCustomerBranch.getCustomerBranch().getPhone(), planRouteCustomerBranch.getCustomerBranch().getCurrentBalance()));
                }
                planRouteCustomerBranchDetailDTO.setStatus(planRouteCustomerBranch.getStatus());
                if (planRouteCustomerBranch.getCustomerBranch() != null && planRouteCustomerBranch.getCustomerBranch().getCustomer() != null) {
                    planRouteCustomerBranchDetailDTO.setCustomer(new CustomerWithLimitDTO(planRouteCustomerBranch.getCustomerBranch().getCustomer().toCommonBaseDTO(), null, null, false, null, planRouteCustomerBranch.getCustomerBranch().getCustomer().getBrand()));
                }
                planRouteCustomerBranchDetailDTO.setStartDate(planRouteCustomerBranch.getStartDate());
                planRouteCustomerBranchDetailDTO.setEndDate(planRouteCustomerBranch.getEndDate());
                planRouteCustomerBranchDetailDTO.setReason(planRouteCustomerBranch.getReason());
                if (planRouteCustomerBranch.getOrder() != null) {
                    planRouteCustomerBranchDetailDTO.setOrder(planRouteCustomerBranch.getOrder().toCommonDTO());
                }
                if (PlanRouteCustomerBranchStatus.VISITED.equals(planRouteCustomerBranch.getStatus())) {
                    totalVisitedCount.getAndIncrement();
                }
                totalCount.getAndIncrement();
                planRouteItemDetailDTO.addCustomerBranch(planRouteCustomerBranchDetailDTO);
            });
            planRouteDetailDTO.addItem(planRouteItemDetailDTO);
            planRouteDetailDTO.setTotalCount(totalCount.intValue());
            planRouteDetailDTO.setTotalVisitedCount(totalVisitedCount.intValue());
            planRouteDetailDTO.setTotalCustomerBranch(totalCustomerBranches.size());
        });
        return planRouteDetailDTO;
    }

    public PlanRouteCustomerBranchDetailDTO getCustomerBranch(Long planRouteCustomerBranchId) {
        return planRouteCustomerBranchRepository.findById(planRouteCustomerBranchId).map(planRouteCustomerBranch -> {
            PlanRouteCustomerBranchDetailDTO planRouteCustomerBranchDetailDTO = new PlanRouteCustomerBranchDetailDTO();
            planRouteCustomerBranchDetailDTO.setId(planRouteCustomerBranch.getId());
            planRouteCustomerBranchDetailDTO.setOrderingBlocked(planRouteCustomerBranch.getCustomerBranch().isOrderingBlocked());
            planRouteCustomerBranchDetailDTO.setCustomerBranch(new CustomerBranchAddressDTO(planRouteCustomerBranch.getCustomerBranch().getId(), planRouteCustomerBranch.getCustomerBranch().getName(),
                    addressMapper.toDetailDTO(planRouteCustomerBranch.getCustomerBranch().getAddress()), planRouteCustomerBranch.getCustomerBranch().getPhone(), planRouteCustomerBranch.getCustomerBranch().getCurrentBalance(),
                    planRouteCustomerBranch.getCustomerBranch().getEquipments().stream().map(equipment -> {
                        EquipmentDetailDTO equipmentDetailDTO = new EquipmentDetailDTO();
                        equipmentDetailDTO.setId(equipment.getId());
                        equipmentDetailDTO.setName(equipment.getName());
                        equipmentDetailDTO.setInventoryNumber(equipment.getInventoryNumber());
                        equipmentDetailDTO.setBrand(equipment.getBrand());
                        equipmentDetailDTO.setType(equipment.getType());
                        equipmentDetailDTO.setCustomerBranch(equipment.getCustomerBranch().toCommonDTO());
                        return equipmentDetailDTO;
                    }).toList()));
            partnerRepository.findByCompanyIdAndCustomerIdAndSupplierId(SecurityUtils.getCurrentCompanyId(), planRouteCustomerBranch.getCustomerBranch().getCustomerId(), SecurityUtils.getCurrentCompanyId())
                    .ifPresent(partners -> planRouteCustomerBranchDetailDTO
                            .setCustomer(new CustomerWithLimitDTO(partners.getCustomer().toCommonBaseDTO(), partners.getLimitBalance(), partners.getCurrentBalance(), partners.isHasLimit(), partners.getCustomer().getPhone(), partners.getCustomer().getBrand())));
            planRouteCustomerBranchDetailDTO.setStatus(planRouteCustomerBranch.getStatus());
            if (planRouteCustomerBranch.getOrder() != null) {
                planRouteCustomerBranchDetailDTO.setOrder(planRouteCustomerBranch.getOrder().toCommonDTO());
            }
            planRouteCustomerBranchDetailDTO.setStartDate(planRouteCustomerBranch.getStartDate());
            planRouteCustomerBranchDetailDTO.setEndDate(planRouteCustomerBranch.getEndDate());
            return planRouteCustomerBranchDetailDTO;
        }).orElseThrow(validationService.entityNotFoundExceptionThrow(PlanRouteItem.class.getSimpleName(), "planRouteCustomerBranchId", planRouteCustomerBranchId));
    }

    public PlanRouteDetailDTO getForMobile(Long id, DayOfWeekEnum day) {
        return planRouteRepository.findById(id).map(planRoute -> getPlanRoute(planRoute, day, null, null, null)).orElseThrow(validationService.entityNotFoundExceptionThrow(PlanRoute.class.getSimpleName(), "id", id));
    }

    public PlanRouteDetailDTO getCurrentPlanRouteForMobile(Long agentId, DayOfWeekEnum
            day, PlanRouteCustomerBranchStatus status) {
        return planRouteRepository.findFirstByAgentIdAndStatusOrderByCreatedDateDesc(agentId, PlanRouteStatus.OPEN).map(planRoute -> getPlanRoute(planRoute, day, status, null, null)).orElseThrow(validationService.entityNotFoundExceptionThrow(PlanRoute.class.getSimpleName(), "agentId", agentId));
    }

    public PlanRouteDetailDTO getCurrentWeekPlanRouteForMobile(Long agentId, PlanRouteCustomerBranchStatus status) {
        return planRouteRepository.findFirstByAgentIdAndStatusOrderByCreatedDateDesc(agentId, PlanRouteStatus.OPEN)
                .map(planRoute -> getPlanRoute(planRoute, null, status, DayOfWeek.MONDAY, DayOfWeek.SUNDAY))
                .orElseThrow(validationService.entityNotFoundExceptionThrow(PlanRoute.class.getSimpleName(), "agentId", agentId));
    }

    private PlanRouteDetailDTO getPlanRoute(PlanRoute planRoute, DayOfWeekEnum day, PlanRouteCustomerBranchStatus status, DayOfWeek startDay, DayOfWeek endDay) {
        PlanRouteDetailDTO planRouteDetailDTO = new PlanRouteDetailDTO();
        planRouteDetailDTO.setId(planRoute.getId());
        planRouteDetailDTO.setPlanRouteNumber(planRoute.getPlanRouteNumber());
        planRouteDetailDTO.setFromDate(planRoute.getFromDate());
        planRouteDetailDTO.setToDate(planRoute.getToDate());
        planRouteDetailDTO.setStatus(planRoute.getStatus());
        if (planRoute.getAgentId() != null) {
            planRouteDetailDTO.setAgent(planRoute.getAgent().toCommonDTO());
        }
        if (!CollectionUtils.isEmpty(planRoute.getExpeditors())) {
            planRoute.getExpeditors().forEach(expeditor -> planRouteDetailDTO.addExpeditor(expeditor.getExpeditor().toCommonDTO()));
        }
        if (planRoute.getRegionId() != null) {
            planRouteDetailDTO.setRegion(planRoute.getRegion().toCommonDTO());
        }
        List<PlanRouteItem> items;
        if (startDay != null && endDay != null) {
            items = planRoute.getItems()
                    .stream()
                    .filter(planRouteItem -> {
                        LocalDate visitDate = planRouteItem.getVisitPlanDate();
                        DayOfWeek itemDay = visitDate.getDayOfWeek();

                        LocalDate now = LocalDate.now();
                        LocalDate startOfWeek = now.with(DayOfWeek.MONDAY);
                        LocalDate endOfWeek = now.with(DayOfWeek.SUNDAY);

                        return !visitDate.isBefore(startOfWeek)
                                && !visitDate.isAfter(endOfWeek)
                                && itemDay.getValue() >= startDay.getValue()
                                && itemDay.getValue() <= endDay.getValue();
                    }).toList();
        } else {
            items = planRoute.getItems()
                    .stream()
                    .filter(planRouteItem ->
                            day.getCode().equals(planRouteItem.getVisitPlanDate().getDayOfWeek().name()))
                    .toList();
        }
        AtomicInteger totalCount = new AtomicInteger(0);
        AtomicInteger totalVisitedCount = new AtomicInteger(0);
        items.forEach(planRouteItem -> {
            PlanRouteItemDetailDTO planRouteItemDetailDTO = new PlanRouteItemDetailDTO();
            planRouteItemDetailDTO.setId(planRouteItem.getId());
            planRouteItemDetailDTO.setVisitPlanDate(planRouteItem.getVisitPlanDate());
            planRouteItemDetailDTO.setDay(DayOfWeekEnum.valueOf(planRouteItem.getVisitPlanDate().getDayOfWeek().name()));
            Set<PlanRouteCustomerBranch> planRouteCustomerBranches;
            if (status != null) {
                planRouteCustomerBranches = planRouteItem.getPlanRouteCustomerBranches().stream().filter(planRouteCustomerBranch -> planRouteCustomerBranch.getStatus().equals(status)).collect(Collectors.toSet());
            } else {
                planRouteCustomerBranches = planRouteItem.getPlanRouteCustomerBranches();
            }
            planRouteCustomerBranches.forEach(planRouteCustomerBranch -> {
                PlanRouteCustomerBranchDetailDTO planRouteCustomerBranchDetailDTO = new PlanRouteCustomerBranchDetailDTO();
                planRouteCustomerBranchDetailDTO.setId(planRouteCustomerBranch.getId());
                planRouteCustomerBranchDetailDTO.setCustomerBranch(new CustomerBranchAddressDTO(planRouteCustomerBranch.getCustomerBranch().getId(), planRouteCustomerBranch.getCustomerBranch().getName(),
                        addressMapper.toDetailDTO(planRouteCustomerBranch.getCustomerBranch().getAddress()), planRouteCustomerBranch.getCustomerBranch().getPhone()));
                planRouteCustomerBranchDetailDTO.setStatus(planRouteCustomerBranch.getStatus());
                planRouteCustomerBranchDetailDTO.setStartDate(planRouteCustomerBranch.getStartDate());
                planRouteCustomerBranchDetailDTO.setEndDate(planRouteCustomerBranch.getEndDate());
                partnerRepository.findByCompanyIdAndCustomerIdAndSupplierId(SecurityUtils.getCurrentCompanyId(), planRouteCustomerBranch.getCustomerBranch().getCustomerId(), SecurityUtils.getCurrentCompanyId())
                        .ifPresent(partners -> planRouteCustomerBranchDetailDTO
                                .setCustomer(new CustomerWithLimitDTO(partners.getCustomer().toCommonBaseDTO(), partners.getLimitBalance(), partners.getCurrentBalance(), partners.isHasLimit(), partners.getCustomer().getPhone(), partners.getCustomer().getBrand())));
                planRouteCustomerBranchDetailDTO.setUnscheduled(planRouteCustomerBranch.isUnscheduled());
                planRouteCustomerBranchDetailDTO.setOrderingBlocked(planRouteCustomerBranch.getCustomerBranch().isOrderingBlocked());
                planRouteCustomerBranchDetailDTO.setReason(planRouteCustomerBranch.getReason());
                if (planRouteCustomerBranch.getOrder() != null) {
                    planRouteCustomerBranchDetailDTO.setOrder(planRouteCustomerBranch.getOrder().toCommonDTO());
                }
                planRouteItemDetailDTO.addCustomerBranch(planRouteCustomerBranchDetailDTO);
                totalCount.getAndIncrement();
                if (PlanRouteCustomerBranchStatus.VISITED.equals(planRouteCustomerBranch.getStatus())) {
                    totalVisitedCount.getAndIncrement();
                }

            });
            planRouteDetailDTO.setTotalCount(totalCount.intValue());
            planRouteDetailDTO.setTotalVisitedCount(totalVisitedCount.intValue());
            planRouteDetailDTO.addItem(planRouteItemDetailDTO);
        });
        return planRouteDetailDTO;
    }

    @Override
    public Page<PlanRouteListDTO> getList(PlanRouteFilter filter) {
        ResultList<PlanRoute> resultList = planRouteRepository.getResultList(filter);
        List<PlanRouteListDTO> result = resultList
                .getList()
                .stream()
                .map(planRoute -> {
                    PlanRouteListDTO planRouteListDTO = new PlanRouteListDTO();
                    planRouteListDTO.setId(planRoute.getId());
                    planRouteListDTO.setPlanRouteNumber(planRoute.getPlanRouteNumber());
                    if (planRoute.getAgentId() != null) {
                        planRouteListDTO.setAgent(planRoute.getAgent().toCommonDTO());
                    }
                    if (!CollectionUtils.isEmpty(planRoute.getExpeditors())) {
                        planRoute.getExpeditors().forEach(expeditor -> planRouteListDTO.addExpeditor(expeditor.getExpeditor().toCommonDTO()));
                    }
                    planRouteListDTO.setFromDate(planRoute.getFromDate());
                    planRouteListDTO.setToDate(planRoute.getToDate());
                    planRouteListDTO.setStatus(planRoute.getStatus());
                    planRouteListDTO.setCreatedDate(planRoute.getCreatedDate());
                    if (planRoute.getRegionId() != null) {
                        planRouteListDTO.setRegion(planRoute.getRegion().toCommonDTO());
                    }
                    return planRouteListDTO;
                }).toList();
        return new PageImpl<>(result, filter.getSortedPageable(), resultList.getCount());
    }

    @Override
    @Transactional
    public void delete(Long id) {
        planRouteRepository.deleteById(id);
    }

    @Transactional
    public Long finish(PlanRouteFinishDTO planRouteFinishDTO) {
        return planRouteCustomerBranchRepository.findById(planRouteFinishDTO.getPlanRouteCustomerBranchId())
                .map(planRouteCustomerBranch -> {
                    if (planRouteCustomerBranch.getOrder() == null) {
                        if (planRouteFinishDTO.getReason() == null) {
                            throw validationService.badRequestExceptionThrow("Reason is required").get();
                        }
                        if (PlanRouteReason.CLOSED.equals(planRouteFinishDTO.getReason()) && CollectionUtils.isEmpty(planRouteCustomerBranch.getAttachments())) {
                            throw validationService.badRequestExceptionThrow(localizationService.localize(MessageConstants.UPLOAD_PHOTO_REPORT)).get();
                        }
                    }
                    planRouteCustomerBranch.setStatus(PlanRouteCustomerBranchStatus.VISITED);
                    planRouteCustomerBranch.setEndDate(LocalDateTime.now());
                    planRouteCustomerBranch.setDescription(planRouteFinishDTO.getDescription());
                    planRouteCustomerBranch.setReason(planRouteFinishDTO.getReason());
                    planRouteCustomerBranchRepository.save(planRouteCustomerBranch);
                    return planRouteCustomerBranch.getId();
                })
                .orElseThrow(validationService.entityNotFoundExceptionThrow(PlanRouteItem.class.getSimpleName(), "id", planRouteFinishDTO.getPlanRouteCustomerBranchId()));
    }

    @Transactional
    public void start(Long planRouteCustomerBranchId) {
        planRouteCustomerBranchRepository.findById(planRouteCustomerBranchId).map(planRouteCustomerBranch -> {
            planRouteCustomerBranch.setStartDate(LocalDateTime.now());
            planRouteCustomerBranch.setStatus(PlanRouteCustomerBranchStatus.VISITING);
            planRouteCustomerBranchRepository.save(planRouteCustomerBranch);
            return planRouteCustomerBranch;
        }).orElseThrow(validationService.entityNotFoundExceptionThrow(PlanRouteCustomerBranch.class.getSimpleName(), "id", planRouteCustomerBranchId));
    }

    private boolean validateCoordinates(double customerBranchLongitude, double customerBranchLatitude,
                                        double agentLongitude, double agentLatitude) {

        final int radius = 6373477;

        double difLat = Math.toRadians(customerBranchLatitude - agentLatitude);

        double difLong = Math.toRadians(customerBranchLongitude - agentLongitude);

        double a = Math.sin(difLat / 2) * Math.sin(difLat / 2) +
                Math.cos(Math.toRadians(customerBranchLatitude)) * Math.cos(Math.toRadians(agentLatitude)) *
                        Math.sin(difLong / 2) * Math.sin(difLong / 2);
        double distance = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a)) * radius;
        return distance < 300;
    }

    @Transactional
    public void changeAgent(Long id, Long agentId) {
        planRouteRepository.findById(id).map(planRoute -> {
            planRoute.setAgentId(agentId);
            planRouteRepository.save(planRoute);
            return planRoute;
        }).orElseThrow(validationService.entityNotFoundExceptionThrow(PlanRoute.class.getSimpleName(), "id", id));
    }

    public List<PlanRouteMobileLookUpDTO> lookUpForMobile(PlanRouteFilter filter) {
        if (filter.getAgentId() == null) {
            filter.setAgentId(SecurityUtils.getCurrentUserId());
        }
        return planRouteRepository.getResultList(filter).getList()
                .stream().map(planRoute -> {
                    PlanRouteMobileLookUpDTO dto = new PlanRouteMobileLookUpDTO();
                    dto.setId(planRoute.getId());
                    dto.setName(planRoute.getName());
                    dto.setFromDate(planRoute.getFromDate());
                    dto.setToDate(planRoute.getToDate());
                    return dto;
                }).toList();
    }

    public List<PlanRouteLookUpDTO> lookUp(PlanRouteFilter filter) {
        return planRouteRepository.getResultList(filter).getList().stream().map(planRoute -> {
            PlanRouteLookUpDTO planRouteLookUpDTO = new PlanRouteLookUpDTO();
            planRouteLookUpDTO.setId(planRoute.getId());
            planRouteLookUpDTO.setPlanRouteNumber(planRoute.getPlanRouteNumber());
            planRouteLookUpDTO.setFromDate(planRoute.getFromDate());
            planRouteLookUpDTO.setToDate(planRoute.getToDate());
            if (filter.isWithExpeditor() && !CollectionUtils.isEmpty(planRoute.getExpeditors())) {
                planRouteLookUpDTO.setExpeditors(planRoute.getExpeditors().stream().map(SimpleEntity::toCommonDTO).toList());
            }
            return planRouteLookUpDTO;
        }).toList();
    }

    public PlanRouteCustomerStatisticsDTO getCustomerStatistics(PlanRouteFilter filter) {
        return planRouteRepository.getCustomerStatistics(filter);
    }

    public Page<PlanRouteReportDTO> report(PlanRouteFilter filter) {
        return planRouteRepository.getReportByAgent(filter);
    }

    public Page<PlanRouteReportDTO> reportByAgent(PlanRouteFilter filter) {
        return planRouteRepository.getReportByAgent(filter);
    }

    public List<PlanRouteReportDTO> reportList(PlanRouteFilter filter) {
        filter.setSize(10000);
        return planRouteRepository.getReportByAgent(filter).getContent();
    }

    public PlanRouteReportTotalQtyDTO reportTotalQty(PlanRouteFilter filter) {
        return planRouteRepository.getReportByAgentTotalQty(filter);
    }

    public PlanRouteReportTotalQtyDTO reportByAgentTotalQty(PlanRouteFilter filter) {
        return planRouteRepository.getReportByAgentTotalQty(filter);
    }

    @Transactional
    public void updateStatus() {
        planRouteRepository.findAllByStatusAndToDateIsBefore(PlanRouteStatus.OPEN, LocalDate.now()).forEach(planRoute -> {
            AtomicInteger completedCount = new AtomicInteger();
            AtomicInteger totalCount = new AtomicInteger();
            planRoute.getItems().
                    forEach(planRouteItem -> planRouteItem.getPlanRouteCustomerBranches()
                            .forEach(planRouteCustomerBranch -> {
                                if (PlanRouteCustomerBranchStatus.VISITED.equals(planRouteCustomerBranch.getStatus())) {
                                    completedCount.getAndIncrement();
                                }
                                totalCount.getAndIncrement();
                            }));
            if (totalCount.get() == completedCount.get()) {
                planRoute.setStatus(PlanRouteStatus.CLOSED);
            } else {
                planRoute.setStatus(PlanRouteStatus.PARTIALLY_CLOSED);
            }
            planRouteRepository.save(planRoute);
        });
    }

    @Transactional
    public List<FileDTO> uploadPhotoWithBytArray(Long
                                                         planRouteCustomerBranchId, List<PhotoByteArrayDTO> files, PlanRouteAttachmentType attachmentType) {
        return this.uploadPhoto(planRouteCustomerBranchId, files.stream().map(photoByteArrayDTO -> new CustomMultipartFile(photoByteArrayDTO.getPhoto(), photoByteArrayDTO.getFileName(), photoByteArrayDTO.getContentType())).collect(Collectors.toList()), attachmentType);
    }

    @Transactional
    public List<FileDTO> uploadPhotoWithBase64String(Long
                                                             planRouteCustomerBranchId, List<PhotoDTO> files, PlanRouteAttachmentType attachmentType) {
        return this.uploadPhoto(planRouteCustomerBranchId, files.stream().map(photoDTO -> new CustomMultipartFile(Base64.getDecoder().decode(photoDTO.getBody().getBytes()), photoDTO.getFileName(), photoDTO.getContentType())).collect(Collectors.toList()), attachmentType);
    }

    @Transactional
    public List<FileDTO> uploadPhoto(Long
                                             planRouteCustomerBranchId, List<MultipartFile> files, PlanRouteAttachmentType attachmentType) {
        return planRouteCustomerBranchRepository.findById(planRouteCustomerBranchId)
                .map(planRouteCustomerBranch -> {
                    List<FileDTO> result = new ArrayList<>();
                    List<PlanRouteCustomerBranchAttachment> attachments = planRouteCustomerBranch.getAttachments();
                    files.forEach(file -> {
                        FileUpload attachment = fileUploadService.fileUpload(file);
                        PlanRouteCustomerBranchAttachment planRouteCustomerBranchAttachment = new PlanRouteCustomerBranchAttachment();
                        planRouteCustomerBranchAttachment.setPlanRouteCustomerBranchId(planRouteCustomerBranch.getId());
                        planRouteCustomerBranchAttachment.setAttachmentType(attachmentType);
                        planRouteCustomerBranchAttachment.setAttachmentId(attachment.getId());
                        attachments.add(planRouteCustomerBranchAttachment);
                        result.add(attachment.toFileDTO());
                    });
                    planRouteCustomerBranch.setAttachments(attachments);
                    return result;
                }).orElseThrow(validationService.entityNotFoundExceptionThrow(PlanRouteCustomerBranch.class.getSimpleName(), "id", planRouteCustomerBranchId));
    }

    public Boolean checkLocation(PlanRouteCheckLocationDTO checkLocationDTO) {
        return customerBranchRepository.findById(checkLocationDTO.getCustomerBranchId()).map(customerBranch -> {
            if (customerBranch.getAddress() == null || StringUtils.isNoneEmpty(customerBranch.getAddress().getLongitude(), customerBranch.getAddress().getLatitude())) {
                throw validationService.badRequestExceptionThrow("Адрес филиала клиента недоступен", "Укажите долготу и широту адреса филиала клиента.").get();
            }
            try {
                return this.validateCoordinates(Double.parseDouble(customerBranch.getAddress().getLongitude()), Double.parseDouble(customerBranch.getAddress().getLatitude()), checkLocationDTO.getAgentLongitude(), checkLocationDTO.getAgentLatitude());
            } catch (NumberFormatException e) {
                throw validationService.badRequestExceptionThrow("Адрес филиала клиента недоступен", "Правильно укажите долготу и широту адреса филиала клиента.").get();
            }
        }).orElseThrow(validationService.entityNotFoundExceptionThrow(CustomerBranch.class.getSimpleName(), "id", checkLocationDTO.getCustomerBranchId()));
    }

    public ResultList<Company> getCustomerForAgent(CompanyFilter filter) {
        return planRouteRepository.getCustomers(filter);
    }

    public List<PlanRouteCustomerBranchAttachmentDTO> getAttachments(Long planRouteCustomerBranchId) {
        return planRouteCustomerBranchRepository.findById(planRouteCustomerBranchId).map(planRouteCustomerBranch -> planRouteCustomerBranch
                .getAttachments()
                .stream()
                .filter(planRouteCustomerBranchAttachment -> planRouteCustomerBranchAttachment.getAttachmentType() != null)
                .map(planRouteCustomerBranchAttachment -> {
                    PlanRouteCustomerBranchAttachmentDTO attachmentDTO = new PlanRouteCustomerBranchAttachmentDTO();
                    attachmentDTO.setId(planRouteCustomerBranchAttachment.getAttachmentId());
                    attachmentDTO.setUrl(planRouteCustomerBranchAttachment.getAttachment().getUrl());
                    attachmentDTO.setAttachmentType(planRouteCustomerBranchAttachment.getAttachmentType());
                    return attachmentDTO;
                }).toList()).orElseThrow(validationService.entityNotFoundExceptionThrow(PlanRouteCustomerBranch.class.getSimpleName(), "id", planRouteCustomerBranchId));
    }

    @Transactional
    public void deleteAttachments(Long planRouteCustomerBranchId, List<Long> attachmentIds) {
        planRouteCustomerBranchRepository.findById(planRouteCustomerBranchId).map(planRouteCustomerBranch -> {
                    List<Long> planRouteCustomerBranchAttachmentIds = new ArrayList<>();
                    planRouteCustomerBranch.getAttachments().forEach(planRouteCustomerBranchAttachment -> {
                        if (attachmentIds.contains(planRouteCustomerBranchAttachment.getAttachmentId())) {
                            planRouteCustomerBranchAttachmentIds.add(planRouteCustomerBranchAttachment.getId());
                        }
                    });
                    planRouteCustomerBranchAttachmentRepository.deleteAllByIdInBatch(planRouteCustomerBranchAttachmentIds);
                    return planRouteCustomerBranch;
                })
                .orElseThrow(validationService.entityNotFoundExceptionThrow(PlanRouteCustomerBranch.class.getSimpleName(), "id", planRouteCustomerBranchId));
    }

    public Optional<PlanRoute> getLastPlanRouteByCustomerBranch(Long customerBranchId, Long companyId) {
        List<PlanRoute> result = planRouteRepository.findLastPlanRouteByCustomerBranch(customerBranchId, companyId);
        if (!CollectionUtils.isEmpty(result)) {
            return Optional.of(result.getFirst());
        } else {
            return Optional.empty();
        }
    }

    @Transactional
    public void transferCustomerBranch(Long planRouteCustomerBranchId, LocalDate visitPlanDate) {
        PlanRouteCustomerBranch planRouteCustomerBranch = planRouteCustomerBranchRepository.findById(planRouteCustomerBranchId).orElseThrow(validationService.entityNotFoundExceptionThrow(PlanRouteCustomerBranch.class.getSimpleName(), "id", planRouteCustomerBranchId));
        if (planRouteCustomerBranchRepository.existsByPlanRouteItemIdAndCustomerBranchIdAndPlanRouteItem_VisitPlanDateAndDeletedIsFalse(planRouteCustomerBranch.getPlanRouteItemId(), planRouteCustomerBranch.getCustomerBranchId(), visitPlanDate)) {
            throw validationService.badRequestExceptionThrow("Customer branch already exists").get();
        }
        PlanRouteItem planRouteItem = planRouteItemRepository.findFirstByPlanRouteIdAndVisitPlanDateAndDeletedIsFalse(planRouteCustomerBranch.getPlanRouteItem().getPlanRouteId(), visitPlanDate).orElseGet(PlanRouteItem::new);
        if (planRouteItem.getId() == null) {
            planRouteItem.setPlanRouteId(planRouteCustomerBranch.getPlanRouteItem().getPlanRouteId());
            planRouteItem.setVisitPlanDate(visitPlanDate);
            planRouteItemRepository.save(planRouteItem);
        }
        PlanRouteCustomerBranch planRouteCustomerBranchNext = new PlanRouteCustomerBranch();
        planRouteCustomerBranchNext.setCustomerBranchId(planRouteCustomerBranch.getCustomerBranchId());
        planRouteCustomerBranchNext.setPlanRouteItemId(planRouteCustomerBranch.getPlanRouteItemId());
        planRouteCustomerBranchRepository.save(planRouteCustomerBranchNext);
        planRouteItem.getPlanRouteCustomerBranches().add(planRouteCustomerBranchNext);
    }

    public List<CommonDTO> lookUpForExpeditor(BaseFilter filter) {
        return planRouteRepository.getResultListForExpeditor(filter).getList().stream().map(PlanRoute::toCommonDTO).toList();
    }
}
