package uz.uzkassa.silen.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.silen.domain.Stock;
import uz.uzkassa.silen.domain.Warehouse;
import uz.uzkassa.silen.dto.dashboard.DashboardFilter;
import uz.uzkassa.silen.dto.dashboard.OrganizationStatsDTO;
import uz.uzkassa.silen.dto.dashboard.StatsV2DTO;
import uz.uzkassa.silen.dto.filter.StockCreateFilter;
import uz.uzkassa.silen.dto.filter.WarehouseFilter;
import uz.uzkassa.silen.dto.mq.WarehousePayload;
import uz.uzkassa.silen.dto.warehouse.*;
import uz.uzkassa.silen.enumeration.CodeType;
import uz.uzkassa.silen.enumeration.PackageType;
import uz.uzkassa.silen.enumeration.WarehouseOperation;
import uz.uzkassa.silen.exceptions.BadRequestException;
import uz.uzkassa.silen.kafka.KafkaConstants;
import uz.uzkassa.silen.kafka.producer.KafkaProducer;
import uz.uzkassa.silen.repository.pg.StockRepository;
import uz.uzkassa.silen.repository.pg.WarehouseRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service Implementation for managing {@link Warehouse}.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WarehouseService extends BaseService {
    private final WarehouseRepository warehouseRepository;
    private final StockRepository stockRepository;
    private final KafkaProducer<WarehousePayload> kafkaProducer;

    @Transactional
    public void calculate(WarehousePayload payload) {
        Warehouse warehouse = new Warehouse();
        warehouse.setOrganizationId(payload.getOrganizationId());
        warehouse.setProductId(payload.getProductId());
        warehouse.setOperationDate(payload.getOperationDate());
        warehouse.setOperation(payload.getOperation());
        warehouse.setCode(payload.getCode());
        warehouse.setUnit(payload.getPackageType());
        warehouse.setSerialId(payload.getSerialId());
        warehouse.setNote(payload.getNote());
        Optional<Warehouse> warehouseOptional = warehouseRepository.findFirstByOrganizationIdAndProductIdAndDeletedFalseOrderByCreatedDateDesc(payload.getOrganizationId(), payload.getProductId());
        if (warehouseOptional.isPresent()) {
            warehouse.setAmountStart(warehouseOptional.get().getAmountEnd());
            warehouse.setAmount(payload.getAlcoholCount());
            warehouse.setAmountEnd(warehouseOptional.get().getAmountEnd());
        } else {
            warehouse.setAmountStart(BigDecimal.ZERO);
            warehouse.setAmount(payload.getAlcoholCount());
            warehouse.setAmountEnd(payload.getAlcoholCount());
        }
        //do not update warehouse amount and
        if (EnumSet.of(PackageType.PALLET, PackageType.BOTTLE).contains(payload.getPackageType())) {
            warehouseRepository.saveAndFlush(warehouse);
            return;
        }

        if (!payload.isUpdateStock()) {
            warehouseRepository.saveAndFlush(warehouse);
            return;
        }

        if (payload.getOperation().isIncrease()) {
            // increment amount end
            warehouse.setAmountEnd(warehouse.getAmountEnd().add(payload.getAlcoholCount()));
        } else {
            // decrement amount end
            warehouse.setAmountEnd(warehouse.getAmountEnd().subtract(payload.getAlcoholCount()));
        }
        warehouseRepository.saveAndFlush(warehouse);

        updateStock(warehouse);
    }

    public void updateStock(Warehouse warehouse) {
        if (CodeType.MARK == warehouse.getUnit().getCodeType()) return;
        Stock stock = stockRepository.findFirstByOrganizationIdAndProductIdAndDeletedFalse(warehouse.getOrganizationId(), warehouse.getProductId())
                .orElseGet(Stock::new);
        if (stock.getId() == null) {
            stock.setOrganizationId(warehouse.getOrganizationId());
            stock.setProductId(warehouse.getProductId());
        }
        stock.setAmount(warehouse.getAmountEnd());
        stockRepository.saveAndFlush(stock);
    }

    public Page<WarehouseDTO> findAll(WarehouseFilter filter) {
        return warehouseRepository.findAllByFilter(filter).map(Warehouse::toDto);
    }

    public void transfer(TransferDTO transferDTO) {
        final LocalDateTime now = LocalDateTime.now();
        final String organizationId = getCurrentOrganizationId();

        Optional<Stock> stock = stockRepository.findFirstByOrganizationIdAndProductIdAndDeletedFalse(organizationId, transferDTO.getFromProductId());
        if (stock.isEmpty() || stock.get().getAmount().compareTo(transferDTO.getAmount()) < 0) {
            throw new BadRequestException("В складе товар отсуствует");
        } else if (stock.get().getAmount().compareTo(transferDTO.getAmount()) < 0) {
            throw new BadRequestException("Количество товара перевышена. Текущая количество" + transferDTO.getAmount() + " дал.");
        }

        WarehousePayload payload = new WarehousePayload();
        payload.setOrganizationId(organizationId);
        payload.setProductId(transferDTO.getFromProductId());
        payload.setOperation(WarehouseOperation.TransferOut);
        payload.setAlcoholCount(transferDTO.getAmount());
        payload.setOperationDate(now);
        payload.setNote(transferDTO.getNote());
        kafkaProducer.sendMessage(KafkaConstants.WAREHOUSE_TOPIC, payload);

        payload.setProductId(transferDTO.getToProductId());
        payload.setOperation(WarehouseOperation.TransferIn);
        kafkaProducer.sendMessage(KafkaConstants.WAREHOUSE_TOPIC, payload);
    }

    @Transactional
    public void income(WarehousePayload payload) {
        Warehouse warehouse = new Warehouse();
        warehouse.setOrganizationId(getCurrentOrganizationId());
        warehouse.setOperationDate(LocalDateTime.now());
        warehouse.setProductId(payload.getProductId());
        warehouse.setAmount(payload.getAlcoholCount());
        warehouse.setOperation(WarehouseOperation.Income);
        warehouse.setNote(payload.getNote());
        warehouse.setUnit(payload.getUnit());
        warehouseRepository.save(warehouse);

        payload.setOperation(WarehouseOperation.Income);
        payload.setOperationDate(LocalDateTime.now());
        payload.setOrganizationId(getCurrentOrganizationId());
        calculate(payload);
    }

    public Page<StockStatsDTO> stock(WarehouseFilter filter) {
        return stockRepository.findAllByFilter(filter);
    }

    public StockStatsDTO stockStats(WarehouseFilter filter) {
        return stockRepository.stats(filter);
    }

    public List<StatsV2DTO> getWarehouseStats(DashboardFilter filter) {
        List<WarehouseStatsDTO> list = warehouseRepository.getWarehouseStats(filter, false);
        return list.stream()
                .map(w -> new StatsV2DTO(w.getProductType(), w.getProduction(), w.getShipment(), w.getLosses(), w.getBalance(), w.getIncome()))
                .collect(Collectors.toList());
    }

    public List<OrganizationStatsDTO> getWarehouseStatsByFilter(DashboardFilter filter) {
        List<WarehouseStatsDTO> list = warehouseRepository.getWarehouseStats(filter, true);
        Map<String, WarehouseStatsDTO> idNameMap = new HashMap<>();
        list.forEach(t -> idNameMap.put(t.getOrganizationId(), t));

        List<OrganizationStatsDTO> statsDTOList = new ArrayList<>();
        for (String organizationId : idNameMap.keySet()) {
            List<StatsV2DTO> statsList = list.stream().filter(w -> Objects.equals(w.getOrganizationId(), organizationId))
                    .map(w -> new StatsV2DTO(w.getProductType(), w.getProduction(), w.getShipment(), w.getLosses(), w.getBalance(), w.getIncome()))
                    .collect(Collectors.toList());
            statsDTOList.add(new OrganizationStatsDTO(organizationId, idNameMap.get(organizationId).getOrganizationName(), statsList));
        }
        return statsDTOList;
    }

    public WarehouseOperationStatsDTO warehouseStatsByOperation(WarehouseFilter filter) {
        if (WarehouseOperation.ShipmentBuyer == filter.getOperation()) {
            return warehouseRepository.warehouseStatsByOperationShipmentBuyer(filter);
        }
        return warehouseRepository.warehouseStatsByOperationProduction(filter);
    }

    @Transactional
    public Void stockAmountUpdate(StockCreateFilter filter) {
        Stock stock = stockRepository.findFirstByOrganizationIdAndProductIdAndDeletedFalse(filter.getOrganizationId(), filter.getProductId()).orElseThrow(notFound());
        stock.setAmount(filter.getAmount());
        stockRepository.save(stock);

        warehouseRepository.findFirstByOrganizationIdAndProductIdAndDeletedFalseOrderByCreatedDateDesc(filter.getOrganizationId(), filter.getProductId())
                .ifPresent(warehouse -> {
                    warehouse.setAmount(BigDecimal.ZERO);
                    warehouse.setAmountStart(BigDecimal.ZERO);
                    warehouse.setAmountEnd(filter.getAmount());
                    warehouseRepository.save(warehouse);
                });
        return null;
    }

    @Transactional
    public Void stockAmountZero(StockCreateFilter filter) {

        List<Stock> stockList = stockRepository.findAllByOrganizationIdAndDeletedFalse(filter.getOrganizationId());
        stockList.forEach(stockItem -> {
            stockItem.setAmount(filter.getAmount());
            stockRepository.save(stockItem);

            warehouseRepository.findFirstByOrganizationIdAndProductIdAndDeletedFalseOrderByCreatedDateDesc(filter.getOrganizationId(), stockItem.getId())
                    .ifPresent(warehouse -> {
                        warehouse.setAmount(BigDecimal.ZERO);
                        warehouse.setAmountStart(BigDecimal.ZERO);
                        warehouse.setAmountEnd(filter.getAmount());
                        warehouseRepository.save(warehouse);
                    });
        });
        return null;
    }
}
