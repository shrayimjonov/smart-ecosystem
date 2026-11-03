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
import uz.uzkassa.silen.domain.Organization;
import uz.uzkassa.silen.domain.Product;
import uz.uzkassa.silen.domain.mongo.Block;
import uz.uzkassa.silen.domain.mongo.Mark;
import uz.uzkassa.silen.domain.redis.RedisAggregation;
import uz.uzkassa.silen.domain.redis.RedisLastAggregationMarks;
import uz.uzkassa.silen.dto.CodeDTO;
import uz.uzkassa.silen.dto.filter.MongoFilter;
import uz.uzkassa.silen.dto.marking.BlockCreateDTO;
import uz.uzkassa.silen.dto.marking.BlockUpdateDTO;
import uz.uzkassa.silen.dto.mongo.BlockDTO;
import uz.uzkassa.silen.dto.mq.Payload;
import uz.uzkassa.silen.dto.mq.WarehousePayload;
import uz.uzkassa.silen.enumeration.*;
import uz.uzkassa.silen.exceptions.BadRequestException;
import uz.uzkassa.silen.exceptions.ErrorConstants;
import uz.uzkassa.silen.integration.asilbelgi.dto.CisInfoResponse;
import uz.uzkassa.silen.integration.turon.AggregationItem;
import uz.uzkassa.silen.integration.turon.CreateAggregationRequest;
import uz.uzkassa.silen.integration.turon.CreateAggregationResponse;
import uz.uzkassa.silen.integration.turon.UtilisationResponse;
import uz.uzkassa.silen.kafka.KafkaConstants;
import uz.uzkassa.silen.kafka.producer.KafkaProducer;
import uz.uzkassa.silen.rabbitmq.MqConstants;
import uz.uzkassa.silen.repository.mongo.BlockRepository;
import uz.uzkassa.silen.repository.mongo.MarkRepository;
import uz.uzkassa.silen.repository.pg.ProductRepository;
import uz.uzkassa.silen.repository.pg.SerialNumberRepository;
import uz.uzkassa.silen.repository.redis.RedisAggregationRepository;
import uz.uzkassa.silen.repository.redis.RedisLastAggregationMarksRepository;
import uz.uzkassa.silen.security.SecurityUtils;
import uz.uzkassa.silen.utils.Utils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BlockService extends BaseService {
    MarkRepository markRepository;
    BlockRepository blockRepository;
    ProductRepository productRepository;
    BillingPermissionService billingPermissionService;
    KafkaProducer<WarehousePayload> kafkaProducer;
    RedisAggregationRepository redisAggregationRepository;
    RedisLastAggregationMarksRepository redisLastAggregationMarksRepository;
    UtilisationService utilisationService;
    SerialNumberRepository serialNumberRepository;
    final static int errorCheckingLimit = 3;
    private final ExportReportService exportReportService;

    @Transactional
    public String create(BlockCreateDTO blockCreateDTO, final boolean fromUpdate) {
        if (blockCreateDTO.getMarks().size() != blockCreateDTO.getQuantity()) {
            throw badRequest("Неверное Количество КМ").get();
        }

        final Organization organization = getCurrentOrganizationAndValidateTokens();
        if (!blockCreateDTO.isOffline()) {
            getCurrentOrganizationAsilBelgiToken(organization.getTin());
        }
        final ProductGroup productGroup = organization.getProductGroup();
        final boolean isAlcohol = productGroup == ProductGroup.Alcohol;
        final boolean isSingle = organization.getAggregationMode() == AggregationMode.Single;

        if (isAlcohol) {
            billingPermissionService.checkIsAvailableCount(organization.getId(), organization.isDemoAllowed(), organization.getParentId(), productGroup, 1);
        }
        Block block;
        if (StringUtils.isEmpty(blockCreateDTO.getPrintCode())) {
            if (StringUtils.isEmpty(blockCreateDTO.getPartyId())) {
                throw badRequest("Заказ маркировок не выбран").get();
            }
            block = blockRepository.findFirst1ByPartyIdAndUsedFalse(blockCreateDTO.getPartyId()).orElseThrow(badRequest("По выбранному заказу код не найден"));
        } else {
            final String blockCode = Utils.clearPrintCode(blockCreateDTO.getPrintCode());
            block = blockRepository.findFirstByCode(blockCode).orElseThrow(notFound("Код блока не найден: " + blockCreateDTO.getPrintCode()));
        }
        if (!block.isBlock()) {
            throw new BadRequestException("Код маркировок сканировано", ErrorConstants.DEFAULT_ERROR_CODE);
        }
        if (!AggregationStatus.DRAFT.equals(block.getStatus())) {
            throw new BadRequestException("Код блок уже использован", ErrorConstants.AGGREGATION_USED_ERROR_CODE);
        }

        // validate true token and throw
//        getCurrentOrganizationAsilBelgiToken(organization.getTin());

        block.setQuantity(blockCreateDTO.getQuantity());
        block.setStatus(AggregationStatus.IN_PROGRESS);
        block.setLogin(getCurrentUserLogin());
        block.setDescription(blockCreateDTO.getDescription());
        block.setLogin(Optional.ofNullable(getCurrentUserLogin()).orElseGet(blockCreateDTO::getLogin));
        block.setProductionDate(LocalDateTime.now());
        block.setUsed(true);
        block.setLastModifiedDate(LocalDateTime.now());
        block.setSerialId(blockCreateDTO.getSerialId());
        if (block.getSerialId() != null) {
            serialNumberRepository.findById(block.getSerialId())
                    .ifPresent(serialNumber -> {
                        block.setSerialNumber(serialNumber.getSerialNumber());
                    });
        }
        block.setOrganizationName(organization.getName());
        blockRepository.save(block);

        // save marks to redis
        final RedisAggregation redisAggregation = new RedisAggregation();
        redisAggregation.setId(block.getCode());
        redisAggregation.setCode(block.getCode());
        redisAggregation.setMarks(blockCreateDTO.getMarks());
        redisAggregation.setProductId(block.getProductId());
        redisAggregation.setOrganizationId(organization.getId());
        redisAggregation.setProductionDate(block.getProductionDate());
        redisAggregation.setSendUtilise(true);
        redisAggregation.setAggregationMode(organization.getAggregationMode());
        redisAggregation.setOffline(blockCreateDTO.isOffline());
        if (block.getSerialNumber() != null) {
            redisAggregation.setSerialNumber(block.getSerialNumber());
        }
        if (block.getExpirationDate() != null) {
            redisAggregation.setExpirationDate(block.getExpirationDate());
        }
        redisAggregationRepository.save(redisAggregation);
        redisLastAggregationMarksRepository.deleteById(block.getCode());

        if (isSingle) {
            //send to save mongo
            afterCommitExecutor.execute(() -> {
                redisLastAggregationMarksRepository.deleteAll(redisLastAggregationMarksRepository.findAllBySessionIdAndProductId(getCurrentSessionId(), block.getProductId()));
                rabbitMqProducer.sendForSaveBlockMarks(new Payload(block.getCode()));
            });
        } else {
            List<String> lastScannedMarks = blockCreateDTO.getMarks().stream()
                    .map(Utils::clearPrintCode)
                    .toList();
            Iterable<RedisLastAggregationMarks> allById = redisLastAggregationMarksRepository.findAllById(lastScannedMarks);
            allById.forEach(redisLastAggregationMarks -> {
                redisLastAggregationMarks.setReserved(true);
                redisLastAggregationMarksRepository.save(redisLastAggregationMarks);
            });
            if (isAlcohol) {
                billingPermissionService.decrementAggregationCountAndSave(organization.getId(), organization.isDemoAllowed(), productGroup, 1);
            }
        }

        return fromUpdate ? block.getId() : block.getPrintCode();
    }

    @Transactional
    public String update(String id, BlockUpdateDTO updateDTO) {
        Block oldAggregation = blockRepository.findById(id).orElseThrow(notFound("Код не найден"));
        if (oldAggregation.getStatus().checkingIfAggregationNotAbleToUpdate())
            throw badRequest("Код не сможете агрегироват. Статус: " + oldAggregation.getStatus()).get();
        if (oldAggregation.getParentCode() != null) {
            throw badRequest("Ошибка! Код состоит в агрегации. КА: " + oldAggregation.getParentCode()).get();
        }
        BlockCreateDTO blockCreateDTO = new BlockCreateDTO();
        blockCreateDTO.setUnit(oldAggregation.getUnit());
        blockCreateDTO.setQuantity(updateDTO.getChildMarks().size());
        blockCreateDTO.setPrintCode(updateDTO.getAggregation());
        blockCreateDTO.setDescription(oldAggregation.getPrintCode());
        blockCreateDTO.setMarks(updateDTO.getChildMarks());
        blockCreateDTO.setOffline(updateDTO.isOffline());
        blockCreateDTO.setProductionDate(Optional.ofNullable(oldAggregation.getProductionDate()).orElse(LocalDateTime.now()).toLocalDate());
        if (oldAggregation.getExpirationDate() != null) {
            blockCreateDTO.setExpirationDate(oldAggregation.getExpirationDate().toLocalDate());
        }
        blockCreateDTO.setType(oldAggregation.getAggregationType());
        blockCreateDTO.setSerialId(oldAggregation.getId());

        String response = create(blockCreateDTO, true);

        String utilizedMessage = localize("code.utilized.message", getCurrentUserLogin(), LocalDateTime.now(), oldAggregation.getCode());
        oldAggregation.setStatus(AggregationStatus.UTILIZED);
        oldAggregation.setDescription(utilizedMessage);
        oldAggregation.setAggregationDate(LocalDateTime.now());
        oldAggregation.setQuantity(0);
        oldAggregation.setLogin(getCurrentUserLogin());
        blockRepository.save(oldAggregation);
        markRepository.findAllByParentCode(oldAggregation.getCode()).forEach(oldMarkItem -> {
            oldMarkItem.setUsed(false);
            oldMarkItem.setParentCode(null);
            oldMarkItem.setStatus(AggregationStatus.DRAFT);
            markRepository.save(oldMarkItem);
        });
        WarehousePayload warehousePayload = new WarehousePayload();
        warehousePayload.setOrganizationId(getCurrentOrganizationId());
        warehousePayload.setOperationDate(LocalDateTime.now());
        warehousePayload.setOperation(WarehouseOperation.Utilized);
        warehousePayload.setNote(utilizedMessage);
        warehousePayload.setUnit(PackageType.BLOCK);
        warehousePayload.setAlcoholCount(BigDecimal.valueOf(oldAggregation.getQuantity()));
        warehousePayload.setProductId(oldAggregation.getProductId());
        warehousePayload.setSerialId(oldAggregation.getSerialId());
        warehousePayload.setCode(oldAggregation.getCode());
        kafkaProducer.sendMessage(KafkaConstants.WAREHOUSE_TOPIC, warehousePayload);

        if (AggregationType.PRINTING == oldAggregation.getAggregationType()) {
            afterCommitExecutor.execute(() -> rabbitMqProducer.sendAggregationStatusChangedEvent(oldAggregation.toPayload()));
        }
        return response;
    }


    public Page<BlockDTO> findAllByFilter(MongoFilter filter) {
        baseFilter(filter);
        filter.setOrderBy("lastModifiedDate");
        filter.setSortOrder(SortTypeEnum.desc);
        return blockRepository.findAllByFilter(filter).map(Block::toListBlockDto);
    }

    public BigDecimal getStats(final MongoFilter filter) {
        baseFilter(filter);
        return blockRepository.findAllByFilterStats(filter);
    }

    private void baseFilter(MongoFilter filter) {
        if (StringUtils.isNotEmpty(filter.getSearch())) {
            if (Utils.isMark(filter.getSearch())) {
                final String code = Utils.clearPrintCode(filter.getSearch());
                Optional<Mark> firstByCode = markRepository.findFirstByCode(code);
                firstByCode.ifPresent(mark -> {
                    if (mark.getUnit() == null) {
                        String parentOrCode = Optional.ofNullable(mark.getParentCode()).orElse(mark.getCode());
                        filter.setSearch(parentOrCode);
                    } else {
                        filter.setSearch(mark.getCode());
                    }
                });
            }
        }
        filter.setUnit(PackageType.BLOCK);
        if (getCurrentOrganizationId() != null) {
            filter.setOrganizationId(getCurrentOrganizationId());
        }
    }

    public BlockDTO getOne(String id) {
        Block aggregation = blockRepository.findById(id).orElseThrow(notFound());
        BlockDTO dto = aggregation.toBlockDto();

        Optional<RedisAggregation> firstByCode = redisAggregationRepository.findFirstByCode(dto.getCode());
        if (firstByCode.isPresent()) {
            Set<String> cleanCodes = firstByCode.get().getMarks().stream().map(Utils::clearPrintCode).collect(Collectors.toSet());
            List<Mark> marks = markRepository.findAllByCodeIn(cleanCodes);
            for (Mark mark : marks) {
                dto.getCleanMarks().add(mark.getCode());
                dto.getMarks().add(mark.getPrintCode());
                if (AggregationStatus.DROPOUT.equals(mark.getStatus())) {
                    dto.getDropoutMarks().add(mark.getPrintCode());
                }
                dto.getMarkDTOs().add(new CodeDTO(mark.getId(), mark.getCode(), mark.getPrintCode(), mark.getStatus()));
            }
        } else {
            if (aggregation.getProductId() != null) {
                Product product = productRepository.findById(aggregation.getProductId()).orElseThrow(notFound());
                dto.setProductId(product.getId());
                dto.setProductName(product.getVisibleName());
                dto.setBarcode(product.getBarcode());
                dto.setParentBarcode(product.getParentBarcode());
            }
            List<Mark> marks = markRepository.findAllByParentCode(dto.getCode());
            for (Mark mark : marks) {
                dto.getCleanMarks().add(mark.getCode());
                dto.getMarks().add(mark.getPrintCode());
                if (AggregationStatus.DROPOUT.equals(mark.getStatus())) {
                    dto.getDropoutMarks().add(mark.getPrintCode());
                }
                dto.getMarkDTOs().add(new CodeDTO(mark.getId(), mark.getCode(), mark.getPrintCode(), mark.getStatus()));
            }
        }
        return dto;
    }

    @Transactional
    public void sendAggregation(final String code) {
        final Optional<Block> aggregationOptional = blockRepository.findFirstByCode(code);
        if (aggregationOptional.isEmpty() || !EnumSet.of(AggregationStatus.ERROR, AggregationStatus.APPLIED, AggregationStatus.UPDATED, AggregationStatus.IN_PROGRESS).contains(aggregationOptional.get().getStatus())) {
            return;
        }
        final Block block = aggregationOptional.get();

        try {
            final Organization organization = organizationRepository.findById(block.getOrganizationId()).orElseThrow(notFound());
            final ProductGroup productGroup = organization.getProductGroup();
            final boolean isAlcohol = productGroup == ProductGroup.Alcohol;
            log.debug("SendAggregation {} ================= STATUS {} =============================", block.getCode(), block.getStatus());

            if (isAlcohol) {
                billingPermissionService.checkIsAvailableCount(organization.getId(), organization.isDemoAllowed(), organization.getParentId(), productGroup, 1);
            }

            final List<Mark> marks = markRepository.findAllByParentCode(block.getCode());
            Set<String> snTins = marks.stream().map(Mark::getCode).collect(Collectors.toSet());

            if (CollectionUtils.isEmpty(snTins)) {
                throw new BadRequestException("Коды маркировки отсутствуют");
            } else if (snTins.size() != block.getQuantity()) {
                throw new BadRequestException("Количество не соответствует");
            }
            final AggregationItem aggregationItem = new AggregationItem();
            aggregationItem.setAggregatedItemsCount(block.getQuantity());
            aggregationItem.setAggregationUnitCapacity(block.getQuantity());
            aggregationItem.setAggregationType(AggregationType.AGGREGATION);
            aggregationItem.setUnitSerialNumber(block.getCode());
            aggregationItem.setSntins(snTins);
            final CreateAggregationRequest request = new CreateAggregationRequest(Collections.singletonList(aggregationItem), organization.getTin());
            if (ProductGroup.Tobacco.equals(organization.getProductGroup())) {
                request.setProductionLineId(ProductGroup.Tobacco.getNameRu());
            }
            final CreateAggregationResponse response = sendAggregationToTuronTrace(request, organization.getProductGroup().getExtension(), organization.getTuronToken(), organization.getOmsId());
            if (response == null) {
                throw badRequest("Timeout").get();
            } else {
                block.setReportId(response.getReportId());
                block.setAggregationDate(LocalDateTime.now());
                block.setStatus(AggregationStatus.SENT);
                blockRepository.save(block);
                if (isAlcohol) {
                    billingPermissionService.decrementAggregationCountAndSave(organization.getId(), organization.isDemoAllowed(), productGroup, 1);
                }
            }
        } catch (Exception e) {
            log.error("Error sending aggregation", e);
            block.setErrorMessage(e.getMessage());
            block.setStatus(AggregationStatus.ERROR);
            blockRepository.save(block);
        }
        if (AggregationType.PRINTING != block.getAggregationType()) return;
        // notify current aggregation state
        rabbitMqProducer.sendAggregationStatusChangedEvent(block.toPayload());
    }

    @Transactional
    public AggregationStatus checkAndUtiliseById(String id) {
        Block block = blockRepository.findById(id).orElseThrow(notFound());
        checkBlocks(block.getOrganizationId(), Collections.singleton(block));
        return block.getStatus();
    }

    @Transactional
    public void checkAndUtiliseByCode(String code) {
        Block block = blockRepository.findFirstByCode(code).orElseThrow(notFound());
        checkBlocks(block.getOrganizationId(), Collections.singleton(block));
    }

    //@Transactional
    public Map<String, String> checkAndUtiliseByBatch(List<String> blockIds) {
        List<Block> blockList = blockRepository.findAllById(blockIds);
        final Map<String, List<Block>> groupByOrganizationId = blockList.stream().collect(Collectors.groupingBy(Block::getOrganizationId));
        for (String organizationId : groupByOrganizationId.keySet()) {
            List<Block> aggregations = groupByOrganizationId.get(organizationId);

            if (CollectionUtils.isEmpty(aggregations)) {
                log.error("List is empty {}", organizationId);
                continue;
            }
            checkBlocks(organizationId, aggregations);
        }
        return null;
    }

    public void checkBlocks(final String organizationId, final Collection<Block> allByFilter) {
        final Set<String> aggregationCodes = allByFilter.stream().map(Block::getCode).collect(Collectors.toSet());
        if (aggregationCodes.isEmpty() || organizationId == null) {
            log.info("Block codes is empty");
            return;
        }
        final Organization organization = organizationRepository.findById(organizationId).orElse(null);
        if (organization == null) {
            return;
        }
//        if (organization.getTrueToken() == null) {
//            log.error("ORGANIZATION {} HAS NO TRUE TOKEN", organization.getName());
//            return;
//        }
        final String currentOrganizationAsilBelgiToken = getCurrentOrganizationAsilBelgiTokenOrNul(organization.getTin());
        if (currentOrganizationAsilBelgiToken == null) {
            log.info("ORGANIZATION {} HAS NO TRUE TOKEN", organization.getName());
            return;
        }
        final boolean isAppliances = ProductGroup.Appliances == organization.getProductGroup();

        Map<String, CisInfoResponse> cisInfoMap = new HashMap<>();
        try {
            Map<String, CisInfoResponse> stringCisInfoResponseMap = checkFromAsilBelgisi(aggregationCodes, currentOrganizationAsilBelgiToken);
            cisInfoMap.putAll(stringCisInfoResponseMap);
        } catch (Exception e) {
            log.error("error checking aggregations from asilbelgisi", e);
        }

        String time = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        for (Block aggregation : allByFilter) {
            final String code = aggregation.getCode();
            if (errorCheckingLimit <= aggregation.getChecked()) {
                log.info("Aggregation {} checked already 3 times", code);
                continue;
            }
            // check cisInfoResponse
            final String errorMessage = checkResponse(code, cisInfoMap, time);
            if (errorMessage != null) {
                aggregation.setErrorMessage(errorMessage);
                aggregation.setStatus(AggregationStatus.ERROR);
            } else {
                aggregation.setErrorMessage(localize("checked.success", LocalDateTime.now(), cisInfoMap.get(code).getCisInfo().getStatusName()));
                aggregation.setStatus(AggregationStatus.CREATED);
                blockRepository.save(aggregation);
                continue;
            }

            // collect children codes
            final Set<String> codes = new HashSet<>();
            final Set<String> utiliseCodes = new HashSet<>();
            List<Mark> childrenMark = markRepository.findAllByParentCode(code);
            for (Mark mark : childrenMark) {
                codes.add(mark.getCode());
                if (mark.getUnit() == null && !isAppliances) {
                    String printCode = Utils.fillWithUniCode(mark.getPrintCode());
                    utiliseCodes.add(printCode);
                }
            }
            aggregation.setChecked(aggregation.getChecked() + 1);
            //check children count
            if (codes.isEmpty()) {
                final RedisAggregation firstByCode = redisAggregationRepository.findFirstByCode(code).orElse(null);
                if (firstByCode != null && CollectionUtils.isNotEmpty(firstByCode.getMarks())) {
                    for (String childPrintCode : firstByCode.getMarks()) {
                        final String cleanCode = Utils.clearPrintCode(childPrintCode);
                        codes.add(cleanCode);
                        String printCode = Utils.fillWithUniCode(childPrintCode);
                        utiliseCodes.add(printCode);
                        markRepository.findFirstByCode(cleanCode)
                                .ifPresent(mark -> {
                                    if (mark.getParentCode() == null) {
                                        mark.setParentCode(code);
                                        mark.setUsed(true);
                                        mark.setStatus(AggregationStatus.CREATED);
                                        markRepository.save(mark);
                                    } else {
                                        log.error("parent {} children {} already parent exists {}", code, cleanCode, mark.getParentCode());
                                    }
                                });
                    }
                } else {
                    aggregation.setStatus(AggregationStatus.ERROR);
                    aggregation.setErrorMessage("КМ не найден");
                    blockRepository.save(aggregation);
                    log.error("Aggregation {} children is empty", code);
                    continue;
                }
            }
            if (aggregation.getQuantity() != codes.size()) {
                aggregation.setStatus(AggregationStatus.ERROR);
                aggregation.setErrorMessage("Неверное Количество КМ");
                blockRepository.save(aggregation);
                log.error("Aggregation {} marks count not equal", code);
                continue;
            }
            // utilise children
            if (CollectionUtils.isNotEmpty(utiliseCodes)) {
                String serialNumber = aggregation.getSerialNumber() != null ? aggregation.getSerialNumber() : null;
                try {
                    utiliseCodes.add(aggregation.getPrintCode());
                    UtilisationResponse utilisationResponse = utilisationService.create(utiliseCodes,
                            organization, aggregation.getProductionDate(),
                            aggregation.getExpirationDate(),
                            serialNumber
                    );
                    aggregation.setUtilisationTime(LocalDateTime.now());
                    if (utilisationResponse == null) {
                        aggregation.setErrorMessage(localize("checked.error", time, "Нанесения не прошло"));
                        aggregation.setStatus(AggregationStatus.ERROR);
                    } else if (utilisationResponse.getErrorMessage() != null) {
                        aggregation.setErrorMessage(localize("checked.error", time, utilisationResponse.getErrorMessage()));
                        aggregation.setStatus(AggregationStatus.ERROR);
                    } else {
                        aggregation.setUtilisationId(utilisationResponse.getReportId());
                        aggregation.setErrorMessage(localize("mark.sent.to.utilise", aggregation.getUtilisationTime()));
                        aggregation.setStatus(AggregationStatus.APPLIED);
                    }
                } catch (Exception e) {
                    aggregation.setErrorMessage(e.getMessage());
                    log.error("Error occurred time utilising", e);
                    if (e.getMessage().contains("Specified array of MC has")) {
                        aggregation.setStatus(AggregationStatus.APPLIED);
                    } else {
                        aggregation.setStatus(AggregationStatus.ERROR);
                    }
                }
            }
            if (AggregationStatus.APPLIED == aggregation.getStatus()) {
                rabbitMqProducer.resendBlockToTuron(aggregation.getCode(), MqConstants.RESEND_AGGREGATION_DELAY);
            }
            blockRepository.save(aggregation);
            if (AggregationType.PRINTING == aggregation.getAggregationType()) {
                rabbitMqProducer.sendAggregationStatusChangedEvent(aggregation.toPayload());
            }
        }
    }

    public void exportReport(MongoFilter filter) {
        filter.setUnit(PackageType.BLOCK);
        String organizationId = Optional.ofNullable(filter.getOrganizationId()).orElse(SecurityUtils.getCurrentOrganizationId());
        filter.setOrganizationId(organizationId);
        ExportReportType exportReportType = FileType.CSV.equals(filter.getFileType()) ? ExportReportType.CSV_MARK_BLOCK : ExportReportType.EXCEL_MARK_BLOCK;
        exportReportService.export(filter, exportReportType, filter.getFileType(), "BlockReport");
    }
}
