package uz.uzkassa.cts.repository;

import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.CustomerBranchScanner;

import java.util.Optional;

@Repository
public interface CustomerBranchScannerRepository extends BaseRepository<CustomerBranchScanner, Long> {

    Optional<CustomerBranchScanner> findByCode(String code);
}
