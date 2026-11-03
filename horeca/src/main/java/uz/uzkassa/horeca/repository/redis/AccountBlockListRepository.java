package uz.uzkassa.horeca.repository.redis;

import org.springframework.data.repository.CrudRepository;
import uz.uzkassa.horeca.domain.redis.AccountBlockList;

public interface AccountBlockListRepository extends CrudRepository<AccountBlockList, Long> {
}
