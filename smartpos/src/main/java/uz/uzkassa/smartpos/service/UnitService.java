package uz.uzkassa.smartpos.service;

import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.smartpos.domain.postgres.common.Unit;
import uz.uzkassa.smartpos.domain.postgres.common.Unit_;
import uz.uzkassa.smartpos.dto.UnitDTO;
import uz.uzkassa.smartpos.dto.filter.BaseFilter;
import uz.uzkassa.smartpos.enums.CompanyType;
import uz.uzkassa.smartpos.repository.postgres.common.UnitRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UnitService {
    private final UnitRepository unitRepository;

    public List<UnitDTO> findAll() {
        return unitRepository.findAll()
            .stream()
            .map(Unit::toDTO)
            .collect(Collectors.toList());
    }

    public List<UnitDTO> findAllByCompanyType(final CompanyType companyType) {
        return unitRepository.findAll()
            .stream()
            .filter(unit -> CollectionUtils.isNotEmpty(unit.getTypes()))
            .filter(unit -> unit.getTypes().contains(companyType))
            .map(Unit::toDTO)
            .collect(Collectors.toList());
    }

    public Page<UnitDTO> getList(BaseFilter filter) {
        return unitRepository.findAll((Specification<Unit>) (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (StringUtils.isNotBlank(filter.getSearch())) {
                predicates.add(criteriaBuilder.or(
                    criteriaBuilder.like(criteriaBuilder.lower(root.get(Unit_.name)), filter.getSearchForQuery()),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get(Unit_.nameUz)), filter.getSearchForQuery()),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get(Unit_.description)), filter.getSearchForQuery())
                ));
            }
            if (StringUtils.isNotBlank(filter.getOrderBy())) {
                query.orderBy(criteriaBuilder.asc(root.get(filter.getOrderBy())));
            } else {
                query.orderBy(criteriaBuilder.asc(root.get(Unit_.id)));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        }, filter.getPageable()).map(Unit::toDTO);
    }

    @Transactional
    public void save(UnitDTO unitDTO) {
        Unit unit = Optional.ofNullable(unitDTO.getId()).flatMap(unitRepository::findById).orElseGet(Unit::new);
        unit.setName(unitDTO.getNameRu());
        unit.setNameUz(unitDTO.getNameUz());
        unit.setDescription(unitDTO.getDescription());
        unit.setCountable(unitDTO.isCountable());
        unitRepository.save(unit);
    }
}
