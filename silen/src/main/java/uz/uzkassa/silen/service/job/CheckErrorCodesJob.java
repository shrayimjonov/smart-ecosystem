package uz.uzkassa.silen.service.job;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import uz.uzkassa.silen.domain.Aggregation;
import uz.uzkassa.silen.domain.mongo.Block;
import uz.uzkassa.silen.dto.filter.AggregationFilter;
import uz.uzkassa.silen.dto.filter.MongoFilter;
import uz.uzkassa.silen.enumeration.AggregationStatus;
import uz.uzkassa.silen.enumeration.PackageType;
import uz.uzkassa.silen.enumeration.SortTypeEnum;
import uz.uzkassa.silen.integration.AsilBelgisiClient;
import uz.uzkassa.silen.repository.mongo.BlockRepository;
import uz.uzkassa.silen.repository.pg.AggregationRepository;
import uz.uzkassa.silen.service.AggregationService;
import uz.uzkassa.silen.service.BaseService;
import uz.uzkassa.silen.service.BlockService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CheckErrorCodesJob extends BaseService {

    final static String cronRegex = "0 0 0/2 * * ?";
    AggregationRepository aggregationRepository;
    BlockRepository blockRepository;
    final static AggregationStatus checkByStatus = AggregationStatus.ERROR;
    AggregationService aggregationService;
    final static int minusOfStartTime = 5;
    private BlockService blockService;


    @Scheduled(cron = cronRegex)
    public void checkErrorAggregationBatchByOrganization() {
        final LocalDateTime now = LocalDateTime.now();
        final LocalDateTime from = now.minusHours(3);

        AggregationFilter filter = new AggregationFilter();
        filter.setFrom(from);
        filter.setTo(now);
        filter.setAggregationStatus(checkByStatus);
        filter.setPage(AsilBelgisiClient.cisInfoLimit);
        filter.setOrderBy("lastModifiedDate");
        filter.setSortOrder(SortTypeEnum.desc);
        boolean hasNext;
        int pageNumber = 0;
        do {
            filter.setPage(pageNumber++);
            Page<Aggregation> page = aggregationRepository.findAllByFilter(filter);
            final Map<String, List<Aggregation>> map = page.getContent().stream().collect(Collectors.groupingBy(Aggregation::getOrganizationId));

            for (String organizationId : map.keySet()) {
                aggregationService.checkAggregations(organizationId, map.get(organizationId));
            }
            hasNext = page.hasNext();

        } while (hasNext);
    }

    @Scheduled(cron = cronRegex)
    public void checkErrorBlockBatchByOrganization() {
        final LocalDateTime now = LocalDateTime.now();
        final LocalDateTime from = now.minusHours(3);

        MongoFilter filter = new MongoFilter();
        filter.setFrom(from);
        filter.setTo(now);
        filter.setAggregationStatus(checkByStatus);
        filter.setPage(AsilBelgisiClient.cisInfoLimit);
        filter.setOrderBy("lastModifiedDate");
        filter.setSortOrder(SortTypeEnum.desc);
        filter.setUnit(PackageType.BLOCK);
        boolean hasNext;
        int pageNumber = 0;
        do {
            filter.setPage(pageNumber++);
            final Page<Block> page = blockRepository.findAllByFilter(filter);
            final Map<String, List<Block>> map = page.getContent().stream().collect(Collectors.groupingBy(Block::getOrganizationId));

            for (String organizationId : map.keySet()) {
                blockService.checkBlocks(organizationId, map.get(organizationId));
            }
            hasNext = page.hasNext();
        } while (hasNext);
    }
}
