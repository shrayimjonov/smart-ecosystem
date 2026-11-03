package uz.uzkassa.smartpossupply.service;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.smartpossupply.domain.base.SimpleEntity;
import uz.uzkassa.smartpossupply.domain.company.Company;
import uz.uzkassa.smartpossupply.domain.mailing.MailingList;
import uz.uzkassa.smartpossupply.domain.mailing.MailingListItem;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.dto.base.ResultList;
import uz.uzkassa.smartpossupply.dto.mailinglist.MailingListDTO;
import uz.uzkassa.smartpossupply.dto.mailinglist.MailingListDetailDTO;
import uz.uzkassa.smartpossupply.dto.mailinglist.MailingListListDTO;
import uz.uzkassa.smartpossupply.enums.CommonStatus;
import uz.uzkassa.smartpossupply.enums.MailingListItemStatus;
import uz.uzkassa.smartpossupply.filters.BaseFilter;
import uz.uzkassa.smartpossupply.repository.MailingListRepository;
import uz.uzkassa.smartpossupply.validator.ValidationContext;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Service
@Transactional(readOnly = true)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class MailingListService extends BaseService {

    ValidationContext validationContext;
    MailingListRepository mailingListRepository;

    @Transactional
    public Long create(MailingListDTO mailingListDTO) {
        validationContext.validate(mailingListDTO);
        MailingList mailingList = new MailingList();
        mailingList.setName(mailingListDTO.getName());
        mailingList.setDescription(mailingListDTO.getDescription());
        mailingList.setStatus(CommonStatus.ACTIVE);
        mailingList.setCompanyId(mailingListDTO.getCompanyId());

        List<Company> customers = companyRepository.findAllById(mailingListDTO.getCustomers());
        List<MailingListItem> mailingListItems = customers.stream().map(customer -> {
            MailingListItem mailingListItem = new MailingListItem();
            mailingListItem.setCustomer(customer);
            mailingListItem.setMailingList(mailingList);
            mailingListItem.setStatus(MailingListItemStatus.PENDING);
            return mailingListItem;
        }).toList();
        mailingList.setItems(mailingListItems);
        mailingListRepository.save(mailingList);
        return mailingList.getId();
    }

    @Transactional
    public Long update(Long id, MailingListDTO mailingListDTO) {
        mailingListDTO.setId(id);
        validationContext.validate(mailingListDTO);
        return mailingListRepository
                .findById(id)
                .map(mailingList -> {
                    mailingList.setName(mailingListDTO.getName());
                    mailingList.setDescription(mailingListDTO.getDescription());
                    mailingList.getItems().clear();
                    List<Company> customers = companyRepository.findAllById(mailingListDTO.getCustomers());
                    customers.forEach(customer -> {
                        MailingListItem item = new MailingListItem();
                        item.setCustomer(customer);
                        item.setMailingList(mailingList);
                        item.setStatus(MailingListItemStatus.PENDING);
                        mailingList.getItems().add(item);
                    });
                    mailingList.setMemberCount(mailingList.getItems().size());
                    mailingListRepository.save(mailingList);
                    return mailingList.getId();
                })
                .orElseThrow(validationService.entityNotFoundExceptionThrow(MailingList.class.getSimpleName(), "id", id));
    }

    @Transactional
    public void updateStatus(Long id, CommonStatus status) {
        mailingListRepository
            .findById(id)
            .ifPresentOrElse(mailingList -> {
                mailingList.setStatus(status);
                mailingListRepository.save(mailingList);
            }, () -> validationService.entityNotFoundExceptionThrow(MailingList.class.getSimpleName(), "id", id).get());
    }

    public Page<MailingListListDTO> getList(BaseFilter filter) {
        ResultList<MailingList> resultList = mailingListRepository.getResultList(filter);
        List<MailingListListDTO> result = resultList
            .getList()
            .stream()
            .map(mailingList -> {
                MailingListListDTO mailingListDTO = new MailingListListDTO();
                mailingListDTO.setId(mailingList.getId());
                mailingListDTO.setName(mailingList.getName());
                mailingListDTO.setDescription(mailingList.getDescription());
                mailingListDTO.setStatus(mailingList.getStatus());
                mailingListDTO.setMemberCount(mailingList.getMemberCount());
                return mailingListDTO;
            })
            .toList();
        return new PageImpl<>(result, filter.getSortedPageable(), resultList.getCount());
    }

    @Transactional
    public void delete(Long id) {
        mailingListRepository.deleteById(id);
    }

    public MailingListDetailDTO get(Long id) {
        return mailingListRepository
            .findById(id)
            .map(mailingList -> {
                MailingListDetailDTO mailingListDetailDTO = new MailingListDetailDTO();
                mailingListDetailDTO.setId(mailingList.getId());
                mailingListDetailDTO.setName(mailingList.getName());
                mailingListDetailDTO.setDescription(mailingList.getDescription());
                mailingListDetailDTO.setStatus(mailingList.getStatus());
                mailingListDetailDTO.setCustomers(mailingList.getItems().stream().map(MailingListItem::getCustomer).map(Company::toCommonBaseDTO).toList());
                mailingListDetailDTO.setCompanyId(mailingList.getCompany() != null ? mailingList.getCompanyId() : null);
                return mailingListDetailDTO;
            })
            .orElseThrow(validationService.entityNotFoundExceptionThrow(MailingList.class.getSimpleName(), "id", id));
    }

    public List<CommonDTO> lookUp(BaseFilter filter) {
        return mailingListRepository.lookUp(filter).stream().map(SimpleEntity::toCommonDTO).toList();
    }
}
