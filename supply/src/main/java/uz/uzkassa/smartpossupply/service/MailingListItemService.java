package uz.uzkassa.smartpossupply.service;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.smartpossupply.domain.mailing.MailingListItem;
import uz.uzkassa.smartpossupply.dto.base.ResultList;
import uz.uzkassa.smartpossupply.dto.mailinglist.MailingListItemListDTO;
import uz.uzkassa.smartpossupply.filters.MailingListItemFilter;
import uz.uzkassa.smartpossupply.repository.MailingListItemRepository;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Service
@Transactional(readOnly = true)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class MailingListItemService extends BaseService {

    MailingListItemRepository mailingListItemRepository;

    public Page<MailingListItemListDTO> getList(MailingListItemFilter filter) {
        ResultList<MailingListItem> resultList = mailingListItemRepository.getResultList(filter);
        List<MailingListItemListDTO> result = resultList
            .getList()
            .stream()
            .map(mailingListItem -> {
                MailingListItemListDTO mailingListDTO = new MailingListItemListDTO();
                mailingListDTO.setId(mailingListItem.getId());
                mailingListDTO.setMailingList(mailingListItem.getMailingList().toCommonDTO());
                mailingListDTO.setCustomer(mailingListItem.getCustomer().toCommonBaseDTO());
                mailingListDTO.setPhone(mailingListItem.getCustomer().getPhone());
                mailingListDTO.setStatus(mailingListItem.getStatus());
                return mailingListDTO;
            })
            .toList();
        return new PageImpl<>(result, filter.getSortedPageable(), resultList.getCount());
    }
}
