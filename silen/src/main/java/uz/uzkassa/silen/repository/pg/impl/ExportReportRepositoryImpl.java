package uz.uzkassa.silen.repository.pg.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import uz.uzkassa.silen.domain.ExportReport;
import uz.uzkassa.silen.dto.filter.ReportFilter;
import uz.uzkassa.silen.repository.pg.ExportReportRepositoryCustom;

public class ExportReportRepositoryImpl implements ExportReportRepositoryCustom {

    @PersistenceContext(unitName = "slavePU")
    private EntityManager entityManager;

    @Override
    public Page<ExportReport> findAllByFilter(ReportFilter filter) {
        final boolean hasSearch = filter.checkSearchIsEmpty();
        StringBuilder sql = new StringBuilder();
        sql.append(" select e from ExportReport e ");
        sql.append(" where e.deleted is not true ");
        sql.append(" and e.organizationId =:organizationId");

        if (filter.getStatus() != null) {
            sql.append(" and e.status=:status");
        }
        if (filter.getFrom() != null) {
            sql.append(" and e.createdDate>=:from");
        }
        if (filter.getTo() != null) {
            sql.append(" and e.createdDate<=:to");
        }
        if (hasSearch) {
            sql.append(" and (lower(e.name) || lower(e.type)) like :searchKey ");
        }

        String countSql = sql.toString().replaceFirst("select e", "select count(e.id)");

        sql.append(" order by e.createdDate desc ");
        TypedQuery<ExportReport> query = this.entityManager.createQuery(sql.toString(), ExportReport.class)
                .setFirstResult(filter.getStart())
                .setMaxResults(filter.getSize())
                .setParameter("organizationId", filter.getOrganizationId());

        TypedQuery<Long> countQuery = entityManager.createQuery(countSql, Long.class);

        countQuery.setParameter("organizationId", filter.getOrganizationId());
        if (filter.getStatus() != null) {
            query.setParameter("status", filter.getStatus());
            countQuery.setParameter("status", filter.getStatus());
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
        return new PageImpl<>(query.getResultList(), filter.getPageable(), countQuery.getSingleResult());
    }
}
