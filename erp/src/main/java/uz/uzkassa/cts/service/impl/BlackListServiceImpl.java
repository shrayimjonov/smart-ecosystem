package uz.uzkassa.cts.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import uz.uzkassa.cts.domain.BlackList;
import uz.uzkassa.cts.domain.BlackList_;
import uz.uzkassa.cts.dto.blacklist.request.BlackListRequestDto;
import uz.uzkassa.cts.dto.blacklist.response.BlackListResponseDto;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.mappings.BlackListMapper;
import uz.uzkassa.cts.repository.BlackListRepository;
import uz.uzkassa.cts.service.BlackListService;

import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;

@Service
public class BlackListServiceImpl extends BaseServiceImpl<BlackList, Long>  implements BlackListService {

    private final BlackListRepository repository;
    private final BlackListMapper blackListMapper;

    public BlackListServiceImpl(BlackListRepository repository, BlackListMapper blackListMapper) {
        super(repository);
        this.repository = repository;
        this.blackListMapper = blackListMapper;
    }

    @Override
    public Page<BlackListResponseDto> list(BaseFilter filter) {
        Specification<BlackList> specification = (root, criteriaQuery, criteriaBuilder) -> {
            criteriaQuery.distinct(true);

            ArrayList<Predicate> list = new ArrayList<>();
            if (StringUtils.isNotEmpty(filter.getSearch())) {
                Path<String> tinPath = root.get(BlackList_.TIN);
                Path<String> descriptionPath = root.get(BlackList_.DESCRIPTION);

                Predicate tinPred = criteriaBuilder.like(tinPath, filter.getSearchForQuery());
                Predicate descriptionPred = criteriaBuilder.like(criteriaBuilder.lower(descriptionPath), filter.getSearchForQuery());
                list.add(criteriaBuilder.or(tinPred, descriptionPred));
            }
            return criteriaBuilder.and(list.toArray(new Predicate[0]));
        };

        return repository.findAll(specification, filter.getOrderedPageable())
            .map(blackListMapper::toDto);
    }

    @Override
    public BlackListResponseDto add(BlackListRequestDto dto) {
        BlackList blackList = blackListMapper.toEntity(dto);
        repository.save(blackList);
        return blackListMapper.toDto(blackList);
    }

    @Override
    public BlackListResponseDto getById(Long id) {
        return blackListMapper.toDto(findById(id));
    }

    @Override
    public BlackListResponseDto updateById(Long id, BlackListRequestDto dto) {
        BlackList blackList = findById(id);
        blackListMapper.toDomainFromDto(dto, blackList);
        save(blackList);

        return blackListMapper.toDto(blackList);
    }
}
