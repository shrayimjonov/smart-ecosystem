package uz.uzkassa.silen.repository.pg;

import org.springframework.data.domain.Page;
import uz.uzkassa.silen.domain.AcceptanceTransferAct;
import uz.uzkassa.silen.dto.filter.AcceptanceTransferActFilter;

public interface AcceptanceTransferActRepositoryCustom {

    Page<AcceptanceTransferAct> findAllByFilter(AcceptanceTransferActFilter filter);
}
