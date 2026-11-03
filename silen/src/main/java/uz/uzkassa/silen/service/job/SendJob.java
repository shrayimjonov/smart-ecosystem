package uz.uzkassa.silen.service.job;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import uz.uzkassa.silen.domain.Aggregation;
import uz.uzkassa.silen.domain.Organization;
import uz.uzkassa.silen.domain.mongo.Block;
import uz.uzkassa.silen.domain.mongo.Mark;
import uz.uzkassa.silen.dto.filter.AggregationFilter;
import uz.uzkassa.silen.dto.mq.Payload;
import uz.uzkassa.silen.enumeration.AggregationStatus;
import uz.uzkassa.silen.enumeration.AggregationType;
import uz.uzkassa.silen.enumeration.PackageType;
import uz.uzkassa.silen.enumeration.ProductGroup;
import uz.uzkassa.silen.integration.AsilBelgisiClient;
import uz.uzkassa.silen.integration.TuronClient;
import uz.uzkassa.silen.integration.turon.AggregationItem;
import uz.uzkassa.silen.integration.turon.CreateAggregationRequest;
import uz.uzkassa.silen.integration.turon.CreateAggregationResponse;
import uz.uzkassa.silen.repository.mongo.BlockRepository;
import uz.uzkassa.silen.repository.mongo.MarkRepository;
import uz.uzkassa.silen.repository.pg.AggregationRepository;
import uz.uzkassa.silen.service.BaseService;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SendJob extends BaseService {

    AggregationRepository aggregationRepository;
    MarkRepository markRepository;
    BlockRepository blockRepository;
    final static String cronRegex = "0 0/10 * * * ?";
    final static AggregationStatus checkByStatus = AggregationStatus.APPLIED;
    final static int minusOfStartTime = 5;

    @Scheduled(cron = cronRegex)
    public void runForAggregation() {
        final LocalDateTime to = LocalDateTime.now().minusMinutes(minusOfStartTime);

        boolean hasNext;
        int pageNumber = 0;
        do {
            final Page<Aggregation> page = aggregationRepository.findAllByStatusAndLastModifiedDateIsBeforeOrderByOrganizationId(checkByStatus, to, PageRequest.of(pageNumber++, AsilBelgisiClient.cisInfoLimit));
            final Map<String, List<Aggregation>> map = page.getContent().stream().collect(Collectors.groupingBy(Aggregation::getOrganizationId));
            for (String organizationId : map.keySet()) {
                final Organization organization = organizationRepository.findById(organizationId).orElse(null);
                if (organization == null) {
                    continue;
                }
                if (organization.getTuronToken() == null) {
                    log.error("ORGANIZATION {} HAS NO TRUE TOKEN", organization.getName());
                    continue;
                }
                sendAggregations(map.get(organizationId), List.of(), organization);
            }
            hasNext = page.hasNext();
        } while (hasNext);
    }

    @Scheduled(cron = cronRegex)
    public void runForBlock() {
        final LocalDateTime to = LocalDateTime.now().minusMinutes(minusOfStartTime);

        boolean hasNext;
        int pageNumber = 0;
        do {
            final Page<Block> page = blockRepository.findAllByStatusAndUnitAndLastModifiedDateIsBeforeOrderByOrganizationId(checkByStatus, PackageType.BLOCK, to, PageRequest.of(pageNumber++, AsilBelgisiClient.cisInfoLimit));
            final Map<String, List<Block>> map = page.getContent().stream().collect(Collectors.groupingBy(Block::getOrganizationId));

            for (String organizationId : map.keySet()) {
                final Organization organization = organizationRepository.findById(organizationId).orElse(null);
                if (organization == null) {
                    continue;
                }
                if (organization.getTuronToken() == null) {
                    log.info("ORGANIZATION {} HAS NO TRUE TOKEN", organization.getName());
                    continue;
                }
                sendAggregations(List.of(), map.get(organizationId), organization);
            }
            hasNext = page.hasNext();
        } while (hasNext);
    }

    private void sendAggregations(final Collection<Aggregation> allByFilter, final Collection<Block> allBlockByFilter, final Organization organization) {
        int limitRemaining = 0;
        final LinkedList<AggregationItem> aggregationItems = new LinkedList<>();
        final LinkedList<Aggregation> aggregations = new LinkedList<>();
        final LinkedList<Block> blocks = new LinkedList<>();

        for (final Aggregation aggregation : allByFilter) {
            final String code = aggregation.getCode();

            // find children
            final List<Mark> marks = markRepository.findAllByParentCode(code);
            Set<String> snTins = marks.stream().map(Mark::getCode).collect(Collectors.toSet());
            if (PackageType.PALLET.equals(aggregation.getPackageType())) {
                snTins.addAll(aggregationRepository.findAllCodeByParentCode(code));
            }

            // check children is not empty & quantity
            if (CollectionUtils.isEmpty(snTins)) {
                aggregation.setErrorMessage("Коды маркировки отсутствуют");
                aggregation.setStatus(AggregationStatus.ERROR);
                aggregationRepository.save(aggregation);
                continue;
            } else if (snTins.size() != aggregation.getQuantity()) {
                aggregation.setErrorMessage("Количество не соответствует");
                aggregation.setStatus(AggregationStatus.ERROR);
                aggregationRepository.save(aggregation);
                continue;
            }

            // convert aggregation to AggregationItem for sending Turon
            final AggregationItem aggregationItem = new AggregationItem();
            aggregationItem.setAggregatedItemsCount(aggregation.getQuantity());
            aggregationItem.setAggregationUnitCapacity(aggregation.getQuantity());
            aggregationItem.setAggregationType(AggregationType.AGGREGATION);
            aggregationItem.setUnitSerialNumber(code);
            aggregationItem.setSntins(snTins);
            int incrementValue = aggregation.getQuantity() + 1;

            //check Turon per request codes limit & limit reached send AggregationItems to Turon
            if (limitRemaining + incrementValue > TuronClient.aggregationLimit) {
                final CreateAggregationResponse createAggregationResponse = sendAggregationItems(organization, aggregationItems);
                setReportInfoAndSave(aggregations, blocks, createAggregationResponse);
                aggregationItems.clear();
                aggregations.clear();
                blocks.clear();
                limitRemaining = 0;
            }

            // add converted AggregationItem & Aggregation to list
            aggregationItems.add(aggregationItem);
            aggregations.add(aggregation);
            limitRemaining += incrementValue;
        }
        for (final Block block : allBlockByFilter) {
            final String code = block.getCode();

            // find children
            final List<Mark> marks = markRepository.findAllByParentCode(code);
            Set<String> snTins = marks.stream().map(Mark::getCode).collect(Collectors.toSet());

            // check children is not empty & quantity
            if (CollectionUtils.isEmpty(snTins)) {
                block.setErrorMessage("Коды маркировки отсутствуют");
                block.setStatus(AggregationStatus.ERROR);
                blockRepository.save(block);
                continue;
            } else if (snTins.size() != block.getQuantity()) {
                block.setErrorMessage("Количество не соответствует");
                block.setStatus(AggregationStatus.ERROR);
                blockRepository.save(block);
                continue;
            }

            // convert aggregation to AggregationItem for sending Turon
            final AggregationItem aggregationItem = new AggregationItem();
            aggregationItem.setAggregatedItemsCount(block.getQuantity());
            aggregationItem.setAggregationUnitCapacity(block.getQuantity());
            aggregationItem.setAggregationType(AggregationType.AGGREGATION);
            aggregationItem.setUnitSerialNumber(code);
            aggregationItem.setSntins(snTins);
            int incrementValue = block.getQuantity() + 1;

            //check Turon per request codes limit & limit reached send AggregationItems to Turon
            if (limitRemaining + incrementValue > TuronClient.aggregationLimit) {
                final CreateAggregationResponse createAggregationResponse = sendAggregationItems(organization, aggregationItems);
                setReportInfoAndSave(aggregations, blocks, createAggregationResponse);
                aggregationItems.clear();
                blocks.clear();
                aggregations.clear();
                limitRemaining = 0;
            }

            // add converted AggregationItem & Block to list
            aggregationItems.add(aggregationItem);
            blocks.add(block);
            limitRemaining += incrementValue;
        }

        // check is list aggregationItems is not empty send that
        if (CollectionUtils.isNotEmpty(aggregationItems)) {
            CreateAggregationResponse createAggregationResponse = sendAggregationItems(organization, aggregationItems);
            setReportInfoAndSave(aggregations, blocks, createAggregationResponse);
            aggregationItems.clear();
            aggregations.clear();
            blocks.clear();
        }
    }

    private void setReportInfoAndSave(Collection<Aggregation> aggregations, Collection<Block> blocks, final CreateAggregationResponse createAggregationResponse) {
        final String errorMessage = createAggregationResponse != null ? createAggregationResponse.getErrorMessage() : "Error occurred while sending aggregation";
        final String reportId = createAggregationResponse != null ? createAggregationResponse.getReportId() : null;

        if (CollectionUtils.isNotEmpty(aggregations)) {
            for (Aggregation aggregation : aggregations) {
                aggregation.setErrorMessage(errorMessage);
                aggregation.setReportId(reportId);
                aggregation.setStatus(AggregationStatus.SENT);
                if (errorMessage != null) {
                    aggregation.setStatus(AggregationStatus.ERROR);
                }
                aggregationRepository.save(aggregation);
            }
        }
        if (CollectionUtils.isNotEmpty(blocks)) {
            for (Block block : blocks) {
                block.setErrorMessage(errorMessage);
                block.setReportId(reportId);
                block.setStatus(AggregationStatus.SENT);
                if (errorMessage != null) {
                    block.setStatus(AggregationStatus.ERROR);
                }
                blockRepository.save(block);
            }
        }
    }

    private CreateAggregationResponse sendAggregationItems(final Organization organization, final List<AggregationItem> aggregationItems) {
        log.info(localize("sending.aggregations", organization.getName(), aggregationItems.size()));
        final CreateAggregationRequest request = new CreateAggregationRequest(aggregationItems, organization.getTin());
        if (ProductGroup.Tobacco.equals(organization.getProductGroup())) {
            request.setProductionLineId(ProductGroup.Tobacco.getNameRu());
        }
        try {
            return sendAggregationToTuronTrace(request, organization.getProductGroup().getExtension(), organization.getTuronToken(), organization.getOmsId());
        } catch (Exception e) {
            log.error(e.getMessage());
            return new CreateAggregationResponse(null, e.getMessage());
        }
    }

    public void checkFromRedis(AggregationFilter filter) {

        int pageNumber = 0;
        boolean isNotLast;
        do {
            filter.setPage(pageNumber);
            // first page off marks
            Page<Aggregation> page = aggregationRepository.findAllByFilter(filter);
            aggregatePage(page);

            pageNumber++;
            isNotLast = page.hasNext();
        } while (isNotLast);

    }

    private void aggregatePage(Page<Aggregation> page) {
        for (Aggregation redisAggregation : page) {
            if (redisAggregation == null) {
                continue;
            }
            log.info("Republish {}", redisAggregation.getCode());
            rabbitMqProducer.sendForSaveAggregationMarks(new Payload(redisAggregation.getCode()));
        }
    }
}
