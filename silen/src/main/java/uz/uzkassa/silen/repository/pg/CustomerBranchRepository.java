package uz.uzkassa.silen.repository.pg;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import uz.uzkassa.silen.domain.CustomerBranch;

import java.util.List;
import java.util.Optional;

public interface CustomerBranchRepository extends JpaRepository<CustomerBranch, Long> {

    Optional<CustomerBranch> findFirstByNum(String branchNumber);

    Page<CustomerBranch> findAllByCustomerId(Long customerId, Pageable pageable);

    List<CustomerBranch> findAllByCustomerId(Long customerId);
}
