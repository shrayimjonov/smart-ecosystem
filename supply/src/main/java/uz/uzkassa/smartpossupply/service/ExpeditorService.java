package uz.uzkassa.smartpossupply.service;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.smartpossupply.domain.auth.User;
import uz.uzkassa.smartpossupply.dto.base.ResultList;
import uz.uzkassa.smartpossupply.dto.expeditor.ExpeditorDetailDTO;
import uz.uzkassa.smartpossupply.dto.expeditor.ExpeditorListDTO;
import uz.uzkassa.smartpossupply.dto.expeditor.ExpeditorStatisticsDTO;
import uz.uzkassa.smartpossupply.filters.ExpeditorFilter;
import uz.uzkassa.smartpossupply.repository.ExpeditorRepository;

import static lombok.AccessLevel.PRIVATE;

@Service
@Transactional(readOnly = true)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class ExpeditorService extends BaseService {

    ExpeditorRepository expeditorRepository;

    public ExpeditorDetailDTO get(Long id) {
        return expeditorRepository.findById(id).map(expeditor -> {
            ExpeditorDetailDTO expeditorDetailDTO = new ExpeditorDetailDTO();
            expeditorDetailDTO.setId(expeditor.getId());
            expeditorDetailDTO.setFirstName(expeditor.getFirstName());
            expeditorDetailDTO.setLastName(expeditor.getLastName());
            expeditorDetailDTO.setPatronymic(expeditor.getPatronymic());
            expeditorDetailDTO.setRole(expeditor.getRole());
            expeditorDetailDTO.setBranch(expeditor.getBranch().toCommonDTO());
            return expeditorDetailDTO;
        }).orElseThrow(validationService.entityNotFoundExceptionThrow(User.class.getSimpleName(), "id", id));
    }

    public Page<ExpeditorListDTO> getList(ExpeditorFilter filter) {
        ResultList<ExpeditorListDTO> resultList = expeditorRepository.getResultList(filter);
        return new PageImpl<>(resultList.getList(), filter.getSortedPageable(), resultList.getCount());
    }

    public ExpeditorStatisticsDTO getStatistics(ExpeditorFilter filter) {
        return expeditorRepository.getStatistics(filter);
    }
}
