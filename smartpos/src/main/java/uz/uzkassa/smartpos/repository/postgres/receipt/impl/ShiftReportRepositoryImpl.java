package uz.uzkassa.smartpos.repository.postgres.receipt.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Tuple;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import uz.uzkassa.smartpos.domain.postgres.company.Branch_;
import uz.uzkassa.smartpos.domain.postgres.receipt.ShiftReport;
import uz.uzkassa.smartpos.domain.postgres.receipt.ShiftReport_;
import uz.uzkassa.smartpos.domain.postgres.terminal.Device_;
import uz.uzkassa.smartpos.dto.shift.ShiftReportStatDTO;
import uz.uzkassa.smartpos.filter.ShiftReportFilter;
import uz.uzkassa.smartpos.filter.specification.ShiftReportSpecificationFilter;
import uz.uzkassa.smartpos.repository.postgres.receipt.ShiftReportRepositoryCustom;
import uz.uzkassa.smartpos.repository.postgres.specifications.ShiftReportSpecification;
import uz.uzkassa.smartpos.security.SecurityUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;

@SuppressWarnings("unused")
public class ShiftReportRepositoryImpl implements ShiftReportRepositoryCustom {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public ShiftReportStatDTO getReportStat(ShiftReportSpecificationFilter filter) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> criteriaQuery = criteriaBuilder.createQuery(Tuple.class);
        Root<ShiftReport> root = criteriaQuery.from(ShiftReport.class);
        Predicate predicate = ShiftReportSpecification.getPredicate(root, criteriaBuilder, filter);
        criteriaQuery.multiselect(
                criteriaBuilder.sum(root.get(ShiftReport_.receipts)).as(Long.class),
                criteriaBuilder.sum(root.get(ShiftReport_.receiptsReturned)),
                criteriaBuilder.sum(root.get(ShiftReport_.cash)),
                criteriaBuilder.sum(root.get(ShiftReport_.cashReturned)),
                criteriaBuilder.sum(root.get(ShiftReport_.card)),
                criteriaBuilder.sum(root.get(ShiftReport_.cardReturned)),
                criteriaBuilder.sum(root.get(ShiftReport_.NDS)),
                criteriaBuilder.sum(root.get(ShiftReport_.ndsReturned)),
                criteriaBuilder.count(root.get(ShiftReport_.id))
        );
        criteriaQuery.where(predicate);
        Tuple tuple = entityManager.createQuery(criteriaQuery).getSingleResult();
        if (tuple == null) return null;
        return new ShiftReportStatDTO(
                (Long) tuple.get(0),
                (Long) tuple.get(1),
                (BigDecimal) tuple.get(2),
                (BigDecimal) tuple.get(3),
                (BigDecimal) tuple.get(4),
                (BigDecimal) tuple.get(5),
                (BigDecimal) tuple.get(6),
                (BigDecimal) tuple.get(7),
                (Long) tuple.get(8)
        );
    }

    @Override
    public ShiftReportStatDTO getReportStat(ShiftReportFilter filter) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> criteriaQuery = criteriaBuilder.createQuery(Tuple.class);
        Root<ShiftReport> root = criteriaQuery.from(ShiftReport.class);
        criteriaQuery.multiselect(
                criteriaBuilder.sum(root.get(ShiftReport_.receipts)).as(Long.class),
                criteriaBuilder.sum(root.get(ShiftReport_.receiptsReturned)),
                criteriaBuilder.sum(root.get(ShiftReport_.cash)),
                criteriaBuilder.sum(root.get(ShiftReport_.cashReturned)),
                criteriaBuilder.sum(root.get(ShiftReport_.card)),
                criteriaBuilder.sum(root.get(ShiftReport_.cardReturned)),
                criteriaBuilder.sum(root.get(ShiftReport_.NDS)),
                criteriaBuilder.sum(root.get(ShiftReport_.ndsReturned)),
                criteriaBuilder.count(root.get(ShiftReport_.id))
        );
        var predicates = new ArrayList<Predicate>();
        if (filter.getFrom() != null) {
            predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get(ShiftReport_.endDateTime), filter.getFrom()));
        }
        if (filter.getTo() != null) {
            predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get(ShiftReport_.endDateTime), filter.getTo()));
        }
        if (filter.getUserId() != null) {
            predicates.add(criteriaBuilder.equal(root.get(ShiftReport_.userId), filter.getUserId()));
        }
        if (CollectionUtils.isNotEmpty(filter.getBranchIds())) {
            predicates.add(root.get(ShiftReport_.branchId).in(filter.getBranchIds()));
        } else {
            var branchJoin = root.join(ShiftReport_.branch);
            predicates.add(criteriaBuilder.equal(branchJoin.get(Branch_.companyId), SecurityUtils.getCurrentCompanyId()));
        }
        if (StringUtils.isNotBlank(filter.getSearch())) {
            predicates.add(criteriaBuilder.or(
                    criteriaBuilder.like(criteriaBuilder.lower(root.get(ShiftReport_.terminal).get(Device_.serialNumber)), filter.getSearchForQuery()),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get(ShiftReport_.branch).get(Branch_.serialNumber)), filter.getSearchForQuery())
            ));
        }
        criteriaQuery.where(criteriaBuilder.and(predicates.toArray(new Predicate[0])));
        Tuple tuple = entityManager.createQuery(criteriaQuery).getSingleResult();
        if (tuple == null) return null;
        return new ShiftReportStatDTO(
                (Long) tuple.get(0),
                (Long) tuple.get(1),
                (BigDecimal) tuple.get(2),
                (BigDecimal) tuple.get(3),
                (BigDecimal) tuple.get(4),
                (BigDecimal) tuple.get(5),
                (BigDecimal) tuple.get(6),
                (BigDecimal) tuple.get(7),
                (Long) tuple.get(8)
        );
    }

    @Override
    public ShiftReport getLastShift(Collection<Long> branchIds, Long companyId) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<ShiftReport> criteriaQuery = criteriaBuilder.createQuery(ShiftReport.class);
        Root<ShiftReport> root = criteriaQuery.from(ShiftReport.class);
        Predicate predicate;
        if (CollectionUtils.isNotEmpty(branchIds)) {
            predicate = root.get(ShiftReport_.branchId).in(branchIds);
        } else {
            var branchJoin = root.join(ShiftReport_.branch);
            predicate = criteriaBuilder.equal(branchJoin.get(Branch_.companyId), companyId);
        }
        criteriaQuery.where(predicate);
        criteriaQuery.orderBy(criteriaBuilder.desc(root.get(ShiftReport_.startDateTime)));
        return entityManager.createQuery(criteriaQuery).setMaxResults(1).getSingleResult();
    }

}
