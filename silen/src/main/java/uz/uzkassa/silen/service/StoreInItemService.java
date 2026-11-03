package uz.uzkassa.silen.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import uz.uzkassa.silen.domain.ShipmentItem;
import uz.uzkassa.silen.domain.StoreIn;
import uz.uzkassa.silen.domain.StoreInItem;
import uz.uzkassa.silen.domain.mongo.ShipmentMark;
import uz.uzkassa.silen.domain.mongo.StoreInMark;
import uz.uzkassa.silen.dto.StoreInItemCreateDTO;
import uz.uzkassa.silen.dto.StoreInItemDTO;
import uz.uzkassa.silen.dto.StoreInMarkDTO;
import uz.uzkassa.silen.dto.mq.Payload;
import uz.uzkassa.silen.dto.warehouse.ShipmentCode;
import uz.uzkassa.silen.enumeration.StockInStatus;
import uz.uzkassa.silen.repository.mongo.ShipmentMarkRepository;
import uz.uzkassa.silen.repository.mongo.StoreInMarkRepository;
import uz.uzkassa.silen.repository.pg.ShipmentItemRepository;
import uz.uzkassa.silen.repository.pg.StoreInItemRepository;
import uz.uzkassa.silen.repository.pg.StoreInRepository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class StoreInItemService extends BaseService {
    private final StoreInRepository storeInRepository;
    private final StoreInItemRepository storeInItemRepository;
    private final StoreInMarkRepository storeInMarkRepository;
    private final ShipmentItemRepository shipmentItemRepository;
    private final ShipmentMarkRepository shipmentMarkRepository;


    public String create(StoreInItemCreateDTO dto) {
        StoreIn storeIn = storeInRepository.findById(dto.getStoreInId())
            .orElseThrow(notFound("Возврат не найден"));

        if (StockInStatus.COMPLETED.equals(storeIn.getStatus())) {
            throw badRequest("Процесс уже завершен!").get();
        }
        List<ShipmentItem> shipmentItems = shipmentItemRepository.findAllByIdIn(dto.getShipmentItemIds());
        for (ShipmentItem shipmentItem : shipmentItems) {
            StoreInItem storeInItem = new StoreInItem();
            storeInItem.setStoreInId(dto.getStoreInId());
            storeInItem.setProductId(shipmentItem.getProductId());
            storeInItem.setSerialId(shipmentItem.getSerialId());
            storeInItem.setQty(shipmentItem.getQty().intValue());
            storeInItemRepository.save(storeInItem);

            List<ShipmentMark> shipmentMarkList = shipmentMarkRepository.findAllByShipmentItemId(shipmentItem.getId());
            List<StoreInMark> storeInMarks = new ArrayList<>();
            for (ShipmentMark shipmentMark : shipmentMarkList) {
                StoreInMark storeInMark = new StoreInMark();
                storeInMark.setUnit(shipmentMark.getUnit());
                storeInMark.setCode(shipmentMark.getCode());
                storeInMark.setPrintCode(shipmentMark.getPrintCode());
                storeInMark.setItemId(storeInItem.getId());
                storeInMarks.add(storeInMark);

                if (storeInMarks.size() > 50) {
                    storeInMarkRepository.saveAll(storeInMarks);
                }
            }
            if (!CollectionUtils.isEmpty(storeInMarks)) {
                storeInMarkRepository.saveAll(storeInMarks);
            }
            shipmentItem.setQty(BigDecimal.ZERO);
            LinkedHashSet<ShipmentCode> types = shipmentItem.getTypes();
            Set<ShipmentCode> revokedTypes = types.stream().peek(shipmentCode -> shipmentCode.setCount(0)).collect(Collectors.toSet());
            shipmentItem.setTypes(new LinkedHashSet<>(revokedTypes));
            shipmentItem.setReturnId(storeInItem.getId());
            shipmentItemRepository.save(shipmentItem);
        }
        storeIn.setStatus(StockInStatus.COMPLETED);
        storeInRepository.save(storeIn);
        afterCommitExecutor.execute(() -> rabbitMqProducer.sendStoreIn(new Payload(storeIn.getId(), storeIn.getOrganizationId())));
        return storeIn.getId();
    }

    public StoreInItemDTO update(Long id, StoreInItemDTO dto) {
        StoreIn storeIn = storeInRepository.findById(dto.getStoreInId())
            .orElseThrow(notFound("Возврат не найден"));

        if (StockInStatus.COMPLETED.equals(storeIn.getStatus())) {
            throw badRequest("Процесс уже завершен!").get();
        }
        if (storeInItemRepository.existsByStoreInIdAndProductId(dto.getStoreInId(), dto.getProductId())) {
            throw badRequest("Товар уже существует").get();
        }
        StoreInItem storeInItem = storeInItemRepository.findById(id).orElseThrow(notFound("Товар не найден"));
        storeInItem.setStoreInId(dto.getStoreInId());
        storeInItem.setProductId(dto.getProductId());
        storeInItem.setSerialId(dto.getSerialId());
        storeInItem.setQty(dto.getQty());
        return storeInItemRepository.save(storeInItem).toDto();
    }

    public void delete(Long id) {
        StoreInItem item = storeInItemRepository.findById(id).orElseThrow(notFound("Товар не найден"));
        if (item.getStoreIn() != null && StockInStatus.COMPLETED.equals(item.getStoreIn().getStatus())) {
            throw badRequest("Товар не может быть удален").get();
        }

        storeInMarkRepository.deleteAll(storeInMarkRepository.findAllByItemId(id));
        storeInItemRepository.delete(item);
    }

    public StoreInItemDTO getItem(Long id) {
        StoreInItem item = storeInItemRepository.findById(id).orElseThrow(notFound("Товар не найден"));
        StoreInItemDTO dto = item.toDto();
        Set<StoreInMarkDTO> marks = storeInMarkRepository.findAllByItemId(id).stream()
            .map(StoreInMark::toDTO)
            .collect(Collectors.toSet());
        dto.setCodes(marks);
        return dto;
    }

}
