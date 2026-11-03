package uz.uzkassa.cts.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.cts.domain.cases.CaseTypeEmployees;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.dto.caseTypeEmployees.CaseTypeEmployeesDTO;
import uz.uzkassa.cts.dto.caseTypeEmployees.CaseTypeEmployeesListDTO;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.repository.BaseRepository;
import uz.uzkassa.cts.repository.CaseTypeEmployeesRepository;
import uz.uzkassa.cts.repository.TelegramUsernameRepository;
import uz.uzkassa.cts.service.CaseTypeEmployeesService;

import java.util.Objects;


@Slf4j
@Service
@Transactional
public class CaseTypeEmployeesServiceImpl extends BaseServiceImpl<CaseTypeEmployees, Long> implements CaseTypeEmployeesService {

    private final CaseTypeEmployeesRepository typeEmployeesRepository;
    private final TelegramUsernameRepository telegramUsernameRepository;

    public CaseTypeEmployeesServiceImpl(BaseRepository<CaseTypeEmployees, Long> repository,
                                        CaseTypeEmployeesRepository typeEmployeesRepository,
                                        TelegramUsernameRepository telegramUsernameRepository) {
        super(repository);
        this.typeEmployeesRepository = typeEmployeesRepository;
        this.telegramUsernameRepository = telegramUsernameRepository;
    }

    @Override
    public Boolean insertUpdate(CaseTypeEmployeesDTO typeEmployeesDTO) {
        if (Objects.nonNull(typeEmployeesDTO.getEmployeeId())) {
            /*insert permission of admin CTO employees*/
            typeEmployeesRepository.insertUpdate(
                StringUtils.join(typeEmployeesDTO.getCaseTypes(), ","),
                typeEmployeesDTO.getEmployeeId());
            /*insert telegram ID of users*/
            telegramUsernameRepository.insertUpdate(typeEmployeesDTO.getEmployeeId(), typeEmployeesDTO.getUsername());
            return Boolean.TRUE;
        } else {
            return Boolean.FALSE;
        }
    }

    @Override
    public Page<CaseTypeEmployeesListDTO> list(BaseFilter filter) {
        ResultList<CaseTypeEmployeesListDTO> resultList = typeEmployeesRepository.getResultList(filter);
        return new PageImpl<>(resultList.getList(), filter.getOrderedPageable(), resultList.getCount());
    }
}
