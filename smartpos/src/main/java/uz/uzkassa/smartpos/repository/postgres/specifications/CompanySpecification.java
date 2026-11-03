package uz.uzkassa.smartpos.repository.postgres.specifications;

import jakarta.persistence.criteria.*;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import uz.uzkassa.smartpos.domain.postgres.company.ActivityType;
import uz.uzkassa.smartpos.domain.postgres.company.ActivityType_;
import uz.uzkassa.smartpos.domain.postgres.company.Company;
import uz.uzkassa.smartpos.domain.postgres.company.Company_;
import uz.uzkassa.smartpos.domain.postgres.user.User_;
import uz.uzkassa.smartpos.dto.filter.CompanySpecificationFilter;
import uz.uzkassa.smartpos.enums.SortTypeEnum;

import java.util.ArrayList;
import java.util.List;

public final class CompanySpecification {
    @NotNull
    public static Predicate getPredicateWithSort(Root<Company> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder, CompanySpecificationFilter filter) {
        Predicate predicate = getPredicate(root, criteriaBuilder, filter);
        Expression<?> orderByField;
        if (StringUtils.isNotBlank(filter.getOrderBy())) {
            orderByField = root.get(filter.getOrderBy());
        } else {
            orderByField = root.get(Company_.id);
        }
        if (SortTypeEnum.asc.equals(filter.getSortOrder())) {
            query.orderBy(criteriaBuilder.asc(orderByField));
        } else {
            query.orderBy(criteriaBuilder.desc(orderByField));
        }
        return predicate;
    }

    @NotNull
    public static Predicate getPredicate(Root<Company> root, CriteriaBuilder criteriaBuilder, CompanySpecificationFilter filter) {
        List<Predicate> predicates = new ArrayList<>();
        if (filter.getManagerId() != null) {
            predicates.add(criteriaBuilder.equal(root.get(Company_.managerId), filter.getManagerId()));
        }
        if (filter.getRegionId() != null) {
            predicates.add(criteriaBuilder.equal(root.get(Company_.regionId), filter.getRegionId()));
        }
        if (filter.getCityId() != null) {
            predicates.add(criteriaBuilder.equal(root.get(Company_.cityId), filter.getCityId()));
        }
        if (filter.getCompanyId() != null) {
            predicates.add(criteriaBuilder.equal(root.get(Company_.id), filter.getCompanyId()));
        }
        if (filter.getActivityTypeId() != null) {
            SetJoin<Company,ActivityType> join = root.join(Company_.activityTypes);
            predicates.add(criteriaBuilder.equal(join.get(ActivityType_.id), filter.getActivityTypeId()));
        }
        if (filter.getBusinessType() != null) {
            predicates.add(criteriaBuilder.equal(root.get(Company_.businessType), filter.getBusinessType()));
        }
        if (filter.getInn() != null) {
            predicates.add(criteriaBuilder.equal(root.get(Company_.inn), filter.getInn()));
        }
        if (filter.getFreeTrial() != null) {
            predicates.add(criteriaBuilder.equal(root.get(Company_.freeTrial), filter.getFreeTrial()));
        }
        if (filter.getFrom() != null) {
            predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get(Company_.createdDate), filter.getFrom()));
        }
        if (filter.getTo() != null) {
            predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get(Company_.createdDate), filter.getTo()));
        }
        if (StringUtils.isNotBlank(filter.getSearch())) {
            predicates.add(criteriaBuilder.or(
                criteriaBuilder.like(criteriaBuilder.lower(root.get(Company_.NAME)), filter.getSearchForQuery()),
                criteriaBuilder.like(criteriaBuilder.lower(root.get(Company_.INN)), filter.getSearchForQuery()),
                criteriaBuilder.like(root.get(Company_.owner).get(User_.login), filter.getSearchForQuery())
            ));
        }
        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }
}
