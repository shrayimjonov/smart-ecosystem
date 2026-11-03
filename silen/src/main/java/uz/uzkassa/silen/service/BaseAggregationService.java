package uz.uzkassa.silen.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.silen.domain.Aggregation;
import uz.uzkassa.silen.domain.Organization;
import uz.uzkassa.silen.domain.mongo.Block;
import uz.uzkassa.silen.domain.mongo.Mark;
import uz.uzkassa.silen.domain.redis.RedisAggregation;
import uz.uzkassa.silen.dto.mq.Payload;
import uz.uzkassa.silen.enumeration.AggregationStatus;
import uz.uzkassa.silen.enumeration.AggregationType;
import uz.uzkassa.silen.enumeration.MarkStatus;
import uz.uzkassa.silen.integration.AsilBelgisiClient;
import uz.uzkassa.silen.integration.asilbelgi.dto.CisInfo;
import uz.uzkassa.silen.integration.asilbelgi.dto.CisInfoResponse;
import uz.uzkassa.silen.integration.turon.UtilisationResponse;
import uz.uzkassa.silen.rabbitmq.MqConstants;
import uz.uzkassa.silen.rabbitmq.producer.RabbitMqProducer;
import uz.uzkassa.silen.repository.mongo.BlockRepository;
import uz.uzkassa.silen.repository.mongo.MarkRepository;
import uz.uzkassa.silen.repository.pg.AggregationRepository;
import uz.uzkassa.silen.repository.pg.OrganizationRepository;
import uz.uzkassa.silen.repository.redis.RedisAggregationRepository;
import uz.uzkassa.silen.repository.redis.RedisLastAggregationMarksRepository;
import uz.uzkassa.silen.utils.Utils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service Implementation for managing {@link Aggregation} and {@link Mark}.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BaseAggregationService extends BaseService {
    MarkRepository markRepository;
    RabbitMqProducer rabbitMqProducer;
    AggregationRepository aggregationRepository;
    RedisAggregationRepository redisAggregationRepository;
    UtilisationService utilisationService;
    AsilBelgisiClient asilBelgisiClient;
    OrganizationRepository organizationRepository;
    BlockRepository blockRepository;
    RedisLastAggregationMarksRepository redisLastAggregationMarksRepository;

    /**
     * Note !
     * Do not create AggregationHistory here
     *
     * @param payload SaveAggregationMarksPayload
     */
    @Transactional
    public void saveMarks(final Payload payload) {
        final String code18 = Utils.clearAggregationCode(payload.getId());
        final String code20 = Utils.clearAggregationCode20(payload.getId());
        final Aggregation aggregation = aggregationRepository.findFirstByCodeOrCode(code18, code20).orElse(null);
        if (aggregation == null) {
            return;
        }
        aggregation.setCode(code20);
        final String parentBarcode = aggregation.getProduct().getParentBarcode();
        final String barcode = aggregation.getProduct().getBarcode();
        final RedisAggregation redisAggregation = redisAggregationRepository.findFirstByCodeOrCode(code18, code20).orElse(null);
        if (redisAggregation == null) {
            return;
        }
//        if (redisAggregation.getAggregationMode() != null && AggregationMode.Massive == redisAggregation.getAggregationMode()) {
//            return;
//        }
        redisAggregation.setCode(code20);
        boolean aggregationChanged = false;
        final Organization organization = aggregation.getOrganization();
        final Map<String, String> codes = new HashMap<>();
        final Set<String> aggrCodes = new HashSet<>();
        final Map<String, String> blockCodes = new HashMap<>();
        for (String printCode : redisAggregation.getMarks()) {
            String code = Utils.clearPrintCode(printCode);
            if (Utils.isAggregation(printCode)) {
                aggrCodes.add(code);
                continue;
            }
            if (barcode != null && printCode.contains(barcode)) {
                codes.put(code, Utils.fillWithUniCode(printCode));
            } else if (parentBarcode != null && printCode.contains(parentBarcode)) {
                blockCodes.put(code, Utils.fillWithUniCode(printCode));
            }
        }
        final Set<String> clearCodes = codes.keySet();
        final Map<String, CisInfo> turonCodes = new HashMap<>();

        if (CollectionUtils.isNotEmpty(clearCodes)) {
            if (!redisAggregation.isOffline()) {
                try {
                    final String currentOrganizationAsilBelgiToken = getCurrentOrganizationAsilBelgiToken(organization.getTin());
                    final CisInfoResponse[] cisInfoResponses = asilBelgisiClient.cisesInfo(currentOrganizationAsilBelgiToken, clearCodes);
                    for (CisInfoResponse cisInfoResponse : cisInfoResponses) {
                        final CisInfo cisInfo = cisInfoResponse.getCisInfo();
                        turonCodes.put(cisInfo.getCis(), cisInfo);
                    }
                } catch (Exception e) {
                    aggregation.setStatus(AggregationStatus.ERROR);
                    aggregation.setErrorMessage("Ошибка при проверке КМ: " + e.getMessage());
                    aggregationChanged = true;
                    log.error("Error while cises info", e);
                }
            }

            final Map<String, Mark> marks = new HashMap<>();
            final Set<String> utiliseCodes = new HashSet<>();
            final Set<String> notFoundCodes = new HashSet<>();
            for (String code : clearCodes) {
                final Mark mark = markRepository.findFirstByCode(code).orElseGet(Mark::new);
                mark.setParentCode(aggregation.getCode());
                mark.setUsed(true);
                mark.setStatus(AggregationStatus.APPLIED);
                if (mark.getId() == null) {
                    mark.setPrintCode(codes.get(code));
                    mark.setCode(code);
                    mark.setProductId(redisAggregation.getProductId());
                    mark.setCreatedDate(LocalDateTime.now());
                    mark.setOrganizationId(redisAggregation.getOrganizationId());
                }
                if (MapUtils.isEmpty(turonCodes)) {
                    marks.put(codes.get(code), mark);
                    continue;
                }
                if (!redisAggregation.isOffline()) {
                    if (turonCodes.containsKey(code)) {
                        final CisInfo cisInfo = turonCodes.get(code);
                        mark.setTuronStatus(cisInfo.getStatus());
                        if (MarkStatus.EMITTED == cisInfo.getStatus()) {
                            utiliseCodes.add(codes.get(code));
                        }
                    } else {
                        mark.setStatus(AggregationStatus.ERROR);
                        mark.setTuronStatus(MarkStatus.ERROR);
                        notFoundCodes.add(code);
                    }
                }
                marks.put(codes.get(code), mark);
            }
            if (CollectionUtils.isNotEmpty(notFoundCodes)) {
                aggregation.setStatus(AggregationStatus.ERROR);
                aggregation.setErrorMessage("КМ не найден: " + notFoundCodes);
                aggregationRepository.save(aggregation);
                return;
            }
            if (CollectionUtils.isNotEmpty(utiliseCodes)) {
                try {
                    UtilisationResponse utilisationResponse;
                    if (aggregation.getSerialNum() != null) {
                        utilisationResponse = utilisationService.create(utiliseCodes, organization, aggregation.getProductionDate(), aggregation.getExpirationDate(), aggregation.getSerialNum().getSerialNumber());
                    } else {
                        utilisationResponse = utilisationService.create(utiliseCodes, organization, aggregation.getProductionDate());
                    }
                    String reportId = utilisationResponse != null && utilisationResponse.getReportId() != null ? utilisationResponse.getReportId() : null;
                    String errorMessage = utilisationResponse != null && utilisationResponse.getErrorMessage() != null ? utilisationResponse.getErrorMessage() : null;

                    aggregation.setUtilisationId(reportId);
                    aggregation.setErrorMessage(errorMessage);
                    aggregation.setStatus(AggregationStatus.IN_PROGRESS);
                    aggregation.setUtilisationTime(LocalDateTime.now());
                    for (String code : utiliseCodes) {
                        marks.get(code).setTuronStatus(MarkStatus.APPLIED);
                        marks.get(code).setReportId(reportId);
                    }
                } catch (Exception e) {
                    aggregation.setStatus(AggregationStatus.ERROR);
                    aggregation.setErrorMessage(e.getMessage());
                    log.error("Error while utilisation", e);
                }
                aggregationChanged = true;
            }
            markRepository.saveAll(marks.values());
        } else if (CollectionUtils.isNotEmpty(aggrCodes)) {
            aggregationRepository.updateParentCode(aggrCodes, aggregation.getCode());
        } else if (CollectionUtils.isNotEmpty(blockCodes.keySet())) {
            Set<String> clearBlockCodes = blockCodes.keySet();
            if (!redisAggregation.isOffline()) {
                try {
                    final String currentOrganizationAsilBelgiToken = getCurrentOrganizationAsilBelgiToken(organization.getTin());
                    final CisInfoResponse[] cisInfoResponses = asilBelgisiClient.cisesInfo(currentOrganizationAsilBelgiToken, clearBlockCodes);
                    for (CisInfoResponse cisInfoResponse : cisInfoResponses) {
                        final CisInfo cisInfo = cisInfoResponse.getCisInfo();
                        turonCodes.put(cisInfo.getCis(), cisInfo);
                    }
                } catch (Exception e) {
                    aggregation.setStatus(AggregationStatus.ERROR);
                    aggregation.setErrorMessage("Ошибка при проверке КМ: " + e.getMessage());
                    aggregationChanged = true;
                    log.error("Error while cises info", e);
                }
            }
            Set<String> errorBlockCodes = new HashSet<>();
            for (String code : clearBlockCodes) {
                final Block block = blockRepository.findFirstByCode(code).orElseGet(Block::new);
                block.setParentCode(aggregation.getCode());
                block.setUsed(true);
                if (block.getId() == null) {
                    block.setPrintCode(blockCodes.get(code));
                    block.setCode(code);
                    block.setProductId(redisAggregation.getProductId());
                    block.setCreatedDate(LocalDateTime.now());
                    block.setOrganizationId(redisAggregation.getOrganizationId());
                }

                if (!redisAggregation.isOffline()) {
                    if (turonCodes.containsKey(code)) {
                        final CisInfo cisInfo = turonCodes.get(code);
                        block.setTuronStatus(cisInfo.getStatus());
                        if (MarkStatus.APPLIED == cisInfo.getStatus() && CollectionUtils.isNotEmpty(cisInfo.getChild())) {
                            if (block.getId() == null) {
                                block.setQuantity(cisInfo.getChild().size());
                                block.setStatus(AggregationStatus.CREATED);
                            }
                        } else {
                            errorBlockCodes.add(code);
                        }
                    } else {
                        errorBlockCodes.add(code);
                    }
                }
                blockRepository.save(block);
            }
            if (CollectionUtils.isNotEmpty(errorBlockCodes)) {
                aggregation.setStatus(AggregationStatus.ERROR);
                aggregation.setErrorMessage("Ошибка при проверке коды: " + errorBlockCodes);
                aggregationRepository.save(aggregation);
                rabbitMqProducer.sendForSaveAggregationMarks(payload, 600_000);
                return;
            }
        }
        clearCodes.forEach(redisLastAggregationMarksRepository::deleteById);
        redisAggregationRepository.delete(redisAggregation);
        if (redisAggregation.isOffline()) {
            aggregation.setStatus(AggregationStatus.ERROR);
            aggregation.setErrorMessage("Агрегировано в режиме оффлайн");
            return;
        }

        if (aggregationChanged) {
            aggregationRepository.save(aggregation);
        }

        if (aggregation.getStatus() != AggregationStatus.ERROR) {
            rabbitMqProducer.resendAggregationToTuron(aggregation.getCode(), MqConstants.RESEND_AGGREGATION_DELAY);
            return;
        }

        if (AggregationType.PRINTING != aggregation.getAggregationType()) return;
        rabbitMqProducer.sendAggregationStatusChangedEvent(aggregation.toPayload());
    }

    public void saveBlockMarks(Payload payload) {
        final Block block = blockRepository.findFirstByCode(payload.getId()).orElse(null);
        if (block == null) {
            return;
        }
        final RedisAggregation redisAggregation = redisAggregationRepository.findFirstByCode(block.getCode()).orElse(null);
        if (redisAggregation == null) {
            return;
        }
        boolean aggregationChanged = false;
        final Organization organization = organizationRepository.findById(block.getOrganizationId()).orElse(null);
        if (organization == null) return;

        final Map<String, String> codes = redisAggregation.getMarks()
                .stream()
                .filter(code -> !Utils.isAggregation(code))
                .collect(Collectors.toMap(Utils::clearPrintCode, Utils::fillWithUniCode));
        codes.put(block.getCode(), block.getPrintCode());
        final Set<String> clearCodes = codes.keySet();
        final Map<String, CisInfo> turonCodes = new HashMap<>();

        if (CollectionUtils.isNotEmpty(clearCodes)) {
            if (!redisAggregation.isOffline()) {
                try {

                    final CisInfoResponse[] cisInfoResponses = asilBelgisiClient.cisesInfo(getCurrentOrganizationAsilBelgiToken(organization.getTin()), clearCodes);
                    for (CisInfoResponse cisInfoResponse : cisInfoResponses) {
                        final CisInfo cisInfo = cisInfoResponse.getCisInfo();
                        turonCodes.put(cisInfo.getRequestedCis(), cisInfo);
                    }
                } catch (Exception e) {
                    block.setStatus(AggregationStatus.ERROR);
                    block.setErrorMessage("Ошибка при проверке КМ: " + e.getMessage());
                    aggregationChanged = true;
                    log.error("Error while cises info", e);
                }
            }

            final Map<String, Mark> marks = new HashMap<>();
            final Set<String> utiliseCodes = new HashSet<>();
            final Set<String> notFoundCodes = new HashSet<>();
            for (String code : clearCodes) {
                final Mark mark = markRepository.findFirstByCode(code).orElseGet(Mark::new);
                if (!mark.isBlock()) {
                    mark.setParentCode(redisAggregation.getCode());
                }
                mark.setUsed(true);
                mark.setStatus(AggregationStatus.APPLIED);
                if (mark.getId() == null) {
                    mark.setPrintCode(codes.get(code));
                    mark.setCode(code);
                    mark.setProductId(redisAggregation.getProductId());
                    mark.setCreatedDate(LocalDateTime.now());
                    mark.setOrganizationId(redisAggregation.getOrganizationId());
                }
                if (MapUtils.isEmpty(turonCodes)) {
                    marks.put(codes.get(code), mark);
                    continue;
                }
                if (!redisAggregation.isOffline()) {
                    if (turonCodes.containsKey(code)) {
                        final CisInfo cisInfo = turonCodes.get(code);
                        mark.setTuronStatus(cisInfo.getStatus());
                        if (MarkStatus.EMITTED == cisInfo.getStatus()) {
                            utiliseCodes.add(codes.get(code));
                        }
                    } else {
                        mark.setStatus(AggregationStatus.ERROR);
                        mark.setTuronStatus(MarkStatus.ERROR);
                        notFoundCodes.add(code);
                    }
                }
                marks.put(codes.get(code), mark);
            }
            if (CollectionUtils.isNotEmpty(notFoundCodes)) {
                block.setStatus(AggregationStatus.ERROR);
                block.setErrorMessage("КМ не найден: " + notFoundCodes);
                blockRepository.save(block);
                return;
            }
            if (CollectionUtils.isNotEmpty(utiliseCodes)) {
                try {
                    UtilisationResponse utilisationResponse;
                    if (redisAggregation.getSerialNumber() != null) {
                        utilisationResponse = utilisationService.create(utiliseCodes, organization, block.getProductionDate(), block.getExpirationDate(), redisAggregation.getSerialNumber());
                    } else {
                        utilisationResponse = utilisationService.create(utiliseCodes, organization, Optional.ofNullable(block.getProductionDate()).orElse(LocalDateTime.now()));
                    }

                    String reportId = utilisationResponse != null && utilisationResponse.getReportId() != null ? utilisationResponse.getReportId() : null;
                    String errorMessage = utilisationResponse != null && utilisationResponse.getErrorMessage() != null ? utilisationResponse.getErrorMessage() : null;

                    block.setUtilisationId(reportId);
                    block.setErrorMessage(errorMessage);
                    block.setStatus(AggregationStatus.IN_PROGRESS);
                    block.setUtilisationTime(LocalDateTime.now());
                    for (String code : utiliseCodes) {
                        marks.get(code).setTuronStatus(MarkStatus.APPLIED);
                        marks.get(code).setReportId(reportId);
                    }
                } catch (Exception e) {
                    block.setStatus(AggregationStatus.ERROR);
                    block.setErrorMessage(e.getMessage());
                    blockRepository.save(block);
                    log.error("Error while utilisation", e);
                }
                aggregationChanged = true;
            }
            markRepository.saveAll(marks.values());
            redisAggregationRepository.delete(redisAggregation);
        }

        if (redisAggregation.isOffline()) {
            block.setStatus(AggregationStatus.ERROR);
            block.setErrorMessage("Агрегировано в режиме оффлайн");
            blockRepository.save(block);
            return;
        }

        if (aggregationChanged) {
            blockRepository.save(block);
        }

        if (block.getStatus() != AggregationStatus.ERROR) {
            rabbitMqProducer.resendBlockToTuron(redisAggregation.getCode(), MqConstants.RESEND_AGGREGATION_DELAY);
        }

//        if (AggregationType.PRINTING != block.getAggregationType()) return;
//        rabbitMqProducer.sendAggregationStatusChangedEvent(block.toPayload());
    }


}