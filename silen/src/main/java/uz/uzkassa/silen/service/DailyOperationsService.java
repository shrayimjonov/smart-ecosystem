package uz.uzkassa.silen.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.silen.domain.DailyOperations;
import uz.uzkassa.silen.domain.Product;
import uz.uzkassa.silen.dto.*;
import uz.uzkassa.silen.enumeration.DailyOperationTypes;
import uz.uzkassa.silen.repository.pg.ContractRepository;
import uz.uzkassa.silen.repository.pg.DailyOperationsRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DailyOperationsService extends BaseService {
    private final DailyOperationsRepository dailyOperationsRepository;
    private static final String production = "0 0 2 * * *";
    private static final String storeIn = "0 10 2 * * *";
    private static final String dropout = "0 20 2 * * *";
    private static final String shipment = "0 30 2 * * *";
    private final ContractRepository contractRepository;

    public Page<DailyOperationsDTO> findAll(DailyOperationsFilter filter) {
        return dailyOperationsRepository.findAllByFilter(filter).map(dailyOperations -> {
            final Product product = dailyOperations.getProduct();
            final BigDecimal price = Optional.ofNullable(dailyOperations.getProduct().getPrice()).orElse(BigDecimal.ZERO);

            DailyOperationsDTO dailyOperationsDTO = new DailyOperationsDTO();
            dailyOperationsDTO.setType(dailyOperations.getType());
            if (dailyOperations.getType() != null) {
                dailyOperationsDTO.setTypeName(dailyOperations.getType().getNameRu());
            }

            dailyOperationsDTO.setStartAmount(dailyOperations.getAmountStart());
            dailyOperationsDTO.setStartAmountPrice(dailyOperations.getAmountStart().multiply(price));

            dailyOperationsDTO.setAmount(dailyOperations.getAmount());
            dailyOperationsDTO.setAmountPrice(dailyOperations.getAmount().multiply(price));

            dailyOperationsDTO.setEndAmount(dailyOperations.getAmountEnd());
            dailyOperationsDTO.setEndAmountPrice(dailyOperations.getAmountEnd().multiply(price));

            dailyOperationsDTO.setProductId(dailyOperations.getProductId());
            dailyOperationsDTO.setProductName(product.getVisibleName());
            dailyOperationsDTO.setBarcode(product.getBarcode());
            dailyOperationsDTO.setPrice(price);
            dailyOperationsDTO.setDate(dailyOperations.getCreatedDate());
            return dailyOperationsDTO;
        });
    }

    public DailyOperationsStats getStats(DailyOperationsFilter filter) {
        return dailyOperationsRepository.getStats(filter);
    }

    public List<DailyOperationsMaterialStats> materialReport(DailyOperationsFilter filter) {
        final List<DailyOperationsMaterialStats> dailyOperationsStats = dailyOperationsRepository.materialReport(filter);


        // change filter date
        if (filter.getFrom() != null) {
            filter.setTo(filter.getFrom().withHour(23).withMinute(59).withSecond(59));
        }
        filter.setSize(!dailyOperationsStats.isEmpty() ? dailyOperationsStats.size() : 50);
        filter.setType(DailyOperationTypes.Shipment);
        final Map<String, BigDecimal> allByFilter = dailyOperationsRepository.findAllByFilter(filter)
                .stream().collect(Collectors.toMap(DailyOperations::getProductId, dailyOperations -> Optional.of(dailyOperations.getAmountEnd()).orElse(BigDecimal.ZERO)));

        List<DailyOperationsMaterialStats> list = new ArrayList<>();
        for (DailyOperationsMaterialStats dailyOperationsMaterialStats : dailyOperationsStats) {
            final String productId = dailyOperationsMaterialStats.getProductId();
            final BigDecimal price = Optional.ofNullable(dailyOperationsMaterialStats.getPrice()).orElse(BigDecimal.ZERO);
            final BigDecimal amountEnd = allByFilter.getOrDefault(productId, BigDecimal.ZERO);

            dailyOperationsMaterialStats.setStartAmount(amountEnd);
            dailyOperationsMaterialStats.setStartPrice(price.multiply(amountEnd));

            dailyOperationsMaterialStats.setEndAmount(amountEnd.add(dailyOperationsMaterialStats.getProductionQty())
                    .add(dailyOperationsMaterialStats.getReturnedQty())
                    .subtract(dailyOperationsMaterialStats.getDropoutQty())
                    .subtract(dailyOperationsMaterialStats.getShippedQty()));
            dailyOperationsMaterialStats.setEndPrice(price.multiply(dailyOperationsMaterialStats.getEndAmount()));
            list.add(dailyOperationsMaterialStats);
        }

        return list;
    }

//    @Scheduled(cron = production)
    @Transactional
    public void createFromAggregation() {
        final LocalDate tomorrow = LocalDate.now().minusDays(1);
        final LocalDateTime from = tomorrow.atTime(LocalTime.MIN);
        final LocalDateTime to = tomorrow.atTime(LocalTime.MAX);

        List<AggregationSum> dropoutQuantity = dailyOperationsRepository.getProductionQuantity(from, to);

        for (AggregationSum item : dropoutQuantity) {
            save(item, DailyOperationTypes.Production);
        }
    }

//    @Scheduled(cron = storeIn)
    @Transactional
    public void createFromStoreIn() {
        final LocalDate tomorrow = LocalDate.now().minusDays(1);
        final LocalDateTime from = tomorrow.atTime(LocalTime.MIN);
        final LocalDateTime to = tomorrow.atTime(LocalTime.MAX);

        List<AggregationSum> storeInQuantity = dailyOperationsRepository.getStoreInQuantity(from, to);
        for (AggregationSum item : storeInQuantity) {
            save(item, DailyOperationTypes.Returned);

        }
    }

//    @Scheduled(cron = dropout)
    @Transactional
    public void createFromDropout() {
        final LocalDate tomorrow = LocalDate.now().minusDays(1);
        final LocalDateTime from = tomorrow.atTime(LocalTime.MIN);
        final LocalDateTime to = tomorrow.atTime(LocalTime.MAX);

        List<AggregationSum> dropoutQuantity = dailyOperationsRepository.getDropoutQuantity(from, to);

        for (AggregationSum item : dropoutQuantity) {
            save(item, DailyOperationTypes.Dropout);
        }
    }

//    @Scheduled(cron = shipment)
    @Transactional
    public void createFromShipment() {
        final LocalDate tomorrow = LocalDate.now().minusDays(1);
        final LocalDateTime from = tomorrow.atTime(LocalTime.MIN);
        final LocalDateTime to = tomorrow.atTime(LocalTime.MAX);

        List<AggregationSum> shippedQuantity = dailyOperationsRepository.getShippedQuantity(from, to);

        for (AggregationSum item : shippedQuantity) {
            save(item, DailyOperationTypes.Shipment);
        }
    }

    private void save(final AggregationSum item, final DailyOperationTypes dailyOperationTypes) {
        final BigDecimal quantity = item.getQuantity();
        final String productId = item.getProductId();
        final String serialId = item.getSerialId();
        final String organizationId = item.getOrganizationId();

        DailyOperations dailyOperations = new DailyOperations();
        Optional<DailyOperations> dailyOperationsOptional = serialId != null ?
                dailyOperationsRepository.findFirstByOrganizationIdAndProductIdAndSerialIdOrderByCreatedDateDesc(organizationId, productId, serialId)
                : dailyOperationsRepository.findFirstByOrganizationIdAndProductIdOrderByCreatedDateDesc(organizationId, productId);
        if (dailyOperationsOptional.isPresent()) {
            dailyOperations.setAmountStart(dailyOperationsOptional.get().getAmountEnd());
            dailyOperations.setAmount(quantity);
            dailyOperations.setAmountEnd(dailyOperationsOptional.get().getAmountEnd().add(quantity));
        } else {
            dailyOperations.setAmountStart(BigDecimal.ZERO);
            dailyOperations.setAmount(quantity);
            dailyOperations.setAmountEnd(quantity);
        }
        dailyOperations.setProductId(productId);
        dailyOperations.setOrganizationId(organizationId);
        dailyOperations.setType(dailyOperationTypes);
        dailyOperations.setSerialId(serialId);
        dailyOperations.setOperationDate(LocalDateTime.now().minusDays(1));
        dailyOperationsRepository.saveAndFlush(dailyOperations);
    }


    public void createFromShipmentCheck(DailyOperationsFilter filter) {
        List<AggregationSum> result = new ArrayList<>();
        switch (filter.getType()) {
            case Shipment:
                result.addAll(dailyOperationsRepository.getShippedQuantity(filter.getFrom(), filter.getTo()));
                break;
            case Dropout:
                result.addAll(dailyOperationsRepository.getDropoutQuantity(filter.getFrom(), filter.getTo()));
                break;
            case Returned:
                result.addAll(dailyOperationsRepository.getStoreInQuantity(filter.getFrom(), filter.getTo()));
                break;
            case Production:
                result.addAll(dailyOperationsRepository.getProductionQuantity(filter.getFrom(), filter.getTo()));
        }

        for (AggregationSum item : result) {
            save(item, filter.getType());
        }
    }
}
