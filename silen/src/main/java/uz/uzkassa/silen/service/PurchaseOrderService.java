package uz.uzkassa.silen.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.silen.domain.PurchaseOrder;
import uz.uzkassa.silen.domain.PurchaseOrderProduct;
import uz.uzkassa.silen.domain.Shipment;
import uz.uzkassa.silen.domain.redis.RedisShipmentItem;
import uz.uzkassa.silen.dto.SelectItem;
import uz.uzkassa.silen.dto.filter.PurchaseOrderFilter;
import uz.uzkassa.silen.dto.invoice.InvoiceBaseDTO;
import uz.uzkassa.silen.dto.warehouse.PurchaseOrderDTO;
import uz.uzkassa.silen.dto.warehouse.PurchaseOrderDetailDTO;
import uz.uzkassa.silen.dto.warehouse.PurchaseOrderListDTO;
import uz.uzkassa.silen.dto.warehouse.ShipmentCode;
import uz.uzkassa.silen.enumeration.*;
import uz.uzkassa.silen.exceptions.BadRequestException;
import uz.uzkassa.silen.repository.pg.PurchaseOrderRepository;
import uz.uzkassa.silen.repository.pg.SerialNumberRepository;
import uz.uzkassa.silen.repository.pg.ShipmentItemRepository;
import uz.uzkassa.silen.repository.pg.ShipmentRepository;
import uz.uzkassa.silen.repository.redis.RedisShipmentCodeRepository;

