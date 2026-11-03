package uz.uzkassa.smartpos.repository.postgres.sales.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.jdbc.core.simple.JdbcClient;
import uz.uzkassa.smartpos.domain.postgres.sales.Transaction;
import uz.uzkassa.smartpos.domain.postgres.sales.Transaction_;
import uz.uzkassa.smartpos.dto.terminal.sales.TransactionAdminStatisticsDto;
import uz.uzkassa.smartpos.dto.terminal.sales.TransactionDynamicsDto;
import uz.uzkassa.smartpos.dto.terminal.sales.TransactionStatisticsDto;
import uz.uzkassa.smartpos.filter.TransactionFilter;
import uz.uzkassa.smartpos.repository.postgres.sales.TransactionRepositoryCustom;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@SuppressWarnings("unused")
@RequiredArgsConstructor
public class TransactionRepositoryImpl implements TransactionRepositoryCustom {
    private final EntityManager entityManager;
    private final JdbcClient jdbcClient;

    @Override
    public TransactionStatisticsDto statistics(TransactionFilter filter) {
        var criteriaBuilder = entityManager.getCriteriaBuilder();
        var query = criteriaBuilder.createQuery(TransactionStatisticsDto.PaymentTypeStatisticsDto.class);
        var root = query.from(Transaction.class);
        var predicates = new ArrayList<Predicate>();
        if (filter.getCompanyId() != null) {
            predicates.add(criteriaBuilder.equal(root.get(Transaction_.companyId), filter.getCompanyId()));
        }
        if (CollectionUtils.isNotEmpty(filter.getBranchIds())) {
            predicates.add(root.get(Transaction_.branchId).in(filter.getBranchIds()));
        }
        if (filter.getUserId() != null) {
            predicates.add(criteriaBuilder.equal(root.get(Transaction_.userId), filter.getUserId()));
        }
        if (filter.getStatus() != null) {
            predicates.add(criteriaBuilder.equal(root.get(Transaction_.status), filter.getStatus()));
        }
        if (filter.getPaymentType() != null) {
            predicates.add(criteriaBuilder.equal(root.get(Transaction_.paymentType), filter.getPaymentType()));
        }
        if (filter.getFrom() != null) {
            predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get(Transaction_.dateTime), filter.getFrom()));
        }
        if (filter.getTo() != null) {
            predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get(Transaction_.dateTime), filter.getTo()));
        }
        query.where(predicates.toArray(new Predicate[0]));
        query.groupBy(root.get(Transaction_.paymentType));
        query.select(criteriaBuilder.construct(TransactionStatisticsDto.PaymentTypeStatisticsDto.class,
                root.get(Transaction_.paymentType),
                criteriaBuilder.count(root.get(Transaction_.id)),
                criteriaBuilder.sum(root.get(Transaction_.amount))
        ));
        var result = new TransactionStatisticsDto();
        result.setPaymentTypeStatistics(entityManager.createQuery(query).getResultList());
        return result;
    }

    @Override
    public List<TransactionDynamicsDto> transactionDynamics(TransactionFilter filter) {
        String dynamicSelect = switch (filter.getTimeInterval()) {
            case DAY, day -> "date_trunc('day', date_time)";
            case WEEK, week -> "date_trunc('week', date_time)";
            case MONTH, month -> "date_trunc('month', date_time)";
            case HOUR, hour -> "date_trunc('hour', date_time)";
        };
        StringBuilder query = new StringBuilder("SELECT ")
                .append(dynamicSelect)
                .append(" AS dateTime, ")
                .append("SUM(amount) AS totalAmount ")
                .append("FROM terminal_transaction where 1=1 ");
        var params = new HashMap<String, Object>();
        if (filter.getCompanyId() != null) {
            query.append(" AND company_id = :companyId ");
            params.put("companyId", filter.getCompanyId());
        }
        if (CollectionUtils.isNotEmpty(filter.getBranchIds())) {
            query.append(" AND branch_id IN (:branchIds) ");
            params.put("branchIds", filter.getBranchIds());
        }
        if (filter.getUserId() != null) {
            query.append(" AND user_id = :userId ");
            params.put("userId", filter.getUserId());
        }
        if (filter.getStatus() != null) {
            query.append(" AND status = :status");
            params.put("status", filter.getStatus().name());
        }
        if (filter.getPaymentType() != null) {
            query.append(" AND payment_type = :paymentType ");
            params.put("paymentType", filter.getPaymentType());
        }
        if (filter.getFrom() != null) {
            query.append(" AND date_time >= :from ");
            params.put("from", filter.getFrom());
        }
        if (filter.getTo() != null) {
            query.append(" AND date_time <= :to ");
            params.put("to", filter.getTo());
        }
        query.append(" GROUP BY dateTime ORDER BY dateTime");
        return jdbcClient.sql(query.toString()).params(params).query(TransactionDynamicsDto.class).list();
    }

    @Override
    public List<TransactionAdminStatisticsDto> adminStatistics(TransactionFilter filter) {
        StringBuilder query = new StringBuilder("""
                select payment_type paymentType, operation, sum(amount) totalAmount, count(id) count
                from terminal_transaction
                where 1 = 1""");

        var params = new HashMap<String, Object>();
        if (filter.getCompanyId() != null) {
            query.append(" AND company_id = :companyId ");
            params.put("companyId", filter.getCompanyId());
        }
        if (CollectionUtils.isNotEmpty(filter.getBranchIds())) {
            query.append(" AND branch_id IN (:branchIds) ");
            params.put("branchIds", filter.getBranchIds());
        }
        if (filter.getUserId() != null) {
            query.append(" AND user_id = :userId ");
            params.put("userId", filter.getUserId());
        }
        if (filter.getStatus() != null) {
            query.append(" AND status = :status");
            params.put("status", filter.getStatus().name());
        }
        if (filter.getPaymentType() != null) {
            query.append(" AND payment_type = :paymentType ");
            params.put("paymentType", filter.getPaymentType());
        }
        if (filter.getFrom() != null) {
            query.append(" AND date_time >= :from ");
            params.put("from", filter.getFrom());
        }
        if (filter.getTo() != null) {
            query.append(" AND date_time <= :to ");
            params.put("to", filter.getTo());
        }
        query.append(" GROUP BY paymentType, operation");
        return jdbcClient.sql(query.toString())
                .params(params)
                .query(TransactionAdminStatisticsDto.class)
                .list();
    }
}
