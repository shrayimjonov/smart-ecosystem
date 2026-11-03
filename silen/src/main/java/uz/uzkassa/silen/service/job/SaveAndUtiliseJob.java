package uz.uzkassa.silen.service.job;

import com.google.common.collect.Lists;
import io.micrometer.common.util.StringUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import uz.uzkassa.silen.domain.Aggregation;
import uz.uzkassa.silen.domain.Organization;
import uz.uzkassa.silen.domain.mongo.Block;
import uz.uzkassa.silen.domain.mongo.Mark;
import uz.uzkassa.silen.domain.redis.RedisAggregation;
import uz.uzkassa.silen.dto.marking.CheckingHashInfo;
import uz.uzkassa.silen.enumeration.AggregationMode;
import uz.uzkassa.silen.enumeration.AggregationStatus;
import uz.uzkassa.silen.enumeration.MarkStatus;
import uz.uzkassa.silen.enumeration.PackageType;
import uz.uzkassa.silen.integration.AsilBelgisiClient;
import uz.uzkassa.silen.integration.asilbelgi.dto.CisInfo;
import uz.uzkassa.silen.integration.asilbelgi.dto.CisInfoResponse;
import uz.uzkassa.silen.integration.turon.UtilisationResponse;
import uz.uzkassa.silen.repository.mongo.BlockRepository;
import uz.uzkassa.silen.repository.mongo.MarkRepository;
import uz.uzkassa.silen.repository.pg.AggregationRepository;
import uz.uzkassa.silen.repository.redis.RedisAggregationRepository;
import uz.uzkassa.silen.service.BaseService;
import uz.uzkassa.silen.service.UtilisationService;
import uz.uzkassa.silen.utils.Utils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SaveAndUtiliseJob extends BaseService {

    RedisAggregationRepository redisAggregationRepository;
    MarkRepository markRepository;
    AggregationRepository aggregationRepository;
    UtilisationService utilisationService;
    final static String cronRegex = "0 0/10 * * * ?";
    BlockRepository blockRepository;
    final PageRequest pageRequest = PageRequest.of(0, 1000);

    //@Transactional
    @Scheduled(cron = cronRegex)
    public void saveMarks2() {
        boolean hasNext = false;
        do {
            final Page<RedisAggregation> all = redisAggregationRepository.findAllByAggregationMode(AggregationMode.Massive, pageRequest);
            final Map<String, List<RedisAggregation>> byOrganizationMap = all.stream()
                    .filter(Objects::nonNull)
                    .collect(Collectors.groupingBy(RedisAggregation::getOrganizationId));

            if (MapUtils.isEmpty(byOrganizationMap)) {
                log.error("\n========================================\nbyOrganizationMap is empty\n========================================\n");
                continue;
            }
            for (String organizationId : byOrganizationMap.keySet()) {
                if (organizationId == null) {
                    log.error("Organization not found");
                    continue;
                }
                final Organization organization = organizationRepository.findById(organizationId).orElse(null);
                if (organization == null) {
                    log.error("Organization not found {}", organizationId);
                    continue;
                }

                final List<RedisAggregation> aggregations = byOrganizationMap.get(organizationId);
                if (!aggregations.isEmpty()) {
                    aggregate(organization, aggregations);
                }
            }
            hasNext = all.hasNext();
        } while (hasNext);
    }

    private void aggregate(final Organization organization, final List<RedisAggregation> aggregations) {
        StringBuilder stringBuilder = new StringBuilder("START UTILISING\n");
        stringBuilder.append("ORGANIZATION: ").append(organization.getName()).append("\n");
        stringBuilder.append("AGGREGATIONS: ").append(aggregations.size()).append("\n");
        log.info("\n========================================\n{}\n========================================\n", stringBuilder);

        final Map<CheckingHashInfo, Set<String>> groupUtiliseCodes = new HashMap<>();
        final Map<CheckingHashInfo, Set<String>> parentAggregationMap = new HashMap<>();
        final Map<String, String> childParentCodes = new HashMap<>();
        final Map<String, String> utilizeCodes = new HashMap<>();

        for (RedisAggregation redisAggregation : aggregations) {
            final String parentCode = redisAggregation.getCode();
            List<String> aggrCodes = new ArrayList<>();
            List<String> markCodes = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(redisAggregation.getMarks())) {
                for (String printCode : redisAggregation.getMarks()) {
                    final String cleanCode = Utils.clearPrintCode(printCode);

                    if (Utils.isAggregation(printCode)) {
                        aggrCodes.add(cleanCode);
                    } else {
                        final Mark mark = markRepository.findFirstByCode(cleanCode).orElseGet(() -> {
                            Mark tmp = new Mark();
                            tmp.setPrintCode(Utils.fillWithUniCode(printCode));
                            tmp.setCode(cleanCode);
                            tmp.setProductId(redisAggregation.getProductId());
                            tmp.setCreatedDate(LocalDateTime.now());
                            tmp.setOrganizationId(redisAggregation.getOrganizationId());
                            return tmp;
                        });
                        mark.setParentCode(parentCode);
                        mark.setUsed(true);
                        mark.setStatus(AggregationStatus.APPLIED);
                        markRepository.save(mark);
                        markCodes.add(mark.getPrintCode());
                        utilizeCodes.put(cleanCode, mark.getPrintCode());
                    }
                    childParentCodes.put(cleanCode, parentCode);
                }
            }
            if (PackageType.BLOCK.equals(redisAggregation.getPackageType())) {
                Optional<Block> firstByCode = blockRepository.findFirstByCode(parentCode);
                if (firstByCode.isPresent() && firstByCode.get().isBlock()) {
                    markCodes.add(firstByCode.get().getPrintCode());
                    utilizeCodes.put(firstByCode.get().getCode(), firstByCode.get().getPrintCode());
                }
            }

            if (!aggrCodes.isEmpty()) {
                aggrCodes.add(parentCode);
                List<Aggregation> allByCodeIn = aggregationRepository.findAllByCodeIn(aggrCodes)
                        .stream().peek(aggregation -> {
                            if (parentCode.equals(aggregation.getCode())) {
                                aggregation.setAggregationDate(LocalDateTime.now());
                                aggregation.setStatus(AggregationStatus.APPLIED);
                            } else {
                                aggregation.setParentCode(parentCode);
                            }
                        }).toList();
                if (CollectionUtils.isNotEmpty(allByCodeIn)) {
                    aggregationRepository.saveAll(allByCodeIn);
                }
                continue;
            }

            if (CollectionUtils.isNotEmpty(markCodes)) {
                final CheckingHashInfo checkingHashInfo = CheckingHashInfo.builder().productId(redisAggregation.getProductId()).serialNumber(redisAggregation.getSerialNumber()).expirationDate(redisAggregation.getExpirationDate()).productionDate(redisAggregation.getProductionDate()).build();

                if (!groupUtiliseCodes.containsKey(checkingHashInfo)) {
                    groupUtiliseCodes.put(checkingHashInfo, new HashSet<>());
                }
                groupUtiliseCodes.get(checkingHashInfo).addAll(markCodes);

                if (!parentAggregationMap.containsKey(checkingHashInfo)) {
                    parentAggregationMap.put(checkingHashInfo, new HashSet<>());
                }
                parentAggregationMap.get(checkingHashInfo).add(parentCode);
            }
//            printInfo("PARENT CODE: " + parentCode);
        }

        final Map<String, CisInfo> turonCodes = getTuronCodes(utilizeCodes, organization.getTin());

        for (CheckingHashInfo key : groupUtiliseCodes.keySet()) {
            Set<String> groupCodes = groupUtiliseCodes.get(key);
            final Set<String> utilisationCodes = new HashSet<>(groupCodes);
//            log.info("Utilisation codes size {}", utilisationCodes.size());
            if (utilisationCodes.isEmpty()) {
                log.error("utilisationCodes is empty {}", key);
                continue;
            }
            final Set<String> notFound = new HashSet<>();
            final Set<String> errorCodes = new HashSet<>();
            for (String utilisationCode : groupCodes) {
                final String code = Utils.clearPrintCode(utilisationCode);
                if (turonCodes.containsKey(code)) {
                    CisInfo cisInfo = turonCodes.get(code);
                    if (MarkStatus.EMITTED != cisInfo.getStatus()) {
                        utilisationCodes.remove(code);
                    }
                    if (!organization.getProductGroup().checkIsCanAggregateCodeByStatus(cisInfo.getStatus())) {
                        errorCodes.add(code);
                        utilisationCodes.remove(code);
                    }
                } else {
                    // not found codes
                    notFound.add(code);
                    utilisationCodes.remove(code);
                }
            }
            final Map<String, String> parentErrorMessageMap = new HashMap<>();
            if (CollectionUtils.isNotEmpty(errorCodes)) {
//                log.info("Error codes {}", errorCodes);
                for (String errorCode : errorCodes) {
                    if (childParentCodes.containsKey(errorCode)) {
                        final String parentCode = childParentCodes.get(errorCode);
                        String oldErrorMessage = parentErrorMessageMap.getOrDefault(parentCode, null);
                        parentErrorMessageMap.put(parentCode, oldErrorMessage + "\nОшибка при проверке КМ: " + errorCode);
                    }
                }
            }
            if (CollectionUtils.isNotEmpty(notFound)) {
//                log.debug("Not found codes {}", notFound);
                for (String errorCode : notFound) {
                    if (childParentCodes.containsKey(errorCode)) {
                        final String parentCode = childParentCodes.get(errorCode);
                        String oldErrorMessage = parentErrorMessageMap.getOrDefault(parentCode, null);
                        parentErrorMessageMap.put(parentCode, oldErrorMessage + "\nКод маркировки отсутствует в системе Турона!: " + errorCode);
                    }
                }
            }
//            log.debug("Parent error messages {}", parentErrorMessageMap);
            if (CollectionUtils.isEmpty(utilisationCodes)) {
                log.error("Utilisation Codes by key {} is empty", key);
                continue;
            }
            String reportId = null;
            String errorMessage = null;
            if (CollectionUtils.isNotEmpty(utilisationCodes)) {
                final UtilisationResponse utilisationResponse = utilisationService.utiliseByCheckingHashCode(utilisationCodes, organization, key);
                reportId = utilisationResponse != null ? utilisationResponse.getReportId() : null;
                errorMessage = utilisationResponse != null ? utilisationResponse.getErrorMessage() : null;
            }

            final Set<String> parents = new HashSet<>(parentAggregationMap.get(key));
            for (String cleanCode : parents) {
                String errors = parentErrorMessageMap.getOrDefault(cleanCode, "");
                final boolean hasError = StringUtils.isNotBlank(errors) && StringUtils.isNotBlank(errorMessage);
                errors = StringUtils.isNotBlank(errorMessage) ? errorMessage : "";
                if (Utils.isAggregation(cleanCode)) {
                    Optional<Aggregation> firstByCode = aggregationRepository.findFirstByCode(cleanCode);
                    if (firstByCode.isPresent()) {
                        Aggregation aggregation = firstByCode.get();
                        if (hasError) {
                            aggregation.setStatus(AggregationStatus.ERROR);
                        } else {
                            aggregation.setStatus(AggregationStatus.APPLIED);
                        }
                        aggregation.setUtilisationTime(LocalDateTime.now());
                        aggregation.setErrorMessage(errors);
                        aggregation.setUtilisationId(reportId);
                        aggregationRepository.save(aggregation);

                    }
                } else {
                    Optional<Block> firstByCode = blockRepository.findFirstByCode(cleanCode);
                    if (firstByCode.isPresent()) {
                        Block block = firstByCode.get();
                        if (hasError) {
                            block.setStatus(AggregationStatus.ERROR);
                        } else {
                            block.setStatus(AggregationStatus.APPLIED);
                        }
                        block.setUtilisationTime(LocalDateTime.now());
                        block.setErrorMessage(errors);
                        block.setUtilisationId(reportId);
                        blockRepository.save(block);
                    }
                }
            }
        }

        redisAggregationRepository.deleteAll(aggregations);
    }

    private Map<String, CisInfo> getTuronCodes(final Map<String, String> utilizeCodes, String organizationTin) {
        final Map<String, CisInfo> turonCodes = new HashMap<>();
        if (MapUtils.isNotEmpty(utilizeCodes)) {
            List<String> cleanCodes = new ArrayList<>(utilizeCodes.keySet());
            final String currentOrganizationAsilBelgiToken = getCurrentOrganizationAsilBelgiTokenOrNul(organizationTin);
            if (currentOrganizationAsilBelgiToken == null) return turonCodes;

            if (cleanCodes.size() > AsilBelgisiClient.cisInfoLimit) {
                List<List<String>> partition = Lists.partition(cleanCodes, AsilBelgisiClient.cisInfoLimit);
                for (List<String> list : partition) {
                    try {
                        final CisInfoResponse[] cisInfoResponses = asilBelgisiClient.cisesInfo(currentOrganizationAsilBelgiToken, list);
                        for (CisInfoResponse cisInfoResponse : cisInfoResponses) {
                            final CisInfo cisInfo = cisInfoResponse.getCisInfo();
                            turonCodes.put(cisInfo.getCis(), cisInfo);
                        }
                    } catch (Exception e) {
                        log.error("Ошибка при проверке КМ: {}", e.getMessage(), e);
                    }
                }
            } else {
                try {
                    final CisInfoResponse[] cisInfoResponses = asilBelgisiClient.cisesInfo(currentOrganizationAsilBelgiToken, cleanCodes);
                    for (CisInfoResponse cisInfoResponse : cisInfoResponses) {
                        final CisInfo cisInfo = cisInfoResponse.getCisInfo();
                        turonCodes.put(cisInfo.getCis(), cisInfo);
                    }
                } catch (Exception e) {
                    log.error("Ошибка при проверке КМ: {}", e.getMessage(), e);
                }
            }

        }
        return turonCodes;
    }

    private Set<String> clearForUtilise(final String asilBelgiToken, final Map<String, String> utilisationCodes) {
        Set<String> removedCodes = new HashSet<>();
        final Set<String> codesForCheck = new HashSet<>(utilisationCodes.keySet());
        if (utilisationCodes.size() < AsilBelgisiClient.cisInfoLimit) {
            String removedNotEmittedCode = removeNotEmitted(asilBelgiToken, utilisationCodes, codesForCheck);
            removedCodes.add(removedNotEmittedCode);
        } else {
            List<List<String>> partitions = Lists.partition(new LinkedList<>(codesForCheck), AsilBelgisiClient.cisInfoLimit);
            for (List<String> partition : partitions) {
                String removedNotEmittedCode = removeNotEmitted(asilBelgiToken, utilisationCodes, new HashSet<>(partition));
                removedCodes.add(removedNotEmittedCode);
            }
        }
        return removedCodes;
    }

    private String removeNotEmitted(final String asilBelgiToken, final Map<String, String> utilisationCodes,
                                    final Set<String> partition) {
        try {
            CisInfoResponse[] cisInfoResponses = asilBelgisiClient.cisesInfo(asilBelgiToken, partition);
            for (CisInfoResponse cisInfoResponse : cisInfoResponses) {
                if (cisInfoResponse != null) {
                    CisInfo info = cisInfoResponse.getCisInfo();
                    if (info != null) {
                        if (MarkStatus.EMITTED != info.getStatus()) {
                            utilisationCodes.remove(info.getCis());
                            return info.getCis();
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("Cis Info check error", e);
        }
        return null;
    }

    private void setUtilisationResponse(final Set<String> parentCodes, final String reportId,
                                        final String errorMessage) {
        for (String cleanCode : parentCodes) {
            if (Utils.isAggregation(cleanCode)) {
                aggregationRepository.findFirstByCode(cleanCode)
                        .ifPresent(aggregation -> {
                            aggregation.setUtilisationTime(LocalDateTime.now());
                            if (errorMessage != null) {
                                aggregation.setStatus(AggregationStatus.ERROR);
                                aggregation.setErrorMessage(errorMessage);
                            } else {
                                aggregation.setStatus(AggregationStatus.APPLIED);
                            }
                            aggregation.setUtilisationId(reportId);
                            aggregationRepository.save(aggregation);
                        });
            } else {
                blockRepository.findFirstByCode(cleanCode)
                        .ifPresent(block -> {
                            block.setUtilisationTime(LocalDateTime.now());
                            if (errorMessage != null) {
                                block.setStatus(AggregationStatus.ERROR);
                                block.setErrorMessage(errorMessage);
                            } else {
                                block.setStatus(AggregationStatus.APPLIED);
                            }
                            block.setUtilisationId(reportId);
                            blockRepository.save(block);
                        });
            }
        }
    }
}
