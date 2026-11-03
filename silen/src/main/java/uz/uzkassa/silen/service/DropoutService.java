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
import uz.uzkassa.silen.domain.Dropout;
import uz.uzkassa.silen.domain.Organization;
import uz.uzkassa.silen.domain.mongo.Block;
import uz.uzkassa.silen.domain.mongo.Mark;
import uz.uzkassa.silen.domain.mongo.ValidationCode;
import uz.uzkassa.silen.dto.*;
import uz.uzkassa.silen.dto.filter.DropoutFilter;
import uz.uzkassa.silen.dto.filter.MongoFilter;
import uz.uzkassa.silen.dto.mq.WarehousePayload;
import uz.uzkassa.silen.enumeration.*;
import uz.uzkassa.silen.exceptions.BadRequestException;
import uz.uzkassa.silen.integration.AsilBelgisiClient;
import uz.uzkassa.silen.integration.TuronClient;
import uz.uzkassa.silen.integration.asilbelgi.dto.CisInfoResponse;
import uz.uzkassa.silen.integration.turon.DropoutRequest;
import uz.uzkassa.silen.integration.turon.DropoutResponse;
import uz.uzkassa.silen.kafka.KafkaConstants;
import uz.uzkassa.silen.kafka.producer.KafkaProducer;
import uz.uzkassa.silen.repository.mongo.BlockRepository;
import uz.uzkassa.silen.repository.mongo.MarkRepository;
import uz.uzkassa.silen.repository.mongo.ValidationCodeRepository;
import uz.uzkassa.silen.repository.pg.AggregationRepository;
import uz.uzkassa.silen.repository.pg.DropoutRepository;
import uz.uzkassa.silen.repository.pg.ProductRepository;
import uz.uzkassa.silen.utils.Utils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DropoutService extends BaseService {
    AggregationRepository aggregationRepository;
    MarkRepository markRepository;
    DropoutRepository dropoutRepository;
    TuronClient turonClient;
    ProductRepository productRepository;
    AsilBelgisiClient asilBelgisiClient;
    BlockRepository blockRepository;
    KafkaProducer<WarehousePayload> kafkaProducer;
    ValidationCodeRepository validationCodeRepository;

    static EnumSet<AggregationStatus> checkParentStatus = EnumSet.of(AggregationStatus.RETURNED, AggregationStatus.CREATED, AggregationStatus.UPDATED);

    public Page<DropoutListDTO> findAllByFilter(DropoutFilter filter) {
        return dropoutRepository.findAllByFilter(filter).map(Dropout::toDTO);
    }

    public DropoutStatsDTO findAllByFilterStats(DropoutFilter filter) {
        return dropoutRepository.findAllByFilterStats(filter);
    }

    public DropoutListDTO get(String id) {
        return dropoutRepository.findById(id).orElseThrow(notFound()).toDTO();
    }

    public DropoutCheckDTO validate(DropoutValidateDTO validateDTO) {
        final String code = Utils.clearPrintCode(validateDTO.getPrintCode());

        if (Utils.isAggregation(validateDTO.getPrintCode())) {
            final String code18 = Utils.clearAggregationCode(validateDTO.getPrintCode());
            Aggregation aggregation = aggregationRepository.findFirstByCodeOrCode(code, code18).orElseThrow(badRequest("Код не найден"));
            if (aggregation.getParentCode() != null) {
                validateByParent(validateDTO, aggregation.getParentCode());
            } else if (!Arrays.asList(AggregationStatus.RETURNED, AggregationStatus.CREATED).contains(aggregation.getStatus()))
                throw badRequest("Код не сможете списат. Код статус: " + aggregation.getStatus().getText()).get();

            DropoutCheckDTO dropoutCheckDTO = new DropoutCheckDTO();
            dropoutCheckDTO.setPrintCode(aggregation.getPrintCode());
            dropoutCheckDTO.setParentCode(aggregation.getParentCode());
            dropoutCheckDTO.setPackageType(CodeType.AGGREGATION);
            dropoutCheckDTO.setPackageTypeName(CodeType.AGGREGATION.getNameRu());
            dropoutCheckDTO.setProduct(new ProductCommonDTO(aggregation.getProductId(), aggregation.getProduct().getVisibleName(), aggregation.getProduct().getBarcode()));
            return dropoutCheckDTO;
        }
        Mark mark = markRepository.findFirstByCode(code).orElseThrow(badRequest("Код не найден"));
        if (mark.getParentCode() != null) {
            validateByParent(validateDTO, mark.getParentCode());
        } else if (!Arrays.asList(AggregationStatus.RETURNED, AggregationStatus.CREATED).contains(mark.getStatus())) {
            throw badRequest("Код не сможете списат. Код статус: " + mark.getStatus().getText()).get();
        }
        DropoutCheckDTO dropoutCheckDTO = new DropoutCheckDTO();
        dropoutCheckDTO.setPrintCode(mark.getPrintCode());
        dropoutCheckDTO.setParentCode(mark.getParentCode());
        dropoutCheckDTO.setPackageType(mark.isBlock() ? CodeType.BLOCK : CodeType.MARK);
        dropoutCheckDTO.setPackageTypeName(dropoutCheckDTO.getPackageType().getNameRu());
        productRepository.findById(mark.getProductId()).ifPresent(product -> dropoutCheckDTO.setProduct(new ProductCommonDTO(product.getId(), product.getVisibleName(), product.getBarcode())));
        return dropoutCheckDTO;
    }

    public void validateByParent(DropoutValidateDTO validateDTO, String parentCode) {
        if (Utils.isAggregation(parentCode)) {
            Aggregation boxOrPallet = aggregationRepository.findFirstByCode(parentCode).orElseThrow(badRequest("Код не найден"));
            if (!checkParentStatus.contains(boxOrPallet.getStatus())) {
                throw badRequest("Код не сможете списат. Код статус: " + boxOrPallet.getStatus().getText()).get();
            }
            if (boxOrPallet.getParentCode() == null) {
                return;
            }
            validateByParent(validateDTO, boxOrPallet.getParentCode());
        } else {
            Block block = blockRepository.findFirstByCode(parentCode).orElseThrow(badRequest("Код не найден"));
            if (!checkParentStatus.contains(block.getStatus())) {
                throw badRequest("Код не сможете списат. Код статус: " + block.getStatus().getText()).get();
            }
            if (block.getParentCode() == null) {
                return;
            }
            validateByParent(validateDTO, block.getParentCode());
        }
    }

    public CisInfoResponse[] partyCheck(MongoFilter mongoFilter) {
        final Organization organization = getCurrentOrganization();
        if (StringUtils.isAnyEmpty(organization.getOmsId(), organization.getTuronToken())) {
            throw badRequest("В данной организации отсутствует OMS_ID или TOKEN").get();
        }

        Page<Mark> allByFilter = markRepository.findAllByFilter(mongoFilter);

        Set<String> dropoutMarks = allByFilter.stream().map(Mark::getPrintCode).collect(Collectors.toSet());

        if (CollectionUtils.isEmpty(dropoutMarks)) {
            throw badRequest("В запросе не указан ни один КМ").get();
        }
        return asilBelgisiClient.cisesInfo(getCurrentOrganizationAsilBelgiToken(organization.getTin()), dropoutMarks);
    }

    @Transactional
    public List<String> create(final DropoutDTO dropoutDTO) {
        final Organization organization = getCurrentOrganization();
        final Set<String> sntins = new LinkedHashSet<>();
        sntins.addAll(dropoutDTO.getAggregations().stream().map(Utils::clearAggregationCode20).collect(Collectors.toSet()));
        sntins.addAll(dropoutDTO.getBlocks()/*.stream().map(Utils::clearAggregationCode).collect(Collectors.toSet())*/);
        sntins.addAll(dropoutDTO.getMarks());
        // send dropout
        final DropoutRequest dropoutRequest = DropoutRequest.builder()
                .participantId(organization.getTin())
                .address(organization.getAddress())
                .dropoutReason(dropoutDTO.getDropoutReason().name())
                .withChild(true)
                .sntins(sntins)
                .build();
        final DropoutResponse dropoutResponse = turonClient.dropout(dropoutRequest, organization.getProductGroup().getExtension(), organization.getTuronToken(), organization.getOmsId());

        if (StringUtils.isEmpty(dropoutResponse.getReportId())) {
            throw new BadRequestException("Списания прошло не успешно!");
        }

        final List<String> errorCodes = new LinkedList<>();
        if (CollectionUtils.isNotEmpty(dropoutDTO.getAggregations())) {
            errorCodes.addAll(dropoutAggregation(dropoutDTO, dropoutResponse.getReportId(), true));
        }
        if (CollectionUtils.isNotEmpty(dropoutDTO.getBlocks())) {
            errorCodes.addAll(dropoutBlock(dropoutDTO, dropoutResponse.getReportId(), true));
        }
        if (CollectionUtils.isNotEmpty(dropoutDTO.getMarks())) {
            errorCodes.addAll(dropoutMark(dropoutDTO, dropoutResponse.getReportId(), true));
        }
        return errorCodes;
    }

    public void sendBatchQueue() {
        final List<String> organizationIds = dropoutRepository.findAllAggregationsOrg(CodeType.AGGREGATION);
        for (String organizationId : organizationIds) {
            sendBatch(organizationId);
        }
    }

    public void sendBatch(final String organizationId) {
        final Organization organization = organizationRepository.getReferenceById(organizationId);
        final List<String> codes = dropoutRepository.findAllAggregationsByOrg(CodeType.AGGREGATION, organizationId);
        // send dropout
        final DropoutRequest dropoutRequest = new DropoutRequest();
        dropoutRequest.setParticipantId(organization.getTin());
        dropoutRequest.setAddress(organization.getAddress());
        dropoutRequest.setDropoutReason(DropoutReason.DEFECT.name());
        dropoutRequest.setWithChild(true);
        dropoutRequest.setSntins(codes.stream().map(Utils::clearAggregationCode).collect(Collectors.toSet()));
        final DropoutResponse dropoutResponse = turonClient.dropout(dropoutRequest, organization.getProductGroup().getExtension(), organization.getTuronToken(), organization.getOmsId());

        if (StringUtils.isEmpty(dropoutResponse.getReportId())) {
            throw new BadRequestException("Списания прошло не успешно! " + organization.getName());
        }
    }

    @Transactional
    public Set<String> dropoutMark(DropoutDTO dropoutDTO, String reportId, boolean writeToDropout) {
        Set<String> errorCodes = new HashSet<>(dropoutDTO.getMarks());
        final Set<String> cleanCodes = dropoutDTO.getMarks().stream().map(Utils::clearPrintCode).collect(Collectors.toSet());
        List<Mark> markList = markRepository.findAllByCodeIn(cleanCodes);

        for (Mark mark : markList) {
            mark.setUsed(true);
            mark.setStatus(AggregationStatus.DROPOUT);
            markRepository.save(mark);
            if (writeToDropout) {
                Dropout dropout = new Dropout();
                dropout.setOrganizationId(mark.getOrganizationId());
                dropout.setReportId(reportId);
                dropout.setDropoutReason(dropoutDTO.getDropoutReason());
                dropout.setCode(mark.getCode());
                dropout.setPrintCode(mark.getPrintCode());
                dropout.setCodeId(mark.getId());
                dropout.setCodeType(CodeType.MARK);
                dropout.setProductId(mark.getProductId());
                dropout.setAmount(1);

                dropoutRepository.save(dropout);

                if (mark.getParentCode() != null) {
                    aggregationRepository.findFirstByCode(mark.getParentCode()).map(parent -> {
                        if (AggregationStatus.UPDATED != parent.getStatus()) {
                            parent.setStatus(AggregationStatus.UPDATED);
                            parent.setErrorMessage(localize("code.dropout.message", "", "маркировок", mark.getPrintCode()));
                            aggregationRepository.save(parent);
                        }
                        if (parent.getSerialId() != null) {
                            dropout.setSerialId(parent.getSerialId());
                        }
                        return parent;
                    }).orElseGet(() -> {
                        blockRepository.findFirstByCode(mark.getParentCode()).ifPresent(parent -> {
                            if (AggregationStatus.UPDATED != parent.getStatus()) {
                                parent.setStatus(AggregationStatus.UPDATED);
                                parent.setErrorMessage(localize("code.dropout.message", "", "маркировок", mark.getPrintCode()));
                                blockRepository.save(parent);
                            }
                            if (parent.getSerialId() != null) {
                                dropout.setSerialId(parent.getSerialId());
                            }
                        });
                        return null;
                    });
                }
            }
            errorCodes.remove(mark.getPrintCode());
        }

        return errorCodes;
    }

    @Transactional
    public Set<String> dropoutBlock(DropoutDTO dropoutDTO, String reportId, boolean writeToDropout) {
        Set<String> errorCodes = new HashSet<>(dropoutDTO.getBlocks());
        final Set<String> cleanCodes = dropoutDTO.getBlocks().stream().map(Utils::clearPrintCode).collect(Collectors.toSet());
        List<Block> blocks = blockRepository.findAllByCodeIn(cleanCodes);

        for (Block block : blocks) {
            block.setStatus(AggregationStatus.DROPOUT);
            block.setErrorMessage(AggregationStatus.DROPOUT.getText());
            blockRepository.save(block);

            if (writeToDropout) {
                Dropout dropout = new Dropout();
                dropout.setReportId(reportId);
                dropout.setDropoutReason(dropoutDTO.getDropoutReason());
                dropout.setCodeType(CodeType.BLOCK);
                dropout.setCodeId(block.getId());
                dropout.setCode(block.getCode());
                dropout.setPrintCode(block.getPrintCode());
                dropout.setProductId(block.getProductId());
                dropout.setOrganizationId(block.getOrganizationId());
                dropout.setAmount(block.getQuantity());
                dropout.setSerialId(block.getSerialId());
                dropoutRepository.save(dropout);

                if (block.getParentCode() != null) {
                    aggregationRepository.findFirstByCode(block.getParentCode()).ifPresent(aggregation -> {
                        aggregation.setStatus(AggregationStatus.UPDATED);
                        aggregation.setErrorMessage(localize("code.dropout.message", block.getErrorMessage(), "блок", block.getPrintCode()));
                        aggregationRepository.save(aggregation);
                    });
                }
            }
            markRepository.findAllByParentCode(block.getCode()).forEach(mark -> {
                mark.setUsed(true);
                mark.setStatus(AggregationStatus.DROPOUT);
                markRepository.save(mark);
            });
            errorCodes.remove(block.getPrintCode());

            WarehousePayload payload = new WarehousePayload();
            payload.setOperation(WarehouseOperation.Dropout);
            payload.setNote("Списано");
            payload.setUnit(block.getUnit());
            payload.setAlcoholCount(BigDecimal.valueOf(block.getQuantity()));
            payload.setOperationDate(LocalDateTime.now());
            payload.setOrganizationId(block.getOrganizationId());
            payload.setProductId(block.getProductId());
            payload.setCode(block.getCode());
            kafkaProducer.sendMessage(KafkaConstants.WAREHOUSE_TOPIC, payload);
        }

        return errorCodes;
    }

    @Transactional
    public Set<String> dropoutAggregation(DropoutDTO dropoutDTO, String reportId, boolean writeToDropout) {
        final Set<String> errorCodes = new LinkedHashSet<>();
        for (String printCode : dropoutDTO.getAggregations()) {
            errorCodes.addAll(Utils.clearAggregationPrintCode(printCode));
        }
        List<Aggregation> aggregations = aggregationRepository.findAllByCodeInAndStatusIn(errorCodes, Arrays.asList(AggregationStatus.CREATED, AggregationStatus.RETURNED));
        for (Aggregation aggregation : aggregations) {
            aggregation.setStatus(AggregationStatus.DROPOUT);
            aggregation.setDescription(AggregationStatus.DROPOUT.getText());
            aggregationRepository.save(aggregation);

            if (writeToDropout) {
                Dropout dropout = new Dropout();
                dropout.setOrganizationId(aggregation.getOrganizationId());
                dropout.setReportId(reportId);
                dropout.setDropoutReason(dropoutDTO.getDropoutReason());
                dropout.setCode(aggregation.getCode());
                dropout.setPrintCode(aggregation.getPrintCode());
                dropout.setCodeId(aggregation.getId());
                dropout.setCodeType(CodeType.AGGREGATION);
                dropout.setProductId(aggregation.getProductId());
                dropout.setAmount(aggregation.getQuantity());
                dropout.setSerialId(aggregation.getSerialId());
                dropoutRepository.save(dropout);

                if (aggregation.getParentCode() != null) {
                    aggregationRepository.findFirstByCode(aggregation.getParentCode()).ifPresent(pallet -> {
                        pallet.setStatus(AggregationStatus.UPDATED);
                        pallet.setErrorMessage(localize("code.dropout.message", aggregation.getErrorMessage(), "аггрегация", aggregation.getPrintCode()));
                        aggregationRepository.save(pallet);
                    });
                }
            }

            if (PackageType.PALLET.equals(aggregation.getPackageType())) {
                Set<String> allByParentCode = aggregationRepository.findAllPrintCodeByParentCode(aggregation.getCode());
                DropoutDTO child = new DropoutDTO();
                child.setDropoutReason(dropoutDTO.getDropoutReason());
                child.setAggregations(allByParentCode);
                dropoutAggregation(dropoutDTO, reportId, false);
            } else {
                DropoutDTO child = new DropoutDTO();
                child.setDropoutReason(dropoutDTO.getDropoutReason());
                List<ValidationCode> allPrintCodeByParentCode = validationCodeRepository.findAllByParentCode(aggregation.getCode());
                for (ValidationCode markOrBlock : allPrintCodeByParentCode) {
                    if (markOrBlock.isBlock()) {
                        child.getBlocks().add(markOrBlock.getPrintCode());
                    } else {
                        child.getMarks().add(markOrBlock.getPrintCode());
                    }
                }
                if (CollectionUtils.isNotEmpty(child.getMarks())) {
                    dropoutMark(dropoutDTO, reportId, false);
                }
                if (CollectionUtils.isNotEmpty(child.getBlocks())) {
                    dropoutBlock(dropoutDTO, reportId, false);
                }
            }
            errorCodes.remove(aggregation.getPrintCode());
            // send to warehouse
            WarehousePayload payload = new WarehousePayload();
            payload.setOperation(WarehouseOperation.Dropout);
            payload.setNote("Списано");
            payload.setUnit(aggregation.getPackageType());
            payload.setAlcoholCount(BigDecimal.valueOf(aggregation.getQuantity()));
            payload.setOperationDate(LocalDateTime.now());
            payload.setOrganizationId(aggregation.getOrganizationId());
            payload.setProductId(aggregation.getProductId());
            payload.setCode(aggregation.getCode());
            kafkaProducer.sendMessage(KafkaConstants.WAREHOUSE_TOPIC, payload);
        }
        return errorCodes;
    }

}
