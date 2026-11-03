package uz.uzkassa.smartpossupply.service;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import uz.uzkassa.smartpossupply.domain.aggregation.AggregationBlock;
import uz.uzkassa.smartpossupply.domain.company.CompanyAggregationInfo;
import uz.uzkassa.smartpossupply.domain.mark.Mark;
import uz.uzkassa.smartpossupply.domain.mark.MarkOrderParty;
import uz.uzkassa.smartpossupply.domain.mark.MarkOrderProduct;
import uz.uzkassa.smartpossupply.domain.product.Product;
import uz.uzkassa.smartpossupply.dto.base.ResultList;
import uz.uzkassa.smartpossupply.dto.integration.turon.mark.TuronGetMarksResponseDTO;
import uz.uzkassa.smartpossupply.dto.integration.turon.mark.TuronUtilisationReportRequestDTO;
import uz.uzkassa.smartpossupply.dto.integration.turon.mark.TuronUtilisationReportResponseDTO;
import uz.uzkassa.smartpossupply.dto.markorder.party.MarkOrderPartyDTO;
import uz.uzkassa.smartpossupply.dto.markorder.party.MarkOrderPartyListDTO;
import uz.uzkassa.smartpossupply.dto.payload.PartyUtilisationPayload;
import uz.uzkassa.smartpossupply.dto.payload.Payload;
import uz.uzkassa.smartpossupply.enums.AggregationStatus;
import uz.uzkassa.smartpossupply.enums.BufferStatus;
import uz.uzkassa.smartpossupply.enums.CodeType;
import uz.uzkassa.smartpossupply.enums.MarkStatus;
import uz.uzkassa.smartpossupply.enums.ProductGroup;
import uz.uzkassa.smartpossupply.enums.ProductType;
import uz.uzkassa.smartpossupply.enums.TuronMarkStatus;
import uz.uzkassa.smartpossupply.filters.MarkOrderPartyFilter;
import uz.uzkassa.smartpossupply.integration.TuronClient;
import uz.uzkassa.smartpossupply.rabbitmq.producer.RabbitMqPartyUtilisationProducer;
import uz.uzkassa.smartpossupply.rabbitmq.producer.RabbitMqStockInByPartyProducer;
import uz.uzkassa.smartpossupply.repository.AggregationBlockRepository;
import uz.uzkassa.smartpossupply.repository.MarkOrderPartyRepository;
import uz.uzkassa.smartpossupply.repository.MarkOrderProductRepository;
import uz.uzkassa.smartpossupply.repository.MarkRepository;
import uz.uzkassa.smartpossupply.utils.MarkUtils;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static lombok.AccessLevel.PRIVATE;

