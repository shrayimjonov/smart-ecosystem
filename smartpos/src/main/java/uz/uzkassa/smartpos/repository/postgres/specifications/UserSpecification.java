package uz.uzkassa.smartpos.repository.postgres.specifications;

import jakarta.persistence.criteria.*;
import jakarta.validation.constraints.NotNull;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import uz.uzkassa.smartpos.domain.postgres.company.Branch;
import uz.uzkassa.smartpos.domain.postgres.company.BranchGroup;
import uz.uzkassa.smartpos.domain.postgres.company.BranchGroup_;
import uz.uzkassa.smartpos.domain.postgres.company.Branch_;
import uz.uzkassa.smartpos.domain.postgres.user.Authority;
import uz.uzkassa.smartpos.domain.postgres.user.Authority_;
import uz.uzkassa.smartpos.domain.postgres.user.User;
import uz.uzkassa.smartpos.domain.postgres.user.User_;
import uz.uzkassa.smartpos.enums.HeaderKeys;
import uz.uzkassa.smartpos.enums.SortTypeEnum;
import uz.uzkassa.smartpos.filter.specification.UserSpecificationFilter;
import uz.uzkassa.smartpos.security.HeaderUtils;

import java.util.ArrayList;
import java.util.List;

public final class UserSpecification {
    @NotNull
    public static Predicate getPredicate(Root<User> root, CriteriaBuilder criteriaBuilder, UserSpecificationFilter filter) {
        List<Predicate> predicates = new ArrayList<>();
        if (Boolean.TRUE.equals(filter.getActivated())) {
            predicates.add(criteriaBuilder.isTrue(root.get(User_.activated)));
        }
        if (filter.isDismissed()) {
            predicates.add(criteriaBuilder.isTrue(root.get(User_.deleted)));
        } else {
            predicates.add(criteriaBuilder.isFalse(root.get(User_.deleted)));
        }
        if (CollectionUtils.isNotEmpty(filter.getRoles())) {
            SetJoin<User, Authority> authorityJoin = root.join(User_.authorities);
            predicates.add(authorityJoin.get(Authority_.name).in(filter.getRoles()));
        }
        if (filter.getCompanyId() != null) {
            predicates.add(criteriaBuilder.equal(root.get(User_.companyId), filter.getCompanyId()));
        }
        if (CollectionUtils.isNotEmpty(filter.getBranchIds())) {
            if (HeaderUtils.getCurrentHeaderByKey(HeaderKeys.TERMINAL_SN) != null) {
                predicates.add(criteriaBuilder.or(
                        root.get(User_.branchId).in(filter.getBranchIds()),
                        criteriaBuilder.isTrue(root.get(User_.owner))
                ));
            } else {
                predicates.add(root.get(User_.branchId).in(filter.getBranchIds()));
            }
        }
        if (CollectionUtils.isNotEmpty(filter.getBranchGroupIds())) {
            Join<User, Branch> branchJoin = root.join(User_.branch);
            SetJoin<Branch, BranchGroup> branchGroupJoin = branchJoin.join(Branch_.groups);
            predicates.add(branchGroupJoin.get(BranchGroup_.id).in(filter.getBranchGroupIds()));
        }
        if (filter.getFrom() != null) {
            predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get(User_.createdDate), filter.getFrom()));
        }
        if (filter.getTo() != null) {
            predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get(User_.createdDate), filter.getTo()));
        }
        if (StringUtils.isNotBlank(filter.getSearch())) {
            predicates.add(criteriaBuilder.or(
                    criteriaBuilder.like(criteriaBuilder.lower(root.get(User_.lastName)), filter.getSearchForQuery()),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get(User_.firstName)), filter.getSearchForQuery()),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get(User_.patronymic)), filter.getSearchForQuery()),
                    criteriaBuilder.like(root.get(User_.login), filter.getSearchForQuery())));
        }
        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }

    @NotNull
    public static Predicate getPredicateWithSort(Root<User> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder, UserSpecificationFilter filter) {
        Expression<?> orderByField;
        if (StringUtils.isNotBlank(filter.getOrderBy())) {
            if ("branchName".equals(filter.getOrderBy())) {
                orderByField = root.get(User_.branch).get(Branch_.name);
            } else if ("fullName".equals(filter.getOrderBy())) {
                orderByField = root.get(User_.lastName);
            } else {
                orderByField = root.get(filter.getOrderBy());
            }
        } else {
            orderByField = root.get(User_.id);
        }
        if (SortTypeEnum.asc.equals(filter.getSortOrder())) {
            query.orderBy(criteriaBuilder.asc(orderByField));
        } else {
            query.orderBy(criteriaBuilder.desc(orderByField));
        }
        query.distinct(true);
        return getPredicate(root, criteriaBuilder, filter);
    }
}
