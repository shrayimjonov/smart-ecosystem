package uz.uzkassa.cts.repository.custom;

import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.Template;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.filter.TemplateFilter;

@Repository
public interface TemplateRepositoryCustom {

    ResultList<Template> getResultList(TemplateFilter filter);
}
