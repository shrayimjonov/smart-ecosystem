package uz.uzkassa.cts.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.FreePeriodHistory;

@Repository
public interface FreePeriodHistoryRepository extends BaseRepository<FreePeriodHistory, Long> {

    @EntityGraph(attributePaths = {"customer", "customerBranchDevice", "customerBranchDevice.device", "creator", "lastModifier"})
    Page<FreePeriodHistory> findAll(Specification<FreePeriodHistory> specification, Pageable pageable);

}
