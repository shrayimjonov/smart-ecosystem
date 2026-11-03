package uz.uzkassa.smartpossupply.repository;

import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.acceptancetransferact.AcceptanceTransferAct;
import uz.uzkassa.smartpossupply.dto.base.ResultList;
import uz.uzkassa.smartpossupply.filters.AcceptanceTransferActFilter;

import java.util.List;

@Repository
public interface AcceptanceTransferActRepositoryCustom {

    ResultList<AcceptanceTransferAct> getResultList(AcceptanceTransferActFilter filter);

    List<AcceptanceTransferAct> lookUp(AcceptanceTransferActFilter filter);
}
