package uz.uzkassa.silen.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.silen.domain.*;
import uz.uzkassa.silen.domain.mongo.Block;
import uz.uzkassa.silen.domain.mongo.Mark;
import uz.uzkassa.silen.domain.mongo.ShipmentMark;
import uz.uzkassa.silen.domain.redis.RedisShipmentItem;
import uz.uzkassa.silen.dto.ShipmentExportDTO;
import uz.uzkassa.silen.dto.ShipmentHistoryDTO;
import uz.uzkassa.silen.dto.ShipmentStatsDTO;
import uz.uzkassa.silen.dto.filter.ShipmentFilter;
import uz.uzkassa.silen.dto.invoice.InvoiceBaseDTO;
import uz.uzkassa.silen.dto.mq.Payload;
import uz.uzkassa.silen.dto.mq.WarehousePayload;
import uz.uzkassa.silen.dto.warehouse.*;
import uz.uzkassa.silen.enumeration.*;
import uz.uzkassa.silen.exceptions.BadRequestException;
import uz.uzkassa.silen.kafka.KafkaConstants;
import uz.uzkassa.silen.kafka.producer.KafkaProducer;
import uz.uzkassa.silen.repository.mongo.BlockRepository;
import uz.uzkassa.silen.repository.mongo.MarkRepository;
import uz.uzkassa.silen.repository.mongo.ShipmentMarkRepository;
import uz.uzkassa.silen.repository.pg.AggregationRepository;
import uz.uzkassa.silen.repository.pg.PurchaseOrderRepository;
import uz.uzkassa.silen.repository.pg.ShipmentItemRepository;
import uz.uzkassa.silen.repository.pg.ShipmentRepository;
import uz.uzkassa.silen.repository.redis.RedisShipmentCodeRepository;
import uz.uzkassa.silen.security.SecurityUtils;
import uz.uzkassa.silen.utils.CyrillicLatinConverter;
import uz.uzkassa.silen.utils.Utils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service Implementation for managing {@link Shipment}.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ShipmentService extends BaseService {
    InvoiceService invoiceService;
    MarkRepository markRepository;
    ShipmentRepository shipmentRepository;
    AggregationRepository aggregationRepository;
    ShipmentItemRepository shipmentItemRepository;
    ShipmentMarkRepository shipmentMarkRepository;
    RedisShipmentCodeRepository shipmentCodeRepository;
    PurchaseOrderRepository purchaseOrderRepository;
    KafkaProducer<WarehousePayload> kafkaProducer;
    BlockRepository blockRepository;
    ExportReportService exportReportService;

    @Transactional
    public String create(ShipmentDTO dto) {
        final String organizationId = getCurrentOrganizationId();
        if (dto.getStoreId() == null && dto.getCustomerId() == null) {
            throw new BadRequestException("Выберите получатель");
        }
        if (dto.getCustomerId() == null && DocumentType.INVOICE.equals(dto.getDocumentType())) {
            throw new BadRequestException("Выберите получатель или поменяйте тип документ");
        }
        Shipment shipment = new Shipment();
        shipment.setOrganizationId(organizationId);
        if (StringUtils.isNotEmpty(dto.getNumber())) {
            if (shipmentRepository.existsShipmentByOrganizationIdAndNumberIgnoreCase(organizationId, dto.getNumber())) {
                throw new BadRequestException("Отгрузка с таким номером уже существует");
            } else {
                shipment.setNumber(dto.getNumber());
            }
        } else {
            generateShipmentNumber(organizationId, shipment, "SHIP-");
        }
        shipment.setCustomerId(dto.getCustomerId());
        shipment.setShipmentDate(dto.getShipmentDate());
        shipment.setStatus(ShipmentStatus.NEW);
        shipment.setCommittent(dto.isCommittent());
        shipment.setStoreId(dto.getStoreId());
        shipment.setDocumentType(dto.getDocumentType());
        shipment.setNote(dto.getNote());
        shipment = shipmentRepository.save(shipment);

        return shipment.getId();
    }

    public void generateShipmentNumber(String organizationId, Shipment shipment, String prefix) {
        Integer intNumber = shipmentRepository.getMaxIntNumber(organizationId);
        String number = null;
        if (intNumber == null) {
            intNumber = 1;
        }
        boolean exists = true;
        while (exists) {
            number = prefix + intNumber;
            if (shipmentRepository.existsShipmentByOrganizationIdAndNumberIgnoreCase(organizationId, number)) {
                intNumber++;
            } else {
                exists = false;
            }
        }
        shipment.setIntNumber(intNumber);
        shipment.setNumber(number);
    }

    @Transactional
    public void update(String id, ShipmentDTO dto) {
        if (dto.getStoreId() == null && dto.getCustomerId() == null) {
            throw new BadRequestException("Выберите получатель");
        }
        if (dto.getCustomerId() == null && DocumentType.INVOICE.equals(dto.getDocumentType())) {
            throw new BadRequestException("Выберите получатель или поменяйте тип документ");
        }
        shipmentRepository.findById(id).ifPresent(shipment -> {
            if (ShipmentStatus.CLOSED.equals(shipment.getStatus())) {
                throw badRequest("Отгрузка уже завершена").get();
            }
            if (StringUtils.isNotEmpty(dto.getNumber())) {
                if (shipmentRepository.existsShipmentByOrganizationIdAndNumberIgnoreCaseAndIdIsNot(getCurrentOrganizationId(), dto.getNumber(), id)) {
                    throw new BadRequestException("Отгрузка с таким номером уже существует");
                } else {
                    shipment.setNumber(dto.getNumber());
                }
            }
            shipment.setCustomerId(dto.getCustomerId());
            shipment.setShipmentDate(dto.getShipmentDate());
            shipment.setCommittent(dto.isCommittent());
            shipment.setStoreId(dto.getStoreId());
            shipment.setDocumentType(dto.getDocumentType());
            shipment.setNote(dto.getNote());
        });
    }

    public Page<ShipmentListDTO> findAllByFilter(ShipmentFilter filter) {
        if (filter.checkSearchIsEmpty()) {
            Optional<ShipmentMark> markOptional = shipmentMarkRepository.findFirstByCode(Utils.clearPrintCode(filter.getSearch()));
            if (markOptional.isPresent() && markOptional.get().getShipmentItemId() != null) {
                ShipmentItem item = shipmentItemRepository.getReferenceById(markOptional.get().getShipmentItemId());
                filter.setId(item.getShipmentId());
            }
        }
        return shipmentRepository.findAllByFilter(filter).map(Shipment::toListDto);
    }

    public List<ShipmentListDTO> getItems(ShipmentFilter filter) {
        filter.setStatus(ShipmentStatus.CLOSED);
        return shipmentRepository.findAllByFilter(filter).map(Shipment::toListDto).getContent();
    }

    public List<ShipmentStatsDTO> findAllForStats(ShipmentFilter filter) {
        if (filter.checkSearchIsEmpty()) {
            Optional<ShipmentMark> markOptional = shipmentMarkRepository.findFirstByCode(Utils.clearPrintCode(filter.getSearch()));
            if (markOptional.isPresent() && markOptional.get().getShipmentItemId() != null) {
                ShipmentItem item = shipmentItemRepository.getReferenceById(markOptional.get().getShipmentItemId());
                filter.setId(item.getShipmentId());
            }
        }
        return shipmentRepository.findAllByFilterForStats(filter);
    }

    public Page<ShipmentDTO> findAllWithoutOrgIdFilter(ShipmentFilter filter) {
        return shipmentRepository.findAllByFilterWithoutOrgId(filter).map(Shipment::toDto);
    }

    public ShipmentDetailsDTO getOne(String id) {
        Shipment shipment = shipmentRepository.findById(id).orElseThrow(notFound());
        ShipmentDetailsDTO dto = new ShipmentDetailsDTO();
        dto.setId(shipment.getId());
        dto.setNumber(shipment.getNumber());
        dto.setShipmentDate(shipment.getShipmentDate());
        if (shipment.getCustomer() != null) {
            dto.setCustomer(shipment.getCustomer().toSelectItem());
        }
        if (shipment.getStatus() != null) {
            dto.setStatus(shipment.getStatus().toDto());
        }
        List<ShipmentItemDTO> items;
        if (EnumSet.of(ShipmentStatus.CLOSED, ShipmentStatus.CANCELLED).contains(shipment.getStatus())) {
            items = shipmentItemRepository.findAllByShipmentIdOrderById(id).stream().map(shipmentItem -> shipmentItem.toDto(false)).collect(Collectors.toList());
        } else {
            List<RedisShipmentItem> allByShipmentIdOrderById = shipmentCodeRepository.findAllByShipmentIdOrderById(id);
            items = calculateRedisShipmentItem(allByShipmentIdOrderById);
        }
        dto.setItems(items);
        dto.setCommittent(shipment.isCommittent());
        dto.setNote(shipment.getNote());
        if (shipment.getDocumentType() != null) {
            dto.setDocumentType(shipment.getDocumentType().toSelectItem());
        }
        if (shipment.getStore() != null) {
            dto.setStore(shipment.getStore().toSelectItem());
        }
        if (shipment.getInvoices() != null) {
            dto.setInvoices(shipment.getInvoices().stream().filter(listInvoice -> !listInvoice.isDeleted())
                    .map(i -> new InvoiceBaseDTO(i.getId(), i.getNumber())).collect(Collectors.toSet()));
        }
        if (shipment.getActs() != null) {
            dto.setActs(shipment.getActs().stream().map(AcceptanceTransferAct::toSelectItem).collect(Collectors.toSet()));
        }
        return dto;
    }

    private List<ShipmentItemDTO> calculateRedisShipmentItem(List<RedisShipmentItem> redisShipmentItems) {
        List<ShipmentItemDTO> shipmentItemDTOS = new LinkedList<>();
        for (RedisShipmentItem shipmentItem : redisShipmentItems) {
            final ShipmentItemDTO shipmentItemDTO = shipmentItem.toDto();
            final List<ShipmentMark> shipmentMarkList = shipmentMarkRepository.findAllByShipmentItemId(shipmentItem.getId());
            final Map<PackageType, List<ShipmentMark>> groupByUnit = shipmentMarkList.stream()
                    .collect(Collectors.groupingBy(ShipmentMark::getPackageType));

            BigDecimal quantity = BigDecimal.ZERO;
            LinkedHashSet<ShipmentCode> shipmentCodesDto = new LinkedHashSet<>(3);
            for (ShipmentCode shipmentCode : shipmentItemDTO.getTypes()) {
                final PackageType packageType = Optional.ofNullable(shipmentCode.getUnit()).orElse(PackageType.BOTTLE);
                int unitCapacityOrAddedQuantity = PackageType.BOTTLE == packageType ? 1 : shipmentCode.getCapacity();
                final List<ShipmentMark> shipmentMark = groupByUnit.getOrDefault(packageType, Collections.emptyList());
                shipmentCode.setCount(shipmentMark.size());
                shipmentCodesDto.add(shipmentCode);
                Integer i = shipmentMark.stream().map(shipmentMarkDTO -> shipmentMarkDTO.getQty() != 0 ? shipmentMarkDTO.getQty() : unitCapacityOrAddedQuantity).reduce(Integer::sum).orElse(0);
                quantity = quantity.add(BigDecimal.valueOf(i));
            }
            shipmentItemDTO.setTypes(shipmentCodesDto);
            shipmentItemDTO.setQty(quantity);
            shipmentItemDTOS.add(shipmentItemDTO);
        }
        return shipmentItemDTOS;
    }

    @Transactional
    public void cancel(final String id) {
        final Shipment shipment = shipmentRepository.findById(id)
                .orElseThrow(notFound());
        if (shipment.getStatus() != ShipmentStatus.CLOSED) {
            throw badRequest("Можно отменить только закрытые отгрузки").get();
        }
        shipment.setStatus(ShipmentStatus.CANCELLED);
        shipmentRepository.save(shipment);

        final String organizationId = shipment.getOrganizationId();
        final LocalDateTime operationDate = LocalDateTime.now();
        final List<ShipmentItem> allByShipmentId = shipmentItemRepository.findAllByShipmentId(id);
        for (ShipmentItem item : allByShipmentId) {
            final String productId = item.getProductId();

            final List<ShipmentMark> allByShipmentItemId = shipmentMarkRepository.findAllByShipmentItemId(item.getId());
            final Map<PackageType, List<ShipmentMark>> codesMap = allByShipmentItemId
                    .stream().collect(Collectors.groupingBy(ShipmentMark::getPackageType));

            for (PackageType packageType : codesMap.keySet()) {
                final List<String> shipmentMarks = codesMap.get(packageType).stream().map(ShipmentMark::getCode).toList();
                for (String code : shipmentMarks) {
                    if (packageType.isAggregation()) {
                        final Aggregation aggregation = aggregationRepository.findFirstByCode(code).orElse(null);
                        if (aggregation != null && AggregationStatus.SHIPPED == aggregation.getStatus()) {
                            aggregation.setStatus(AggregationStatus.RETURNED);
                            aggregationRepository.save(aggregation);
                            // add to warehouse like returned
                            WarehousePayload payload = new WarehousePayload();
                            payload.setOrganizationId(organizationId);
                            payload.setOperationDate(operationDate);
                            payload.setOperation(WarehouseOperation.Returned);
                            payload.setNote("Возвращен");
                            payload.setUnit(aggregation.getPackageType());
                            payload.setAlcoholCount(BigDecimal.valueOf(aggregation.getQuantity()));
                            payload.setProductId(productId);
                            payload.setSerialId(aggregation.getSerialId());
                            payload.setCode(code);
                            kafkaProducer.sendMessage(KafkaConstants.WAREHOUSE_TOPIC, payload);
                        }
                    } else if (packageType.isMark()) {
                        Mark mark = markRepository.findFirstByCode(code).orElse(null);
                        if (mark != null && AggregationStatus.SHIPPED == mark.getStatus()) {
                            mark.setStatus(AggregationStatus.RETURNED);
                            markRepository.save(mark);
                            // add to warehouse like returned
                            WarehousePayload payload = new WarehousePayload();
                            payload.setOrganizationId(organizationId);
                            payload.setOperationDate(operationDate);
                            payload.setOperation(WarehouseOperation.Returned);
                            payload.setNote("Возвращен");
                            payload.setUnit(mark.getPackageType());
                            payload.setAlcoholCount(BigDecimal.valueOf(mark.getQuantity()));
                            payload.setProductId(productId);
                            if (mark.isBlock()) {
                                payload.setSerialId(((Block) mark).getSerialId());
                            }
                            payload.setCode(code);
                            kafkaProducer.sendMessage(KafkaConstants.WAREHOUSE_TOPIC, payload);
                        }
                    }
                }
            }
        }
    }

    @Transactional
    public String copy(final String id) {
        final Shipment shipment = shipmentRepository.findById(id)
                .orElseThrow(notFound());
        if (shipment.getStatus() != ShipmentStatus.CANCELLED) {
            throw badRequest("Можно копировать только отмененные отгрузки").get();
        }
        final ShipmentDTO dto = new ShipmentDTO();
        dto.setStoreId(shipment.getStoreId());
        dto.setCustomerId(shipment.getCustomerId());
        dto.setDocumentType(shipment.getDocumentType());
        dto.setShipmentDate(LocalDateTime.now());
        dto.setCommittent(shipment.isCommittent());
        final String newShipmentId = create(dto);

        final Set<ShipmentItem> items = shipment.getItems();
        for (final ShipmentItem item : items) {
            final RedisShipmentItem redisShipmentItem = new RedisShipmentItem();
            redisShipmentItem.setShipmentId(newShipmentId);
            redisShipmentItem.setTypes(item.getTypes());
            redisShipmentItem.setProductId(item.getProductId());
            redisShipmentItem.setOrderQty(item.getOrderQty());
            if (item.getProduct() != null) {
                final Product product = item.getProduct();
                redisShipmentItem.setProductName(product.getVisibleName());
                redisShipmentItem.setBarcode(product.getBarcode());
                redisShipmentItem.setParentBarcode(product.getParentBarcode());
            }
            redisShipmentItem.setQty(item.getQty());
            redisShipmentItem.setSerialId(item.getSerialId());
            if (item.getSerialNumber() != null) {
                redisShipmentItem.setSerialNumber(item.getSerialNumber().getNameWithDate());
            }
            final Long itemId = shipmentItemRepository.getNextSequenceValue();
            redisShipmentItem.setId(itemId);
            shipmentCodeRepository.save(redisShipmentItem);

            copyMarks(item.getId(), itemId);
        }

        return newShipmentId;
    }

    private void copyMarks(final Long oldItemId, final Long newItemId) {
        final List<ShipmentMark> marks = shipmentMarkRepository.findAllByShipmentItemId(oldItemId);
        List<ShipmentMark> list = marks.stream().peek(shipmentMark -> shipmentMark.setShipmentItemId(newItemId)).toList();
        shipmentMarkRepository.saveAll(list);
    }

    @Transactional
    public void close(String id) {
        Shipment shipment = shipmentRepository.findById(id).orElseThrow(notFound());
        shipment.setStatus(ShipmentStatus.CLOSED);
        shipmentRepository.save(shipment);

        List<RedisShipmentItem> shipmentItems = shipmentCodeRepository.findAllByShipmentIdOrderById(shipment.getId());
        if (CollectionUtils.isNotEmpty(shipmentItems)) {
            List<ShipmentItem> items = new ArrayList<>();

            List<ShipmentItemDTO> shipmentItemDTOS = calculateRedisShipmentItem(shipmentItems);
            for (ShipmentItemDTO shipmentItemDTO : shipmentItemDTOS) {
                ShipmentItem entity = new ShipmentItem();
                entity.setId(shipmentItemDTO.getId());
                entity.setProductId(shipmentItemDTO.getProductId());
                entity.setShipmentId(shipmentItemDTO.getShipmentId());
                entity.setQty(shipmentItemDTO.getQty());
                entity.setTypes(shipmentItemDTO.getTypes());
                entity.setSerialId(shipmentItemDTO.getSerialId());
                items.add(entity);
            }
            log.debug("Save shipment items on close");
            shipmentItemRepository.saveAll(items);
            shipmentCodeRepository.deleteAll(shipmentItems);
        }

        if (shipment.getPurchaseOrder() != null) {
            PurchaseOrder purchaseOrder = shipment.getPurchaseOrder();
            purchaseOrder.setStatus(PurchaseOrderStatus.CLOSED);
            purchaseOrderRepository.save(purchaseOrder);
        }

        Payload payload = new Payload();
        payload.setId(id);
        payload.setExtraId(SecurityUtils.getCurrentUserLogin());
        log.debug("ShipmentClose action payload {}", payload);
        switch (shipment.getDocumentType()) {
            case ACCEPTANCE_TRANSFER_ACT:
                rabbitMqProducer.sendShipmentAct(payload);
                break;
            case INVOICE:
                rabbitMqProducer.sendShipmentInvoice(payload);
                break;
        }
        rabbitMqProducer.sendShipmentItemsToWarehouse(payload);
    }

    public void convert(String id) {
        invoiceService.createShipmentInvoice(id, false);
    }

    @Transactional
    public void delete(String id) {
        Shipment shipment = shipmentRepository.findById(id).orElseThrow(notFound());
        if (!isAgency() && ShipmentStatus.CLOSED.equals(shipment.getStatus())) {
            throw badRequest("Невозможно удалить завершенную отгрузку").get();
        }
        if (ShipmentStatus.CLOSED.equals(shipment.getStatus())) {
            List<ShipmentItem> items = shipmentItemRepository.findAllByShipmentId(id);
            for (ShipmentItem item : items) {
                shipmentMarkRepository.findAllByShipmentItemIdAndUnitIsNull(item.getId())
                        .forEach(shipmentMark -> markRepository.findFirstByCode(shipmentMark.getCode())
                                .ifPresent(mark -> {
                                    if (mark.isBlock()) {
                                        mark.setStatus(AggregationStatus.CREATED);
                                    } else {
                                        mark.setStatus(AggregationStatus.DRAFT);
                                        mark.setUsed(false);
                                    }
                                    markRepository.save(mark);
                                }));
                shipmentMarkRepository.deleteAllByShipmentItemId(item.getId());
            }
            shipmentItemRepository.deleteAll(items);
        } else {
            List<RedisShipmentItem> items = shipmentCodeRepository.findAllByShipmentId(id);
            items.forEach(redisShipmentItem -> shipmentMarkRepository.deleteAllByShipmentItemId(redisShipmentItem.getId()));
            shipmentCodeRepository.deleteAll(items);
        }
        shipmentRepository.deleteById(id);
        if (shipment.getPurchaseOrder() != null) {
            PurchaseOrder purchaseOrder = shipment.getPurchaseOrder();
            if (PurchaseOrderStatus.IN_PROGRESS.equals(purchaseOrder.getStatus())) {
                purchaseOrder.setStatus(PurchaseOrderStatus.APPROVED);
                purchaseOrderRepository.save(purchaseOrder);
            }
        }
    }

    public void removeDeletedCodes() {
        shipmentMarkRepository.deleteAllByShipmentItemIdIsNull();
    }

    public Page<WarehouseSelectItem> getShippedProductsStats(ShipmentFilter filter) {
        return shipmentRepository.getShippedProductsStats(filter);
    }

    public Page<ShipmentHistoryDTO> getShipHistory(ShipmentFilter filter) {
        filter.setStatus(ShipmentStatus.CLOSED);
        return shipmentRepository.findAllByFilter(filter).map(shipment -> {
            ShipmentHistoryDTO historyDTO = new ShipmentHistoryDTO();
            historyDTO.setShipment(shipment.toSelectItem());
            historyDTO.setInvoice(shipment.getInvoices().stream().filter(listInvoice -> listInvoice.getStatus().equals(Status.ACCEPTED)).map(ListInvoice::toSelectItem).collect(Collectors.toList()));
            return historyDTO;
        });
    }

    public BigDecimal getShipHistorySummary(ShipmentFilter filter) {
        return shipmentRepository.getShipHistorySummary(filter);
    }

    /**
     * Method will execute from RabbitMQ task
     * payload.id is shipmentId
     * payload.extraId is who is closed shipment user's login
     *
     * @param closedActionPayload {@link Payload}
     */
    @Transactional
    public void sendClosedShipment(Payload closedActionPayload) {
        final String shipmentId = closedActionPayload.getId();
        final String userLogin = closedActionPayload.getExtraId();
        Shipment shipment = shipmentRepository.findById(shipmentId).orElseGet(Shipment::new);
        if (shipment == null) return;

        for (ShipmentItem shipmentItem : shipment.getItems()) {
            List<ShipmentMark> allCodesByShipmentItemId = shipmentMarkRepository.findAllByShipmentItemId(shipmentItem.getId());
            Set<String> disAggregateParent = new HashSet<>();
            Set<String> aggregations = new HashSet<>();
            Set<String> blocks = new HashSet<>();
            Set<String> marks = new HashSet<>();

            for (ShipmentMark shipmentMark : allCodesByShipmentItemId) {
                PackageType packageType = shipmentMark.getPackageType();
                if (CodeType.AGGREGATION.equals(packageType.getCodeType())) {
                    aggregations.add(Utils.clearAggregationCode(shipmentMark.getCode()));
                    aggregations.add(shipmentMark.getCode());
                }
                if (PackageType.BLOCK.equals(packageType)) {
                    blocks.add(shipmentMark.getCode());
                }
                if (CodeType.MARK.equals(packageType.getCodeType())) {
                    marks.add(shipmentMark.getCode());
                }
            }

            // only aggregations
            if (CollectionUtils.isNotEmpty(aggregations)) {
                List<Aggregation> aggregationList = aggregationRepository.findAllByCodeInOrParentCodeIn(aggregations);
                List<Aggregation> aggregationBatch = new LinkedList<>();
                for (Aggregation aggregationItem : aggregationList) {
                    if (aggregationItem.getParentCode() != null && !aggregations.contains(aggregationItem.getParentCode())) {
                        disAggregateParent.add(aggregationItem.getParentCode());
                        aggregationItem.setParentCode(null);
                    }
                    // change here aggregations
                    aggregationItem.setStatus(AggregationStatus.SHIPPED);
                    aggregationItem.setShipmentId(shipmentId);
                    aggregationBatch.add(aggregationItem);
                    //sending aggregation item like shipped
                    sendAggregationToWarehouse(aggregationItem, shipment, shipmentItem.getProductId(), userLogin);
                }
                if (CollectionUtils.isNotEmpty(aggregationBatch)) {
                    aggregationRepository.saveAll(aggregationBatch);
                }
            }
            // only blocks
            if (CollectionUtils.isNotEmpty(blocks)) {
                List<Block> blockList = blockRepository.findAllByCodeIn(blocks);
                List<Block> blockBatch = new LinkedList<>();
                for (Block block : blockList) {
                    if (block.getParentCode() != null && !aggregations.contains(block.getParentCode())) {
                        disAggregateParent.add(block.getParentCode());
                        block.setParentCode(null);
                    }
                    block.setStatus(AggregationStatus.SHIPPED);
                    blockBatch.add(block);
                    //sending block item like shipped
                    sendBlockToWarehouse(block, shipment);
                }
                if (CollectionUtils.isNotEmpty(blockBatch)) {
                    blockRepository.saveAll(blockBatch);
                }
            }
            // only marks
            if (CollectionUtils.isNotEmpty(marks)) {
                List<Mark> markList = markRepository.findAllByCodeIn(marks);
                List<Mark> markBatch = new LinkedList<>();
                for (Mark markItem : markList) {
                    if (markItem.getParentCode() != null && (!aggregations.contains(markItem.getParentCode()) || !blocks.contains(markItem.getParentCode()))) {
                        disAggregateParent.add(markItem.getParentCode());
                        markItem.setParentCode(null);
                    }
                    markItem.setUsed(true);
                    markBatch.add(markItem);
                }
                if (CollectionUtils.isNotEmpty(markBatch)) {
                    markRepository.saveAll(markBatch);
                }
                if (CollectionUtils.isNotEmpty(marks)) {
                    // send to warehouse all bottles which parent is null
                    WarehousePayload payload = new WarehousePayload();
                    payload.setOrganizationId(shipment.getOrganizationId());
                    payload.setOperation(WarehouseOperation.Production);
                    payload.setNote(WarehouseOperation.Production.getNameRu());
                    payload.setUnit(PackageType.BOTTLE);
                    payload.setAlcoholCount(BigDecimal.valueOf(marks.size()));
                    payload.setProductId(shipmentItem.getProductId());
                    payload.setLogin(userLogin);
                    payload.setOperationDate(LocalDateTime.now());
                    kafkaProducer.sendMessage(KafkaConstants.WAREHOUSE_TOPIC, payload);

                    payload.setOperation(WarehouseOperation.ShipmentBuyer);
                    payload.setNote("Отгружено");
                    log.debug("ShipmentBuyer payload UNIT {}", payload);
                    kafkaProducer.sendMessage(KafkaConstants.WAREHOUSE_TOPIC, payload);
                }
            }
            // disaggregate parent boxes
            if (CollectionUtils.isNotEmpty(disAggregateParent)) {
                //update all aggregations
                List<Aggregation> aggregationList = aggregationRepository.findAllByUnitAndCodeIn(PackageType.BOX.getCode(), disAggregateParent);
                for (Aggregation aggregation : aggregationList) {

                    WarehousePayload payload = new WarehousePayload();
                    payload.setOperation(WarehouseOperation.Utilized);
                    payload.setNote("Дизагрегировано " + aggregation.getCode());
                    payload.setUnit(aggregation.getPackageType());
                    payload.setAlcoholCount(BigDecimal.valueOf(aggregation.getQuantity()));
                    payload.setOperationDate(LocalDateTime.now());
                    payload.setOrganizationId(aggregation.getOrganizationId());
                    payload.setProductId(aggregation.getProductId());
                    payload.setCode(aggregation.getCode());
                    payload.setLogin(userLogin);
                    kafkaProducer.sendMessage(KafkaConstants.WAREHOUSE_TOPIC, payload);

                    aggregation.setQuantity(0);
                    aggregation.setStatus(AggregationStatus.DISAGGREGATED);
                    aggregationRepository.save(aggregation);

                    // if box content is mark
                    markRepository.findAllByParentCode(aggregation.getCode()).forEach(mark -> {
                        if (mark.isBlock()) {
                            mark.setParentCode(null);
                            markRepository.save(mark);
                        } else {
                            mark.setUsed(false);
                            mark.setStatus(null);
                            mark.setParentCode(null);
                            markRepository.save(mark);
                        }
                    });
                }

                List<Block> byCodeIn = blockRepository.findAllByCodeIn(disAggregateParent);
                for (Block block : byCodeIn) {
                    WarehousePayload payload = new WarehousePayload();
                    payload.setOperation(WarehouseOperation.Utilized);
                    payload.setNote("Дизагрегировано " + block.getCode());
                    payload.setUnit(block.getUnit());
                    payload.setAlcoholCount(BigDecimal.valueOf(block.getQuantity()));
                    payload.setOperationDate(LocalDateTime.now());
                    payload.setOrganizationId(block.getOrganizationId());
                    payload.setProductId(block.getProductId());
                    payload.setCode(block.getCode());
                    payload.setLogin(userLogin);
                    kafkaProducer.sendMessage(KafkaConstants.WAREHOUSE_TOPIC, payload);

                    block.setQuantity(0);
                    block.setStatus(AggregationStatus.DISAGGREGATED);
                    blockRepository.save(block);

                    markRepository.findAllByParentCode(block.getCode()).forEach(mark -> {
                        mark.setUsed(false);
                        mark.setParentCode(null);
                        mark.setStatus(null);
                        markRepository.save(mark);
                    });
                }
            }
        }
    }

    /**
     * send aggregation to warehouse. WarehouseOperation is ShipmentBuyer
     *
     * @param aggregation @see {@link Aggregation}
     * @param shipment    @see {@link Shipment}
     * @param productId   @see {@link Product}
     */
    private void sendAggregationToWarehouse(Aggregation aggregation, Shipment shipment, String productId, String userLogin) {
        WarehousePayload payload = new WarehousePayload();
        payload.setOrganizationId(shipment.getOrganizationId());
        payload.setOperationDate(LocalDateTime.now());
        payload.setOperation(WarehouseOperation.ShipmentBuyer);
        payload.setNote("Отгружено");
        payload.setProductId(productId);
        payload.setUnit(aggregation.getPackageType());
        payload.setAlcoholCount(BigDecimal.valueOf(aggregation.getQuantity()));
        payload.setCode(aggregation.getCode());
        payload.setLogin(userLogin);
        kafkaProducer.sendMessage(KafkaConstants.WAREHOUSE_TOPIC, payload);
    }

    /**
     * send block to warehouse. WarehouseOperation is ShipmentBuyer
     *
     * @param block    @see {@link Block}
     * @param shipment @see {@link Shipment}
     */
    private void sendBlockToWarehouse(Block block, Shipment shipment) {
        WarehousePayload payload = new WarehousePayload();
        payload.setOrganizationId(shipment.getOrganizationId());
        payload.setOperationDate(LocalDateTime.now());
        payload.setOperation(WarehouseOperation.ShipmentBuyer);
        payload.setNote("Отгружено");
        payload.setProductId(block.getProductId());
        payload.setUnit(PackageType.BLOCK);
        payload.setAlcoholCount(BigDecimal.valueOf(block.getQuantity()));
        payload.setCode(block.getCode());
        payload.setLogin(shipment.getLastModifiedBy());
        kafkaProducer.sendMessage(KafkaConstants.WAREHOUSE_TOPIC, payload);
    }

    public List<ShipmentItemDTO> sync(final String id) {
        final Shipment shipment = shipmentRepository.findById(id).orElseThrow(notFound());
        if (ShipmentStatus.CLOSED.equals(shipment.getStatus())) {
            throw badRequest("Отгрузка уже завершена").get();
        }
        final List<RedisShipmentItem> list = shipmentCodeRepository.findAllByShipmentIdOrderById(id);
        for (RedisShipmentItem item : list) {
            if (item != null && item.getTypes() != null) {
                shipmentCodeRepository.save(calculateQty2(item));
            }
        }
        return list.stream().map(RedisShipmentItem::toDto).collect(Collectors.toList());
    }

    private RedisShipmentItem calculateQty(RedisShipmentItem item) {
        int qty = 0;
        final Map<PackageType, ShipmentCode> types = new LinkedHashMap<>();
        for (ShipmentCode code : item.getTypes()) {
            types.put(code.getUnit(), code);
        }
        final List<ShipmentMark> marks = shipmentMarkRepository.findAllByShipmentItemId(item.getId());
        final Map<Integer, Long> count = new HashMap<>();
        for (ShipmentMark mark : marks) {
            count.putIfAbsent(mark.getUnit(), 0L);
            count.put(mark.getUnit(), count.get(mark.getUnit()) + 1);
        }
        if (count.get(null) != null) {
            final int markCount = count.get(null).intValue();
            types.get(null).setCount(markCount);
            qty += markCount;
        }
        if (types.get(PackageType.BLOCK) != null && count.get(PackageType.BLOCK.getCode()) != null) {
            final Integer markCount = count.get(PackageType.BLOCK.getCode()).intValue();
            types.get(PackageType.BLOCK).setCount(markCount);
            qty += types.get(PackageType.BLOCK).getCapacity() * markCount;
        }
        if (types.get(PackageType.BOX) != null && count.get(PackageType.BOX.getCode()) != null) {
            final Integer markCount = count.get(PackageType.BOX.getCode()).intValue();
            types.get(PackageType.BOX).setCount(markCount);
            qty += types.get(PackageType.BOX).getCapacity() * markCount;
        }
        if (types.get(PackageType.PALLET) != null && count.get(PackageType.PALLET.getCode()) != null) {
            final Integer markCount = count.get(PackageType.PALLET.getCode()).intValue();
            types.get(PackageType.BOX).setCount(markCount);
            qty += types.get(PackageType.BOX).getCapacity() * types.get(PackageType.PALLET).getCapacity() * markCount;
        }
        item.setTypes(new LinkedHashSet<>(types.values()));
        item.setQty(BigDecimal.valueOf(qty));
        return item;
    }

    private RedisShipmentItem calculateQty2(RedisShipmentItem item) {
        final Map<PackageType, ShipmentCode> types = item.getTypes()
                .stream()
                .collect(Collectors.toMap(ShipmentCode::getUnit, shipmentCode -> {
                    Integer typeUnit = shipmentCode.getUnit() != null ? shipmentCode.getUnit().getCode() : null;
                    Long count = shipmentMarkRepository.countAllByShipmentItemIdAndUnit(item.getId(), typeUnit);
                    shipmentCode.setCount(count.intValue());
                    return shipmentCode;
                }));

        Integer qty = types.containsKey(null) ? types.get(null).getCount() : Integer.valueOf(0);

        if (types.containsKey(PackageType.BLOCK)) {
            final Integer count = types.get(PackageType.BLOCK).getCount();
            qty += types.get(PackageType.BLOCK).getCapacity() * count;
        }
        if (types.containsKey(PackageType.BOX)) {
            final Integer count = types.get(PackageType.BOX).getCount();
            qty += types.get(PackageType.BOX).getCapacity() * count;
        }

        if (types.containsKey(PackageType.PALLET)) {
            final Integer quantity = types.get(PackageType.PALLET).getCount();
            if (types.containsKey(PackageType.BOX)) {
                qty += types.get(PackageType.BOX).getCapacity() * quantity;
            } else if (types.containsKey(PackageType.BLOCK)) {
                qty += types.get(PackageType.BLOCK).getCapacity() * quantity;
            } else {
                qty += types.get(PackageType.PALLET).getCapacity() * quantity;
            }
        }
        item.setTypes(new LinkedHashSet<>(types.values()));
        item.setQty(BigDecimal.valueOf(qty));
        return item;
    }

    public void export(ShipmentExportDTO shipmentExportDTO) {
        Optional<Long> first = shipmentExportDTO.getShipmentItems().stream().findFirst();
        String name = "ShipmentCodes";
        if (first.isPresent()) {
            Optional<ShipmentItem> byId = shipmentItemRepository.findById(first.get());
            if (byId.isPresent()) {
                Shipment shipment = byId.get().getShipment();
                name = CyrillicLatinConverter.cyrilicToLatin(shipment.getNumber());
            }
        }
        ExportReportType exportReportType = FileType.EXCEL.equals(shipmentExportDTO.getFileType()) ? ExportReportType.EXCEL_SHIPMENT : ExportReportType.CSV_SHIPMENT;
        exportReportService.export(shipmentExportDTO, exportReportType, shipmentExportDTO.getFileType(), name);
    }
}
