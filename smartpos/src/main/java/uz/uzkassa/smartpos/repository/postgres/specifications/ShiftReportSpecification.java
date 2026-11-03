package uz.uzkassa.smartpos.repository.postgres.specifications;

import jakarta.persistence.criteria.*;
import org.apache.commons.lang3.StringUtils;
import uz.uzkassa.smartpos.domain.postgres.company.Branch_;
import uz.uzkassa.smartpos.domain.postgres.receipt.ShiftReport;
import uz.uzkassa.smartpos.domain.postgres.receipt.ShiftReport_;
import uz.uzkassa.smartpos.domain.postgres.terminal.Device_;
import uz.uzkassa.smartpos.enums.SortTypeEnum;
import uz.uzkassa.smartpos.filter.specification.ShiftReportSpecificationFilter;

import java.util.ArrayList;
import java.util.List;

public final class ShiftReportSpecification {
    public static Predicate getPredicate(Root<ShiftReport> root, CriteriaBuilder criteriaBuilder, ShiftReportSpecificationFilter filter) {
        List<Predicate> predicates = new ArrayList<>();
        if (filter.getCompanyId() != null) {
            predicates.add(criteriaBuilder.equal(root.get(ShiftReport_.branch).get(Branch_.companyId), filter.getCompanyId()));
        }
        if (filter.getBranchId() != null) {
            predicates.add(criteriaBuilder.equal(root.get(ShiftReport_.branchId), filter.getBranchId()));
        }
        if (filter.getFrom() != null) {
            predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get(ShiftReport_.endDateTime), filter.getFrom()));
        }
        if (filter.getTo() != null) {
            predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get(ShiftReport_.endDateTime), filter.getTo()));
        }
        if (filter.getTerminalId() != null) {
            predicates.add(criteriaBuilder.equal(root.get(ShiftReport_.terminalId), filter.getTerminalId()));
        }
        if (StringUtils.isNotBlank(filter.getSearch())) {
            predicates.add(criteriaBuilder.or(
                criteriaBuilder.like(criteriaBuilder.lower(root.get(ShiftReport_.terminal).get(Device_.serialNumber)), filter.getSearchForQuery()),
                criteriaBuilder.like(criteriaBuilder.lower(root.get(ShiftReport_.branch).get(Branch_.serialNumber)), filter.getSearchForQuery())
            ));
        }
        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }

    public static Predicate getPredicateWithSort(Root<ShiftReport> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder, ShiftReportSpecificationFilter filter) {
        Expression<?> orderByField;
        if (StringUtils.isNotBlank(filter.getOrderBy())) {
            if ("terminalSN".equals(filter.getOrderBy())) {
                orderByField = root.get(ShiftReport_.terminal).get(Device_.serialNumber);
            } else {
                orderByField = root.get(filter.getOrderBy());
            }
        } else {
            orderByField = root.get(ShiftReport_.endDateTime);
        }
        if (SortTypeEnum.asc.equals(filter.getSortOrder())) {
            query.orderBy(criteriaBuilder.asc(orderByField));
        } else {
            query.orderBy(criteriaBuilder.desc(orderByField));
        }
        return getPredicate(root, criteriaBuilder, filter);
    }
}
