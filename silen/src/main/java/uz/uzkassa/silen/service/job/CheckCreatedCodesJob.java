package uz.uzkassa.silen.service.job;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import uz.uzkassa.silen.domain.Aggregation;
import uz.uzkassa.silen.domain.Organization;
import uz.uzkassa.silen.domain.mongo.Block;
import uz.uzkassa.silen.dto.filter.AggregationFilter;
import uz.uzkassa.silen.dto.filter.MongoFilter;
import uz.uzkassa.silen.enumeration.AggregationStatus;
import uz.uzkassa.silen.enumeration.PackageType;
import uz.uzkassa.silen.integration.AsilBelgisiClient;
import uz.uzkassa.silen.integration.asilbelgi.dto.CisInfoResponse;
import uz.uzkassa.silen.repository.mongo.BlockRepository;
import uz.uzkassa.silen.repository.pg.AggregationRepository;
import uz.uzkassa.silen.repository.pg.OrganizationRepository;
import uz.uzkassa.silen.service.BaseService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CheckCreatedCodesJob extends BaseService {

    final static String cronRegex = "0 0 1 * * ?";
    OrganizationRepository organizationRepository;
    AggregationRepository aggregationRepository;
    BlockRepository blockRepository;
    final static int minusOfStartTime = 15;
    final static int minusDay = 1;
    final static AggregationStatus checkByStatus = AggregationStatus.CREATED;

    //@Transactional
    @Scheduled(cron = cronRegex)
    public void start() {
        final List<Organization> activeOrganizations = organizationRepository.findAllByDeletedIsFalseAndActiveIsTrue();
        for (Organization organization : activeOrganizations) {
            if (organization == null) {
                continue;
            }
            final String trueToken = getCurrentOrganizationAsilBelgiTokenOrNul(organization.getTin());
            if (trueToken == null) {
                log.error("ORGANIZATION NAME: {} AsilBelgisi token is null", organization.getName());
                continue;
            }
            final String organizationName = organization.getName();

            long start = System.currentTimeMillis();
            try {
                final LocalDateTime to = LocalDateTime.now().minusMinutes(minusOfStartTime);
                final LocalDateTime from = LocalDateTime.now().minusDays(minusDay);
                final AggregationFilter aggregationFilter = new AggregationFilter();
                aggregationFilter.setFrom(from);
                aggregationFilter.setTo(to);
                aggregationFilter.setAggregationStatus(checkByStatus);
                aggregationFilter.setOrganizationId(organization.getId());
                aggregationFilter.setSize(AsilBelgisiClient.cisInfoLimit);
                checkAggregation(organizationName, trueToken, aggregationFilter);

                MongoFilter mongoFilter = new MongoFilter();
                mongoFilter.setOrganizationId(organization.getId());
                mongoFilter.setUnit(PackageType.BLOCK);
                mongoFilter.setAggregationStatus(checkByStatus);
                mongoFilter.setFrom(from);
                ;
                mongoFilter.setTo(to);
                ;
                checkBlock(organizationName, trueToken, mongoFilter);
            } catch (Exception e) {
                log.error("Checking organization {} codes error {}", organization.getName(), e.getMessage(), e);
            }
            log.info("EXECUTED FOR ORG: {}, {}ms", organization.getName(), System.currentTimeMillis() - start);
        }
    }

    public void checkBlock(final String organizationName, final String trueToken, final MongoFilter mongoFilter) {
        boolean hasNext;
        int pageNumber = 0;
        do {
            mongoFilter.setPage(pageNumber++);
            Page<Block> allByFilter = blockRepository.findAllByFilter(mongoFilter);
            if (allByFilter.getContent().isEmpty()) return;
            log.info("ORG: {}\n,PAGE:{}\n,COUNT: {} block", organizationName, allByFilter.getPageable().getPageNumber(), allByFilter.getTotalElements());
            final Set<String> codes = allByFilter.stream().map(Block::getCode).collect(Collectors.toSet());

            final Map<String, CisInfoResponse> cisInfoMap = checkFromAsilBelgisi(codes, trueToken);

            String time = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            for (Block block : allByFilter) {
                String code = block.getCode();
                String errorMessage = checkResponse(code, cisInfoMap, time);
                if (errorMessage != null) {
                    block.setStatus(AggregationStatus.ERROR);
                    block.setErrorMessage(errorMessage);
                } else {
                    block.setErrorMessage(localize("checked.success", time, cisInfoMap.get(code).getCisInfo().getStatusName()));
                }
                blockRepository.save(block);
            }
            hasNext = allByFilter.hasNext();
        } while (hasNext);
    }

    public void checkAggregation(final String organizationName, final String trueToken, final AggregationFilter aggregationFilter) {
        boolean hasNext;
        int pageNumber = 0;
        do {
            aggregationFilter.setPage(pageNumber++);
            Page<Aggregation> allByFilter = aggregationRepository.findAllByFilter(aggregationFilter);
            if (allByFilter.getContent().isEmpty()) return;
            log.info("ORG: {}\n,PAGE:{}\n,COUNT: {}", organizationName, allByFilter.getPageable().getPageNumber(), allByFilter.getTotalElements());
            final Map<String, Aggregation> createdAggregations = allByFilter.stream().collect(Collectors.toMap(Aggregation::getCode, Function.identity()));
            final Set<String> codes = createdAggregations.keySet();
            final Map<String, CisInfoResponse> cisInfoMap = checkFromAsilBelgisi(codes, trueToken);

            String time = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            for (Aggregation aggregation : allByFilter) {
                final String code = aggregation.getCode();
                String errorMessage = checkResponse(code, cisInfoMap, time);
                if (errorMessage != null) {
                    aggregation.setStatus(AggregationStatus.ERROR);
                    aggregation.setErrorMessage(errorMessage);
                } else {
                    aggregation.setErrorMessage(localize("checked.success", LocalDateTime.now(), cisInfoMap.get(code).getCisInfo().getStatusName()));
                }
                aggregationRepository.save(aggregation);
            }
            hasNext = allByFilter.hasNext();
        } while (hasNext);
    }
}
