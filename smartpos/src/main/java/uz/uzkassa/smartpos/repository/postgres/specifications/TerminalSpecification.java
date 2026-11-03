package uz.uzkassa.smartpos.repository.postgres.specifications;

import jakarta.persistence.criteria.*;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import uz.uzkassa.smartpos.domain.postgres.company.Branch_;
import uz.uzkassa.smartpos.domain.postgres.terminal.Device;
import uz.uzkassa.smartpos.domain.postgres.terminal.Device_;
import uz.uzkassa.smartpos.enums.SortTypeEnum;
import uz.uzkassa.smartpos.filter.specification.TerminalSpecificationFilter;

import java.util.ArrayList;
import java.util.List;

public final class TerminalSpecification {
    @NotNull
    public static Predicate getPredicateWithSort(Root<Device> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder, TerminalSpecificationFilter filter) {
        Predicate predicate = getPredicate(root, criteriaBuilder, filter);
        Expression<?> orderByField;
        if (StringUtils.isNotBlank(filter.getOrderBy())) {
            orderByField = root.get(filter.getOrderBy());
        } else {
            orderByField = root.get(Device_.id);
        }
        if (SortTypeEnum.asc.equals(filter.getSortOrder())) {
            query.orderBy(criteriaBuilder.asc(orderByField));
        } else {
            query.orderBy(criteriaBuilder.desc(orderByField));
        }
        return predicate;
    }

    @NotNull
    public static Predicate getPredicate(Root<Device> root, CriteriaBuilder criteriaBuilder, TerminalSpecificationFilter filter) {
        List<Predicate> predicates = new ArrayList<>();
        if (filter.getCompanyId() != null) {
            predicates.add(criteriaBuilder.equal(root.get(Device_.branch).get(Branch_.companyId), filter.getCompanyId()));
        }
        if (filter.getBranchId() != null) {
            predicates.add(criteriaBuilder.equal(root.get(Device_.branchId), filter.getBranchId()));
        }
        if (filter.getFrom() != null) {
            predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get(Device_.createdDate), filter.getFrom()));
        }
        if (filter.getTo() != null) {
            predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get(Device_.createdDate), filter.getTo()));
        }
        if (StringUtils.isNotBlank(filter.getSearch())) {
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get(Device_.serialNumber)), filter.getSearchForQuery()));
        }
        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }

}
