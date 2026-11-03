package uz.uzkassa.cts.repository.custom;

import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.OfdApplication;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.dto.tin.integration.OfdApplicationMakeObjectListDTO;
import uz.uzkassa.cts.filter.OfdApplicationFilter;

@Repository
public interface OfdApplicationRepositoryCustom {

    ResultList<OfdApplicationMakeObjectListDTO> getApplications(OfdApplicationFilter filter);

    ResultList<OfdApplication> getResultList(OfdApplicationFilter filter);
}
