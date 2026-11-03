package uz.uzkassa.silen.repository.pg;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.uzkassa.silen.domain.Branch;

public interface BranchRepository extends JpaRepository<Branch, String>, BranchRepositoryCustom {

}
