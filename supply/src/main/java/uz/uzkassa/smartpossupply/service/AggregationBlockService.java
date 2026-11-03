package uz.uzkassa.smartpossupply.service;

import uz.uzkassa.smartpossupply.constants.MessageConstants;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import uz.uzkassa.smartpossupply.domain.aggregation.AggregationBlock;
import uz.uzkassa.smartpossupply.domain.billing.UsagePlan;
import uz.uzkassa.smartpossupply.domain.company.Company;
import uz.uzkassa.smartpossupply.domain.company.CompanyAggregationInfo;
import uz.uzkassa.smartpossupply.domain.mark.Mark;
import uz.uzkassa.smartpossupply.domain.product.Product;
import uz.uzkassa.smartpossupply.dto.aggregation.AggregationBlockDetailDTO;
import uz.uzkassa.smartpossupply.dto.aggregation.AggregationBlockListDTO;
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
import uz.uzkassa.smartpossupply.dto.payload.PartyUtilisationPayload;
import uz.uzkassa.smartpossupply.enums.AggregationStatus;
import uz.uzkassa.smartpossupply.enums.CountableServiceTypes;
import uz.uzkassa.smartpossupply.enums.MarkStatus;
import uz.uzkassa.smartpossupply.enums.PackageType;
import uz.uzkassa.smartpossupply.enums.ProductGroup;
import uz.uzkassa.smartpossupply.enums.TuronMarkStatus;
import uz.uzkassa.smartpossupply.filters.AggregationBlockFilter;
import uz.uzkassa.smartpossupply.filters.AggregationFilter;
import uz.uzkassa.smartpossupply.integration.TuronClient;
import uz.uzkassa.smartpossupply.repository.AggregationBlockRepository;
import uz.uzkassa.smartpossupply.repository.AggregationRepository;
import uz.uzkassa.smartpossupply.repository.MarkRepository;
import uz.uzkassa.smartpossupply.repository.ProductRepository;
import uz.uzkassa.smartpossupply.utils.MarkUtils;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static lombok.AccessLevel.PRIVATE;

