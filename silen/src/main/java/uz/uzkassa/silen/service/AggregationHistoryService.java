package uz.uzkassa.silen.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.silen.domain.Aggregation;
import uz.uzkassa.silen.domain.AggregationHistory;
import uz.uzkassa.silen.domain.Organization;
import uz.uzkassa.silen.domain.mongo.Block;
import uz.uzkassa.silen.dto.AggregationHistoryDTO;
import uz.uzkassa.silen.dto.MaterialReportDTO;
import uz.uzkassa.silen.dto.MaterialReportFilter;
import uz.uzkassa.silen.dto.filter.AggregationFilter;
import uz.uzkassa.silen.dto.marking.AggregationStatsDTO;
import uz.uzkassa.silen.dto.mq.WarehousePayload;
import uz.uzkassa.silen.enumeration.AggregationStatus;
import uz.uzkassa.silen.enumeration.CodeType;
import uz.uzkassa.silen.enumeration.PackageType;
import uz.uzkassa.silen.enumeration.WarehouseOperation;
import uz.uzkassa.silen.integration.AsilBelgisiClient;
import uz.uzkassa.silen.integration.asilbelgi.dto.CisInfo;
import uz.uzkassa.silen.integration.asilbelgi.dto.CisInfoResponse;
import uz.uzkassa.silen.kafka.KafkaConstants;
import uz.uzkassa.silen.kafka.producer.KafkaProducer;
import uz.uzkassa.silen.repository.mongo.BlockRepository;
import uz.uzkassa.silen.repository.mongo.MarkRepository;
import uz.uzkassa.silen.repository.pg.AggregationHistoryRepository;
import uz.uzkassa.silen.repository.pg.AggregationRepository;
import uz.uzkassa.silen.repository.redis.RedisAggregationRepository;
import uz.uzkassa.silen.security.SecurityUtils;
import uz.uzkassa.silen.utils.Utils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AggregationHistoryService extends BaseService {
    AsilBelgisiClient asilBelgisiClient;
    MarkRepository markRepository;
    AggregationRepository aggregationRepository;
    AggregationHistoryRepository aggregationHistoryRepository;
    KafkaProducer<WarehousePayload> kafkaProducer;
    private final BlockRepository blockRepository;
    private final RedisAggregationRepository redisAggregationRepository;

    public void create(WarehousePayload payload) {
//        printInfo("CREATE HISTORY: " + payload.getCode() + "\n PAYLOAD: " + payload);
        if (EnumSet.of(PackageType.BOX, PackageType.PALLET).contains(payload.getPackageType())) {
            final String code18 = Utils.clearAggregationCode(payload.getCode());
            final String code20 = Utils.clearPrintCode(payload.getCode());
            aggregationRepository.findFirstByCodeOrCode(code18, code20)
                    .ifPresent(aggregation -> {
                        AggregationHistory history = new AggregationHistory();
                        history.setStatus(aggregation.getStatus());
                        history.setUnit(aggregation.getUnit());
                        history.setProductId(aggregation.getProductId());
                        history.setQuantity(aggregation.getQuantity());
                        history.setLogin(aggregation.getLogin());
                        history.setPrintCode(aggregation.getPrintCode());
                        history.setCode(aggregation.getCode());
                        history.setTuronAggregationId(aggregation.getReportId());
                        history.setOrganizationId(aggregation.getOrganizationId());
                        history.setSerialId(aggregation.getSerialId());
                        history.setShipmentId(aggregation.getShipmentId());
                        history.setAggregationId(aggregation.getId());
                        aggregationHistoryRepository.save(history);
                    });
        } else if (PackageType.BLOCK.equals(payload.getPackageType())) {
            blockRepository.findFirstByCode(payload.getCode())
                    .ifPresent(block -> {
                        AggregationHistory history = new AggregationHistory();
                        history.setStatus(block.getStatus());
                        history.setUnit(PackageType.BLOCK.getCode());
                        history.setProductId(block.getProductId());
                        history.setQuantity(block.getQuantity());
                        history.setLogin(block.getLogin());
                        history.setPrintCode(block.getPrintCode());
                        history.setCode(block.getCode());
                        history.setTuronAggregationId(block.getReportId());
                        history.setOrganizationId(block.getOrganizationId());
//                        history.setSerialId(block.getSerialId());
//                        history.setShipmentId(block.getShipmentId());
                        history.setAggregationId(block.getId());
                        aggregationHistoryRepository.save(history);
                    });
        }
    }

    public Page<AggregationHistoryDTO> findAll(AggregationFilter filter) {
        filterSearchByCode(filter);
        return aggregationHistoryRepository.findAllByFilter(filter).map(aggregationHistory -> aggregationHistory.toDto(isAgency()));
    }

    public AggregationStatsDTO getStats(AggregationFilter filter) {
        filterSearchByCode(filter);
        return aggregationHistoryRepository.getStats(filter);
    }

    public CisInfoResponse[] checkMarkFromTrueApi(Set<String> printCodes) {
        if (SecurityUtils.getCurrentOrganizationId() == null) {
            final Set<String> aggregationCodes = new HashSet<>();
            final Set<String> blockCodes = new HashSet<>();
            for (String printCode : printCodes) {
                String code = Utils.clearPrintCode(printCode);
                if (Utils.isAggregation(printCode)) {
                    aggregationCodes.add(code);
                } else {
                    blockCodes.add(code);
                }
            }
            Map<String, Set<String>> collect = new HashMap<>();
            if (CollectionUtils.isNotEmpty(aggregationCodes)) {
                aggregationRepository.findAllByCodeIn(aggregationCodes)
                        .forEach(aggregation -> {
                            if (!collect.containsKey(aggregation.getOrganizationId())) {
                                collect.put(aggregation.getOrganizationId(), new HashSet<>());
                            }
                            collect.get(aggregation.getOrganizationId())
                                    .add(aggregation.getCode());
                        });
            }
            if (CollectionUtils.isNotEmpty(blockCodes)) {
                markRepository.findAllByCodeIn(blockCodes)
                        .forEach(aggregation -> {
                            if (!collect.containsKey(aggregation.getOrganizationId())) {
                                collect.put(aggregation.getOrganizationId(), new HashSet<>());
                            }
                            collect.get(aggregation.getOrganizationId())
                                    .add(aggregation.getCode());
                        });
            }
            List<CisInfoResponse> cisInfoResponses = new ArrayList<>();
            for (Map.Entry<String, Set<String>> entry : collect.entrySet()) {
                Optional<Organization> byId = organizationRepository.findById(entry.getKey());
                if (byId.isPresent()) {
                    Organization organization = byId.get();
                    try {
                        final String token = getCurrentOrganizationAsilBelgiToken(organization.getTin());
                        CisInfoResponse[] cisInfoResponses1 = asilBelgisiClient.cisesInfo(token, entry.getValue());
                        cisInfoResponses.addAll(Arrays.asList(cisInfoResponses1));
                    } catch (Exception e) {
                        CisInfoResponse cisInfoResponse = new CisInfoResponse();
                        CisInfo cisInfo = new CisInfo();
                        for (String code : entry.getValue()) {
                            cisInfo.setCis(code);
                            cisInfoResponse.setErrorMessage(e.getMessage());
                            cisInfoResponse.setErrorCode("404");
                            cisInfoResponse.setCisInfo(cisInfo);
                            cisInfoResponses.add(cisInfoResponse);
                        }
                    }
                }
            }
            return cisInfoResponses.toArray(new CisInfoResponse[0]);
        } else {
            final Set<String> codes = printCodes.stream().map(Utils::clearPrintCode).collect(Collectors.toSet());
            String currentOrganizationAsilBelgiToken = getCurrentOrganizationAsilBelgiToken();
            return asilBelgisiClient.cisesInfo(currentOrganizationAsilBelgiToken, codes);
        }
    }


    @Transactional
    public HashMap<CodeType, List<String>> utilize(Set<String> printCodes) {
        HashMap<CodeType, List<String>> resultMap = new HashMap<>();
        resultMap.put(CodeType.AGGREGATION, new LinkedList<>());
        resultMap.put(CodeType.BLOCK, new LinkedList<>());

        for (String printCode : printCodes) {
            if (Utils.isAggregation(printCode)) {
                resultMap.get(CodeType.AGGREGATION).addAll(Utils.clearAggregationPrintCode(printCode));
            } else {
                resultMap.get(CodeType.BLOCK).add(Utils.clearPrintCode(printCode));
            }
        }
        final String utilizedMessage = localize("utilized.message", SecurityUtils.getCurrentUserLogin(), String.valueOf(LocalDateTime.now()));
        if (CollectionUtils.isNotEmpty(resultMap.get(CodeType.AGGREGATION))) {
            List<String> utilizedCodes = new ArrayList<>();
            List<Aggregation> aggregations = aggregationRepository.findAllByCodeIn(resultMap.get(CodeType.AGGREGATION));
            for (Aggregation aggregation : aggregations) {
                utilizedCodes.add(aggregation.getCode());
                if (PackageType.PALLET.equals(aggregation.getPackageType())) {
                    aggregationRepository.unsetParentCode(aggregation.getCode());
                }
                markRepository.findAllByParentCodeIn(Utils.clearAggregationPrintCode(aggregation.getPrintCode())).forEach(item -> {
                    item.setParentCode(null);
                    if (!item.isBlock() && AggregationStatus.DROPOUT != item.getStatus()) {
                        item.setUsed(false);
                        item.setStatus(AggregationStatus.DRAFT);
                    }
                    markRepository.save(item);
                });

                // create aggregation history
                aggregation.setStatus(AggregationStatus.UTILIZED);
                aggregation.setErrorMessage(utilizedMessage);
                aggregationRepository.saveAndFlush(aggregation);

                WarehousePayload warehousePayload = new WarehousePayload();
                warehousePayload.setOperation(WarehouseOperation.Utilized);
                warehousePayload.setNote("Утилизировано");
                warehousePayload.setUnit(PackageType.fromCode(aggregation.getUnit()));
                warehousePayload.setOperationDate(LocalDateTime.now());
                warehousePayload.setOrganizationId(aggregation.getOrganizationId());
                warehousePayload.setProductId(aggregation.getProductId());
                warehousePayload.setCode(aggregation.getCode());
                warehousePayload.setAlcoholCount(BigDecimal.valueOf(Optional.ofNullable(aggregation.getQuantity()).orElse(0)));
                kafkaProducer.sendMessage(KafkaConstants.WAREHOUSE_TOPIC, warehousePayload);
                redisAggregationRepository.findFirstByCode(aggregation.getCode()).ifPresent(redisAggregationRepository::delete);
            }
            resultMap.get(CodeType.AGGREGATION).clear();
            resultMap.get(CodeType.AGGREGATION).addAll(utilizedCodes);
        }
        if (CollectionUtils.isNotEmpty(resultMap.get(CodeType.BLOCK))) {
            List<Block> allByCodeIn = blockRepository.findAllByCodeIn(resultMap.get(CodeType.BLOCK));
            for (Block block : allByCodeIn) {
                if (block.isBlock()) {
                    markRepository.findAllByParentCode(block.getCode()).forEach(item -> {
                        item.setParentCode(null);
                        if (AggregationStatus.DROPOUT != item.getStatus()) {
                            item.setUsed(false);
                            item.setStatus(AggregationStatus.DRAFT);
                            markRepository.save(item);
                        }
                    });

                    block.setStatus(AggregationStatus.UTILIZED);
                    block.setErrorMessage(utilizedMessage);
                    blockRepository.save(block);

                    WarehousePayload warehousePayload = new WarehousePayload();
                    warehousePayload.setOperation(WarehouseOperation.Utilized);
                    warehousePayload.setNote("Утилизировано");
                    warehousePayload.setUnit(PackageType.BLOCK);
                    warehousePayload.setOperationDate(LocalDateTime.now());
                    warehousePayload.setOrganizationId(block.getOrganizationId());
                    warehousePayload.setProductId(block.getProductId());
                    warehousePayload.setCode(block.getCode());
                    warehousePayload.setAlcoholCount(BigDecimal.valueOf(block.getQuantity()));
                    kafkaProducer.sendMessage(KafkaConstants.WAREHOUSE_TOPIC, warehousePayload);

                }
                redisAggregationRepository.findFirstByCode(block.getCode()).ifPresent(redisAggregationRepository::delete);
            }
        }
        return resultMap;
    }

    private void filterSearchByCode(AggregationFilter filter) {
        if (filter.checkSearchIsEmpty()) {
            final String code = Utils.clearPrintCode(filter.getSearch());
            if (Utils.isMark(filter.getSearch()) || Utils.alcoholMatcherWithoutCX(filter.getSearch())) {
                markRepository.findFirstByCode(code).ifPresent(value -> {
                    if (value.getParentCode() != null) {
                        filter.setSearch(value.getParentCode());
                    } else {
                        filter.setSearch(value.getCode());
                    }

                });
            } else {
                filter.setSearch(code);
            }
        }
    }

    public Page<MaterialReportDTO> materialReport(MaterialReportFilter filter) {
        Page<MaterialReportDTO> page = aggregationHistoryRepository.materialReport(filter);

        if (filter.getTo() != null) {
            filter.setTo(filter.getFrom());
            filter.setFrom(null);
        }

        Page<MaterialReportDTO> toToday = aggregationHistoryRepository.materialReport(filter);
        return page.map(dto -> {
            toToday.stream()
                    .filter(materialReportDTO -> materialReportDTO.getProductId().equals(dto.getProductId()))
                    .findFirst()
                    .ifPresent(materialReportDTO -> {
                        dto.setBalanceStartBottle(materialReportDTO.getBalanceEndBottle());
                        dto.setBalanceStartBottleSum(materialReportDTO.getBalanceEndBottle().multiply(dto.getPrice()));
                        dto.setBalanceStartBox(materialReportDTO.getBalanceEndBox());
                        dto.setBalanceStartPallet(materialReportDTO.getBalanceEndPallet());
                    });
            return dto;
        });
    }
}
