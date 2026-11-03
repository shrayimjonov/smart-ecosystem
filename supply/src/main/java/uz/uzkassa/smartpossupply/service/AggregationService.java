package uz.uzkassa.smartpossupply.service;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.smartpossupply.domain.aggregation.Aggregation;
import uz.uzkassa.smartpossupply.domain.aggregation.AggregationBlock;
import uz.uzkassa.smartpossupply.domain.aggregation.RedisLastAggregationMarks;
import uz.uzkassa.smartpossupply.domain.billing.UsagePlan;
import uz.uzkassa.smartpossupply.domain.company.Company;
import uz.uzkassa.smartpossupply.domain.company.CompanyAggregationInfo;
import uz.uzkassa.smartpossupply.domain.mark.Mark;
import uz.uzkassa.smartpossupply.dto.aggregation.AggregationCreateDTO;
import uz.uzkassa.smartpossupply.dto.aggregation.AggregationDetailDTO;
import uz.uzkassa.smartpossupply.dto.aggregation.AggregationListDTO;
import uz.uzkassa.smartpossupply.dto.aggregation.AggregationPayload;
import uz.uzkassa.smartpossupply.dto.aggregation.AggregationStatisticsDTO;
import uz.uzkassa.smartpossupply.dto.aggregation.AggregationValidationDTO;
import uz.uzkassa.smartpossupply.dto.aggregation.turon.TuronAggregationItem;
import uz.uzkassa.smartpossupply.dto.aggregation.turon.TuronAggregationType;
import uz.uzkassa.smartpossupply.dto.aslbelgi.CisInfo;
import uz.uzkassa.smartpossupply.dto.aslbelgi.CisInfoResponse;
import uz.uzkassa.smartpossupply.dto.base.ResultList;
import uz.uzkassa.smartpossupply.dto.integration.turon.aggregation.TuronCreateAggregationRequest;
import uz.uzkassa.smartpossupply.dto.integration.turon.aggregation.TuronCreateAggregationResponse;
import uz.uzkassa.smartpossupply.dto.payload.AggregationOrderPayload;
import uz.uzkassa.smartpossupply.enums.AggregationStatus;
import uz.uzkassa.smartpossupply.enums.CountableServiceTypes;
import uz.uzkassa.smartpossupply.enums.PackageType;
import uz.uzkassa.smartpossupply.enums.ProductGroup;
import uz.uzkassa.smartpossupply.enums.ProductType;
import uz.uzkassa.smartpossupply.enums.TuronMarkStatus;
import uz.uzkassa.smartpossupply.filters.AggregationFilter;
import uz.uzkassa.smartpossupply.integration.TuronClient;
import uz.uzkassa.smartpossupply.repository.AggregationBlockRepository;
import uz.uzkassa.smartpossupply.repository.AggregationRepository;
import uz.uzkassa.smartpossupply.repository.MarkRepository;
import uz.uzkassa.smartpossupply.repository.redis.RedisLastAggregationMarksRepository;
import uz.uzkassa.smartpossupply.utils.MarkUtils;
import uz.uzkassa.smartpossupply.web.rest.errors.AppGlobalException;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static lombok.AccessLevel.PRIVATE;

