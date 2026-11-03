package uz.uzkassa.silen.service;

import com.opencsv.CSVReader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.silen.domain.*;
import uz.uzkassa.silen.domain.mongo.Block;
import uz.uzkassa.silen.domain.mongo.Mark;
import uz.uzkassa.silen.dto.SelectItem;
import uz.uzkassa.silen.dto.filter.MongoFilter;
import uz.uzkassa.silen.dto.marking.PartyDTO;
import uz.uzkassa.silen.dto.marking.PartyMergeDTO;
import uz.uzkassa.silen.dto.marking.PartyUtilisationDTO;
import uz.uzkassa.silen.dto.mq.Payload;
import uz.uzkassa.silen.enumeration.*;
import uz.uzkassa.silen.exceptions.BadRequestException;
import uz.uzkassa.silen.integration.TuronClient;
import uz.uzkassa.silen.integration.turon.GetMarksResponse;
import uz.uzkassa.silen.integration.turon.UtilisationRequest;
import uz.uzkassa.silen.integration.turon.UtilisationResponse;
import uz.uzkassa.silen.repository.mongo.BlockRepository;
import uz.uzkassa.silen.repository.mongo.MarkRepository;
import uz.uzkassa.silen.repository.pg.OrderProductRepository;
import uz.uzkassa.silen.repository.pg.PartyRepository;
import uz.uzkassa.silen.utils.Utils;

