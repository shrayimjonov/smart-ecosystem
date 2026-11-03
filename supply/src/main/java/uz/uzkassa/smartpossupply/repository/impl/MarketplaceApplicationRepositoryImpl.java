package uz.uzkassa.smartpossupply.repository.impl;

import jakarta.persistence.TypedQuery;
import org.springframework.util.CollectionUtils;
import uz.uzkassa.smartpossupply.domain.MarketplaceApplication;
import uz.uzkassa.smartpossupply.dto.base.ResultList;
import uz.uzkassa.smartpossupply.dto.marketplaceapplication.MarketplaceApplicationStatisticDTO;
import uz.uzkassa.smartpossupply.enums.MarketplaceApplicationStatus;
import uz.uzkassa.smartpossupply.filters.BaseFilter;
import uz.uzkassa.smartpossupply.repository.MarketplaceApplicationRepositoryCustom;

@SuppressWarnings("unused")
public class MarketplaceApplicationRepositoryImpl extends BaseRepositoryImpl implements MarketplaceApplicationRepositoryCustom {

    public ResultList<MarketplaceApplication> getResultList(BaseFilter filter) {
        filter.setCompanyId(getCompanyId(filter));
        ResultList<MarketplaceApplication> resultList = new ResultList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ma ");
        sql.append(getBaseSql(filter));
        sql.append(" ORDER BY ma.").append(filter.getOrderBy());
        sql.append(" ").append(filter.getSortOrder().getName());

        StringBuilder countSql = new StringBuilder();
        countSql.append("select COUNT(ma.id) ");
        countSql.append(getBaseSql(filter).toString().replaceAll("FETCH", ""));

        TypedQuery<Long> countQuery = entityManager.createQuery(countSql.toString(), Long.class);

        this.setParameters(countQuery, filter);
        Long count = countQuery.getSingleResult();
        if (count == 0) {
            return resultList;
        }

        TypedQuery<MarketplaceApplication> query = entityManager
            .createQuery(sql.toString(), MarketplaceApplication.class)
            .setFirstResult(filter.getStart())
            .setMaxResults(filter.getSize());
        this.setParameters(query, filter);

        resultList.setCount(count);
        resultList.setList(query.getResultList());

        return resultList;
    }

    @Override
    public MarketplaceApplicationStatisticDTO getStatistics(BaseFilter filter) {
        filter.setCompanyId(getCompanyId(filter));
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ");
        sql.append(" SUM(CASE WHEN ma.status IN ('IN_MARKETPLACE') THEN 1 ELSE 0 END), ");
        sql.append(" SUM(CASE WHEN ma.status = 'IN_MODERATION' THEN 1 ELSE 0 END), ");
        sql.append(" SUM(CASE WHEN ma.status IN ('NOT_FILLED','FILLED','FAILED_VERIFICATION') THEN 1 ELSE 0 END), ");
        sql.append(" SUM(CASE WHEN ma.status = 'REMOVED' THEN 1 ELSE 0 END) ");
        sql.append(getBaseSql(filter).toString().replaceAll("FETCH", ""));

        TypedQuery<Object[]> query = entityManager.createQuery(sql.toString(), Object[].class);
        this.setParameters(query, filter);

        Object[] result = query.getSingleResult();
        Long inMarketplaceTotal = result[0] != null ? ((Number) result[0]).longValue() : 0;
        Long inModerationTotal = result[1] != null ? ((Number) result[1]).longValue() : 0;
        Long draftTotal = result[2] != null ? ((Number) result[2]).longValue() : 0;
        Long removedTotal = result[3] != null ? ((Number) result[3]).longValue() : 0;

        return new MarketplaceApplicationStatisticDTO(inMarketplaceTotal, inModerationTotal, draftTotal, removedTotal);
    }

    private StringBuilder getBaseSql(BaseFilter filter) {
        StringBuilder sql = new StringBuilder();
        sql.append(" FROM MarketplaceApplication ma ");
        sql.append(" JOIN FETCH ma.product p ");
        sql.append(" WHERE ma.deleted = false ");
        sql.append(" AND p.companyId = :companyId");
        if (filter.getStatus() != null) {
            sql.append(" AND ma.status = :status");
        } else if (!CollectionUtils.isEmpty(filter.getStatuses())) {
            sql.append(" AND ma.status in (:statuses) ");
        }
        if (filter.getFrom() != null) {
            sql.append(" AND ma.createdDate >= :fromDate");
        }
        if (filter.getTo() != null) {
            sql.append(" AND ma.createdDate <= :toDate");
        }
        if (filter.isNotEmpty()) {
            sql.append(" AND (");
            sql.append(" LOWER(ma.nameUz)").append(filter.getSearchKey());
            sql.append(" OR LOWER(ma.nameCyrillic)").append(filter.getSearchKey());
            sql.append(" OR LOWER(ma.nameRu)").append(filter.getSearchKey());
            sql.append(" OR LOWER(ma.barcode)").append(filter.getSearchKey());
            sql.append(" OR LOWER(ma.catalogCode)").append(filter.getSearchKey());
            sql.append(")");
        }
        return sql;
    }

    private void setParameters(TypedQuery<?> query, BaseFilter filter) {
        query.setParameter("companyId", filter.getCompanyId());
        if (filter.getStatus() != null) {
            query.setParameter("status", MarketplaceApplicationStatus.get(filter.getStatus()));
        } else if (!CollectionUtils.isEmpty(filter.getStatuses())) {
            query.setParameter("statuses", filter.getStatuses());
        }
        if (filter.getFrom() != null) {
            query.setParameter("fromDate", filter.getFrom());
        }
        if (filter.getTo() != null) {
            query.setParameter("toDate", filter.getTo());
        }
        if (filter.isNotEmpty()) {
            query.setParameter("searchKey", filter.getSearchValue());
        }
    }
}
