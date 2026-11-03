package uz.uzkassa.silen.repository.mongo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import uz.uzkassa.silen.domain.mongo.Block;
import uz.uzkassa.silen.enumeration.AggregationStatus;
import uz.uzkassa.silen.enumeration.PackageType;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface BlockRepository extends MongoRepository<Block, String>, MarkRepositoryCustom<Block> {

    Optional<Block> findFirstByCode(String code);

    List<Block> findAllByParentCode(String parentCode);

    List<Block> findAllByCodeIn(Collection<String> printCodes);

    List<Block> findAllByPartyId(String partyId);

    Optional<Block> findFirst1ByPartyIdAndUsedFalse(String orderProductId);

    List<Block> findAllByStatusAndUnit(AggregationStatus aggregationStatus, PackageType packageType);

    Optional<Block> findFirstByPartyAggregationIdAndUnit(String partyAggregationId, PackageType packageType);

    Collection<Block> findAllByPartyAggregationIdAndUnitOrderByCreatedDateAsc(String id, PackageType unit);

    Page<Block> findAllByStatusAndUnitAndLastModifiedDateIsBeforeOrderByOrganizationId(AggregationStatus aggregationStatus, PackageType unit, LocalDateTime lastModifiedDate, Pageable pageable);

    Page<Block> findAllByUnitAndPartyIdInAndUsedFalse(PackageType unit, Collection<String> partyIds, Pageable pageable);

    List<Block> findAllByPartyAggregationIdOrderByCreatedDateAsc(String id);

    List<Block> findAllByParentCodeIn(Collection<String> palletCodes);
}
