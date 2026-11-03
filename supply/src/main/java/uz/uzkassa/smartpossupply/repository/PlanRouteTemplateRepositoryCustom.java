package uz.uzkassa.smartpossupply.repository;

import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.planroutetemplate.PlanRouteTemplate;
import uz.uzkassa.smartpossupply.dto.base.ResultList;
import uz.uzkassa.smartpossupply.filters.PlanRouteFilter;

import java.util.List;

@Repository
public interface PlanRouteTemplateRepositoryCustom {

    ResultList<PlanRouteTemplate> getResultList(PlanRouteFilter filter);

    List<PlanRouteTemplate> lookUp(PlanRouteFilter filter);
}
