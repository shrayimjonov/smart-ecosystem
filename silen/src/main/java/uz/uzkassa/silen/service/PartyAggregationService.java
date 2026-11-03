package uz.uzkassa.silen.service;

import com.google.common.collect.Lists;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Hibernate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.silen.domain.Aggregation;
import uz.uzkassa.silen.domain.Organization;
import uz.uzkassa.silen.domain.PartyAggregation;
import uz.uzkassa.silen.domain.mongo.Block;
import uz.uzkassa.silen.domain.mongo.Mark;
import uz.uzkassa.silen.domain.redis.RedisBlockedParty;
import uz.uzkassa.silen.dto.PrintAggregationDTO;
import uz.uzkassa.silen.dto.PrintAggregationMarkDTO;
import uz.uzkassa.silen.dto.SelectItem;
import uz.uzkassa.silen.dto.filter.AggregationFilter;
import uz.uzkassa.silen.dto.filter.MongoFilter;
import uz.uzkassa.silen.dto.filter.PartyAggregationFilter;
import uz.uzkassa.silen.dto.mq.Payload;
import uz.uzkassa.silen.dto.mq.WarehousePayload;
import uz.uzkassa.silen.dto.partyaggregation.AggregationStateMessage;
import uz.uzkassa.silen.dto.partyaggregation.AggregationStatusChangedPayload;
import uz.uzkassa.silen.dto.partyaggregation.PartyAggregationDTO;
import uz.uzkassa.silen.enumeration.*;
import uz.uzkassa.silen.exceptions.BadRequestException;
import uz.uzkassa.silen.integration.TuronClient;
import uz.uzkassa.silen.integration.turon.AggregationItem;
import uz.uzkassa.silen.integration.turon.CreateAggregationRequest;
import uz.uzkassa.silen.integration.turon.CreateAggregationResponse;
import uz.uzkassa.silen.integration.turon.UtilisationResponse;
import uz.uzkassa.silen.kafka.KafkaConstants;
import uz.uzkassa.silen.kafka.producer.KafkaProducer;
import uz.uzkassa.silen.repository.mongo.BlockRepository;
import uz.uzkassa.silen.repository.mongo.MarkRepository;
import uz.uzkassa.silen.repository.pg.AggregationRepository;
import uz.uzkassa.silen.repository.pg.PartyAggregationRepository;
import uz.uzkassa.silen.repository.redis.RedisBlockedPartyRepository;
import uz.uzkassa.silen.utils.Utils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PartyAggregationService extends BaseService {
    PartyAggregationRepository partyAggregationRepository;
    MarkRepository markRepository;
    RedisBlockedPartyRepository redisBlockedPartyRepository;
    BillingPermissionService billingPermissionService;
    AggregationService aggregationService;
    AggregationRepository aggregationRepository;
    PushNotificationService pushNotificationService;
    UtilisationService utilisationService;
    KafkaProducer<WarehousePayload> kafkaProducer;
    BlockRepository blockRepository;
    AggregationItemConverter aggregationItemConverter;
    ExportReportService exportReportService;


    @Transactional
    public String create(PartyAggregationDTO dto) {
        final Organization organization = getCurrentOrganization();

        // check and throw if not enough
        Long notUsedMarksCount = checkPartiesNotUsedMarksCount(dto.getParties(), dto.getCapacity());
        long quantity = getQuantity(dto, notUsedMarksCount);

        if (organization.getTypes().contains(OrganizationType.CustomsWarehouse)) {
            billingPermissionService.checkIsAvailableCountMarks(organization.getId(), organization.isDemoAllowed(), notUsedMarksCount.intValue());
        }
        PartyAggregation partyAggregation = new PartyAggregation();
        partyAggregation.setOrganizationId(organization.getId());
        if (StringUtils.isNotEmpty(dto.getNumber())) {
            if (partyAggregationRepository.existsPartyAggregationByOrganizationIdAndNumberIgnoreCase(organization.getId(), dto.getNumber())) {
                throw new BadRequestException("Aggregation с таким номером уже существует");
            } else {
                partyAggregation.setNumber(dto.getNumber());
            }
        } else {
            generateNumber(organization.getId(), partyAggregation, "AGGREGATION-");
        }
        partyAggregation.setParties(dto.getParties());
        partyAggregation.setBlockParties(dto.getBlockParties());
        partyAggregation.setProductId(dto.getProductId());
        partyAggregation.setStatus(PartyAggregationStatus.NEW);
        partyAggregation.setSerialId(dto.getSerialId());
        partyAggregation.setStoreId(dto.getStoreId());
        partyAggregation.setGcp(dto.getGcp());
        partyAggregation.setDescription(dto.getDescription());
        partyAggregation.setProductionDate(dto.getProductionDate());
        partyAggregation.setExpirationDate(dto.getExpirationDate());
        partyAggregation.setQuantity((int) quantity);
        partyAggregation.setCapacity(dto.getCapacity());
        partyAggregation.setOffline(dto.isOffline());
        partyAggregation.setOrderId(dto.getOrderId());
        partyAggregation.setBlockOrderId(dto.getBlockOrderId());
        partyAggregation.setOrderNumber(dto.getOrderNumber());
        partyAggregation.setBlockOrderNumber(dto.getBlockOrderNumber());
        partyAggregation.setUnit(dto.getUnit().getCode());
        partyAggregation.setFully(dto.isFully());
        partyAggregationRepository.save(partyAggregation);

        return partyAggregation.getId();
    }

    public Page<PartyAggregationDTO> findAllByFilter(PartyAggregationFilter filter) {
        return partyAggregationRepository.findAllByFilter(filter).map(PartyAggregation::toDto);
    }

    public List<SelectItem> getItems(PartyAggregationFilter filter) {
        return partyAggregationRepository.findAllByFilter(filter).map(PartyAggregation::toSelectItem).getContent();
    }

    public PartyAggregationDTO getOne(String id) {
        PartyAggregation partyAggregation = partyAggregationRepository.findById(id).orElseThrow(notFound());
        PartyAggregationDTO dto = partyAggregation.toDto();
        if (partyAggregation.getBlockParties().isEmpty()) {
            List<AggregationStateMessage> codes = aggregationRepository.findAllByPartyAggregationIdOrderByCreatedDateAsc(id).stream().map(aggregation -> new AggregationStateMessage(aggregation.getPrintCode(), aggregation.getStatus())).toList();
            dto.setCodes(codes);
        } else {
            List<AggregationStateMessage> codes = blockRepository.findAllByPartyAggregationIdAndUnitOrderByCreatedDateAsc(id, PackageType.BLOCK).stream().map(aggregation -> new AggregationStateMessage(aggregation.getPrintCode(), aggregation.getStatus())).toList();
            dto.setCodes(codes);
        }

        return dto;
    }

    @Transactional
    public String update(String id, PartyAggregationDTO dto) {
        final Organization organization = getCurrentOrganization();
        PartyAggregation partyAggregation = partyAggregationRepository.findById(id).orElseThrow(notFound());
        // check and throw if not enough
        Long notUsedMarksCount = checkPartiesNotUsedMarksCount(dto.getParties(), dto.getCapacity());
        long quantity = getQuantity(dto, notUsedMarksCount);
        if (organization.getTypes().contains(OrganizationType.CustomsWarehouse)) {
            billingPermissionService.checkIsAvailableCountMarks(organization.getId(), organization.isDemoAllowed(), notUsedMarksCount.intValue());
        }
        if (StringUtils.isNotEmpty(dto.getNumber()) && !Objects.equals(partyAggregation.getNumber(), dto.getNumber())) {
            if (partyAggregationRepository.existsPartyAggregationByOrganizationIdAndNumberIgnoreCase(partyAggregation.getOrganizationId(), dto.getNumber())) {
                throw new BadRequestException("Aggregation с таким номером уже существует");
            } else {
                partyAggregation.setNumber(dto.getNumber());
            }
        } else {
            generateNumber(partyAggregation.getOrganizationId(), partyAggregation, "AGGREGATION-");
        }
        partyAggregation.setBlockParties(dto.getBlockParties());
        partyAggregation.setParties(dto.getParties());
        partyAggregation.setProductId(dto.getProductId());
        partyAggregation.setSerialId(dto.getSerialId());
        partyAggregation.setStoreId(dto.getStoreId());
        partyAggregation.setGcp(dto.getGcp());
        partyAggregation.setDescription(dto.getDescription());
        partyAggregation.setProductionDate(dto.getProductionDate());
        partyAggregation.setExpirationDate(dto.getExpirationDate());
        partyAggregation.setQuantity((int) quantity);
        partyAggregation.setCapacity(dto.getCapacity());
        partyAggregation.setOrderId(dto.getOrderId());
        partyAggregation.setBlockOrderId(dto.getBlockOrderId());
        partyAggregation.setOrderNumber(dto.getOrderNumber());
        partyAggregation.setBlockOrderNumber(dto.getBlockOrderNumber());
        partyAggregation.setUnit(dto.getUnit().getCode());
        partyAggregation.setFully(dto.isFully());
        partyAggregationRepository.save(partyAggregation);

        return partyAggregation.getId();
    }

    private long getQuantity(PartyAggregationDTO dto, Long notUsedMarksCount) {
        long quantity = notUsedMarksCount / dto.getCapacity();
        if (dto.isFully()) {
            if (notUsedMarksCount % dto.getCapacity() > 0) quantity++;
        }
        if (PackageType.BLOCK == dto.getUnit()) {
            Long notUsedBlocksCount = checkPartiesNotUsedMarksCount(dto.getBlockParties(), 1);
            quantity = Math.min(notUsedBlocksCount, notUsedMarksCount / dto.getCapacity());
            if (quantity * dto.getCapacity() > notUsedMarksCount) {
                throw badRequest("Не достаточно количество не использованных маркировок").get();
            }
        }
        return quantity;
    }

    private Long checkPartiesNotUsedMarksCount(Set<String> parties, Integer capacity) {
        // get parties not used marks count
        Long partiesNotUsedMarksCount = markRepository.countAllByPartyIdInAndUsedIsFalse(parties);
        if (partiesNotUsedMarksCount < capacity) {
            throw badRequest("Не достаточно количество не использованных маркировок").get();
        }
        return partiesNotUsedMarksCount;
    }

    public void aggregate(String id) {
        PartyAggregation partyAggregation = partyAggregationRepository.findById(id).orElseThrow(notFound());
        if (PartyAggregationStatus.NEW != partyAggregation.getStatus()) {
            throw badRequest("Операция уже завершена").get();
        }
        // checking tokens for integrations
        final Organization organization = partyAggregation.getOrganization();
        if (StringUtils.isAnyBlank(organization.getTuronToken(), organization.getOmsId())) {
            throw badRequest("AslBelgi токен отсутствует").get();
        }
//        String currentOrganizationAsilBelgiToken = getCurrentOrganizationAsilBelgiToken();

        // find party aggregation
        Hibernate.initialize(partyAggregation.getParties());
        /*if (CollectionUtils.isNotEmpty(partyAggregation.getParties())) {
            String firstParty = partyAggregation.getParties().stream().findFirst().orElseThrow(badRequest("По партию маркировок не найден"));
            final Mark testMark = markRepository.findFirstByPartyId(firstParty).orElseThrow(badRequest("По партию маркировок не найден"));
            final String firstCode = Utils.clearPrintCode(testMark.getPrintCode());
            try {
                asilBelgisiClient.cisesInfo(currentOrganizationAsilBelgiToken, Collections.singleton(firstCode));
            } catch (Exception e) {
                if (e.getMessage().contains("Connection refused")) {
                    throw new BadRequestException(ErrorConstants.ERR_CONNECTION_ERROR, e.getMessage(), ErrorConstants.TURON_NOT_AVAILABLE_ERROR_CODE, null);
                } else {
                    throw badRequest(e.getMessage()).get();
                }
            }
        }*/
        // check for aggregation exists available count
        final ProductGroup productGroup = organization.getProductGroup();
        final boolean isAlcohol = productGroup == ProductGroup.Alcohol;
        final boolean isPharma = productGroup == ProductGroup.Pharma;
        final boolean isWarehouse = organization.getTypes().contains(OrganizationType.CustomsWarehouse);

        Long notUsedMarksCount = checkPartiesNotUsedMarksCount(partyAggregation.getParties(), partyAggregation.getCapacity());

        // evaluate aggregation count
        long quantityAggregation = notUsedMarksCount / partyAggregation.getCapacity();
        if (partyAggregation.isFully()) {
            if (notUsedMarksCount % partyAggregation.getCapacity() > 0) quantityAggregation++;
        }
        if (PackageType.BLOCK == PackageType.fromCode(partyAggregation.getUnit())) {
            Long notUsedBlocksCount = checkPartiesNotUsedMarksCount(partyAggregation.getBlockParties(), 1);
            quantityAggregation = Math.min(notUsedBlocksCount, notUsedMarksCount / partyAggregation.getCapacity());
            if (quantityAggregation * partyAggregation.getCapacity() > notUsedMarksCount) {
                throw badRequest("Не достаточно количество не использованных маркировок").get();
            }
        }

        if (isAlcohol) {
            billingPermissionService.checkIsAvailableCount(organization.getId(), organization.isDemoAllowed(), organization.getParentId(), organization.getProductGroup(), (int) quantityAggregation);
        }
        if (isPharma) {
            billingPermissionService.checkIsAvailableCount(organization.getId(), organization.isDemoAllowed(), organization.getParentId(), organization.getProductGroup(), notUsedMarksCount.intValue());
        }
        if (isWarehouse) {
            billingPermissionService.checkIsAvailableCountMarks(organization.getId(), organization.isDemoAllowed(), notUsedMarksCount.intValue());
        }

        partyAggregation.setStatus(PartyAggregationStatus.IN_PROGRESS);
        partyAggregation.setQuantity((int) quantityAggregation);
        partyAggregationRepository.save(partyAggregation);

        // here create blocked parties in redis
        for (String partyId : partyAggregation.getParties()) {
            redisBlockedPartyRepository.save(new RedisBlockedParty(partyId));
        }
        for (String partyId : partyAggregation.getBlockParties()) {
            redisBlockedPartyRepository.save(new RedisBlockedParty(partyId));
        }

        // send to rabbit for process
        rabbitMqProducer.sendPartyAggregation(new Payload(id));
    }

    private void generateNumber(String organizationId, PartyAggregation shipment, String prefix) {
        Integer intNumber = partyAggregationRepository.getMaxIntNumber(organizationId);
        String number = null;
        if (intNumber == null) {
            intNumber = 1;
        }
        boolean exists = true;
        while (exists) {
            number = prefix + intNumber;
            if (partyAggregationRepository.existsPartyAggregationByOrganizationIdAndNumberIgnoreCase(organizationId, number)) {
                intNumber++;
            } else {
                exists = false;
            }
        }
        shipment.setIntNumber(intNumber);
        shipment.setNumber(number);
    }

    public void partyAggregate(Payload payload) {
        final PartyAggregation partyAggregation = partyAggregationRepository.findById(payload.getId()).orElseThrow(notFound("Party not found"));
        final Organization organization = partyAggregation.getOrganization();
        final ProductGroup productGroup = organization.getProductGroup();
        final boolean isAlcohol = productGroup == ProductGroup.Alcohol;
        final boolean isPharma = productGroup == ProductGroup.Pharma;
        final boolean isWarehouse = organization.getTypes().contains(OrganizationType.CustomsWarehouse);
        final boolean isBlock = PackageType.BLOCK == PackageType.fromCode(partyAggregation.getUnit());
        int quantityMarks = partyAggregation.getQuantity() * partyAggregation.getCapacity();

        final LocalDateTime currentTime = LocalDateTime.now();
        final LocalDateTime productionDate = Utils.getWithServerTime(partyAggregation.getProductionDate(), currentTime);
        final LocalDateTime expirationDate = partyAggregation.getExpirationDate() != null ? Utils.getWithServerTime(partyAggregation.getExpirationDate(), currentTime) : null;

        if (isBlock) {
            partyAggregateBlock(partyAggregation, organization, productionDate, expirationDate);
        } else {
            partyAggregateBox(partyAggregation, organization, productionDate, expirationDate);
        }

        if (isAlcohol) {
            billingPermissionService.decrementAggregationCountAndSave(organization.getId(), organization.isDemoAllowed(), productGroup, partyAggregation.getQuantity());
        } else if (isPharma) {
            if (organization.getTypes().contains(OrganizationType.Importer)) {
                billingPermissionService.decrementAggregationCountAndSave(organization.getId(), organization.isDemoAllowed(), productGroup, quantityMarks);
            }
        }
        if (isWarehouse) {
            billingPermissionService.decrementCountMarksAndSave(organization.getId(), quantityMarks);
        }
        // after success remove party block
        if (!PartyAggregationStatus.ERROR.equals(partyAggregation.getStatus())) {
            partyAggregation.getParties().forEach(redisBlockedPartyRepository::deleteById);
            partyAggregation.getBlockParties().forEach(redisBlockedPartyRepository::deleteById);
            partyAggregation.setStatus(PartyAggregationStatus.UTILISED);
            partyAggregationRepository.save(partyAggregation);
            log.info("======================== AGGREGATION PARTITIONS FINISHED {} ========================", partyAggregation.getNumber());
            rabbitMqProducer.sendPartyAggregationFinished(new Payload(partyAggregation.getId()));
        }
    }

    private void partyAggregateBox(PartyAggregation partyAggregation, Organization organization, LocalDateTime productionDate, LocalDateTime expirationDate) {
        int remaining = partyAggregation.getQuantity() * partyAggregation.getCapacity();
        int filterPageSize = Math.min(remaining, TuronClient.utilisationLimit);

        MongoFilter mongoFilter = new MongoFilter();
        mongoFilter.setSize(filterPageSize);
        mongoFilter.setUsed(false);
        mongoFilter.setPage(0);
        List<Mark> unused = new LinkedList<>();
        for (String partyId : partyAggregation.getParties()) {
            mongoFilter.setPartyId(partyId);
            if (remaining > 0) {
                int pageNumber = 0;
                boolean isNotLast = false;
                do {
                    if (remaining > 0) {
                        mongoFilter.setSize(Math.min(remaining, filterPageSize));
                        Page<Mark> page = markRepository.findAllByFilter(mongoFilter);
                        isNotLast = page.hasNext();
                        remaining -= page.getNumberOfElements();

                        if (page.isEmpty()) {
                            continue;
                        }

                        String errorMessage = null;
                        String reportId = null;
                        List<String> printCodes = page.stream().map(Mark::getPrintCode).toList();
                        if (CollectionUtils.isNotEmpty(printCodes)) {
                            final UtilisationResponse utilisationResponse = utilisationService.sendToApplyPartyMarks(printCodes, partyAggregation, organization);
                            printInfo("PAGE NUMBER " + pageNumber++ + " TOTAL " + page.getTotalPages() + " UTILISATION RESPONSE " + utilisationResponse);

                            errorMessage = utilisationResponse != null ? utilisationResponse.getErrorMessage() : null;
                            reportId = utilisationResponse != null ? utilisationResponse.getReportId() : null;
                        }

                        unused.addAll(page.getContent());
                        List<List<Mark>> boxPartitions = Lists.partition(new LinkedList<>(unused), partyAggregation.getCapacity());
                        for (List<Mark> part : boxPartitions) {
                            if (part.size() == partyAggregation.getCapacity()) {
                                generateSingleAggregation(partyAggregation, reportId, errorMessage, part, productionDate, expirationDate);
                                unused.removeAll(part);
                            }
                        }
                    } else {
                        isNotLast = false;
                    }
                } while (isNotLast);
            }
        }

        // from any parties
        if (partyAggregation.isFully() && !unused.isEmpty()) {
            List<String> printCodes = unused.stream().map(Mark::getPrintCode).toList();
            final UtilisationResponse utilisationResponse = utilisationService.sendToApplyPartyMarks(printCodes, partyAggregation, organization);
            log.info("======================== PAGE NUMBER {} TOTAL {} UTILISATION RESPONSE {} ========================", -1, unused.size(), utilisationResponse);
            String errorMessage = utilisationResponse != null ? utilisationResponse.getErrorMessage() : null;
            String reportId = utilisationResponse != null ? utilisationResponse.getReportId() : null;

            List<List<Mark>> otherPartitions = Lists.partition(new LinkedList<>(unused), partyAggregation.getCapacity());
            for (List<Mark> partition : otherPartitions) {
                generateSingleAggregation(partyAggregation, reportId, errorMessage, partition, productionDate, expirationDate);
            }
        }
    }

    public void partyAggregateBlock(PartyAggregation partyAggregation, Organization organization, LocalDateTime productionDate, LocalDateTime expirationDate) {
        final Map<String, Block> setOfBlockCodes = getBlockCodes(partyAggregation);
        if (MapUtils.isEmpty(setOfBlockCodes)) {
            log.error("Blocks not found ");
            partyAggregation.setErrorMessage("Blocks not found");
            partyAggregationRepository.save(partyAggregation);
            return;
        }

        int remainingMarks = partyAggregation.getQuantity() * partyAggregation.getCapacity();
        int filterPageSize = Math.min(remainingMarks, TuronClient.utilisationLimit - partyAggregation.getQuantity());
        MongoFilter mongoFilter = new MongoFilter();
        mongoFilter.setUsed(false);
        mongoFilter.setUnit(null);
        mongoFilter.setSize(filterPageSize);
        mongoFilter.setPage(0);

        LinkedList<String> blockCodesKeySet = new LinkedList<>(setOfBlockCodes.keySet());
        Map<String, String> utilisationCodes = new HashMap<>();
        Map<String, List<Mark>> parentChildMap = new HashMap<>();
        final List<Mark> unused = new LinkedList<>();
        for (String partyId : partyAggregation.getParties()) {
            if (remainingMarks <= 0) continue;
            mongoFilter.setPartyId(partyId);
            int pageNumber = 0;
            boolean isNotLast = false;
            do {
                if (remainingMarks > 0) {
                    mongoFilter.setSize(Math.min(remainingMarks, filterPageSize));
                    Page<Mark> page = markRepository.findAllByFilter(mongoFilter);
                    isNotLast = page.hasNext();
                    remainingMarks -= page.getNumberOfElements();

                    if (page.isEmpty() || setOfBlockCodes.isEmpty()) {
                        continue;
                    }
                    unused.addAll(page.getContent());
                    List<List<Mark>> boxPartitions = Lists.partition(new LinkedList<>(unused), partyAggregation.getCapacity());
                    for (List<Mark> part : boxPartitions) {
                        if (blockCodesKeySet.isEmpty()) {
                            continue;
                        }
                        if (part.size() == partyAggregation.getCapacity()) {
                            String parentCode = blockCodesKeySet.removeFirst();
                            Block block = setOfBlockCodes.get(parentCode);
                            parentChildMap.put(parentCode, part);

                            utilisationCodes.put(block.getCode(), block.getPrintCode());
                            Map<String, String> collect = part.stream().collect(Collectors.toMap(Mark::getCode, Mark::getPrintCode));
                            utilisationCodes.putAll(collect);
                            unused.removeAll(part);
                        }
                    }

                    String errorMessage = null;
                    String reportId = null;

                    if (MapUtils.isNotEmpty(utilisationCodes)) {
                        final Collection<String> values = utilisationCodes.values();
                        UtilisationResponse utilisationResponse = utilisationService.sendToApplyPartyMarks(values, partyAggregation, organization);
                        log.info("======================== PAGE NUMBER {} TOTAL {} UTILISATION RESPONSE {} ========================", pageNumber, values.size(), utilisationResponse);
                        errorMessage = utilisationResponse != null ? utilisationResponse.getErrorMessage() : null;
                        reportId = utilisationResponse != null ? utilisationResponse.getReportId() : null;
                    }

                    for (String parentCode : parentChildMap.keySet()) {
                        List<Mark> marks = parentChildMap.get(parentCode);
                        Block block = setOfBlockCodes.get(parentCode);
                        setMarkReportInfo(partyAggregation, reportId, errorMessage, block, marks, productionDate, expirationDate);
                    }
                    parentChildMap.clear();
                    utilisationCodes.clear();
                } else {
                    isNotLast = false;
                }
            } while (isNotLast);
        }

        /*if (partyAggregation.isFully() && !unused.isEmpty()) {
            List<List<Mark>> otherPartitions = Lists.partition(new LinkedList<>(unused), partyAggregation.getCapacity());
            for (List<Mark> partition : otherPartitions) {
                if (blockCodesKeySet.isEmpty()) {
                    continue;
                }
                String parentCode = blockCodesKeySet.removeFirst();
                Block block = setOfBlockCodes.get(parentCode);
                for (Mark mark : partition) {
                    utilisationCodes.put(mark.getCode(), mark.getPrintCode());
                }
                utilisationCodes.put(block.getCode(), block.getPrintCode());
                parentChildMap.put(parentCode, partition);
            }

            UtilisationResponse utilisationResponse = utilisationService.sendToApplyPartyMarks(utilisationCodes.values(), partyAggregation, organization);
            log.info("======================== PAGE NUMBER {} TOTAL {} UTILISATION RESPONSE {} ========================", -1, unused.size(), utilisationResponse);

            final String errorMessage = utilisationResponse != null ? utilisationResponse.getErrorMessage() : null;
            final String reportId = utilisationResponse != null ? utilisationResponse.getReportId() : null;
            for (String code : parentChildMap.keySet()) {
                List<Mark> marks = parentChildMap.get(code);
                Block block = setOfBlockCodes.get(code);
                setMarkReportInfo(partyAggregation, reportId, errorMessage, block, marks, productionDate, expirationDate);
            }
            parentChildMap.clear();
            utilisationCodes.clear();
        }*/
    }

    private Map<String, Block> getBlockCodes(PartyAggregation partyAggregation) {
        int remainingBlocks = partyAggregation.getQuantity();
        final Map<String, Block> setOfBlockCodes = new HashMap<>();
        MongoFilter mongoFilter = new MongoFilter();
        mongoFilter.setUsed(false);
        mongoFilter.setUnit(PackageType.BLOCK);
        mongoFilter.setSize(Math.min(remainingBlocks, TuronClient.utilisationLimit));

        for (String partyId : partyAggregation.getBlockParties()) {
            if (remainingBlocks <= 0) continue;

            int pageNumber = 0;
            mongoFilter.setPartyId(partyId);
            boolean hasNext = false;
            do {
                if (remainingBlocks > 0) {
                    mongoFilter.setPage(pageNumber++);
                    Page<Block> page = blockRepository.findAllByFilter(mongoFilter);
                    for (Block block : page) {
                        setOfBlockCodes.put(block.getCode(), block);
                    }
                    remainingBlocks -= page.getNumberOfElements();
                    hasNext = page.hasNext();
                } else {
                    hasNext = false;
                }
            } while (hasNext);
        }

        return setOfBlockCodes;
    }

    public void setMarkReportInfo(final PartyAggregation partyAggregation, final String reportId, String errorMessage, Block block, Collection<Mark> children, LocalDateTime productionDate, LocalDateTime expirationDate) {
        if (block == null) {
            return;
        }
        for (Mark mark : children) {
            mark.setUsed(true);
            mark.setStatus(AggregationStatus.APPLIED);
            mark.setParentCode(block.getCode());
            mark.setUtilisationTime(LocalDateTime.now());
            mark.setUtilisationId(reportId);
            mark.setErrorMessage(errorMessage);
            markRepository.save(mark);
        }
        block.setAggregationType(AggregationType.PRINTING);
        block.setPartyAggregationId(partyAggregation.getId());
        block.setLogin(partyAggregation.getLastModifiedBy());
        block.setUsed(true);
        if (productionDate != null) {
            block.setProductionDate(productionDate);
        }
        if (expirationDate != null) {
            block.setExpirationDate(expirationDate);
        }
        block.setUtilisationTime(LocalDateTime.now());
        block.setQuantity(children.size());
        if (reportId != null) {
            block.setUtilisationId(reportId);
            block.setStatus(AggregationStatus.APPLIED);
        } else {
            block.setErrorMessage(errorMessage);
            block.setStatus(AggregationStatus.ERROR);
        }
        blockRepository.save(block);
        // notify current aggregation state
        pushNotificationService.sendPartyAggregation(block.toPayload(), partyAggregation.getId());
    }

    public void generateSingleAggregation(final PartyAggregation partyAggregation, final String reportId, String errorMessage, final Collection<Mark> marks, final LocalDateTime productionDate, final LocalDateTime expirationDate) {
        // this method only generates aggregation code and not saves it to db
        Aggregation aggregation = aggregationService.generateAggregation(PackageType.fromCode(partyAggregation.getUnit()), partyAggregation.getOrganizationId(), partyAggregation.getGcp());
        aggregation.setProductId(partyAggregation.getProductId());
        aggregation.setGcp(partyAggregation.getGcp());
        aggregation.setSerialId(partyAggregation.getSerialId());
        aggregation.setUnit(partyAggregation.getUnit());
        aggregation.setQuantity(marks.size());
        aggregation.setDescription(partyAggregation.getDescription());
        aggregation.setAggregationType(AggregationType.PRINTING);
        aggregation.setStatus(AggregationStatus.IN_PROGRESS);
        aggregation.setPartyAggregationId(partyAggregation.getId());
        aggregation.setLogin(partyAggregation.getLastModifiedBy());
        aggregation.setUtilisationId(reportId);
        aggregation.setErrorMessage(errorMessage);
        aggregation.setUtilisationTime(LocalDateTime.now());
        if (productionDate != null) {
            aggregation.setProductionDate(productionDate);
        }
        if (expirationDate != null) {
            aggregation.setExpirationDate(expirationDate);
        }
        aggregationRepository.save(aggregation);

        log.info("PARTY {}\n AGGREGATION {}\nCOUNT {}\nMARK SIZE {}", partyAggregation.getId(), aggregation.getCode(), partyAggregation.getCapacity(), marks.size());
        for (Mark mark : marks) {
            mark.setParentCode(aggregation.getCode());
            mark.setStatus(AggregationStatus.CREATED);
            mark.setUsed(true);
            mark.setUtilisationId(reportId);
            mark.setUtilisationTime(LocalDateTime.now());
            markRepository.save(mark);
        }
        // notify current aggregation state
        pushNotificationService.sendPartyAggregation(aggregation.toPayload(), partyAggregation.getId());
    }

    public List<PrintAggregationDTO> getForPrinting(String id) {
        final PartyAggregation partyAggregation = partyAggregationRepository.findById(id).orElseThrow(notFound());
        if (PartyAggregationStatus.CLOSED != partyAggregation.getStatus())
            throw badRequest("Процесс не завершен").get();

        List<PrintAggregationDTO> responseList = new ArrayList<>();

        if (PackageType.BLOCK == PackageType.fromCode(partyAggregation.getUnit())) {
            List<Block> aggregations = blockRepository.findAllByPartyAggregationIdOrderByCreatedDateAsc(id);
            for (Block aggregation : aggregations) {
                Set<PrintAggregationMarkDTO> allByParentCode = getPrintAggregationMarkDTOS(aggregation.getCode());
                responseList.add(new PrintAggregationDTO(aggregation.getPrintCode(), allByParentCode));
            }
        } else {
            List<Aggregation> aggregations = aggregationRepository.findAllByPartyAggregationIdOrderByCreatedDateAsc(id);
            for (Aggregation aggregation : aggregations) {
                Set<PrintAggregationMarkDTO> allByParentCode = getPrintAggregationMarkDTOS(aggregation.getCode());
                responseList.add(new PrintAggregationDTO(aggregation.getPrintCode(), allByParentCode));
            }
        }
        return responseList;
    }

    @NotNull
    private Set<PrintAggregationMarkDTO> getPrintAggregationMarkDTOS(String aggregation) {
        return markRepository.findAllByParentCode(aggregation).stream().map(mark -> {
            PrintAggregationMarkDTO printAggregationMarkDTO = new PrintAggregationMarkDTO();
            printAggregationMarkDTO.setPrintCode(mark.getPrintCode());
            Matcher matcher = Utils.parseMarkForPharma(mark.getPrintCode());
            if (matcher.find()) {
                printAggregationMarkDTO.setSerialNumber(matcher.group(3));
            }
            return printAggregationMarkDTO;
        }).collect(Collectors.toSet());
    }


    public void partyAggregationStatusCheck(AggregationStatusChangedPayload payload) {
        // notify current aggregation state
        pushNotificationService.sendPartyAggregation(payload, payload.getPartyAggregationId());

        Optional<PartyAggregation> findById = partyAggregationRepository.findById(payload.getPartyAggregationId());
        if (findById.isEmpty()) return;
        PartyAggregation partyAggregation = findById.get();
        Long aggregatedQuantity = aggregationRepository.countAllByPartyAggregationIdAndStatusIn(partyAggregation.getId(), Arrays.asList(AggregationStatus.CREATED, AggregationStatus.ERROR));
        if (partyAggregation.getQuantity() != aggregatedQuantity.intValue()) return;

        partyAggregation.setStatus(PartyAggregationStatus.CLOSED);
        partyAggregationRepository.save(partyAggregation);
    }

    public void partyAggregationFinished(Payload payload) {
        final PartyAggregation partyAggregation = partyAggregationRepository.findById(payload.getId()).orElse(null);
        if (partyAggregation == null) return;
        partyAggregation.setStatus(PartyAggregationStatus.CLOSED);

        final Organization organization = partyAggregation.getOrganization();
        final ProductGroup productGroup = organization.getProductGroup();
        final boolean isTobacco = productGroup == ProductGroup.Tobacco;
        int quantityLimit = TuronClient.aggregationLimit / (partyAggregation.getCapacity() + 1);
        final boolean isBlock = PackageType.BLOCK == PackageType.fromCode(partyAggregation.getUnit());

        if (isBlock) {
            MongoFilter aggregationFilter = new MongoFilter();
            aggregationFilter.setSize(quantityLimit);
            aggregationFilter.setPartyAggregationId(partyAggregation.getId());
            aggregationFilter.setOrganizationId(partyAggregation.getOrganizationId());
            aggregationFilter.setUnit(PackageType.BLOCK);
            int pageNumber = 0;
            boolean isNotLast;
            do {
                aggregationFilter.setPage(pageNumber++);
                // first page off marks
                Page<Block> page = blockRepository.findAllByFilter(aggregationFilter);
                if (page.isEmpty()) {
                    isNotLast = page.hasNext();
                    continue;
                }

                List<AggregationItem> aggregationItems = page.getContent().stream().map(aggregationItemConverter)
                        .filter(Objects::nonNull)
                        .toList();
                CreateAggregationResponse response = sentToTuron(partyAggregation, organization, pageNumber, isTobacco, aggregationItems);
                String reportId = response != null ? response.getReportId() : null;
                String errorMessage = response != null ? response.getErrorMessage() : null;
                if (errorMessage != null) {
                    partyAggregation.setErrorMessage(errorMessage);
                    partyAggregation.setStatus(PartyAggregationStatus.ERROR);
                }
                setReportInfo(Collections.emptyList(), page.getContent(), partyAggregation, organization, reportId, errorMessage);

                isNotLast = page.hasNext();
            } while (isNotLast);
        } else {
            AggregationFilter aggregationFilter = new AggregationFilter();
            aggregationFilter.setSize(quantityLimit);
            aggregationFilter.setPartyAggregationId(partyAggregation.getId());
            aggregationFilter.setOrganizationId(partyAggregation.getOrganizationId());
            int pageNumber = 0;
            boolean isNotLast;
            do {
                aggregationFilter.setPage(pageNumber);
                // first page off marks
                Page<Aggregation> page = aggregationRepository.findAllByFilter(aggregationFilter);
                if (!page.isEmpty()) {
                    List<AggregationItem> aggregationItems = page.getContent().stream().map(aggregationItemConverter)
                            .filter(Objects::nonNull)
                            .toList();
                    CreateAggregationResponse response = sentToTuron(partyAggregation, organization, pageNumber, isTobacco, aggregationItems);
                    String reportId = response != null ? response.getReportId() : null;
                    String errorMessage = response != null ? response.getErrorMessage() : null;
                    if (errorMessage != null) {
                        partyAggregation.setErrorMessage(errorMessage);
                        partyAggregation.setStatus(PartyAggregationStatus.ERROR);
                    }
                    setReportInfo(page.getContent(), Collections.emptyList(), partyAggregation, organization, reportId, errorMessage);
                }

                pageNumber++;
                isNotLast = page.hasNext();
            } while (isNotLast);
        }


        partyAggregationRepository.save(partyAggregation);
    }

    private void setReportInfo(Collection<Aggregation> aggregations, Collection<Block> blocks, PartyAggregation partyAggregation, Organization organization, String reportId, String errorMessage) {
        for (Aggregation aggregation : aggregations) {
            aggregation.setReportId(reportId);
            aggregation.setAggregationDate(LocalDateTime.now());
            aggregation.setStatus(AggregationStatus.CREATED);
            aggregation.setErrorMessage(partyAggregation.getErrorMessage());
            if (errorMessage != null) {
                aggregation.setStatus(AggregationStatus.ERROR);
            }
            aggregationRepository.save(aggregation);

            // add aggregation to warehouse production this includes aggregation history also
            if (AggregationStatus.ERROR != aggregation.getStatus()) {
                final WarehousePayload warehousePayload = new WarehousePayload();
                warehousePayload.setOrganizationId(organization.getId());
                warehousePayload.setOperationDate(LocalDateTime.now());
                warehousePayload.setOperation(WarehouseOperation.Production);
                warehousePayload.setNote("Произведено " + aggregation.getPrintCode());
                warehousePayload.setUnit(PackageType.fromCode(aggregation.getUnit()));
                warehousePayload.setAlcoholCount(BigDecimal.valueOf(aggregation.getQuantity().longValue()));
                warehousePayload.setProductId(aggregation.getProductId());
                warehousePayload.setSerialId(aggregation.getSerialId());
                warehousePayload.setCode(aggregation.getCode());
                warehousePayload.setAggregationStatus(aggregation.getStatus());
                kafkaProducer.sendMessage(KafkaConstants.WAREHOUSE_TOPIC, warehousePayload);
            }

            // notify current aggregation state
            pushNotificationService.sendPartyAggregation(aggregation.toPayload(), partyAggregation.getId());
        }
        for (Block aggregation : blocks) {
            aggregation.setReportId(reportId);
            aggregation.setAggregationDate(LocalDateTime.now());
            aggregation.setStatus(AggregationStatus.CREATED);
            aggregation.setErrorMessage(partyAggregation.getErrorMessage());
            if (errorMessage != null) {
                aggregation.setStatus(AggregationStatus.ERROR);
            }
            blockRepository.save(aggregation);

            // add aggregation to warehouse production this includes aggregation history also
            if (AggregationStatus.ERROR != aggregation.getStatus()) {
                final WarehousePayload warehousePayload = new WarehousePayload();
                warehousePayload.setOrganizationId(organization.getId());
                warehousePayload.setOperationDate(LocalDateTime.now());
                warehousePayload.setOperation(WarehouseOperation.Production);
                warehousePayload.setNote("Произведено " + aggregation.getPrintCode());
                warehousePayload.setUnit(PackageType.BLOCK);
                warehousePayload.setAlcoholCount(BigDecimal.valueOf(aggregation.getQuantity()));
                warehousePayload.setProductId(aggregation.getProductId());
                warehousePayload.setSerialId(aggregation.getSerialId());
                warehousePayload.setCode(aggregation.getCode());
                warehousePayload.setAggregationStatus(aggregation.getStatus());
                kafkaProducer.sendMessage(KafkaConstants.WAREHOUSE_TOPIC, warehousePayload);
            }

            // notify current aggregation state
            pushNotificationService.sendPartyAggregation(aggregation.toPayload(), partyAggregation.getId());
        }
    }

    private @Nullable CreateAggregationResponse sentToTuron(PartyAggregation partyAggregation, Organization organization, int pageNumber, boolean isTobacco, List<AggregationItem> aggregationItems) {
        final CreateAggregationRequest request = new CreateAggregationRequest(aggregationItems, organization.getTin());

        if (isTobacco) {
            request.setProductionLineId(partyAggregation.getId());
        }
        try {
            log.info("======================== PAGE {} PARTY ID {} COUNT {} CreateAggregationRequest {} ========================", pageNumber, partyAggregation.getId(), aggregationItems.size(), request);
            return sendAggregationToTuronTrace(request, organization.getProductGroup().getExtension(), organization.getTuronToken(), organization.getOmsId());
        } catch (Exception e) {
            return new CreateAggregationResponse(null, e.getMessage());
        }
    }

    public void exportReport(AggregationFilter filter) {
        PartyAggregation partyAggregation = partyAggregationRepository.findById(filter.getPartyAggregationId()).orElseThrow(notFound());
        if (PackageType.BLOCK.getCode() == partyAggregation.getUnit()) {
            MongoFilter mongoFilter = new MongoFilter();
            mongoFilter.setPartyAggregationId(filter.getPartyAggregationId());
            mongoFilter.setOrganizationId(filter.getOrganizationId());
            mongoFilter.setUnit(PackageType.BLOCK);
            mongoFilter.setFileType(filter.getFileType());
            mongoFilter.setCodeLength(filter.getCodeLength());
            ExportReportType exportReportType = FileType.CSV.equals(filter.getFileType()) ? ExportReportType.CSV_PARTY_AGGREGATION_BLOCK : ExportReportType.EXCEL_PARTY_AGGREGATION_BLOCK;
            exportReportService.export(mongoFilter, exportReportType, filter.getFileType(), "Party_Aggregation_Report");
        } else {
            filter.setUnit(PackageType.BOX);
            ExportReportType exportReportType = FileType.CSV.equals(filter.getFileType()) ? ExportReportType.CSV_PARTY_AGGREGATION : ExportReportType.EXCEL_PARTY_AGGREGATION;
            exportReportService.export(filter, exportReportType, filter.getFileType(), "Party_Aggregation_Report");
        }
    }
}
