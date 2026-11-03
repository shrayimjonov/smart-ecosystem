package uz.uzkassa.horeca.repository;

import org.springframework.data.domain.Page;
import uz.uzkassa.horeca.domain.branch.Branch;
import uz.uzkassa.horeca.dto.base.CommonDTO;
import uz.uzkassa.horeca.dto.base.ResultList;
import uz.uzkassa.horeca.filters.BaseFilter;

import java.util.List;

public interface BranchRepositoryCustom {
    Page<Branch> getResultList(BaseFilter filter);

    List<CommonDTO> getLookup(BaseFilter filter);
}
