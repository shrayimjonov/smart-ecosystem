package uz.uzkassa.cts.service.impl;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import uz.uzkassa.cts.domain.*;
import uz.uzkassa.cts.domain.base.SimpleEntity;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.dto.repairticket.*;
import uz.uzkassa.cts.enums.Prefix;
import uz.uzkassa.cts.enums.RepairTicketStatus;
import uz.uzkassa.cts.enums.RepairTicketVisitType;
import uz.uzkassa.cts.enums.RepairType;
import uz.uzkassa.cts.filter.RepairTicketFilter;
import uz.uzkassa.cts.numbering.NumberingContext;
import uz.uzkassa.cts.repository.*;
import uz.uzkassa.cts.security.SecurityUtils;
import uz.uzkassa.cts.service.RepairTicketService;
import uz.uzkassa.cts.service.ThymeleafService;
import uz.uzkassa.cts.utils.CalcUtils;
import uz.uzkassa.cts.validator.ValidationContext;

import java.math.BigDecimal;
import java.math.MathContext;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static uz.uzkassa.cts.constants.Constants.INCREMENT_NUMBER;
import static uz.uzkassa.cts.constants.Constants.NUMBER;

@Service
@Transactional(readOnly = true)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class RepairTicketServiceImpl extends BaseServiceImpl<RepairTicket, Long> implements RepairTicketService {

    RepairTicketRepository repairTicketRepository;
    DeviceRepository deviceRepository;
    BranchRepository branchRepository;
    ReferenceRepository referenceRepository;
    RepairTicketItemRepository repairTicketItemRepository;
    ProductRepository productRepository;
    ThymeleafService thymeleafService;
    ValidationContext validationContext;
    NumberingContext numberingContext;

    public RepairTicketServiceImpl(RepairTicketRepository repairTicketRepository,
                                   DeviceRepository deviceRepository,
                                   BranchRepository branchRepository,
                                   ReferenceRepository referenceRepository,
                                   RepairTicketItemRepository repairTicketItemRepository, ProductRepository productRepository,
                                   ThymeleafService thymeleafService, ValidationContext validationContext, NumberingContext numberingContext) {
        super(repairTicketRepository);
        this.repairTicketRepository = repairTicketRepository;
        this.deviceRepository = deviceRepository;
        this.branchRepository = branchRepository;
        this.referenceRepository = referenceRepository;
        this.repairTicketItemRepository = repairTicketItemRepository;
        this.productRepository = productRepository;
        this.thymeleafService = thymeleafService;
        this.validationContext = validationContext;
        this.numberingContext = numberingContext;
    }

    @Override
    @Transactional
    public Long create(RepairTicketDTO repairTicketDTO) {
        validationContext.validate(repairTicketDTO);
        Device device = deviceRepository.findByIdOrSerialNumber(repairTicketDTO.getDeviceId(), repairTicketDTO.getSerialNumber()).orElseThrow(() -> validationService.entityNotFoundExceptionThrow(DEVICE).get());
        RepairTicket repairTicket = new RepairTicket();
        Map<String, String> numberingMap = numberingContext.generateNumber(Prefix.REPAIR_TICKET, getCompanyId());
        repairTicket.setIncrementNumber(Long.valueOf(numberingMap.get(INCREMENT_NUMBER)));
        repairTicket.setRepairTicketNumber(numberingMap.get(NUMBER));
        repairTicket.setCompanyId(SecurityUtils.getCompanyId());
        repairTicket.setDeviceId(device.getId());
        repairTicket.setCustomerId(repairTicketDTO.getCustomerId());
        repairTicket.setPhone(repairTicketDTO.getPhone());
        repairTicket.setTypeId(repairTicketDTO.getTypeId());
        repairTicket.setDescription(repairTicketDTO.getDescription());
        Long branchId;
        if (SecurityUtils.getBranchId() == null || SecurityUtils.getBranchId() == 0) {
            User user = userRepository.findById(SecurityUtils.getCurrentUserId()).orElseThrow(validationService.entityNotFoundExceptionThrow(USER));
            branchId = user.getBranchId();
        } else {
            branchId = SecurityUtils.getBranchId();
        }
        repairTicket.setFromBranchId(branchId);
        repairTicket.setCurrentBranchId(branchId);
        if (repairTicketRepository.existsByDeviceId(device.getId())) {
            repairTicket.setVisitType(RepairTicketVisitType.REPEATED);
        } else {
            repairTicket.setVisitType(RepairTicketVisitType.UNIQUE);
        }
        if (!CollectionUtils.isEmpty(repairTicketDTO.getEquipments())) {
            repairTicket.setEquipments(new HashSet<>(referenceRepository.findAllById(repairTicketDTO.getEquipments())));
        }
        if (!CollectionUtils.isEmpty(repairTicketDTO.getItems())) {
            for (RepairTicketItemDTO repairTicketItemDTO : repairTicketDTO.getItems()) {
                validationContext.validate(repairTicketItemDTO);
                productRepository.findById(repairTicketItemDTO.getProductId()).ifPresentOrElse(product -> {
                    RepairTicketItem repairTicketItem = new RepairTicketItem();
                    repairTicketItem.setProductId(repairTicketItemDTO.getProductId());
                    repairTicketItem.setUnitId(product.getUnitId());
                    repairTicketItem.setQty(repairTicketItemDTO.getQty());
                    repairTicketItem.setPrice(product.getPrice());
                    BigDecimal totalAmount = product.getPrice().multiply(repairTicketItemDTO.getQty(), MathContext.DECIMAL32);
                    repairTicketItem.setTotalAmount(totalAmount);

                    if (product.getVat() != null) {
                        repairTicketItem.setVatRate(product.getVat().getPercent());
                        BigDecimal totalVatAmount = CalcUtils.calcVat(product.getVat().getPercent(), totalAmount);
                        repairTicketItem.setVatPrice(totalVatAmount.divide(repairTicketItemDTO.getQty(), MathContext.DECIMAL32));
                        repairTicketItem.setTotalVatAmount(totalVatAmount);
                    }
                    repairTicket.addItem(repairTicketItem);
                    repairTicketItemRepository.save(repairTicketItem);
                }, () -> validationService.entityNotFoundExceptionThrow(PRODUCT).get());
            }
        }
        return repairTicketRepository.save(repairTicket).getId();
    }

    @Override
    public RepairTicketDetailDTO get(Long id) {
        RepairTicket repairTicket = repairTicketRepository.findById(id).orElseThrow(() -> validationService.entityNotFoundExceptionThrow(REPAIR_TICKET).get());
        RepairTicketDetailDTO repairTicketDetailDTO = new RepairTicketDetailDTO();
        repairTicketDetailDTO.setId(repairTicket.getId());
        repairTicketDetailDTO.setRepairTicketNumber(repairTicket.getRepairTicketNumber());
        repairTicketDetailDTO.setCustomer(repairTicket.getCustomer().toCustomerShortDTO());
        repairTicketDetailDTO.setMaster(repairTicket.getMaster() != null ? repairTicket.getMaster().toCommonDTO() : null);
        repairTicketDetailDTO.setFromBranch(repairTicket.getFromBranch().toCommonDTO());
        repairTicketDetailDTO.setToBranch(repairTicket.getToBranch() != null ? repairTicket.getToBranch().toCommonDTO() : null);
        repairTicketDetailDTO.setDevice(repairTicket.getDevice().toSerialNumberDTO());
        repairTicketDetailDTO.setPhone(repairTicket.getPhone());
        repairTicketDetailDTO.setCreatedDate(repairTicket.getCreatedDate());
        repairTicketDetailDTO.setType(repairTicket.getType().toCommonDTO());
        repairTicketDetailDTO.setCreatedBy(repairTicket.getCreator().toCommonDTO());
        repairTicketDetailDTO.setStatus(repairTicket.getStatus());
        repairTicketDetailDTO.setRepairStatus(repairTicket.getRepairStatus() != null ? repairTicket.getRepairStatus().toCommonDTO() : null);
        repairTicketDetailDTO.setVisitType(repairTicket.getVisitType());
        repairTicketDetailDTO.setAcceptedDate(repairTicket.getAcceptedDate());
        repairTicketDetailDTO.setRepairType(repairTicket.getRepairType());
        repairTicketDetailDTO.setDescription(repairTicket.getDescription());
        repairTicketDetailDTO.setEquipments(repairTicket.getEquipments()
                .stream()
                .map(SimpleEntity::toCommonDTO)
                .toList());
        repairTicketDetailDTO.setItems(repairTicket.getItems()
                .stream()
                .map(item -> {
                    RepairTicketItemDetailDTO repairTicketItemDetailDTO = new RepairTicketItemDetailDTO();
                    repairTicketItemDetailDTO.setId(item.getId());
                    repairTicketItemDetailDTO.setProduct(item.getProduct().toCommonDTO());
                    repairTicketItemDetailDTO.setUnit(item.getUnit().toCommonDTO());
                    repairTicketItemDetailDTO.setQty(item.getQty());
                    repairTicketItemDetailDTO.setTotalAmount(item.getTotalAmount());
                    repairTicketItemDetailDTO.setVatRate(item.getVatRate());
                    repairTicketItemDetailDTO.setTotalVatAmount(item.getTotalVatAmount());
                    return repairTicketItemDetailDTO;
                })
                .toList());
        return repairTicketDetailDTO;
    }

    @Override
    public Page<RepairTicketListDTO> getList(RepairTicketFilter filter) {
        ResultList<RepairTicket> resultList = repairTicketRepository.getResultList(filter);
        List<RepairTicketListDTO> result = resultList
                .getList()
                .stream()
                .map(repairTicket -> {
                    RepairTicketListDTO repairTicketListDTO = new RepairTicketListDTO();
                    repairTicketListDTO.setId(repairTicket.getId());
                    repairTicketListDTO.setRepairTicketNumber(repairTicket.getRepairTicketNumber());
                    repairTicketListDTO.setCustomer(repairTicket.getCustomer().toCustomerShortDTO());
                    repairTicketListDTO.setFromBranch(repairTicket.getFromBranch().toCommonDTO());
                    repairTicketListDTO.setToBranch(repairTicket.getToBranch() != null ? repairTicket.getToBranch().toCommonDTO() : null);
                    repairTicketListDTO.setDevice(repairTicket.getDevice().toSerialNumberDTO());
                    repairTicketListDTO.setPhone(repairTicket.getPhone());
                    repairTicketListDTO.setCreatedDate(repairTicket.getCreatedDate());
                    repairTicketListDTO.setType(repairTicket.getType().toCommonDTO());
                    repairTicketListDTO.setStatus(repairTicket.getStatus());
                    repairTicketListDTO.setUpdatedDate(repairTicket.getLastModifiedDate());
                    repairTicketListDTO.setRepairType(repairTicket.getRepairType());
                    repairTicketListDTO.setAcceptedDate(repairTicket.getAcceptedDate());
                    repairTicketListDTO.setVisitType(repairTicket.getVisitType());
                    repairTicketListDTO.setCreator(repairTicket.getCreator().toCommonDTO());
                    repairTicketListDTO.setCurrentBranch(repairTicket.getCurrentBranch() != null ? repairTicket.getCurrentBranch().toCommonDTO() : null);
                    repairTicketListDTO.setRepairStatus(repairTicket.getRepairStatus() != null ? repairTicket.getRepairStatus().toCommonDTO() : null);
                    repairTicketListDTO.setDescription(repairTicket.getDescription());
                    return repairTicketListDTO;
                }).toList();
        return new PageImpl<>(result, filter.getOrderedPageable(), resultList.getCount());
    }

    @Override
    @Transactional
    public void updateStatus(Long id, RepairTicketStatus status) {
        repairTicketRepository.findById(id)
                .ifPresentOrElse(repairTicket -> {
                    repairTicket.setStatus(status);
                    repairTicketRepository.save(repairTicket);
                }, () -> validationService.entityNotFoundExceptionThrow(REPAIR_TICKET).get());
    }

    @Override
    @Transactional
    public void assignMaster(Long id, Long masterId) {
        repairTicketRepository.findById(id)
                .ifPresentOrElse(repairTicket -> {
                    User master = userRepository.findById(masterId).orElseThrow(validationService.entityNotFoundExceptionThrow(USER));
                    repairTicket.setMasterId(masterId);
                    repairTicket.setMaster(master);
                    repairTicketRepository.save(repairTicket);
                }, () -> validationService.entityNotFoundExceptionThrow(REPAIR_TICKET).get());
    }

    @Override
    @Transactional
    public void transfer(Long id, Long toBranchId) {
        repairTicketRepository.findById(id)
                .ifPresentOrElse(repairTicket -> {
                    Branch toBranch = branchRepository.findById(toBranchId).orElseThrow(validationService.entityNotFoundExceptionThrow(BRANCH));
                    repairTicket.setToBranchId(toBranchId);
                    repairTicket.setToBranch(toBranch);
                    repairTicket.setStatus(RepairTicketStatus.TRANSFERRED);
                    repairTicketRepository.save(repairTicket);
                }, () -> validationService.entityNotFoundExceptionThrow(REPAIR_TICKET).get());
    }

    @Override
    @Transactional
    public void updateType(Long id, Long typeId) {
        repairTicketRepository.findById(id)
                .ifPresentOrElse(repairTicket -> {
                    Reference reference = referenceRepository.findById(typeId).orElseThrow(validationService.entityNotFoundExceptionThrow(REFERENCE));
                    repairTicket.setTypeId(typeId);
                    repairTicket.setType(reference);
                    repairTicketRepository.save(repairTicket);
                }, () -> validationService.entityNotFoundExceptionThrow(REPAIR_TICKET).get());
    }

    @Override
    @Transactional
    public void updateRepairType(Long id, RepairType repairType) {
        repairTicketRepository.findById(id)
                .ifPresentOrElse(repairTicket -> {
                    repairTicket.setRepairType(repairType);
                    repairTicketRepository.save(repairTicket);
                }, () -> validationService.entityNotFoundExceptionThrow(REPAIR_TICKET).get());
    }

    @Override
    @Transactional
    public void updateEquipments(Long id, List<Long> equipmentIds) {
        repairTicketRepository.findById(id)
                .ifPresentOrElse(repairTicket -> {
                    repairTicket.setEquipments(new HashSet<>(referenceRepository.findAllById(equipmentIds)));
                    repairTicketRepository.save(repairTicket);
                }, () -> validationService.entityNotFoundExceptionThrow(REPAIR_TICKET).get());
    }

    @Override
    @Transactional
    public void updateItems(Long id, List<RepairTicketItemDTO> items) {
        repairTicketRepository.findById(id)
                .ifPresentOrElse(repairTicket -> {
                    Map<Long, RepairTicketItem> repairTicketItemMap = repairTicket
                            .getItems()
                            .stream()
                            .collect(Collectors.toMap(RepairTicketItem::getId, Function.identity()));
                    for (RepairTicketItemDTO repairTicketItemDTO : items) {
                        validationContext.validate(repairTicketItemDTO);
                        productRepository.findById(repairTicketItemDTO.getProductId()).ifPresentOrElse(product -> {
                            RepairTicketItem repairTicketItem;
                            if (repairTicketItemDTO.getId() != null && repairTicketItemMap.get(repairTicketItemDTO.getId()) != null) {
                                repairTicketItem = repairTicketItemMap.get(repairTicketItemDTO.getId());
                                repairTicketItemMap.remove(repairTicketItem.getId());
                            } else {
                                repairTicketItem = new RepairTicketItem();
                            }
                            BigDecimal totalAmount = product.getPrice().multiply(repairTicketItemDTO.getQty(), MathContext.DECIMAL32);
                            repairTicketItem.setProductId(repairTicketItemDTO.getProductId());
                            repairTicketItem.setUnitId(product.getUnitId());
                            repairTicketItem.setQty(repairTicketItemDTO.getQty());
                            repairTicketItem.setPrice(product.getPrice());
                            repairTicketItem.setTotalAmount(totalAmount);

                            if (product.getVat() != null) {
                                repairTicketItem.setVatRate(product.getVat().getPercent());
                                BigDecimal totalVatAmount = CalcUtils.calcVat(product.getVat().getPercent(), totalAmount);
                                repairTicketItem.setVatPrice(totalVatAmount.divide(repairTicketItemDTO.getQty(), MathContext.DECIMAL32));
                                repairTicketItem.setTotalVatAmount(totalVatAmount);
                            }
                            repairTicket.addItem(repairTicketItem);

                            if (repairTicketItem.getId() == null) {
                                repairTicketItemRepository.save(repairTicketItem);
                            }
                        }, () -> validationService.entityNotFoundExceptionThrow(PRODUCT).get());
                    }

                    if (!CollectionUtils.isEmpty(repairTicketItemMap.values())) {
                        repairTicketItemRepository.deleteAllInBatch(repairTicketItemMap.values());
                    }
                    repairTicketRepository.save(repairTicket);
                }, () -> validationService.entityNotFoundExceptionThrow(REPAIR_TICKET).get());
    }

    @Override
    @Transactional
    public void accept(Long id) {
        repairTicketRepository.findById(id)
                .ifPresentOrElse(repairTicket -> {
                    repairTicket.setAcceptDate(LocalDateTime.now());
                    Long branchId;
                    if (SecurityUtils.getBranchId() == null || SecurityUtils.getBranchId() == 0) {
                        User user = userRepository.findById(SecurityUtils.getCurrentUserId()).orElseThrow(validationService.entityNotFoundExceptionThrow(USER));
                        branchId = user.getBranchId();
                    } else {
                        branchId = SecurityUtils.getBranchId();
                    }
                    repairTicket.setCurrentBranchId(branchId);
                    repairTicketRepository.save(repairTicket);
                }, () -> validationService.entityNotFoundExceptionThrow(REPAIR_TICKET).get());
    }

    @Override
    @Transactional
    public void updateRepairStatus(Long id, Long repairStatusId) {
        repairTicketRepository.findById(id).ifPresentOrElse(repairTicket -> {
            repairTicket.setRepairStatusId(repairStatusId);
            repairTicketRepository.save(repairTicket);
        }, () -> validationService.entityNotFoundExceptionThrow(REPAIR_TICKET).get());
    }

    @Override
    public String getRepairTicketAcceptActHtmlTemplate(Long id) {
        return repairTicketRepository.findById(id).map(thymeleafService::generateRepairTicketAcceptAct)
                .orElseThrow(() -> validationService.entityNotFoundExceptionThrow(REPAIR_TICKET).get());
    }

    @Override
    public String getRepairTicketDefectActHtmlTemplate(Long id) {
        return repairTicketRepository.findById(id).map(thymeleafService::generateRepairTicketDefectAct)
                .orElseThrow(() -> validationService.entityNotFoundExceptionThrow(REPAIR_TICKET).get());
    }
}
