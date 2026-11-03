package uz.uzkassa.smartpossupply.repository;

import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.checklist.Condition;

import java.util.List;

@Repository
public interface ConditionRepository extends BaseRepository<Condition, Long> {

    List<Condition> findAllByCompanyId(Long companyId);
}
