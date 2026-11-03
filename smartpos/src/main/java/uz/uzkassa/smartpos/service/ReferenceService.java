package uz.uzkassa.smartpos.service;

import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.smartpos.domain.postgres.*;
import uz.uzkassa.smartpos.filter.ReferenceFilter;
import uz.uzkassa.smartpos.repository.postgres.ReferenceRepository;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class ReferenceService {
    private final ReferenceRepository referenceRepository;
    private final ReferenceMapper referenceMapper;

    @Transactional
    public Long create(ReferenceCreateDto referenceCreateDto) {
        Reference reference = referenceRepository.save(referenceMapper.toEntity(referenceCreateDto));
        return reference.getId();
    }

    @Transactional
    public void update(ReferenceCreateDto referenceCreateDto, Long id) {
        Reference reference = referenceRepository.findById(id).orElseThrow();
        referenceMapper.partialUpdate(referenceCreateDto, reference);
        referenceRepository.save(reference);
    }

    @Transactional
    public void delete(Long id) {
        referenceRepository.deleteById(id);
    }

    public Page<ReferenceAdminDto> findAll(ReferenceFilter filter) {
        return referenceRepository.findAll((Specification<Reference>) (root, query, criteriaBuilder) -> {
            var predicates = new ArrayList<Predicate>();
            predicates.add(criteriaBuilder.isNull(root.get(Reference_.parentId)));
            if (StringUtils.isNotBlank(filter.getSearch())) {
                predicates.add(criteriaBuilder.or(
                        criteriaBuilder.like(criteriaBuilder.lower(root.get(Reference_.nameRu)), filter.getSearchForQuery()),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get(Reference_.nameUz)), filter.getSearchForQuery()),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get(Reference_.nameCyrillic)), filter.getSearchForQuery())
                ));
            }
            query.orderBy(criteriaBuilder.desc(root.get(Reference_.id)));
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        }, filter.getPageable())
                .map(referenceMapper::toDto);
    }

}
