package uz.uzkassa.smartpos.repository.postgres.company;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import uz.uzkassa.smartpos.domain.postgres.company.CompanyTuronInfo;

import java.util.List;

public interface CompanyTuronInfoRepository extends JpaRepository<CompanyTuronInfo, Long> {
    @Query(value = "select i from CompanyTuronInfo i where i.expirationDate <= current_timestamp ")
    List<CompanyTuronInfo> findAllExpiredTokens();
}