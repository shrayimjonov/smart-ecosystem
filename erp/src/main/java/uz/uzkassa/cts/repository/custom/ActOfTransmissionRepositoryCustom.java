package uz.uzkassa.cts.repository.custom;

import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.dto.actoftransmission.smartpos.ActOfTransmissionSmartposDTO;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.filter.BaseFilter;

@Repository
public interface ActOfTransmissionRepositoryCustom {

    ResultList<ActOfTransmissionSmartposDTO> getResultList(BaseFilter filter);
}
