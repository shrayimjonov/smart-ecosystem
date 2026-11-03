package uz.uzkassa.smartpossupply.repository;

import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.checklist.Checklist;
import uz.uzkassa.smartpossupply.dto.base.ResultList;
import uz.uzkassa.smartpossupply.filters.ChecklistFilter;

@Repository
public interface ChecklistRepositoryCustom {
    ResultList<Checklist> getResultList(ChecklistFilter filter);
}
