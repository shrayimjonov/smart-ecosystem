package uz.uzkassa.smartpos.repository.postgres.common;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpos.domain.postgres.company.ActivityType;

import java.util.List;
import java.util.Optional;


/**
 * Spring Data  repository for the ActivityType entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ActivityTypeRepository extends JpaRepository<ActivityType, Long> {
    List<ActivityType> findAllByParentId(Long parentId);

    @Query("select at from ActivityType at where at.code=:code and at.parent.code=:parentCode")
    Optional<ActivityType> findOneByCodeAndParent(@Param("code") Long code, @Param("parentCode") Long parentCode);
}
