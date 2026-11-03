package uz.uzkassa.smartpos.service;

import jakarta.persistence.criteria.Predicate;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.smartpos.domain.postgres.company.Company;
import uz.uzkassa.smartpos.domain.postgres.company.OneCCompany;
import uz.uzkassa.smartpos.domain.postgres.company.OneCCompany_;
import uz.uzkassa.smartpos.dto.OneCCompanyDto;
import uz.uzkassa.smartpos.enums.SortTypeEnum;
import uz.uzkassa.smartpos.filter.OneCCompanyFilter;
import uz.uzkassa.smartpos.mappers.CompanyMapper;
import uz.uzkassa.smartpos.repository.postgres.company.CompanyRepository;
import uz.uzkassa.smartpos.repository.postgres.company.OneCCompanyRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Service
public class OneCCompanyService extends BaseService{
    private final OneCCompanyRepository repository;
    private final CompanyMapper companyMapper;
    private final CompanyRepository companyRepository;

    @Transactional
    public void create(Long id) {
        OneCCompany oneCCompany = repository.findById(id).orElseGet(OneCCompany::new);
        Company company = companyRepository.findById(id).orElseThrow(notFound(localizationService.getMessage("company")));
        oneCCompany.setId(id);
        oneCCompany.setTin(company.getInn());
        oneCCompany.setName(company.getName());
        oneCCompany.setLastTime(LocalDateTime.now());
        oneCCompany.addReceipt();
        repository.save(oneCCompany);
    }

    public Page<OneCCompanyDto> findAllByFilter(OneCCompanyFilter filter) {
        return repository.findAll((Specification<OneCCompany>) (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (filter.getReceiptCountFrom() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get(OneCCompany_.receiptCount), filter.getReceiptCountFrom()));
            }
            if (filter.getReceiptCountTo() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get(OneCCompany_.receiptCount), filter.getReceiptCountTo()));
            }
            if (StringUtils.isNotBlank(filter.getSearch())) {
                predicates.add(criteriaBuilder.or(
                    criteriaBuilder.like(criteriaBuilder.lower(root.get(OneCCompany_.tin)), filter.getSearchForQuery()),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get(OneCCompany_.name)), filter.getSearchForQuery())
                ));
            }
            if (StringUtils.isNotBlank(filter.getOrderBy())) {
                if (SortTypeEnum.asc.equals(filter.getSortOrder())) {
                    query.orderBy(criteriaBuilder.asc(root.get(filter.getOrderBy())));
                } else {
                    query.orderBy(criteriaBuilder.desc(root.get(filter.getOrderBy())));
                }
            } else {
                query.orderBy(criteriaBuilder.desc(root.get(OneCCompany_.lastTime)));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        }, filter.getPageable()).map(companyMapper::toDto);
    }
}
