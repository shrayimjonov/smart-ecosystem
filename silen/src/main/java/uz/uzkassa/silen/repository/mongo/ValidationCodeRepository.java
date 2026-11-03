package uz.uzkassa.silen.repository.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import uz.uzkassa.silen.domain.mongo.ValidationCode;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface ValidationCodeRepository extends MongoRepository<ValidationCode, String> {

    boolean existsByCode(String code);

    Optional<ValidationCode> findFirstByCode(String code);

    List<ValidationCode> findAllByParentCode(String parentCode);

    List<ValidationCode> findAllByCodeIn(Collection<String> printCodes);

}
