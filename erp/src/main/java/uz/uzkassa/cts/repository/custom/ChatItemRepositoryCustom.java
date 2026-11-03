package uz.uzkassa.cts.repository.custom;

import uz.uzkassa.cts.domain.hrms.ChatItem;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.filter.BaseFilter;

public interface ChatItemRepositoryCustom {

    ResultList<ChatItem> getResultList(BaseFilter filter);
}
