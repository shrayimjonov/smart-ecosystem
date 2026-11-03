package uz.uzkassa.smartpos.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.smartpos.constants.TokenType;
import uz.uzkassa.smartpos.domain.postgres.company.Company;
import uz.uzkassa.smartpos.domain.postgres.warehouse.Order;
import uz.uzkassa.smartpos.domain.postgres.warehouse.OrderProduct;
import uz.uzkassa.smartpos.domain.postgres.warehouse.Order_;
import uz.uzkassa.smartpos.dto.base.CommonDTO;
import uz.uzkassa.smartpos.enums.SortTypeEnum;
import uz.uzkassa.smartpos.integration.enums.OrderStatus;
import uz.uzkassa.smartpos.integration.supply.SupplyClient;
import uz.uzkassa.smartpos.integration.supply.dto.*;
import uz.uzkassa.smartpos.integration.supply.filter.CommentFilter;
import uz.uzkassa.smartpos.integration.supply.filter.OrderFilter;
import uz.uzkassa.smartpos.mappers.OrderMapper;
import uz.uzkassa.smartpos.repository.postgres.warehouse.BasketItemsRepository;
import uz.uzkassa.smartpos.repository.postgres.warehouse.OrderRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService extends SmartposService {
    private final OrderRepository orderRepository;
    private final SupplyClient supplyClient;
    private final OrderMapper orderMapper;
    private final BasketItemsRepository basketItemRepository;
    private final ObjectMapper objectMapper;
    private final TokenStoreService tokenStoreService;

    @Transactional
    public Long create(OrderCreateDTO orderCreateDTO) {
        Company customer = companyRepository.findById(orderCreateDTO.getCustomerId())
                .orElseThrow(notFound(localizationService.getMessage("company")));
        SupplierOrderDTO supplierOrderDTO = new SupplierOrderDTO(orderCreateDTO, customer);
        log.info("================================================================");
        try {
            log.info("REQUEST: {}", objectMapper.writeValueAsString(supplierOrderDTO));
        } catch (JsonProcessingException ignored) {
        }
        log.info("================================================================");
        ResponseEntity<Long> idResponse = supplyClient.createOrder(tokenStoreService.getToken(TokenType.SUPPLY), supplierOrderDTO);
        return save(orderCreateDTO, customer, idResponse);
    }

    @Transactional
    public Long update(Long id, OrderCreateDTO orderCreateDTO) {
        Order order = orderRepository.findById(id).orElseThrow(notFound(localizationService.getMessage("order")));
        if (!(OrderStatus.NEW.equals(order.getStatus()) || OrderStatus.DRAFT.equals(order.getStatus()))) {
            throw badRequest("Вы можете обновлять новые или черновые заказы").get();
        }
        Company customer = companyRepository.findById(orderCreateDTO.getCustomerId()).orElseThrow(notFound(localizationService.getMessage("company")));
        SupplierOrderDTO supplierOrderDTO = new SupplierOrderDTO(orderCreateDTO, customer);
        log.info("================================================================");
        try {
            log.info("REQUEST: {}", objectMapper.writeValueAsString(supplierOrderDTO));
        } catch (JsonProcessingException ignored) {
        }
        log.info("================================================================");
        ResponseEntity<Long> idResponse = supplyClient.updateOrder(tokenStoreService.getToken(TokenType.SUPPLY), order.getId(), supplierOrderDTO);
        return save(orderCreateDTO, customer, idResponse);
    }

    private Long save(OrderCreateDTO orderCreateDTO, Company customer, ResponseEntity<Long> idResponse) {
        ResponseEntity<SupplierOrderDetailDTO> createdOrderResponse = supplyClient.getOrderById(tokenStoreService.getToken(TokenType.SUPPLY), idResponse.getBody());
        SupplierOrderDetailDTO supplierOrder = createdOrderResponse.getBody();
        log.info("================================================================");
        try {
            log.info("RESPONSE: {}", objectMapper.writeValueAsString(supplierOrder));
        } catch (JsonProcessingException ignored) {
        }
        log.info("================================================================");
        Order entity = orderMapper.toEntity(supplierOrder);
        entity.setCustomerId(customer.getId());
        entity.setCustomer(customer);
        Map<Long, OrderProduct> orderProductMap = entity.getProducts().stream().collect(Collectors.toMap(OrderProduct::getId, Function.identity()));
        orderCreateDTO.getProducts().forEach(productDto -> {
            OrderProduct orderProduct = orderProductMap.get(productDto.getId());
            orderProduct.setPhotos(productDto.getPhotos());
            entity.setTotalPrice(entity.getTotalPrice().add(orderProduct.getTotalPrice()));
        });
        orderRepository.save(entity);
        basketItemRepository.deleteAllByCompanyIdAndSupplierProductIdIn(entity.getCustomerId(), entity.getProducts()
                .stream().map(OrderProduct::getId)
                .collect(Collectors.toList()));
        return entity.getId();
    }

    public OrderDTO get(Long id) {
        Order order = orderRepository.findById(id).orElseThrow(notFound(localizationService.getMessage("order")));
        return orderMapper.toDTO(order);
    }

    public void updateStatus(Long id, OrderStatus status) {
        Optional<Order> orderOptional = orderRepository
                .findById(id);
        if (orderOptional.isPresent()) {
            Order order = orderOptional.get();
            order.setStatus(status);
            supplyClient.updateOrderStatus(tokenStoreService.getToken(TokenType.SUPPLY), order.getId(), status.name());
            orderRepository.save(order);
        } else {
            throw notFound(localizationService.getMessage("order")).get();
        }
    }

    public void statusWebhook(Long id, OrderWebhookDto dto) {
        Order order = orderRepository.findById(id).orElseThrow(notFound(localizationService.getMessage("order")));
        if (OrderStatus.DELETED.equals(dto.getStatus())) {
            orderRepository.delete(order);
        } else {
            order.setStatus(dto.getStatus());
            order.setDeliveryDate(dto.getDeliveryDate());
            orderRepository.save(order);
        }
    }

    public Page<OrderListDTO> getList(OrderFilter filter) {
        return orderRepository.findAll(getOrderSpecification(filter), filter.getPageable())
                .map(OrderListDTO::new);
    }

    public void deleteById(Long id) {
        Optional<Order> orderOptional = orderRepository.findById(id);
        if (orderOptional.isPresent()) {
            supplyClient.deleteOrder(tokenStoreService.getToken(TokenType.SUPPLY), id);
            orderRepository.delete(orderOptional.get());
        } else {
            throw notFound(localizationService.getMessage("order")).get();
        }
    }

    public List<CommonDTO> lookUp(OrderFilter filter) {
        return orderRepository.findAll(getOrderSpecification(filter)).stream().map(Order::toCommonDTO).collect(Collectors.toList());
    }

    @NotNull
    private Specification<Order> getOrderSpecification(OrderFilter filter) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (StringUtils.isNotEmpty(filter.getSearch())) {
                predicates.add(criteriaBuilder.or(
                        criteriaBuilder.like(criteriaBuilder.lower(root.get(Order_.orderNumber)), filter.getSearchForQuery()),
                        criteriaBuilder.like(root.get(Order_.supplierTin), filter.getSearchForQuery()),
                        criteriaBuilder.like(root.get(Order_.supplierName), filter.getSearchForQuery())
                ));
            }
            if (filter.getCustomerId() != null) {
                predicates.add(criteriaBuilder.equal(root.get(Order_.customerId), filter.getCustomerId()));
            } else {
                predicates.add(criteriaBuilder.equal(root.get(Order_.customerId), getCurrentCompanyId()));
            }
            if (filter.getDeliveryBranchId() != null) {
                predicates.add(criteriaBuilder.equal(root.get(Order_.deliveryBranchId), filter.getDeliveryBranchId()));
            }
            if (filter.getFrom() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get(Order_.orderDate), filter.getFrom()));
            }
            if (filter.getTo() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get(Order_.orderDate), filter.getTo()));
            }
            if (StringUtils.isNotBlank(filter.getOrderBy())) {
                if (SortTypeEnum.desc.equals(filter.getSortType())) {
                    criteriaQuery.orderBy(criteriaBuilder.desc(root.get(filter.getOrderBy())));
                } else {
                    criteriaQuery.orderBy(criteriaBuilder.asc(root.get(filter.getOrderBy())));
                }
            } else {
                criteriaQuery.orderBy(criteriaBuilder.desc(root.get(Order_.ID)));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    public ResponseEntity<Long> createComment(Long id, CreateCommentDTO dto) {
        orderRepository.findById(id).orElseThrow(notFound(localizationService.getMessage("order")));
        SupplierCommentDTO commentDTO = new SupplierCommentDTO();
        commentDTO.setOrderId(id);
        commentDTO.setMessage(dto.getMessage());
        return supplyClient.createComment(tokenStoreService.getToken(TokenType.SUPPLY), commentDTO);
    }

    public ResponseEntity<Object> getComments(Long id, CommentFilter filter) {
        filter.setId(id);
        return supplyClient.getComments(tokenStoreService.getToken(TokenType.SUPPLY), filter);
    }
}
