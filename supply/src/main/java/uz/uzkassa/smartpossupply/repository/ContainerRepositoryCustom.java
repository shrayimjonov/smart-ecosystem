package uz.uzkassa.smartpossupply.repository;

import uz.uzkassa.smartpossupply.domain.container.Container;
import uz.uzkassa.smartpossupply.dto.base.ResultList;
import uz.uzkassa.smartpossupply.filters.BaseFilter;

public interface ContainerRepositoryCustom {

    ResultList<Container> getResultList(BaseFilter filter);
}
