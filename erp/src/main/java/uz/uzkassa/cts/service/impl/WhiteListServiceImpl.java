package uz.uzkassa.cts.service.impl;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.cts.domain.WhiteList;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.dto.whitelist.WhiteListDetailDTO;
import uz.uzkassa.cts.dto.whitelist.WhiteListListDTO;
import uz.uzkassa.cts.dto.whitelist.WhiteListNewDTO;
import uz.uzkassa.cts.enums.CommonStatus;
import uz.uzkassa.cts.filter.WhiteListFilter;
import uz.uzkassa.cts.repository.WhiteListRepository;
import uz.uzkassa.cts.service.WhiteListService;
import uz.uzkassa.cts.validator.ValidationContext;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class WhiteListServiceImpl extends BaseServiceImpl<WhiteList, Long> implements WhiteListService {

    WhiteListRepository whiteListRepository;
    ValidationContext validationContext;

    public WhiteListServiceImpl(WhiteListRepository whiteListRepository, ValidationContext validationContext) {
        super(whiteListRepository);
        this.whiteListRepository = whiteListRepository;
        this.validationContext = validationContext;
    }

    @Override
    @Transactional
    public Long create(WhiteListNewDTO whiteListNewDTO) {
        validationContext.validate(whiteListNewDTO);
        WhiteList whiteList = new WhiteList();
        whiteList.setCustomerId(whiteListNewDTO.getCustomerId());
        whiteList.setFromDate(whiteListNewDTO.getFromDate());
        whiteList.setToDate(whiteListNewDTO.getToDate());
        whiteList.setAction(whiteListNewDTO.getAction());
        whiteList.setStatus(CommonStatus.ACTIVE);
        whiteList.setDescription(whiteListNewDTO.getDescription());
        return whiteListRepository.save(whiteList).getId();
    }

    @Override
    @Transactional
    public Long update(Long id, WhiteListNewDTO whiteListNewDTO) {
        whiteListNewDTO.setId(id);
        validationContext.validate(whiteListNewDTO);
        return whiteListRepository.findById(id).map(whiteList -> {
            whiteList.setCustomerId(whiteListNewDTO.getCustomerId());
            whiteList.setFromDate(whiteListNewDTO.getFromDate());
            whiteList.setToDate(whiteListNewDTO.getToDate());
            whiteList.setAction(whiteListNewDTO.getAction());
            whiteList.setDescription(whiteListNewDTO.getDescription());
            return whiteListRepository.save(whiteList).getId();
        }).orElseThrow(validationService.entityNotFoundExceptionThrow(WHITE_LIST));
    }

    @Override
    public WhiteListDetailDTO get(Long id) {
        return whiteListRepository.findById(id).map(whiteList -> {
            WhiteListDetailDTO whiteListDetailDTO = new WhiteListDetailDTO();
            whiteListDetailDTO.setId(whiteList.getId());
            whiteListDetailDTO.setCustomer(whiteList.getCustomer().toCommonDTO());
            whiteListDetailDTO.setFromDate(whiteList.getFromDate());
            whiteListDetailDTO.setToDate(whiteList.getToDate());
            whiteListDetailDTO.setAction(whiteList.getAction());
            whiteListDetailDTO.setDescription(whiteList.getDescription());
            return whiteListDetailDTO;
        }).orElseThrow(validationService.entityNotFoundExceptionThrow(WHITE_LIST));
    }

    @Override
    public Page<WhiteListListDTO> getList(WhiteListFilter filter) {
        ResultList<WhiteList> resultList = whiteListRepository.getResultList(filter);
        List<WhiteListListDTO> result = resultList.
                getList()
                .stream()
                .map(whiteList -> {
                    WhiteListListDTO whiteListDTO = new WhiteListListDTO();
                    whiteListDTO.setId(whiteList.getId());
                    whiteListDTO.setCustomer(whiteList.getCustomer().toCommonDTO());
                    whiteListDTO.setFromDate(whiteList.getFromDate());
                    whiteListDTO.setToDate(whiteList.getToDate());
                    whiteListDTO.setAction(whiteList.getAction());
                    return whiteListDTO;
                })
                .collect(Collectors.toList());
        return new PageImpl<>(result, filter.getOrderedPageable(), resultList.getCount());
    }

    @Override
    @Transactional
    public void updateStatusCron() {
        whiteListRepository.updateStatus(String.valueOf(CommonStatus.IN_ACTIVE), LocalDate.now());
    }
}
