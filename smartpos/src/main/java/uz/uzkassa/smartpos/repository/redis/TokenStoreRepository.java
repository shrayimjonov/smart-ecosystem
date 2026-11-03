package uz.uzkassa.smartpos.repository.redis;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpos.constants.TokenType;
import uz.uzkassa.smartpos.domain.redis.TokenStoreHash;

@Repository
public interface TokenStoreRepository extends CrudRepository<TokenStoreHash, TokenType> {
}
