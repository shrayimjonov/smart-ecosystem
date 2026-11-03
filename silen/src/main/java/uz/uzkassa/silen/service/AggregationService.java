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
import uz.uzkassa.silen.domain.Aggregation;
import uz.uzkassa.silen.domain.Organization;
import uz.uzkassa.silen.domain.mongo.Mark;
import uz.uzkassa.silen.domain.redis.RedisAggregation;
import uz.uzkassa.silen.domain.redis.RedisLastAggregationMarks;
import uz.uzkassa.silen.dto.CodeDTO;
import uz.uzkassa.silen.dto.filter.AggregationFilter;
import uz.uzkassa.silen.dto.marking.*;
import uz.uzkassa.silen.dto.mq.Payload;
import uz.uzkassa.silen.dto.mq.WarehousePayload;
import uz.uzkassa.silen.enumeration.*;
import uz.uzkassa.silen.exceptions.BadRequestException;
import uz.uzkassa.silen.exceptions.ErrorConstants;
import uz.uzkassa.silen.integration.TuronClient;
import uz.uzkassa.silen.integration.asilbelgi.dto.CisInfo;
import uz.uzkassa.silen.integration.asilbelgi.dto.CisInfoResponse;
import uz.uzkassa.silen.integration.turon.*;
import uz.uzkassa.silen.kafka.KafkaConstants;
import uz.uzkassa.silen.kafka.producer.KafkaProducer;
import uz.uzkassa.silen.rabbitmq.MqConstants;
import uz.uzkassa.silen.repository.mongo.MarkRepository;
import uz.uzkassa.silen.repository.pg.AggregationRepository;
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

