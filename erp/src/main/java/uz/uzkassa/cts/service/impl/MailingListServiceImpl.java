package uz.uzkassa.cts.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.cts.domain.MailingList;
import uz.uzkassa.cts.domain.base.SimpleEntity;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.dto.maillinglist.MailingListDTO;
import uz.uzkassa.cts.dto.maillinglist.MailingListDetailDTO;
import uz.uzkassa.cts.dto.maillinglist.MailingListListDTO;
import uz.uzkassa.cts.enums.CommonStatus;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.repository.MailingListRepository;
import uz.uzkassa.cts.service.MailingListService;
import uz.uzkassa.cts.validator.ValidationContext;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class MailingListServiceImpl extends BaseServiceImpl<MailingList, Long> implements MailingListService {

    private final MailingListRepository mailingListRepository;
    private final ValidationContext validationContext;

    public MailingListServiceImpl(MailingListRepository mailingListRepository, ValidationContext validationContext) {
        super(mailingListRepository);
        this.mailingListRepository = mailingListRepository;
        this.validationContext = validationContext;
    }

    @Override
    @Transactional
    public Long create(MailingListDTO mailingListDTO) {
        validationContext.validate(mailingListDTO);
        MailingList mailingList = new MailingList();
        mailingList.setName(mailingListDTO.getName());
        mailingList.setDescription(mailingListDTO.getDescription());
        return mailingListRepository.save(mailingList).getId();
    }

    @Override
    @Transactional
    public Long update(Long id, MailingListDTO mailingListDTO) {
        validationContext.validate(mailingListDTO);
        return mailingListRepository.findById(id)
                .map(mailingList -> {
                    mailingList.setName(mailingListDTO.getName());
                    mailingList.setDescription(mailingListDTO.getDescription());
                    mailingListRepository.save(mailingList);
                    return id;
                }).orElseThrow(validationService.entityNotFoundExceptionThrow(MAILING_LIST));
    }

    @Override
    public MailingListDetailDTO get(Long id) {
        return mailingListRepository.findById(id)
                .map(mailingList -> {
                    MailingListDetailDTO mailingListDetailDTO = new MailingListDetailDTO();
                    mailingListDetailDTO.setId(mailingList.getId());
                    mailingListDetailDTO.setName(mailingList.getName());
                    mailingListDetailDTO.setDescription(mailingList.getDescription());
                    mailingListDetailDTO.setStatus(mailingList.getStatus());
                    mailingListDetailDTO.setMemberCount(mailingList.getMemberCount());
                    return mailingListDetailDTO;
                })
                .orElseThrow(validationService.entityNotFoundExceptionThrow(MAILING_LIST));
    }

    @Override
    @Transactional
    public void updateStatus(Long id, CommonStatus status) {
        mailingListRepository
                .findById(id)
                .ifPresentOrElse(mailingList -> {
                    mailingList.setStatus(status);
                    mailingListRepository.save(mailingList);
                }, () -> validationService.entityNotFoundExceptionThrow(MAILING_LIST).get());
    }

    @Override
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
                .collect(Collectors.toList());
        return new PageImpl<>(result, filter.getOrderedPageable(), resultList.getCount());
    }

    @Override
    public List<CommonDTO> lookUp(BaseFilter filter) {
        return mailingListRepository.getResultList(filter).getList().stream().map(SimpleEntity::toCommonDTO).collect(Collectors.toList());
    }
}