@Slf4j
@Service
@Transactional(readOnly = true)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class MarkOrderPartyService extends BaseService {

    TuronClient turonClient;
    MarkOrderProductRepository markOrderProductRepository;
    MarkOrderPartyRepository markOrderPartyRepository;
    AggregationBlockRepository aggregationBlockRepository;
    MarkRepository markRepository;
    RabbitMqPartyUtilisationProducer rabbitMqPartyUtilisationProducer;
    RabbitMqStockInByPartyProducer rabbitMqStockInByPartyProducer;

    @Transactional
    public void create(MarkOrderPartyDTO markOrderPartyDTO) {
        MarkOrderProduct markOrderProduct = markOrderProductRepository
            .findById(markOrderPartyDTO.getMarkOrderProductId())
            .orElseThrow(validationService.entityNotFoundExceptionThrow(MarkOrderProduct.class.getSimpleName(), "id", markOrderPartyDTO.getMarkOrderProductId()));
        if (!BufferStatus.ACTIVE.equals(markOrderProduct.getBufferStatus())) {
            throw validationService.badRequestExceptionThrow(
                markOrderProduct.getBufferStatus() != null ? markOrderProduct.getBufferStatus().getName() : "Буфер не готов",
                "Буфер не готов"
            ).get();
        }
        if (markOrderPartyDTO.getQty() > markOrderProduct.getRemaining()) {
            throw validationService.badRequestExceptionThrow("Количество превышает лимит", "Количество превышает лимит").get();
        }

        CompanyAggregationInfo companyAggregationInfo = validateTuronAndGetAggregationInfo();
        TuronGetMarksResponseDTO marksResponseDTO = turonClient.getMarks(
            markOrderProduct.getProduct().getProductType().getProductGroup().getExtension(),
            markOrderProduct.getMarkOrder().getTuronId(),
            (markOrderProduct.getLastBlockId() != null) ? markOrderProduct.getLastBlockId() : "0",
            MarkUtils.generateGTIN(markOrderProduct.getProduct().getBarcode()),
            markOrderPartyDTO.getQty(),
            companyAggregationInfo.getTuronToken(),
            companyAggregationInfo.getOmsId()
        );

        MarkOrderParty markOrderParty = new MarkOrderParty();
        markOrderParty.setMarkOrderProductId(markOrderProduct.getId());
        markOrderParty.setQty(markOrderPartyDTO.getQty());
        markOrderParty.setBlockId(marksResponseDTO.getBlockId());
        markOrderPartyRepository.save(markOrderParty);

        saveMarks(marksResponseDTO.getCodes(), markOrderProduct.getProduct(), markOrderParty.getId());

        markOrderProduct.setReceived(markOrderProduct.getReceived() + markOrderPartyDTO.getQty());
        markOrderProduct.setRemaining(markOrderProduct.getRemaining() - markOrderPartyDTO.getQty());
        markOrderProduct.setLastBlockId(marksResponseDTO.getBlockId());
        if (markOrderProduct.getRemaining() == 0) {
            markOrderProduct.setBufferStatus(BufferStatus.CLOSED);
        }
        markOrderProductRepository.save(markOrderProduct);
        // automatic stock in operation only when Company.ProductGroup is Tobacco or Appliances
        if (EnumSet.of(ProductGroup.Tobacco, ProductGroup.Appliances).contains(getCurrentCompany().getProductGroup())) {
            rabbitMqStockInByPartyProducer.sendParty(new Payload(markOrderParty.getId()));
        }
    }

    private void saveMarks(Set<String> codes, Product product, Long markOrderPartyId) {
        if (ProductType.isContainGroupMarkOrder(product.getProductType())) {
            List<AggregationBlock> aggregationBlocks = codes
                .stream()
                .map(printCode -> {
                    String code = MarkUtils.clearOriginalCode(printCode, product.getProductType());
                    AggregationBlock aggregation = aggregationBlockRepository.findFirstByCode(code).orElseGet(AggregationBlock::new);
                    if (aggregation.getId() == null) {
                        aggregation.setPrintCode(printCode);
                        aggregation.setCode(code);
                        aggregation.setProductId(product.getId());
                        aggregation.setMarkOrderPartyId(markOrderPartyId);
                        aggregation.setCreatedDate(LocalDateTime.now());
                        aggregation.setCompanyId(getCurrentCompanyId());
                        aggregation.setStatus(AggregationStatus.DRAFT);
                    }
                    return aggregation;
                })
                .toList();
            aggregationBlockRepository.saveAll(aggregationBlocks);
        } else {
            List<Mark> marks = codes
                .stream()
                .map(printCode -> {
                    String code = MarkUtils.clearOriginalCode(printCode, product.getProductType());
                    Mark mark = markRepository.findFirstByCode(code).orElseGet(Mark::new);
                    if (mark.getId() == null) {
                        mark.setOriginalCode(printCode);
                        mark.setCode(code);
                        mark.setProductId(product.getId());
                        mark.setMarkOrderPartyId(markOrderPartyId);
                        mark.setCompanyId(getCurrentCompanyId());
                    }
                    return mark;
                })
                .toList();
            markRepository.saveAll(marks);
        }
    }

    public Page<MarkOrderPartyListDTO> getList(MarkOrderPartyFilter filter) {
        ResultList<MarkOrderParty> resultList = markOrderPartyRepository.getResultList(filter);
        List<MarkOrderPartyListDTO> result = resultList
            .getList()
            .stream()
            .map(markOrderParty -> {
                MarkOrderPartyListDTO markOrderPartyListDTO = new MarkOrderPartyListDTO();
                markOrderPartyListDTO.setId(markOrderParty.getId());
                markOrderPartyListDTO.setMarkOrderProduct(markOrderParty.getMarkOrderProduct().toCommonDTO());
                markOrderPartyListDTO.setQty(markOrderParty.getQty());
                markOrderPartyListDTO.setCreatedDate(markOrderParty.getCreatedDate());
                markOrderPartyListDTO.setReportId(markOrderParty.getReportId());
                markOrderPartyListDTO.setReportTime(markOrderParty.getReportTime());
                if (markOrderParty.getStatus() != null) {
                    markOrderPartyListDTO.setStatus(MarkStatus.valueOf(markOrderParty.getStatus().name()));
                }
                return markOrderPartyListDTO;
            })
            .toList();
        return new PageImpl<>(result, filter.getSortedPageable(), resultList.getCount());
    }

    @Transactional
    public void retry(Long id) {
        MarkOrderParty markOrderParty = markOrderPartyRepository
            .findById(id)
            .orElseThrow(validationService.entityNotFoundExceptionThrow(MarkOrderParty.class.getSimpleName(), "id", id));
        MarkOrderProduct orderProduct = markOrderParty.getMarkOrderProduct();

        String orderId = orderProduct.getMarkOrder() != null ? orderProduct.getMarkOrder().getTuronId() : null;
        String extension = Optional.ofNullable(orderProduct.getProduct().getBarcode()).orElse("alcohol");

        CompanyAggregationInfo companyAggregationInfo = validateTuronAndGetAggregationInfo();
        TuronGetMarksResponseDTO marksResponseDTO = turonClient.retry(
            extension,
            orderId,
            orderProduct.getProduct().getBarcode(),
            markOrderParty.getBlockId(),
            companyAggregationInfo.getTuronToken(),
            companyAggregationInfo.getOmsId()
        );
        saveMarks(marksResponseDTO.getCodes(), orderProduct.getProduct(), id);
    }

    @Transactional
    public Long apply(Long partyId) {
        MarkOrderParty markOrderParty = markOrderPartyRepository.findById(partyId)
            .orElseThrow(validationService.entityNotFoundExceptionThrow("Заказ не найден", "id", partyId));
        if (markOrderParty.getQty() > 30000) {
            throw validationService.badRequestExceptionThrow("Reached out of limit 30000", "Reached out of limit 30000").get();
        }
        if (TuronMarkStatus.APPLIED.equals(markOrderParty.getStatus()))
            throw validationService.badRequestExceptionThrow("Заказ уже нанесен", "Заказ уже нанесен: " + partyId).get();
        ProductType productType = markOrderParty.getMarkOrderProduct().getProduct().getProductType();

        if (!Arrays.asList(ProductGroup.Beer, ProductGroup.Tobacco).contains(productType.getProductGroup())) {
            throw validationService.badRequestExceptionThrow("Заказ уже нанесен", "Заказ уже нанесен: " + partyId).get();
        }

        TuronUtilisationReportRequestDTO turonUtilisationReportRequestDTO = new TuronUtilisationReportRequestDTO();
        CompanyAggregationInfo companyAggregationInfo = getCompanyAggregationInfo();

        PartyUtilisationPayload partyUtilisationPayload = new PartyUtilisationPayload();
        partyUtilisationPayload.setClientToken(companyAggregationInfo.getAslBelgiToken());
        partyUtilisationPayload.setId(partyId);
        partyUtilisationPayload.setProductType(productType);
        partyUtilisationPayload.setProductionDate(LocalDateTime.now());

        if (ProductGroup.Tobacco.equals(productType.getProductGroup())) {
            turonUtilisationReportRequestDTO.setProductionLineId(companyAggregationInfo.getFactoryId());
        }
        if (ProductType.isContainGroupMarkOrder(productType)) {
            partyUtilisationPayload.setCodeType(CodeType.BLOCK);
            turonUtilisationReportRequestDTO.setSntins(aggregationBlockRepository.findAllPrintCodesByMarkOrderPartyId(partyId));
        } else {
            partyUtilisationPayload.setCodeType(CodeType.MARK);
            turonUtilisationReportRequestDTO.setSntins(markRepository.findAllPrintCodesByMarkOrderPartyId(partyId));
        }
        if (CollectionUtils.isEmpty(turonUtilisationReportRequestDTO.getSntins())) {
            throw validationService.badRequestExceptionThrow("Внутри партии коды не найден", "Code not found").get();
        }
        TuronUtilisationReportResponseDTO utilisationReportDTO = turonClient.apply(companyAggregationInfo.getTuronToken(), productType.getProductGroup().getExtension(), companyAggregationInfo.getOmsId(), turonUtilisationReportRequestDTO);
        if (utilisationReportDTO.getReportId() != null) {
            markOrderParty.setReportId(utilisationReportDTO.getReportId());
            markOrderParty.setReportTime(LocalDateTime.now());
            markOrderParty.setStatus(TuronMarkStatus.APPLIED);
            markOrderPartyRepository.save(markOrderParty);
            rabbitMqPartyUtilisationProducer.sendParty(partyUtilisationPayload);
            return partyId;
        } else {
            throw validationService.badRequestExceptionThrow("Што то пошло не так попробуйте ещё раз", "Што то пошло не так попробуйте ещё раз").get();
        }
    }
}