import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service Implementation for managing {@link Mark}.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PartyService extends BaseService {
    private final OrderProductRepository orderProductRepository;
    private final PartyRepository partyRepository;
    private final MarkRepository markRepository;
    private final TuronClient turonClient;
    private final BlockRepository blockRepository;
    private final UtilisationService utilisationService;

    public Page<PartyDTO> findAll(MongoFilter filter) {
        return partyRepository.findAllByFilter(filter).map(Party::toDto);
    }

    public List<SelectItem> getItems(MongoFilter filter) {
        return partyRepository.findAllByFilter(filter)
            .map(Party::toSelectItem)
            .getContent();
    }

    public PartyDTO findOne(String partyId) {
        return partyRepository.findById(partyId)
            .map(Party::toDto)
            .orElseThrow(notFound("Партия не найден"));
    }

    @Transactional
    public void save(final PartyDTO partyDTO) {
        final String orderProductId = partyDTO.getOrderProductId();

        final OrderProduct orderProduct = orderProductRepository.findById(orderProductId)
            .orElseThrow(notFound());
        if (!BufferStatus.ACTIVE.equals(orderProduct.getBufferStatus())) {
            throw new BadRequestException(Optional.ofNullable(orderProduct.getBufferStatus()).map(BufferStatus::getNameRu).orElse("Буфер не готов"));
        } else if (partyRepository.existsByOrderProductIdAndJobStatus(orderProductId, AggregationStatus.IN_PROGRESS)) {
            throw new BadRequestException("Невозможно создать партию. В процессе есть партия");
        }

        final int quantity = partyDTO.getQuantity();
        if (quantity > orderProduct.getRemaining()) {
            throw new BadRequestException("Количество превышает лимит");
        }

        final Organization organization = orderProduct.getOrder().getOrganization();
        if (StringUtils.isAnyEmpty(organization.getOmsId(), organization.getTuronToken())) {
            throw badRequest("В данной организации отсутствует OMS_ID или TOKEN").get();
        }

        Party party = new Party();
        party.setOrganizationId(organization.getId());
        party.setOrderProductId(orderProductId);
        party.setQuantity(quantity);
        party.setDescription(partyDTO.getDescription());
        party.setJobStatus(AggregationStatus.IN_PROGRESS);
        party = partyRepository.saveAndFlush(party);

        final String partyId = party.getId();
        afterCommitExecutor.execute(() -> rabbitMqProducer.sendPartyMarks(new Payload(partyId)));
    }

    @Transactional
    public void partyMarks(final Payload payload) {
        final Party party = partyRepository.findById(payload.getId())
            .orElseThrow(notFound());
        final OrderProduct orderProduct = party.getOrderProduct();
        if (!BufferStatus.ACTIVE.equals(orderProduct.getBufferStatus())) {
            return;
        }
        final int quantity = party.getQuantity();

        final Order order = orderProduct.getOrder();
        final Organization organization = orderProduct.getOrder().getOrganization();
        final Product product = orderProduct.getProduct();

        final String extension = product.getType().getProductGroup().getExtension();
        final boolean isBlock = CisType.GROUP.equals(orderProduct.getCisType());
        final String barcode = Utils.fillProductBarcode(isBlock ? product.getParentBarcode() : product.getBarcode());
        final String lastBlockId = Optional.ofNullable(orderProduct.getLastBlockId()).orElse("0");
        try {
            final GetMarksResponse response = turonClient.getMarks(extension, order.getTuronId(), lastBlockId, barcode, quantity, organization.getTuronToken(), organization.getOmsId());
            log.info("Turon party codes: {}, blockId: {}", CollectionUtils.isNotEmpty(response.getCodes()) ? response.getCodes().size() : 0, response.getBlockId());
            saveMarks(response.getCodes(), party.getOrderProductId(), orderProduct.getProductId(), product.getShortName(), party.getId(), isBlock, organization.getId(), organization.getName(), barcode);

            orderProduct.setReceived(orderProduct.getReceived() + quantity);
            orderProduct.setRemaining(orderProduct.getRemaining() - quantity);
            orderProduct.setLastBlockId(response.getBlockId());
            if (orderProduct.getRemaining() == 0) {
                orderProduct.setBufferStatus(BufferStatus.CLOSED);
            }
            orderProductRepository.save(orderProduct);

            party.setBlockId(response.getBlockId());
            party.setJobStatus(AggregationStatus.DRAFT);
            partyRepository.save(party);
        } catch (Exception e) {
            party.setJobStatus(AggregationStatus.ERROR);
            party.setErrorMessage(e.getMessage());
            partyRepository.save(party);
        }
    }

    public void retryPartyMarks(final String id) {
        final Party party = partyRepository.findById(id)
            .orElseThrow(notFound());
        if (AggregationStatus.IN_PROGRESS.equals(party.getJobStatus())) {
            throw new BadRequestException("Партия в процессе");
        }
        final OrderProduct orderProduct = party.getOrderProduct();
        final int quantity = party.getQuantity();
        final Order order = orderProduct.getOrder();
        final Organization organization = orderProduct.getOrder().getOrganization();
        final Product product = orderProduct.getProduct();

        final String extension = product.getType().getProductGroup().getExtension();
        final boolean isBlock = CisType.GROUP.equals(orderProduct.getCisType());
        final String barcode = Utils.fillProductBarcode(isBlock ? product.getParentBarcode() : product.getBarcode());
        final String lastBlockId = Optional.ofNullable(orderProduct.getLastBlockId()).orElse(null);
        try {
            final GetMarksResponse response = turonClient.retry(extension, order.getTuronId(), barcode, lastBlockId, organization.getTuronToken(), organization.getOmsId());
            log.info("Turon party codes: {}, blockId: {}", CollectionUtils.isNotEmpty(response.getCodes()) ? response.getCodes().size() : 0, response.getBlockId());
            saveMarks(response.getCodes(), party.getOrderProductId(), orderProduct.getProductId(), product.getShortName(), party.getId(), isBlock, organization.getId(), organization.getName(), barcode);

            party.setBlockId(response.getBlockId());
            party.setJobStatus(AggregationStatus.DRAFT);
            partyRepository.save(party);

            orderProduct.setReceived(orderProduct.getReceived() + quantity);
            orderProduct.setRemaining(orderProduct.getRemaining() - quantity);
            orderProduct.setLastBlockId(response.getBlockId());
            if (orderProduct.getRemaining() == 0) {
                orderProduct.setBufferStatus(BufferStatus.CLOSED);
            }
            orderProductRepository.save(orderProduct);
        } catch (final Exception e) {
            party.setJobStatus(AggregationStatus.ERROR);
            party.setErrorMessage(e.getMessage());
            partyRepository.save(party);
        }
    }

    private void saveMarks(final Set<String> codes, final String orderProductId, final String productId,
                           final String productName, final String partyId, final boolean isBlock, final String organizationId, final String organizationName, final String barcode) {
        int created = 0;
        for (final String printCode : codes) {
            final String code = Utils.clearPrintCode(printCode);
            if (!markRepository.existsByCode(code)) {
                Mark blockOrMark = new Block();
                blockOrMark.setPrintCode(printCode);
                blockOrMark.setCode(code);
                blockOrMark.setOrderProductId(orderProductId);
                blockOrMark.setProductId(productId);
                blockOrMark.setProductName(productName);
                blockOrMark.setPartyId(partyId);
                blockOrMark.setOrganizationId(organizationId);
                blockOrMark.setOrganizationName(organizationName);
                blockOrMark.setStatus(AggregationStatus.DRAFT);
                blockOrMark.setBarcode(barcode);
                if (isBlock) {
                    blockOrMark.setUnit(PackageType.BLOCK);
                    blockOrMark.setQuantity(0);
                } else {
                    blockOrMark.setQuantity(1);
                }
                created++;
                markRepository.save(blockOrMark);
            }
        }
        log.info("Marks count: {} saved: {}", codes.size(), created);
    }

    @Transactional
    public PartyStatus apply(String partyId, PartyUtilisationDTO partyUtilisationDTO) {
        Party party = partyRepository.findById(partyId).orElseThrow(notFound("Заказ не найден"));
        if (party.getQuantity() > 30000) {
            throw badRequest("Reached out of limit 30000").get();
        }
        if (PartyStatus.APPLIED.equals(party.getStatus())) throw badRequest("Заказ уже нанесен").get();
        ProductType productType = party.getOrderProduct().getProduct().getType();

        if (EnumSet.of(ProductGroup.Beer, ProductGroup.Alcohol).contains(productType.getProductGroup())) {
            if (partyUtilisationDTO.getProductionDate() == null) {
                throw this.badRequest("productionDate обязателен для ТГ " + productType.getProductGroup().getNameRu()).get();
            }
        } else if (ProductGroup.Pharma.equals(productType.getProductGroup())) {
            if (partyUtilisationDTO.getProductionDate() == null) {
                throw this.badRequest("productionDate обязателен для ТГ " + productType.getProductGroup().getNameRu()).get();
            }
            if (partyUtilisationDTO.getExpirationDate() == null) {
                throw this.badRequest("expirationDate обязателен для ТГ " + productType.getProductGroup().getNameRu()).get();
            }
            if (partyUtilisationDTO.getSeriesNumber() == null) {
                throw this.badRequest("serialNumber обязателен для ТГ " + productType.getProductGroup().getNameRu()).get();
            }
        }
        Organization organization = getCurrentOrganizationAndValidateOmsIdAndTuronToken();

        List<Mark> allByPartyId = markRepository.findAllByPartyId(partyId);
        Set<String> sntins = allByPartyId.stream().map(Mark::getPrintCode).collect(Collectors.toSet());

        if (CollectionUtils.isEmpty(sntins)) {
            throw badRequest("Массив строк отсутствует").get();
        }
        UtilisationRequest utilisationRequest = new UtilisationRequest();
        utilisationRequest.setSntins(sntins);
        utilisationRequest.setUsageType(Optional.ofNullable(partyUtilisationDTO.getUsageType()).orElse(UsageType.USED_FOR_PRODUCTION));
        utilisationRequest.setProductionDate(partyUtilisationDTO.getProductionDate());
        utilisationRequest.setExpirationDate(partyUtilisationDTO.getExpirationDate());
        utilisationRequest.setSeriesNumber(partyUtilisationDTO.getSeriesNumber());
        utilisationRequest.setProductionLineId(partyId);

        UtilisationResponse utilisationResponse = utilisationService.traceUtilisationResponse(organization.getTuronToken(), productType.getProductGroup().getExtension(), organization.getOmsId(), utilisationRequest);
        log.debug("UtilisationDTO {}", utilisationResponse);
        if (utilisationResponse.getReportId() != null) {
            party.setUsageType(utilisationRequest.getUsageType());
            party.setReportId(utilisationResponse.getReportId());
            party.setReportTime(LocalDateTime.now());
            party.setStatus(PartyStatus.APPLIED);
            partyRepository.save(party);

            afterCommitExecutor.execute(() -> rabbitMqProducer.sendPartyMarksSetApplied(new Payload(partyId)));
            return party.getStatus();
        } else {
            throw badRequest("Што то пошло не так попробуйте ещё раз").get();
        }
    }

    public void sendPartyMarksSetApplied(Payload payload) {
        long startTime = System.currentTimeMillis();
        List<Block> allByPartyId = blockRepository.findAllByPartyId(payload.getId());
        List<Block> batchUpdate = allByPartyId.stream()
            .peek(markOrBlock -> {
                if (markOrBlock.isBlock()) {
                    markOrBlock.setStatus(AggregationStatus.DRAFT);
                }
//                    markOrBlock.setTuronStatus(MarkStatus.APPLIED);
            }).collect(Collectors.toList());

        if (!CollectionUtils.isEmpty(batchUpdate)) {
            blockRepository.saveAll(batchUpdate);
        }
        log.info("By party {} marks applied timing: {}", payload.getId(), System.currentTimeMillis() - startTime);
    }

    @Transactional
    public void uploadParty(String orderProductId, int quantity, MultipartFile multipartFile) {
        OrderProduct orderProduct = orderProductRepository.findById(orderProductId).orElseThrow(notFound());
        final Product product = orderProduct.getProduct();
        final boolean isBlock = CisType.GROUP.equals(orderProduct.getCisType());
        final String barcode = Utils.fillProductBarcode(isBlock ? product.getParentBarcode() : product.getBarcode());
        if (!BufferStatus.ACTIVE.equals(orderProduct.getBufferStatus())) {
            throw new BadRequestException(orderProduct.getBufferStatus() != null ? orderProduct.getBufferStatus().getNameRu() : "Буфер не готов");
        }
        if (quantity > orderProduct.getRemaining()) {
            throw new BadRequestException("Количество превышает лимит");
        }
        final Organization organization = getCurrentOrganization();
        if (StringUtils.isAnyEmpty(organization.getOmsId(), organization.getTuronToken())) {
            throw badRequest("В данной организации отсутствует OMS_ID или TOKEN").get();
        }
        Set<String> marks = readFromFile(multipartFile);
        Party party = new Party();
        party.setOrganizationId(organization.getId());
        party.setOrderProductId(orderProductId);
        party.setQuantity(quantity);
        party.setBlockId(orderProductId);
        partyRepository.save(party);

        saveMarks(marks, orderProductId, orderProduct.getProductId(), product.getShortName(), party.getId(), isBlock, organization.getId(), organization.getName(), barcode);

        orderProduct.setReceived(marks.size() + quantity);
        orderProduct.setRemaining(orderProduct.getRemaining() - quantity);
        orderProduct.setLastBlockId(orderProductId);
        if (orderProduct.getRemaining() == 0) {
            orderProduct.setBufferStatus(BufferStatus.CLOSED);
        }
        orderProductRepository.save(orderProduct);
    }

    private Set<String> readFromFile(MultipartFile file) {
        Set<String> codes = new LinkedHashSet<>();
        try {
            CSVReader csvReader = new CSVReader(new InputStreamReader(file.getInputStream()));
            String[] values;
            while ((values = csvReader.readNext()) != null) {
                codes.add(values[0]);
            }
        } catch (Exception e) {
            log.error("On time reading something went wrong", e);
            throw badRequest("Time out").get();
        }
        return codes;
    }

    public void merge(final PartyMergeDTO dto) {
        final List<Mark> fromParty = markRepository.findAllByPartyId(dto.getFromId());
        final List<Mark> toParty = markRepository.findAllByPartyId(dto.getToId());
        final List<Mark> toDelete = new ArrayList<>();
        final List<Mark> toUpdate = new ArrayList<>();
        final Set<String> existingCodes = toParty.stream().map(Mark::getCode).collect(Collectors.toSet());
        final String toPartyId = dto.getToId();
        for (Mark mark : fromParty) {
            if (existingCodes.contains(mark.getCode())) {
                toDelete.add(mark);
            } else {
                mark.setPartyId(toPartyId);
                toUpdate.add(mark);
            }
        }
        if (CollectionUtils.isNotEmpty(toUpdate)) {
            markRepository.saveAll(toUpdate);
        }
        if (CollectionUtils.isNotEmpty(toDelete)) {
            markRepository.deleteAll(toDelete);
        }
    }

    public Map<String, Long> checkParties(List<String> partyIds) {
        Map<String, Long> responseMap = new HashMap<>();
        for (String partyId : partyIds) {
            responseMap.put(partyId, markRepository.countMarksByPartyIdAndUsedIsFalse(partyId));
        }
        return responseMap;
    }
}
