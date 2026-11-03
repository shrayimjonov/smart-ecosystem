package uz.uzkassa.horeca.repository.impl;

import jakarta.persistence.TypedQuery;
import org.apache.commons.lang3.StringUtils;
import uz.uzkassa.horeca.domain.company.Partner;
import uz.uzkassa.horeca.dto.base.ResultList;
import uz.uzkassa.horeca.enums.CommonStatus;
import uz.uzkassa.horeca.filters.CompanyFilter;
import uz.uzkassa.horeca.repository.PartnerRepositoryCustom;

@SuppressWarnings("unused")
public class PartnerRepositoryImpl extends BaseRepositoryImpl implements PartnerRepositoryCustom {

    @Override
    public ResultList<Partner> getResultList(CompanyFilter filter, boolean isSupplier) {
        ResultList<Partner> resultList = new ResultList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("select p ");
        sql.append(" from Partner p ");
        sql.append(" join p.customer customer ");
        sql.append(" join p.supplier supplier ");
        sql.append(" where customer.deleted=false and supplier.deleted=false ");
        sql.append(" and p.companyId=:companyId");

        if (isSupplier) {
            sql
                .append(" and (supplier.id !=:companyId")
                .append(" or (supplier.id =:companyId and customer.id =:companyId and p.companyId=:companyId))");
        } else {
            sql
                .append(" and (customer.id !=:companyId")
                .append(" or (supplier.id =:companyId and customer.id =:companyId and p.companyId=:companyId))");
        }
        if (StringUtils.isNotEmpty(filter.getStatus())) {
            if (isSupplier) {
                sql.append(" and p.status=:status");
            } else {
                sql.append(" and p.status=:status");
            }
        }

        if (filter.hasSearch()) {
            if (isSupplier) {
                sql.append(" and (");
                sql.append(" lower(supplier.name)").append(filter.getLikeSearchKey());
                sql.append(" or lower(supplier.tin)").append(filter.getLikeSearchKey());
                sql.append(" or lower(supplier.brand)").append(filter.getLikeSearchKey());
                sql.append(" or lower(supplier.contactPhone)").append(filter.getLikeSearchKey());
                sql.append(" or lower(supplier.contactName)").append(filter.getLikeSearchKey());
                sql.append(")");
            } else {
                sql.append(" and (");
                sql.append(" lower(customer.name)").append(filter.getLikeSearchKey());
                sql.append(" or lower(customer.tin)").append(filter.getLikeSearchKey());
                sql.append(" or lower(customer.brand)").append(filter.getLikeSearchKey());
                sql.append(" or lower(customer.contactPhone)").append(filter.getLikeSearchKey());
                sql.append(" or lower(customer.contactName)").append(filter.getLikeSearchKey());
                sql.append(")");
            }
        }

        String countSql = sql.toString().replaceFirst("select p ", "select count(p.id)");
        sql.append(" order by ");
        sql.append("p.").append(filter.getOrderBy());
        sql.append(" ").append(filter.getSortOrder().getName());

        TypedQuery<Partner> query = entityManager
            .createQuery(sql.toString(), Partner.class)
            .setFirstResult(filter.getStart())
            .setMaxResults(filter.getSize());

        TypedQuery<Long> countQuery = entityManager.createQuery(countSql, Long.class);

        query.setParameter("companyId", filter.getCompanyId());
        countQuery.setParameter("companyId", filter.getCompanyId());

        if (StringUtils.isNotEmpty(filter.getStatus())) {
            query.setParameter("status", CommonStatus.valueOf(filter.getStatus()));
            countQuery.setParameter("status", CommonStatus.valueOf(filter.getStatus()));
        }

        if (filter.hasSearch()) {
            query.setParameter("searchKey", filter.getSearchForQuery());
            countQuery.setParameter("searchKey", filter.getSearchForQuery());
        }

        Long count = countQuery.getSingleResult();
        if (count > 0) {
            resultList.setList(query.getResultList());
            resultList.setCount(count);
        }

        return resultList;
    }
}