@Slf4j
@Service
@Transactional(readOnly = true)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AggregationBlockService extends AggregationBaseService {

    AggregationRepository aggregationRepository;
    AggregationBlockRepository aggregationBlockRepository;
    ProductRepository productRepository;
    MarkRepository markRepository;
    TuronClient turonClient;
    UsagePlanService usagePlanService;

    @Transactional
    public AggregationStatus create(AggregationPayload aggregationPayload, Company company) {
        CompanyAggregationInfo companyAggregationInfo = validateTuronAndGetAggregationInfo();
        if (CollectionUtils.isEmpty(aggregationPayload.getMarks())) {
            throw validationService.badRequestExceptionThrow(localizationService.localize(MessageConstants.MARK_CODE_ERROR), "Коды маркировки отсутствуют.").get();
        } else if (aggregationPayload.getMarks().size() != aggregationPayload.getQty()) {
            throw validationService.badRequestExceptionThrow(localizationService.localize(MessageConstants.INCORRECT_QUANTITY_KM), "Неверное Количество КМ").get();
        }
        if (aggregationPayload.getChildProductId() == null) {
            throw validationService.badRequestExceptionThrow("Необходимо указать штрих-код КМ").get();
        }

        UsagePlan usagePlan = null;
        if (!company.isFreeTrial()) {
            usagePlan = usagePlanService.getUsagePlan(CountableServiceTypes.AGGREGATION.getCode());
            if (usagePlan.getQty() <= 0) {
                throw validationService.badRequestExceptionThrow("Недостаточно баланса для агрегации", "Available:" + usagePlan.getQty()).get();
            }
        }

        Product childProduct = productRepository
                .findById(aggregationPayload.getChildProductId())
                .orElseThrow(validationService.entityNotFoundExceptionThrow(Product.class.getSimpleName(), "id", aggregationPayload.getChildProductId()));

        List<String> snTins = aggregationPayload
                .getMarks()
                .stream()
                .map(code -> MarkUtils.clearOriginalCode(code, childProduct.getProductType()))
                .toList();

        List<String> usedMarks = markRepository.findAllByCodeInAndUsedIsTrue(snTins);
        if (!CollectionUtils.isEmpty(usedMarks)) {
            throw validationService.badRequestExceptionThrow("Коды уже использованы").get();
        }

        List<String> snTinsOther = snTins.stream().filter(code -> !MarkUtils.checkIsEqualBarcodes(childProduct.getBarcode(), code)).toList();

        if (!CollectionUtils.isEmpty(snTinsOther)) {
            throw validationService.badRequestExceptionThrow("Ошибка! Несоответствие штрих кода:KM: " + aggregationPayload.getPrintCode()).get();
        }

        String code = MarkUtils.clearOriginalCode(aggregationPayload.getPrintCode(), childProduct.getProductType());
        AggregationBlock aggregationBlock;
        Optional<AggregationBlock> optionalAggregationBlock = aggregationBlockRepository.findFirstByCode(code);
        if (optionalAggregationBlock.isPresent()) {
            aggregationBlock = optionalAggregationBlock.get();
        } else {
            if (!MarkUtils.tobaccoBlockMatcher(aggregationPayload.getPrintCode())) {
                throw validationService.badRequestExceptionThrow("Не соответствует формат код маркировки: " + aggregationPayload.getPrintCode()).get();
            }
            String cutSimpleCode = MarkUtils.clearMarkCigaretteBlock(aggregationPayload.getPrintCode());
            CisInfo cisInfo = cisesInfoWrapper(cutSimpleCode);
            if (cisInfo == null)
                throw validationService.badRequestExceptionThrow("Ошибка при проверке кода. Код не найден: " + aggregationPayload.getPrintCode()).get();
            if (cisInfo.getParent() != null)
                throw validationService.badRequestExceptionThrow("Ошибка при проверке кода. Код состоит другой агрегации: " + cisInfo.getParent()).get();

            if (TuronMarkStatus.APPLIED.equals(cisInfo.getStatus())) {
                if (!CollectionUtils.isEmpty(cisInfo.getChild()))
                    throw validationService.badRequestExceptionThrow("Код уже использован " + usedMarks).get();

                aggregationBlock = new AggregationBlock();
                aggregationBlock.setProductId(aggregationPayload.getProductId());
                aggregationBlock.setCode(cutSimpleCode);
                aggregationBlock.setPrintCode(aggregationPayload.getPrintCode());
                aggregationBlock.setStatus(AggregationStatus.DRAFT);
                aggregationBlock.setCreatedDate(LocalDateTime.now());
                aggregationBlock.setCompanyId(company.getId());
                aggregationBlockRepository.save(aggregationBlock);
            } else
                throw validationService.badRequestExceptionThrow("Ошибка при проверке кода. Статус: " + cisInfo.getStatus().getText()).get();
        }

        if (AggregationStatus.IN_GROUP.equals(aggregationBlock.getStatus()) || aggregationBlock.getPartyProduct() != null) {
            throw validationService.badRequestExceptionThrow("Код аггрегации уже использован внутри:" + aggregationBlock.getProduct().toCommonDTO()).get();
        }
        if (!aggregationPayload.getProductId().equals(aggregationBlock.getProductId())) {
            throw validationService.badRequestExceptionThrow("Ошибка! Несоответствие штрихкода:\nKM: " + aggregationPayload.getPrintCode() + "\n").get();
        }
        aggregationBlock.setQty(aggregationPayload.getQty());

        //Turon aggregation
        if (aggregationPayload.isSendToTuron()) {
            TuronAggregationItem aggregationItem = new TuronAggregationItem();
            aggregationItem.setAggregatedItemsCount(aggregationPayload.getQty());
            aggregationItem.setAggregationUnitCapacity(aggregationPayload.getQty());
            aggregationItem.setAggregationType(TuronAggregationType.AGGREGATION);
            aggregationItem.setUnitSerialNumber(aggregationBlock.getCode());
            aggregationItem.setSntins(snTins);

            TuronCreateAggregationRequest request = new TuronCreateAggregationRequest(
                    Collections.singletonList(aggregationItem),
                    company.getTin(),
                    aggregationBlock.getProductId().toString()
            );
            if (ProductGroup.Tobacco.equals(company.getProductGroup())) {
                request.setProductionLineId(company.getId().toString());
            }
            TuronCreateAggregationResponse response = turonClient.createAggregation(
                    request,
                    company.getProductGroup().getExtension(),
                    companyAggregationInfo.getTuronToken(),
                    companyAggregationInfo.getOmsId()
            );
            if (response == null) {
                aggregationBlock.setStatus(AggregationStatus.ERROR);
            } else {
                aggregationBlock.setReportId(response.getReportId());
                aggregationBlock.setAggregationDate(LocalDateTime.now());
                aggregationBlock.setStatus(AggregationStatus.CREATED);
            }
        } else {
            aggregationBlock.setStatus(AggregationStatus.IN_GROUP);
        }
        aggregationBlock.setPartyProductId(aggregationPayload.getPartyProductId());
        aggregationBlock.setUsed(true);
        aggregationBlockRepository.save(aggregationBlock);
        saveMarks(aggregationPayload, childProduct, aggregationBlock);

        if (usagePlan != null) {
            usagePlan.setQty(usagePlan.getQty() - 1);
            usagePlanRepository.save(usagePlan);
        }
        return aggregationBlock.getStatus();
    }

    @Transactional
    public void saveMarks(AggregationPayload aggregationPayload, Product product, AggregationBlock parentAggregationBlock) {
        List<Mark> marks = aggregationPayload.getMarks()
                .stream()
                .map(printCode -> {
                    String code = MarkUtils.clearOriginalCode(printCode, product.getProductType());
                    Mark mark = markRepository.findFirstByCode(code).orElseGet(Mark::new);
                    if (mark.getId() == null) {
                        mark.setOriginalCode(printCode);
                        mark.setCode(code);
                        mark.setProductId(product.getId());
                        mark.setMarkOrderPartyId(parentAggregationBlock.getMarkOrderPartyId());
                        mark.setCreatedDate(LocalDateTime.now());
                        mark.setCompanyId(getCurrentCompanyId());
                    }
                    mark.setPartyProductId(aggregationPayload.getPartyProductId());
                    mark.setParentCode(parentAggregationBlock.getCode());
                    mark.setUsed(true);
                    return mark;
                })
                .toList();
        markRepository.saveAll(marks);
    }

    public AggregationBlockDetailDTO get(Long id) {
        return aggregationBlockRepository
                .getDetail(id)
                .map(aggregationBlock -> {
                    AggregationBlockDetailDTO aggregationBlockDetailDTO = new AggregationBlockDetailDTO();
                    aggregationBlockDetailDTO.setProduct(aggregationBlock.getProduct().toCommonDTO());
                    aggregationBlockDetailDTO.setQty(aggregationBlock.getQty());
                    aggregationBlockDetailDTO.setCode(aggregationBlock.getCode());
                    aggregationBlockDetailDTO.setPrintCode(aggregationBlock.getPrintCode());
                    aggregationBlockDetailDTO.setPackageType(PackageType.BLOCK);
                    aggregationBlockDetailDTO.setReportId(aggregationBlock.getReportId());
                    markRepository
                            .findAllByParentCodeOrderByCreatedDateDesc(aggregationBlock.getCode())
                            .forEach(mark -> {
                                aggregationBlockDetailDTO.setChildProduct(mark.getProduct().toCommonDTO());
                                aggregationBlockDetailDTO.addMark(mark.getOriginalCode());
                            });
                    return aggregationBlockDetailDTO;
                })
                .orElseThrow(validationService.entityNotFoundExceptionThrow(AggregationBlock.class.getSimpleName(), "id", id));
    }

    public Page<AggregationBlockListDTO> getList(AggregationBlockFilter filter) {
        if (StringUtils.isNotEmpty(filter.getSearch())) {
            String code = MarkUtils.clearOriginalCode(filter.getSearch(), null);
            if (MarkUtils.isMark(filter.getSearch())) {
                markRepository.findFirstByCode(code).ifPresent(mark -> filter.setSearch(mark.getParentCode()));
            } else {
                filter.setSearch(code);
            }
        }
        filter.setOrderBy("updatedDate");

        ResultList<AggregationBlock> resultList = aggregationBlockRepository.getResultList(filter);
        List<AggregationBlockListDTO> result = resultList
                .getList()
                .stream()
                .map(aggregationBlock -> {
                    AggregationBlockListDTO aggregationBlockListDTO = new AggregationBlockListDTO();
                    aggregationBlockListDTO.setId(aggregationBlock.getId());
                    aggregationBlockListDTO.setCode(aggregationBlock.getCode());
                    aggregationBlockListDTO.setPrintCode(aggregationBlock.getPrintCode());
                    if (aggregationBlock.getProduct() != null) {
                        aggregationBlockListDTO.setProduct(aggregationBlock.getProduct().toProductCommonDTO());
                    }
                    aggregationBlockListDTO.setStatus(aggregationBlock.getStatus());
                    aggregationBlockListDTO.setPackageType(PackageType.BLOCK);
                    aggregationBlockListDTO.setQty(aggregationBlock.getQty());
                    aggregationBlockListDTO.setCreatedDate(aggregationBlock.getCreatedDate());
                    aggregationBlockListDTO.setAggregatedDate(aggregationBlock.getAggregationDate());
                    aggregationBlockListDTO.setCreator(aggregationBlock.getCreator().toCommonDTO());
                    aggregationBlockListDTO.setTuronStatus(aggregationBlock.getTuronStatus());
                    return aggregationBlockListDTO;
                })
                .toList();
        return new PageImpl<>(result, filter.getSortedPageable(), resultList.getCount());
    }

    public String validate(AggregationValidationDTO aggregationValidationDTO) {
        final boolean isBlock = MarkUtils.blockMatcher(aggregationValidationDTO.getCode());
        final boolean isPacks = MarkUtils.isMark(aggregationValidationDTO.getCode());

        if (aggregationValidationDTO.isChild() && isBlock) {
            throw validationService.badRequestExceptionThrow("Код не можете аггрегировать внутри блок.").get();
        }
        if (!isPacks && !isBlock) {
            throw validationService.badRequestExceptionThrow("Код не можете аггрегировать.").get();
        }
        if (
                !MarkUtils.checkIsEqualBarcodes(aggregationValidationDTO.getBarcode(), aggregationValidationDTO.getCode())
        )
            throw validationService.badRequestExceptionThrow("Ошибка! Несоответствие штрихкода:\nKM: " + aggregationValidationDTO.getCode() + "\n").get();

        Company company = getCurrentCompany();
        CompanyAggregationInfo companyAggregationInfo = validateAslBelgiAndGetAggregationInfo();

        final String code = MarkUtils.clearOriginalCode(aggregationValidationDTO.getCode(), null);
        if (!aggregationValidationDTO.isChild() && isBlock) {
            Optional<AggregationBlock> blockOptional = aggregationBlockRepository.findFirstByCode(code);
            if (blockOptional.isPresent()) {
                if (AggregationStatus.DRAFT.equals(blockOptional.get().getStatus())) {
                    if (aggregationValidationDTO.isCheckAslBelgi()) {
                        // run when block mark not found from db
                        CisInfoResponse[] result = aslBelgiClient.cisesInfo(company.getTin(), companyAggregationInfo.getAslBelgiToken(), Collections.singleton(code));
                        if (result != null && result.length > 0) {
                            CisInfo cisInfo = result[0].getCisInfo();
                            if (!TuronMarkStatus.APPLIED.equals(cisInfo.getStatus())) {
                                String errorMessage = "Код не может быть аггрегирован! Статус КМ : " + cisInfo.getStatus().getText();
                                StringBuilder message = new StringBuilder();
                                message.append("TIN:").append(company.getTin()).append(" \n");
                                message.append("CODE: ").append(code).append(" \n");
                                message.append(errorMessage);
                                throw validationService.badRequestExceptionThrow(message.toString()).get();
                            }
                            if (cisInfo.getParent() != null) {
                                throw validationService.badRequestExceptionThrow("Код состоит в агрегации").get();
                            }
                            if (!aggregationValidationDTO.isChild() && !CollectionUtils.isEmpty(cisInfo.getChild())) {
                                throw validationService.badRequestExceptionThrow("Код аггрегации уже был аггрегирован.").get();
                            }
                        }
                    }
                    return aggregationValidationDTO.getCode();
                } else {
                    if (AggregationStatus.CREATED.equals(blockOptional.get().getStatus())) {
                        if (blockOptional.get().getParentCode() != null) {
                            throw validationService.badRequestExceptionThrow("Код состоит в агрегации КA: " + blockOptional.get().getParentCode()).get();
                        }
                        throw validationService.badRequestExceptionThrow("Код состоит в агрегации").get();
                    } else if (aggregationValidationDTO.isCheckAslBelgi()) {
                        // run when block mark not found from db
                        CisInfoResponse[] result = aslBelgiClient.cisesInfo(company.getTin(), companyAggregationInfo.getAslBelgiToken(), Collections.singleton(code));
                        if (result != null && result.length > 0) {
                            CisInfo cisInfo = result[0].getCisInfo();
                            if (!TuronMarkStatus.APPLIED.equals(cisInfo.getStatus())) {
                                String errorMessage = "Код не может быть аггрегирован! Статус КМ : " + cisInfo.getStatus().getText();
                                StringBuilder message = new StringBuilder();
                                message.append("TIN:").append(company.getTin()).append(" \n");
                                message.append("CODE: ").append(code).append(" \n");
                                message.append(errorMessage);
                                throw validationService.badRequestExceptionThrow(message.toString()).get();
                            }
                            if (cisInfo.getParent() != null) {
                                throw validationService.badRequestExceptionThrow("Код состоит в агрегации").get();
                            }
                            if (!aggregationValidationDTO.isChild() && !CollectionUtils.isEmpty(cisInfo.getChild())) {
                                throw validationService.badRequestExceptionThrow("Код аггрегации уже был аггрегирован.").get();
                            }
                            return aggregationValidationDTO.getCode();
                        }
                    }

                }
            } else {
                // run when block mark not found from db
                CisInfoResponse[] result = aslBelgiClient.cisesInfo(company.getTin(), companyAggregationInfo.getAslBelgiToken(), Collections.singleton(code));
                if (result != null && result.length > 0) {
                    CisInfo cisInfo = result[0].getCisInfo();

                    if (!TuronMarkStatus.APPLIED.equals(cisInfo.getStatus())) {
                        String errorMessage = "Код не может быть аггрегирован! Статус КМ : " + cisInfo.getStatus().getText();
                        StringBuilder message = new StringBuilder();
                        message.append("TIN:").append(company.getTin()).append(" \n");
                        message.append("CODE: ").append(code).append(" \n");
                        message.append(errorMessage);
                        throw validationService.badRequestExceptionThrow(message.toString()).get();
                    }
                    if (cisInfo.getParent() != null) {
                        throw validationService.badRequestExceptionThrow("Код состоит в агрегации").get();
                    }
                    if (!aggregationValidationDTO.isChild() && !CollectionUtils.isEmpty(cisInfo.getChild())) {
                        throw validationService.badRequestExceptionThrow("Код аггрегации уже был аггрегирован.").get();
                    }
                    return aggregationValidationDTO.getCode();
                }
                throw validationService.badRequestExceptionThrow("Код аггрегации уже был использован. Или не найден!:" + aggregationValidationDTO.getCode()).get();
            }
        } else {

            Optional<Mark> markOptional = markRepository.findFirstByCode(code);
            if (markOptional.isPresent()) {
                if (markOptional.get().getParentCode() != null) {
                    throw validationService.badRequestExceptionThrow("Код состоит в агрегации").get();
                }
                if (markOptional.get().isUsed()) {
                    throw validationService.badRequestExceptionThrow("Код маркировки уже был использован").get();
                }
            } else {
                // not present in db
                CisInfoResponse[] result = aslBelgiClient.cisesInfo(company.getTin(), companyAggregationInfo.getAslBelgiToken(), Collections.singleton(code));
                if (result != null && result.length > 0) {
                    CisInfo cisInfo = result[0].getCisInfo();

                    if (!TuronMarkStatus.APPLIED.equals(cisInfo.getStatus())) {
                        String errorMessage = "Код не может быть аггрегирован! Статус КМ : " + cisInfo.getStatus().getText();
                        StringBuilder message = new StringBuilder();
                        message.append("TIN:").append(company.getTin()).append(" \n");
                        message.append("CODE: ").append(code).append(" \n");
                        message.append(errorMessage);
                        throw validationService.badRequestExceptionThrow(message.toString()).get();
                    }
                    if (cisInfo.getParent() != null) {
                        throw validationService.badRequestExceptionThrow("Код состоит в агрегации").get();
                    }
                    return aggregationValidationDTO.getCode();
                }
            }
        }

        return aggregationValidationDTO.getCode();
    }

    @Transactional
    public void resend(Long id) {
        AggregationBlock aggregationBlock = aggregationBlockRepository
                .findById(id)
                .orElseThrow(validationService.entityNotFoundExceptionThrow(AggregationBlock.class.getSimpleName(), "id", id));
        if (!AggregationStatus.ERROR.equals(aggregationBlock.getStatus())) {
            throw validationService.badRequestExceptionThrow("Аггрегация уже создана").get();
        }
        Company company = getCurrentCompany();
        List<String> markCodes = markRepository.findAllCodesByParentCodeOrderByCreatedDateDesc(aggregationBlock.getCode());
        if (CollectionUtils.isEmpty(markCodes)) {
            StringBuilder message = new StringBuilder("ПЕРЕОТПРАВКА КА в ТУРОН \n");
            message.append("ОТСУТСТВУЕТ КМ в базе \n");
            message.append("-------------------------------------\n");
            message.append("TIN: ").append(company.getTin()).append("\n");
            message.append("КА: ").append(aggregationBlock.getCode());
            log.error(message.toString());
            aggregationBlock.setChecked(true);
            aggregationBlockRepository.save(aggregationBlock);
            return;
        }
        CompanyAggregationInfo companyAggregationInfo = validateTuronAndGetAggregationInfo();
        TuronAggregationItem aggregationItem = new TuronAggregationItem();
        aggregationItem.setAggregatedItemsCount(aggregationBlock.getQty());
        aggregationItem.setAggregationUnitCapacity(aggregationBlock.getQty());
        aggregationItem.setAggregationType(TuronAggregationType.AGGREGATION);
        aggregationItem.setUnitSerialNumber(aggregationBlock.getPrintCode());
        aggregationItem.setSntins(markCodes);

        TuronCreateAggregationRequest request = new TuronCreateAggregationRequest(
                Collections.singletonList(aggregationItem),
                company.getTin()
        );
        TuronCreateAggregationResponse response = turonClient.createAggregation(
                request,
                company.getProductGroup().getExtension(),
                companyAggregationInfo.getTuronToken(),
                companyAggregationInfo.getOmsId()
        );

        if (response == null) {
            throw validationService.badRequestExceptionThrow("Timeout").get();
        } else {
            aggregationBlock.setReportId(response.getReportId());
            aggregationBlock.setAggregationDate(LocalDateTime.now());
            aggregationBlock.setStatus(AggregationStatus.CREATED);
        }
        aggregationBlockRepository.save(aggregationBlock);
    }

    public AggregationStatisticsDTO getStatistics(AggregationFilter filter) {
        return aggregationRepository.getStatistics(filter);
    }

    @Transactional
    public void unsetPartyProductIdById(Long id) {
        aggregationBlockRepository.unsetPartyProductIdById(id);
    }

    @Transactional
    public void setAppliedByParty(PartyUtilisationPayload payload) {
        Set<String> blocks = aggregationBlockRepository.findAllCodesByMarkOrderPartyId(payload.getId());
        Map<String, TuronMarkStatus> cises = aslBelgiClient.checkMarksCisesInfo(payload.getClientToken(), blocks);

        List<AggregationBlock> updatedBlockList = new LinkedList<>();
        for (String requestedCises : cises.keySet()) {
            Optional<AggregationBlock> blockOptional = aggregationBlockRepository.findFirstByCode(MarkUtils.clearOriginalCode(requestedCises, payload.getProductType()));
            if (blockOptional.isPresent()) {
                blockOptional.get().setTuronStatus(MarkStatus.valueOf(cises.get(requestedCises).name()));
                updatedBlockList.add(blockOptional.get());
            }
            if (updatedBlockList.size() > 50) {
                aggregationBlockRepository.saveAll(updatedBlockList);
            }
        }
        if (!CollectionUtils.isEmpty(updatedBlockList)) {
            aggregationBlockRepository.saveAll(updatedBlockList);
        }
    }
}
