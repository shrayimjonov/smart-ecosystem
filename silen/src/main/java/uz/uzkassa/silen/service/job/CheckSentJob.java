package uz.uzkassa.silen.service.job;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import uz.uzkassa.silen.domain.Aggregation;
import uz.uzkassa.silen.domain.Organization;
import uz.uzkassa.silen.domain.mongo.Block;
import uz.uzkassa.silen.dto.mq.WarehousePayload;
import uz.uzkassa.silen.enumeration.AggregationStatus;
import uz.uzkassa.silen.enumeration.PackageType;
import uz.uzkassa.silen.enumeration.WarehouseOperation;
import uz.uzkassa.silen.integration.AsilBelgisiClient;
import uz.uzkassa.silen.integration.asilbelgi.dto.CisInfoResponse;
import uz.uzkassa.silen.kafka.KafkaConstants;
import uz.uzkassa.silen.kafka.producer.KafkaProducer;
import uz.uzkassa.silen.repository.mongo.BlockRepository;
import uz.uzkassa.silen.repository.pg.AggregationRepository;
import uz.uzkassa.silen.service.BaseService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CheckSentJob extends BaseService {
    AggregationRepository aggregationRepository;
    KafkaProducer<WarehousePayload> kafkaProducer;
    private final BlockRepository blockRepository;
    final static String cronRegex = "0 0/10 * * * ?";
    final static AggregationStatus checkByStatus = AggregationStatus.SENT;
    final static int minusOfStartTime = 5;
    final PageRequest pageRequest = PageRequest.of(0, AsilBelgisiClient.cisInfoLimit);

    @Scheduled(cron = cronRegex)
    public void startCheckAggregations() {
        final LocalDateTime to = LocalDateTime.now().minusMinutes(minusOfStartTime);
        PageRequest pageRequest = PageRequest.of(0, AsilBelgisiClient.cisInfoLimit);
        boolean hasNext;
        do {
            final Page<Aggregation> page = aggregationRepository.findAllByStatusAndLastModifiedDateIsBeforeOrderByOrganizationId(checkByStatus, to, pageRequest);
            printInfo("TOTAL PAGES: " + page.getTotalPages() + "\nPAGE NUMBER: " + page.getNumber() + "\nCONTENT: " + page.getContent().size());
            final Map<String, List<Aggregation>> map = page.getContent().stream().collect(Collectors.groupingBy(Aggregation::getOrganizationId));
            if (MapUtils.isEmpty(map)) return;
            for (String organizationId : map.keySet()) {
                final Organization organization = organizationRepository.findById(organizationId).orElse(null);
                if (organization == null) {
                    continue;
                }
                final String trueToken = getCurrentOrganizationAsilBelgiTokenOrNul(organization.getTin());
                if (trueToken == null) {
                    log.info("ORGANIZATION {} HAS NO TRUE TOKEN", organization.getName());
                    continue;
                }
                final Set<String> codes = map.get(organizationId).stream().map(Aggregation::getCode).collect(Collectors.toSet());
                final List<Aggregation> list = map.get(organizationId);
                String time = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                try {
                    final Map<String, CisInfoResponse> cisInfoMap = checkFromAsilBelgisi(codes, trueToken);
                    for (Aggregation aggregation : list) {
                        final String code = aggregation.getCode();
                        String errorMessage = checkResponse(code, cisInfoMap, time);
                        if (errorMessage != null) {
                            aggregation.setErrorMessage(errorMessage);
                            aggregation.setStatus(AggregationStatus.ERROR);
                        } else {
                            aggregation.setErrorMessage(localize("checked.success", time, cisInfoMap.get(code).getCisInfo().getStatusName()));
                            aggregation.setStatus(AggregationStatus.CREATED);
                        }
                        aggregationRepository.save(aggregation);
                        if (AggregationStatus.CREATED == aggregation.getStatus()) {
                            // add aggregation to warehouse production this includes aggregation history also
                            final WarehousePayload payload = new WarehousePayload();
                            payload.setOrganizationId(aggregation.getOrganizationId());
                            payload.setOperationDate(LocalDateTime.now());
                            payload.setOperation(WarehouseOperation.Production);
                            payload.setNote("Произведено " + aggregation.getPrintCode());
                            payload.setUnit(PackageType.fromCode(aggregation.getUnit()));
                            payload.setAlcoholCount(BigDecimal.valueOf(aggregation.getQuantity().longValue()));
                            payload.setProductId(aggregation.getProductId());
                            payload.setSerialId(aggregation.getSerialId());
                            payload.setCode(aggregation.getCode());
                            payload.setAggregationStatus(aggregation.getStatus());
                            kafkaProducer.sendMessage(KafkaConstants.WAREHOUSE_TOPIC, payload);
//                            printInfo(payload.toString());
                        }
                    }
                } catch (Exception e) {
                    log.error("Error occurred time checking from AsilBelgisi", e);
                    list.forEach(aggregation -> {
                        aggregation.setErrorMessage(localize("checked.error", time, e.getMessage()));
//                        aggregation.setStatus(AggregationStatus.ERROR);
                        aggregationRepository.save(aggregation);
                    });
                }
            }

            hasNext = page.hasNext();
        } while (hasNext);
    }

    @Scheduled(cron = cronRegex)
    public void startCheckBlocks() {
        final LocalDateTime to = LocalDateTime.now().minusMinutes(minusOfStartTime);

        boolean hasNext;
        do {
            final Page<Block> page = blockRepository.findAllByStatusAndUnitAndLastModifiedDateIsBeforeOrderByOrganizationId(checkByStatus, PackageType.BLOCK, to, pageRequest);
            printInfo("TOTAL PAGES: " + page.getTotalPages() + "\nPAGE NUMBER: " + page.getNumber() + "\nCONTENT: " + page.getContent().size());
            final Map<String, List<Block>> map = page.getContent().stream().collect(Collectors.groupingBy(Block::getOrganizationId));
            if (MapUtils.isEmpty(map)) return;
            for (String organizationId : map.keySet()) {
                final Organization organization = organizationRepository.findById(organizationId).orElse(null);
                if (organization == null) {
                    continue;
                }
                final String trueToken = getCurrentOrganizationAsilBelgiTokenOrNul(organization.getTin());
                if (trueToken == null) {
                    log.info("ORGANIZATION {} HAS NO TRUE TOKEN", organization.getName());
                    continue;
                }
                final List<Block> blockList = map.get(organizationId);
                final Set<String> codes = blockList.stream().map(Block::getCode).collect(Collectors.toSet());
                printInfo(organization.getName() + "\nCodes: " + codes.size());
                String time = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                try {
                    final Map<String, CisInfoResponse> cisInfoMap = checkFromAsilBelgisi(codes, trueToken);
                    for (Block block : blockList) {
                        String code = block.getCode();
                        String errorMessage = checkResponse(code, cisInfoMap, time);
                        if (errorMessage != null) {
                            block.setErrorMessage(errorMessage);
                            block.setStatus(AggregationStatus.ERROR);
                        } else {
                            block.setErrorMessage(localize("checked.success", time, cisInfoMap.get(code).getCisInfo().getStatusName()));
                            block.setStatus(AggregationStatus.CREATED);
                        }
                        blockRepository.save(block);
                        if (AggregationStatus.CREATED == block.getStatus()) {
                            // add aggregation to warehouse production this includes aggregation history also
                            final WarehousePayload payload = new WarehousePayload();
                            payload.setOrganizationId(block.getOrganizationId());
                            payload.setOperationDate(block.getAggregationDate());
                            payload.setOperation(WarehouseOperation.Production);
                            payload.setNote("Произведено " + block.getPrintCode());
                            payload.setUnit(block.getUnit());
                            payload.setAlcoholCount(BigDecimal.valueOf(block.getQuantity()));
                            payload.setProductId(block.getProductId());
                            payload.setSerialId(block.getSerialId());
                            payload.setCode(block.getCode());
                            payload.setAggregationStatus(block.getStatus());
                            kafkaProducer.sendMessage(KafkaConstants.WAREHOUSE_TOPIC, payload);
//                            printInfo(payload.toString());
                        }
                    }
                } catch (Exception e) {
                    log.error("Error occurred time checking from AsilBelgisi", e);
                    blockList.forEach(block -> {
                        block.setErrorMessage(localize("checked.error", time, e.getMessage()));
//                        block.setStatus(AggregationStatus.ERROR);
                        blockRepository.save(block);
                    });
                }
            }
            hasNext = page.hasNext();
        } while (hasNext);

    }
}
