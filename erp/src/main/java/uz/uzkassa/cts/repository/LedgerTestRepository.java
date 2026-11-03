package uz.uzkassa.cts.repository;

import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.LedgerTest;

import java.util.List;

@Repository
public interface LedgerTestRepository extends BaseRepository<LedgerTest, Long> {
    List<LedgerTest> findAllByTin(String tin);
}
