package uz.uzkassa.silen.repository.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import uz.uzkassa.silen.domain.mongo.StoreInMark;

import java.util.List;
import java.util.Set;


@Repository
public interface StoreInMarkRepository extends MongoRepository<StoreInMark, String> {

    Set<StoreInMark> findAllByItemId(Long id);

    List<StoreInMark> findAllByItemIdAndCodeIn(Long itemId, Set<String> codes);
}
