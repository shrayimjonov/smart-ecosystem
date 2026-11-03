package uz.uzkassa.smartpos.repository.redis;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpos.domain.redis.BranchCityHash;

@Repository
public interface BranchCityRepository extends CrudRepository<BranchCityHash, Long> {
}
