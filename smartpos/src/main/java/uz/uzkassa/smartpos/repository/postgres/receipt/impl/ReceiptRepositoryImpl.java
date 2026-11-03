package uz.uzkassa.smartpos.repository.postgres.receipt.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import uz.uzkassa.smartpos.domain.postgres.receipt.Receipt;
import uz.uzkassa.smartpos.dto.filter.receipt.ReceiptFilter;
import uz.uzkassa.smartpos.enums.ReceiptStatus;
import uz.uzkassa.smartpos.enums.SortTypeEnum;
import uz.uzkassa.smartpos.repository.postgres.receipt.ReceiptRepositoryCustom;

@SuppressWarnings("unused")
public class ReceiptRepositoryImpl implements ReceiptRepositoryCustom {
    @PersistenceContext
    EntityManager entityManager;

    @Override
    public Page<Receipt> findAllDrafts(ReceiptFilter filter) {
        final boolean hasSearch = StringUtils.isNotEmpty(filter.getSearch());

        StringBuilder sql = new StringBuilder("select r from Receipt r \n");
        sql.append("where r.status=:status \n");
        if (filter.getCompanyId() != null) {
            sql.append(" and r.companyId=:companyId \n");
        }
        if (CollectionUtils.isNotEmpty(filter.getBranchIdsWithBranchId())) {
            sql.append(" and r.branchId in (:branchIds) \n");
        }
        if (filter.getTerminalId() != null) {
            sql.append(" and r.terminalId=:terminalId \n");
        }
        if (filter.getSource() != null) {
            sql.append(" and r.source=:source \n");
        }
        if (filter.getSources() != null) {
            sql.append(" and r.source in (:sources) \n");
        }
        if (filter.getFrom() != null) {
            sql.append(" and r.receiptDateTime>=:from \n");
        }
        if (filter.getTo() != null) {
            sql.append(" and r.receiptDateTime<=:to \n");
        }
        if (filter.getTotalCostFrom() != null) {
            sql.append(" and r.totalCost>=:totalCostFrom \n");
        }
        if (filter.getTotalCostTo() != null) {
            sql.append(" and r.totalCost<=:totalCostTo \n");
        }

        if (hasSearch) {
            sql.append("and ( \n")
                    .append("lower(r.uid) like :searchKey \n")
                    .append(") \n");
        }

        if (filter.getUserId() != null) {
            sql.append(" and r.createdById = :userId \n");
        }

        if (CollectionUtils.isNotEmpty(filter.getPaymentTypes())) {
            filter.getPaymentTypes().forEach(paymentType -> sql.append(" and r.payments['").append(paymentType.name()).append("'] > 0"));
        }

        String countSql = sql.toString().replace("select r", "select count(r)");

        if (StringUtils.isNotBlank(filter.getOrderBy())) {
            sql.append(" order by r.").append(filter.getOrderBy()).append(" ").append(SortTypeEnum.asc.equals(filter.getSortOrder()) ? "asc" : "desc");
        }

        TypedQuery<Receipt> query = entityManager.createQuery(sql.toString(), Receipt.class)
                .setParameter("status", ReceiptStatus.DRAFT)
                .setFirstResult(filter.getStart())
                .setMaxResults(filter.getSize());
        TypedQuery<Long> countQuery = entityManager.createQuery(countSql, Long.class)
                .setParameter("status", ReceiptStatus.DRAFT)
                .setMaxResults(1);

        if (filter.getCompanyId() != null) {
            query.setParameter("companyId", filter.getCompanyId());
            countQuery.setParameter("companyId", filter.getCompanyId());
        }
        if (CollectionUtils.isNotEmpty(filter.getBranchIdsWithBranchId())) {
            query.setParameter("branchIds", filter.getBranchIdsWithBranchId());
            countQuery.setParameter("branchIds", filter.getBranchIdsWithBranchId());
        }
        if (filter.getTerminalId() != null) {
            query.setParameter("terminalId", filter.getTerminalId());
            countQuery.setParameter("terminalId", filter.getTerminalId());
        }
        if (filter.getSource() != null) {
            query.setParameter("source", filter.getSource());
            countQuery.setParameter("source", filter.getSource());
        }
        if (filter.getSources() != null) {
            query.setParameter("sources", filter.getSources());
            countQuery.setParameter("sources", filter.getSources());
        }
        if (filter.getFrom() != null) {
            query.setParameter("from", filter.getFrom());
            countQuery.setParameter("from", filter.getFrom());
        }
        if (filter.getTo() != null) {
            query.setParameter("to", filter.getTo());
            countQuery.setParameter("to", filter.getTo());
        }
        if (hasSearch) {
            query.setParameter("searchKey", filter.getSearchForQuery());
            countQuery.setParameter("searchKey", filter.getSearchForQuery());
        }

        if (filter.getUserId() != null) {
            query.setParameter("userId", filter.getUserId());
            countQuery.setParameter("userId", filter.getUserId());
        }


        if (filter.getTotalCostFrom() != null) {
            query.setParameter("totalCostFrom", filter.getTotalCostFrom());
            countQuery.setParameter("totalCostFrom", filter.getTotalCostFrom());
        }


        if (filter.getTotalCostTo() != null) {
            query.setParameter("totalCostTo", filter.getTotalCostTo());
            countQuery.setParameter("totalCostTo", filter.getTotalCostTo());
        }


        return new PageImpl<>(query.getResultList(), filter.getPageable(), countQuery.getSingleResult());
    }

}
