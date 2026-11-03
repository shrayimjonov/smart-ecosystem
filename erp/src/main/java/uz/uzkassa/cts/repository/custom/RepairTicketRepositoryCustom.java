package uz.uzkassa.cts.repository.custom;

import uz.uzkassa.cts.domain.RepairTicket;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.filter.RepairTicketFilter;

public interface RepairTicketRepositoryCustom {

    ResultList<RepairTicket> getResultList(RepairTicketFilter filter);
}