@Slf4j
@Service
@Transactional(readOnly = true)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AggregationService extends AggregationBaseService {

    AggregationRepository aggregationRepository;
    AggregationBlockRepository aggregationBlockRepository;
    MarkRepository markRepository;
    TuronClient turonClient;
    AggregationOrderService aggregationOrderService;
    RedisLastAggregationMarksRepository redisLastAggregationMarksRepository;
    UsagePlanService usagePlanService;

    @Transactional
    public String create(AggregationCreateDTO aggregationCreateDTO) {
        if (CollectionUtils.isEmpty(aggregationCreateDTO.getMarks())) {
            throw validationService.badRequestExceptionThrow("Коды маркировки отсутствуют.").get();
        } else if (aggregationCreateDTO.getMarks().size() != aggregationCreateDTO.getQty()) {
            throw validationService.badRequestExceptionThrow("Неверное Количество КМ").get();
        }
        final Aggregation parentCode;
        if (StringUtils.isNotEmpty(aggregationCreateDTO.getPrintCode())) {
            parentCode = aggregationRepository.findFirstByCode(MarkUtils.clearAggregationCode(aggregationCreateDTO.getPrintCode()))
                    .orElseThrow(validationService.entityNotFoundExceptionThrow(Aggregation.class.getSimpleName(), "code", aggregationCreateDTO.getPrintCode()));
        } else {
            CompanyAggregationInfo companyAggregationInfo = validateAslBelgiAndGetAggregationInfo();
            AggregationOrderPayload aggregationOrderPayload = new AggregationOrderPayload(null, companyAggregationInfo.getCompanyId(), 1, PackageType.PALLET.name(), getCurrentUserId());
            parentCode = aggregationOrderService.generateAggregation(aggregationOrderPayload, companyAggregationInfo.getGcp());
        }
        parentCode.setStatus(AggregationStatus.CREATED);
        parentCode.setQty(aggregationCreateDTO.getQty());
        parentCode.setProductId(aggregationCreateDTO.getProductId());
        parentCode.setDescription(aggregationCreateDTO.getDescription());
        parentCode.setAggregationDate(LocalDateTime.now());
        parentCode.setProductionDate(aggregationCreateDTO.getProductionDate());
        parentCode.setExpirationDate(aggregationCreateDTO.getExpiredDate());
        parentCode.setQtyInUnit(aggregationCreateDTO.getQtyInUnit());
        aggregationRepository.save(parentCode);
        List<Aggregation> children = aggregationRepository.findAllByCodeIn(aggregationCreateDTO.getMarks());
        if (aggregationCreateDTO.getQty() > children.size()) {
            throw validationService.badRequestExceptionThrow("Неверное Количество КМ").get();
        }
        List<Aggregation> list = children.stream().map(child -> {
            child.setProductId(aggregationCreateDTO.getProductId());
            child.setParentCode(parentCode.getCode());
            child.setStatus(AggregationStatus.CREATED);
            return child;
        }).toList();
        aggregationRepository.saveAll(list);
        return parentCode.getOriginalCode();
    }

    public Page<AggregationListDTO> getList(AggregationFilter filter) {
        filter.setPackageType(PackageType.PALLET.name());

        if (StringUtils.isNotEmpty(filter.getSearch())) {
            String code = MarkUtils.clearOriginalCode(filter.getSearch(), null);
            if (MarkUtils.isMark(filter.getSearch()) || MarkUtils.alcoholMatcherWithoutCX(filter.getSearch())) {
                markRepository.findFirstByCode(code).ifPresent(mark -> filter.setSearch(mark.getOriginalCode()));
            } else {
                filter.setSearch(code);
            }
        }

        ResultList<Aggregation> resultList = aggregationRepository.getResultList(filter);
        List<AggregationListDTO> result = resultList.getList().stream()
            .map(aggregation -> {
                AggregationListDTO aggregationListDTO = new AggregationListDTO();
                    aggregationListDTO.setId(aggregation.getId());
                    aggregationListDTO.setCode(aggregation.getCode());
                    aggregationListDTO.setPrintCode(aggregation.getOriginalCode());
                    if (aggregation.getProduct() != null) {
                        aggregationListDTO.setProduct(aggregation.getProduct().toProductCommonDTO());
                    }
                    aggregationListDTO.setStatus(aggregation.getStatus());
                    aggregationListDTO.setPackageType(aggregation.getPackageType());
                    aggregationListDTO.setQty(aggregation.getQty());
                    aggregationListDTO.setAggregationDate(aggregation.getAggregationDate());
                    aggregationListDTO.setCreator(aggregation.getCreator().toCommonDTO());
                    aggregationListDTO.setProductionDate(aggregation.getProductionDate());
                    aggregationListDTO.setExpirationDate(aggregation.getExpirationDate());
                    aggregationListDTO.setQtyInUnit(aggregation.getQtyInUnit());
                    if (aggregation.getStock() != null) {
                        aggregationListDTO.setStock(aggregation.getStock().toCommonDTO());
                    }
                    if (aggregation.getProductSerial() != null) {
                        aggregationListDTO.setProductSerial(aggregation.getProductSerial().toCommonDTO());
                    }
                    return aggregationListDTO;
                }).toList();
        return new PageImpl<>(result, filter.getSortedPageable(), resultList.getCount());
    }

    @Transactional
    public AggregationStatus create(AggregationPayload aggregationPayload, Company company) {
        if (CollectionUtils.isEmpty(aggregationPayload.getMarks())) {
            throw validationService.badRequestExceptionThrow("Коды маркировки отсутствуют.").get();
        } else if (aggregationPayload.getMarks().size() != aggregationPayload.getQty()) {
            throw validationService.badRequestExceptionThrow("Неверное Количество КМ").get();
        }

        UsagePlan usagePlan = null;
        if (!company.isFreeTrial()) {
            usagePlan = usagePlanService.getUsagePlan(CountableServiceTypes.AGGREGATION.getCode());
            if (usagePlan.getQty() <= 0) {
                throw validationService.badRequestExceptionThrow("Недостаточно баланса для агрегации", String.valueOf(usagePlan.getQty())).get();
            }
        }

        List<String> snTins = aggregationPayload.getMarks().stream().map(code -> MarkUtils.clearOriginalCode(code, aggregationPayload.getProductType())).collect(Collectors.toList());

        if (ProductType.isContainGroupMarkOrder(aggregationPayload.getProductType())) {
            if (PackageType.BOX.equals(aggregationPayload.getPackageType())) {
                List<String> invalidMarks = aggregationPayload.getMarks().stream().filter(code -> !MarkUtils.isMark(code)).collect(Collectors.toList());
                if (!CollectionUtils.isEmpty(invalidMarks)) {
                    throw validationService.badRequestExceptionThrow("Не соответствует формат код маркировки: " + aggregationPayload.getPrintCode()).get();
                }
                List<String> notAggregatedMarks = new LinkedList<>();
                for (String item : snTins) {
                    Optional<AggregationBlock> block = aggregationBlockRepository.findFirstByCode(item);
                    if (block.isPresent()) {
                        if (!AggregationStatus.CREATED.equals(block.get().getStatus())) {
                            notAggregatedMarks.add(block.get().getPrintCode());
                        }
                    } else {
                        CisInfo cisInfo = cisesInfoWrapper(item);
                        if (cisInfo == null)
                            throw validationService.badRequestExceptionThrow("Ошибка при проверке кода. Код не найден: " + aggregationPayload.getPrintCode()).get();
                        if (cisInfo.getParent() != null)
                            throw validationService.badRequestExceptionThrow("Ошибка при проверке кода. Код состоит другой агрегации: " + cisInfo.getParent()).get();

                        if (TuronMarkStatus.APPLIED.equals(cisInfo.getStatus())) {
                            if (cisInfo.getChild() == null) {
                                notAggregatedMarks.add(cisInfo.getRequestedCis());
                            } else {
                                AggregationBlock aggregationBlock = new AggregationBlock();
                                aggregationBlock.setProductId(aggregationPayload.getProductId());
                                aggregationBlock.setCode(item);
                                aggregationBlock.setPrintCode(aggregationPayload.getPrintCode());
                                aggregationBlock.setStatus(AggregationStatus.CREATED);
                                aggregationBlock.setCreatedDate(LocalDateTime.now());
                                aggregationBlock.setQty(cisInfo.getChild().size());
                                aggregationBlock.setAggregationDate(LocalDateTime.now());
                                aggregationBlock.setUsed(true);
                                aggregationBlock.setPartyProductId(aggregationPayload.getPartyProductId());
                                aggregationBlock.setCreatedDate(LocalDateTime.now());
                                aggregationBlock.setCreatorId(getCurrentUserId());
                                aggregationBlock.setUpdaterId(getCurrentUserId());
                                aggregationBlock.setMarkOrderPartyId(Long.MIN_VALUE);
                                aggregationBlock.setCompanyId(company.getId());
                                aggregationBlockRepository.save(aggregationBlock);

                                if (!CollectionUtils.isEmpty(cisInfo.getChild())) {
                                    CisInfoResponse[] childMarks = cisesInfoWrapper(new HashSet<>(cisInfo.getChild()));
                                    for (CisInfoResponse responseItem : childMarks) {
                                        Mark mark = new Mark();
                                        mark.setCode(responseItem.getCisInfo().getRequestedCis());
                                        mark.setParentCode(aggregationBlock.getCode());
                                        mark.setPartyProductId(aggregationPayload.getPartyProductId());
                                        mark.setOriginalCode(cisInfo.getOriginalCode());
                                        mark.setCompanyId(company.getId());
                                        mark.setUpdaterId(getCurrentUserId());
                                        mark.setCreatorId(getCurrentUserId());
                                        mark.setProductId(aggregationPayload.getProductId());
                                        mark.setCreatedDate(LocalDateTime.now());
                                        mark.setMarkOrderPartyId(Long.MIN_VALUE);
                                        mark.setUsed(true);
                                    }
                                }
                            }
                        } else {
                            throw validationService.badRequestExceptionThrow("Ошибка при проверке кода. Статус: " + cisInfo.getStatus().getText()).get();
                        }
                    }
                }
                if (!CollectionUtils.isEmpty(notAggregatedMarks)) {
                    throw validationService.badRequestExceptionThrow("Коды еще не аггрегированы KM:" + notAggregatedMarks).get();
                }
            }
        } else {
            if (PackageType.BOX.equals(aggregationPayload.getPackageType())) {

                List<String> alreadyAggregatedMarks = markRepository.findAllByCodeInAndUsedIsTrue(snTins);
                if (!CollectionUtils.isEmpty(alreadyAggregatedMarks)) {
                    throw validationService.badRequestExceptionThrow("Коды уже использованы").get();
                }
            }
        }
        if (PackageType.PALLET.equals(aggregationPayload.getPackageType())) {
            List<String> notAggregatedBoxes = aggregationRepository.findAllByCodesInAndStatusIs(snTins, AggregationStatus.DRAFT);
            if (!CollectionUtils.isEmpty(notAggregatedBoxes)) {
                throw validationService.badRequestExceptionThrow("Коды еще не аггрегированы", "Коды еще не аггрегированы").get();
            }
        }

        CompanyAggregationInfo companyAggregationInfo = validateTuronAndGetAggregationInfo();
        Aggregation aggregation;
        if (StringUtils.isNotEmpty(aggregationPayload.getPrintCode())) {
            String code = MarkUtils.clearOriginalCode(aggregationPayload.getPrintCode(), null);
            aggregation = aggregationRepository.findFirstByCode(code).orElseThrow(validationService.entityNotFoundExceptionThrow(Aggregation.class.getSimpleName(), "code", aggregationPayload.getPrintCode()));
            if (!AggregationStatus.DRAFT.equals(aggregation.getStatus())) {
                if (aggregation.getParentCode() != null) {
                    throw validationService.badRequestExceptionThrow("Код состоит в агрегации КA: " + aggregation.getParentCode()).get();
                }
                throw validationService.badRequestExceptionThrow("Код состоит в агрегации КA").get();
            }
        } else {
            aggregation = new Aggregation();
            aggregation.setCompanyId(company.getId());
            Long incrementNumber = aggregationRepository.nextVal(companyAggregationInfo.getGcp());
            aggregation.setIncrementNumber(incrementNumber);
            aggregation.setSerialNumber(AGGREGATION_FORMAT.format(incrementNumber));
        }

        aggregation.setProductId(aggregationPayload.getProductId());
        aggregation.setQty(aggregationPayload.getQty());
        aggregation.setSsccCode("(00)");
        aggregation.setGcp(companyAggregationInfo.getGcp());
        aggregation.setPackageType(aggregationPayload.getPackageType());

        if (aggregation.getId() == null) {
            String tempNumber = StringUtils.join(aggregation.getPackageType().getCodeNumber(), companyAggregationInfo.getGcp(), aggregation.getSerialNumber());
            aggregation.setCode(StringUtils.join(tempNumber, MarkUtils.generateControlNumber(tempNumber)));
            aggregation.setOriginalCode(StringUtils.join(aggregation.getSsccCode(), aggregation.getCode()));
        }
        if (ProductGroup.Tobacco.equals(company.getProductGroup())) {
            aggregation.setCode(aggregation.getOriginalCode().replace("(00)", "00"));
        }

        aggregationRepository.save(aggregation);

        if (aggregationPayload.isSendToTuron()) {
            TuronAggregationItem turonAggregationItem = new TuronAggregationItem();
            turonAggregationItem.setAggregatedItemsCount(aggregationPayload.getQty());
            turonAggregationItem.setAggregationUnitCapacity(aggregationPayload.getQty());
            turonAggregationItem.setAggregationType(TuronAggregationType.AGGREGATION);
            turonAggregationItem.setUnitSerialNumber(aggregation.getCode());
            turonAggregationItem.setSntins(snTins);

            TuronCreateAggregationRequest request = new TuronCreateAggregationRequest(Collections.singletonList(turonAggregationItem), company.getTin(), String.valueOf(aggregationPayload.getProductId()));
            TuronCreateAggregationResponse response = turonClient.createAggregation(request, company.getProductGroup().getExtension(), companyAggregationInfo.getTuronToken(), companyAggregationInfo.getOmsId());
            if (response == null) {
                aggregation.setStatus(AggregationStatus.ERROR);
            } else {
                aggregation.setReportId(response.getReportId());
                aggregation.setStatus(AggregationStatus.CREATED);
                aggregation.setAggregationDate(LocalDateTime.now());
            }
        } else {
            aggregation.setStatus(AggregationStatus.IN_GROUP);
        }
        aggregation.setPartyProductId(aggregationPayload.getPartyProductId());
        aggregation = aggregationRepository.save(aggregation);
        saveMarks(aggregationPayload, aggregation);

        if (usagePlan != null) {
            usagePlan.setQty(usagePlan.getQty() - 1);
            usagePlanRepository.save(usagePlan);
        }
        return aggregation.getStatus();
    }

    @Transactional
    public void saveMarks(AggregationPayload aggregationPayload, Aggregation parentAggregation) {
        if (ProductType.isContainGroupMarkOrder(aggregationPayload.getProductType())) {
            if (PackageType.BOX.equals(aggregationPayload.getPackageType())) {
                List<AggregationBlock> aggregationBlocks = aggregationPayload.getMarks().stream().map(printCode -> {
                    String code = MarkUtils.clearOriginalCode(printCode, aggregationPayload.getProductType());
                    AggregationBlock aggregationBlock = aggregationBlockRepository.findFirstByCode(code).orElseGet(AggregationBlock::new);
                    if (aggregationBlock.getId() == null) {
                        aggregationBlock.setPrintCode(printCode);
                        aggregationBlock.setCode(code);
                        aggregationBlock.setProductId(aggregationPayload.getProductId());
                        aggregationBlock.setCompanyId(getCurrentCompanyId());
                        aggregationBlock.setStatus(AggregationStatus.DRAFT);
                    }
                    aggregationBlock.setParentCode(parentAggregation.getCode());
                    if (aggregationPayload.getPartyProductId() != null) {
                        aggregationBlock.setPartyProductId(aggregationPayload.getPartyProductId());
                    }
                    aggregationBlock.setUsed(true);
                    return aggregationBlock;
                }).collect(Collectors.toList());
                aggregationBlockRepository.saveAll(aggregationBlocks);
                aggregationBlocks.forEach(block -> markRepository.updateSetPartyProductIdByParentCode(aggregationPayload.getPartyProductId(), block.getCode()));
            }

        } else {
            List<Mark> marks = aggregationPayload.getMarks().stream().map(printCode -> {
                String code = MarkUtils.clearOriginalCode(printCode, aggregationPayload.getProductType());
                Mark mark = markRepository.findFirstByCode(code).orElseGet(Mark::new);
                if (mark.getId() == null) {
                    mark.setOriginalCode(printCode);
                    mark.setCode(code);
                    mark.setProductId(aggregationPayload.getProductId());
                    mark.setCompanyId(getCurrentCompanyId());
                }
                if (aggregationPayload.getPartyProductId() != null) {
                    mark.setPartyProductId(aggregationPayload.getPartyProductId());
                }
                mark.setParentCode(parentAggregation.getCode());
                mark.setUsed(true);
                return mark;
            }).collect(Collectors.toList());
            markRepository.saveAll(marks);
        }
        if (PackageType.PALLET.equals(aggregationPayload.getPackageType())) {
            Set<String> aggregationBoxList = aggregationPayload.getMarks().stream().map(printCode -> MarkUtils.clearOriginalCode(printCode, null)).collect(Collectors.toSet());
            aggregationRepository.updateAllSetPartyProductIdAndParentCodeWhereAggregationCodeSetIn(aggregationPayload.getPartyProductId(), parentAggregation.getCode(), aggregationBoxList);
        }
    }

    @Transactional
    public void resend(Long id) {
        Aggregation aggregation = aggregationRepository.findById(id).orElseThrow(validationService.entityNotFoundExceptionThrow(Aggregation.class.getSimpleName(), "id", id));
        if (!AggregationStatus.ERROR.equals(aggregation.getStatus())) {
            throw validationService.badRequestExceptionThrow("Аггрегация уже создана").get();
        }
        Company company = getCurrentCompany();
        CompanyAggregationInfo companyAggregationInfo = validateTuronAndGetAggregationInfo();
        List<String> snTins = markRepository.findAllCodesByParentCode(aggregation.getCode());

        if (CollectionUtils.isEmpty(snTins)) {
            StringBuilder message = new StringBuilder();
            message.append("ПЕРЕОТПРАВКА КА в ТУРОН \n");
            message.append("ОТСУТСТВУЕТ КМ в базе \n");
            message.append("-------------------------------------\n");
            message.append("ИНН: ").append(company.getTin()).append("\n");
            message.append("КА: ").append(aggregation.getOriginalCode());
            log.error(message.toString());

            aggregation.setChecked(true);
            aggregationRepository.save(aggregation);
            return;
        }

        TuronAggregationItem turonAggregationItem = new TuronAggregationItem();
        turonAggregationItem.setAggregatedItemsCount(aggregation.getQty());
        turonAggregationItem.setAggregationUnitCapacity(aggregation.getQty());
        turonAggregationItem.setAggregationType(TuronAggregationType.AGGREGATION);
        turonAggregationItem.setUnitSerialNumber(aggregation.getCode());
        turonAggregationItem.setSntins(snTins);

        TuronCreateAggregationRequest request = new TuronCreateAggregationRequest(Collections.singletonList(turonAggregationItem), company.getTin());
        TuronCreateAggregationResponse response = turonClient.createAggregation(request, company.getProductGroup().getExtension(), companyAggregationInfo.getTuronToken(), companyAggregationInfo.getOmsId());

        if (response == null) {
            aggregation.setStatus(AggregationStatus.ERROR);
        } else {
            aggregation.setReportId(response.getReportId());
            aggregation.setAggregationDate(LocalDateTime.now());
            aggregation.setStatus(AggregationStatus.CREATED);
        }
        aggregationRepository.save(aggregation);
    }

    public AggregationDetailDTO getPallet(Long id) {
        return aggregationRepository.findById(id)
                .map(aggregation -> {
                    AggregationDetailDTO aggregationDetailDTO = new AggregationDetailDTO();
                    aggregationDetailDTO.setId(aggregation.getId());
                    aggregationDetailDTO.setQty(aggregation.getQty());
                    aggregationDetailDTO.setCode(aggregation.getCode());
                    aggregationDetailDTO.setPrintCode(aggregation.getOriginalCode());
                    aggregationDetailDTO.setDescription(aggregation.getDescription());
                    aggregationDetailDTO.setPackageType(aggregation.getPackageType());
                    aggregationDetailDTO.setProductionDate(aggregation.getProductionDate());
                    aggregationDetailDTO.setExpirationDate(aggregation.getExpirationDate());
                    aggregationDetailDTO.setQtyInUnit(aggregation.getQtyInUnit());
                    if (aggregation.getProductId() != null) {
                        aggregationDetailDTO.setProduct(aggregation.getProduct().toProductCommonDTO());
                    }
                    if (aggregation.getStock() != null) {
                        aggregationDetailDTO.setStock(aggregation.getStock().toCommonDTO());
                    }
                    if (PackageType.PALLET.equals(aggregation.getPackageType())) {
                        Set<String> childBoxSet = aggregationRepository.findAllCodesByParentCode(aggregation.getCode());
                        aggregationDetailDTO.setMarks(childBoxSet);
                    } else if (PackageType.BOX.equals(aggregation.getPackageType())) {
                        aggregationDetailDTO.setMarks(Collections.emptySet());
                    }
                    return aggregationDetailDTO;
                }).orElseThrow(validationService.entityNotFoundExceptionThrow(Aggregation.class.getSimpleName(), "id", id));
    }

    public List<String> getStampList(AggregationFilter filter) {
        return aggregationRepository.getCodesByAggregationOrder(filter);
    }

    public String validateSimple(AggregationValidationDTO aggregationValidationDTO) {
        final RedisLastAggregationMarks redisMark = redisLastAggregationMarksRepository.findById(aggregationValidationDTO.getCode()).orElse(null);
        if (redisMark != null) {
            final String userLogin = Optional.ofNullable(redisMark.getUserLogin()).orElse("другим оператором");
            throw validationService.badRequestExceptionThrow(StringUtils.join("Код уже сканирован со стороны ", userLogin, " в ", redisMark.getScanTime()), aggregationValidationDTO.getCode()).get();
        }
        Aggregation code = aggregationRepository.findFirstByCode(aggregationValidationDTO.getCode())
                .orElseThrow(validationService.entityNotFoundExceptionThrow(Aggregation.class.getSimpleName(), "code", aggregationValidationDTO.getCode()));

        if (aggregationValidationDTO.isChild()) {
            if (StringUtils.isNotEmpty(code.getParentCode())) {
                throw validationService.badRequestExceptionThrow("Код состоит в составе " + code.getParentCode()).get();
            }
            if (!PackageType.BOX.equals(code.getPackageType())) {
                throw validationService.badRequestExceptionThrow("Сканировали " + code.getPackageType().getName()).get();
            }
        } else {
            if (!PackageType.PALLET.equals(code.getPackageType())) {
                throw validationService.badRequestExceptionThrow("Сканировали " + code.getPackageType().getName()).get();
            }
        }

        redisLastAggregationMarksRepository.save(new RedisLastAggregationMarks(aggregationValidationDTO.getCode(), getCurrentUserId(), LocalDateTime.now(), getCurrentUserLogin(), aggregationValidationDTO.getPackageType().getCodeNumber()));
        return aggregationValidationDTO.getCode();
    }

    public String validate(AggregationValidationDTO aggregationValidationDTO) {
        // common validations
        if (!MarkUtils.validateCode(aggregationValidationDTO.getCode(), aggregationValidationDTO.getPackageType())) {
            throw validationService.badRequestExceptionThrow("Код не соответствует стандартному формату", aggregationValidationDTO.getCode()).get();
        }
        // aggregationValidationDTO.isCheckAslBelgi() is true then check company trueToken token exists
        if (aggregationValidationDTO.isCheckAslBelgi()) {
            validateAslBelgiAndGetAggregationInfo();
        }

        // detect printCode is aggregation or mark
        boolean isAggregation = MarkUtils.isAggregation(aggregationValidationDTO.getCode());
        //detect code is block aggregation or base mark
        boolean isBlock = MarkUtils.blockMatcher(aggregationValidationDTO.getCode());
        //detect code is mark matched or not matched
        boolean isMark = MarkUtils.isMark(aggregationValidationDTO.getCode());

        try {
            if (isBlock) {
                validateBlockAggregation(aggregationValidationDTO);
            } else if (isAggregation) {
                validateAggregation(aggregationValidationDTO);
            } else if (isMark) {
                validateMark(aggregationValidationDTO);
            }
        } catch (AppGlobalException notFoundException) {
            if (aggregationValidationDTO.isCheckAslBelgi()) {
                return validateCodeFromAsilBelgi(aggregationValidationDTO, isBlock, isMark);
            } else {
                throw notFoundException;
            }
        }

        return aggregationValidationDTO.getCode();
    }

    private String validateCodeFromAsilBelgi(AggregationValidationDTO aggregationValidationDTO, boolean isBlock, boolean isMark) {
        String code = MarkUtils.clearOriginalCode(aggregationValidationDTO.getCode(), null);

        Company company = getCurrentCompany();
        CompanyAggregationInfo companyAggregationInfo = validateAslBelgiAndGetAggregationInfo();

        CisInfoResponse[] result = aslBelgiClient.cisesInfo(company.getTin(), companyAggregationInfo.getAslBelgiToken(), Collections.singleton(code));
        if (result != null && result.length > 0) {
            CisInfo cisInfo = result[0].getCisInfo();
            boolean isApplied = TuronMarkStatus.APPLIED.equals(cisInfo.getStatus());
            if (!isApplied) {
                StringBuilder message = new StringBuilder("TIN: ").append(company.getTin()).append("\n");
                message.append("CODE: ").append(code).append(" \n");
                String errorMessage = cisInfo.getStatus() != null ? "Ошибка аггрегации! Статус КМ : " + cisInfo.getStatus().getText() : "Ошибка аггрегации! Статус КМ : NULL";
                message.append(errorMessage);
                throw validationService.badRequestExceptionThrow(message.toString()).get();
            }
            if (isBlock || isMark) {
                if (!MarkUtils.checkIsEqualBarcodes(aggregationValidationDTO.getBarcode(), cisInfo.getGtin())) {
                    StringBuilder message = new StringBuilder("TIN: ").append(company.getTin()).append("\n");
                    message.append("CODE: ").append(code).append(" \n");
                    String errorMessage = "Ошибка! Несоответствие штрихкода:\nKM: " + aggregationValidationDTO.getCode();
                    message.append(errorMessage);
                    throw validationService.badRequestExceptionThrow(message.toString()).get();
                }
            }
            return code;
        } else {
            throw validationService.badRequestExceptionThrow("Код маркировки отсутствует в системе").get();
        }
    }

    private boolean validateMark(AggregationValidationDTO aggregationValidationDTO) {
        String code = MarkUtils.clearOriginalCode(aggregationValidationDTO.getCode(), null);
        // marks can be only child. If mark sent like not child throw exception
        if (aggregationValidationDTO.isChild())
            throw validationService.badRequestExceptionThrow("Маркировок не может быть родителем").get();

        // find mark
        Mark mark = markRepository.findFirstByCode(code).orElseThrow(validationService.entityNotFoundExceptionThrow(Mark.class.getSimpleName(), "code", code));

        // Validate this rule "Mark and product barcode must be compatible"
        if (!MarkUtils.checkIsEqualBarcodes(aggregationValidationDTO.getBarcode(), mark.getOriginalCode()))
            throw validationService.badRequestExceptionThrow("Ошибка! Несоответствие штрихкода:\nKM: " + aggregationValidationDTO.getCode() + "\nПродукция: " + mark.getProduct().getName()).get();

        //check mark is not used
        if (mark.isUsed()) {
            if (mark.getParentCode() != null) {
                throw validationService.badRequestExceptionThrow("Код состоит в агрегации КA: ", HttpStatus.BAD_REQUEST + mark.getParentCode()).get();
            }
            throw validationService.badRequestExceptionThrow("Код уже использован:" + aggregationValidationDTO.getCode()).get();
        }
        if (aggregationValidationDTO.isCheckAslBelgi()) {
            validateCodeFromAsilBelgi(aggregationValidationDTO, false, true);
        }

        // Congrats ! Mark can aggregate
        return true;
    }

    private boolean validateAggregation(AggregationValidationDTO aggregationValidationDTO) {
        String code = MarkUtils.clearOriginalCode(aggregationValidationDTO.getCode(), null);
        Aggregation aggregation = aggregationRepository.findFirstByCode(code).orElseThrow(validationService.entityNotFoundExceptionThrow(Aggregation.class.getSimpleName(), "code", code));

        // if code is sent like child aggregation must be already aggregated
        if (aggregationValidationDTO.isChild()) {
            if (AggregationStatus.CREATED.equals(aggregation.getStatus())) {
                // Congrats ! Can use this aggregation
                // Validate this rule "Mark and product barcode must be compatible"
                if (!MarkUtils.checkIsEqualBarcodes(aggregationValidationDTO.getBarcode(), aggregation.getProduct().getBarcode())) {
                    throw validationService.badRequestExceptionThrow("Ошибка! Несоответствие штрихкода:\nKM: " + aggregationValidationDTO.getCode() + "\nПродукция: " + aggregation.getProduct().getName()).get();
                }
                if (aggregation.getParentCode() != null) {
                    throw validationService.badRequestExceptionThrow("Код состоит в агрегации КA: " + aggregation.getParentCode()).get();
                }
                return true;
            } else {
                throw validationService.badRequestExceptionThrow("Код не состоит в агрегации").get();
            }
        }
        // if code is sent like child aggregation must be not used
        else {
            if (AggregationStatus.DRAFT.equals(aggregation.getStatus()) && aggregation.getParentCode() == null) {
                // Congrats ! Can use this aggregation
                return true;
            } else {
                if (aggregation.getParentCode() != null) {
                    throw validationService.badRequestExceptionThrow("Код состоит в агрегации КA: " + aggregation.getParentCode()).get();
                }
                throw validationService.badRequestExceptionThrow("Код уже использован").get();
            }
        }
    }

    private boolean validateBlockAggregation(AggregationValidationDTO aggregationValidationDTO) {
        String code = MarkUtils.clearOriginalCode(aggregationValidationDTO.getCode(), null);
        AggregationBlock aggregation = aggregationBlockRepository.findFirstByCode(code).orElseThrow(validationService.entityNotFoundExceptionThrow(Aggregation.class.getSimpleName(), "code", code));

        // Validate this rule "Mark and product barcode must be compatible"
        if (!MarkUtils.checkIsEqualBarcodes(aggregationValidationDTO.getBarcode(), aggregation.getCode())) {
            throw validationService.badRequestExceptionThrow("Ошибка! Несоответствие штрихкода:\nKM: " + aggregationValidationDTO.getCode() + "\nПродукция: " + aggregation.getProduct().getName()).get();
        }

        // if block is child it must be aggregated
        if (aggregationValidationDTO.isChild()) {

            if (AggregationStatus.CREATED.equals(aggregation.getStatus())) {
                if (aggregation.getParentCode() != null) {
                    throw validationService.badRequestExceptionThrow("Код состоит в агрегации КA: " + aggregation.getParentCode()).get();
                }
                if (aggregationValidationDTO.isCheckAslBelgi()) {
                    validateCodeFromAsilBelgi(aggregationValidationDTO, true, false);
                }
                // Congrats ! Can use this block aggregation
                return true;
            } else {
                throw validationService.badRequestExceptionThrow("Код не состоит в агрегации").get();
            }
        }
        // if block is not child it must be not used
        else {
            if (AggregationStatus.DRAFT.equals(aggregation.getStatus()) && aggregation.getParentCode() == null && !aggregation.isUsed()) {
                // Congrats ! Can use this block aggregation
                return true;
            } else {
                if (aggregation.getParentCode() != null) {
                    throw validationService.badRequestExceptionThrow("Код состоит в агрегации КA: " + aggregation.getParentCode()).get();
                } else {
                    throw validationService.badRequestExceptionThrow("Код состоит в агрегации КA: не найден").get();
                }
            }
        }
    }

    public CisInfoResponse[] checkTuron(List<String> codes) {
        Company company = getCurrentCompany();
        CompanyAggregationInfo companyAggregationInfo = validateAslBelgiAndGetAggregationInfo();
        Set<String> printCodes = codes.stream().map(code -> MarkUtils.clearOriginalCode(code, null)).collect(Collectors.toSet());
        return aslBelgiClient.cisesInfo(company.getTin(), companyAggregationInfo.getAslBelgiToken(), printCodes);
    }

    public AggregationStatisticsDTO getStatistics(AggregationFilter filter) {
        return aggregationRepository.getStatistics(filter);
    }

    @Transactional
    public void removeFromPartyProduct(Long id) {
        aggregationRepository.unsetAggregationPartyProductIdById(id);
    }
}
