package uz.uzkassa.silen.repository.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import uz.uzkassa.silen.domain.mongo.Mark;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface MarkRepository extends MongoRepository<Mark, String>, MarkRepositoryCustom<Mark> {

    boolean existsByCode(String code);

    Optional<Mark> findFirstByCode(String code);

    List<Mark> findAllByParentCode(String parentCode);

    List<Mark> findAllByParentCodeOrParentCode(String parentCode18, String parentCode20);

    List<Mark> findAllByParentCodeIn(Collection<String> parentCodes);

    Long countMarksByPartyIdAndUsedIsFalse(String partyId);

    List<Mark> findAllByPartyIdAndUsedIsFalse(String partyId);

    List<Mark> findAllByCodeIn(Collection<String> printCodes);

    List<Mark> findAllByPartyId(String partyId);

    Optional<Mark> findFirstByPartyId(String partyId);

    Long countAllByPartyIdInAndUsedIsFalse(Collection<String> partyIds);

}