/**
 * Service Implementation for managing {@link Aggregation}.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AggregationService extends BaseService {
    TuronClient turonClient;
    MarkRepository markRepository;
    UtilisationService utilisationService;
    AggregationRepository aggregationRepository;
    RedisAggregationRepository redisAggregationRepository;
    RedisLastAggregationMarksRepository redisLastAggregationMarksRepository;
    BillingPermissionService billingPermissionService;
    KafkaProducer<WarehousePayload> kafkaProducer;
    SerialNumberRepository serialNumberRepository;
    ExportReportService exportReportService;

    @Transactional
    public String create(final AggregationCreateDTO dto, final boolean fromUpdate) {
        final Organization organization = Optional.ofNullable(dto.getOrganizationId())
                .flatMap(organizationRepository::findById)
                .orElseGet(this::getCurrentOrganizationAndValidateTokens);
        if (!dto.isOffline()) {
            getCurrentOrganizationAsilBelgiToken(organization.getTin());
        }
        final ProductGroup productGroup = organization.getProductGroup();
        final boolean isAlcohol = productGroup == ProductGroup.Alcohol;
        final boolean isPharma = productGroup == ProductGroup.Pharma;
        final boolean isAppliances = productGroup == ProductGroup.Appliances;
        final boolean isSingle = organization.getAggregationMode() == AggregationMode.Single;

        if (dto.getMarks().size() != dto.getQuantity()) {
            throw badRequest("Неверное Количество КМ").get();
        }
        if (isPharma) {
            if (dto.getSerialId() == null)
                throw badRequest("Серия не выбрано").get();
            if (dto.getProductionDate() == null)
                throw badRequest("Дата производства не определена").get();
            if (dto.getExpirationDate() == null)
                throw badRequest("Срок годности не определена").get();
        }

        if (isAlcohol) {
            billingPermissionService.checkIsAvailableCount(organization.getId(), organization.isDemoAllowed(), organization.getParentId(), productGroup, 1);
        } else if (isPharma) {
            if (organization.getTypes().contains(OrganizationType.Importer)) {
                billingPermissionService.checkIsAvailableCount(organization.getId(), organization.isDemoAllowed(), organization.getParentId(), productGroup, dto.getQuantity());
            }
        }

        Aggregation aggregation;
        if (StringUtils.isNotEmpty(dto.getPrintCode())) {
            final String code20 = Utils.clearPrintCode(dto.getPrintCode());
            final String code18 = Utils.clearAggregationCode(dto.getPrintCode());
            aggregation = aggregationRepository.findFirstByCodeOrCode(code18, code20).orElseThrow(notFound("Код агрегации не найден: " + dto.getPrintCode()));
            if (aggregation.getStatus() != AggregationStatus.DRAFT) {
                throw new BadRequestException("Код агрегации уже использован", ErrorConstants.AGGREGATION_USED_ERROR_CODE);
            }
            // aggregation code changed from 18 to 20
            aggregation.setCode(code20);
        } else {
            aggregation = generateAggregation(dto.getUnit(), organization.getId(), dto.getGcp());
        }
        aggregation.setProductId(dto.getProductId());
        aggregation.setQuantity(dto.getQuantity());
        aggregation.setSerialId(dto.getSerialId());
        aggregation.setLogin(Optional.ofNullable(getCurrentUserLogin()).orElseGet(dto::getLogin));
        aggregation.setProductionDate(LocalDateTime.now());
        aggregation.setAggregationType(dto.getType());
        aggregation.setStatus(AggregationStatus.IN_PROGRESS);
        aggregation.setPartyAggregationId(dto.getAggregationPartyId());
        aggregation.setDescription(dto.getDescription());
        if (isPharma) {
            LocalDateTime currentTime = LocalDateTime.now();
            if (dto.getProductionDate() != null) {
                aggregation.setProductionDate(Utils.getWithServerHour(dto.getProductionDate(), currentTime));
            }
            if (dto.getExpirationDate() != null) {
                aggregation.setExpirationDate(Utils.getWithServerHour(dto.getExpirationDate(), currentTime));
            }
        }

        aggregationRepository.save(aggregation);

        // save marks to redis
        final RedisAggregation redisAggregation = new RedisAggregation();
        redisAggregation.setId(aggregation.getCode());
        redisAggregation.setCode(aggregation.getCode());
        redisAggregation.setMarks(dto.getMarks());
        redisAggregation.setProductId(dto.getProductId());
        if (dto.getSerialId() != null) {
            serialNumberRepository.findById(dto.getSerialId())
                    .ifPresent(serialNumber -> redisAggregation.setSerialNumber(serialNumber.getSerialNumber()));
        }
        if (aggregation.getExpirationDate() != null) {
            redisAggregation.setExpirationDate(aggregation.getExpirationDate());
        }
        redisAggregation.setProductionDate(aggregation.getProductionDate());
        redisAggregation.setOrganizationId(organization.getId());
        redisAggregation.setSendUtilise(!isAppliances);
        redisAggregation.setAggregationMode(organization.getAggregationMode());
        redisAggregation.setOffline(dto.isOffline());
        redisAggregationRepository.save(redisAggregation);

        List<String> lastScannedMarks = dto.getMarks().stream()
                .map(Utils::clearPrintCode)
                .toList();
        Iterable<RedisLastAggregationMarks> allById = redisLastAggregationMarksRepository.findAllById(lastScannedMarks);
        for (RedisLastAggregationMarks redisLastAggregationMarks : allById) {
            redisLastAggregationMarks.setReserved(true);
            redisLastAggregationMarksRepository.save(redisLastAggregationMarks);
        }
        redisLastAggregationMarksRepository.deleteById(aggregation.getCode());
        if (isSingle) {
            //send to save mongo
            afterCommitExecutor.execute(() -> rabbitMqProducer.sendForSaveAggregationMarks(new Payload(aggregation.getCode())));
        } else {
            if (isAlcohol) {
                billingPermissionService.decrementAggregationCountAndSave(organization.getId(), organization.isDemoAllowed(), productGroup, 1);
            } else if (isPharma) {
                if (organization.getTypes().contains(OrganizationType.Importer)) {
                    billingPermissionService.decrementAggregationCountAndSave(organization.getId(), organization.isDemoAllowed(), productGroup, aggregation.getQuantity());
                }
            }
        }
        return fromUpdate ? aggregation.getId() : aggregation.getPrintCode();
    }

    @Transactional
    public String update(String id, AggregationUpdateDTO aggregationUpdateDTO) {
        final Aggregation oldAggregation = aggregationRepository.findById(id).orElseThrow(notFound("Код не найден"));
        if (oldAggregation.getStatus().checkingIfAggregationNotAbleToUpdate())
            throw badRequest("Код не сможете агрегироват. Статус: " + oldAggregation.getStatus()).get();
        if (oldAggregation.getParentCode() != null) {
            throw badRequest("Ошибка! Код состоит в агрегации. КА: " + oldAggregation.getParentCode()).get();
        }

        AggregationCreateDTO aggregationDTO = new AggregationCreateDTO();
        aggregationDTO.setProductId(oldAggregation.getProductId());
        aggregationDTO.setGcp(oldAggregation.getGcp());
        aggregationDTO.setSerialId(oldAggregation.getSerialId());
        aggregationDTO.setUnit(PackageType.fromCode(oldAggregation.getUnit()));
        aggregationDTO.setQuantity(aggregationUpdateDTO.getChildMarks().size());
        aggregationDTO.setPrintCode(aggregationUpdateDTO.getAggregation());
        aggregationDTO.setDescription(oldAggregation.getPrintCode());
        aggregationDTO.setMarks(aggregationUpdateDTO.getChildMarks());
        aggregationDTO.setOffline(aggregationUpdateDTO.isOffline());
        aggregationDTO.setProductionDate(Optional.ofNullable(oldAggregation.getProductionDate()).orElse(LocalDateTime.now()).toLocalDate());
        if (oldAggregation.getExpirationDate() != null) {
            aggregationDTO.setExpirationDate(oldAggregation.getExpirationDate().toLocalDate());
        }
        aggregationDTO.setType(aggregationUpdateDTO.getType());
        aggregationDTO.setAggregationPartyId(oldAggregation.getPartyAggregationId());

        String response = create(aggregationDTO, true);

        if (StringUtils.isEmpty(response)) {
            return null;
        }

        final Set<String> newChildren = aggregationUpdateDTO.getChildMarks()
                .stream()
                .map(Utils::clearPrintCode)
                .collect(Collectors.toSet());
        List<String> oldAggregationCodes = Utils.clearAggregationPrintCode(oldAggregation.getPrintCode());

        List<Aggregation> aggregationList = aggregationRepository.findAllByParentCodeIn(oldAggregationCodes);
        if (CollectionUtils.isNotEmpty(aggregationList)) {
            for (Aggregation child : aggregationList) {
                if (!newChildren.contains(child.getCode())) {
                    child.setParentCode(null);
                    aggregationRepository.save(child);
                }
            }
        }

        List<Mark> allByParentCodeIn = markRepository.findAllByParentCodeIn(oldAggregationCodes);
        if (CollectionUtils.isNotEmpty(allByParentCodeIn)) {
            for (Mark child : allByParentCodeIn) {
                if (!newChildren.contains(child.getCode())) {
                    child.setParentCode(null);
                    if (!AggregationStatus.DROPOUT.equals(child.getStatus())) {
                        child.setStatus(AggregationStatus.DRAFT);
                        child.setUsed(false);
                    }
                    markRepository.save(child);
                }
            }
        }

        // oldAggregation
        int qty = oldAggregation.getQuantity();
        oldAggregation.setQuantity(0);
        oldAggregation.setStatus(AggregationStatus.UTILIZED);
        oldAggregation.setPartyAggregationId(null);
        String utilizedMessage = localize("code.utilized.message", getCurrentUserLogin(), LocalDateTime.now(), oldAggregation.getCode());
        oldAggregation.setErrorMessage(utilizedMessage);
        oldAggregation.setLogin(getCurrentUserLogin());
        oldAggregation.setCode(Utils.clearAggregationCode20(oldAggregation.getPrintCode()));
        aggregationRepository.save(oldAggregation);

        WarehousePayload warehousePayload = new WarehousePayload();
        warehousePayload.setOrganizationId(getCurrentOrganizationId());
        warehousePayload.setOperationDate(LocalDateTime.now());
        warehousePayload.setOperation(WarehouseOperation.Utilized);
        warehousePayload.setNote(utilizedMessage);
        warehousePayload.setUnit(oldAggregation.getPackageType());
        warehousePayload.setAlcoholCount(BigDecimal.valueOf(qty));
        warehousePayload.setProductId(oldAggregation.getProductId());
        warehousePayload.setSerialId(oldAggregation.getSerialId());
        warehousePayload.setCode(oldAggregation.getCode());
        kafkaProducer.sendMessage(KafkaConstants.WAREHOUSE_TOPIC, warehousePayload);

        if (AggregationType.PRINTING == oldAggregation.getAggregationType()) {
            afterCommitExecutor.execute(() -> rabbitMqProducer.sendAggregationStatusChangedEvent(oldAggregation.toPayload()));
        }
        return response;
    }

    public Page<AggregationListDTO> findAllByFilter(AggregationFilter filter) {
        baseFilter(filter);
        return aggregationRepository.findAllByFilter(filter).map(aggregation -> {
            AggregationListDTO aggregationListDTO = new AggregationListDTO();
            aggregationListDTO.setId(aggregation.getId());
            aggregationListDTO.setQuantity(aggregation.getQuantity());
            aggregationListDTO.setPrintCode(aggregation.getPrintCode());
            aggregationListDTO.setLogin(aggregation.getLogin());
            aggregationListDTO.setUnitName(PackageType.fromCode(aggregation.getUnit()).getNameRu());
            aggregationListDTO.setLastModifiedDate(aggregation.getLastModifiedDate());
            aggregationListDTO.setTuronAggregationId(aggregation.getReportId());
            aggregationListDTO.setSerialId(aggregation.getSerialId());
            aggregationListDTO.setGcp(aggregation.getGcp());
            aggregationListDTO.setDescription(aggregation.getDescription());
            if (aggregation.getStatus() != null) {
                aggregationListDTO.setStatus(aggregation.getStatus());
                aggregationListDTO.setStatusName(aggregation.getStatus().getText());
            }
            if (aggregation.getProduct() != null) {
                aggregationListDTO.setProductName(aggregation.getProduct().getVisibleName());
                aggregationListDTO.setBarcode(aggregation.getProduct().getBarcode());
            }
            if (isAgency()) {
                if (aggregation.getOrganization() != null) {
                    aggregationListDTO.setOrganizationName(aggregation.getOrganization().getName());
                }
            }
            if (aggregation.getSerialNum() != null) {
                aggregationListDTO.setSerial(aggregation.getSerialNum().toSelectItem());
            }
            if (!AggregationStatus.DRAFT.equals(aggregation.getStatus()) && aggregation.getAggregationType() != null) {
                aggregationListDTO.setType(aggregation.getAggregationType());
                aggregationListDTO.setTypeName(aggregation.getAggregationType().getText());
            }
            return aggregationListDTO;
        });
    }

    public AggregationDTO findOne(String id) {
        Aggregation aggregation = aggregationRepository.findById(id).orElseThrow(notFound());
        AggregationDTO dto = aggregation.toDto(isAgency());

        Optional<RedisAggregation> firstByCode = redisAggregationRepository.findFirstByCode(dto.getCode());
        if (firstByCode.isPresent()) {
            Set<String> cleanCodes = firstByCode.get().getMarks().stream().map(Utils::clearPrintCode).collect(Collectors.toSet());
            List<Aggregation> aggregationList = aggregationRepository.findAllByCodeIn(cleanCodes);
            for (Aggregation child : aggregationList) {
                dto.getMarkDTOs().add(new CodeDTO(child.getId(), child.getCode(), child.getPrintCode(), child.getStatus()));
                dto.getMarks().add(child.getCode());
            }
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
            final Collection<String> parentCodes = Utils.clearAggregationPrintCode(aggregation.getPrintCode());
            List<Aggregation> aggregationList = aggregationRepository.findAllByParentCodeIn(parentCodes);
            for (Aggregation child : aggregationList) {
                dto.getMarkDTOs().add(new CodeDTO(child.getId(), child.getCode(), child.getPrintCode(), child.getStatus()));
                dto.getMarks().add(child.getCode());
                if (AggregationStatus.DROPOUT.equals(child.getStatus())) {
                    dto.getDropoutMarks().add(child.getPrintCode());
                }
            }
            List<Mark> marks = markRepository.findAllByParentCodeIn(parentCodes);
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

    public AggregationStatsDTO getStats(AggregationFilter filter) {
        baseFilter(filter);
        return aggregationRepository.getStats(filter);
    }

    private void baseFilter(AggregationFilter filter) {
        if (StringUtils.isNotEmpty(filter.getSearch())) {
            if (Utils.isMark(filter.getSearch())) {
                final String code = Utils.clearPrintCode(filter.getSearch());
                Optional<Mark> firstByCode = markRepository.findFirstByCode(code);
                if (firstByCode.isPresent()) {
                    String parentOrCode = Optional.ofNullable(firstByCode.get().getParentCode()).orElse(code);
                    filter.setSearch(parentOrCode);
                } else {
                    filter.setSearch(code);
                }
            }
        }
    }

    @Transactional
    public void sendAggregation(final String code) {
        final String code20 = Utils.clearAggregationCode20(code);
        final String code18 = Utils.clearAggregationCode(code);
        final Optional<Aggregation> aggregationOptional = aggregationRepository.findFirstByCodeOrCode(code18, code20);
        if (aggregationOptional.isEmpty() || !EnumSet.of(AggregationStatus.ERROR, AggregationStatus.UPDATED, AggregationStatus.IN_PROGRESS, AggregationStatus.APPLIED).contains(aggregationOptional.get().getStatus())) {
            return;
        }
        final Aggregation aggregation = aggregationOptional.get();
        aggregation.setCode(code20);

        try {
            final Organization organization = aggregation.getOrganization();
            final ProductGroup productGroup = organization.getProductGroup();
            final boolean isAlcohol = productGroup == ProductGroup.Alcohol;
            final boolean isPharma = productGroup == ProductGroup.Pharma;
            final boolean isSingle = organization.getAggregationMode() == AggregationMode.Single;
            log.info("================= SEND AGGREGATION: {}  STATUS: {} =============================", aggregation.getCode(), aggregation.getStatus());
            if (isSingle) {
                if (isAlcohol) {
                    billingPermissionService.checkIsAvailableCount(organization.getId(), organization.isDemoAllowed(), organization.getParentId(), productGroup, 1);
                } else if (isPharma) {
                    if (organization.getTypes().contains(OrganizationType.Importer)) {
                        billingPermissionService.checkIsAvailableCount(organization.getId(), organization.isDemoAllowed(), organization.getParentId(), productGroup, aggregation.getQuantity());
                    }
                }
            }

            final List<Mark> marks = markRepository.findAllByParentCodeOrParentCode(code18, code20);
            Set<String> snTins = marks.stream().map(Mark::getCode).collect(Collectors.toSet());
            if (PackageType.PALLET.equals(aggregationOptional.get().getPackageType())) {
                snTins.addAll(aggregationRepository.findAllCodeByParentCode(code18, code20));
            }
            if (CollectionUtils.isEmpty(snTins)) {
                throw new BadRequestException("Коды маркировки отсутствуют");
            } else if (snTins.size() != aggregation.getQuantity()) {
                throw new BadRequestException("Количество не соответствует");
            }
            final AggregationItem aggregationItem = new AggregationItem();
            aggregationItem.setAggregatedItemsCount(aggregation.getQuantity());
            aggregationItem.setAggregationUnitCapacity(aggregation.getQuantity());
            aggregationItem.setAggregationType(AggregationType.AGGREGATION);
            aggregationItem.setUnitSerialNumber(aggregation.getCode());
            aggregationItem.setSntins(snTins);
            final CreateAggregationRequest request = new CreateAggregationRequest(Collections.singletonList(aggregationItem), organization.getTin());
            if (ProductGroup.Tobacco.equals(organization.getProductGroup())) {
                request.setProductionLineId(ProductGroup.Tobacco.getNameRu());
            }
            final CreateAggregationResponse response = sendAggregationToTuronTrace(request, organization.getProductGroup().getExtension(), organization.getTuronToken(), organization.getOmsId());
            if (response == null) {
                throw badRequest("Timeout").get();
            } else {
                aggregation.setReportId(response.getReportId());
                aggregation.setAggregationDate(LocalDateTime.now());
                aggregation.setStatus(AggregationStatus.SENT);
                aggregationRepository.saveAndFlush(aggregation);
                if (isSingle) {
                    if (isAlcohol) {
                        billingPermissionService.decrementAggregationCountAndSave(organization.getId(), organization.isDemoAllowed(), productGroup, 1);
                    } else if (isPharma && AggregationStatus.SENT == aggregation.getStatus()) {
                        if (organization.getTypes().contains(OrganizationType.Importer)) {
                            billingPermissionService.decrementAggregationCountAndSave(organization.getId(), organization.isDemoAllowed(), productGroup, aggregation.getQuantity());
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error sending aggregation", e);
            aggregation.setErrorMessage(e.getMessage());
            aggregation.setStatus(AggregationStatus.ERROR);
            aggregationRepository.saveAndFlush(aggregation);
        }
        if (AggregationType.PRINTING != aggregation.getAggregationType()) return;
        // notify current aggregation state
        rabbitMqProducer.sendAggregationStatusChangedEvent(aggregation.toPayload());
    }

    public Aggregation generateAggregation(final PackageType packageType, final String organizationId, final String gcp) {
        final Aggregation aggregation = new Aggregation();
        aggregation.setOrganizationId(organizationId);
        aggregation.setSsccCode("(00)");
        aggregation.setUnit(packageType.getCode());
        aggregation.setCompanyPrefix(gcp);
        aggregation.setGcp(gcp);
        int maxIntNumber = Math.toIntExact(aggregationRepository.nextVal("org_seq_" + gcp));
        aggregation.setIntNumber(maxIntNumber);

        aggregation.setSerialNumber(Utils.generateSerialNumber(maxIntNumber));
        final String tempNumber = StringUtils.join(aggregation.getUnit(), aggregation.getCompanyPrefix(), aggregation.getSerialNumber());
        String code = StringUtils.join(tempNumber, Utils.generateControlNumber(tempNumber));
        aggregation.setCode("00" + code);
        aggregation.setPrintCode(StringUtils.join(aggregation.getSsccCode(), code));

        return aggregation;
    }

    public AggregationDTO findAggregationWithMarks(String id) {
        return aggregationRepository.findById(id).map(aggregation -> {
            AggregationDTO aggregationDTO = aggregation.toDto(isAgency());
            List<Mark> marks = markRepository.findAllByParentCodeIn(Utils.clearAggregationPrintCode(aggregation.getCode()));
            for (Mark mark : marks) {
                aggregationDTO.getMarkDTOs().add(new CodeDTO(mark.getId(), mark.getCode(), mark.getPrintCode(), mark.getStatus()));
            }
            return aggregationDTO;
        }).orElseThrow(notFound());
    }

    //@Transactional
    public Map<String, String> checkAndUtiliseByBatch(List<String> aggregationIds) {
        List<Aggregation> aggregationList = aggregationRepository.findAllById(aggregationIds);
        final Map<String, List<Aggregation>> groupByOrganizationId = aggregationList.stream().collect(Collectors.groupingBy(Aggregation::getOrganizationId));

        for (String organizationId : groupByOrganizationId.keySet()) {
            List<Aggregation> aggregations = groupByOrganizationId.get(organizationId);
            if (CollectionUtils.isEmpty(aggregations)) {
                log.error("List is empty {}", organizationId);
                continue;
            }
            checkAggregations(organizationId, aggregations);
        }
        return null;
    }

    public void checkAggregations(final String organizationId, final Collection<Aggregation> allByFilter) {
        final Set<String> aggregationCodes = allByFilter.stream().map(Aggregation::getCode).collect(Collectors.toSet());
        if (aggregationCodes.isEmpty() || organizationId == null) {
            log.info("Aggregation codes is empty");
            return;
        }
        final Organization organization = organizationRepository.findById(organizationId).orElse(null);
        if (organization == null) {
            return;
        }
        final String currentOrganizationAsilBelgiToken = getCurrentOrganizationAsilBelgiTokenOrNul(organization.getTin());
        if (currentOrganizationAsilBelgiToken == null) {
            log.error("ORGANIZATION {} HAS NO TRUE TOKEN", organization.getName());
            return;
        }
        final boolean isAppliances = ProductGroup.Appliances == organization.getProductGroup();
        Map<String, CisInfoResponse> cisInfoMap = new HashMap<>();
        try {
            Map<String, CisInfoResponse> stringCisInfoResponseMap = checkFromAsilBelgisi(aggregationCodes, currentOrganizationAsilBelgiToken);
            cisInfoMap.putAll(stringCisInfoResponseMap);
        } catch (Exception e) {
//            log.error("error checking aggregations from asilbelgisi", e);
        }
        String time = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        for (Aggregation aggregation : allByFilter) {
            final String code = aggregation.getCode();
            int errorCheckingLimit = 3;
//            if (errorCheckingLimit <= aggregation.getChecked()) {
//                log.info("Aggregation {} checked already 3 times", code);
//                continue;
//            }
            // check cisInfoResponse
            final String errorMessage = checkResponse(code, cisInfoMap, time);
            if (errorMessage != null) {
                aggregation.setErrorMessage(errorMessage);
                aggregation.setStatus(AggregationStatus.ERROR);
                if (cisInfoMap.containsKey(code) && cisInfoMap.get(code).getCisInfo() != null) {
                    CisInfo cisInfo = cisInfoMap.get(code).getCisInfo();
                    if (MarkStatus.WRITTEN_OFF == cisInfo.getStatus()) {
                        aggregation.setStatus(AggregationStatus.UTILIZED);
                        aggregationRepository.save(aggregation);
                        continue;
                    } else if (MarkStatus.DISAGGREGATED == cisInfo.getStatus()) {
                        aggregation.setStatus(AggregationStatus.DISAGGREGATED);
                        aggregationRepository.save(aggregation);
                        continue;
                    }
                }
            } else {
                if (AggregationStatus.CREATED != aggregation.getStatus()) {
                    aggregation.setErrorMessage(localize("checked.success", LocalDateTime.now(), cisInfoMap.get(code).getCisInfo().getStatusName()));
                    aggregation.setStatus(AggregationStatus.CREATED);
                    aggregationRepository.save(aggregation);

                }
                continue;
            }
            // collect children codes
            final Set<String> codes = new HashSet<>();
            final Set<String> utiliseCodes = new HashSet<>();
            if (PackageType.PALLET.equals(aggregation.getPackageType())) {
                List<String> childrenAggregation = aggregationRepository.findAllCodeByParentCode(code);
                codes.addAll(childrenAggregation);
            }
            List<Mark> childrenMark = markRepository.findAllByParentCode(code);
//            log.debug("childrenMark {}", childrenMark);
            for (Mark mark : childrenMark) {
                String printCode = Utils.fillWithUniCode(mark.getPrintCode());
                codes.add(mark.getCode());
                if (mark.getUnit() == null && !isAppliances) {
                    utiliseCodes.add(printCode);
                }
            }
            aggregation.setChecked(aggregation.getChecked() + 1);
//            log.debug("Codes {}", codes);
            if (codes.isEmpty()) {
                final RedisAggregation firstByCode = redisAggregationRepository.findFirstByCode(code).orElse(null);
                if (firstByCode != null && CollectionUtils.isNotEmpty(firstByCode.getMarks())) {
                    Set<String> aggregations = new LinkedHashSet<>();
                    List<String> marks = new LinkedList<>();
                    for (String childPrintCode : firstByCode.getMarks()) {
                        final String cleanCode = Utils.clearPrintCode(childPrintCode);
                        if (Utils.isAggregation(childPrintCode)) {
                            aggregations.add(cleanCode);
                        } else {
                            marks.add(cleanCode);

                        }
                        codes.add(cleanCode);
                    }
                    if (CollectionUtils.isNotEmpty(aggregations)) {
                        aggregationRepository.findAllByCodeIn(aggregations).forEach(child -> {
                            child.setParentCode(code);
                            aggregationRepository.save(child);
                        });
                    }
                    if (CollectionUtils.isNotEmpty(marks)) {
                        List<Mark> allByCodeIn = markRepository.findAllByCodeIn(marks);
                        for (Mark mark : allByCodeIn) {
                            mark.setParentCode(code);
                            markRepository.save(mark);
                            if (mark.getUnit() == null) {
                                utiliseCodes.add(mark.getPrintCode());
                            }
                        }
                    }
                } else {
                    aggregation.setStatus(AggregationStatus.ERROR);
                    aggregation.setErrorMessage("КМ не найден");
                    aggregationRepository.save(aggregation);
                    log.error("Aggregation {} children is empty", code);
                    continue;
                }
            } else if (aggregation.getQuantity() != codes.size()) {
                aggregation.setStatus(AggregationStatus.ERROR);
                aggregation.setErrorMessage("Неверное Количество КМ");
                aggregationRepository.save(aggregation);
                log.error("Aggregation {} marks count not equal", code);
                continue;
            }
            if (CollectionUtils.isNotEmpty(utiliseCodes)) {
                String serialNumber = aggregation.getSerialNum() != null ? aggregation.getSerialNum().getSerialNumber() : null;
                try {
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
            } else {
                aggregation.setStatus(AggregationStatus.APPLIED);
            }
            if (AggregationStatus.APPLIED == aggregation.getStatus()) {
                rabbitMqProducer.resendAggregationToTuron(aggregation.getCode(), MqConstants.RESEND_AGGREGATION_DELAY);
            }
            aggregationRepository.save(aggregation);
            if (AggregationType.PRINTING == aggregation.getAggregationType()) {
                rabbitMqProducer.sendAggregationStatusChangedEvent(aggregation.toPayload());
            }
        }
    }

    public List<String> print(AggregationFilter filter) {
        return aggregationRepository.findAllByFilter(filter).map(Aggregation::getPrintCode).getContent();
    }

    public ReportInfoResponse getTuronReportInfo(String id, String organizationId) {
        final String orgId = Optional.ofNullable(organizationId).orElse(getCurrentOrganizationId());
        final Organization organization = organizationRepository.findById(orgId).orElseThrow(notFound("Organization not found"));
        return turonClient.reportInfo(organization.getTuronToken(), organization.getProductGroup().getExtension(), organization.getOmsId(), id);
    }

    //@Transactional
    public void checkAndResendByFilter(AggregationFilter filter) {
        int pageNumber = 0;
        boolean isNotLast;
        do {
            long start = System.currentTimeMillis();
            filter.setPage(pageNumber);

            Page<Aggregation> page = aggregationRepository.findAllByFilter(filter);
            final Map<String, List<Aggregation>> groupByOrganizationId = page.stream().collect(Collectors.groupingBy(Aggregation::getOrganizationId));
            for (String organizationId : groupByOrganizationId.keySet()) {
                List<Aggregation> orgAggregations = groupByOrganizationId.get(organizationId);
                if (CollectionUtils.isEmpty(orgAggregations)) {
                    continue;
                }
                checkAggregations(organizationId, orgAggregations);
            }
            pageNumber++;
            isNotLast = page.hasNext();
            String message = localize("start.checking.error", pageNumber, page.getTotalPages(), page.getTotalElements(), System.currentTimeMillis() - start);
            log.info(message);
        } while (isNotLast);
    }

    public void exportReport(AggregationFilter filter) {
        if (filter.getFrom() == null && filter.getTo() == null) {
            filter.setTo(LocalDateTime.now());
            filter.setFrom(filter.getTo().minusMonths(1));
        }
        String organizationId = Optional.ofNullable(filter.getOrganizationId()).orElse(SecurityUtils.getCurrentOrganizationId());
        filter.setOrganizationId(organizationId);
        ExportReportType exportReportType = FileType.EXCEL.equals(filter.getFileType()) ? ExportReportType.EXCEL_AGGREGATION_BY_PRODUCT : ExportReportType.CSV_AGGREGATION_BY_PRODUCT;
        exportReportService.export(filter, exportReportType, filter.getFileType(), "Aggregation_Report");
    }
}




