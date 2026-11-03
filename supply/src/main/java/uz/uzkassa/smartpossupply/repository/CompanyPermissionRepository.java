package uz.uzkassa.smartpossupply.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.company.CompanyPermission;

import java.time.LocalDateTime;
import java.util.List;


@Repository
public interface CompanyPermissionRepository extends BaseRepository<CompanyPermission, Long> {

    @Query("""
            select cp
            from CompanyPermission cp
            where cp.deleted = false
            and cp.endDate between :fromDate and :toDate
            """)
    List<CompanyPermission> findByPeriod(@Param("fromDate") LocalDateTime fromDate, @Param("toDate") LocalDateTime toDate);

}
