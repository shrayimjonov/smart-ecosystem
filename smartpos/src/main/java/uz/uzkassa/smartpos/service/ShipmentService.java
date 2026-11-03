package uz.uzkassa.smartpos.service;

import jakarta.persistence.criteria.Predicate;
import lombok.AllArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.smartpos.domain.postgres.base.SimpleEntity;
import uz.uzkassa.smartpos.domain.postgres.company.Branch;
import uz.uzkassa.smartpos.domain.postgres.warehouse.Shipment;
import uz.uzkassa.smartpos.domain.postgres.warehouse.Shipment_;
import uz.uzkassa.smartpos.dto.base.CommonDTO;
import uz.uzkassa.smartpos.dto.mq.RegisterPayload;
import uz.uzkassa.smartpos.dto.warehouse.ShipmentCreateDto;
import uz.uzkassa.smartpos.dto.warehouse.ShipmentDto;
import uz.uzkassa.smartpos.dto.warehouse.ShipmentItemCreateDto;
import uz.uzkassa.smartpos.dto.warehouse.ShipmentListDto;
import uz.uzkassa.smartpos.enums.RegisterOperation;
import uz.uzkassa.smartpos.enums.ShipmentStatus;
import uz.uzkassa.smartpos.enums.SortTypeEnum;
import uz.uzkassa.smartpos.exceptions.BadRequestException;
import uz.uzkassa.smartpos.exceptions.EntityNotFound;
import uz.uzkassa.smartpos.filter.ShipmentFilter;
import uz.uzkassa.smartpos.mappers.ShipmentMapper;
import uz.uzkassa.smartpos.rabbitmq.MqConstants;
import uz.uzkassa.smartpos.rabbitmq.producer.RabbitMqProducer;
import uz.uzkassa.smartpos.repository.postgres.company.BranchRepository;
import uz.uzkassa.smartpos.repository.postgres.warehouse.ShipmentRepository;
import uz.uzkassa.smartpos.security.SecurityUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ShipmentService {
    private final ShipmentRepository shipmentRepository;
    private final ShipmentMapper shipmentMapper;
    private final InvoiceService invoiceService;
    private final BranchRepository branchRepository;
    private final RabbitMqProducer rabbitMqProducer;

    @Transactional
    public ShipmentDto create(ShipmentCreateDto dto) {
        Shipment shipment = shipmentMapper.toEntity(dto);
        Map<Long, Long> branchCityMap = branchRepository.findAllById(dto.getItems().stream()
                        .map(ShipmentItemCreateDto::getBranchId).collect(Collectors.toSet()))
                .stream().collect(Collectors.toMap(SimpleEntity::getId, Branch::getCityKeyId));
        Integer number = shipmentRepository.getMaxIntNumber(dto.getCompanyId());
        if (number == null) {
            number = 1;
        } else {
            number++;
        }
        shipment.setIntNumber(number);
        shipment.setNumber("№-" + number);
        shipment.getItems().forEach(shipmentItem -> shipmentItem.setCityId(branchCityMap.get(shipmentItem.getBranchId())));
        shipmentRepository.save(shipment);
        return shipmentMapper.toDto(shipment);
    }

    @Transactional
    public ShipmentDto update(Long id, ShipmentCreateDto dto) {
        Shipment shipment = shipmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFound("Отгрузка не найдена"));
        if (ShipmentStatus.CLOSED.equals(shipment.getStatus())) {
            throw new BadRequestException("Вы не можете изменить закрытую отгрузку");
        }
        Map<Long, Long> branchCityMap = branchRepository.findAllById(dto.getItems().stream()
                        .map(ShipmentItemCreateDto::getBranchId).collect(Collectors.toSet()))
                .stream().collect(Collectors.toMap(SimpleEntity::getId, Branch::getCityKeyId));
        shipmentMapper.partialUpdate(dto, shipment);
        shipment.getItems().forEach(shipmentItem -> shipmentItem.setCityId(branchCityMap.get(shipmentItem.getBranchId())));
        shipmentRepository.save(shipment);
        return shipmentMapper.toDto(shipment);
    }

    @Transactional
    public ShipmentDto updateStatus(Long id, ShipmentStatus status) {
        Shipment shipment = shipmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFound("Отгрузка не найдена"));
        shipment.setStatus(status);
        shipmentRepository.save(shipment);
        if (ShipmentStatus.CLOSED.equals(status)) {
            shipment.getItems().forEach(shipmentItem -> rabbitMqProducer.sendWithType(RegisterPayload.builder()
                            .amount(shipmentItem.getQty())
                            .branchId(shipmentItem.getBranchId())
                            .markingCount(CollectionUtils.size(shipmentItem.getMarkings()) +
                                    CollectionUtils.size(shipmentItem.getAggregations()))
                            .costPrice(shipmentItem.getPrice())
                            .productId(shipmentItem.getProductId())
                            .salesPrice(shipmentItem.getPrice())
                            .unitId(shipmentItem.getUnitId())
                            .operation(RegisterOperation.SALE)
                            .build(),
                    MqConstants.REGISTER_QUEUE,
                    "register_payload"));
        }
        return shipmentMapper.toDto(shipment);
    }

    public ShipmentDto findById(Long id) {
        Shipment shipment = shipmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFound("Отгрузка не найдена"));
        return shipmentMapper.toDto(shipment);
    }

    @Transactional
    public void delete(Long id) {
        Shipment shipment = shipmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFound("Отгрузка не найдена"));
        if (ShipmentStatus.CLOSED.equals(shipment.getStatus())) {
            throw new BadRequestException("Вы не можете удалить закрытую отгрузку");
        }
        shipmentRepository.delete(shipment);
    }

    public Page<ShipmentListDto> findAllByFilter(ShipmentFilter filter) {
        if (filter.getCompanyId() == null) {
            filter.setCompanyId(SecurityUtils.getCurrentCompanyId());
        }
        return shipmentRepository.findAll((Specification<Shipment>) (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicateList = new ArrayList<>();
            predicateList.add(criteriaBuilder.equal(root.get(Shipment_.companyId), filter.getCompanyId()));
            if (filter.getFrom() != null) {
                predicateList.add(criteriaBuilder.greaterThanOrEqualTo(root.get(Shipment_.createdDate), filter.getFrom()));
            }
            if (filter.getTo() != null) {
                predicateList.add(criteriaBuilder.lessThanOrEqualTo(root.get(Shipment_.createdDate), filter.getTo()));
            }
            if (filter.getStatus() != null) {
                predicateList.add(criteriaBuilder.equal(root.get(Shipment_.status), filter.getStatus()));
            }
            if (StringUtils.isNotBlank(filter.getSearch())) {
                predicateList.add(criteriaBuilder.or(
                        criteriaBuilder.like(criteriaBuilder.lower(root.get(Shipment_.number)), filter.getSearchForQuery()),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get(Shipment_.customerName)), filter.getSearchForQuery()),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get(Shipment_.customerTin)), filter.getSearchForQuery())
                ));
            }
            if (StringUtils.isNotEmpty(filter.getOrderBy())) {
                if (SortTypeEnum.asc.equals(filter.getSortOrder())) {
                    criteriaQuery.orderBy(criteriaBuilder.asc(root.get(filter.getOrderBy())));
                } else {
                    criteriaQuery.orderBy(criteriaBuilder.desc(root.get(filter.getOrderBy())));
                }
            } else {
                criteriaQuery.orderBy(criteriaBuilder.desc(root.get(Shipment_.id)));
            }
            return criteriaBuilder.and(predicateList.toArray(new Predicate[0]));
        }, filter.getPageable()).map(shipmentMapper::toListDto);
    }

    public Long convertToInvoice(List<Long> ids) {
        return invoiceService.createShipmentInvoice(ids);
    }

    public List<CommonDTO> lookup(ShipmentFilter filter) {
        if (filter.getCompanyId() == null) {
            filter.setCompanyId(SecurityUtils.getCurrentCompanyId());
        }
        return shipmentRepository.lookup(filter);
    }


}
