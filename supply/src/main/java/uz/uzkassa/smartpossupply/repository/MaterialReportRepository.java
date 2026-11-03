package uz.uzkassa.smartpossupply.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.MaterialReport;

import java.util.List;

@Repository
public interface MaterialReportRepository extends BaseRepository<MaterialReport, Long> {

    @Query("SELECT mr.id FROM MaterialReport mr WHERE mr.clickhouseSync = false")
    List<Long> findAllIdsToResync();
}
