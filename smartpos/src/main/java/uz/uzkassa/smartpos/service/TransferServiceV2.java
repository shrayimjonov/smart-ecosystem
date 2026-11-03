package uz.uzkassa.smartpos.service;

import com.google.common.collect.Sets;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.smartpos.domain.postgres.catalog.ProductBranch;
import uz.uzkassa.smartpos.domain.postgres.catalog.ProductBranchId;
import uz.uzkassa.smartpos.domain.postgres.company.Company;
import uz.uzkassa.smartpos.domain.postgres.warehouse.Transfer;
import uz.uzkassa.smartpos.domain.postgres.warehouse.TransferDetail;
import uz.uzkassa.smartpos.domain.postgres.warehouse.Transfer_;
import uz.uzkassa.smartpos.dto.base.PageDto;
import uz.uzkassa.smartpos.dto.filter.TransferFilter;
import uz.uzkassa.smartpos.dto.integration.agreement.ServiceType;
import uz.uzkassa.smartpos.dto.mq.RegisterPayload;
import uz.uzkassa.smartpos.dto.warehouse.TransferDetailDtoV2;
import uz.uzkassa.smartpos.dto.warehouse.fashion.FashionTransferCreateDto;
import uz.uzkassa.smartpos.dto.warehouse.fashion.FashionTransferUpdateStatusDto;
import uz.uzkassa.smartpos.dto.warehouse.fashion.TransferFashionDto;
import uz.uzkassa.smartpos.dto.warehouse.fashion.TransferListDto;
import uz.uzkassa.smartpos.enums.RegisterOperation;
import uz.uzkassa.smartpos.enums.SortTypeEnum;
import uz.uzkassa.smartpos.enums.TransferStatus;
import uz.uzkassa.smartpos.exceptions.BadRequestException;
import uz.uzkassa.smartpos.mappers.TransferMapper;
import uz.uzkassa.smartpos.rabbitmq.MqConstants;
import uz.uzkassa.smartpos.rabbitmq.producer.RabbitMqProducer;
import uz.uzkassa.smartpos.repository.postgres.catalog.ProductBranchRepository;
import uz.uzkassa.smartpos.repository.postgres.warehouse.TransferRepository;
import uz.uzkassa.smartpos.security.SecurityUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransferServiceV2 extends SmartposService {
    private final TransferRepository transferRepository;
    private final RabbitMqProducer rabbitMqProducer;
    private final ProductBranchRepository productBranchRepository;
    private final TransferMapper transferMapper;

    @Transactional
    public Long create(FashionTransferCreateDto dto, boolean send) {
        Transfer transfer = new Transfer();
        transfer.setStatus(send ? TransferStatus.SENT : TransferStatus.REQUESTED);
        transfer.setCompanyId(Optional.ofNullable(dto.companyId()).orElse(getCurrentCompanyId()));
        transfer.setFromBranchId(dto.fromBranchId());
        transfer.setToBranchId(dto.toBranchId());
        transfer.setTransferDate(dto.transferDate());
        transferRepository.save(transfer);
        dto.transferDetails().forEach(detailDto -> {
            var detail = new TransferDetail();
            detail.setAskedQty(detailDto.askedQty());
            detail.setRealQty(detailDto.askedQty());
            detail.setTransferId(transfer.getId());
            detail.setProductId(detailDto.productId());
            detail.setBranchId(dto.fromBranchId());
            detail.setCityId(getBranchCityId(dto.fromBranchId()));
            detail.setUnitId(detailDto.unitId());
            detail.setProductItemId(detailDto.productItemId());
            detail.setSalesPrice(detailDto.salesPrice());
            transfer.getTransferDetails().add(detail);
        });
        transferRepository.save(transfer);
        if (send) {
            createProducts(transfer);
            sendToRegister(transfer);
        }
        return transfer.getId();
    }

    @Transactional
    public void updateStatus(Long id, FashionTransferUpdateStatusDto dto) {
        Transfer transfer = transferRepository.findById(id).orElseThrow();
        transfer.setStatus(dto.status());
        if (TransferStatus.APPROVED.equals(dto.status())) {
            transfer.setApprovedById(getCurrentUserId());
        }
        if (TransferStatus.CANCELLED.equals(dto.status())) {
            transfer.setCancelledById(getCurrentUserId());
        }
        transferRepository.save(transfer);
        if (List.of(TransferStatus.APPROVED, TransferStatus.SENT).contains(dto.status())) {
            createProducts(transfer);
            sendToRegister(transfer);
        }
    }

    @Transactional
    public void delete(Long id) {
        Transfer transfer = transferRepository.findById(id).orElseThrow();
        if (TransferStatus.REQUESTED.equals(transfer.getStatus())) {
            transferRepository.delete(transfer);
        } else {
            throw new BadRequestException("Вы не можете удалить отклоненные или одобренные переводы.");
        }
    }

    public TransferFashionDto<TransferDetailDtoV2> getById(Long id) {
        Transfer transfer = transferRepository.findById(id).orElseThrow();
        return transferMapper.toDto(transfer);
    }

    private void createProducts(Transfer transfer) {
        transfer.getTransferDetails().forEach(detail -> {
            Long toBranchId = transfer.getToBranchId();
            Long cityKeyId = getBranchCityId(toBranchId);
            ProductBranch productBranch = productBranchRepository.findById(new ProductBranchId(detail.getProductId(), toBranchId, cityKeyId))
                    .orElseGet(() -> new ProductBranch(detail.getProduct(), toBranchId, cityKeyId));
            productBranchRepository.saveAndFlush(productBranch);
        });
    }

    private void sendToRegister(Transfer transfer) {
        transfer.getTransferDetails().forEach(detail -> {
            rabbitMqProducer.sendWithType(new RegisterPayload(
                    transfer.getFromBranchId(),
                    null,
                    detail.getProductId(),
                    detail.getUnitId(),
                    detail.getQty(),
                    detail.getCostPrice(),
                    detail.getSalesPrice(),
                    0,
                    RegisterOperation.TRANSFER_OUT
            ), MqConstants.REGISTER_QUEUE, "register_payload");
            rabbitMqProducer.sendWithType(new RegisterPayload(
                    transfer.getToBranchId(),
                    transfer.getFromBranchId(),
                    detail.getProductId(),
                    detail.getUnitId(),
                    detail.getQty(),
                    detail.getCostPrice(),
                    detail.getSalesPrice(),
                    0,
                    RegisterOperation.TRANSFER_IN
            ), MqConstants.REGISTER_QUEUE, "register_payload");
        });
    }

    public PageDto<TransferListDto> findAllByFilter(TransferFilter filter) {
        final boolean hasPermissionToAdmin = hasPermissionToAdmin();
        if (!hasPermissionToAdmin || filter.getCompanyId() == null) {
            filter.setCompanyId(getCurrentCompanyId());
        }
        if (!hasPermissionToAdmin) {
            checkWarehousePermission(filter.getCompanyId());
            if (!isOwner() && !isBranchAdmin()) {
                filter.setBranchIds(Sets.newHashSet(getCurrentBranchId()));
            }
            if (isBranchAdmin() && CollectionUtils.isEmpty(filter.getBranchIds())) {
                SecurityUtils.getCurrentBranchGroupId()
                        .ifPresentOrElse(groupId -> filter.setBranchGroupIds(Set.of(groupId)),
                                () -> filter.setBranchIds(Set.of(SecurityUtils.getCurrentBranchId()))
                        );
            }
        }
        filter.setBranchIds(getBranchesByGroup(filter.getBranchGroupIds(), filter.getBranchIds()));
        Page<TransferListDto> page = transferRepository.findAll((Specification<Transfer>) (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(criteriaBuilder.equal(root.get(Transfer_.companyId), filter.getCompanyId()));
            if (CollectionUtils.isNotEmpty(filter.getBranchIds())) {
                predicates.add(criteriaBuilder.or(
                        root.get(Transfer_.fromBranchId).in(filter.getBranchIds()),
                        root.get(Transfer_.toBranchId).in(filter.getBranchIds()))
                );
            }
            if (filter.getStatus() != null) {
                predicates.add(criteriaBuilder.equal(root.get(Transfer_.status), filter.getStatus()));
            }
            if (filter.getFrom() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get(Transfer_.transferDate), filter.getFrom()));
            }
            if (filter.getTo() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get(Transfer_.transferDate), filter.getTo()));
            }
            if (StringUtils.isNotBlank(filter.getSearch())) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get(Transfer_.description)), filter.getSearchForQuery()));
            }
            if (StringUtils.isNotBlank(filter.getOrderBy())) {
                if (SortTypeEnum.asc.equals(filter.getSortOrder())) {
                    criteriaQuery.orderBy(criteriaBuilder.asc(root.get(filter.getOrderBy())));
                } else {
                    criteriaQuery.orderBy(criteriaBuilder.desc(root.get(filter.getOrderBy())));
                }
            } else {
                criteriaQuery.orderBy(criteriaBuilder.desc(root.get(Transfer_.id)));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        }, filter.getPageable()).map(transferMapper::toListDto);
        return new PageDto<>(page);
    }

    public void checkWarehousePermission(Long companyId) {
        Company company = companyRepository.findById(companyId).orElseThrow(notFound(localizationService.getMessage("company")));
        if ((!company.isFreeTrial() && !company.getPermissions().getOrDefault(ServiceType.WAREHOUSE, LocalDateTime.MIN).isAfter(LocalDateTime.now())) && !isAdmin() && !isOperator()) {
            throw new BadRequestException("Пожалуйста, купите и активируйте складскую услугу", 400);
        }
    }
}
