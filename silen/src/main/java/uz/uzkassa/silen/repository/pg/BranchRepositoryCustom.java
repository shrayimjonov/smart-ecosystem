package uz.uzkassa.silen.repository.pg;

import org.springframework.data.domain.Page;
import uz.uzkassa.silen.domain.Branch;
import uz.uzkassa.silen.dto.filter.BranchFilter;

public interface BranchRepositoryCustom {

    Page<Branch> findAllByFilter(BranchFilter filter);
}
