package uz.uzkassa.silen.repository.pg;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.uzkassa.silen.domain.DashboardStats;


@Repository
public interface DashboardStatsRepository extends JpaRepository<DashboardStats, String>, DashboardStatsRepositoryCustom {

}