import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service Implementation for managing {@link PurchaseOrder}.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PurchaseOrderService extends BaseService {
    PurchaseOrderRepository purchaseOrderRepository;
    ShipmentRepository shipmentRepository;
    RedisShipmentCodeRepository redisShipmentCodeRepository;
    ShipmentItemRepository shipmentItemRepository;
    SerialNumberRepository serialNumberRepository;
    ShipmentService shipmentService;

    @Transactional
    public Long create(PurchaseOrderDTO orderDTO) {
        final String organizationId = Optional.ofNullable(orderDTO.getFromOrganizationId()).orElse(getCurrentOrganizationId());
        PurchaseOrder order = new PurchaseOrder();
        order.setFromOrganizationId(organizationId);
        order.setCustomerId(orderDTO.getCustomerId());
        order.setStoreId(orderDTO.getStoreId());
        order.setContractId(orderDTO.getContractId());
        order.setStatus(PurchaseOrderStatus.NEW);
        order.setType(PurchaseOrderType.SELF);
        order.setOrderDate(orderDTO.getOrderDate());

        if (StringUtils.isNotEmpty(orderDTO.getNumber())) {
            if (purchaseOrderRepository.existsPurchaseOrderByFromOrganizationIdAndNumberIgnoreCase(organizationId, orderDTO.getNumber())) {
                throw new BadRequestException("Заказ с таким номером уже существует");
            } else {
                order.setNumber(orderDTO.getNumber());
            }
        } else {
            generatePurchaseOrderNumber(organizationId, order, "PO-");
        }
        purchaseOrderRepository.save(order);
        return order.getId();
    }

    public void generatePurchaseOrderNumber(String organizationId, PurchaseOrder order, String prefix) {
        Integer intNumber = purchaseOrderRepository.getMaxIntNumber(organizationId);
        String number = null;
        if (intNumber == null) {
            intNumber = 1;
        }
        boolean exists = true;
        while (exists) {
            number = prefix + intNumber;
            if (purchaseOrderRepository.existsPurchaseOrderByFromOrganizationIdAndNumberIgnoreCase(organizationId, number)) {
                intNumber++;
            } else {
                exists = false;
            }
        }
        order.setIntNumber(intNumber);
        order.setNumber(number);
    }

    /**
     * If order status is not {@link PurchaseOrderStatus#NEW} then order can't update
     *
     * @param id       Long
     * @param orderDTO {@link PurchaseOrderDTO}
     */
    @Transactional
    public void update(Long id, PurchaseOrderDTO orderDTO) {
        final String organizationId = Optional.ofNullable(orderDTO.getFromOrganizationId()).orElse(getCurrentOrganizationId());
        PurchaseOrder order = purchaseOrderRepository.findById(id).orElseThrow(notFound());
        if (!PurchaseOrderStatus.NEW.equals(order.getStatus())) {
            throw badRequest("Заказ уже '" + order.getStatus().getText() + "'").get();
        } else if (orderDTO.getStoreId() != null) {
            if (purchaseOrderRepository.existsPurchaseOrderByIdNotAndFromOrganizationIdAndStoreIdAndNumberIgnoreCase(id, organizationId, orderDTO.getStoreId(), orderDTO.getNumber())) {
                throw new BadRequestException("Заказ с таким номером уже существует");
            }
        } else {
            if (purchaseOrderRepository.existsPurchaseOrderByIdNotAndFromOrganizationIdAndNumberIgnoreCase(id, organizationId, orderDTO.getNumber())) {
                throw new BadRequestException("Заказ с таким номером уже существует");
            }
        }

        order.setCustomerId(orderDTO.getCustomerId());
        order.setContractId(orderDTO.getContractId());
        order.setStoreId(orderDTO.getStoreId());
        order.setOrderDate(orderDTO.getOrderDate());
        order.setNumber(orderDTO.getNumber());

        purchaseOrderRepository.save(order);
    }


    /**
     * <p>
     * if status is equal to {@link  PurchaseOrderStatus#NEW} purchase order available to change status
     * {@link PurchaseOrderStatus#APPROVED} or {@link PurchaseOrderStatus#REJECTED}
     * </p>
     * <p>
     * if status is equal to {@link PurchaseOrderStatus#REJECTED} not available to change status to another. Will be thrown
     * error with message 'Заказ уже Отклонено'
     * </p>
     * <p>
     * When shipment created from purchase order and it status equal to {@link ShipmentStatus#IN_PROGRESS}
     * then order status will changed to {@link PurchaseOrderStatus#IN_PROGRESS} automatically
     * </p>
     * <p>
     * When shipment closed which of converted from purchase order and it status equal to {@link ShipmentStatus#CLOSED}
     * then order status will changed to {@link PurchaseOrderStatus#CLOSED} automatically
     * </p>
     *
     * @param orderId Long
     * @param status  {@link PurchaseOrderStatus}
     * @return {@link PurchaseOrderStatus}
     */
    @Transactional
    public PurchaseOrderStatus updateStatus(Long orderId, PurchaseOrderStatus status) {
        PurchaseOrder purchaseOrder = purchaseOrderRepository.findById(orderId).orElseThrow(notFound());
        if (purchaseOrder.getStatus().checkingForUpdateStatus(status)) {
            purchaseOrder.setStatus(status);
            purchaseOrderRepository.save(purchaseOrder);
            return purchaseOrder.getStatus();
        }
        throw badRequest("Заказ уже '" + purchaseOrder.getStatus().getText() + "'").get();
    }

    public List<SelectItem> getItems(PurchaseOrderFilter filter) {
        filter.setStatus(PurchaseOrderStatus.CLOSED);
        return purchaseOrderRepository.findAllByFilter(filter).map(PurchaseOrder::toSelectItem).getContent();
    }

    /**
     * @param purchaseOrderId Long
     * @return newly created shipment id {@link Shipment}
     */
    @Transactional
    public String convertToShipment(Long purchaseOrderId) {
        PurchaseOrder purchaseOrder = purchaseOrderRepository.findById(purchaseOrderId).orElseThrow(notFound("Заказ не найден"));

        if (!purchaseOrder.getStatus().checkingForUpdateStatus(PurchaseOrderStatus.IN_PROGRESS)) {
            throw badRequest("Заказ уже '" + purchaseOrder.getStatus().getText() + "'").get();
        }

        Shipment shipment = new Shipment();
        shipment.setShipmentDate(purchaseOrder.getOrderDate().atStartOfDay());
        shipmentService.generateShipmentNumber(purchaseOrder.getFromOrganizationId(), shipment, purchaseOrder.getNumber() + "-");
        if (purchaseOrder.getCustomer() != null) {
            shipment.setCommittent(purchaseOrder.getCustomer().isCommittent());
            if (shipment.isCommittent()) {
                shipment.setDocumentType(DocumentType.ACCEPTANCE_TRANSFER_ACT);
            } else {
                shipment.setDocumentType(DocumentType.INVOICE);
            }
        }
        shipment.setStoreId(purchaseOrder.getStoreId());
        shipment.setCustomerId(purchaseOrder.getCustomerId());
        shipment.setStatus(ShipmentStatus.IN_PROGRESS);
        shipment.setPurchaseOrderId(purchaseOrderId);
        shipment.setOrganizationId(purchaseOrder.getFromOrganizationId());
        shipmentRepository.save(shipment);

        for (PurchaseOrderProduct product : purchaseOrder.getProducts()) {
            RedisShipmentItem redisShipmentItem = new RedisShipmentItem();
            redisShipmentItem.setShipmentId(shipment.getId());
            redisShipmentItem.setProductId(product.getProductId());
            redisShipmentItem.setProductName(product.getProduct().getVisibleName());
            redisShipmentItem.setBarcode(product.getProduct().getBarcode());
            redisShipmentItem.setQty(BigDecimal.ZERO);
            redisShipmentItem.setOrderQty(BigDecimal.valueOf(product.getQuantity()));
            if (StringUtils.isNotEmpty(product.getSerialId())) {
                serialNumberRepository.findById(product.getSerialId()).ifPresent(serialNumber -> {
                    redisShipmentItem.setSerialId(serialNumber.getId());
                    redisShipmentItem.setSerialNumber(serialNumber.getNameWithDate());
                });
            }

            redisShipmentItem.setTypes(createTypes(product));

            redisShipmentItem.setId(shipmentItemRepository.getNextSequenceValue());
            redisShipmentCodeRepository.save(redisShipmentItem);
        }
        updateStatus(purchaseOrderId, PurchaseOrderStatus.IN_PROGRESS);
        return shipment.getId();
    }

    private LinkedHashSet<ShipmentCode> createTypes(PurchaseOrderProduct orderProduct) {
        LinkedHashSet<ShipmentCode> types = new LinkedHashSet<>();
        types.add(ShipmentCode.builder().count(0).capacity(1).unit(null).unitName(null).build());
        int boxCapacity = 0;
        if (orderProduct.getProduct() != null) {
            boxCapacity = orderProduct.getProduct().getQtyInAggregation();
        }
        types.add(ShipmentCode.builder().count(0).capacity(boxCapacity).unit(PackageType.BOX).unitName(PackageType.BOX.getNameRu()).build());
        return types;
    }

    public Page<PurchaseOrderListDTO> findAllByFilter(PurchaseOrderFilter filter) {
        return purchaseOrderRepository.findAllByFilter(filter).map(purchaseOrder -> {
            PurchaseOrderListDTO listDTO = new PurchaseOrderListDTO();
            listDTO.setId(purchaseOrder.getId());
            listDTO.setNumber(purchaseOrder.getNumber());
            listDTO.setOrderDate(purchaseOrder.getOrderDate());
            listDTO.setCreatedBy(purchaseOrder.getCreatedBy());
            if (purchaseOrder.getCustomer() != null) {
                listDTO.setCustomer(purchaseOrder.getCustomer().toCommonDto());
            }
            if (purchaseOrder.getContract() != null) {
                listDTO.setContract(purchaseOrder.getContract().toSelectItem());
            }
            if (purchaseOrder.getStatus() != null) {
                listDTO.setStatus(purchaseOrder.getStatus().toDto());
            }
            if (purchaseOrder.getStore() != null) {
                listDTO.setStore(purchaseOrder.getStore().toSelectItem());
            }
            if (purchaseOrder.getType() != null) {
                listDTO.setType(purchaseOrder.getType().toDto());
            }
            listDTO.setShipments(purchaseOrder.getShipments().stream().map(shipment -> {
                listDTO.getInvoices().addAll(shipment.getInvoices().stream().filter(listInvoice -> !listInvoice.isDeleted()).map(i -> new InvoiceBaseDTO(i.getId(), i.getNumber())).collect(Collectors.toSet()));
                return shipment.toSelectItem();
            }).collect(Collectors.toList()));
            return listDTO;
        });
    }

    public PurchaseOrderDetailDTO get(Long id) {
        return purchaseOrderRepository.findById(id).map(PurchaseOrder::toDto).orElseThrow(notFound());
    }

    @Transactional
    public void delete(Long id) {
        PurchaseOrder purchaseOrder = purchaseOrderRepository.findById(id).orElseThrow(notFound());
        if (PurchaseOrderStatus.NEW.equals(purchaseOrder.getStatus())) {
            purchaseOrderRepository.deleteById(id);
        } else {
            throw badRequest("Заказ уже '" + purchaseOrder.getStatus().getText() + "'").get();
        }
    }
}
